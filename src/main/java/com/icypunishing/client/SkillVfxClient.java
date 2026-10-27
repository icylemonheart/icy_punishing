package com.icypunishing.client;

import com.icypunishing.network.ClientboundSkillVfxPayload;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SkillVfxClient {

    public record Fx(byte shape, byte style, Vec3 pos, float yaw, float pitch,
                     float radius, float thickness, int color,
                     long birthNanos, long durationNanos) {
        public float progress(long now) {
            return Math.min(1.0f, (now - birthNanos) / (float) durationNanos);
        }
    }

    private static final List<Fx> ACTIVE = new CopyOnWriteArrayList<>();

    private SkillVfxClient() {
    }

    public static void add(ClientboundSkillVfxPayload payload) {
        ACTIVE.add(new Fx(payload.shape(), payload.style(),
                new Vec3(payload.x(), payload.y(), payload.z()),
                payload.yaw(), payload.pitch(), payload.radius(), payload.thickness(),
                payload.color(), System.nanoTime(), payload.lifeMillis() * 1_000_000L));
    }

    public static void prune(long now) {
        ACTIVE.removeIf(fx -> now - fx.birthNanos() > fx.durationNanos());
    }

    public static List<Fx> snapshot() {
        return List.copyOf(ACTIVE);
    }
}
