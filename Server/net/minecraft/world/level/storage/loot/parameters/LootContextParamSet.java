/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class LootContextParamSet {
    private final Set<LootContextParam<?>> required;
    private final Set<LootContextParam<?>> all;

    LootContextParamSet(Set<LootContextParam<?>> $$0, Set<LootContextParam<?>> $$1) {
        this.required = ImmutableSet.copyOf($$0);
        this.all = ImmutableSet.copyOf((Collection)Sets.union($$0, $$1));
    }

    public boolean isAllowed(LootContextParam<?> $$0) {
        return this.all.contains($$0);
    }

    public Set<LootContextParam<?>> getRequired() {
        return this.required;
    }

    public Set<LootContextParam<?>> getAllowed() {
        return this.all;
    }

    public String toString() {
        return "[" + Joiner.on((String)", ").join(this.all.stream().map($$0 -> (this.required.contains($$0) ? "!" : "") + $$0.getName()).iterator()) + "]";
    }

    public void validateUser(ValidationContext $$0, LootContextUser $$1) {
        Set<LootContextParam<?>> $$2 = $$1.getReferencedContextParams();
        Sets.SetView $$3 = Sets.difference($$2, this.all);
        if (!$$3.isEmpty()) {
            $$0.reportProblem("Parameters " + (Set)$$3 + " are not provided in this context");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<LootContextParam<?>> required = Sets.newIdentityHashSet();
        private final Set<LootContextParam<?>> optional = Sets.newIdentityHashSet();

        public Builder required(LootContextParam<?> $$0) {
            if (this.optional.contains($$0)) {
                throw new IllegalArgumentException("Parameter " + $$0.getName() + " is already optional");
            }
            this.required.add($$0);
            return this;
        }

        public Builder optional(LootContextParam<?> $$0) {
            if (this.required.contains($$0)) {
                throw new IllegalArgumentException("Parameter " + $$0.getName() + " is already required");
            }
            this.optional.add($$0);
            return this;
        }

        public LootContextParamSet build() {
            return new LootContextParamSet(this.required, this.optional);
        }
    }
}