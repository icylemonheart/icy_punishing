package com.icypunishing.client.obj;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 极简 OBJ 加载器：支持 v / vt / vn / f / g，按 g 分组存面。
 * 顶点存原始数据，建 Face 时按分组归一化：
 * - item_blade：物品栏平面图标，缩到 0.9 格
 * - 其他（blade / blade_luminous）：绕 Z -90° 立起，总长 3 格
 */
public class ObjModel {

    public static class Face {
        // 3 或 4 个顶点
        public final float[][] pos;    // [n][3]
        public final float[][] uv;     // [n][2]
        public final float[][] normal; // [n][3]
        public final int count;

        public Face(float[][] pos, float[][] uv, float[][] normal, int count) {
            this.pos = pos;
            this.uv = uv;
            this.normal = normal;
            this.count = count;
        }
    }

    public final Map<String, List<Face>> groups = new HashMap<>();

    // builtin/entity 空间 1 单位 = 1 格。目标：刀总长 4 格，原始 x=0（护手处）对齐手部锚点（拔刀剑约定）
    // blade/blade_luminous：原始 X[-350.5..45] 绕 Z -90° -> Y
    private static final float WORLD_SCALE = 4.0F / 395.5F;
    private static final float WORLD_Y_OFFSET = 0.0F;
    // item_blade：141.42x141.42 平面图标，缩到 0.9 格居中
    private static final float ICON_SCALE = 0.9F / 141.42F;

    private static final Map<ResourceLocation, ObjModel> CACHE = new HashMap<>();

    public static ObjModel get(ResourceLocation loc) {
        return CACHE.computeIfAbsent(loc, l -> {
            try {
                return load(Minecraft.getInstance().getResourceManager(), l);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load OBJ " + l, e);
            }
        });
    }

