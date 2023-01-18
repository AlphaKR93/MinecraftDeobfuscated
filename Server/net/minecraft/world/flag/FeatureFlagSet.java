/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.HashCommon
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.Arrays
 *  java.util.Collection
 *  javax.annotation.Nullable
 */
package net.minecraft.world.flag;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagUniverse;

public final class FeatureFlagSet {
    private static final FeatureFlagSet EMPTY = new FeatureFlagSet(null, 0L);
    public static final int MAX_CONTAINER_SIZE = 64;
    @Nullable
    private final FeatureFlagUniverse universe;
    private final long mask;

    private FeatureFlagSet(@Nullable FeatureFlagUniverse $$0, long $$1) {
        this.universe = $$0;
        this.mask = $$1;
    }

    static FeatureFlagSet create(FeatureFlagUniverse $$0, Collection<FeatureFlag> $$1) {
        if ($$1.isEmpty()) {
            return EMPTY;
        }
        long $$2 = FeatureFlagSet.computeMask($$0, 0L, $$1);
        return new FeatureFlagSet($$0, $$2);
    }

    public static FeatureFlagSet of() {
        return EMPTY;
    }

    public static FeatureFlagSet of(FeatureFlag $$0) {
        return new FeatureFlagSet($$0.universe, $$0.mask);
    }

    public static FeatureFlagSet of(FeatureFlag $$0, FeatureFlag ... $$1) {
        long $$2 = $$1.length == 0 ? $$0.mask : FeatureFlagSet.computeMask($$0.universe, $$0.mask, (Iterable<FeatureFlag>)Arrays.asList((Object[])$$1));
        return new FeatureFlagSet($$0.universe, $$2);
    }

    private static long computeMask(FeatureFlagUniverse $$0, long $$1, Iterable<FeatureFlag> $$2) {
        for (FeatureFlag $$3 : $$2) {
            if ($$0 != $$3.universe) {
                throw new IllegalStateException("Mismatched feature universe, expected '" + $$0 + "', but got '" + $$3.universe + "'");
            }
            $$1 |= $$3.mask;
        }
        return $$1;
    }

    public boolean contains(FeatureFlag $$0) {
        if (this.universe != $$0.universe) {
            return false;
        }
        return (this.mask & $$0.mask) != 0L;
    }

    public boolean isSubsetOf(FeatureFlagSet $$0) {
        if (this.universe == null) {
            return true;
        }
        if (this.universe != $$0.universe) {
            return false;
        }
        return (this.mask & ($$0.mask ^ 0xFFFFFFFFFFFFFFFFL)) == 0L;
    }

    public FeatureFlagSet join(FeatureFlagSet $$0) {
        if (this.universe == null) {
            return $$0;
        }
        if ($$0.universe == null) {
            return this;
        }
        if (this.universe != $$0.universe) {
            throw new IllegalArgumentException("Mismatched set elements: '" + this.universe + "' != '" + $$0.universe + "'");
        }
        return new FeatureFlagSet(this.universe, this.mask | $$0.mask);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof FeatureFlagSet)) return false;
        FeatureFlagSet $$1 = (FeatureFlagSet)$$0;
        if (this.universe != $$1.universe) return false;
        if (this.mask != $$1.mask) return false;
        return true;
    }

    public int hashCode() {
        return (int)HashCommon.mix((long)this.mask);
    }
}