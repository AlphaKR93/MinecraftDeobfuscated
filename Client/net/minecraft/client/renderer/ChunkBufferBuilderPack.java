/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Map
 *  java.util.stream.Collectors
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.RenderType;

public class ChunkBufferBuilderPack {
    private final Map<RenderType, BufferBuilder> builders = (Map)RenderType.chunkBufferLayers().stream().collect(Collectors.toMap($$0 -> $$0, $$0 -> new BufferBuilder($$0.bufferSize())));

    public BufferBuilder builder(RenderType $$0) {
        return (BufferBuilder)this.builders.get((Object)$$0);
    }

    public void clearAll() {
        this.builders.values().forEach(BufferBuilder::clear);
    }

    public void discardAll() {
        this.builders.values().forEach(BufferBuilder::discard);
    }
}