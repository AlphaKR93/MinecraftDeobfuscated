/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.BitSet
 *  java.util.Collections
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class MultiPartBakedModel
implements BakedModel {
    private final List<Pair<Predicate<BlockState>, BakedModel>> selectors;
    protected final boolean hasAmbientOcclusion;
    protected final boolean isGui3d;
    protected final boolean usesBlockLight;
    protected final TextureAtlasSprite particleIcon;
    protected final ItemTransforms transforms;
    protected final ItemOverrides overrides;
    private final Map<BlockState, BitSet> selectorCache = new Object2ObjectOpenCustomHashMap(Util.identityStrategy());

    public MultiPartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> $$0) {
        this.selectors = $$0;
        BakedModel $$1 = (BakedModel)((Pair)$$0.iterator().next()).getRight();
        this.hasAmbientOcclusion = $$1.useAmbientOcclusion();
        this.isGui3d = $$1.isGui3d();
        this.usesBlockLight = $$1.usesBlockLight();
        this.particleIcon = $$1.getParticleIcon();
        this.transforms = $$1.getTransforms();
        this.overrides = $$1.getOverrides();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState $$0, @Nullable Direction $$1, RandomSource $$2) {
        if ($$0 == null) {
            return Collections.emptyList();
        }
        BitSet $$3 = (BitSet)this.selectorCache.get((Object)$$0);
        if ($$3 == null) {
            $$3 = new BitSet();
            for (int $$4 = 0; $$4 < this.selectors.size(); ++$$4) {
                Pair $$5 = (Pair)this.selectors.get($$4);
                if (!((Predicate)$$5.getLeft()).test((Object)$$0)) continue;
                $$3.set($$4);
            }
            this.selectorCache.put((Object)$$0, (Object)$$3);
        }
        ArrayList $$6 = Lists.newArrayList();
        long $$7 = $$2.nextLong();
        for (int $$8 = 0; $$8 < $$3.length(); ++$$8) {
            if (!$$3.get($$8)) continue;
            $$6.addAll(((BakedModel)((Pair)this.selectors.get($$8)).getRight()).getQuads($$0, $$1, RandomSource.create($$7)));
        }
        return $$6;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.hasAmbientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return this.isGui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return this.usesBlockLight;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.particleIcon;
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.transforms;
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.overrides;
    }

    public static class Builder {
        private final List<Pair<Predicate<BlockState>, BakedModel>> selectors = Lists.newArrayList();

        public void add(Predicate<BlockState> $$0, BakedModel $$1) {
            this.selectors.add((Object)Pair.of($$0, (Object)$$1));
        }

        public BakedModel build() {
            return new MultiPartBakedModel(this.selectors);
        }
    }
}