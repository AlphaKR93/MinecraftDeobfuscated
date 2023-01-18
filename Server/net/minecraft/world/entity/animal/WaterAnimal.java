/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class WaterAnimal
extends PathfinderMob {
    protected WaterAnimal(EntityType<? extends WaterAnimal> $$0, Level $$1) {
        super((EntityType<? extends PathfinderMob>)$$0, $$1);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return $$0.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public int getExperienceReward() {
        return 1 + this.level.random.nextInt(3);
    }

    protected void handleAirSupply(int $$0) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply($$0 - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DROWN, 2.0f);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public void baseTick() {
        int $$0 = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply($$0);
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player $$0) {
        return false;
    }

    public static boolean checkSurfaceWaterAnimalSpawnRules(EntityType<? extends WaterAnimal> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        int $$5 = $$1.getSeaLevel();
        int $$6 = $$5 - 13;
        return $$3.getY() >= $$6 && $$3.getY() <= $$5 && $$1.getFluidState((BlockPos)$$3.below()).is(FluidTags.WATER) && $$1.getBlockState((BlockPos)$$3.above()).is(Blocks.WATER);
    }
}