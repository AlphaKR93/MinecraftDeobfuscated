/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.LinkedHashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.Consumer
 *  org.slf4j.Logger
 */
package net.minecraft.world.flag;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlagUniverse;
import org.slf4j.Logger;

public class FeatureFlagRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final FeatureFlagUniverse universe;
    private final Map<ResourceLocation, FeatureFlag> names;
    private final FeatureFlagSet allFlags;

    FeatureFlagRegistry(FeatureFlagUniverse $$0, FeatureFlagSet $$1, Map<ResourceLocation, FeatureFlag> $$2) {
        this.universe = $$0;
        this.names = $$2;
        this.allFlags = $$1;
    }

    public boolean isSubset(FeatureFlagSet $$0) {
        return $$0.isSubsetOf(this.allFlags);
    }

    public FeatureFlagSet allFlags() {
        return this.allFlags;
    }

    public FeatureFlagSet fromNames(Iterable<ResourceLocation> $$02) {
        return this.fromNames($$02, (Consumer<ResourceLocation>)((Consumer)$$0 -> LOGGER.warn("Unknown feature flag: {}", $$0)));
    }

    public FeatureFlagSet subset(FeatureFlag ... $$0) {
        return FeatureFlagSet.create(this.universe, (Collection<FeatureFlag>)Arrays.asList((Object[])$$0));
    }

    public FeatureFlagSet fromNames(Iterable<ResourceLocation> $$0, Consumer<ResourceLocation> $$1) {
        Set $$2 = Sets.newIdentityHashSet();
        for (ResourceLocation $$3 : $$0) {
            FeatureFlag $$4 = (FeatureFlag)this.names.get((Object)$$3);
            if ($$4 == null) {
                $$1.accept((Object)$$3);
                continue;
            }
            $$2.add((Object)$$4);
        }
        return FeatureFlagSet.create(this.universe, (Collection<FeatureFlag>)$$2);
    }

    public Set<ResourceLocation> toNames(FeatureFlagSet $$0) {
        HashSet $$1 = new HashSet();
        this.names.forEach((arg_0, arg_1) -> FeatureFlagRegistry.lambda$toNames$1($$0, (Set)$$1, arg_0, arg_1));
        return $$1;
    }

    public Codec<FeatureFlagSet> codec() {
        return ResourceLocation.CODEC.listOf().comapFlatMap($$0 -> {
            HashSet $$1 = new HashSet();
            FeatureFlagSet $$2 = this.fromNames((Iterable<ResourceLocation>)$$0, (Consumer<ResourceLocation>)((Consumer)arg_0 -> ((Set)$$1).add(arg_0)));
            if (!$$1.isEmpty()) {
                return DataResult.error((String)("Unknown feature ids: " + (Set)$$1), (Object)$$2);
            }
            return DataResult.success((Object)$$2);
        }, $$0 -> List.copyOf(this.toNames((FeatureFlagSet)$$0)));
    }

    private static /* synthetic */ void lambda$toNames$1(FeatureFlagSet $$0, Set $$1, ResourceLocation $$2, FeatureFlag $$3) {
        if ($$0.contains($$3)) {
            $$1.add((Object)$$2);
        }
    }

    public static class Builder {
        private final FeatureFlagUniverse universe;
        private int id;
        private final Map<ResourceLocation, FeatureFlag> flags = new LinkedHashMap();

        public Builder(String $$0) {
            this.universe = new FeatureFlagUniverse($$0);
        }

        public FeatureFlag createVanilla(String $$0) {
            return this.create(new ResourceLocation("minecraft", $$0));
        }

        public FeatureFlag create(ResourceLocation $$0) {
            FeatureFlag $$1;
            FeatureFlag $$2;
            if (this.id >= 64) {
                throw new IllegalStateException("Too many feature flags");
            }
            if (($$2 = (FeatureFlag)this.flags.put((Object)$$0, (Object)($$1 = new FeatureFlag(this.universe, this.id++)))) != null) {
                throw new IllegalStateException("Duplicate feature flag " + $$0);
            }
            return $$1;
        }

        public FeatureFlagRegistry build() {
            FeatureFlagSet $$0 = FeatureFlagSet.create(this.universe, (Collection<FeatureFlag>)this.flags.values());
            return new FeatureFlagRegistry(this.universe, $$0, (Map<ResourceLocation, FeatureFlag>)Map.copyOf(this.flags));
        }
    }
}