package com.icypunishing.skill.hongying;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/** 红樱·渊裂之术唯一时间轴：客户端动画与服务端判定共用同一份时间与几何。 */
public final class UltTimeline {

    /** 演出总时长（秒），对应 104 tick。 */
    public static final float LENGTH = 5.25f;

    /** 10 次斩击的触发 tick。 */
    public static final int[] SLASH_TICKS = {14, 20, 26, 32, 38, 44, 50, 56, 62, 68};
    /** 每斩走向：0 左下→右上，1 左上→右下，2 左侧上挑。 */
    public static final int[] SLASH_KINDS = {0, 1, 0, 1, 2, 0, 1, 0, 1, 2};

    /** 第二轮收尾后的蓄力停顿开始 tick。 */
    public static final int CHARGE_TICK = 70;
    /** 终结斩 tick。 */
    public static final int FINISH_TICK = 84;
    /** 大招结束（解除锁定）tick。 */
    public static final int END_TICK = 104;

    /** 刀光几何：弧带绕前向轴弯折的角度（弧度），与 SkillVfxRenderer 共用。 */
    public static final double BEND = 0.25;
    /** 弧带半张角（度）。 */
    public static final double HALF_ANGLE = 68.0;
    /** 各走向的挥砍倾角（度）：0 左下→右上，1 左上→右下，2 左侧上挑，3 水平横扫。 */
    public static final double[] SWEEP_ANGLE = {42.0, -42.0, 86.0, 0.0};

    /** 三种斩击姿态：{起手 zRot,xRot}、{中段}、{命中}。 */
    private static final float[][][] POSES = {
            {{52f, 6f}, {-30f, -62f}, {-118f, -28f}},
            {{118f, -28f}, {30f, -62f}, {-52f, 8f}},
            {{62f, 26f}, {84f, -30f}, {104f, -78f}}
    };

    /** 各类型身体转向（度，正值向左拧）：{起手, 中段, 命中}。 */
    private static final float[][] BODY = {
            {20f, 2f, -22f},
            {20f, 2f, -22f},
            {-8f, 8f, 26f}
    };

    /** 单个关键帧：右臂 zRot/xRot、下压量、头部俯仰、身体转向。 */
    public record Key(float t, float rz, float rx, float crouch, float head, float body) {
    }

    private static final List<Key> KEYS = build();

    private UltTimeline() {
    }

    private static List<Key> build() {
        List<Key> keys = new ArrayList<>();
        keys.add(new Key(0.00f, 0f, 0f, 0f, 0f, 0f));
        // 起势：下压屈腿、低头、身体左拧蓄势
        keys.add(new Key(0.30f, 44f, 8f, 2.2f, 12f, 18f));

        // 每斩五点节奏：起手 → 加速 → 临近(65%) → 命中(同 tick 结算) → 惯性过冲，过冲后 0.04 秒硬切下一斩
        for (int i = 0; i < SLASH_TICKS.length; i++) {
            float fire = SLASH_TICKS[i] / 20.0f;
            int kind = SLASH_KINDS[i];
            float[] wind = POSES[kind][0];
            float[] mid = POSES[kind][1];
            float[] hit = POSES[kind][2];
            float[] body = BODY[kind];

            keys.add(new Key(fire - 0.20f, wind[0], wind[1], 2.0f, 10f, body[0]));
            keys.add(new Key(fire - 0.12f, mid[0], mid[1], 2.3f, 6f, body[1]));
            keys.add(new Key(fire - 0.04f,
                    Mth.lerp(0.65f, mid[0], hit[0]), Mth.lerp(0.65f, mid[1], hit[1]),
                    2.6f, 2f, Mth.lerp(0.65f, body[1], body[2])));
            keys.add(new Key(fire, hit[0], hit[1], 2.9f, -4f, body[2]));
            keys.add(new Key(fire + 0.06f,
                    hit[0] + (hit[0] - mid[0]) * 0.12f,
                    hit[1] + (hit[1] - mid[1]) * 0.12f,
                    2.7f, 0f, body[2] + (body[2] - body[1]) * 0.12f));
        }

        // 蓄力停顿：身体右深拧，刀收在身侧
        keys.add(new Key(3.58f, -18f, -92f, 2.8f, -4f, -34f));
        keys.add(new Key(3.95f, -24f, -96f, 2.8f, -6f, -38f));
        // 终结斩：反向硬拧 → 大范围横扫带出整圈转体
        keys.add(new Key(4.02f, -108f, -62f, 2.2f, 2f, -50f));
        keys.add(new Key(4.20f, 82f, -14f, 1.0f, -10f, 45f));
        // 过冲 → 定格：先钉住再缓缓放下
        keys.add(new Key(4.34f, 92f, -8f, 0.8f, -6f, 52f));
        keys.add(new Key(4.66f, 88f, -6f, 0.8f, -4f, 50f));
        // 收刀
        keys.add(new Key(4.92f, 12f, 2f, 0.6f, 6f, 12f));
        keys.add(new Key(LENGTH, 0f, 0f, 0f, 0f, 0f));
        return keys;
    }

    /** 第 index 斩的触发时刻（秒）。 */
    public static float slashTime(int index) {
        return SLASH_TICKS[index % SLASH_TICKS.length] / 20.0f;
    }

    /** 第 index 斩的走向。 */
    public static int slashKind(int index) {
        return SLASH_KINDS[index % SLASH_KINDS.length];
    }

    /** 采样：四肢线性插值；身体转向用五次方缓动，在每段末尾形成"硬转"顿挫。 */
    public static Key sample(float t) {
        Key first = KEYS.get(0);
        if (t <= first.t()) {
            return first;
        }
        Key last = KEYS.get(KEYS.size() - 1);
        if (t >= last.t()) {
            return last;
        }
        int lo = 0;
        int hi = KEYS.size() - 1;
        while (lo + 1 < hi) {
            int mid = (lo + hi) >>> 1;
            if (KEYS.get(mid).t() <= t) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        Key a = KEYS.get(lo);
        Key b = KEYS.get(hi);
        float span = b.t() - a.t();
        float k = span <= 1.0E-6f ? 1.0f : (t - a.t()) / span;
        float hard = k * k * k * k * k;
        return new Key(t,
                Mth.lerp(k, a.rz(), b.rz()),
                Mth.lerp(k, a.rx(), b.rx()),
                Mth.lerp(k, a.crouch(), b.crouch()),
                Mth.lerp(k, a.head(), b.head()),
                Mth.lerp(hard, a.body(), b.body()));
    }
}
