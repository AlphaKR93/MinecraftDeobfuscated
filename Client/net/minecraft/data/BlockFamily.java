/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Optional
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;

public class BlockFamily {
    private final Block baseBlock;
    final Map<Variant, Block> variants = Maps.newHashMap();
    FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
    boolean generateModel = true;
    boolean generateRecipe = true;
    @Nullable
    String recipeGroupPrefix;
    @Nullable
    String recipeUnlockedBy;

    BlockFamily(Block $$0) {
        this.baseBlock = $$0;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public Map<Variant, Block> getVariants() {
        return this.variants;
    }

    public Block get(Variant $$0) {
        return (Block)this.variants.get((Object)$$0);
    }

    public boolean shouldGenerateModel() {
        return this.generateModel;
    }

    public boolean shouldGenerateRecipe(FeatureFlagSet $$0) {
        return this.generateRecipe && this.requiredFeatures.isSubsetOf($$0);
    }

    public Optional<String> getRecipeGroupPrefix() {
        if (StringUtils.isBlank((CharSequence)this.recipeGroupPrefix)) {
            return Optional.empty();
        }
        return Optional.of((Object)this.recipeGroupPrefix);
    }

    public Optional<String> getRecipeUnlockedBy() {
        if (StringUtils.isBlank((CharSequence)this.recipeUnlockedBy)) {
            return Optional.empty();
        }
        return Optional.of((Object)this.recipeUnlockedBy);
    }

    public static class Builder {
        private final BlockFamily family;

        public Builder(Block $$0) {
            this.family = new BlockFamily($$0);
        }

        public BlockFamily getFamily() {
            return this.family;
        }

        public Builder button(Block $$0) {
            this.family.variants.put((Object)Variant.BUTTON, (Object)$$0);
            return this;
        }

        public Builder chiseled(Block $$0) {
            this.family.variants.put((Object)Variant.CHISELED, (Object)$$0);
            return this;
        }

        public Builder mosaic(Block $$0) {
            this.family.variants.put((Object)Variant.MOSAIC, (Object)$$0);
            return this;
        }

        public Builder cracked(Block $$0) {
            this.family.variants.put((Object)Variant.CRACKED, (Object)$$0);
            return this;
        }

        public Builder cut(Block $$0) {
            this.family.variants.put((Object)Variant.CUT, (Object)$$0);
            return this;
        }

        public Builder door(Block $$0) {
            this.family.variants.put((Object)Variant.DOOR, (Object)$$0);
            return this;
        }

        public Builder customFence(Block $$0) {
            this.family.variants.put((Object)Variant.CUSTOM_FENCE, (Object)$$0);
            return this;
        }

        public Builder fence(Block $$0) {
            this.family.variants.put((Object)Variant.FENCE, (Object)$$0);
            return this;
        }

        public Builder customFenceGate(Block $$0) {
            this.family.variants.put((Object)Variant.CUSTOM_FENCE_GATE, (Object)$$0);
            return this;
        }

        public Builder fenceGate(Block $$0) {
            this.family.variants.put((Object)Variant.FENCE_GATE, (Object)$$0);
            return this;
        }

        public Builder sign(Block $$0, Block $$1) {
            this.family.variants.put((Object)Variant.SIGN, (Object)$$0);
            this.family.variants.put((Object)Variant.WALL_SIGN, (Object)$$1);
            return this;
        }

        public Builder slab(Block $$0) {
            this.family.variants.put((Object)Variant.SLAB, (Object)$$0);
            return this;
        }

        public Builder stairs(Block $$0) {
            this.family.variants.put((Object)Variant.STAIRS, (Object)$$0);
            return this;
        }

        public Builder pressurePlate(Block $$0) {
            this.family.variants.put((Object)Variant.PRESSURE_PLATE, (Object)$$0);
            return this;
        }

        public Builder polished(Block $$0) {
            this.family.variants.put((Object)Variant.POLISHED, (Object)$$0);
            return this;
        }

        public Builder trapdoor(Block $$0) {
            this.family.variants.put((Object)Variant.TRAPDOOR, (Object)$$0);
            return this;
        }

        public Builder wall(Block $$0) {
            this.family.variants.put((Object)Variant.WALL, (Object)$$0);
            return this;
        }

        public Builder dontGenerateModel() {
            this.family.generateModel = false;
            return this;
        }

        public Builder dontGenerateRecipe() {
            this.family.generateRecipe = false;
            return this;
        }

        public Builder featureLockedBehind(FeatureFlag ... $$0) {
            this.family.requiredFeatures = FeatureFlags.REGISTRY.subset($$0);
            return this;
        }

        public Builder recipeGroupPrefix(String $$0) {
            this.family.recipeGroupPrefix = $$0;
            return this;
        }

        public Builder recipeUnlockedBy(String $$0) {
            this.family.recipeUnlockedBy = $$0;
            return this;
        }
    }

    public static enum Variant {
        BUTTON("button"),
        CHISELED("chiseled"),
        CRACKED("cracked"),
        CUT("cut"),
        DOOR("door"),
        CUSTOM_FENCE("custom_fence"),
        FENCE("fence"),
        CUSTOM_FENCE_GATE("custom_fence_gate"),
        FENCE_GATE("fence_gate"),
        MOSAIC("mosaic"),
        SIGN("sign"),
        SLAB("slab"),
        STAIRS("stairs"),
        PRESSURE_PLATE("pressure_plate"),
        POLISHED("polished"),
        TRAPDOOR("trapdoor"),
        WALL("wall"),
        WALL_SIGN("wall_sign");

        private final String name;

        private Variant(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }
    }
}