    public static ObjModel load(ResourceManager rm, ResourceLocation loc) throws Exception {
        ObjModel model = new ObjModel();

        List<float[]> positions = new ArrayList<>();
        List<float[]> uvs = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();

        Resource res = rm.getResource(loc).orElseThrow(() -> new IllegalStateException("Missing OBJ " + loc));
        try (BufferedReader br = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
            String currentGroup = "default";
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("v ")) {
                    String[] p = line.substring(2).trim().split("\\s+");
                    positions.add(new float[]{
                            Float.parseFloat(p[0]),
                            Float.parseFloat(p[1]),
                            Float.parseFloat(p[2])});
                } else if (line.startsWith("vt ")) {
                    String[] p = line.substring(3).trim().split("\\s+");
                    float u = Float.parseFloat(p[0]);
                    float v = p.length > 1 ? Float.parseFloat(p[1]) : 0.0F;
                    // OBJ 的 v 原点在左下，MC 在左上，翻转
                    uvs.add(new float[]{u, 1.0F - v});
                } else if (line.startsWith("vn ")) {
                    String[] p = line.substring(3).trim().split("\\s+");
                    float x = Float.parseFloat(p[0]);
                    float y = Float.parseFloat(p[1]);
                    float z = Float.parseFloat(p[2]);
                    float len = (float) Math.sqrt(x * x + y * y + z * z);
                    if (len > 1e-6f) { x /= len; y /= len; z /= len; }
                    normals.add(new float[]{x, y, z});
                } else if (line.startsWith("g ") || line.startsWith("o ")) {
                    currentGroup = line.substring(2).trim();
                } else if (line.startsWith("f ")) {
                    String[] p = line.substring(2).trim().split("\\s+");
                    int n = p.length;
                    if (n < 3) continue;

                    int[] vIdx = new int[n];
                    int[] vtIdx = new int[n];
                    int[] vnIdx = new int[n];
                    for (int i = 0; i < n; i++) {
                        String[] tok = p[i].split("/", -1);
                        vIdx[i] = resolveIndex(parseIntSafe(tok, 0), positions.size());
                        vtIdx[i] = resolveIndex(parseIntSafe(tok, 1), uvs.size());
                        vnIdx[i] = resolveIndex(parseIntSafe(tok, 2), normals.size());
                    }

                    // 三角化：扇形拆分；3 直接存，4 直接存（QUAD），>4 拆成多个
                    List<Face> faces = model.groups.computeIfAbsent(currentGroup, k -> new ArrayList<>());
                    if (n <= 4) {
                        faces.add(buildFace(currentGroup, positions, uvs, normals, vIdx, vtIdx, vnIdx, n));
                    } else {
                        // 扇形：(0,i,i+1)
                        for (int i = 1; i + 1 < n; i++) {
                            int[] fv = {vIdx[0], vIdx[i], vIdx[i + 1]};
                            int[] fvt = {vtIdx[0], vtIdx[i], vtIdx[i + 1]};
                            int[] fvn = {vnIdx[0], vnIdx[i], vnIdx[i + 1]};
                            faces.add(buildFace(currentGroup, positions, uvs, normals, fv, fvt, fvn, 3));
                        }
                    }
                }
            }
        }
        return model;
    }

    private static int parseIntSafe(String[] tok, int i) {
        if (i >= tok.length) return 0;
        String s = tok[i];
        if (s == null || s.isEmpty()) return 0;
        return Integer.parseInt(s);
    }

    /** OBJ 索引 1-based；负值为相对索引（-1 = 最后一个）。返回 0-based；无效返回 -1 */
    private static int resolveIndex(int raw, int size) {
        if (raw == 0) return -1;
        if (raw > 0) return raw - 1;
        return size + raw;
    }

    private static Face buildFace(String group, List<float[]> positions, List<float[]> uvs, List<float[]> normals,
                                  int[] vIdx, int[] vtIdx, int[] vnIdx, int n) {
        boolean icon = "item_blade".equalsIgnoreCase(group);
        float[][] pos = new float[n][3];
        float[][] uv = new float[n][2];
        float[][] nor = new float[n][3];
        for (int i = 0; i < n; i++) {
            float[] p = vIdx[i] >= 0 && vIdx[i] < positions.size() ? positions.get(vIdx[i]) : new float[]{0, 0, 0};
            if (icon) {
                // 平面图标：缩到 0.9 格，中心对齐
                pos[i][0] = p[0] * ICON_SCALE;
                pos[i][1] = p[1] * ICON_SCALE;
                pos[i][2] = p[2] * ICON_SCALE;
            } else {
                // 世界刀身：绕 Z -90° (x,y,z)->(y,-x,z)，再缩放平移
                pos[i][0] = p[1] * WORLD_SCALE;
                pos[i][1] = -p[0] * WORLD_SCALE + WORLD_Y_OFFSET;
                pos[i][2] = p[2] * WORLD_SCALE;
            }
            if (vtIdx[i] >= 0 && vtIdx[i] < uvs.size()) {
                float[] t = uvs.get(vtIdx[i]);
                uv[i][0] = t[0]; uv[i][1] = t[1];
            } else {
                uv[i][0] = 0; uv[i][1] = 0;
            }
            if (vnIdx[i] >= 0 && vnIdx[i] < normals.size()) {
                float[] nn = normals.get(vnIdx[i]);
                if (icon) {
                    nor[i][0] = nn[0]; nor[i][1] = nn[1]; nor[i][2] = nn[2];
                } else {
                    // 法线同步绕 Z -90°
                    float nx = nn[1], ny = -nn[0], nz = nn[2];
                    float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
                    if (len > 1e-6f) { nx /= len; ny /= len; nz /= len; }
                    nor[i][0] = nx; nor[i][1] = ny; nor[i][2] = nz;
                }
            } else {
                nor[i][0] = 0; nor[i][1] = 1; nor[i][2] = 0;
            }
        }
        return new Face(pos, uv, nor, n);
    }
}
