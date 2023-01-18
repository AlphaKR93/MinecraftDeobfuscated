/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

public class BakedGlyph {
    private final RenderType normalType;
    private final RenderType seeThroughType;
    private final RenderType polygonOffsetType;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final float left;
    private final float right;
    private final float up;
    private final float down;

    public BakedGlyph(RenderType $$0, RenderType $$1, RenderType $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10) {
        this.normalType = $$0;
        this.seeThroughType = $$1;
        this.polygonOffsetType = $$2;
        this.u0 = $$3;
        this.u1 = $$4;
        this.v0 = $$5;
        this.v1 = $$6;
        this.left = $$7;
        this.right = $$8;
        this.up = $$9;
        this.down = $$10;
    }

    public void render(boolean $$0, float $$1, float $$2, Matrix4f $$3, VertexConsumer $$4, float $$5, float $$6, float $$7, float $$8, int $$9) {
        int $$10 = 3;
        float $$11 = $$1 + this.left;
        float $$12 = $$1 + this.right;
        float $$13 = this.up - 3.0f;
        float $$14 = this.down - 3.0f;
        float $$15 = $$2 + $$13;
        float $$16 = $$2 + $$14;
        float $$17 = $$0 ? 1.0f - 0.25f * $$13 : 0.0f;
        float $$18 = $$0 ? 1.0f - 0.25f * $$14 : 0.0f;
        $$4.vertex($$3, $$11 + $$17, $$15, 0.0f).color($$5, $$6, $$7, $$8).uv(this.u0, this.v0).uv2($$9).endVertex();
        $$4.vertex($$3, $$11 + $$18, $$16, 0.0f).color($$5, $$6, $$7, $$8).uv(this.u0, this.v1).uv2($$9).endVertex();
        $$4.vertex($$3, $$12 + $$18, $$16, 0.0f).color($$5, $$6, $$7, $$8).uv(this.u1, this.v1).uv2($$9).endVertex();
        $$4.vertex($$3, $$12 + $$17, $$15, 0.0f).color($$5, $$6, $$7, $$8).uv(this.u1, this.v0).uv2($$9).endVertex();
    }

    public void renderEffect(Effect $$0, Matrix4f $$1, VertexConsumer $$2, int $$3) {
        $$2.vertex($$1, $$0.x0, $$0.y0, $$0.depth).color($$0.r, $$0.g, $$0.b, $$0.a).uv(this.u0, this.v0).uv2($$3).endVertex();
        $$2.vertex($$1, $$0.x1, $$0.y0, $$0.depth).color($$0.r, $$0.g, $$0.b, $$0.a).uv(this.u0, this.v1).uv2($$3).endVertex();
        $$2.vertex($$1, $$0.x1, $$0.y1, $$0.depth).color($$0.r, $$0.g, $$0.b, $$0.a).uv(this.u1, this.v1).uv2($$3).endVertex();
        $$2.vertex($$1, $$0.x0, $$0.y1, $$0.depth).color($$0.r, $$0.g, $$0.b, $$0.a).uv(this.u1, this.v0).uv2($$3).endVertex();
    }

    public RenderType renderType(Font.DisplayMode $$0) {
        return switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case Font.DisplayMode.NORMAL -> this.normalType;
            case Font.DisplayMode.SEE_THROUGH -> this.seeThroughType;
            case Font.DisplayMode.POLYGON_OFFSET -> this.polygonOffsetType;
        };
    }

    public static class Effect {
        protected final float x0;
        protected final float y0;
        protected final float x1;
        protected final float y1;
        protected final float depth;
        protected final float r;
        protected final float g;
        protected final float b;
        protected final float a;

        public Effect(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8) {
            this.x0 = $$0;
            this.y0 = $$1;
            this.x1 = $$2;
            this.y1 = $$3;
            this.depth = $$4;
            this.r = $$5;
            this.g = $$6;
            this.b = $$7;
            this.a = $$8;
        }
    }
}