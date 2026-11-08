package com.icypunishing.mixin;

import com.icypunishing.client.ult.UltPlayerAnimation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 在 setupAnim 末尾接管玩家四肢与头部旋转，播放太刀大招关键帧动作。 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void icy$applyUltPose(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                  float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Player player) {
            UltPlayerAnimation.apply((HumanoidModel<?>) (Object) this, player);
        }
    }
}
