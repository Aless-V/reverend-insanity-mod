package com.reverendinsanity.client.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// VFX管理器单例，负责更新和渲染所有活跃的VFX效果
public class VfxManager {

    private static final VfxManager INSTANCE = new VfxManager();
    private final List<VfxEffect> activeEffects = new ArrayList<>();

    public static VfxManager getInstance() {
        return INSTANCE;
    }

    public void addEffect(VfxEffect effect) {
        activeEffects.add(effect);
    }

    public void tick() {
        Iterator<VfxEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            VfxEffect effect = it.next();
            effect.tick();
            if (effect.isExpired()) {
                it.remove();
            }
        }
    }

    public void render(PoseStack poseStack, Camera camera, float partialTick) {
        if (activeEffects.isEmpty()) return;
        Vec3 cameraPos = camera.getPosition();

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (VfxEffect effect : activeEffects) {
            renderEffect(effect, poseStack, bufferSource, camera, partialTick);
        }

        poseStack.popPose();

        bufferSource.endBatch(ModRenderTypes.VFX_GLOW);
        bufferSource.endBatch(ModRenderTypes.VFX_TRANSLUCENT);
    }

    private void renderEffect(VfxEffect effect, PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, float partialTick) {
        float progress = effect.getProgress();
        float x = (float) effect.getX();
        float y = (float) effect.getY();
        float z = (float) effect.getZ();
        float scale = effect.getScale();
        int r = effect.getRed();
        int g = effect.getGreen();
        int b = effect.getBlue();
        int baseAlpha = effect.getAlpha();
        Matrix4f matrix = poseStack.last().pose();

        switch (effect.getType()) {
            case SLASH_ARC -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float arcProgress = Math.min(1.0f, progress * 2.0f);
                int alpha = progress > 0.5f ? (int) (baseAlpha * (1.0f - (progress - 0.5f) * 2.0f)) : baseAlpha;
                alpha = Math.max(0, Math.min(255, alpha));
                VfxGeometry.drawSlashArc(consumer, matrix, x, y, z,
                    effect.getDirX(), effect.getDirZ(),
                    scale, 120.0f, arcProgress, r, g, b, alpha);
            }
            case ENERGY_BEAM -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float beamLen = scale * 3.0f;
                float endX = x + effect.getDirX() * beamLen;
                float endY = y + effect.getDirY() * beamLen;
                float endZ = z + effect.getDirZ() * beamLen;
                float width = 0.3f * (1.0f - progress * 0.5f);
                int alpha = (int) (baseAlpha * (1.0f - progress));
                alpha = Math.max(0, Math.min(255, alpha));
                VfxGeometry.drawBeamLine(consumer, matrix, camera,
                    x, y, z, endX, endY, endZ, width, r, g, b, alpha);
            }
            case AURA_RING -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float innerR = scale * 0.8f;
                float outerR = scale * 1.2f;
                float yOff = progress < 0.5f ? progress * 1.0f : (1.0f - progress) * 1.0f;
                int alpha;
                if (progress < 0.3f) {
                    alpha = (int) (baseAlpha * (progress / 0.3f));
                } else if (progress > 0.7f) {
                    alpha = (int) (baseAlpha * ((1.0f - progress) / 0.3f));
                } else {
                    alpha = baseAlpha;
                }
                alpha = Math.max(0, Math.min(255, alpha));
                float rotation = effect.getAge() * 5.0f;
                poseStack.pushPose();
                poseStack.translate(x, y + yOff, z);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
                poseStack.translate(-x, -(y + yOff), -z);
                Matrix4f rotMatrix = poseStack.last().pose();
                VfxGeometry.drawRing(consumer, rotMatrix, x, y + yOff, z, innerR, outerR, 32, r, g, b, alpha);
                poseStack.popPose();
            }
            case HEAL_SPIRAL -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                int alpha = (int) (baseAlpha * (1.0f - progress));
                alpha = Math.max(0, Math.min(255, alpha));
                VfxGeometry.drawSpiral(consumer, matrix, x, y, z,
                    scale * 0.8f, 2.0f, 2.0f, progress, r, g, b, alpha);
            }
            case RIPPLE -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                float radius = progress * scale * 3.0f;
                int alpha = (int) (baseAlpha * (1.0f - progress));
                alpha = Math.max(0, Math.min(255, alpha));
                VfxGeometry.drawExpandingRing(consumer, matrix, x, y + 0.1f, z,
                    radius, 0.3f, 32, r, g, b, alpha);
            }
            case PULSE_WAVE -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float radius = progress * scale * 5.0f;
                int alpha = (int) (baseAlpha * (1.0f - progress));
                alpha = Math.max(0, Math.min(255, alpha));
                VfxGeometry.drawExpandingRing(consumer, matrix, x, y + 0.1f, z,
                    radius, 0.5f, 32, r, g, b, alpha);
            }
            case GLOW_BURST -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float size;
                int alpha;
                if (progress < 0.3f) {
                    size = scale * (progress / 0.3f) * 1.5f;
                    alpha = (int) (baseAlpha * (progress / 0.3f));
                } else {
                    size = scale * 1.5f * (1.0f - (progress - 0.3f) / 0.7f);
                    alpha = (int) (baseAlpha * (1.0f - (progress - 0.3f) / 0.7f));
                }
                alpha = Math.max(0, Math.min(255, alpha));
                VfxGeometry.drawBillboard(consumer, matrix, camera, x, y + 1.0f, z, size, r, g, b, alpha);
            }
            case SHADOW_FADE -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                int alpha = (int) (baseAlpha * (1.0f - progress));
                alpha = Math.max(0, Math.min(255, alpha));
                long seed = Double.doubleToLongBits(effect.getX() * 31 + effect.getZ() * 17);
                java.util.Random rand = new java.util.Random(seed);
                for (int i = 0; i < 5; i++) {
                    float ox = (rand.nextFloat() - 0.5f) * scale * (1.0f + progress);
                    float oy = rand.nextFloat() * scale * 0.5f + progress * 1.5f;
                    float oz = (rand.nextFloat() - 0.5f) * scale * (1.0f + progress);
                    float size = scale * 0.4f * (1.0f - progress * 0.5f);
                    VfxGeometry.drawBillboard(consumer, matrix, camera,
                        x + ox, y + oy, z + oz, size, r, g, b, alpha);
                }
            }
            case IMPACT_BURST -> {
                VertexConsumer consumer = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float size;
                int alpha;
                if (progress < 0.2f) {
                    size = scale * (progress / 0.2f) * 2.0f;
                    alpha = baseAlpha;
                } else {
                    size = scale * 2.0f * (1.0f - (progress - 0.2f) / 0.8f * 0.5f);
                    alpha = (int) (baseAlpha * (1.0f - (progress - 0.2f) / 0.8f));
                }
                alpha = Math.max(0, Math.min(255, alpha));
                VfxGeometry.drawBillboard(consumer, matrix, camera, x, y + 1f, z, size * 0.6f, r, g, b, alpha);
                float ringRadius = progress * scale * 3f;
                int ringAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.7f)));
                VfxGeometry.drawExpandingRing(consumer, matrix, x, y + 0.1f, z, ringRadius, 0.4f, 24, r, g, b, ringAlpha);
            }
            case BLACK_HOLE -> {
                float coreProgress;
                float streamIntensity;
                float fadeOut;

                if (progress < 0.15f) {
                    coreProgress = progress / 0.15f * 0.3f;
                    streamIntensity = progress / 0.15f;
                    fadeOut = 1.0f;
                } else if (progress < 0.7f) {
                    float p = (progress - 0.15f) / 0.55f;
                    coreProgress = 0.3f + p * 0.7f;
                    streamIntensity = 1.0f;
                    fadeOut = 1.0f;
                } else {
                    float p = (progress - 0.7f) / 0.3f;
                    coreProgress = 1.0f - p * 0.5f;
                    streamIntensity = 1.0f - p;
                    fadeOut = 1.0f - p;
                }

                float coreRadius = scale * coreProgress;
                float maxStreamRadius = scale * 3.0f;
                float rotation = effect.getAge() * 3.0f;

                if (streamIntensity > 0.01f) {
                    VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                    int sa = Math.max(0, Math.min(255, (int) (180 * streamIntensity * fadeOut)));
                    VfxGeometry.drawVortexStreams(tc, matrix,
                        x, y, z, maxStreamRadius, Math.max(0.1f, coreRadius * 0.8f),
                        8, (float) Math.toRadians(rotation),
                        15, 5, 25, sa);
                }

                if (coreProgress > 0.01f) {
                    VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                    int ca = Math.max(0, Math.min(255, (int) (240 * fadeOut)));
                    VfxGeometry.drawSphere(tc, matrix, x, y, z, coreRadius, 10, 16, 5, 0, 10, ca);
                }

                if (coreProgress > 0.2f) {
                    VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                    float ringR = coreRadius * 1.4f;
                    int ra = Math.max(0, Math.min(255, (int) (160 * fadeOut * Math.min(1f, (coreProgress - 0.2f) / 0.3f))));
                    poseStack.pushPose();
                    poseStack.translate(x, y, z);
                    poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(30f));
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
                    poseStack.translate(-x, -y, -z);
                    Matrix4f rm = poseStack.last().pose();
                    VfxGeometry.drawRing(gc, rm, x, y, z, ringR * 0.7f, ringR, 32, 60, 15, 90, ra);
                    poseStack.popPose();
                }

                if (coreProgress > 0.3f) {
                    VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                    float edgeR = coreRadius * 1.1f;
                    int ea = Math.max(0, Math.min(255, (int) (100 * fadeOut)));
                    for (int i = -2; i <= 2; i++) {
                        float yOff = i * coreRadius * 0.25f;
                        float rScale = (float) Math.sqrt(Math.max(0, 1.0f - (i * 0.25f) * (i * 0.25f)));
                        float rr = edgeR * rScale;
                        if (rr > 0.1f) {
                            int a2 = Math.max(0, Math.min(255, (int) (ea * rScale)));
                            VfxGeometry.drawRing(gc, matrix, x, y + yOff, z, rr * 0.95f, rr, 24, 80, 20, 120, a2);
                        }
                    }
                }
            }
            case TORNADO -> {
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float height = scale * 3.0f;
                float baseR = scale * 0.4f;
                float topR = scale * 1.5f;
                int rings = 12;
                float rot = effect.getAge() * 8.0f;

                int alpha;
                if (progress < 0.15f) {
                    alpha = (int) (baseAlpha * (progress / 0.15f));
                } else if (progress > 0.8f) {
                    alpha = (int) (baseAlpha * ((1f - progress) / 0.2f));
                } else {
                    alpha = baseAlpha;
                }
                alpha = Math.max(0, Math.min(255, alpha));

                for (int i = 0; i < rings; i++) {
                    float t = (float) i / (rings - 1);
                    float ringY = y + t * height;
                    float ringR = baseR + (topR - baseR) * t;
                    float innerR = ringR * 0.7f;
                    int ra = Math.max(0, Math.min(255, (int) (alpha * (0.5f + 0.5f * (1f - t)))));

                    poseStack.pushPose();
                    poseStack.translate(x, ringY, z);
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rot + t * 120f));
                    poseStack.translate(-x, -ringY, -z);
                    Matrix4f rm = poseStack.last().pose();
                    VfxGeometry.drawRing(gc, rm, x, ringY, z, innerR, ringR, 24, r, g, b, ra);
                    poseStack.popPose();
                }

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                VfxGeometry.drawSpiral(tc, matrix, x, y, z, baseR * 1.2f, height, 3.0f,
                    Math.min(1f, progress * 1.5f), r, g, b, Math.max(0, Math.min(255, (int) (alpha * 0.6f))));
            }
            case SKY_STRIKE -> {
                float beamH = 15.0f;
                float beamW = scale * 0.4f;
                float impactR = progress * scale * 2.5f;

                int alpha;
                if (progress < 0.1f) {
                    alpha = (int) (baseAlpha * (progress / 0.1f));
                } else {
                    alpha = (int) (baseAlpha * (1.0f - (progress - 0.1f) / 0.9f));
                }
                alpha = Math.max(0, Math.min(255, alpha));

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                VfxGeometry.drawBeamLine(gc, matrix, camera,
                    x, y + beamH, z, x, y, z,
                    beamW * (1.0f - progress * 0.5f), r, g, b, alpha);

                int flashAlpha = Math.max(0, Math.min(255, (int) (alpha * 1.5f)));
                float flashSize = scale * (progress < 0.2f ? progress / 0.2f : 1.0f - (progress - 0.2f) / 0.8f);
                VfxGeometry.drawBillboard(gc, matrix, camera, x, y + 0.5f, z, flashSize, r, g, b, flashAlpha);

                int ringAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.8f)));
                VfxGeometry.drawExpandingRing(gc, matrix, x, y + 0.1f, z, impactR, 0.4f, 24, r, g, b, ringAlpha);
            }
            case DOME_FIELD -> {
                float domeR = scale * 2.0f;
                float rot = effect.getAge() * 2.0f;

                int alpha;
                if (progress < 0.2f) {
                    alpha = (int) (baseAlpha * 0.5f * (progress / 0.2f));
                } else if (progress > 0.8f) {
                    alpha = (int) (baseAlpha * 0.5f * ((1f - progress) / 0.2f));
                } else {
                    alpha = (int) (baseAlpha * 0.5f);
                }
                alpha = Math.max(0, Math.min(255, alpha));

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                poseStack.pushPose();
                poseStack.translate(x, y, z);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rot));
                poseStack.translate(-x, -y, -z);
                Matrix4f rm = poseStack.last().pose();
                VfxGeometry.drawHemisphere(tc, rm, x, y, z, domeR, 8, 16, r, g, b, alpha);
                poseStack.popPose();

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                int edgeAlpha = Math.max(0, Math.min(255, (int) (alpha * 1.5f)));
                VfxGeometry.drawRing(gc, matrix, x, y + 0.1f, z, domeR * 0.9f, domeR, 32, r, g, b, edgeAlpha);
            }
            case SPATIAL_TEAR -> {
                float dirX = effect.getDirX();
                float dirY = effect.getDirY();
                float dirZ = effect.getDirZ();
                float dirLen = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                if (dirLen < 0.001f) {
                    dirX = 0.0f;
                    dirY = 1.0f;
                    dirZ = 0.0f;
                } else {
                    dirX /= dirLen;
                    dirY /= dirLen;
                    dirZ /= dirLen;
                }

                int alpha;
                if (progress < 0.1f) {
                    alpha = (int) (baseAlpha * (progress / 0.1f));
                } else {
                    alpha = (int) (baseAlpha * (1.0f - (progress - 0.1f) / 0.9f));
                }
                alpha = Math.max(0, Math.min(255, alpha));

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                long seed = Double.doubleToLongBits(effect.getX() * 31 + effect.getZ() * 17);
                java.util.Random rand = new java.util.Random(seed);
                float beamLen = scale * 2.0f;
                int segments = 5;
                float prevX = x;
                float prevY = y;
                float prevZ = z;
                for (int i = 0; i < segments; i++) {
                    float t = (float) (i + 1) / segments;
                    float jitter = scale * 0.45f * (1.0f - t);
                    float nextX = x + dirX * beamLen * t + (rand.nextFloat() - 0.5f) * jitter;
                    float nextY = y + dirY * beamLen * t + (rand.nextFloat() - 0.5f) * jitter * 0.4f;
                    float nextZ = z + dirZ * beamLen * t + (rand.nextFloat() - 0.5f) * jitter;
                    int segAlpha = Math.max(0, Math.min(255, (int) (alpha * (1.0f - t * 0.35f))));
                    VfxGeometry.drawBeamLine(gc, matrix, camera,
                        prevX, prevY, prevZ, nextX, nextY, nextZ,
                        0.14f, r, g, b, segAlpha);
                    prevX = nextX;
                    prevY = nextY;
                    prevZ = nextZ;
                }

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                float ringRadius = scale * (0.8f + progress * 1.4f);
                int ringAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.6f)));
                VfxGeometry.drawExpandingRing(tc, matrix, x, y + 0.05f, z,
                    ringRadius, 0.35f, 28,
                    Math.max(0, r / 5), Math.max(0, g / 5), Math.max(0, b / 5), ringAlpha);
            }
            case TIME_DISTORTION -> {
                float pulse = 1.0f + (float) Math.sin(effect.getAge() * 0.2f) * 0.3f;
                float radius = scale * pulse;
                int sphereAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.6f))));
                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                VfxGeometry.drawSphere(tc, matrix, x, y, z, radius, 10, 18, r, g, b, sphereAlpha);

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                int ringAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (0.9f - progress * 0.4f))));
                float rotation = effect.getAge() * 4.0f;
                float[] tilts = new float[]{30.0f, 60.0f, 90.0f};
                for (int i = 0; i < tilts.length; i++) {
                    poseStack.pushPose();
                    poseStack.translate(x, y, z);
                    poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(tilts[i]));
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation + i * 35.0f));
                    poseStack.translate(-x, -y, -z);
                    Matrix4f rm = poseStack.last().pose();
                    int a2 = Math.max(0, Math.min(255, (int) (ringAlpha * (1.0f - i * 0.15f))));
                    VfxGeometry.drawRing(gc, rm, x, y, z, radius * 0.88f, radius, 32, r, g, b, a2);
                    poseStack.popPose();
                }
            }
            case SOUL_VORTEX -> {
                float height = scale * 2.0f;
                int streamAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.5f))));
                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                float maxHeight = height * Math.min(1.0f, progress * 1.3f);
                float baseRot = (float) Math.toRadians(effect.getAge() * 3.0f);
                int layers = 4;
                for (int i = 0; i < layers; i++) {
                    float t = (float) i / (layers - 1);
                    float ly = y + maxHeight * t;
                    float outer = scale * (1.4f - t * 0.35f);
                    float inner = Math.max(0.1f, scale * (0.35f - t * 0.08f));
                    int la = Math.max(0, Math.min(255, (int) (streamAlpha * (1.0f - t * 0.35f))));
                    VfxGeometry.drawVortexStreams(tc, matrix, x, ly, z, outer, inner, 6, baseRot + t * 0.8f, r, g, b, la);
                }

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float topY = y + maxHeight;
                float ringRadius = scale * (0.4f + progress * 1.2f);
                int ringAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress))));
                VfxGeometry.drawExpandingRing(gc, matrix, x, topY, z, ringRadius, 0.25f, 24, r, g, b, ringAlpha);
            }
            case LAW_CHAINS -> {
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float beamLen = scale * 2.5f;
                float rotation = effect.getAge() * 2.0f;
                int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.4f))));
                for (int i = 0; i < 6; i++) {
                    float angle = (float) Math.toRadians(rotation + i * 60.0f);
                    float endX = x + (float) Math.cos(angle) * beamLen;
                    float endZ = z + (float) Math.sin(angle) * beamLen;
                    float endY = y + (float) Math.sin(effect.getAge() * 0.15f + i * 0.5f) * scale * 0.15f;
                    int beamAlpha = Math.max(0, Math.min(255, (int) (alpha * (0.8f + 0.2f * (float) Math.sin(effect.getAge() * 0.2f + i)))));
                    VfxGeometry.drawBeamLine(gc, matrix, camera, x, y, z, endX, endY, endZ, 0.15f, r, g, b, beamAlpha);
                }
            }
            case BEAST_PHANTOM -> {
                float growth;
                int alpha;
                if (progress < 0.3f) {
                    growth = progress / 0.3f;
                    alpha = baseAlpha;
                } else {
                    float p = (progress - 0.3f) / 0.7f;
                    growth = 1.0f - p * 0.4f;
                    alpha = (int) (baseAlpha * (1.0f - p));
                }
                alpha = Math.max(0, Math.min(255, alpha));

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float size = scale * 2.5f * (0.6f + growth * 0.9f);
                VfxGeometry.drawBillboard(gc, matrix, camera, x, y + 1.0f, z, size, r, g, b, alpha);

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                float auraRadius = scale * (0.8f + progress * 1.8f);
                int auraAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.6f)));
                VfxGeometry.drawExpandingRing(tc, matrix, x, y + 0.1f, z, auraRadius, 0.35f, 28, r, g, b, auraAlpha);
            }
            case BLOOD_RAIN -> {
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                long seed = Double.doubleToLongBits(effect.getX() * 31 + effect.getZ() * 17);
                java.util.Random rand = new java.util.Random(seed);
                int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.5f))));
                for (int i = 0; i < 15; i++) {
                    float dropX = x + (rand.nextFloat() - 0.5f) * scale * 2.0f;
                    float dropZ = z + (rand.nextFloat() - 0.5f) * scale * 2.0f;
                    float startY = y + scale * 4.0f + rand.nextFloat() * scale;
                    float localProgress = Math.max(0.0f, Math.min(1.0f, progress * 1.35f - i * 0.03f));
                    float dropY = startY + (y - startY) * localProgress;
                    int dropAlpha = Math.max(0, Math.min(255, (int) (alpha * (0.6f + 0.4f * localProgress))));
                    VfxGeometry.drawBillboard(gc, matrix, camera, dropX, dropY, dropZ, 0.2f, r, g, b, dropAlpha);
                    int trailAlpha = Math.max(0, Math.min(255, (int) (dropAlpha * 0.55f)));
                    VfxGeometry.drawBeamLine(gc, matrix, camera,
                        dropX, dropY + 0.45f, dropZ,
                        dropX, dropY - 0.25f, dropZ,
                        0.05f, r, g, b, trailAlpha);
                }
            }
            case EARTH_PILLAR -> {
                float pillarTopY = y + scale * 3.0f;
                int alpha;
                if (progress < 0.15f) {
                    alpha = (int) (baseAlpha * (progress / 0.15f));
                } else {
                    alpha = (int) (baseAlpha * (1.0f - progress * 0.35f));
                }
                alpha = Math.max(0, Math.min(255, alpha));

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                VfxGeometry.drawBeamLine(gc, matrix, camera,
                    x, y, z, x, pillarTopY, z,
                    scale * 0.6f, r, g, b, alpha);

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                for (int i = 0; i < 3; i++) {
                    float t = (float) i / 2.0f;
                    float rise = Math.max(0.0f, Math.min(1.0f, progress * 1.2f - t * 0.2f));
                    float ringY = y + rise * scale * 3.0f;
                    float ringRadius = scale * (0.6f + t * 0.35f + rise * 0.25f);
                    int ringAlpha = Math.max(0, Math.min(255, (int) (alpha * (1.0f - t * 0.2f))));
                    VfxGeometry.drawExpandingRing(tc, matrix, x, ringY, z, ringRadius, 0.28f, 24, r, g, b, ringAlpha);
                }
            }
            case STAR_RAIN -> {
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                long seed = Double.doubleToLongBits(effect.getX() * 31 + effect.getZ() * 17);
                java.util.Random rand = new java.util.Random(seed);
                for (int i = 0; i < 8; i++) {
                    float startAt = (float) i / 8.0f;
                    if (progress <= startAt) continue;
                    float localProgress = Math.max(0.0f, Math.min(1.0f, (progress - startAt) / (1.0f - startAt)));

                    float angle = rand.nextFloat() * (float) (Math.PI * 2.0);
                    float radius = scale * 3.0f * (0.35f + rand.nextFloat() * 0.65f);
                    float startX = x + (float) Math.cos(angle) * radius;
                    float startZ = z + (float) Math.sin(angle) * radius;
                    float startY = y + 20.0f + rand.nextFloat() * 4.0f;
                    float endX = x;
                    float endY = y + 0.3f;
                    float endZ = z;
                    float curX = startX + (endX - startX) * localProgress;
                    float curY = startY + (endY - startY) * localProgress;
                    float curZ = startZ + (endZ - startZ) * localProgress;
                    int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * localProgress * (1.0f - progress * 0.3f))));
                    VfxGeometry.drawBeamLine(gc, matrix, camera, startX, startY, startZ, curX, curY, curZ, 0.14f, r, g, b, alpha);
                    if (localProgress > 0.8f) {
                        int tipAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.8f)));
                        VfxGeometry.drawBillboard(gc, matrix, camera, curX, curY, curZ, 0.3f, r, g, b, tipAlpha);
                    }
                }
            }
            case VINE_CAGE -> {
                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                float height = scale * 2.0f * progress;
                int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.3f))));
                for (int i = 0; i < 4; i++) {
                    poseStack.pushPose();
                    poseStack.translate(x, y, z);
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(i * 90.0f + effect.getAge() * 4.0f));
                    poseStack.translate(-x, -y, -z);
                    Matrix4f sm = poseStack.last().pose();
                    VfxGeometry.drawSpiral(tc, sm, x, y, z, scale, Math.max(0.01f, height), 1.4f, 1.0f, r, g, b, alpha);
                    poseStack.popPose();
                }

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float closingRadius = Math.max(scale * 0.3f, scale * (1.5f - progress * 1.2f));
                int ringAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.2f))));
                VfxGeometry.drawExpandingRing(gc, matrix, x, y + height, z, closingRadius, 0.22f, 24, r, g, b, ringAlpha);
            }
            case QI_STORM -> {
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float breath = (float) (Math.sin(effect.getAge() * 0.3f) * 0.3f + 0.7f);
                int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * breath * (1.0f - progress * 0.2f))));
                float rotation = effect.getAge() * 4.0f;
                float ringRadius = scale * 1.5f;

                poseStack.pushPose();
                poseStack.translate(x, y, z);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
                poseStack.translate(-x, -y, -z);
                Matrix4f rm1 = poseStack.last().pose();
                VfxGeometry.drawRing(gc, rm1, x, y, z, ringRadius * 0.8f, ringRadius, 32, r, g, b, alpha);
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.translate(x, y, z);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(45.0f));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation * 1.1f));
                poseStack.translate(-x, -y, -z);
                Matrix4f rm2 = poseStack.last().pose();
                int alpha2 = Math.max(0, Math.min(255, (int) (alpha * 0.9f)));
                VfxGeometry.drawRing(gc, rm2, x, y, z, ringRadius * 0.82f, ringRadius, 32, r, g, b, alpha2);
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.translate(x, y, z);
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(45.0f));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation * 0.9f));
                poseStack.translate(-x, -y, -z);
                Matrix4f rm3 = poseStack.last().pose();
                int alpha3 = Math.max(0, Math.min(255, (int) (alpha * 0.85f)));
                VfxGeometry.drawRing(gc, rm3, x, y, z, ringRadius * 0.84f, ringRadius, 32, r, g, b, alpha3);
                poseStack.popPose();
            }
            case DRAGON_BREATH -> {
                float dirX = effect.getDirX();
                float dirY = effect.getDirY();
                float dirZ = effect.getDirZ();
                float dirLen = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                if (dirLen < 0.001f) {
                    dirX = 0.0f;
                    dirY = 0.0f;
                    dirZ = 1.0f;
                } else {
                    dirX /= dirLen;
                    dirY /= dirLen;
                    dirZ /= dirLen;
                }

                int alpha;
                if (progress < 0.15f) {
                    alpha = baseAlpha;
                } else {
                    alpha = (int) (baseAlpha * (1.0f - (progress - 0.15f) / 0.85f));
                }
                alpha = Math.max(0, Math.min(255, alpha));

                float beamLen = scale * 4.0f;
                float midX = x + dirX * beamLen * 0.5f;
                float midY = y + dirY * beamLen * 0.5f;
                float midZ = z + dirZ * beamLen * 0.5f;
                float endX = x + dirX * beamLen;
                float endY = y + dirY * beamLen;
                float endZ = z + dirZ * beamLen;

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                VfxGeometry.drawBeamLine(gc, matrix, camera,
                    x, y, z, midX, midY, midZ, scale * 0.3f, r, g, b, alpha);
                int tailAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.9f)));
                VfxGeometry.drawBeamLine(gc, matrix, camera,
                    midX, midY, midZ, endX, endY, endZ, scale * 1.2f, r, g, b, tailAlpha);

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                float ringRadius = scale * (0.8f + progress * 1.6f);
                int ringAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.7f)));
                VfxGeometry.drawExpandingRing(tc, matrix, endX, endY, endZ, ringRadius, 0.35f, 28, r, g, b, ringAlpha);
            }
            case FORTUNE_WHEEL -> {
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.3f))));
                float[] radii = new float[]{scale * 0.5f, scale, scale * 1.5f};
                float[] speeds = new float[]{3.0f, 5.0f, 7.0f};

                for (int i = 0; i < radii.length; i++) {
                    poseStack.pushPose();
                    poseStack.translate(x, y, z);
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(effect.getAge() * speeds[i]));
                    poseStack.translate(-x, -y, -z);
                    Matrix4f rm = poseStack.last().pose();
                    int ringAlpha = Math.max(0, Math.min(255, (int) (alpha * (1.0f - i * 0.15f))));
                    VfxGeometry.drawRing(gc, rm, x, y, z, radii[i] * 0.85f, radii[i], 32, r, g, b, ringAlpha);
                    poseStack.popPose();
                }

                for (int i = 0; i < 4; i++) {
                    float angle = (float) Math.toRadians(i * 90.0f + effect.getAge() * 2.0f);
                    float sx = x + (float) Math.cos(angle) * radii[0];
                    float sz = z + (float) Math.sin(angle) * radii[0];
                    float ex = x + (float) Math.cos(angle) * radii[2];
                    float ez = z + (float) Math.sin(angle) * radii[2];
                    int beamAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.8f)));
                    VfxGeometry.drawBeamLine(gc, matrix, camera, sx, y, sz, ex, y, ez, 0.1f, r, g, b, beamAlpha);
                }

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                int centerAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.65f)));
                VfxGeometry.drawBillboard(tc, matrix, camera, x, y + 0.1f, z, scale * 0.6f, r, g, b, centerAlpha);
            }
            case CONSTELLATION -> {
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                long seed = Double.doubleToLongBits(effect.getX() * 31 + effect.getZ() * 17);
                java.util.Random rand = new java.util.Random(seed);
                int pointCount = 6;
                float[][] points = new float[pointCount][3];
                float constellationRadius = scale * 2.0f;

                for (int i = 0; i < pointCount; i++) {
                    float u = rand.nextFloat() * 2.0f - 1.0f;
                    float theta = rand.nextFloat() * (float) (Math.PI * 2.0);
                    float base = (float) Math.sqrt(Math.max(0.0f, 1.0f - u * u));
                    points[i][0] = x + constellationRadius * base * (float) Math.cos(theta);
                    points[i][1] = y + scale * 0.8f + constellationRadius * u * 0.6f;
                    points[i][2] = z + constellationRadius * base * (float) Math.sin(theta);
                }

                for (int i = 0; i < pointCount; i++) {
                    float appear = (float) i / pointCount;
                    if (progress < appear) continue;
                    float localProgress = Math.max(0.0f, Math.min(1.0f, (progress - appear) / Math.max(0.0001f, 1.0f - appear)));
                    int pointAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * localProgress * (1.0f - progress * 0.3f))));
                    VfxGeometry.drawBillboard(gc, matrix, camera,
                        points[i][0], points[i][1], points[i][2],
                        Math.max(0.15f, scale * 0.12f), r, g, b, pointAlpha);
                }

                for (int i = 0; i < pointCount - 1; i++) {
                    float appear = (float) (i + 1) / pointCount;
                    if (progress < appear) continue;
                    float localProgress = Math.max(0.0f, Math.min(1.0f, (progress - appear) / Math.max(0.0001f, 1.0f - appear)));
                    int lineAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * localProgress * (1.0f - progress * 0.35f))));
                    VfxGeometry.drawBeamLine(gc, matrix, camera,
                        points[i][0], points[i][1], points[i][2],
                        points[i + 1][0], points[i + 1][1], points[i + 1][2],
                        0.09f, r, g, b, lineAlpha);
                }
            }
            case HEAVEN_DESCENT -> {
                int alpha;
                if (progress < 0.12f) {
                    alpha = (int) (baseAlpha * (progress / 0.12f));
                } else {
                    alpha = (int) (baseAlpha * (1.0f - (progress - 0.12f) / 0.88f * 0.7f));
                }
                alpha = Math.max(0, Math.min(255, alpha));

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                VfxGeometry.drawBeamLine(gc, matrix, camera,
                    x, y + 20.0f, z, x, y, z,
                    scale * 0.8f * (1.0f - progress * 0.2f), r, g, b, alpha);
                int flashAlpha = Math.max(0, Math.min(255, (int) (alpha * 1.2f)));
                VfxGeometry.drawBillboard(gc, matrix, camera, x, y + 0.6f, z, scale * (0.8f + progress * 0.6f), r, g, b, flashAlpha);

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                float ringRadius = scale * 3.0f * progress;
                int ringAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.75f)));
                VfxGeometry.drawExpandingRing(tc, matrix, x, y + 0.1f, z, ringRadius, 0.55f, 32, r, g, b, ringAlpha);
            }
            case PAINT_STROKE -> {
                float dirX = effect.getDirX();
                float dirY = effect.getDirY();
                float dirZ = effect.getDirZ();
                float dirLen = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                if (dirLen < 0.001f) {
                    dirX = 1.0f;
                    dirY = 0.0f;
                    dirZ = 0.0f;
                } else {
                    dirX /= dirLen;
                    dirY /= dirLen;
                    dirZ /= dirLen;
                }

                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                long seed = Double.doubleToLongBits(effect.getX() * 31 + effect.getZ() * 17);
                java.util.Random rand = new java.util.Random(seed);
                int count = 10;
                for (int i = 0; i < count; i++) {
                    float t = (float) i / (count - 1);
                    float appear = t * 0.8f;
                    if (progress < appear) continue;
                    float expand = Math.max(0.0f, Math.min(1.0f, (progress - appear) / Math.max(0.0001f, 1.0f - appear)));
                    float dist = scale * 3.0f * t * Math.max(0.1f, progress);
                    float px = x + dirX * dist * expand;
                    float py = y + dirY * dist * expand + (rand.nextFloat() - 0.5f) * scale * 0.1f;
                    float pz = z + dirZ * dist * expand + (rand.nextFloat() - 0.5f) * scale * 0.1f;
                    int vr = Math.max(0, Math.min(255, r + rand.nextInt(41) - 20));
                    int vg = Math.max(0, Math.min(255, g + rand.nextInt(41) - 20));
                    int vb = Math.max(0, Math.min(255, b + rand.nextInt(41) - 20));
                    int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - t * 0.6f) * (1.0f - progress * 0.35f))));
                    float size = Math.max(0.18f, scale * (0.24f + (1.0f - t) * 0.1f));
                    VfxGeometry.drawBillboard(tc, matrix, camera, px, py, pz, size, vr, vg, vb, alpha);
                }
            }
            case CHAIN_LIGHTNING -> {
                float dirX = effect.getDirX();
                float dirY = effect.getDirY();
                float dirZ = effect.getDirZ();
                float dirLen = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                if (dirLen < 0.001f) {
                    dirX = 1.0f;
                    dirY = 0.0f;
                    dirZ = 0.0f;
                } else {
                    dirX /= dirLen;
                    dirY /= dirLen;
                    dirZ /= dirLen;
                }

                long seed = Double.doubleToLongBits(effect.getX() * 31 + effect.getZ() * 17);
                java.util.Random rand = new java.util.Random(seed);
                int segments = 3 + rand.nextInt(2);
                float totalLen = scale * 3.0f;
                float flicker = (float) Math.abs(Math.sin(effect.getAge() * 20.0f));
                int alpha = Math.max(0, Math.min(255, (int) (baseAlpha * flicker * (1.0f - progress * 0.4f))));
                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);

                float prevX = x;
                float prevY = y;
                float prevZ = z;
                for (int i = 0; i < segments; i++) {
                    float t = (float) (i + 1) / segments;
                    float nextX = x + dirX * totalLen * t;
                    float nextY = y + dirY * totalLen * t;
                    float nextZ = z + dirZ * totalLen * t;
                    if (i < segments - 1) {
                        float jitter = scale * 0.9f * (1.0f - t * 0.5f);
                        nextX += (rand.nextFloat() - 0.5f) * jitter;
                        nextY += (rand.nextFloat() - 0.5f) * jitter * 0.5f;
                        nextZ += (rand.nextFloat() - 0.5f) * jitter;
                    }
                    int segAlpha = Math.max(0, Math.min(255, (int) (alpha * (1.0f - t * 0.15f))));
                    VfxGeometry.drawBeamLine(gc, matrix, camera, prevX, prevY, prevZ, nextX, nextY, nextZ, 0.16f, r, g, b, segAlpha);
                    prevX = nextX;
                    prevY = nextY;
                    prevZ = nextZ;
                }

                int tipAlpha = Math.max(0, Math.min(255, (int) (alpha * 0.7f)));
                VfxGeometry.drawBillboard(gc, matrix, camera, prevX, prevY, prevZ, Math.max(0.2f, scale * 0.35f), r, g, b, tipAlpha);
            }
            case GRAVITY_WELL -> {
                VertexConsumer tc = bufferSource.getBuffer(ModRenderTypes.VFX_TRANSLUCENT);
                int coreAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (0.7f - progress * 0.2f))));
                VfxGeometry.drawSphere(tc, matrix, x, y, z, scale * 0.5f, 10, 16,
                    Math.max(0, r / 8), Math.max(0, g / 8), Math.max(0, b / 8), coreAlpha);

                VertexConsumer gc = bufferSource.getBuffer(ModRenderTypes.VFX_GLOW);
                float outerRadius = scale * (2.4f - progress * 1.2f);
                float innerRadius = Math.max(scale * 0.25f, scale * (0.9f - progress * 0.6f));
                int streamAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.4f))));
                VfxGeometry.drawVortexStreams(gc, matrix, x, y, z,
                    Math.max(innerRadius + 0.1f, outerRadius), innerRadius,
                    8, (float) Math.toRadians(-effect.getAge() * 4.0f),
                    r, g, b, streamAlpha);

                float ringRadius = Math.max(scale * 0.3f, scale * (2.0f - progress * 1.7f));
                int ringAlpha = Math.max(0, Math.min(255, (int) (baseAlpha * (1.0f - progress * 0.5f))));
                VfxGeometry.drawExpandingRing(gc, matrix, x, y, z, ringRadius, 0.3f, 28, r, g, b, ringAlpha);
            }
        }
    }
}
