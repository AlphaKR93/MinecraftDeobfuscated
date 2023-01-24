/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  java.lang.CharSequence
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.BiConsumer
 *  java.util.stream.Collectors
 */
package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class EntityLootSubProvider
implements LootTableSubProvider {
    protected static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build());
    private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
    private final FeatureFlagSet allowed;
    private final FeatureFlagSet required;
    private final Map<EntityType<?>, Map<ResourceLocation, LootTable.Builder>> map = Maps.newHashMap();

    protected EntityLootSubProvider(FeatureFlagSet $$0) {
        this($$0, $$0);
    }

    protected EntityLootSubProvider(FeatureFlagSet $$0, FeatureFlagSet $$1) {
        this.allowed = $$0;
        this.required = $$1;
    }

    protected static LootTable.Builder createSheepTable(ItemLike $$0) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(LootItem.lootTableItem($$0))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(LootTableReference.lootTableReference(EntityType.SHEEP.getDefaultLootTable())));
    }

    public abstract void generate();

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> $$0) {
        this.generate();
        HashSet $$1 = Sets.newHashSet();
        BuiltInRegistries.ENTITY_TYPE.holders().forEach(arg_0 -> this.lambda$generate$1((Set)$$1, $$0, arg_0));
        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
        }
    }

    private static boolean canHaveLootTable(EntityType<?> $$0) {
        return SPECIAL_LOOT_TABLE_TYPES.contains($$0) || $$0.getCategory() != MobCategory.MISC;
    }

    protected LootItemCondition.Builder killedByFrog() {
        return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
    }

    protected LootItemCondition.Builder killedByFrogVariant(FrogVariant $$0) {
        return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG).subPredicate(EntitySubPredicate.variant($$0))));
    }

    protected void add(EntityType<?> $$0, LootTable.Builder $$1) {
        this.add($$0, $$0.getDefaultLootTable(), $$1);
    }

    protected void add(EntityType<?> $$02, ResourceLocation $$1, LootTable.Builder $$2) {
        ((Map)this.map.computeIfAbsent($$02, $$0 -> new HashMap())).put((Object)$$1, (Object)$$2);
    }

    private /* synthetic */ void lambda$generate$1(Set $$0, BiConsumer $$1, Holder.Reference $$2) {
        EntityType $$32 = (EntityType)$$2.value();
        if (!$$32.isEnabled(this.allowed)) {
            return;
        }
        if (EntityLootSubProvider.canHaveLootTable($$32)) {
            Map $$42 = (Map)this.map.remove((Object)$$32);
            ResourceLocation $$5 = $$32.getDefaultLootTable();
            if (!($$5.equals(BuiltInLootTables.EMPTY) || !$$32.isEnabled(this.required) || $$42 != null && $$42.containsKey((Object)$$5))) {
                throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"Missing loottable '%s' for '%s'", (Object[])new Object[]{$$5, $$2.key().location()}));
            }
            if ($$42 != null) {
                $$42.forEach(($$3, $$4) -> {
                    if (!$$0.add($$3)) {
                        throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"Duplicate loottable '%s' for '%s'", (Object[])new Object[]{$$3, $$2.key().location()}));
                    }
                    $$1.accept($$3, $$4);
                });
            }
        } else {
            Map $$6 = (Map)this.map.remove((Object)$$32);
            if ($$6 != null) {
                throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", (Object[])new Object[]{$$6.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining((CharSequence)",")), $$2.key().location()}));
            }
        }
    }
}