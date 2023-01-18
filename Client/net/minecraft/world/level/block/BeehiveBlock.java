/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeehiveBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.LEVEL_HONEY;
    public static final int MAX_HONEY_LEVELS = 5;
    private static final int SHEARED_HONEYCOMB_COUNT = 3;

    public BeehiveBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HONEY_LEVEL, 0)).setValue(FACING, Direction.NORTH));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return $$0.getValue(HONEY_LEVEL);
    }

    @Override
    public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
        super.playerDestroy($$0, $$1, $$2, $$3, $$4, $$5);
        if (!$$0.isClientSide && $$4 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$6 = (BeehiveBlockEntity)$$4;
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, $$5) == 0) {
                $$6.emptyAllLivingFromHive($$1, $$3, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                $$0.updateNeighbourForOutputSignal($$2, this);
                this.angerNearbyBees($$0, $$2);
            }
            CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayer)$$1, $$3, $$5, $$6.getOccupantCount());
        }
    }

    private void angerNearbyBees(Level $$0, BlockPos $$1) {
        List $$2 = $$0.getEntitiesOfClass(Bee.class, new AABB($$1).inflate(8.0, 6.0, 8.0));
        if (!$$2.isEmpty()) {
            List $$3 = $$0.getEntitiesOfClass(Player.class, new AABB($$1).inflate(8.0, 6.0, 8.0));
            int $$4 = $$3.size();
            for (Bee $$5 : $$2) {
                if ($$5.getTarget() != null) continue;
                $$5.setTarget((LivingEntity)$$3.get($$0.random.nextInt($$4)));
            }
        }
    }

    public static void dropHoneycomb(Level $$0, BlockPos $$1) {
        BeehiveBlock.popResource($$0, $$1, new ItemStack(Items.HONEYCOMB, 3));
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$12, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        ItemStack $$6 = $$3.getItemInHand($$4);
        int $$7 = $$0.getValue(HONEY_LEVEL);
        boolean $$8 = false;
        if ($$7 >= 5) {
            Item $$9 = $$6.getItem();
            if ($$6.is(Items.SHEARS)) {
                $$12.playSound($$3, $$3.getX(), $$3.getY(), $$3.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
                BeehiveBlock.dropHoneycomb($$12, $$2);
                $$6.hurtAndBreak(1, $$3, $$1 -> $$1.broadcastBreakEvent($$4));
                $$8 = true;
                $$12.gameEvent((Entity)((Object)$$3), GameEvent.SHEAR, $$2);
            } else if ($$6.is(Items.GLASS_BOTTLE)) {
                $$6.shrink(1);
                $$12.playSound($$3, $$3.getX(), $$3.getY(), $$3.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                if ($$6.isEmpty()) {
                    $$3.setItemInHand($$4, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!$$3.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
                    $$3.drop(new ItemStack(Items.HONEY_BOTTLE), false);
                }
                $$8 = true;
                $$12.gameEvent((Entity)((Object)$$3), GameEvent.FLUID_PICKUP, $$2);
            }
            if (!$$12.isClientSide() && $$8) {
                $$3.awardStat(Stats.ITEM_USED.get($$9));
            }
        }
        if ($$8) {
            if (!CampfireBlock.isSmokeyPos($$12, $$2)) {
                if (this.hiveContainsBees($$12, $$2)) {
                    this.angerNearbyBees($$12, $$2);
                }
                this.releaseBeesAndResetHoneyLevel($$12, $$0, $$2, $$3, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            } else {
                this.resetHoneyLevel($$12, $$0, $$2);
            }
            return InteractionResult.sidedSuccess($$12.isClientSide);
        }
        return super.use($$0, $$12, $$2, $$3, $$4, $$5);
    }

    private boolean hiveContainsBees(Level $$0, BlockPos $$1) {
        BlockEntity $$2 = $$0.getBlockEntity($$1);
        if ($$2 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$3 = (BeehiveBlockEntity)$$2;
            return !$$3.isEmpty();
        }
        return false;
    }

    public void releaseBeesAndResetHoneyLevel(Level $$0, BlockState $$1, BlockPos $$2, @Nullable Player $$3, BeehiveBlockEntity.BeeReleaseStatus $$4) {
        this.resetHoneyLevel($$0, $$1, $$2);
        BlockEntity $$5 = $$0.getBlockEntity($$2);
        if ($$5 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$6 = (BeehiveBlockEntity)$$5;
            $$6.emptyAllLivingFromHive($$3, $$1, $$4);
        }
    }

    public void resetHoneyLevel(Level $$0, BlockState $$1, BlockPos $$2) {
        $$0.setBlock($$2, (BlockState)$$1.setValue(HONEY_LEVEL, 0), 3);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(HONEY_LEVEL) >= 5) {
            for (int $$4 = 0; $$4 < $$3.nextInt(1) + 1; ++$$4) {
                this.trySpawnDripParticles($$1, $$2, $$0);
            }
        }
    }

    private void trySpawnDripParticles(Level $$0, BlockPos $$1, BlockState $$2) {
        if (!$$2.getFluidState().isEmpty() || $$0.random.nextFloat() < 0.3f) {
            return;
        }
        VoxelShape $$3 = $$2.getCollisionShape($$0, $$1);
        double $$4 = $$3.max(Direction.Axis.Y);
        if ($$4 >= 1.0 && !$$2.is(BlockTags.IMPERMEABLE)) {
            double $$5 = $$3.min(Direction.Axis.Y);
            if ($$5 > 0.0) {
                this.spawnParticle($$0, $$1, $$3, (double)$$1.getY() + $$5 - 0.05);
            } else {
                Vec3i $$6 = $$1.below();
                BlockState $$7 = $$0.getBlockState((BlockPos)$$6);
                VoxelShape $$8 = $$7.getCollisionShape($$0, (BlockPos)$$6);
                double $$9 = $$8.max(Direction.Axis.Y);
                if (($$9 < 1.0 || !$$7.isCollisionShapeFullBlock($$0, (BlockPos)$$6)) && $$7.getFluidState().isEmpty()) {
                    this.spawnParticle($$0, $$1, $$3, (double)$$1.getY() - 0.05);
                }
            }
        }
    }

    private void spawnParticle(Level $$0, BlockPos $$1, VoxelShape $$2, double $$3) {
        this.spawnFluidParticle($$0, (double)$$1.getX() + $$2.min(Direction.Axis.X), (double)$$1.getX() + $$2.max(Direction.Axis.X), (double)$$1.getZ() + $$2.min(Direction.Axis.Z), (double)$$1.getZ() + $$2.max(Direction.Axis.Z), $$3);
    }

    private void spawnFluidParticle(Level $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        $$0.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp($$0.random.nextDouble(), $$1, $$2), $$5, Mth.lerp($$0.random.nextDouble(), $$3, $$4), 0.0, 0.0, 0.0);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(HONEY_LEVEL, FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BeehiveBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return $$0.isClientSide ? null : BeehiveBlock.createTickerHelper($$2, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
    }

    @Override
    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        BlockEntity $$4;
        if (!$$0.isClientSide && $$3.isCreative() && $$0.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && ($$4 = $$0.getBlockEntity($$1)) instanceof BeehiveBlockEntity) {
            boolean $$8;
            BeehiveBlockEntity $$5 = (BeehiveBlockEntity)$$4;
            ItemStack $$6 = new ItemStack(this);
            int $$7 = $$2.getValue(HONEY_LEVEL);
            boolean bl = $$8 = !$$5.isEmpty();
            if ($$8 || $$7 > 0) {
                if ($$8) {
                    CompoundTag $$9 = new CompoundTag();
                    $$9.put("Bees", $$5.writeBees());
                    BlockItem.setBlockEntityData($$6, BlockEntityType.BEEHIVE, $$9);
                }
                CompoundTag $$10 = new CompoundTag();
                $$10.putInt("honey_level", $$7);
                $$6.addTagElement("BlockStateTag", $$10);
                ItemEntity $$11 = new ItemEntity($$0, $$1.getX(), $$1.getY(), $$1.getZ(), $$6);
                $$11.setDefaultPickUpDelay();
                $$0.addFreshEntity($$11);
            }
        }
        super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    public List<ItemStack> getDrops(BlockState $$0, LootContext.Builder $$1) {
        BlockEntity $$3;
        Entity $$2 = $$1.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (($$2 instanceof PrimedTnt || $$2 instanceof Creeper || $$2 instanceof WitherSkull || $$2 instanceof WitherBoss || $$2 instanceof MinecartTNT) && ($$3 = $$1.getOptionalParameter(LootContextParams.BLOCK_ENTITY)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$4 = (BeehiveBlockEntity)$$3;
            $$4.emptyAllLivingFromHive(null, $$0, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        }
        return super.getDrops($$0, $$1);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        BlockEntity $$6;
        if ($$3.getBlockState($$5).getBlock() instanceof FireBlock && ($$6 = $$3.getBlockEntity($$4)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$7 = (BeehiveBlockEntity)$$6;
            $$7.emptyAllLivingFromHive(null, $$0, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }
}