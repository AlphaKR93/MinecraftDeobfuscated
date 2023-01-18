/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.entity.vehicle;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartHopper
extends AbstractMinecartContainer
implements Hopper {
    private boolean enabled = true;

    public MinecartHopper(EntityType<? extends MinecartHopper> $$0, Level $$1) {
        super($$0, $$1);
    }

    public MinecartHopper(Level $$0, double $$1, double $$2, double $$3) {
        super(EntityType.HOPPER_MINECART, $$1, $$2, $$3, $$0);
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.HOPPER;
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.HOPPER.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 1;
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public void activateMinecart(int $$0, int $$1, int $$2, boolean $$3) {
        boolean $$4;
        boolean bl = $$4 = !$$3;
        if ($$4 != this.isEnabled()) {
            this.setEnabled($$4);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean $$0) {
        this.enabled = $$0;
    }

    @Override
    public double getLevelX() {
        return this.getX();
    }

    @Override
    public double getLevelY() {
        return this.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return this.getZ();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.isAlive() && this.isEnabled() && this.suckInItems()) {
            this.setChanged();
        }
    }

    public boolean suckInItems() {
        if (HopperBlockEntity.suckInItems(this.level, this)) {
            return true;
        }
        List $$0 = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25, 0.0, 0.25), EntitySelector.ENTITY_STILL_ALIVE);
        for (ItemEntity $$1 : $$0) {
            if (!HopperBlockEntity.addItem(this, $$1)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected Item getDropItem() {
        return Items.HOPPER_MINECART;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("Enabled", this.enabled);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.enabled = $$0.contains("Enabled") ? $$0.getBoolean("Enabled") : true;
    }

    @Override
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1) {
        return new HopperMenu($$0, $$1, this);
    }
}