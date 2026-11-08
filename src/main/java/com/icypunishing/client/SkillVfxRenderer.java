package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.icypunishing.client.obj.ObjKatanaRenderer;
import com.icypunishing.registry.ModItems;
import com.icypunishing.skill.hongying.UltTimeline;
import com.mojang.math.Axis;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
    /** 极细直线贯穿；thickness 半宽，radius 长度 */
    public static final byte SHAPE_LINE = 9;
    /** 放射细线扇；style 控数量 */
    public static final byte SHAPE_RAY = 10;
    /** 面向视角的细椭圆环 */
    public static final byte SHAPE_LOOP = 11;
    /** 地面圆 + 径向辐条 */
    public static final byte SHAPE_GRID = 12;
    /** 空间撕裂：多层锯齿环 + 能量喷流 */
    public static final byte SHAPE_WARP = 13;
    /** 红球终结：冲击波 + 金色光带 */
    public static final byte SHAPE_RED_FINISH = 14;
    /** 黄球终结：虫洞 + 垂直吸 beam */
    public static final byte SHAPE_YELLOW_FINISH = 15;
    /** 蓝球终结：吸聚漩涡 + 闪电 */
    public static final byte SHAPE_BLUE_FINISH = 16;
    /** 水平环形斩 */
    public static final byte SHAPE_H_RING = 17;
    /** 水平 Z 字折线斩 */
    public static final byte SHAPE_H_ZIGZAG = 18;
    /** 竖直月牙剑气 */
    public static final byte SHAPE_QI_ARC = 19;
    /** 大招残留刀光 */
    public static final byte SHAPE_ULT_TRAIL = 20;
    /** 竖直悬浮刀影环 */
    public static final byte SHAPE_BLADE_RING = 21;
    /** 地面挑起的月牙斩 */
    public static final byte SHAPE_GROUND_ARC = 22;
    /** 剑身螺旋能量带 + 火星 */
    public static final byte SHAPE_BLADE_AURA = 23;
    /** 突刺贯穿：极细红线 + 碎刃 */
    public static final byte SHAPE_PIERCE = 24;
    /** 地面分段弧旋转轮盘 */
    public static final byte SHAPE_SPIN_WHEEL = 25;
    /** 范围领域：多重环 + 光墙 */
    public static final byte SHAPE_DOMAIN = 26;
    /** 背后四把悬浮太刀 */
    public static final byte SHAPE_ULT_BLADES = 27;
    /** 投掷矛：枪杆 + 菱形矛头 */
    public static final byte SHAPE_SPEAR = 28;

    private static final Vec3 WORLD_UP = new Vec3(0, 1, 0);
    private static final int ARC_SEGMENTS = 40;
    private static final int RING_SEGMENTS = 56;
    /** 剑气月牙贴图 */
    private static final ResourceLocation QI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "textures/particle/sword_qi.png");
    /** 残光刀身模型/贴图（必杀背后四把刀） */
    private static final ResourceLocation CANGUANG_MODEL =
            ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "model/katana/canguang_blade.obj");
    private static final ResourceLocation CANGUANG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(IcyPunishing.MODID, "model/katana/canguang_blade.png");
    private static ObjKatanaRenderer canguangRenderer;

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
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
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
            Anchor anchor = anchor(fx, partialTick);
            drawFx(buffer, mat, fx, anchor.pos().subtract(cam), anchor.yaw(), anchor.pitch(), now);
        }
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }

        // 剑气贴图单独一遍：加法混合
        renderQiPass(list, mat, cam, now);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        // 太刀模型通道（必杀背后四把残光）
        renderBladePass(list, pose, cam, now, partialTick);

        pose.popPose();
    }

    /** 背后太刀通道：直接绘制残光 OBJ，四把刀扇形展开，前段弹出、尾段收起。 */
    private static void renderBladePass(List<SkillVfxClient.Fx> list, PoseStack pose, Vec3 cam, long now,
                                        float partialTick) {
        boolean needed = false;
        for (SkillVfxClient.Fx fx : list) {
            if (fx.shape() == SHAPE_ULT_BLADES) {
                needed = true;
                break;
            }
        }
        if (!needed) {
            return;
        }
        if (canguangRenderer == null) {
            canguangRenderer = new ObjKatanaRenderer(CANGUANG_MODEL, CANGUANG_TEXTURE);
        }
        ItemStack stack = new ItemStack(ModItems.CANGUANG.get());
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        double[] spread = {-0.85, -0.3, 0.3, 0.85};
        for (SkillVfxClient.Fx fx : list) {
            if (fx.shape() != SHAPE_ULT_BLADES) {
                continue;
            }
            float p = fx.progress(now);
            float appear = Math.min(1.0f, p * 8.0f);
            float retreat = p < 0.72f ? 1.0f : 1.0f - (p - 0.72f) / 0.28f;
            float scale = 0.42f * appear * Math.max(0.0f, retreat);
            if (scale <= 0.001f) {
                continue;
            }
            Anchor anchor = anchor(fx, partialTick);
            Vec3 origin = anchor.pos().subtract(cam);
            Vec3 fwd = lookVector(anchor.yaw(), anchor.pitch());
            Vec3 back = new Vec3(-fwd.x, 0.0, -fwd.z);
            if (back.lengthSqr() < 1.0E-6) {
                back = new Vec3(0.0, 0.0, 1.0);
            }
            back = back.normalize();
            Vec3 side = new Vec3(-back.z, 0.0, back.x);
            for (int i = 0; i < spread.length; i++) {
                double a = spread[i];
                Vec3 d = back.scale(Math.cos(a)).add(side.scale(Math.sin(a))).normalize();
                pose.pushPose();
                pose.translate(origin.x + d.x * 0.55, origin.y + 0.15 + p * 0.18, origin.z + d.z * 0.55);
                pose.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(d.x, d.z))));
                pose.mulPose(Axis.ZP.rotationDegrees((float) Math.toDegrees(a) * 0.55f));
                pose.scale(scale, scale, scale);
                pose.translate(-0.5f, -0.51f, -0.5f);
                canguangRenderer.renderByItem(stack, ItemDisplayContext.FIXED, pose, buffer, 0xF000F0,
                        OverlayTexture.NO_OVERLAY);
                pose.popPose();
            }
        }
    }

    /** 剑气贴图通道：月牙贴图面向玩家，顶点色只负责染色与淡出。 */
    private static void renderQiPass(List<SkillVfxClient.Fx> list, Matrix4f mat, Vec3 cam, long now) {
        boolean any = false;
        for (SkillVfxClient.Fx fx : list) {
            if (fx.shape() == SHAPE_QI_ARC && fx.progress(now) < 1.0f) {
                any = true;
                break;
            }
        }
        if (!any) {
            return;
        }

        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (SkillVfxClient.Fx fx : list) {
            if (fx.shape() != SHAPE_QI_ARC) {
                continue;
            }
            float p = fx.progress(now);
            if (p >= 1.0f) {
                continue;
            }
            Vec3 fwd = lookVector(fx.yaw(), fx.pitch());
            Vec3 right = fwd.cross(WORLD_UP);
            if (right.lengthSqr() < 1.0E-6) {
                right = new Vec3(1, 0, 0);
            }
            right = right.normalize();
            Vec3 up = right.cross(fwd).normalize();

            // 0.1 秒淡入后整体淡出并轻微放大；两道剑气反向倾斜交叉成 X
            float alpha = Math.min(1.0f, p * 10.0f) * (float) Math.pow(1.0 - p, 0.8);
            float size = fx.radius() * 1.55f * (1.0f + p * 0.18f);
            double tilt = Math.toRadians((fx.style() < 10 ? -1.0 : 1.0) * 28.0);
            Vec3 u = right.scale(Math.cos(tilt)).add(up.scale(Math.sin(tilt))).normalize();
            Vec3 v = right.scale(-Math.sin(tilt)).add(up.scale(Math.cos(tilt))).normalize();

            Vec3 center = fx.pos().subtract(cam).add(fwd.scale(fx.radius() * 0.35));
            float h = size * 0.5f;
            int c = fx.color();
            int r = (c >> 16) & 0xFF;
            int g = (c >> 8) & 0xFF;
            int b = c & 0xFF;
            texQuad(buf, mat,
                    center.subtract(u.scale(h)).subtract(v.scale(h)),
                    center.add(u.scale(h)).subtract(v.scale(h)),
                    center.add(u.scale(h)).add(v.scale(h)),
                    center.subtract(u.scale(h)).add(v.scale(h)),
                    r, g, b, alpha);
        }
        MeshData mesh = buf.build();
        if (mesh == null) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, QI_TEXTURE);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        BufferUploader.drawWithShader(mesh);
        RenderSystem.defaultBlendFunc();
    }

    private static void texQuad(BufferBuilder buf, Matrix4f mat, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                                int r, int g, int bl, float alpha) {
        float ar = alpha * (r / 255.0f);
        float ag = alpha * (g / 255.0f);
        float ab = alpha * (bl / 255.0f);
        buf.addVertex(mat, (float) a.x, (float) a.y, (float) a.z).setColor(ar, ag, ab, alpha).setUv(0.0f, 1.0f);
        buf.addVertex(mat, (float) b.x, (float) b.y, (float) b.z).setColor(ar, ag, ab, alpha).setUv(1.0f, 1.0f);
        buf.addVertex(mat, (float) c.x, (float) c.y, (float) c.z).setColor(ar, ag, ab, alpha).setUv(1.0f, 0.0f);
        buf.addVertex(mat, (float) d.x, (float) d.y, (float) d.z).setColor(ar, ag, ab, alpha).setUv(0.0f, 0.0f);
    }

    /** 跟随实体的特效在世界中的实时锚点；followId 为 0 时退回发送时记录的坐标。 */
    private record Anchor(Vec3 pos, float yaw, float pitch) {
    }

    private static Anchor anchor(SkillVfxClient.Fx fx, float partialTick) {
        if (fx.followId() != 0 && Minecraft.getInstance().level != null) {
            var entity = Minecraft.getInstance().level.getEntity(fx.followId());
            if (entity != null) {
                // 用插值坐标与朝向，避免 20 tick/秒的阶梯抖动
                return new Anchor(entity.getPosition(partialTick),
                        Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot()),
                        Mth.lerp(partialTick, entity.xRotO, entity.getXRot()));
            }
        }
        return new Anchor(fx.pos(), fx.yaw(), fx.pitch());
    }

    private static void drawFx(BufferBuilder buf, Matrix4f mat, SkillVfxClient.Fx fx, Vec3 origin,
                               float yaw, float pitch, long now) {
        float p = fx.progress(now);
        if (p >= 1.0f) {
            return;
        }
        Vec3 fwd = lookVector(yaw, pitch);
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
            case SHAPE_LINE -> line(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_RAY -> ray(buf, mat, origin, fwd, right, up, radius, thick, color, p, fx.style() & 0xFF);
            case SHAPE_LOOP -> loop(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_GRID -> grid(buf, mat, origin, radius, thick, color, p);
            case SHAPE_WARP -> warp(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_RED_FINISH -> redFinish(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_YELLOW_FINISH -> yellowFinish(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_BLUE_FINISH -> blueFinish(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            case SHAPE_H_RING -> hRing(buf, mat, origin, radius, thick, color, p);
            case SHAPE_H_ZIGZAG -> hZigzag(buf, mat, origin, fwd, right, radius, thick, color, p);
            case SHAPE_ULT_TRAIL -> ultTrail(buf, mat, origin, fwd, right, up, radius, thick, color, p,
                    (now - fx.birthNanos()) / 1_000_000.0f, fx.style() & 0xFF);
            case SHAPE_BLADE_RING -> bladeRing(buf, mat, origin, radius, thick, color, p, fx.style() & 0xFF);
            case SHAPE_GROUND_ARC -> groundArc(buf, mat, origin, fwd, up, radius, thick, color, p);
            case SHAPE_BLADE_AURA -> bladeAura(buf, mat, origin, right, up, radius, thick, color, p);
            case SHAPE_PIERCE -> pierce(buf, mat, origin, fwd, right, up, radius, thick, color, p,
                    fx.style() & 0xFF);
            case SHAPE_SPIN_WHEEL -> spinWheel(buf, mat, origin, radius, thick, color, p, fx.style() & 0xFF);
            case SHAPE_DOMAIN -> domain(buf, mat, origin, radius, thick, color, p);
            case SHAPE_SPEAR -> spear(buf, mat, origin, fwd, right, up, radius, thick, color, p);
            // ULT_BLADES 走模型通道，不画顶点色几何
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

    /** 大招刀光：穿过视线的月牙弧带；style &amp; 3 决定挥砍走向。 */
    private static void ultTrail(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                                 float radius, float thick, int color, float p, float elapsedMs, int style) {
        // 几何与服务端刀身判定共用 UltTimeline，改一处两边同步
        double angle = Math.toRadians(UltTimeline.SWEEP_ANGLE[style & 3]);
        Vec3 u = right.scale(Math.cos(angle)).add(up.scale(Math.sin(angle))).normalize();
        Vec3 w = right.scale(-Math.sin(angle)).add(up.scale(Math.cos(angle))).normalize();
        final double bend = UltTimeline.BEND;
        Vec3 v = fwd.scale(Math.cos(bend)).add(w.scale(Math.sin(bend))).normalize();

        double half = Math.toRadians(UltTimeline.HALF_ANGLE);
        double end = -half + half * 2.0 * Math.min(1.0f, elapsedMs / 140.0f);
        // 斩出瞬间爆亮，随后回落到残留亮度
        float flash = 0.5f + 0.5f * (float) Math.pow(1.0 - Math.min(1.0f, elapsedMs / 320.0f), 4.0);
        float bright = flash * (1.0f - (float) Math.pow(p, 6.0));

        arcBand(buf, mat, origin, v, u, -half, end,
                radius - thick * 1.35, radius + thick * 1.35, dim(color, 0.72f),
                0.02f * bright, 0.20f * bright, 0.6f);
        arcBand(buf, mat, origin, v, u, -half, end,
                radius - thick * 0.85, radius + thick * 0.85, color,
                0.05f * bright, 0.50f * bright, 0.55f);
        arcBand(buf, mat, origin, v, u, -half, end,
                radius - thick * 0.32, radius + thick * 0.32, towardsWhite(color, 0.6f),
                0.18f * bright, 0.88f * bright, 0.45f);
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

    private static void line(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                             float length, float thick, int color, float p) {
        // 前期快速爬升（斩击出现），后期 (1-p)^0.6 缓慢淡出，整体更亮
        float onset = Math.min(1.0f, p * 8.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.6);
        int coreColor = towardsWhite(color, 0.75f);
        int dimColor = dim(color, 0.5f);
        // 尾部保持 45% 宽度
        float tailFalloff = 0.55f;

        // 三层叠加：外光晕 / 固有色 / 白芯
        beamTail(buf, mat, origin, fwd, right, length, thick * 5.0f, tailFalloff, dimColor, 0.28f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 5.0f, tailFalloff, dimColor, 0.28f * fade);
        beamTail(buf, mat, origin, fwd, right, length, thick * 1.6f, tailFalloff, color, 0.72f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 1.6f, tailFalloff, color, 0.72f * fade);
        beamTail(buf, mat, origin, fwd, right, length, thick * 0.55f, tailFalloff, coreColor, 0.98f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 0.55f, tailFalloff, coreColor, 0.98f * fade);
    }

    private static void ray(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                            float radius, float thick, int color, float p, int style) {
        int count = 6 + (style & 0x0F) * 2; // 6 ~ 36
        float fade = (float) Math.pow(1.0 - p, 1.2);
        int coreColor = towardsWhite(color, 0.7f);
        int dimColor = dim(color, 0.5f);
        for (int i = 0; i < count; i++) {
            double a = i * Math.PI * 2.0 / count;
            Vec3 dir = right.scale(Math.cos(a)).add(up.scale(Math.sin(a))).normalize();
            Vec3 side = right.cross(dir).normalize();
            beam(buf, mat, origin, dir, side, radius, thick * 3.0f, dimColor, 0.16f * fade);
            beam(buf, mat, origin, dir, side, radius, thick, color, 0.55f * fade);
            beam(buf, mat, origin, dir, side, radius, thick * 0.3f, coreColor, 0.9f * fade);
        }
    }

    private static void loop(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                             float radius, float thick, int color, float p) {
        float fade = (float) Math.pow(1.0 - p, 1.3);
        ringBand(buf, mat, origin, right, up, Math.max(0, radius - thick * 2.0), radius + thick * 2.0,
                dim(color, 0.5f), 0.14f * fade);
        ringBand(buf, mat, origin, right, up, Math.max(0, radius - thick), radius + thick, color, 0.5f * fade);
        ringBand(buf, mat, origin, right, up, Math.max(0, radius - thick * 0.3), radius + thick * 0.3,
                towardsWhite(color, 0.6f), 0.9f * fade);
    }

    private static void grid(BufferBuilder buf, Matrix4f mat, Vec3 origin, float radius, float thick, int color, float p) {
        float fade = (float) Math.pow(1.0 - p, 1.3);
        Vec3 u = new Vec3(1, 0, 0);
        Vec3 v = new Vec3(0, 0, 1);
        ringBand(buf, mat, origin, u, v, Math.max(0, radius - thick), radius + thick, color, 0.5f * fade);
        ringBand(buf, mat, origin, u, v, Math.max(0, radius - thick * 0.3), radius + thick * 0.3,
                towardsWhite(color, 0.5f), 0.85f * fade);
        int spokes = 16;
        for (int i = 0; i < spokes; i++) {
            double a = i * Math.PI * 2.0 / spokes;
            Vec3 d = u.scale(Math.cos(a)).add(v.scale(Math.sin(a)));
            beam(buf, mat, origin, d, WORLD_UP, radius, thick * 0.35f, dim(color, 0.7f), 0.3f * fade);
        }
    }

    /** 空间撕裂：多层不规则裂缝环 + 内部能量喷流。 */
    private static void warp(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                             float radius, float thick, int color, float p) {
        float onset = Math.min(1.0f, p * 6.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.7);
        int core = towardsWhite(color, 0.65f);
        int dim = dim(color, 0.5f);

        int tearLayers = 5;
        for (int layer = 0; layer < tearLayers; layer++) {
            double layerOffset = layer / (double) tearLayers;
            double rProgress = (layerOffset + p * 1.2) % 1.0;
            double ringR = radius * (0.15 + 0.85 * rProgress);
            // 每层沿 fwd 错开纵深，形成 3D 扭曲
            float depth = thick * 2.2f * (1.0f - (float) layer * 0.15f);
            float fwdOffset = (float) Math.sin(layer * 1.7 + p * 3.14) * thick * 0.6f;

            Vec3 layerCenter = origin.add(fwd.scale(fwdOffset));
            jaggedRing(buf, mat, layerCenter, right, up, ringR, depth * 1.2f, depth * 0.45f,
                    dim, color, fade * (1.0f - (float) rProgress) * 0.5f, 12 + layer * 2, layer * 3 + 7);
        }

        int burstCount = 8;
        for (int i = 0; i < burstCount; i++) {
            double a = i * Math.PI * 2.0 / burstCount + p * 1.8;
            double sinA = Math.sin(a);
            Vec3 burstDir = right.scale(Math.cos(a)).add(up.scale(sinA)).add(fwd.scale(sinA * 0.35)).normalize();
            Vec3 side = burstDir.cross(right).lengthSqr() > 1.0E-6 ? burstDir.cross(right).normalize()
                    : burstDir.cross(up).normalize();
            float len = radius * (0.55f + 0.35f * (1.0f - p));
            float beamFade = fade * (1.0f - (float) p * 0.6f);
            beamTail(buf, mat, origin, burstDir, side, len, thick * 2.2f, 0.55f, dim, 0.25f * beamFade);
            beamTail(buf, mat, origin, burstDir, side, len, thick * 0.8f, 0.45f, color, 0.65f * beamFade);
            beamTail(buf, mat, origin, burstDir, side, len, thick * 0.28f, 0.3f, core, 0.95f * beamFade);
        }

        float flashAlpha = (float) Math.pow(1.0 - p, 3.0) * fade * 0.7f;
        if (flashAlpha > 0.01f) {
            float flashR = radius * 0.35f * (0.6f + 0.4f * onset);
            quad(buf, mat, origin.add(up.scale(-flashR)), origin.add(right.scale(flashR)),
                    origin.add(up.scale(flashR)), origin.add(right.scale(-flashR)),
                    0xFFFFFF, flashAlpha, flashAlpha);
        }
    }

    private static void jaggedRing(BufferBuilder buf, Matrix4f mat, Vec3 center, Vec3 u, Vec3 v,
                                   double radius, double outThick, double coreThick,
                                   int outColor, int coreColor, float outAlpha, int jaggedPoints, double phase) {
        jaggedRingCore(buf, mat, center, u, v, radius, outThick, outColor, outAlpha, jaggedPoints, phase);
        jaggedRingCore(buf, mat, center, u, v, radius, coreThick, coreColor, outAlpha * 1.3f, jaggedPoints, phase);
    }

    private static void jaggedRingCore(BufferBuilder buf, Matrix4f mat, Vec3 center, Vec3 u, Vec3 v,
                                       double radius, double thick, int color, float alpha,
                                       int jaggedPoints, double phase) {
        int n = RING_SEGMENTS;
        for (int i = 0; i < n; i++) {
            double t0 = i / (double) n * Math.PI * 2.0;
            double t1 = (i + 1) / (double) n * Math.PI * 2.0;
            double j0 = Math.sin(jaggedPoints * t0 + phase) * 0.18 + Math.sin((jaggedPoints * 2 + 3) * t0 + phase * 1.7) * 0.07;
            double j1 = Math.sin(jaggedPoints * t1 + phase) * 0.18 + Math.sin((jaggedPoints * 2 + 3) * t1 + phase * 1.7) * 0.07;
            double rIn0 = radius * (1.0 + j0) - thick;
            double rOut0 = radius * (1.0 + j0) + thick;
            double rIn1 = radius * (1.0 + j1) - thick;
            double rOut1 = radius * (1.0 + j1) + thick;
            Vec3 d0 = u.scale(Math.cos(t0)).add(v.scale(Math.sin(t0)));
            Vec3 d1 = u.scale(Math.cos(t1)).add(v.scale(Math.sin(t1)));
            quad(buf, mat,
                    center.add(d0.scale(rIn0)), center.add(d0.scale(rOut0)),
                    center.add(d1.scale(rOut1)), center.add(d1.scale(rIn1)),
                    color, alpha, alpha);
        }
    }

    /** beam 尾部收细可控：tailFalloff 越小末端越粗。 */
    private static void beamTail(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 dir, Vec3 side,
                                 float length, float halfWidth, float tailFalloff, int color, float alpha) {
        int n = 7;
        for (int i = 0; i < n; i++) {
            float t0 = i / (float) n;
            float t1 = (i + 1) / (float) n;
            float w0 = halfWidth * (1.0f - t0 * tailFalloff);
            float w1 = halfWidth * (1.0f - t1 * tailFalloff);
            float a0 = alpha * (1.0f - t0 * (tailFalloff * 0.9f));
            float a1 = alpha * (1.0f - t1 * (tailFalloff * 0.9f));
            Vec3 c0 = origin.add(dir.scale(length * t0));
            Vec3 c1 = origin.add(dir.scale(length * t1));
            quad(buf, mat, c0.subtract(side.scale(w0)), c0.add(side.scale(w0)),
                    c1.add(side.scale(w1)), c1.subtract(side.scale(w1)), color, a0, a1);
        }
    }

    /** 红球终结：向外爆发冲击波 + 一道金色弧形光带。 */
    private static void redFinish(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                                  float radius, float thick, int color, float p) {
        float onset = Math.min(1.0f, p * 6.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.6);
        float expand = Math.min(1.0f, p * 2.5f);
        float r = radius * (0.15f + 0.9f * expand);
        int core = towardsWhite(color, 0.65f);
        int dim = dim(color, 0.5f);
        int gold = 0xFFD24A;
        int goldDim = dim(gold, 0.55f);

        int layers = 4;
        for (int i = 0; i < layers; i++) {
            double layerOffset = i / (double) layers * 0.3;
            float lr = (float) (radius * (0.15 + 0.9 * expand - layerOffset));
            if (lr <= 0) continue;
            float la = fade * (1.0f - (float) p) * 0.55f;
            ringBand(buf, mat, origin, right, up, Math.max(0, lr - thick * 1.6), lr + thick * 1.6,
                    dim, la * 0.55f);
            ringBand(buf, mat, origin, right, up, Math.max(0, lr - thick), lr + thick, color, la);
            ringBand(buf, mat, origin, right, up, Math.max(0, lr - thick * 0.35), lr + thick * 0.35,
                    core, la * 1.4f);
        }

        // 金色弧形光带，横扫 180°，略斜向上
        double halfArc = Math.toRadians(96.0);
        arcBand(buf, mat, origin, fwd, up, -halfArc, halfArc,
                r - thick * 4.0, r + thick * 4.0, goldDim, fade * 0.25f, 0.4f);
        arcBand(buf, mat, origin, fwd, up, -halfArc, halfArc,
                r - thick * 1.5, r + thick * 1.5, gold, fade * 0.55f, 0.3f);
        arcBand(buf, mat, origin, fwd, up, -halfArc, halfArc,
                r - thick * 0.3, r + thick * 0.3, towardsWhite(gold, 0.7f), fade * 0.9f, 0.2f);

        float flash = (float) Math.pow(1.0 - p, 2.4) * fade;
        if (flash > 0.01f) {
            float fr = radius * 0.4f * (0.6f + 0.4f * onset);
            quad(buf, mat, origin.add(up.scale(-fr)), origin.add(right.scale(fr)),
                    origin.add(up.scale(fr)), origin.add(right.scale(-fr)),
                    0xFFFFFF, flash, flash);
        }
    }

    /** 黄球终结：黑色虫洞核心 + 白色光环 + 垂直向下吸 beam。 */
    private static void yellowFinish(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                                      float radius, float thick, int color, float p) {
        float onset = Math.min(1.0f, p * 8.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.9);

        // 核心半径先小→大→小（出现→稳定→消失）
        float coreR = radius * (float) (0.3 + 0.5 * Math.sin(p * Math.PI));
        float ringR = coreR * 1.45f;

        jaggedRing(buf, mat, origin, right, up, ringR, thick * 2.0, thick * 0.7,
                dim(color, 0.5f), 0xFFFFFF, fade * 0.85f, 16, 5);
        ringBand(buf, mat, origin, right, up,
                Math.max(0, ringR - thick * 0.5), ringR + thick * 0.5,
                0xFFFFFF, fade * 1.0f);

        drawDisc(buf, mat, origin, right, up, coreR, 0x0A0408, fade * 0.9f);

        // 从核心垂直向下的吸聚 beam
        float beamLen = radius * 2.2f * onset;
        Vec3 groundHit = origin.add(up.scale(-beamLen));
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI * 2.0 / 8.0;
            Vec3 dir = right.scale(Math.cos(a)).add(up.scale(-1.0)).normalize();
            Vec3 side = dir.cross(right).lengthSqr() > 1.0E-6 ? dir.cross(right).normalize()
                    : dir.cross(up).normalize();
            float len = beamLen * (0.4f + 0.6f * onset);
            float ba = fade * (0.3f + 0.7f * (1.0f - p));
            beamTail(buf, mat, origin.add(up.scale(coreR * 0.8f)), dir, side,
                    len, thick * 1.6f, 0.65f, dim(color, 0.4f), ba * 0.3f);
            beamTail(buf, mat, origin.add(up.scale(coreR * 0.8f)), dir, side,
                    len, thick * 0.5f, 0.45f, 0xFFFFFF, ba * 0.7f);
        }

        float jetLen = radius * 0.8f * onset;
        if (jetLen > 0.1f) {
            for (int i = 0; i < 4; i++) {
                double a = i * Math.PI * 2.0 / 4.0 + p * 2.0;
                Vec3 dir = right.scale(Math.cos(a)).add(up.scale(1.0)).normalize();
                Vec3 side = dir.cross(right).lengthSqr() > 1.0E-6 ? dir.cross(right).normalize()
                        : dir.cross(up).normalize();
                float ja = fade * 0.6f * (1.0f - p * 0.5f);
                beamTail(buf, mat, origin.add(up.scale(coreR * 0.8f)), dir, side,
                        jetLen, thick * 1.2f, 0.5f, dim(color, 0.45f), ja * 0.4f);
                beamTail(buf, mat, origin.add(up.scale(coreR * 0.8f)), dir, side,
                        jetLen, thick * 0.35f, 0.3f, 0xFFFFFF, ja * 0.8f);
            }
        }
    }

    /** 蓝球终结：内收吸聚漩涡 + 放射闪电 beam。 */
    private static void blueFinish(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                                    float radius, float thick, int color, float p) {
        float onset = Math.min(1.0f, p * 5.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.7);
        int core = towardsWhite(color, 0.65f);
        int dim = dim(color, 0.55f);

        // 吸聚螺旋：半径从大内收到 0
        int segments = 80;
        double turns = 3.5;
        for (int i = 0; i < segments; i++) {
            double t0 = i / (double) segments;
            double t1 = (i + 1) / (double) segments;
            double shrink = Math.max(0.05, 1.0 - p * 1.05);
            double r0 = radius * shrink * (1.0 - t0 * 0.98);
            double r1 = radius * shrink * (1.0 - t1 * 0.98);
            double a0 = turns * Math.PI * 2.0 * t0 + p * 6.28;
            double a1 = turns * Math.PI * 2.0 * t1 + p * 6.28;
            Vec3 d0 = right.scale(Math.cos(a0)).add(up.scale(Math.sin(a0)));
            Vec3 d1 = right.scale(Math.cos(a1)).add(up.scale(Math.sin(a1)));
            // 螺旋同时沿 fwd 推进，形成 3D 漩涡
            double fwd0 = fwd.scale(r0 * 0.3).x + fwd.scale(r0 * 0.3).y * 0.0 + fwd.scale(r0 * 0.3).z;
            double fwd1 = fwd.scale(r1 * 0.3).x + fwd.scale(r1 * 0.3).y * 0.0 + fwd.scale(r1 * 0.3).z;
            Vec3 c0 = origin.add(d0.scale(r0)).add(fwd0 > 0 ? fwd.scale(r0 * 0.3) : Vec3.ZERO);
            Vec3 c1 = origin.add(d1.scale(r1)).add(fwd1 > 0 ? fwd.scale(r1 * 0.3) : Vec3.ZERO);
            float alpha = fade * (float) (0.3 + 0.7 * Math.pow(1.0 - t0, 2));
            quad(buf, mat, c0.subtract(d0.scale(thick * 0.4)), c0.add(d0.scale(thick * 0.4)),
                    c1.add(d1.scale(thick * 0.4)), c1.subtract(d1.scale(thick * 0.4)),
                    dim, alpha * 0.55f, alpha * 0.55f);
            quad(buf, mat, c0.subtract(d0.scale(thick * 0.12)), c0.add(d0.scale(thick * 0.12)),
                    c1.add(d1.scale(thick * 0.12)), c1.subtract(d1.scale(thick * 0.12)),
                    core, alpha * 0.95f, alpha * 0.95f);
        }

        int beams = 8;
        for (int i = 0; i < beams; i++) {
            double a = i * Math.PI * 2.0 / beams + p * 1.5;
            double sinA = Math.sin(a);
            Vec3 dir = right.scale(Math.cos(a)).add(up.scale(sinA)).add(fwd.scale(sinA * 0.25)).normalize();
            Vec3 side = dir.cross(right).lengthSqr() > 1.0E-6 ? dir.cross(right).normalize()
                    : dir.cross(up).normalize();
            float len = radius * 0.7f * (1.0f - p * 0.7f);
            float ba = fade * (1.0f - (float) p * 0.6f);
            beamTail(buf, mat, origin, dir, side, len, thick * 1.8f, 0.6f, dim, ba * 0.35f);
            beamTail(buf, mat, origin, dir, side, len, thick * 0.6f, 0.45f, color, ba * 0.7f);
            beamTail(buf, mat, origin, dir, side, len, thick * 0.2f, 0.25f, core, ba * 0.95f);
        }

        // 向内收缩的白芯
        float coreR = radius * 0.25f * (1.0f - p * 0.85f);
        if (coreR > 0.01f) {
            ringBand(buf, mat, origin, right, up, Math.max(0, coreR - thick * 0.5), coreR + thick * 0.5,
                    core, fade * 1.0f);
            drawDisc(buf, mat, origin, right, up, coreR * 0.6f, 0xFFFFFF, fade * 1.1f);
        }
    }

    /** 水平面 Z 字折线斩：按 横→斜→横 逐段显现。 */
    private static void hZigzag(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right,
                                float radius, float thick, int color, float p) {
        float onset = Math.min(1.0f, p * 8.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.7);
        int core = towardsWhite(color, 0.7f);
        int dim = dim(color, 0.5f);

        // 只取水平分量，让折线贴地
        Vec3 f = new Vec3(fwd.x, 0.0, fwd.z);
        if (f.lengthSqr() < 1.0E-6) {
            f = new Vec3(0.0, 0.0, -1.0);
        }
        f = f.normalize();
        Vec3 r = new Vec3(-f.z, 0.0, f.x);

        double w = radius * 0.55;
        double rear = -radius * 0.3;
        double front = radius * 0.7;
        Vec3 p1 = origin.add(f.scale(rear)).add(r.scale(-w));
        Vec3 p2 = origin.add(f.scale(rear)).add(r.scale(w));
        Vec3 p3 = origin.add(f.scale(front)).add(r.scale(-w));
        Vec3 p4 = origin.add(f.scale(front)).add(r.scale(w));
        Vec3[] pts = {p1, p2, p3, p4};

        double[] segLen = new double[3];
        double total = 0.0;
        for (int i = 0; i < 3; i++) {
            segLen[i] = pts[i].distanceTo(pts[i + 1]);
            total += segLen[i];
        }
        // Z 字逐段显现
        double reveal = total * Math.min(1.0, p * 4.0);
        double acc = 0.0;
        for (int i = 0; i < 3; i++) {
            if (acc >= reveal) {
                break;
            }
            double frac = Math.min(1.0, (reveal - acc) / segLen[i]);
            Vec3 a = pts[i];
            Vec3 b = pts[i].add(pts[i + 1].subtract(pts[i]).scale(frac));
            flatSegment(buf, mat, a, b, thick * 3.0f, dim, fade * 0.25f);
            flatSegment(buf, mat, a, b, thick, color, fade * 0.7f);
            flatSegment(buf, mat, a, b, thick * 0.35f, core, fade * 0.95f);
            acc += segLen[i];
        }
    }

    private static void flatSegment(BufferBuilder buf, Matrix4f mat, Vec3 a, Vec3 b, float halfWidth,
                                    int color, float alpha) {
        Vec3 d = b.subtract(a);
        if (d.lengthSqr() < 1.0E-8) {
            return;
        }
        Vec3 dir = d.normalize();
        Vec3 perp = new Vec3(-dir.z, 0.0, dir.x).scale(halfWidth);
        quad(buf, mat, a.subtract(perp), a.add(perp), b.add(perp), b.subtract(perp), color, alpha, alpha);
    }

    private static void hRing(BufferBuilder buf, Matrix4f mat, Vec3 origin, float radius, float thick,
                              int color, float p) {
        float onset = Math.min(1.0f, p * 10.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.7);
        float expand = Math.min(1.0f, p * 2.2f);
        float r = radius * (0.25f + 0.75f * expand);
        Vec3 u = new Vec3(1.0, 0.0, 0.0);
        Vec3 v = new Vec3(0.0, 0.0, 1.0);
        int core = towardsWhite(color, 0.6f);
        int dim = dim(color, 0.5f);

        ringBand(buf, mat, origin, u, v, Math.max(0, r - thick * 2.2), r + thick * 2.2, dim, fade * 0.32f);
        ringBand(buf, mat, origin, u, v, Math.max(0, r - thick), r + thick, color, fade * 0.72f);
        ringBand(buf, mat, origin, u, v, Math.max(0, r - thick * 0.3), r + thick * 0.3, core, fade * 0.95f);
        float r2 = r * 0.7f;
        ringBand(buf, mat, origin, u, v, Math.max(0, r2 - thick * 0.6), r2 + thick * 0.6, color, fade * 0.4f);
    }

    private static void bladeRing(BufferBuilder buf, Matrix4f mat, Vec3 origin, float radius, float thick,
                                  int color, float p, int style) {
        int count = 8 + (style & 0x0F) * 2;
        float onset = Math.min(1.0f, p * 12.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.55);
        int core = towardsWhite(color, 0.72f);
        int dimColor = dim(color, 0.5f);
        for (int i = 0; i < count; i++) {
            double a = i * Math.PI * 2.0 / count;
            Vec3 dir = new Vec3(Math.cos(a), 0.0, Math.sin(a));
            Vec3 side = new Vec3(-dir.z, 0.0, dir.x);
            double rr = radius * (0.82 + 0.3 * (((i * 37) % 11) / 11.0));
            Vec3 base = origin.add(dir.scale(rr));
            Vec3 lean = WORLD_UP.add(dir.scale(0.09)).normalize();
            float h = 1.7f + ((i * 53) % 7) / 7.0f * 0.9f;
            float w = thick * (0.5f + ((i * 29) % 5) / 8.0f);
            float alpha = fade * (0.55f + 0.45f * (((i * 17) % 9) / 9.0f));
            bladeQuad(buf, mat, base, lean, side, h, w * 2.6f, dimColor, alpha * 0.32f);
            bladeQuad(buf, mat, base, lean, side, h, w, color, alpha * 0.68f);
            bladeQuad(buf, mat, base, lean, side, h, w * 0.3f, core, alpha * 0.95f);
        }
    }

    private static void bladeQuad(BufferBuilder buf, Matrix4f mat, Vec3 base, Vec3 dir, Vec3 side,
                                  float height, float halfWidth, int color, float alpha) {
        if (alpha <= 0.004f) {
            return;
        }
        Vec3 top = base.add(dir.scale(height));
        float tip = halfWidth * 0.12f;
        quad4(buf, mat,
                base.subtract(side.scale(halfWidth)), base.add(side.scale(halfWidth)),
                top.add(side.scale(tip)), top.subtract(side.scale(tip)),
                color, alpha, alpha, alpha * 0.15f, alpha * 0.15f);
    }

    private static void groundArc(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 up,
                                  float radius, float thick, int color, float p) {
        Vec3 f = new Vec3(fwd.x, 0.0, fwd.z);
        if (f.lengthSqr() < 1.0E-6) {
            f = new Vec3(0.0, 0.0, -1.0);
        }
        f = f.normalize();
        float onset = Math.min(1.0f, p * 9.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.8);
        int core = towardsWhite(color, 0.65f);
        int dimColor = dim(color, 0.55f);
        // 从贴地 -35° 一路挑到竖直偏后
        double start = Math.toRadians(-35.0);
        double sweep = Math.toRadians(150.0) * Math.min(1.0f, p * 1.7f);
        double end = start + sweep;
        float r = radius * (0.92f + 0.12f * p);
        arcBand(buf, mat, origin, f, up, start, end,
                r - thick * 1.5, r + thick * 1.9, dimColor, 0.20f * fade, 0.65f);
        arcBand(buf, mat, origin, f, up, start, end,
                r - thick * 0.7, r + thick * 0.8, color, 0.62f * fade, 0.5f);
        arcBand(buf, mat, origin, f, up, start, end,
                r - thick * 0.2, r + thick * 0.24, core, 0.95f * fade, 0.35f);
        Vec3 u = new Vec3(1.0, 0.0, 0.0);
        Vec3 v = new Vec3(0.0, 0.0, 1.0);
        float gr = radius * 0.58f;
        ringBand(buf, mat, origin, u, v, Math.max(0, gr - thick * 0.28f), gr + thick * 0.28f,
                core, 0.55f * fade);
        ringBand(buf, mat, origin, u, v, Math.max(0, gr * 1.35f - thick * 0.12f), gr * 1.35f + thick * 0.12f,
                color, 0.22f * fade);
    }

    private static void bladeAura(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 right, Vec3 up,
                                  float radius, float thick, int color, float p) {
        float onset = Math.min(1.0f, p * 10.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.45);
        int core = towardsWhite(color, 0.7f);
        int dimColor = dim(color, 0.42f);
        int ribbons = 3;
        int seg = 40;
        double turns = 1.5;
        for (int r = 0; r < ribbons; r++) {
            double phase = r * Math.PI * 2.0 / ribbons;
            for (int i = 0; i < seg; i++) {
                double t0 = i / (double) seg;
                double t1 = (i + 1) / (double) seg;
                double a0 = phase + turns * Math.PI * 2.0 * t0;
                double a1 = phase + turns * Math.PI * 2.0 * t1;
                double rad0 = radius * (0.34 + 0.22 * Math.sin(t0 * Math.PI));
                double rad1 = radius * (0.34 + 0.22 * Math.sin(t1 * Math.PI));
                Vec3 d0 = new Vec3(Math.cos(a0), 0.0, Math.sin(a0));
                Vec3 d1 = new Vec3(Math.cos(a1), 0.0, Math.sin(a1));
                Vec3 c0 = origin.add(d0.scale(rad0)).add(up.scale(radius * (1.5 * t0 - 0.6)));
                Vec3 c1 = origin.add(d1.scale(rad1)).add(up.scale(radius * (1.5 * t1 - 0.6)));
                float a = fade * (0.4f + 0.6f * (float) Math.sin(t0 * Math.PI));
                quad(buf, mat, c0.subtract(d0.scale(thick * 1.9)), c0.add(d0.scale(thick * 1.9)),
                        c1.add(d1.scale(thick * 1.9)), c1.subtract(d1.scale(thick * 1.9)),
                        dimColor, a * 0.3f, a * 0.3f);
                quad(buf, mat, c0.subtract(d0.scale(thick * 0.55)), c0.add(d0.scale(thick * 0.55)),
                        c1.add(d1.scale(thick * 0.55)), c1.subtract(d1.scale(thick * 0.55)),
                        color, a * 0.72f, a * 0.72f);
                quad(buf, mat, c0.subtract(d0.scale(thick * 0.16)), c0.add(d0.scale(thick * 0.16)),
                        c1.add(d1.scale(thick * 0.16)), c1.subtract(d1.scale(thick * 0.16)),
                        core, a * 0.95f, a * 0.95f);
            }
        }
        // 火星：沿螺旋上浮的小亮点
        int sparks = 18;
        for (int i = 0; i < sparks; i++) {
            double ang = i * 2.399963 + p * 2.4;
            double rise = (i * 0.37 + p * 1.4) % 1.0;
            double rad = radius * (0.45 + 0.35 * (((i * 13) % 7) / 7.0));
            Vec3 pos = origin.add(new Vec3(Math.cos(ang), 0.0, Math.sin(ang)).scale(rad))
                    .add(up.scale(radius * (1.6 * rise - 0.7)));
            float sa = fade * (float) (0.4 + 0.6 * Math.sin(rise * Math.PI));
            float s = thick * (0.6f + ((i * 7) % 5) / 6.0f);
            quad(buf, mat, pos.add(right.scale(s)), pos.add(up.scale(s)),
                    pos.subtract(right.scale(s)), pos.subtract(up.scale(s)),
                    i % 3 == 0 ? 0xFFD24A : color, sa, sa);
        }
    }

    /** 突刺贯穿：极细红线 + 垂直细环 + 碎刃。 */
    private static void pierce(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                               float length, float thick, int color, float p, int style) {
        float onset = Math.min(1.0f, p * 12.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.65);
        int core = towardsWhite(color, 0.82f);
        int dimColor = dim(color, 0.5f);
        beamTail(buf, mat, origin, fwd, right, length, thick * 7.0f, 0.5f, dimColor, 0.16f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 7.0f, 0.5f, dimColor, 0.16f * fade);
        beamTail(buf, mat, origin, fwd, right, length, thick * 1.6f, 0.45f, color, 0.62f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 1.6f, 0.45f, color, 0.62f * fade);
        beamTail(buf, mat, origin, fwd, right, length, thick * 0.5f, 0.35f, core, 0.95f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 0.5f, 0.35f, core, 0.95f * fade);

        float ringR = length * 0.3f;
        Vec3 ringCenter = origin.add(fwd.scale(length * 0.34f));
        ringBand(buf, mat, ringCenter, right, up,
                Math.max(0, ringR - thick * 0.5f), ringR + thick * 0.5f, 0xFFFFFF, 0.75f * fade);

        int shards = 6 + (style & 0x07);
        for (int i = 0; i < shards; i++) {
            double t = (i + 0.5) / shards;
            double sideSign = (i % 2 == 0) ? 1.0 : -1.0;
            Vec3 c = origin.add(fwd.scale(length * t * 0.92))
                    .add(right.scale(sideSign * length * 0.07 * (0.4 + t)))
                    .add(up.scale(Math.sin(i * 1.7) * length * 0.08));
            Vec3 ax = fwd.scale(Math.cos(i * 0.9)).add(up.scale(Math.sin(i * 1.3))).normalize();
            Vec3 sd = ax.cross(fwd);
            if (sd.lengthSqr() < 1.0E-6) {
                sd = right;
            }
            sd = sd.normalize();
            float s = length * 0.05f * (0.6f + (i % 3) * 0.3f);
            float sa = fade * (0.45f + 0.55f * (((i * 11) % 7) / 7.0f));
            Vec3 tip = ax.scale(s * 2.8);
            quad4(buf, mat,
                    c.subtract(sd.scale(s)).subtract(tip), c.add(sd.scale(s)).subtract(tip),
                    c.add(sd.scale(s * 0.15f)).add(tip), c.subtract(sd.scale(s * 0.15f)).add(tip),
                    color, sa * 0.9f, sa * 0.9f, sa * 0.1f, sa * 0.1f);
        }
    }

    private static void spear(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 fwd, Vec3 right, Vec3 up,
                              float length, float thick, int color, float p) {
        float onset = Math.min(1.0f, p * 14.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.55);
        int core = towardsWhite(color, 0.85f);
        int dimColor = dim(color, 0.45f);
        beamTail(buf, mat, origin, fwd, right, length, thick * 0.42f, 0.55f, dimColor, 0.45f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 0.42f, 0.55f, dimColor, 0.45f * fade);
        beamTail(buf, mat, origin, fwd, right, length, thick * 0.14f, 0.55f, core, 0.85f * fade);
        beamTail(buf, mat, origin, fwd, up, length, thick * 0.14f, 0.55f, core, 0.85f * fade);
        // 矛头：十字交叉的双层菱形
        Vec3 base = origin.add(fwd.scale(length * 0.5f));
        Vec3 tip = origin.add(fwd.scale(length));
        quad4(buf, mat, base.add(right.scale(thick * 1.9f)), tip,
                base.subtract(right.scale(thick * 1.9f)), tip,
                color, 0.55f * fade, 0.92f * fade, 0.55f * fade, 0.92f * fade);
        quad4(buf, mat, base.add(up.scale(thick * 1.9f)), tip,
                base.subtract(up.scale(thick * 1.9f)), tip,
                color, 0.55f * fade, 0.92f * fade, 0.55f * fade, 0.92f * fade);
        quad4(buf, mat, base.add(right.scale(thick * 0.7f)), tip,
                base.subtract(right.scale(thick * 0.7f)), tip,
                core, 0.7f * fade, 0.95f * fade, 0.7f * fade, 0.95f * fade);
    }

    private static void spinWheel(BufferBuilder buf, Matrix4f mat, Vec3 origin, float radius, float thick,
                                  int color, float p, int style) {
        float onset = Math.min(1.0f, p * 8.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.55);
        int core = towardsWhite(color, 0.55f);
        int dimColor = dim(color, 0.5f);
        Vec3 u = new Vec3(1.0, 0.0, 0.0);
        Vec3 v = new Vec3(0.0, 0.0, 1.0);
        double spin = p * Math.PI * (((style & 1) == 0) ? 1.7 : -1.7);
        int segs = 4 + (style & 0x03);
        double gap = Math.toRadians(15.0);
        for (int i = 0; i < segs; i++) {
            double a0 = spin + i * Math.PI * 2.0 / segs + gap * 0.5;
            double a1 = spin + (i + 1) * Math.PI * 2.0 / segs - gap * 0.5;
            arcBand(buf, mat, origin, u, v, a0, a1,
                    radius - thick * 1.9, radius + thick * 1.9, dimColor, 0.16f * fade, 1.0f);
            arcBand(buf, mat, origin, u, v, a0, a1,
                    radius - thick * 0.85, radius + thick * 0.85, color, 0.52f * fade, 1.0f);
            arcBand(buf, mat, origin, u, v, a0, a1,
                    radius - thick * 0.26, radius + thick * 0.26, core, 0.85f * fade, 1.0f);
        }
        float inner = radius * 0.42f;
        ringBand(buf, mat, origin, u, v, Math.max(0, inner - thick * 0.55), inner + thick * 0.55,
                color, 0.4f * fade);
        ringBand(buf, mat, origin, u, v, 0.0, radius * 0.16f, dimColor, 0.2f * fade);
    }

    private static void domain(BufferBuilder buf, Matrix4f mat, Vec3 origin, float radius, float thick,
                               int color, float p) {
        float onset = Math.min(1.0f, p * 6.0f);
        float fade = onset * (float) Math.pow(1.0 - p, 0.55);
        int core = towardsWhite(color, 0.6f);
        int dimColor = dim(color, 0.5f);
        Vec3 u = new Vec3(1.0, 0.0, 0.0);
        Vec3 v = new Vec3(0.0, 0.0, 1.0);
        float r = radius * (0.35f + 0.65f * Math.min(1.0f, p * 2.4f));
        ringBand(buf, mat, origin, u, v, Math.max(0, r - thick * 2.4), r + thick * 2.4, dimColor, 0.18f * fade);
        ringBand(buf, mat, origin, u, v, Math.max(0, r - thick * 0.9), r + thick * 0.9, color, 0.52f * fade);
        ringBand(buf, mat, origin, u, v, Math.max(0, r - thick * 0.26), r + thick * 0.26, core, 0.9f * fade);

        // 圆柱光墙：逐层抬升的细环
        int layers = 5;
        for (int i = 1; i <= layers; i++) {
            float h = radius * 0.15f * i;
            float la = fade * (1.0f - i / (float) (layers + 1)) * 0.3f;
            ringBand(buf, mat, origin.add(WORLD_UP.scale(h)), u, v,
                    Math.max(0, r - thick * 0.45), r + thick * 0.45, color, la);
        }

        int spokes = 12;
        for (int i = 0; i < spokes; i++) {
            double a = i * Math.PI * 2.0 / spokes + p * 1.1;
            Vec3 d = u.scale(Math.cos(a)).add(v.scale(Math.sin(a)));
            beam(buf, mat, origin, d, WORLD_UP, r * 0.9f, thick * 0.14f, dimColor, 0.2f * fade);
        }

        // 旋转断环
        double spin = p * Math.PI * 1.5;
        float mid = r * 0.62f;
        for (int i = 0; i < 4; i++) {
            double a0 = spin + i * Math.PI * 0.5 + 0.17;
            double a1 = spin + (i + 1) * Math.PI * 0.5 - 0.17;
            arcBand(buf, mat, origin, u, v, a0, a1,
                    Math.max(0, mid - thick * 0.6), mid + thick * 0.6, color, 0.38f * fade, 1.0f);
        }
    }

    private static void drawDisc(BufferBuilder buf, Matrix4f mat, Vec3 center, Vec3 u, Vec3 v,
                                 float radius, int color, float alpha) {
        if (radius <= 0.01f || alpha <= 0.01f) return;
        int n = 16;
        for (int i = 0; i < n; i++) {
            double t0 = i / (double) n * Math.PI * 2.0;
            double t1 = (i + 1) / (double) n * Math.PI * 2.0;
            Vec3 d0 = u.scale(Math.cos(t0)).add(v.scale(Math.sin(t0)));
            Vec3 d1 = u.scale(Math.cos(t1)).add(v.scale(Math.sin(t1)));
            quad(buf, mat, center, center.add(d0.scale(radius)), center.add(d1.scale(radius)),
                    center, color, alpha, alpha);
        }
    }

    private static void arcBand(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 a, Vec3 b,
                                double startRad, double endRad, double rIn, double rOut,
                                int color, float alpha, float tailPow) {
        arcBand(buf, mat, origin, a, b, startRad, endRad, rIn, rOut, color, alpha, alpha, tailPow);
    }

    /** 内外缘 alpha 可分别控制的弧带：内缘调小即可羽化。 */
    private static void arcBand(BufferBuilder buf, Matrix4f mat, Vec3 origin, Vec3 a, Vec3 b,
                                double startRad, double endRad, double rIn, double rOut,
                                int color, float alphaIn, float alphaOut, float tailPow) {
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
            quad4(buf, mat,
                    origin.add(d0.scale(rIn)), origin.add(d0.scale(rOut)),
                    origin.add(d1.scale(rOut)), origin.add(d1.scale(rIn)),
                    color, alphaIn * f0, alphaOut * f0, alphaOut * f1, alphaIn * f1);
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
        quad4(buf, mat, a, b, c, d, color, alphaA, alphaA, alphaB, alphaB);
    }

    /** 每个顶点独立 alpha 的四边形，用于顶点级羽化。 */
    private static void quad4(BufferBuilder buf, Matrix4f mat, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                              int color, float alphaA, float alphaB, float alphaC, float alphaD) {
        int ai = alphaByte(alphaA);
        int bi = alphaByte(alphaB);
        int ci = alphaByte(alphaC);
        int di = alphaByte(alphaD);
        if (ai == 0 && bi == 0 && ci == 0 && di == 0) {
            return;
        }
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int bl = color & 0xFF;
        buf.addVertex(mat, (float) a.x, (float) a.y, (float) a.z).setColor(r, g, bl, ai);
        buf.addVertex(mat, (float) b.x, (float) b.y, (float) b.z).setColor(r, g, bl, bi);
        buf.addVertex(mat, (float) c.x, (float) c.y, (float) c.z).setColor(r, g, bl, ci);
        buf.addVertex(mat, (float) d.x, (float) d.y, (float) d.z).setColor(r, g, bl, di);
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
