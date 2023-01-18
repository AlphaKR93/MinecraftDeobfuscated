/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.function.BiConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import java.util.Iterator;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public interface ContainerEntity
extends Container,
MenuProvider {
    public Vec3 position();

    @Nullable
    public ResourceLocation getLootTable();

    public void setLootTable(@Nullable ResourceLocation var1);

    public long getLootTableSeed();

    public void setLootTableSeed(long var1);

    public NonNullList<ItemStack> getItemStacks();

    public void clearItemStacks();

    public Level getLevel();

    public boolean isRemoved();

    @Override
    default public boolean isEmpty() {
        return this.isChestVehicleEmpty();
    }

    default public void addChestVehicleSaveData(CompoundTag $$0) {
        if (this.getLootTable() != null) {
            $$0.putString("LootTable", this.getLootTable().toString());
            if (this.getLootTableSeed() != 0L) {
                $$0.putLong("LootTableSeed", this.getLootTableSeed());
            }
        } else {
            ContainerHelper.saveAllItems($$0, this.getItemStacks());
        }
    }

    default public void readChestVehicleSaveData(CompoundTag $$0) {
        this.clearItemStacks();
        if ($$0.contains("LootTable", 8)) {
            this.setLootTable(new ResourceLocation($$0.getString("LootTable")));
            this.setLootTableSeed($$0.getLong("LootTableSeed"));
        } else {
            ContainerHelper.loadAllItems($$0, this.getItemStacks());
        }
    }

    default public void chestVehicleDestroyed(DamageSource $$0, Level $$1, Entity $$2) {
        Entity $$3;
        if (!$$1.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        Containers.dropContents($$1, $$2, (Container)this);
        if (!$$1.isClientSide && ($$3 = $$0.getDirectEntity()) != null && $$3.getType() == EntityType.PLAYER) {
            PiglinAi.angerNearbyPiglins((Player)$$3, true);
        }
    }

    default public InteractionResult interactWithChestVehicle(BiConsumer<GameEvent, Entity> $$0, Player $$1) {
        $$1.openMenu(this);
        if (!$$1.level.isClientSide) {
            $$0.accept((Object)GameEvent.CONTAINER_OPEN, (Object)$$1);
            PiglinAi.angerNearbyPiglins($$1, true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    default public void unpackChestVehicleLootTable(@Nullable Player $$0) {
        MinecraftServer $$1 = this.getLevel().getServer();
        if (this.getLootTable() != null && $$1 != null) {
            LootTable $$2 = $$1.getLootTables().get(this.getLootTable());
            if ($$0 != null) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)$$0, this.getLootTable());
            }
            this.setLootTable(null);
            LootContext.Builder $$3 = new LootContext.Builder((ServerLevel)this.getLevel()).withParameter(LootContextParams.ORIGIN, this.position()).withOptionalRandomSeed(this.getLootTableSeed());
            if ($$0 != null) {
                $$3.withLuck($$0.getLuck()).withParameter(LootContextParams.THIS_ENTITY, $$0);
            }
            $$2.fill(this, $$3.create(LootContextParamSets.CHEST));
        }
    }

    default public void clearChestVehicleContent() {
        this.unpackChestVehicleLootTable(null);
        this.getItemStacks().clear();
    }

    default public boolean isChestVehicleEmpty() {
        Iterator iterator = this.getItemStacks().iterator();
        while (iterator.hasNext()) {
            ItemStack $$0 = (ItemStack)iterator.next();
            if ($$0.isEmpty()) continue;
            return false;
        }
        return true;
    }

    default public ItemStack removeChestVehicleItemNoUpdate(int $$0) {
        this.unpackChestVehicleLootTable(null);
        ItemStack $$1 = this.getItemStacks().get($$0);
        if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.getItemStacks().set($$0, ItemStack.EMPTY);
        return $$1;
    }

    default public ItemStack getChestVehicleItem(int $$0) {
        this.unpackChestVehicleLootTable(null);
        return this.getItemStacks().get($$0);
    }

    default public ItemStack removeChestVehicleItem(int $$0, int $$1) {
        this.unpackChestVehicleLootTable(null);
        return ContainerHelper.removeItem(this.getItemStacks(), $$0, $$1);
    }

    default public void setChestVehicleItem(int $$0, ItemStack $$1) {
        this.unpackChestVehicleLootTable(null);
        this.getItemStacks().set($$0, $$1);
        if (!$$1.isEmpty() && $$1.getCount() > this.getMaxStackSize()) {
            $$1.setCount(this.getMaxStackSize());
        }
    }

    default public SlotAccess getChestVehicleSlot(final int $$0) {
        if ($$0 >= 0 && $$0 < this.getContainerSize()) {
            return new SlotAccess(){

                @Override
                public ItemStack get() {
                    return ContainerEntity.this.getChestVehicleItem($$0);
                }

                @Override
                public boolean set(ItemStack $$02) {
                    ContainerEntity.this.setChestVehicleItem($$0, $$02);
                    return true;
                }
            };
        }
        return SlotAccess.NULL;
    }

    default public boolean isChestVehicleStillValid(Player $$0) {
        return !this.isRemoved() && this.position().closerThan($$0.position(), 8.0);
    }
}