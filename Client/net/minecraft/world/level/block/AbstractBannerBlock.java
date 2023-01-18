/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBannerBlock
extends BaseEntityBlock {
    private final DyeColor color;

    protected AbstractBannerBlock(DyeColor $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.color = $$0;
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BannerBlockEntity($$0, $$1, this.color);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$12, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        if ($$0.isClientSide) {
            $$0.getBlockEntity($$12, BlockEntityType.BANNER).ifPresent($$1 -> $$1.fromItem($$4));
        } else if ($$4.hasCustomHoverName()) {
            $$0.getBlockEntity($$12, BlockEntityType.BANNER).ifPresent($$1 -> $$1.setCustomName($$4.getHoverName()));
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        BlockEntity $$3 = $$0.getBlockEntity($$1);
        if ($$3 instanceof BannerBlockEntity) {
            return ((BannerBlockEntity)$$3).getItem();
        }
        return super.getCloneItemStack($$0, $$1, $$2);
    }

    public DyeColor getColor() {
        return this.color;
    }
}