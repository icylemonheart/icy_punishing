package com.icypunishing.client;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.slf4j.Logger;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.loading.json.raw.Cube;
import software.bernie.geckolib.loading.json.raw.ModelProperties;
import software.bernie.geckolib.loading.json.raw.PolyMesh;
import software.bernie.geckolib.loading.json.raw.PolysUnion;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.BoneStructure;
import software.bernie.geckolib.loading.object.GeometryTree;

import java.util.ArrayList;
import java.util.List;

public class IcyPunishingModelFactory implements BakedModelFactory {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BakedModelFactory DEFAULT = BakedModelFactory.DEFAULT_FACTORY;

    @Override
    public BakedGeoModel constructGeoModel(GeometryTree geometryTree) {
        LOGGER.info("[ICY] constructGeoModel called with {} top-level bones", geometryTree.topLevelBones().size());
        List<GeoBone> topBones = new ArrayList<>();
        for (BoneStructure bs : geometryTree.topLevelBones().values()) {
            GeoBone gb = constructBone(bs, geometryTree.properties(), null);
            topBones.add(gb);
        }
        return new BakedGeoModel(topBones, geometryTree.properties());
    }

    @Override
    public GeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, GeoBone parent) {
        GeoBone bone = DEFAULT.constructBone(boneStructure, properties, parent);
        processPolyMeshRecursive(boneStructure, bone);
        return bone;
    }

    private void processPolyMeshRecursive(BoneStructure boneStructure, GeoBone bone) {
        PolyMesh polyMesh = boneStructure.self().polyMesh();
        String boneName = boneStructure.self().name();
        if (polyMesh != null) {
            int posCount = polyMesh.positions() != null ? polyMesh.positions().length : 0;
            int polyCount = (polyMesh.polysUnion() != null && polyMesh.polysUnion().union() != null) ? polyMesh.polysUnion().union().length : 0;
            LOGGER.info("[ICY] polyMesh on bone: {} positions={} polys={}", boneName, posCount, polyCount);
            try {
                List<GeoCube> polyCubes = buildPolyMeshCubes(polyMesh);
                if (!polyCubes.isEmpty()) {
                    bone.getCubes().addAll(polyCubes);
                    LOGGER.info("[ICY] bone {} now has {} cubes", boneName, bone.getCubes().size());
                }
            } catch (Exception e) {
                LOGGER.error("[ICY] ERROR building poly mesh for " + boneName, e);
            }
        }
        // recurse into children
        for (BoneStructure child : boneStructure.children().values()) {
            GeoBone childBone = null;
            for (GeoBone cb : bone.getChildBones()) {
                if (cb.getName().equals(child.self().name())) {
                    childBone = cb;
                    break;
                }
            }
            if (childBone != null) {
                processPolyMeshRecursive(child, childBone);
            }
        }
    }

    @Override
    public GeoCube constructCube(Cube cube, ModelProperties properties, GeoBone parent) {
        return DEFAULT.constructCube(cube, properties, parent);
    }

    private List<GeoCube> buildPolyMeshCubes(PolyMesh polyMesh) {
        List<GeoCube> cubes = new ArrayList<>();
        double[] positions = polyMesh.positions();
        double[] normals = polyMesh.normals();
        double[] uvs = polyMesh.uvs();
        PolysUnion polysUnion = polyMesh.polysUnion();
        if (polysUnion == null) return cubes;
        double[][][] polys = polysUnion.union();
        if (polys == null || polys.length == 0) return cubes;

        boolean normalized = polyMesh.normalizedUVs() != null && polyMesh.normalizedUVs();

        List<GeoQuad> quads = new ArrayList<>();
        for (double[][] poly : polys) {
            if (poly == null || poly.length < 3) continue;
            // poly[v] = [posIdx, uvIdx] or [posIdx, normalIdx, uvIdx]
            int vCount = poly.length;
            GeoVertex[] vertices = new GeoVertex[vCount];
            Vector3f[] worldVerts = new Vector3f[vCount];
            for (int i = 0; i < vCount; i++) {
                double[] vi = poly[i];
                int posIdx = (int) vi[0];
                int uvIdx;
                if (vi.length >= 3) {
                    uvIdx = (int) vi[2];
                } else if (vi.length == 2) {
                    uvIdx = (int) vi[1];
                } else {
                    uvIdx = posIdx;
                }
                float x = (float) positions[posIdx * 3];
                float y = (float) positions[posIdx * 3 + 1];
                float z = (float) positions[posIdx * 3 + 2];
                float u = (float) uvs[uvIdx * 2];
                float v = (float) uvs[uvIdx * 2 + 1];
                worldVerts[i] = new Vector3f(x, y, z);
                vertices[i] = new GeoVertex(new Vector3f(x, y, z), u, v);
            }

            // Compute face normal from first 3 vertices
            Vector3f e1 = new Vector3f(worldVerts[1]).sub(worldVerts[0]);
            Vector3f e2 = new Vector3f(worldVerts[2]).sub(worldVerts[0]);
            Vector3f normal = new Vector3f();
            e1.cross(e2, normal);
            if (normal.lengthSquared() > 1e-12f) {
                normal.normalize();
            } else {
                // fallback to vertex normal
                int posIdx = (int) poly[0][0];
                normal.set((float) normals[posIdx * 3], (float) normals[posIdx * 3 + 1], (float) normals[posIdx * 3 + 2]);
            }

            // Convert triangle to quad (duplicate last vertex)
            GeoVertex[] quadVerts;
            if (vCount == 3) {
                quadVerts = new GeoVertex[]{vertices[0], vertices[1], vertices[2], vertices[2]};
            } else if (vCount == 4) {
                quadVerts = vertices;
            } else {
                // fan triangulation for n-gons: create multiple quads
                for (int i = 1; i < vCount - 1; i++) {
                    GeoVertex[] q = new GeoVertex[]{vertices[0], vertices[i], vertices[i + 1], vertices[i + 1]};
                    quads.add(new GeoQuad(q, new Vector3f(normal), Direction.UP));
                }
                continue;
            }
            quads.add(new GeoQuad(quadVerts, normal, Direction.UP));
        }

        if (!quads.isEmpty()) {
            GeoQuad[] quadArray = quads.toArray(new GeoQuad[0]);
            cubes.add(new GeoCube(quadArray, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 0.0, false));
        }
        return cubes;
    }
}
