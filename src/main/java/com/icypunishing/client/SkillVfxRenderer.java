package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

@EventBusSubscriber(modid = IcyPunishing.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SkillVfxRenderer {

    public static final byte SHAPE_THRUST = 0;
    public static final byte SHAPE_SLASH = 1;
    public static final byte SHAPE_RING = 2;
    public static final byte SHAPE_BURST = 3;
    public static final byte SHAPE_CROSS = 4;
    public static final byte SHAPE_SPHERE = 5;
    public static final byte SHAPE_SPIRAL = 6;
    public static final byte SHAPE_PILLAR = 7;
    public static final byte SHAPE_SHOCKWAVE = 8;

    private static final Vec3 WORLD_UP = new Vec3(0, 1, 0);
    private static final int ARC_SEGMENTS = 40;
    private static final int RING_SEGMENTS = 56;

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        long now = System.nanoTime();
        SkillVfxClient.prune(now);
        List<SkillVfxClient.Fx> list = SkillVfxClient.snapshot();
        if (list.isEmpty()) {
            return;
        }

        Vec3 cam = event.getCamera().getPosition();
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        Matrix4f mat = pose.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (SkillVfxClient.Fx fx : list) {
            drawFx(buffer, mat, fx, fx.pos().subtract(cam), now);
        }
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        pose.popPose();
    }

    private static void drawFx(BufferBuilder buf, Matrix4f mat, SkillVfxClient.Fx fx, Vec3 origin, long now) {
        float p = fx.progress(now);
        if (p >= 1.0f) {
            return;
        }
        Vec3 fwd = lookVector(fx.yaw(), fx.pitch());
        Vec3 right = fwd.cross(WORLD_UP);
        if (right.lengthSqr() < 1.0E-6) {
            right = new Vec3(1, 0, 0);
        }
        right = right.normalize();
        Vec3 up = right.cross(fwd).normalize();

        float radius = fx.radius();
        float thick = fx.thickness();
        int color = fx.color();

        switch (fx.shape()) {
            case SHAPE_THRUST -> thrust(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_SLASH -> slash(buf, mat, origin, fwd, right, up, radius, thick, color, p, fx.style() & 0xFF);
            case SHAPE_RING -> ring(buf, mat, origin, radius, thick, color, p);
            case SHAPE_BURST -> burst(buf, mat, origin, right, up, radius, thick, color, p);
            case SHAPE_CROSS -> {
                slash(buf, mat, origin, fwd, right, up, radius, thick, color, p, 0);
                slash(buf, mat, origin, fwd, right, up, radius, thick, color, p, 2);
            }
            case SHAPE_SPHERE -> sphere(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_SPIRAL -> spiral(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_PILLAR -> pillar(buf, mat, origin, radius, thick, color, p);
            case SHAPE_SHOCKWAVE -> shockwave(buf, mat, origin, radius, thick, color, p);
            default -> {
            }
        }
    }

    private static final double[] SLASH_TILTS = {34.0, -34.0, 56.0, -56.0};

    private static void slash(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                              float radius, float thick, int color, float p, int style) {
        boolean mirrored = (style & 1) != 0;
        double tilt = Math.toRadians(SLASH_TILTS[(style >> 1) & 3]);
        Vec3 a = rotateAround(right, fwd, tilt);
        Vec3 b = rotateAround(up, fwd, tilt);

        double dir = mirrored ? -1.0 : 1.0;
        double half = Math.toRadians(96.0);
        double visible = Math.min(1.0, p * 2.4);
        double start = -half * dir;
        double end = (-half + half * 2.0 * visible) * dir;
        float fade = 1.0f - p * 0.55f;

        arcBand(buf, mat, origin, a, b, start, end, radius - thick * 1.05, radius + thick * 1.05,
                dim(color, 0.7f), 0.30f * fade, 0.6f);
        arcBand(buf, mat, origin, a, b, start, end, radius - thick * 0.55, radius + thick * 0.55,
                color, 0.62f * fade, 0.6f);
        arcBand(buf, mat, origin, a, b, start, end, radius - thick * 0.18, radius + thick * 0.18,
                towardsWhite(color, 0.55f), 0.98f * fade, 0.5f);

        double head = end;
        arcBand(buf, mat, origin, a, b, head - Math.toRadians(9.0), head + Math.toRadians(9.0),
                radius - thick * 1.5, radius + thick * 1.5, 0xFFFFFF, 0.85f * fade, 0.0f);
    }

    private static void thrust(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                               float radius, float thick, int color, float p) {
        float len = radius * (0.5f + 0.8f * p);
        float fade = (float) Math.pow(1.0f - p, 0.7);
        int core = towardsWhite(color, 0.7f);
        beam(buf, mat, origin, fwd, up, len, thick * 2.2f, dim(color, 0.65f), 0.24f * fade);
        beam(buf, mat, origin, fwd, up, len, thick, color, 0.55f * fade);
        beam(buf, mat, origin, fwd, up, len, thick * 0.34f, core, 0.92f * fade);
        beam(buf, mat, origin, fwd, right, len, thick * 1.6f, dim(color, 0.7f), 0.20f * fade);
        beam(buf, mat, origin, fwd, right, len, thick * 0.55f, color, 0.42f * fade);
        beam(buf, mat, origin, fwd, right, len, thick * 0.2f, core, 0.85f * fade);
    }

    private static void ring(BufferBuilder buf, Matrix4f mat, Vec3 origin, float radius, float thick,
                             int color, float p) {
        float r = radius * (0.3f + 1.05f * p);
        float th = thick * (1.0f - 0.45f * p);
        float fade = (float) Math.pow(1.0f - p, 1.3);
        ringBand(buf, mat, origin, new Vec3(1, 0, 0), new Vec3(0, 0, 1), r - th * 1.3, r + th * 1.3,
                dim(color, 0.7f), 0.26f * fade);
        ringBand(buf, mat, origin, new Vec3(1, 0, 0), new Vec3(0, 0, 1), r - th * 0.55, r + th * 0.55,
                color, 0.6f * fade);
        ringBand(buf, mat, origin, new Vec3(1, 0, 0), new Vec3(0, 0, 1), r - th * 0.16, r + th * 0.16,
                towardsWhite(color, 0.6f), 0.95f * fade);
        float inner = r * 0.55f;
        ringBand(buf, mat, origin, new Vec3(1, 0, 0), new Vec3(0, 0, 1), 0.0, inner,
                dim(color, 0.8f), 0.10f * fade);
    }

    private static void burst(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 right, Vec3 up,
                              float radius, float thick, int color, float p) {
        float r = radius * (0.2f + 1.15f * p);
        float th = thick * (1.0f - 0.3f * p);
        float fade = (float) Math.pow(1.0f - p, 1.1);
        Vec3 forward = right.cross(up).normalize();
        Vec3[] u = {right, right, forward};
        Vec3[] v = {up, forward, up};
        for (int i = 0; i < 3; i++) {
            ringBand(buf, mat, origin, u[i], v[i], r - th * 1.4, r + th * 1.4,
                    dim(color, 0.7f), 0.22f * fade);
            ringBand(buf, mat, origin, u[i], v[i], r - th * 0.5, r + th * 0.5, color, 0.55f * fade);
            ringBand(buf, mat, origin, u[i], v[i], r - th * 0.15, r + th * 0.15,
                    towardsWhite(color, 0.65f), 0.9f * fade);
        }
        float flash = radius * 0.9f * (1.0f - p * 0.6f);
        float flashAlpha = (float) Math.pow(1.0f - p, 2.4) * 0.95f;
        quad(buf, mat, origin.add(up.scale(-flash)), origin.add(right.scale(flash)),
                origin.add(up.scale(flash)), origin.add(right.scale(-flash)), 0xFFFFFF, flashAlpha, flashAlpha);
        quad(buf, mat, origin.add(forward.scale(-flash)), origin.add(right.scale(flash)),
                origin.add(forward.scale(flash)), origin.add(right.scale(-flash)), 0xFFFFFF,
                flashAlpha * 0.7f, flashAlpha * 0.7f);
        quad(buf, mat, origin.add(forward.scale(-flash)), origin.add(up.scale(flash)),
                origin.add(forward.scale(flash)), origin.add(up.scale(-flash)), 0xFFFFFF,
                flashAlpha * 0.7f, flashAlpha * 0.7f);
    }

    private static void sphere(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                               float radius, float thick, int color, float p) {
        float r = radius * (0.25f + 0.95f * p);
        float th = thick * (1.0f - 0.4f * p);
        float fade = (float) Math.pow(1.0f - p, 1.2);
        int lat = 5;
        for (int i = 1; i <= lat; i++) {
            double phi = Math.PI * i / (lat + 1);
            double ringR = r * Math.sin(phi);
            Vec3 center = origin.add(up.scale(r * Math.cos(phi)));
            ringBand(buf, mat, center, right, fwd, ringR - th, ringR + th, color, 0.45f * fade);
        }
        ringBand(buf, mat, origin, right, fwd, r - th * 1.4, r + th * 1.4, dim(color, 0.7f), 0.22f * fade);
        ringBand(buf, mat, origin, right, fwd, r - th * 0.4, r + th * 0.4, towardsWhite(color, 0.5f), 0.8f * fade);
        ringBand(buf, mat, origin, right, up, r - th, r + th, color, 0.4f * fade);
        ringBand(buf, mat, origin, fwd, up, r - th, r + th, color, 0.4f * fade);
    }

    private static void spiral(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                               float radius, float thick, int color, float p) {
        float fade = (float) Math.pow(1.0f - p, 0.9);
        int segments = 64;
        double turns = 2.2;
        for (int i = 0; i < segments; i++) {
            double t0 = i / (double) segments;
            if (t0 > p) {
                break;
            }
            double t1 = Math.min(p, (i + 1) / (double) segments);
            double ang0 = turns * Math.PI * 2.0 * t0;
            double ang1 = turns * Math.PI * 2.0 * t1;
            double rad0 = radius * (0.30 + 0.75 * t0);
            double rad1 = radius * (0.30 + 0.75 * t1);
            double len0 = radius * (1.7 * t0 - 0.85);
            double len1 = radius * (1.7 * t1 - 0.85);
            Vec3 d0 = right.scale(Math.cos(ang0)).add(up.scale(Math.sin(ang0)));
            Vec3 d1 = right.scale(Math.cos(ang1)).add(up.scale(Math.sin(ang1)));
            Vec3 c0 = origin.add(fwd.scale(len0)).add(d0.scale(rad0));
            Vec3 c1 = origin.add(fwd.scale(len1)).add(d1.scale(rad1));
            float alpha = 0.65f * fade * (float) (0.35 + 0.65 * t0);
            quad(buf, mat, c0.subtract(d0.scale(thick)), c0.add(d0.scale(thick)),
                    c1.add(d1.scale(thick)), c1.subtract(d1.scale(thick)),
                    dim(color, 0.85f), alpha * 0.6f, alpha * 0.6f);
            quad(buf, mat, c0.subtract(d0.scale(thick * 0.3f)), c0.add(d0.scale(thick * 0.3f)),
                    c1.add(d1.scale(thick * 0.3f)), c1.subtract(d1.scale(thick * 0.3f)),
                    towardsWhite(color, 0.6f), alpha, alpha);
        }
    }

    private static void pillar(BufferBuilder buf, Matrix4f mat, Vec3 origin,
                               float radius, float thick, int color, float p) {
        float len = radius * (1.6f + 0.8f * p);
        float fade = (float) Math.pow(1.0f - p, 0.9);
        Vec3 up = new Vec3(0.0, 1.0, 0.0);
        Vec3 x = new Vec3(1.0, 0.0, 0.0);
        Vec3 z = new Vec3(0.0, 0.0, 1.0);
        Vec3 start = origin.add(0.0, len, 0.0);
        for (Vec3 side : new Vec3[]{x, z}) {
            beam(buf, mat, start, up.scale(-1.0), side, len * 2.0f, thick * 2.4f,
                    dim(color, 0.6f), 0.26f * fade);
            beam(buf, mat, start, up.scale(-1.0), side, len * 2.0f, thick * 0.9f, color, 0.62f * fade);
            beam(buf, mat, start, up.scale(-1.0), side, len * 2.0f, thick * 0.3f, 0xFFFFFF, 0.92f * fade);
        }
        float ringR = radius * (0.3f + 1.0f * p);
        ringBand(buf, mat, origin, x, z, ringR - thick * 1.4, ringR + thick * 1.4,
                dim(color, 0.75f), 0.24f * fade);
        ringBand(buf, mat, origin, x, z, ringR - thick * 0.5, ringR + thick * 0.5, color, 0.6f * fade);
        ringBand(buf, mat, origin, x, z, ringR - thick * 0.15, ringR + thick * 0.15,
                towardsWhite(color, 0.6f), 0.9f * fade);
    }

    private static void shockwave(BufferBuilder buf, Matrix4f mat, Vec3 origin,
                                  float radius, float thick, int color, float p) {
        float r = radius * (0.2f + 1.5f * p);
        float fade = (float) Math.pow(1.0f - p, 1.4);
        Vec3 u = new Vec3(1.0, 0.0, 0.0);
        Vec3 v = new Vec3(0.0, 0.0, 1.0);
        ringBand(buf, mat, origin, u, v, r - thick * 2.0, r + thick * 2.0, dim(color, 0.7f), 0.26f * fade);
        ringBand(buf, mat, origin, u, v, r - thick, r + thick, color, 0.55f * fade);
        ringBand(buf, mat, origin, u, v, r - thick * 0.22, r + thick * 0.22, 0xFFFFFF, 0.9f * fade);
        float trail = r * 0.62f;
        ringBand(buf, mat, origin, u, v, trail - thick * 0.6, trail + thick * 0.6, color, 0.3f * fade);
    }

    private static void arcBand(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 a, Vec3 b,
                                double startRad, double endRad, double rIn, double rOut,
                                int color, float alpha, float tailPow) {
        if (Math.abs(endRad - startRad) < 1.0E-6) {
            return;
        }
        for (int i = 0; i < ARC_SEGMENTS; i++) {
            double t0 = i / (double) ARC_SEGMENTS;
            double t1 = (i + 1) / (double) ARC_SEGMENTS;
            double a0 = startRad + (endRad - startRad) * t0;
            double a1 = startRad + (endRad - startRad) * t1;
            float f0 = tailPow > 0 ? (float) Math.pow(t0, tailPow) : 1.0f;
            float f1 = tailPow > 0 ? (float) Math.pow(t1, tailPow) : 1.0f;
            Vec3 d0 = a.scale(Math.cos(a0)).add(b.scale(Math.sin(a0))).normalize();
            Vec3 d1 = a.scale(Math.cos(a1)).add(b.scale(Math.sin(a1))).normalize();
            quad(buf, mat,
                    origin.add(d0.scale(rIn)), origin.add(d0.scale(rOut)),
                    origin.add(d1.scale(rOut)), origin.add(d1.scale(rIn)),
                    color, alpha * f0, alpha * f1);
        }
    }

    private static void ringBand(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 u, Vec3 v,
                                 double rIn, double rOut, int color, float alpha) {
        for (int i = 0; i < RING_SEGMENTS; i++) {
            double t0 = i / (double) RING_SEGMENTS * Math.PI * 2.0;
            double t1 = (i + 1) / (double) RING_SEGMENTS * Math.PI * 2.0;
            Vec3 d0 = u.scale(Math.cos(t0)).add(v.scale(Math.sin(t0)));
            Vec3 d1 = u.scale(Math.cos(t1)).add(v.scale(Math.sin(t1)));
            quad(buf, mat,
                    origin.add(d0.scale(rIn)), origin.add(d0.scale(rOut)),
                    origin.add(d1.scale(rOut)), origin.add(d1.scale(rIn)),
                    color, alpha, alpha);
        }
    }

    private static void beam(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 dir, Vec3 side,
                             float length, float halfWidth, int color, float alpha) {
        int n = 7;
        for (int i = 0; i < n; i++) {
            float t0 = i / (float) n;
            float t1 = (i + 1) / (float) n;
            float w0 = halfWidth * (1.0f - t0 * 0.92f);
            float w1 = halfWidth * (1.0f - t1 * 0.92f);
            float a0 = alpha * (1.0f - t0 * 0.85f);
            float a1 = alpha * (1.0f - t1 * 0.85f);
            Vec3 c0 = origin.add(dir.scale(length * t0));
            Vec3 c1 = origin.add(dir.scale(length * t1));
            quad(buf, mat, c0.subtract(side.scale(w0)), c0.add(side.scale(w0)),
                    c1.add(side.scale(w1)), c1.subtract(side.scale(w1)), color, a0, a1);
        }
    }

    private static void quad(BufferBuilder buf, Matrix4f mat, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                             int color, float alphaA, float alphaB) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int bl = color & 0xFF;
        int ai = alphaByte(alphaA);
        int bi = alphaByte(alphaB);
        if (ai == 0 && bi == 0) {
            return;
        }
        buf.addVertex(mat, (float) a.x, (float) a.y, (float) a.z).setColor(r, g, bl, ai);
        buf.addVertex(mat, (float) b.x, (float) b.y, (float) b.z).setColor(r, g, bl, ai);
        buf.addVertex(mat, (float) c.x, (float) c.y, (float) c.z).setColor(r, g, bl, bi);
        buf.addVertex(mat, (float) d.x, (float) d.y, (float) d.z).setColor(r, g, bl, bi);
    }

    private static int alphaByte(float a) {
        return (int) (Math.max(0.0f, Math.min(1.0f, a)) * 255.0f);
    }

    private static int dim(int color, float factor) {
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }

    private static int towardsWhite(int color, float t) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        r = (int) (r + (255 - r) * t);
        g = (int) (g + (255 - g) * t);
        b = (int) (b + (255 - b) * t);
        return (r << 16) | (g << 8) | b;
    }

    private static Vec3 rotateAround(Vec3 v, Vec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return v.scale(cos).add(axis.cross(v).scale(sin)).add(axis.scale(axis.dot(v) * (1.0 - cos)));
    }

    private static Vec3 lookVector(float yaw, float pitch) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);
        return new Vec3(x, y, z).normalize();
    }
}
