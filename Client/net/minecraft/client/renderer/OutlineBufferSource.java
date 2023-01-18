/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class OutlineBufferSource
implements MultiBufferSource {
    private final MultiBufferSource.BufferSource bufferSource;
    private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
    private int teamR = 255;
    private int teamG = 255;
    private int teamB = 255;
    private int teamA = 255;

    public OutlineBufferSource(MultiBufferSource.BufferSource $$0) {
        this.bufferSource = $$0;
    }

    @Override
    public VertexConsumer getBuffer(RenderType $$0) {
        if ($$0.isOutline()) {
            VertexConsumer $$1 = this.outlineBufferSource.getBuffer($$0);
            return new EntityOutlineGenerator($$1, this.teamR, this.teamG, this.teamB, this.teamA);
        }
        VertexConsumer $$2 = this.bufferSource.getBuffer($$0);
        Optional<RenderType> $$3 = $$0.outline();
        if ($$3.isPresent()) {
            VertexConsumer $$4 = this.outlineBufferSource.getBuffer((RenderType)$$3.get());
            EntityOutlineGenerator $$5 = new EntityOutlineGenerator($$4, this.teamR, this.teamG, this.teamB, this.teamA);
            return VertexMultiConsumer.create((VertexConsumer)$$5, $$2);
        }
        return $$2;
    }

    public void setColor(int $$0, int $$1, int $$2, int $$3) {
        this.teamR = $$0;
        this.teamG = $$1;
        this.teamB = $$2;
        this.teamA = $$3;
    }

    public void endOutlineBatch() {
        this.outlineBufferSource.endBatch();
    }

    static class EntityOutlineGenerator
    extends DefaultedVertexConsumer {
        private final VertexConsumer delegate;
        private double x;
        private double y;
        private double z;
        private float u;
        private float v;

        EntityOutlineGenerator(VertexConsumer $$0, int $$1, int $$2, int $$3, int $$4) {
            this.delegate = $$0;
            super.defaultColor($$1, $$2, $$3, $$4);
        }

        @Override
        public void defaultColor(int $$0, int $$1, int $$2, int $$3) {
        }

        @Override
        public void unsetDefaultColor() {
        }

        @Override
        public VertexConsumer vertex(double $$0, double $$1, double $$2) {
            this.x = $$0;
            this.y = $$1;
            this.z = $$2;
            return this;
        }

        @Override
        public VertexConsumer color(int $$0, int $$1, int $$2, int $$3) {
            return this;
        }

        @Override
        public VertexConsumer uv(float $$0, float $$1) {
            this.u = $$0;
            this.v = $$1;
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int $$0, int $$1) {
            return this;
        }

        @Override
        public VertexConsumer uv2(int $$0, int $$1) {
            return this;
        }

        @Override
        public VertexConsumer normal(float $$0, float $$1, float $$2) {
            return this;
        }

        @Override
        public void vertex(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, int $$10, float $$11, float $$12, float $$13) {
            this.delegate.vertex($$0, $$1, $$2).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv($$7, $$8).endVertex();
        }

        @Override
        public void endVertex() {
            this.delegate.vertex(this.x, this.y, this.z).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(this.u, this.v).endVertex();
        }
    }
}