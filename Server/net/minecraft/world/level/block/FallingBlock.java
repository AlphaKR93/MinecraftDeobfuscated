/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class FallingBlock
extends Block
implements Fallable {
    public FallingBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        $$1.scheduleTick($$2, this, this.getDelayAfterPlace());
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        $$3.scheduleTick($$4, this, this.getDelayAfterPlace());
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!FallingBlock.isFree($$1.getBlockState((BlockPos)$$2.below())) || $$2.getY() < $$1.getMinBuildHeight()) {
            return;
        }
        FallingBlockEntity $$4 = FallingBlockEntity.fall($$1, $$2, $$0);
        this.falling($$4);
    }

    protected void falling(FallingBlockEntity $$0) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState $$0) {
        Material $$1 = $$0.getMaterial();
        return $$0.isAir() || $$0.is(BlockTags.FIRE) || $$1.isLiquid() || $$0.canBeReplaced();
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        Vec3i $$4;
        if ($$3.nextInt(16) == 0 && FallingBlock.isFree($$1.getBlockState((BlockPos)($$4 = $$2.below())))) {
            double $$5 = (double)$$2.getX() + $$3.nextDouble();
            double $$6 = (double)$$2.getY() - 0.05;
            double $$7 = (double)$$2.getZ() + $$3.nextDouble();
            $$1.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, $$0), $$5, $$6, $$7, 0.0, 0.0, 0.0);
        }
    }

    public int getDustColor(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return -16777216;
    }
}