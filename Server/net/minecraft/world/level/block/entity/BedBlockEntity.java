/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BedBlockEntity
extends BlockEntity {
    private DyeColor color;

    public BedBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BED, $$0, $$1);
        this.color = ((BedBlock)$$1.getBlock()).getColor();
    }

    public BedBlockEntity(BlockPos $$0, BlockState $$1, DyeColor $$2) {
        super(BlockEntityType.BED, $$0, $$1);
        this.color = $$2;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor $$0) {
        this.color = $$0;
    }
}