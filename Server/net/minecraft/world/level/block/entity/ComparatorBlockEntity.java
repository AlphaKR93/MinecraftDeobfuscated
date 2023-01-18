/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ComparatorBlockEntity
extends BlockEntity {
    private int output;

    public ComparatorBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.COMPARATOR, $$0, $$1);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putInt("OutputSignal", this.output);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.output = $$0.getInt("OutputSignal");
    }

    public int getOutputSignal() {
        return this.output;
    }

    public void setOutputSignal(int $$0) {
        this.output = $$0;
    }
}