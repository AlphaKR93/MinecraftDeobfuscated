/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteCoordinateExpander
implements VertexConsumer {
    private final VertexConsumer delegate;
    private final TextureAtlasSprite sprite;

    public SpriteCoordinateExpander(VertexConsumer $$0, TextureAtlasSprite $$1) {
        this.delegate = $$0;
        this.sprite = $$1;
    }

    @Override
    public VertexConsumer vertex(double $$0, double $$1, double $$2) {
        return this.delegate.vertex($$0, $$1, $$2);
    }

    @Override
    public VertexConsumer color(int $$0, int $$1, int $$2, int $$3) {
        return this.delegate.color($$0, $$1, $$2, $$3);
    }

    @Override
    public VertexConsumer uv(float $$0, float $$1) {
        return this.delegate.uv(this.sprite.getU($$0 * 16.0f), this.sprite.getV($$1 * 16.0f));
    }

    @Override
    public VertexConsumer overlayCoords(int $$0, int $$1) {
        return this.delegate.overlayCoords($$0, $$1);
    }

    @Override
    public VertexConsumer uv2(int $$0, int $$1) {
        return this.delegate.uv2($$0, $$1);
    }

    @Override
    public VertexConsumer normal(float $$0, float $$1, float $$2) {
        return this.delegate.normal($$0, $$1, $$2);
    }

    @Override
    public void endVertex() {
        this.delegate.endVertex();
    }

    @Override
    public void defaultColor(int $$0, int $$1, int $$2, int $$3) {
        this.delegate.defaultColor($$0, $$1, $$2, $$3);
    }

    @Override
    public void unsetDefaultColor() {
        this.delegate.unsetDefaultColor();
    }

    @Override
    public void vertex(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, int $$10, float $$11, float $$12, float $$13) {
        this.delegate.vertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, this.sprite.getU($$7 * 16.0f), this.sprite.getV($$8 * 16.0f), $$9, $$10, $$11, $$12, $$13);
    }
}