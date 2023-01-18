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
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ChestBoat
extends Boat
implements HasCustomInventoryScreen,
ContainerEntity {
    private static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public ChestBoat(EntityType<? extends Boat> $$0, Level $$1) {
        super($$0, $$1);
    }

    public ChestBoat(Level $$0, double $$1, double $$2, double $$3) {
        this((EntityType<? extends Boat>)EntityType.CHEST_BOAT, $$0);
        this.setPos($$1, $$2, $$3);
        this.xo = $$1;
        this.yo = $$2;
        this.zo = $$3;
    }

    @Override
    protected float getSinglePassengerXOffset() {
        return 0.15f;
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
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
    public void destroy(DamageSource $$0) {
        super.destroy($$0);
        this.chestVehicleDestroyed($$0, this.level, this);
    }

    @Override
    public void remove(Entity.RemovalReason $$0) {
        if (!this.level.isClientSide && $$0.shouldDestroy()) {
            Containers.dropContents(this.level, this, (Container)this);
        }
        super.remove($$0);
    }

    @Override
    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        if (!this.canAddPassenger($$0) || $$0.isSecondaryUseActive()) {
            return this.interactWithChestVehicle(this::gameEvent, $$0);
        }
        return super.interact($$0, $$1);
    }

    @Override
    public void openCustomInventoryScreen(Player $$0) {
        $$0.openMenu(this);
        if (!$$0.level.isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, $$0);
            PiglinAi.angerNearbyPiglins($$0, true);
        }
    }

    @Override
    public Item getDropItem() {
        return switch (this.getVariant()) {
            case Boat.Type.SPRUCE -> Items.SPRUCE_CHEST_BOAT;
            case Boat.Type.BIRCH -> Items.BIRCH_CHEST_BOAT;
            case Boat.Type.JUNGLE -> Items.JUNGLE_CHEST_BOAT;
            case Boat.Type.ACACIA -> Items.ACACIA_CHEST_BOAT;
            case Boat.Type.DARK_OAK -> Items.DARK_OAK_CHEST_BOAT;
            case Boat.Type.MANGROVE -> Items.MANGROVE_CHEST_BOAT;
            case Boat.Type.BAMBOO -> Items.BAMBOO_CHEST_RAFT;
            default -> Items.OAK_CHEST_BOAT;
        };
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    @Override
    public int getContainerSize() {
        return 27;
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
    @Nullable
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        if (this.lootTable == null || !$$2.isSpectator()) {
            this.unpackLootTable($$1.player);
            return ChestMenu.threeRows($$0, $$1, this);
        }
        return null;
    }

    public void unpackLootTable(@Nullable Player $$0) {
        this.unpackChestVehicleLootTable($$0);
    }

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