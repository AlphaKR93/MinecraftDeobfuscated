/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PowderSnowBlock
extends Block
implements BucketPickup {
    private static final float HORIZONTAL_PARTICLE_MOMENTUM_FACTOR = 0.083333336f;
    private static final float IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER = 0.9f;
    private static final float IN_BLOCK_VERTICAL_SPEED_MULTIPLIER = 1.5f;
    private static final float NUM_BLOCKS_TO_FALL_INTO_BLOCK = 2.5f;
    private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9f, 1.0);
    private static final double MINIMUM_FALL_DISTANCE_FOR_SOUND = 4.0;
    private static final double MINIMUM_FALL_DISTANCE_FOR_BIG_SOUND = 7.0;

    public PowderSnowBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean skipRendering(BlockState $$0, BlockState $$1, Direction $$2) {
        if ($$1.is(this)) {
            return true;
        }
        return super.skipRendering($$0, $$1, $$2);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if (!($$3 instanceof LivingEntity) || $$3.getFeetBlockState().is(this)) {
            $$3.makeStuckInBlock($$0, new Vec3(0.9f, 1.5, 0.9f));
            if ($$1.isClientSide) {
                boolean $$5;
                RandomSource $$4 = $$1.getRandom();
                boolean bl = $$5 = $$3.xOld != $$3.getX() || $$3.zOld != $$3.getZ();
                if ($$5 && $$4.nextBoolean()) {
                    $$1.addParticle(ParticleTypes.SNOWFLAKE, $$3.getX(), $$2.getY() + 1, $$3.getZ(), Mth.randomBetween($$4, -1.0f, 1.0f) * 0.083333336f, 0.05f, Mth.randomBetween($$4, -1.0f, 1.0f) * 0.083333336f);
                }
            }
        }
        $$3.setIsInPowderSnow(true);
        if (!$$1.isClientSide) {
            if ($$3.isOnFire() && ($$1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || $$3 instanceof Player) && $$3.mayInteract($$1, $$2)) {
                $$1.destroyBlock($$2, false);
            }
            $$3.setSharedFlagOnFire(false);
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, float $$4) {
        void $$6;
        if ((double)$$4 < 4.0 || !($$3 instanceof LivingEntity)) {
            return;
        }
        LivingEntity $$5 = (LivingEntity)$$3;
        LivingEntity.Fallsounds $$7 = $$6.getFallSounds();
        SoundEvent $$8 = (double)$$4 < 7.0 ? $$7.small() : $$7.big();
        $$3.playSound($$8, 1.0f, 1.0f);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        EntityCollisionContext $$4;
        Entity $$5;
        if ($$3 instanceof EntityCollisionContext && ($$5 = ($$4 = (EntityCollisionContext)$$3).getEntity()) != null) {
            if ($$5.fallDistance > 2.5f) {
                return FALLING_COLLISION_SHAPE;
            }
            boolean $$6 = $$5 instanceof FallingBlockEntity;
            if ($$6 || PowderSnowBlock.canEntityWalkOnPowderSnow($$5) && $$3.isAbove(Shapes.block(), $$2, false) && !$$3.isDescending()) {
                return super.getCollisionShape($$0, $$1, $$2, $$3);
            }
        }
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return Shapes.empty();
    }

    public static boolean canEntityWalkOnPowderSnow(Entity $$0) {
        if ($$0.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
            return true;
        }
        if ($$0 instanceof LivingEntity) {
            return ((LivingEntity)$$0).getItemBySlot(EquipmentSlot.FEET).is(Items.LEATHER_BOOTS);
        }
        return false;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
        $$0.setBlock($$1, Blocks.AIR.defaultBlockState(), 11);
        if (!$$0.isClientSide()) {
            $$0.levelEvent(2001, $$1, Block.getId($$2));
        }
        return new ItemStack(Items.POWDER_SNOW_BUCKET);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of((Object)SoundEvents.BUCKET_FILL_POWDER_SNOW);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return true;
    }
}