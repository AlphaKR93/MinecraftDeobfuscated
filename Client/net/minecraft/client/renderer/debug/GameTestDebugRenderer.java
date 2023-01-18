/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class GameTestDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final float PADDING = 0.02f;
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos $$0, int $$1, String $$2, int $$3) {
        this.markers.put((Object)$$0, (Object)new Marker($$1, $$2, Util.getMillis() + (long)$$3));
    }

    @Override
    public void clear() {
        this.markers.clear();
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$12, double $$2, double $$3, double $$4) {
        long $$5 = Util.getMillis();
        this.markers.entrySet().removeIf($$1 -> $$5 > ((Marker)$$1.getValue()).removeAtTime);
        this.markers.forEach(this::renderMarker);
    }

    private void renderMarker(BlockPos $$0, Marker $$1) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(0.0f, 1.0f, 0.0f, 0.75f);
        DebugRenderer.renderFilledBox($$0, 0.02f, $$1.getR(), $$1.getG(), $$1.getB(), $$1.getA());
        if (!$$1.text.isEmpty()) {
            double $$2 = (double)$$0.getX() + 0.5;
            double $$3 = (double)$$0.getY() + 1.2;
            double $$4 = (double)$$0.getZ() + 0.5;
            DebugRenderer.renderFloatingText($$1.text, $$2, $$3, $$4, -1, 0.01f, true, 0.0f, true);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    static class Marker {
        public int color;
        public String text;
        public long removeAtTime;

        public Marker(int $$0, String $$1, long $$2) {
            this.color = $$0;
            this.text = $$1;
            this.removeAtTime = $$2;
        }

        public float getR() {
            return (float)(this.color >> 16 & 0xFF) / 255.0f;
        }

        public float getG() {
            return (float)(this.color >> 8 & 0xFF) / 255.0f;
        }

        public float getB() {
            return (float)(this.color & 0xFF) / 255.0f;
        }

        public float getA() {
            return (float)(this.color >> 24 & 0xFF) / 255.0f;
        }
    }
}