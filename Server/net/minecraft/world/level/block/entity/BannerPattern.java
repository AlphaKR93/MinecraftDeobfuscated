/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class BannerPattern {
    final String hashname;

    public BannerPattern(String $$0) {
        this.hashname = $$0;
    }

    public static ResourceLocation location(ResourceKey<BannerPattern> $$0, boolean $$1) {
        String $$2 = $$1 ? "banner" : "shield";
        return $$0.location().withPrefix("entity/" + $$2 + "/");
    }

    public String getHashname() {
        return this.hashname;
    }

    @Nullable
    public static Holder<BannerPattern> byHash(String $$0) {
        return (Holder)BuiltInRegistries.BANNER_PATTERN.holders().filter($$1 -> ((BannerPattern)$$1.value()).hashname.equals((Object)$$0)).findAny().orElse(null);
    }

    public static class Builder {
        private final List<Pair<Holder<BannerPattern>, DyeColor>> patterns = Lists.newArrayList();

        public Builder addPattern(ResourceKey<BannerPattern> $$0, DyeColor $$1) {
            return this.addPattern(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow($$0), $$1);
        }

        public Builder addPattern(Holder<BannerPattern> $$0, DyeColor $$1) {
            return this.addPattern((Pair<Holder<BannerPattern>, DyeColor>)Pair.of($$0, (Object)$$1));
        }

        public Builder addPattern(Pair<Holder<BannerPattern>, DyeColor> $$0) {
            this.patterns.add($$0);
            return this;
        }

        public ListTag toListTag() {
            ListTag $$0 = new ListTag();
            for (Pair $$1 : this.patterns) {
                CompoundTag $$2 = new CompoundTag();
                $$2.putString("Pattern", ((BannerPattern)((Holder)$$1.getFirst()).value()).hashname);
                $$2.putInt("Color", ((DyeColor)$$1.getSecond()).getId());
                $$0.add($$2);
            }
            return $$0;
        }
    }
}