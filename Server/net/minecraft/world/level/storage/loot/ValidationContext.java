/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Multimap
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ValidationContext {
    private final Multimap<String, String> problems;
    private final Supplier<String> context;
    private final LootContextParamSet params;
    private final Function<ResourceLocation, LootItemCondition> conditionResolver;
    private final Set<ResourceLocation> visitedConditions;
    private final Function<ResourceLocation, LootTable> tableResolver;
    private final Set<ResourceLocation> visitedTables;
    private String contextCache;

    public ValidationContext(LootContextParamSet $$0, Function<ResourceLocation, LootItemCondition> $$1, Function<ResourceLocation, LootTable> $$2) {
        this((Multimap<String, String>)HashMultimap.create(), (Supplier<String>)((Supplier)() -> ""), $$0, $$1, (Set<ResourceLocation>)ImmutableSet.of(), $$2, (Set<ResourceLocation>)ImmutableSet.of());
    }

    public ValidationContext(Multimap<String, String> $$0, Supplier<String> $$1, LootContextParamSet $$2, Function<ResourceLocation, LootItemCondition> $$3, Set<ResourceLocation> $$4, Function<ResourceLocation, LootTable> $$5, Set<ResourceLocation> $$6) {
        this.problems = $$0;
        this.context = $$1;
        this.params = $$2;
        this.conditionResolver = $$3;
        this.visitedConditions = $$4;
        this.tableResolver = $$5;
        this.visitedTables = $$6;
    }

    private String getContext() {
        if (this.contextCache == null) {
            this.contextCache = (String)this.context.get();
        }
        return this.contextCache;
    }

    public void reportProblem(String $$0) {
        this.problems.put((Object)this.getContext(), (Object)$$0);
    }

    public ValidationContext forChild(String $$0) {
        return new ValidationContext(this.problems, (Supplier<String>)((Supplier)() -> this.getContext() + $$0), this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
    }

    public ValidationContext enterTable(String $$0, ResourceLocation $$1) {
        ImmutableSet $$2 = ImmutableSet.builder().addAll(this.visitedTables).add((Object)$$1).build();
        return new ValidationContext(this.problems, (Supplier<String>)((Supplier)() -> this.getContext() + $$0), this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, (Set<ResourceLocation>)$$2);
    }

    public ValidationContext enterCondition(String $$0, ResourceLocation $$1) {
        ImmutableSet $$2 = ImmutableSet.builder().addAll(this.visitedConditions).add((Object)$$1).build();
        return new ValidationContext(this.problems, (Supplier<String>)((Supplier)() -> this.getContext() + $$0), this.params, this.conditionResolver, (Set<ResourceLocation>)$$2, this.tableResolver, this.visitedTables);
    }

    public boolean hasVisitedTable(ResourceLocation $$0) {
        return this.visitedTables.contains((Object)$$0);
    }

    public boolean hasVisitedCondition(ResourceLocation $$0) {
        return this.visitedConditions.contains((Object)$$0);
    }

    public Multimap<String, String> getProblems() {
        return ImmutableMultimap.copyOf(this.problems);
    }

    public void validateUser(LootContextUser $$0) {
        this.params.validateUser(this, $$0);
    }

    @Nullable
    public LootTable resolveLootTable(ResourceLocation $$0) {
        return (LootTable)this.tableResolver.apply((Object)$$0);
    }

    @Nullable
    public LootItemCondition resolveCondition(ResourceLocation $$0) {
        return (LootItemCondition)this.conditionResolver.apply((Object)$$0);
    }

    public ValidationContext setParams(LootContextParamSet $$0) {
        return new ValidationContext(this.problems, this.context, $$0, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
    }
}