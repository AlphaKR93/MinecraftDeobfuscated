/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;

public class VertexMultiConsumer {
    public static VertexConsumer create() {
        throw new IllegalArgumentException();
    }

    public static VertexConsumer create(VertexConsumer $$0) {
        return $$0;
    }

    public static VertexConsumer create(VertexConsumer $$0, VertexConsumer $$1) {
        return new Double($$0, $$1);
    }

    public static VertexConsumer create(VertexConsumer ... $$0) {
        return new Multiple($$0);
    }

    static class Double
    implements VertexConsumer {
        private final VertexConsumer first;
        private final VertexConsumer second;

        public Double(VertexConsumer $$0, VertexConsumer $$1) {
            if ($$0 == $$1) {
                throw new IllegalArgumentException("Duplicate delegates");
            }
            this.first = $$0;
            this.second = $$1;
        }

        @Override
        public VertexConsumer vertex(double $$0, double $$1, double $$2) {
            this.first.vertex($$0, $$1, $$2);
            this.second.vertex($$0, $$1, $$2);
            return this;
        }

        @Override
        public VertexConsumer color(int $$0, int $$1, int $$2, int $$3) {
            this.first.color($$0, $$1, $$2, $$3);
            this.second.color($$0, $$1, $$2, $$3);
            return this;
        }

        @Override
        public VertexConsumer uv(float $$0, float $$1) {
            this.first.uv($$0, $$1);
            this.second.uv($$0, $$1);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int $$0, int $$1) {
            this.first.overlayCoords($$0, $$1);
            this.second.overlayCoords($$0, $$1);
            return this;
        }

        @Override
        public VertexConsumer uv2(int $$0, int $$1) {
            this.first.uv2($$0, $$1);
            this.second.uv2($$0, $$1);
            return this;
        }

        @Override
        public VertexConsumer normal(float $$0, float $$1, float $$2) {
            this.first.normal($$0, $$1, $$2);
            this.second.normal($$0, $$1, $$2);
            return this;
        }

        @Override
        public void vertex(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, int $$10, float $$11, float $$12, float $$13) {
            this.first.vertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12, $$13);
            this.second.vertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12, $$13);
        }

        @Override
        public void endVertex() {
            this.first.endVertex();
            this.second.endVertex();
        }

        @Override
        public void defaultColor(int $$0, int $$1, int $$2, int $$3) {
            this.first.defaultColor($$0, $$1, $$2, $$3);
            this.second.defaultColor($$0, $$1, $$2, $$3);
        }

        @Override
        public void unsetDefaultColor() {
            this.first.unsetDefaultColor();
            this.second.unsetDefaultColor();
        }
    }

    static class Multiple
    implements VertexConsumer {
        private final VertexConsumer[] delegates;

        public Multiple(VertexConsumer[] $$0) {
            for (int $$1 = 0; $$1 < $$0.length; ++$$1) {
                for (int $$2 = $$1 + 1; $$2 < $$0.length; ++$$2) {
                    if ($$0[$$1] != $$0[$$2]) continue;
                    throw new IllegalArgumentException("Duplicate delegates");
                }
            }
            this.delegates = $$0;
        }

        private void forEach(Consumer<VertexConsumer> $$0) {
            for (VertexConsumer $$1 : this.delegates) {
                $$0.accept((Object)$$1);
            }
        }

        @Override
        public VertexConsumer vertex(double $$0, double $$1, double $$2) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$3 -> $$3.vertex($$0, $$1, $$2)));
            return this;
        }

        @Override
        public VertexConsumer color(int $$0, int $$1, int $$2, int $$3) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$4 -> $$4.color($$0, $$1, $$2, $$3)));
            return this;
        }

        @Override
        public VertexConsumer uv(float $$0, float $$1) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$2 -> $$2.uv($$0, $$1)));
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int $$0, int $$1) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$2 -> $$2.overlayCoords($$0, $$1)));
            return this;
        }

        @Override
        public VertexConsumer uv2(int $$0, int $$1) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$2 -> $$2.uv2($$0, $$1)));
            return this;
        }

        @Override
        public VertexConsumer normal(float $$0, float $$1, float $$2) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$3 -> $$3.normal($$0, $$1, $$2)));
            return this;
        }

        @Override
        public void vertex(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, int $$10, float $$11, float $$12, float $$13) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$14 -> $$14.vertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12, $$13)));
        }

        @Override
        public void endVertex() {
            this.forEach((Consumer<VertexConsumer>)((Consumer)VertexConsumer::endVertex));
        }

        @Override
        public void defaultColor(int $$0, int $$1, int $$2, int $$3) {
            this.forEach((Consumer<VertexConsumer>)((Consumer)$$4 -> $$4.defaultColor($$0, $$1, $$2, $$3)));
        }

        @Override
        public void unsetDefaultColor() {
            this.forEach((Consumer<VertexConsumer>)((Consumer)VertexConsumer::unsetDefaultColor));
        }
    }
}