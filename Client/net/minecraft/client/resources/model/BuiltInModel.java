/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BuiltInModel
implements BakedModel {
    private final ItemTransforms itemTransforms;
    private final ItemOverrides overrides;
    private final TextureAtlasSprite particleTexture;
    private final boolean usesBlockLight;

    public BuiltInModel(ItemTransforms $$0, ItemOverrides $$1, TextureAtlasSprite $$2, boolean $$3) {
        this.itemTransforms = $$0;
        this.overrides = $$1;
        this.particleTexture = $$2;
        this.usesBlockLight = $$3;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState $$0, @Nullable Direction $$1, RandomSource $$2) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return this.usesBlockLight;
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.particleTexture;
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.itemTransforms;
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.overrides;
    }
}