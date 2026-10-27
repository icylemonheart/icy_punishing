package com.icypunishing.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.loading.json.raw.Cube;
import software.bernie.geckolib.loading.json.raw.ModelProperties;
import software.bernie.geckolib.loading.json.raw.PolyMesh;
import software.bernie.geckolib.loading.json.raw.PolysUnion;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.BoneStructure;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * 支持 poly_mesh 自由网格的模型工厂。
 * <p>
 * GeckoLib 内置工厂只读取 {@code cubes}，遇到只有 {@code poly_mesh} 的骨骼不会生成任何几何体，
 * 模型会完全不可见。这里把 poly_mesh 的三角面转成 GeckoLib 的 {@link GeoQuad}。
 * <p>
 * 坐标约定（GeckoLib 渲染管线）：骨骼 pivot 与立方体顶点都是“模型绝对坐标”，
 * 且 x 轴取反、整体除以 16 换算成方块单位。geo.json 中的 poly_mesh 顶点是相对骨骼 pivot 的局部坐标，
 * 因此需要先加回骨骼 pivot 再做变换。
 */
public class IcyPunishingModelFactory implements BakedModelFactory {
    private static final double PX = 16.0D;

    @Override
    public BakedGeoModel constructGeoModel(GeometryTree geometryTree) {
        List<GeoBone> bones = new ObjectArrayList<>();

        for (BoneStructure boneStructure : geometryTree.topLevelBones().values()) {
            bones.add(constructBone(boneStructure, geometryTree.properties(), null));
        }

        return new BakedGeoModel(bones, geometryTree.properties());
    }

    @Override
    public GeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, @Nullable GeoBone parent) {
        Bone bone = boneStructure.self();
        GeoBone geoBone = new GeoBone(parent, bone.name(), bone.mirror(), bone.inflate(), bone.neverRender(), bone.reset());
        Vec3 rotation = RenderUtil.arrayToVec(bone.rotation());
        Vec3 pivot = RenderUtil.arrayToVec(bone.pivot());

        geoBone.updateRotation((float) Math.toRadians(-rotation.x), (float) Math.toRadians(-rotation.y), (float) Math.toRadians(rotation.z));
        geoBone.updatePivot((float) -pivot.x, (float) pivot.y, (float) pivot.z);

        for (Cube cube : bone.cubes()) {
            geoBone.getCubes().add(BakedModelFactory.DEFAULT_FACTORY.constructCube(cube, properties, geoBone));
        }

        if (bone.polyMesh() != null) {
            buildPolyMesh(bone, geoBone);
        }

        for (BoneStructure child : boneStructure.children().values()) {
            geoBone.getChildBones().add(constructBone(child, properties, geoBone));
        }

        return geoBone;
    }

    @Override
    public GeoCube constructCube(Cube cube, ModelProperties properties, GeoBone bone) {
        return BakedModelFactory.DEFAULT_FACTORY.constructCube(cube, properties, bone);
    }

    /** 把骨骼的 poly_mesh 三角面转成一个 GeoCube（内含多个四边形），挂到该骨骼上 */
    private void buildPolyMesh(Bone bone, GeoBone geoBone) {
        PolyMesh mesh = bone.polyMesh();
        if (mesh == null) {
            return;
        }
        double[] positions = mesh.positions();
        PolysUnion polys = mesh.polysUnion();
        if (positions == null || positions.length < 9 || polys == null || polys.union() == null) {
            return;
        }
        double[] normals = mesh.normals() == null ? new double[0] : mesh.normals();
        double[] uvs = mesh.uvs() == null ? new double[0] : mesh.uvs();

        Vec3 pivot = RenderUtil.arrayToVec(bone.pivot());
        int vertexCount = positions.length / 3;

        double[][][] raw = polys.union();
        List<GeoQuad> quads = new ObjectArrayList<>(raw.length);
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        for (double[][] poly : raw) {
            if (poly == null || poly.length < 3) {
                continue;
            }
            GeoVertex[] vertices = new GeoVertex[4];
            float nx = 0.0F, ny = 0.0F, nz = 0.0F;
            boolean valid = true;

            for (int i = 0; i < 3; i++) {
                int idx = (int) poly[i][0];
                if (idx < 0 || idx >= vertexCount) {
                    valid = false;
                    break;
                }
                double lx = positions[idx * 3];
                double ly = positions[idx * 3 + 1];
                double lz = positions[idx * 3 + 2];

                // 骨骼局部坐标 + 骨骼绝对 pivot → 模型绝对坐标；再按 GeckoLib 约定 x 取反、除以 16
                float vx = (float) (-(pivot.x + lx) / PX);
                float vy = (float) ((pivot.y + ly) / PX);
                float vz = (float) ((pivot.z + lz) / PX);

                float u = 0.0F;
                float v = 0.0F;
                // 角点格式为 [顶点索引, UV索引]，UV 必须用 UV 索引而非顶点索引，否则会采到贴图错误区域
                int uvIdx = poly[i].length > 1 ? (int) poly[i][1] : idx;
                if (uvs.length >= uvIdx * 2 + 1) {
                    u = (float) uvs[uvIdx * 2];
                    v = (float) uvs[uvIdx * 2 + 1];
                }

                vertices[i] = new GeoVertex(new Vector3f(vx, vy, vz), u, v);
                nx += (float) -get(normals, idx * 3);
                ny += (float) get(normals, idx * 3 + 1);
                nz += (float) get(normals, idx * 3 + 2);

                minX = Math.min(minX, vx); maxX = Math.max(maxX, vx);
                minY = Math.min(minY, vy); maxY = Math.max(maxY, vy);
                minZ = Math.min(minZ, vz); maxZ = Math.max(maxZ, vz);
            }

            if (!valid) {
                continue;
            }

            // 三角形以退化四边形表示：第四个顶点与第三个重合
            vertices[3] = vertices[2];

            Vector3f normal = new Vector3f(nx, ny, nz);
            if (normal.lengthSquared() < 1.0E-8F) {
                normal.set(0.0F, 1.0F, 0.0F);
            } else {
                normal.normalize();
            }

            quads.add(new GeoQuad(vertices, normal, directionOf(normal)));
        }

        if (quads.isEmpty()) {
            return;
        }

        Vec3 size = new Vec3(Math.max(1.0E-4D, maxX - minX), Math.max(1.0E-4D, maxY - minY), Math.max(1.0E-4D, maxZ - minZ));
        // pivot 与旋转留零：顶点本身已是模型绝对坐标，交给骨骼矩阵统一变换
        geoBone.getCubes().add(new GeoCube(quads.toArray(new GeoQuad[0]), Vec3.ZERO, Vec3.ZERO, size, 0.0D, false));
    }

    private static double get(double[] array, int index) {
        return index >= 0 && index < array.length ? array[index] : 0.0D;
    }

    private static Direction directionOf(Vector3f normal) {
        float ax = Math.abs(normal.x());
        float ay = Math.abs(normal.y());
        float az = Math.abs(normal.z());

        if (ay >= ax && ay >= az) {
            return normal.y() >= 0.0F ? Direction.UP : Direction.DOWN;
        }
        if (ax >= az) {
            return normal.x() >= 0.0F ? Direction.EAST : Direction.WEST;
        }
        return normal.z() >= 0.0F ? Direction.SOUTH : Direction.NORTH;
    }
}
