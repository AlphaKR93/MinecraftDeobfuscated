/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public abstract class RandomizableContainerBlockEntity
extends BaseContainerBlockEntity {
    public static final String LOOT_TABLE_TAG = "LootTable";
    public static final String LOOT_TABLE_SEED_TAG = "LootTableSeed";
    @Nullable
    protected ResourceLocation lootTable;
    protected long lootTableSeed;

    protected RandomizableContainerBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    public static void setLootTable(BlockGetter $$0, RandomSource $$1, BlockPos $$2, ResourceLocation $$3) {
        BlockEntity $$4 = $$0.getBlockEntity($$2);
        if ($$4 instanceof RandomizableContainerBlockEntity) {
            ((RandomizableContainerBlockEntity)$$4).setLootTable($$3, $$1.nextLong());
        }
    }

    protected boolean tryLoadLootTable(CompoundTag $$0) {
        if ($$0.contains(LOOT_TABLE_TAG, 8)) {
            this.lootTable = new ResourceLocation($$0.getString(LOOT_TABLE_TAG));
            this.lootTableSeed = $$0.getLong(LOOT_TABLE_SEED_TAG);
            return true;
        }
        return false;
    }

    protected boolean trySaveLootTable(CompoundTag $$0) {
        if (this.lootTable == null) {
            return false;
        }
        $$0.putString(LOOT_TABLE_TAG, this.lootTable.toString());
        if (this.lootTableSeed != 0L) {
            $$0.putLong(LOOT_TABLE_SEED_TAG, this.lootTableSeed);
        }
        return true;
    }

    public void unpackLootTable(@Nullable Player $$0) {
        if (this.lootTable != null && this.level.getServer() != null) {
            LootTable $$1 = this.level.getServer().getLootTables().get(this.lootTable);
            if ($$0 instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)$$0, this.lootTable);
            }
            this.lootTable = null;
            LootContext.Builder $$2 = new LootContext.Builder((ServerLevel)this.level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(this.lootTableSeed);
            if ($$0 != null) {
                $$2.withLuck($$0.getLuck()).withParameter(LootContextParams.THIS_ENTITY, $$0);
            }
            $$1.fill(this, $$2.create(LootContextParamSets.CHEST));
        }
    }

    public void setLootTable(ResourceLocation $$0, long $$1) {
        this.lootTable = $$0;
        this.lootTableSeed = $$1;
    }

    @Override
    public boolean isEmpty() {
        this.unpackLootTable(null);
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int $$0) {
        this.unpackLootTable(null);
        return this.getItems().get($$0);
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        this.unpackLootTable(null);
        ItemStack $$2 = ContainerHelper.removeItem(this.getItems(), $$0, $$1);
        if (!$$2.isEmpty()) {
            this.setChanged();
        }
        return $$2;
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        this.unpackLootTable(null);
        return ContainerHelper.takeItem(this.getItems(), $$0);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        this.unpackLootTable(null);
        this.getItems().set($$0, $$1);
        if ($$1.getCount() > this.getMaxStackSize()) {
            $$1.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player $$0) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return !($$0.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }

    protected abstract NonNullList<ItemStack> getItems();

    protected abstract void setItems(NonNullList<ItemStack> var1);

    @Override
    public boolean canOpen(Player $$0) {
        return super.canOpen($$0) && (this.lootTable == null || !$$0.isSpectator());
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        if (this.canOpen($$2)) {
            this.unpackLootTable($$1.player);
            return this.createMenu($$0, $$1);
        }
        return null;
    }
}