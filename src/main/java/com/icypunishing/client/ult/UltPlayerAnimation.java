package com.icypunishing.client.ult;

import com.icypunishing.skill.hongying.UltTimeline;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 太刀大招：客户端关键帧驱动原版玩家模型（Mixin 注入 setupAnim），身体硬转走 RenderPlayerEvent。
 * 时间轴与几何来自 {@link UltTimeline}，与服务端判定共用。
 * 姿态约定（正面朝 +Z）：右臂 zRot &gt; 0 向身体左侧横摆，xRot &lt; 0 向前上方抬起。
 */
public final class UltPlayerAnimation {

    /** 视角俯仰补偿系数：抬头/低头时躯干与头部一起跟随，避免刀光与身体错位。 */
    private static final float LOOK_COMPENSATION = 0.45f;
    private static final float LOOK_LIMIT = 35.0f;

    private static final Map<UUID, Long> START_NANOS = new HashMap<>();

    private UltPlayerAnimation() {
    }

    /** 大招开始：记录起始时间。 */
    public static void start(UUID playerId, int durationTicks) {
        START_NANOS.put(playerId, System.nanoTime());
    }

    public static void stop(UUID playerId) {
        START_NANOS.remove(playerId);
    }

    /** 身体硬转：在玩家渲染矩阵上绕竖直轴追加偏转。 */
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        UltTimeline.Key key = currentKey(player);
        if (key == null || Math.abs(key.body()) < 0.05f) {
            return;
        }
        event.getPoseStack().mulPose(Axis.YP.rotationDegrees(key.body()));
    }

    /** 由 Mixin 每帧调用：覆盖玩家模型的骨骼变换（护甲层模型同样生效）。 */
    public static void apply(HumanoidModel<?> model, Player player) {
        UltTimeline.Key key = currentKey(player);
        if (key == null) {
            return;
        }
        float crouch = key.crouch();

        model.rightArm.xRot = Mth.DEG_TO_RAD * key.rx();
        model.rightArm.yRot = 0.0f;
        model.rightArm.zRot = Mth.DEG_TO_RAD * key.rz();
        model.rightArm.y = 2.0f + crouch;

        // 副手保持原版姿态，不参与动画；只跟随躯干一起下沉，避免肩部脱节
        model.leftArm.y = 2.0f + crouch;

        // 屈腿：整条腿绕髋关节抬起，配合下压形成重心下沉
        float legX = crouch * 8.0f;
        model.rightLeg.xRot = Mth.DEG_TO_RAD * legX;
        model.leftLeg.xRot = Mth.DEG_TO_RAD * legX;

        // 视角俯仰补偿：正 pitch = 低头。躯干与头部同量追加，脖子处不会错位
        float look = Mth.clamp(player.getXRot(), -LOOK_LIMIT, LOOK_LIMIT) * LOOK_COMPENSATION;
        model.body.xRot = Mth.DEG_TO_RAD * look;
        model.head.xRot = Mth.DEG_TO_RAD * (key.head() + look);
        model.head.yRot = 0.0f;
        model.head.y = crouch;
        // 原版模型 y 正方向向下：正位移才是「压下去」，与弯腿配合才不会在腰腹处裂开
        model.body.y = crouch;

        model.hat.copyFrom(model.head);
        if (model instanceof PlayerModel<?> playerModel) {
            playerModel.jacket.copyFrom(playerModel.body);
            playerModel.leftSleeve.copyFrom(playerModel.leftArm);
            playerModel.rightSleeve.copyFrom(playerModel.rightArm);
            playerModel.leftPants.copyFrom(playerModel.leftLeg);
            playerModel.rightPants.copyFrom(playerModel.rightLeg);
        }
    }

    /** 取当前时刻的关键帧；不在演出中返回 null。 */
    private static UltTimeline.Key currentKey(Player player) {
        Long start = START_NANOS.get(player.getUUID());
        if (start == null) {
            return null;
        }
        float t = (System.nanoTime() - start) / 1_000_000_000.0f;
        if (t > UltTimeline.LENGTH) {
            START_NANOS.remove(player.getUUID());
            return null;
        }
        return UltTimeline.sample(t);
    }
}
