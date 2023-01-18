/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.item;

import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class FallingBlockEntity
extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public int time;
    public boolean dropItem = true;
    private boolean cancelDrop;
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamagePerDistance;
    @Nullable
    public CompoundTag blockData;
    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);

    public FallingBlockEntity(EntityType<? extends FallingBlockEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    private FallingBlockEntity(Level $$0, double $$1, double $$2, double $$3, BlockState $$4) {
        this((EntityType<? extends FallingBlockEntity>)EntityType.FALLING_BLOCK, $$0);
        this.blockState = $$4;
        this.blocksBuilding = true;
        this.setPos($$1, $$2, $$3);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = $$1;
        this.yo = $$2;
        this.zo = $$3;
        this.setStartPos(this.blockPosition());
    }

    public static FallingBlockEntity fall(Level $$0, BlockPos $$1, BlockState $$2) {
        FallingBlockEntity $$3 = new FallingBlockEntity($$0, (double)$$1.getX() + 0.5, $$1.getY(), (double)$$1.getZ() + 0.5, $$2.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)$$2.setValue(BlockStateProperties.WATERLOGGED, false) : $$2);
        $$0.setBlock($$1, $$2.getFluidState().createLegacyBlock(), 3);
        $$0.addFreshEntity($$3);
        return $$3;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setStartPos(BlockPos $$0) {
        this.entityData.set(DATA_START_POS, $$0);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_START_POS, BlockPos.ZERO);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
            return;
        }
        Block $$0 = this.blockState.getBlock();
        ++this.time;
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.level.isClientSide) {
            BlockHitResult $$5;
            BlockPos $$1 = this.blockPosition();
            boolean $$2 = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean $$3 = $$2 && this.level.getFluidState($$1).is(FluidTags.WATER);
            double $$4 = this.getDeltaMovement().lengthSqr();
            if ($$2 && $$4 > 1.0 && ($$5 = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && this.level.getFluidState($$5.getBlockPos()).is(FluidTags.WATER)) {
                $$1 = $$5.getBlockPos();
                $$3 = true;
            }
            if (this.onGround || $$3) {
                BlockState $$6 = this.level.getBlockState($$1);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                if (!$$6.is(Blocks.MOVING_PISTON)) {
                    if (!this.cancelDrop) {
                        boolean $$9;
                        boolean $$7 = $$6.canBeReplaced(new DirectionalPlaceContext(this.level, $$1, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean $$8 = FallingBlock.isFree(this.level.getBlockState((BlockPos)$$1.below())) && (!$$2 || !$$3);
                        boolean bl = $$9 = this.blockState.canSurvive(this.level, $$1) && !$$8;
                        if ($$7 && $$9) {
                            if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState($$1).getType() == Fluids.WATER) {
                                this.blockState = (BlockState)this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                            }
                            if (this.level.setBlock($$1, this.blockState, 3)) {
                                BlockEntity $$10;
                                ((ServerLevel)this.level).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket($$1, this.level.getBlockState($$1)));
                                this.discard();
                                if ($$0 instanceof Fallable) {
                                    ((Fallable)((Object)$$0)).onLand(this.level, $$1, this.blockState, $$6, this);
                                }
                                if (this.blockData != null && this.blockState.hasBlockEntity() && ($$10 = this.level.getBlockEntity($$1)) != null) {
                                    CompoundTag $$11 = $$10.saveWithoutMetadata();
                                    for (String $$12 : this.blockData.getAllKeys()) {
                                        $$11.put($$12, this.blockData.get($$12).copy());
                                    }
                                    try {
                                        $$10.load($$11);
                                    }
                                    catch (Exception $$13) {
                                        LOGGER.error("Failed to load block entity from falling block", (Throwable)$$13);
                                    }
                                    $$10.setChanged();
                                }
                            } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.discard();
                                this.callOnBrokenAfterFall($$0, $$1);
                                this.spawnAtLocation($$0);
                            }
                        } else {
                            this.discard();
                            if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.callOnBrokenAfterFall($$0, $$1);
                                this.spawnAtLocation($$0);
                            }
                        }
                    } else {
                        this.discard();
                        this.callOnBrokenAfterFall($$0, $$1);
                    }
                }
            } else if (!(this.level.isClientSide || (this.time <= 100 || $$1.getY() > this.level.getMinBuildHeight() && $$1.getY() <= this.level.getMaxBuildHeight()) && this.time <= 600)) {
                if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation($$0);
                }
                this.discard();
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }

    public void callOnBrokenAfterFall(Block $$0, BlockPos $$1) {
        if ($$0 instanceof Fallable) {
            ((Fallable)((Object)$$0)).onBrokenAfterFall(this.level, $$1, this);
        }
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$22) {
        DamageSource $$8;
        Predicate<Entity> $$7;
        if (!this.hurtEntities) {
            return false;
        }
        int $$3 = Mth.ceil($$0 - 1.0f);
        if ($$3 < 0) {
            return false;
        }
        if (this.blockState.getBlock() instanceof Fallable) {
            Fallable $$4 = (Fallable)((Object)this.blockState.getBlock());
            Predicate<Entity> $$5 = $$4.getHurtsEntitySelector();
            DamageSource $$6 = $$4.getFallDamageSource(this);
        } else {
            $$7 = EntitySelector.NO_SPECTATORS;
            $$8 = DamageSource.fallingBlock(this);
        }
        float $$9 = Math.min((int)Mth.floor((float)$$3 * this.fallDamagePerDistance), (int)this.fallDamageMax);
        this.level.getEntities(this, this.getBoundingBox(), $$7).forEach($$2 -> $$2.hurt($$8, $$9));
        boolean $$10 = this.blockState.is(BlockTags.ANVIL);
        if ($$10 && $$9 > 0.0f && this.random.nextFloat() < 0.05f + (float)$$3 * 0.05f) {
            BlockState $$11 = AnvilBlock.damage(this.blockState);
            if ($$11 == null) {
                this.cancelDrop = true;
            } else {
                this.blockState = $$11;
            }
        }
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        $$0.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        $$0.putInt("Time", this.time);
        $$0.putBoolean("DropItem", this.dropItem);
        $$0.putBoolean("HurtEntities", this.hurtEntities);
        $$0.putFloat("FallHurtAmount", this.fallDamagePerDistance);
        $$0.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            $$0.put("TileEntityData", this.blockData);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        this.blockState = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), $$0.getCompound("BlockState"));
        this.time = $$0.getInt("Time");
        if ($$0.contains("HurtEntities", 99)) {
            this.hurtEntities = $$0.getBoolean("HurtEntities");
            this.fallDamagePerDistance = $$0.getFloat("FallHurtAmount");
            this.fallDamageMax = $$0.getInt("FallHurtMax");
        } else if (this.blockState.is(BlockTags.ANVIL)) {
            this.hurtEntities = true;
        }
        if ($$0.contains("DropItem", 99)) {
            this.dropItem = $$0.getBoolean("DropItem");
        }
        if ($$0.contains("TileEntityData", 10)) {
            this.blockData = $$0.getCompound("TileEntityData");
        }
        if (this.blockState.isAir()) {
            this.blockState = Blocks.SAND.defaultBlockState();
        }
    }

    public void setHurtsEntities(float $$0, int $$1) {
        this.hurtEntities = true;
        this.fallDamagePerDistance = $$0;
        this.fallDamageMax = $$1;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory $$0) {
        super.fillCrashReportCategory($$0);
        $$0.setDetail("Immitating BlockState", this.blockState.toString());
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        this.blockState = Block.stateById($$0.getData());
        this.blocksBuilding = true;
        double $$1 = $$0.getX();
        double $$2 = $$0.getY();
        double $$3 = $$0.getZ();
        this.setPos($$1, $$2, $$3);
        this.setStartPos(this.blockPosition());
    }
}