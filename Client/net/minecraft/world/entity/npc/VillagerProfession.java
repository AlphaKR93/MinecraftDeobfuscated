/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record VillagerProfession(String name, Predicate<Holder<PoiType>> heldJobSite, Predicate<Holder<PoiType>> acquirableJobSite, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound) {
    public static final Predicate<Holder<PoiType>> ALL_ACQUIRABLE_JOBS = $$0 -> $$0.is(PoiTypeTags.ACQUIRABLE_JOB_SITE);
    public static final VillagerProfession NONE = VillagerProfession.register("none", PoiType.NONE, ALL_ACQUIRABLE_JOBS, null);
    public static final VillagerProfession ARMORER = VillagerProfession.register("armorer", PoiTypes.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
    public static final VillagerProfession BUTCHER = VillagerProfession.register("butcher", PoiTypes.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = VillagerProfession.register("cartographer", PoiTypes.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession CLERIC = VillagerProfession.register("cleric", PoiTypes.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
    public static final VillagerProfession FARMER = VillagerProfession.register("farmer", PoiTypes.FARMER, (ImmutableSet<Item>)ImmutableSet.of((Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS, (Object)Items.BONE_MEAL), (ImmutableSet<Block>)ImmutableSet.of((Object)Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER);
    public static final VillagerProfession FISHERMAN = VillagerProfession.register("fisherman", PoiTypes.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
    public static final VillagerProfession FLETCHER = VillagerProfession.register("fletcher", PoiTypes.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
    public static final VillagerProfession LEATHERWORKER = VillagerProfession.register("leatherworker", PoiTypes.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = VillagerProfession.register("librarian", PoiTypes.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
    public static final VillagerProfession MASON = VillagerProfession.register("mason", PoiTypes.MASON, SoundEvents.VILLAGER_WORK_MASON);
    public static final VillagerProfession NITWIT = VillagerProfession.register("nitwit", PoiType.NONE, PoiType.NONE, null);
    public static final VillagerProfession SHEPHERD = VillagerProfession.register("shepherd", PoiTypes.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
    public static final VillagerProfession TOOLSMITH = VillagerProfession.register("toolsmith", PoiTypes.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = VillagerProfession.register("weaponsmith", PoiTypes.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);

    public String toString() {
        return this.name;
    }

    private static VillagerProfession register(String $$0, ResourceKey<PoiType> $$12, @Nullable SoundEvent $$2) {
        return VillagerProfession.register($$0, (Predicate<Holder<PoiType>>)((Predicate)$$1 -> $$1.is($$12)), (Predicate<Holder<PoiType>>)((Predicate)$$1 -> $$1.is($$12)), $$2);
    }

    private static VillagerProfession register(String $$0, Predicate<Holder<PoiType>> $$1, Predicate<Holder<PoiType>> $$2, @Nullable SoundEvent $$3) {
        return VillagerProfession.register($$0, $$1, $$2, (ImmutableSet<Item>)ImmutableSet.of(), (ImmutableSet<Block>)ImmutableSet.of(), $$3);
    }

    private static VillagerProfession register(String $$0, ResourceKey<PoiType> $$12, ImmutableSet<Item> $$2, ImmutableSet<Block> $$3, @Nullable SoundEvent $$4) {
        return VillagerProfession.register($$0, (Predicate<Holder<PoiType>>)((Predicate)$$1 -> $$1.is($$12)), (Predicate<Holder<PoiType>>)((Predicate)$$1 -> $$1.is($$12)), $$2, $$3, $$4);
    }

    private static VillagerProfession register(String $$0, Predicate<Holder<PoiType>> $$1, Predicate<Holder<PoiType>> $$2, ImmutableSet<Item> $$3, ImmutableSet<Block> $$4, @Nullable SoundEvent $$5) {
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, new ResourceLocation($$0), new VillagerProfession($$0, $$1, $$2, $$3, $$4, $$5));
    }
}