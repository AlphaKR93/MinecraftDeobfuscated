/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  java.lang.Object
 *  java.util.SortedMap
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.ModelBakery;

public class RenderBuffers {
    private final ChunkBufferBuilderPack fixedBufferPack = new ChunkBufferBuilderPack();
    private final SortedMap<RenderType, BufferBuilder> fixedBuffers = (SortedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), $$0 -> {
        $$0.put((Object)Sheets.solidBlockSheet(), (Object)this.fixedBufferPack.builder(RenderType.solid()));
        $$0.put((Object)Sheets.cutoutBlockSheet(), (Object)this.fixedBufferPack.builder(RenderType.cutout()));
        $$0.put((Object)Sheets.bannerSheet(), (Object)this.fixedBufferPack.builder(RenderType.cutoutMipped()));
        $$0.put((Object)Sheets.translucentCullBlockSheet(), (Object)this.fixedBufferPack.builder(RenderType.translucent()));
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, Sheets.shieldSheet());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, Sheets.bedSheet());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, Sheets.shulkerBoxSheet());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, Sheets.signSheet());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, Sheets.hangingSignSheet());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, Sheets.chestSheet());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.translucentNoCrumbling());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.armorGlint());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.armorEntityGlint());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.glint());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.glintDirect());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.glintTranslucent());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.entityGlint());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.entityGlintDirect());
        RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, RenderType.waterMask());
        ModelBakery.DESTROY_TYPES.forEach($$1 -> RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder>)$$0, $$1));
    });
    private final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediateWithBuffers(this.fixedBuffers, new BufferBuilder(256));
    private final MultiBufferSource.BufferSource crumblingBufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
    private final OutlineBufferSource outlineBufferSource = new OutlineBufferSource(this.bufferSource);

    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> $$0, RenderType $$1) {
        $$0.put((Object)$$1, (Object)new BufferBuilder($$1.bufferSize()));
    }

    public ChunkBufferBuilderPack fixedBufferPack() {
        return this.fixedBufferPack;
    }

    public MultiBufferSource.BufferSource bufferSource() {
        return this.bufferSource;
    }

    public MultiBufferSource.BufferSource crumblingBufferSource() {
        return this.crumblingBufferSource;
    }

    public OutlineBufferSource outlineBufferSource() {
        return this.outlineBufferSource;
    }
}