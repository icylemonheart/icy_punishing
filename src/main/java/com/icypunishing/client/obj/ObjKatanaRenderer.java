package com.icypunishing.client.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * 使用 OBJ 网格渲染太刀物品。
 * 所有场景（含 GUI）都只渲染 blade（entityCutout）+ blade_luminous（eyes + 全亮 light=0xF000F0），
 * item_blade 底板不渲染。
 */
public class ObjKatanaRenderer extends BlockEntityWithoutLevelRenderer {

    private final ResourceLocation modelLoc;
    private final ResourceLocation textureLoc;
    private final boolean holdCenter;
    private float guiCx, guiCy, guiCz;
    private boolean guiCenterReady;

    public ObjKatanaRenderer(ResourceLocation modelLoc, ResourceLocation textureLoc) {
        this(modelLoc, textureLoc, false);
    }

    public ObjKatanaRenderer(ResourceLocation modelLoc, ResourceLocation textureLoc, boolean holdCenter) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.modelLoc = modelLoc;
        this.textureLoc = textureLoc;
        this.holdCenter = holdCenter;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose,
                             MultiBufferSource buf, int light, int overlay) {
        ObjModel model = ObjModel.get(modelLoc);

        pose.pushPose();

        if (ctx == ItemDisplayContext.GUI) {
            // 物品栏 3D 斜刀：按模型包围盒中心居中缩放到 slot，再斜 45°
            computeGuiCenter(model);
            pose.translate(0.5F, 0.5F, 0.5F);
            pose.scale(0.55F, 0.55F, 0.55F);
            pose.translate(-guiCx, -guiCy, -guiCz);
            pose.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        } else {
            // 抵消原版管线调用 BEWLR 前的 -0.5 平移（与 GeckoLib 约定一致），让模型原点落在手部锚点
            pose.translate(0.5F, 0.51F, 0.5F);
            if (holdCenter) {
                // 弓类握把在模型长轴中点，将长轴中心平移到手上（X/Z 保持轴向原点，避免附件干扰）
                computeGuiCenter(model);
                pose.translate(0.0F, -guiCy, 0.0F);
            }
        }

        for (Map.Entry<String, List<ObjModel.Face>> entry : model.groups.entrySet()) {
            String group = entry.getKey();

            // 平面图标底板不渲染，所有场景都用真刀身
            if ("item_blade".equalsIgnoreCase(group)) continue;

            boolean luminous = "blade_luminous".equalsIgnoreCase(group);
            RenderType rt = RenderType.entityCutoutNoCull(textureLoc);
            VertexConsumer vc = buf.getBuffer(rt);
            int useLight = luminous ? 0xF000F0 : light;

            for (ObjModel.Face f : entry.getValue()) {
                int n = f.count;
                if (n == 4) {
                    writeVertex(vc, pose, f, 0, useLight, overlay);
                    writeVertex(vc, pose, f, 1, useLight, overlay);
                    writeVertex(vc, pose, f, 2, useLight, overlay);
                    writeVertex(vc, pose, f, 3, useLight, overlay);
                } else {
                    writeVertex(vc, pose, f, 0, useLight, overlay);
                    writeVertex(vc, pose, f, 1, useLight, overlay);
                    writeVertex(vc, pose, f, 2, useLight, overlay);
                    writeVertex(vc, pose, f, 2, useLight, overlay);
                }
            }
        }

        pose.popPose();

        if (buf instanceof MultiBufferSource.BufferSource bs) {
            bs.endBatch();
        }
    }

    private void computeGuiCenter(ObjModel model) {
        if (guiCenterReady) return;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (Map.Entry<String, List<ObjModel.Face>> entry : model.groups.entrySet()) {
            if ("item_blade".equalsIgnoreCase(entry.getKey())) continue;
            for (ObjModel.Face f : entry.getValue()) {
                for (int i = 0; i < f.count; i++) {
                    minX = Math.min(minX, f.pos[i][0]); maxX = Math.max(maxX, f.pos[i][0]);
                    minY = Math.min(minY, f.pos[i][1]); maxY = Math.max(maxY, f.pos[i][1]);
                    minZ = Math.min(minZ, f.pos[i][2]); maxZ = Math.max(maxZ, f.pos[i][2]);
                }
            }
        }
        guiCx = (minX + maxX) * 0.5F;
        guiCy = (minY + maxY) * 0.5F;
        guiCz = (minZ + maxZ) * 0.5F;
        guiCenterReady = true;
    }

    private static void writeVertex(VertexConsumer vc, PoseStack pose, ObjModel.Face f, int i,
                                    int light, int overlay) {
        float x = f.pos[i][0];
        float y = f.pos[i][1];
        float z = f.pos[i][2];
        float u = f.uv[i][0];
        float v = f.uv[i][1];
        float nx = f.normal[i][0];
        float ny = f.normal[i][1];
        float nz = f.normal[i][2];

        vc.addVertex(pose.last(), x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose.last(), nx, ny, nz);
    }
}
