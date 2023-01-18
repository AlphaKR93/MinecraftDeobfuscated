/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnderChestBlockEntity
extends BlockEntity
implements LidBlockEntity {
    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter(){

        @Override
        protected void onOpen(Level $$0, BlockPos $$1, BlockState $$2) {
            $$0.playSound(null, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5f, $$0.random.nextFloat() * 0.1f + 0.9f);
        }

        @Override
        protected void onClose(Level $$0, BlockPos $$1, BlockState $$2) {
            $$0.playSound(null, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5f, $$0.random.nextFloat() * 0.1f + 0.9f);
        }

        @Override
        protected void openerCountChanged(Level $$0, BlockPos $$1, BlockState $$2, int $$3, int $$4) {
            $$0.blockEvent(EnderChestBlockEntity.this.worldPosition, Blocks.ENDER_CHEST, 1, $$4);
        }

        @Override
        protected boolean isOwnContainer(Player $$0) {
            return $$0.getEnderChestInventory().isActiveChest(EnderChestBlockEntity.this);
        }
    };

    public EnderChestBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.ENDER_CHEST, $$0, $$1);
    }

    public static void lidAnimateTick(Level $$0, BlockPos $$1, BlockState $$2, EnderChestBlockEntity $$3) {
        $$3.chestLidController.tickLid();
    }

    @Override
    public boolean triggerEvent(int $$0, int $$1) {
        if ($$0 == 1) {
            this.chestLidController.shouldBeOpen($$1 > 0);
            return true;
        }
        return super.triggerEvent($$0, $$1);
    }

    public void startOpen(Player $$0) {
        if (!this.remove && !$$0.isSpectator()) {
            this.openersCounter.incrementOpeners($$0, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void stopOpen(Player $$0) {
        if (!this.remove && !$$0.isSpectator()) {
            this.openersCounter.decrementOpeners($$0, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public boolean stillValid(Player $$0) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return !($$0.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public float getOpenNess(float $$0) {
        return this.chestLidController.getOpenness($$0);
    }
}