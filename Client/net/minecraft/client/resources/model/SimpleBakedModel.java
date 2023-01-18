/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBakedModel
implements BakedModel {
    protected final List<BakedQuad> unculledFaces;
    protected final Map<Direction, List<BakedQuad>> culledFaces;
    protected final boolean hasAmbientOcclusion;
    protected final boolean isGui3d;
    protected final boolean usesBlockLight;
    protected final TextureAtlasSprite particleIcon;
    protected final ItemTransforms transforms;
    protected final ItemOverrides overrides;

    public SimpleBakedModel(List<BakedQuad> $$0, Map<Direction, List<BakedQuad>> $$1, boolean $$2, boolean $$3, boolean $$4, TextureAtlasSprite $$5, ItemTransforms $$6, ItemOverrides $$7) {
        this.unculledFaces = $$0;
        this.culledFaces = $$1;
        this.hasAmbientOcclusion = $$2;
        this.isGui3d = $$4;
        this.usesBlockLight = $$3;
        this.particleIcon = $$5;
        this.transforms = $$6;
        this.overrides = $$7;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState $$0, @Nullable Direction $$1, RandomSource $$2) {
        return $$1 == null ? this.unculledFaces : (List)this.culledFaces.get((Object)$$1);
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
        private final List<BakedQuad> unculledFaces = Lists.newArrayList();
        private final Map<Direction, List<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
        private final ItemOverrides overrides;
        private final boolean hasAmbientOcclusion;
        private TextureAtlasSprite particleIcon;
        private final boolean usesBlockLight;
        private final boolean isGui3d;
        private final ItemTransforms transforms;

        public Builder(BlockModel $$0, ItemOverrides $$1, boolean $$2) {
            this($$0.hasAmbientOcclusion(), $$0.getGuiLight().lightLikeBlock(), $$2, $$0.getTransforms(), $$1);
        }

        private Builder(boolean $$0, boolean $$1, boolean $$2, ItemTransforms $$3, ItemOverrides $$4) {
            for (Direction $$5 : Direction.values()) {
                this.culledFaces.put((Object)$$5, (Object)Lists.newArrayList());
            }
            this.overrides = $$4;
            this.hasAmbientOcclusion = $$0;
            this.usesBlockLight = $$1;
            this.isGui3d = $$2;
            this.transforms = $$3;
        }

        public Builder addCulledFace(Direction $$0, BakedQuad $$1) {
            ((List)this.culledFaces.get((Object)$$0)).add((Object)$$1);
            return this;
        }

        public Builder addUnculledFace(BakedQuad $$0) {
            this.unculledFaces.add((Object)$$0);
            return this;
        }

        public Builder particle(TextureAtlasSprite $$0) {
            this.particleIcon = $$0;
            return this;
        }

        public Builder item() {
            return this;
        }

        public BakedModel build() {
            if (this.particleIcon == null) {
                throw new RuntimeException("Missing particle!");
            }
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
        }
    }
}