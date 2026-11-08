package com.icypunishing.client.entity;

import com.icypunishing.client.obj.ObjKatanaRenderer;
import com.icypunishing.client.obj.ObjModel;
import com.icypunishing.entity.TianlangBladeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

/** 天狼星悬浮体的渲染：直接用太刀 OBJ 绘制在实体位置。 */
public class TianlangBladeRenderer extends EntityRenderer<TianlangBladeEntity> {

    private static final float SCALE = 0.45f;
    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/tianlang_blade.obj");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("icy_punishing", "model/katana/tianlang_blade.png");

    private static ObjKatanaRenderer renderer;
    private static int longAxis = 1;

    public TianlangBladeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
    }

    @Override
    public void render(TianlangBladeEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        ItemStack stack = entity.getDisplayStack();
        if (stack.isEmpty()) {
            return;
        }
        ensureRenderer();
        if (renderer == null) {
            return;
        }
        pose.pushPose();
        // 客户端逐帧用主人的插值位置/朝向重新计算环绕位置，避免网络 3 tick 同步造成的抽动
        Entity owner = entity.level().isClientSide()
                ? entity.level().getEntity(entity.getOwnerEntityId()) : null;
        if (owner != null) {
            float ownerYaw = Mth.rotLerp(partialTick, owner.yRotO, owner.getYRot());
            Vec3 ownerPos = owner.getPosition(partialTick);
            double yawRad = Math.toRadians(ownerYaw);
            Vec3 back = new Vec3(Math.sin(yawRad), 0.0, -Math.cos(yawRad));
            Vec3 left = new Vec3(Math.cos(yawRad), 0.0, Math.sin(yawRad));
            double bob = Math.sin((owner.tickCount + partialTick) * 0.08) * 0.08;
            Vec3 target = ownerPos
                    .add(back.scale(TianlangBladeEntity.BACK_OFFSET))
                    .add(left.scale(TianlangBladeEntity.LEFT_OFFSET))
                    .add(0.0, TianlangBladeEntity.HEIGHT + bob, 0.0);
            Vec3 current = entity.getPosition(partialTick);
            // 抵消实体自身朝向 → 平移到主人身上的目标点 → 套用主人朝向
            pose.mulPose(Axis.YP.rotationDegrees(entityYaw));
            pose.translate(target.x - current.x, target.y - current.y, target.z - current.z);
            pose.mulPose(Axis.YP.rotationDegrees(ownerYaw));
        }
        // 实体渲染时 pose 已按实体朝向旋转，局部 +Z 即前方；下一层把刀身长轴摆平
        switch (longAxis) {
            case 0 -> pose.mulPose(Axis.YP.rotationDegrees(-90.0f));
            case 1 -> pose.mulPose(Axis.XP.rotationDegrees(90.0f));
            default -> {
            }
        }
        pose.mulPose(Axis.ZP.rotationDegrees((float) Math.sin((entity.tickCount + partialTick) * 0.08) * 5.0f));
        pose.scale(SCALE, SCALE, SCALE);
        // 抵消 renderByItem 内部为非 GUI 场景添加的 +0.5 平移
        pose.translate(-0.5f, -0.51f, -0.5f);
        renderer.renderByItem(stack, ItemDisplayContext.FIXED, pose, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY);
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    private static void ensureRenderer() {
        if (renderer != null) {
            return;
        }
        renderer = new ObjKatanaRenderer(MODEL, TEXTURE);
        ObjModel model = ObjModel.get(MODEL);
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (Map.Entry<String, List<ObjModel.Face>> entry : model.groups.entrySet()) {
            if ("item_blade".equalsIgnoreCase(entry.getKey())) {
                continue;
            }
            for (ObjModel.Face face : entry.getValue()) {
                for (int i = 0; i < face.count; i++) {
                    minX = Math.min(minX, face.pos[i][0]);
                    maxX = Math.max(maxX, face.pos[i][0]);
                    minY = Math.min(minY, face.pos[i][1]);
                    maxY = Math.max(maxY, face.pos[i][1]);
                    minZ = Math.min(minZ, face.pos[i][2]);
                    maxZ = Math.max(maxZ, face.pos[i][2]);
                }
            }
        }
        float dx = maxX - minX;
        float dy = maxY - minY;
        float dz = maxZ - minZ;
        longAxis = dy >= dx && dy >= dz ? 1 : (dx >= dz ? 0 : 2);
    }

    @Override
    public ResourceLocation getTextureLocation(TianlangBladeEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("icy_punishing", "textures/item/tianlang.png");
    }
}
