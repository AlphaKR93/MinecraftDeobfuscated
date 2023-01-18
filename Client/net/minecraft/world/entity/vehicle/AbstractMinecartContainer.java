/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractMinecartContainer
extends AbstractMinecart
implements ContainerEntity {
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    protected AbstractMinecartContainer(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }

    protected AbstractMinecartContainer(EntityType<?> $$0, double $$1, double $$2, double $$3, Level $$4) {
        super($$0, $$4, $$1, $$2, $$3);
    }

    @Override
    public void destroy(DamageSource $$0) {
        super.destroy($$0);
        this.chestVehicleDestroyed($$0, this.level, this);
    }

    @Override
    public ItemStack getItem(int $$0) {
        return this.getChestVehicleItem($$0);
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        return this.removeChestVehicleItem($$0, $$1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        return this.removeChestVehicleItemNoUpdate($$0);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        this.setChestVehicleItem($$0, $$1);
    }

    @Override
    public SlotAccess getSlot(int $$0) {
        return this.getChestVehicleSlot($$0);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.isChestVehicleStillValid($$0);
    }

    @Override
    public void remove(Entity.RemovalReason $$0) {
        if (!this.level.isClientSide && $$0.shouldDestroy()) {
            Containers.dropContents(this.level, this, (Container)this);
        }
        super.remove($$0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        this.addChestVehicleSaveData($$0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.readChestVehicleSaveData($$0);
    }

    @Override
    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        return this.interactWithChestVehicle(this::gameEvent, $$0);
    }

    @Override
    protected void applyNaturalSlowdown() {
        float $$0 = 0.98f;
        if (this.lootTable == null) {
            int $$1 = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
            $$0 += (float)$$1 * 0.001f;
        }
        if (this.isInWater()) {
            $$0 *= 0.95f;
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply($$0, 0.0, $$0));
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    public void setLootTable(ResourceLocation $$0, long $$1) {
        this.lootTable = $$0;
        this.lootTableSeed = $$1;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        if (this.lootTable == null || !$$2.isSpectator()) {
            this.unpackChestVehicleLootTable($$1.player);
            return this.createMenu($$0, $$1);
        }
        return null;
    }

    protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);

    @Override
    @Nullable
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceLocation $$0) {
        this.lootTable = $$0;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long $$0) {
        this.lootTableSeed = $$0;
    }

    @Override
    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    @Override
    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }
}