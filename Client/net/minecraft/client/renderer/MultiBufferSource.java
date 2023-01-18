/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.renderer.RenderType;

public interface MultiBufferSource {
    public static BufferSource immediate(BufferBuilder $$0) {
        return MultiBufferSource.immediateWithBuffers((Map<RenderType, BufferBuilder>)ImmutableMap.of(), $$0);
    }

    public static BufferSource immediateWithBuffers(Map<RenderType, BufferBuilder> $$0, BufferBuilder $$1) {
        return new BufferSource($$1, $$0);
    }

    public VertexConsumer getBuffer(RenderType var1);

    public static class BufferSource
    implements MultiBufferSource {
        protected final BufferBuilder builder;
        protected final Map<RenderType, BufferBuilder> fixedBuffers;
        protected Optional<RenderType> lastState = Optional.empty();
        protected final Set<BufferBuilder> startedBuffers = Sets.newHashSet();

        protected BufferSource(BufferBuilder $$0, Map<RenderType, BufferBuilder> $$1) {
            this.builder = $$0;
            this.fixedBuffers = $$1;
        }

        @Override
        public VertexConsumer getBuffer(RenderType $$0) {
            Optional<RenderType> $$1 = $$0.asOptional();
            BufferBuilder $$2 = this.getBuilderRaw($$0);
            if (!Objects.equals(this.lastState, $$1) || !$$0.canConsolidateConsecutiveGeometry()) {
                RenderType $$3;
                if (this.lastState.isPresent() && !this.fixedBuffers.containsKey((Object)($$3 = (RenderType)this.lastState.get()))) {
                    this.endBatch($$3);
                }
                if (this.startedBuffers.add((Object)$$2)) {
                    $$2.begin($$0.mode(), $$0.format());
                }
                this.lastState = $$1;
            }
            return $$2;
        }

        private BufferBuilder getBuilderRaw(RenderType $$0) {
            return (BufferBuilder)this.fixedBuffers.getOrDefault((Object)$$0, (Object)this.builder);
        }

        public void endLastBatch() {
            if (this.lastState.isPresent()) {
                RenderType $$0 = (RenderType)this.lastState.get();
                if (!this.fixedBuffers.containsKey((Object)$$0)) {
                    this.endBatch($$0);
                }
                this.lastState = Optional.empty();
            }
        }

        public void endBatch() {
            this.lastState.ifPresent($$0 -> {
                VertexConsumer $$1 = this.getBuffer((RenderType)$$0);
                if ($$1 == this.builder) {
                    this.endBatch((RenderType)$$0);
                }
            });
            for (RenderType $$02 : this.fixedBuffers.keySet()) {
                this.endBatch($$02);
            }
        }

        public void endBatch(RenderType $$0) {
            BufferBuilder $$1 = this.getBuilderRaw($$0);
            boolean $$2 = Objects.equals(this.lastState, $$0.asOptional());
            if (!$$2 && $$1 == this.builder) {
                return;
            }
            if (!this.startedBuffers.remove((Object)$$1)) {
                return;
            }
            $$0.end($$1, 0, 0, 0);
            if ($$2) {
                this.lastState = Optional.empty();
            }
        }
    }
}