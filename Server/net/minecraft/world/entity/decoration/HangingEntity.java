/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.world.level.Level
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public abstract class HangingEntity
extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static final Predicate<Entity> HANGING_ENTITY = $$0 -> $$0 instanceof HangingEntity;
    private int checkInterval;
    protected BlockPos pos;
    protected Direction direction = Direction.SOUTH;

    protected HangingEntity(EntityType<? extends HangingEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    protected HangingEntity(EntityType<? extends HangingEntity> $$0, Level $$1, BlockPos $$2) {
        this($$0, $$1);
        this.pos = $$2;
    }

    @Override
    protected void defineSynchedData() {
    }

    protected void setDirection(Direction $$0) {
        Validate.notNull((Object)$$0);
        Validate.isTrue((boolean)$$0.getAxis().isHorizontal());
        this.direction = $$0;
        this.setYRot(this.direction.get2DDataValue() * 90);
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    protected void recalculateBoundingBox() {
        if (this.direction == null) {
            return;
        }
        double $$0 = (double)this.pos.getX() + 0.5;
        double $$1 = (double)this.pos.getY() + 0.5;
        double $$2 = (double)this.pos.getZ() + 0.5;
        double $$3 = 0.46875;
        double $$4 = this.offs(this.getWidth());
        double $$5 = this.offs(this.getHeight());
        $$0 -= (double)this.direction.getStepX() * 0.46875;
        $$2 -= (double)this.direction.getStepZ() * 0.46875;
        Direction $$6 = this.direction.getCounterClockWise();
        this.setPosRaw($$0 += $$4 * (double)$$6.getStepX(), $$1 += $$5, $$2 += $$4 * (double)$$6.getStepZ());
        double $$7 = this.getWidth();
        double $$8 = this.getHeight();
        double $$9 = this.getWidth();
        if (this.direction.getAxis() == Direction.Axis.Z) {
            $$9 = 1.0;
        } else {
            $$7 = 1.0;
        }
        this.setBoundingBox(new AABB($$0 - ($$7 /= 32.0), $$1 - ($$8 /= 32.0), $$2 - ($$9 /= 32.0), $$0 + $$7, $$1 + $$8, $$2 + $$9));
    }

    private double offs(int $$0) {
        return $$0 % 32 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            this.checkOutOfWorld();
            if (this.checkInterval++ == 100) {
                this.checkInterval = 0;
                if (!this.isRemoved() && !this.survives()) {
                    this.discard();
                    this.dropItem(null);
                }
            }
        }
    }

    public boolean survives() {
        if (!this.level.noCollision((Entity)this)) {
            return false;
        }
        int $$0 = Math.max((int)1, (int)(this.getWidth() / 16));
        int $$1 = Math.max((int)1, (int)(this.getHeight() / 16));
        Vec3i $$2 = this.pos.relative(this.direction.getOpposite());
        Direction $$3 = this.direction.getCounterClockWise();
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (int $$5 = 0; $$5 < $$0; ++$$5) {
            for (int $$6 = 0; $$6 < $$1; ++$$6) {
                int $$7 = ($$0 - 1) / -2;
                int $$8 = ($$1 - 1) / -2;
                $$4.set($$2).move($$3, $$5 + $$7).move(Direction.UP, $$6 + $$8);
                BlockState $$9 = this.level.getBlockState((BlockPos)$$4);
                if ($$9.getMaterial().isSolid() || DiodeBlock.isDiode($$9)) continue;
                return false;
            }
        }
        return this.level.getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity $$0) {
        if ($$0 instanceof Player) {
            Player $$1 = (Player)$$0;
            if (!this.level.mayInteract($$1, this.pos)) {
                return true;
            }
            return this.hurt(DamageSource.playerAttack($$1), 0.0f);
        }
        return false;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (!this.isRemoved() && !this.level.isClientSide) {
            this.kill();
            this.markHurt();
            this.dropItem($$0.getEntity());
        }
        return true;
    }

    @Override
    public void move(MoverType $$0, Vec3 $$1) {
        if (!this.level.isClientSide && !this.isRemoved() && $$1.lengthSqr() > 0.0) {
            this.kill();
            this.dropItem(null);
        }
    }

    @Override
    public void push(double $$0, double $$1, double $$2) {
        if (!this.level.isClientSide && !this.isRemoved() && $$0 * $$0 + $$1 * $$1 + $$2 * $$2 > 0.0) {
            this.kill();
            this.dropItem(null);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        BlockPos $$1 = this.getPos();
        $$0.putInt("TileX", $$1.getX());
        $$0.putInt("TileY", $$1.getY());
        $$0.putInt("TileZ", $$1.getZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        BlockPos $$1 = new BlockPos($$0.getInt("TileX"), $$0.getInt("TileY"), $$0.getInt("TileZ"));
        if (!$$1.closerThan(this.blockPosition(), 16.0)) {
            LOGGER.error("Hanging entity at invalid position: {}", (Object)$$1);
            return;
        }
        this.pos = $$1;
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void dropItem(@Nullable Entity var1);

    public abstract void playPlacementSound();

    @Override
    public ItemEntity spawnAtLocation(ItemStack $$0, float $$1) {
        ItemEntity $$2 = new ItemEntity(this.level, this.getX() + (double)((float)this.direction.getStepX() * 0.15f), this.getY() + (double)$$1, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15f), $$0);
        $$2.setDefaultPickUpDelay();
        this.level.addFreshEntity((Entity)$$2);
        return $$2;
    }

    @Override
    protected boolean repositionEntityAfterLoad() {
        return false;
    }

    @Override
    public void setPos(double $$0, double $$1, double $$2) {
        this.pos = new BlockPos($$0, $$1, $$2);
        this.recalculateBoundingBox();
        this.hasImpulse = true;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public float rotate(Rotation $$0) {
        if (this.direction.getAxis() != Direction.Axis.Y) {
            switch ($$0) {
                case CLOCKWISE_180: {
                    this.direction = this.direction.getOpposite();
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    this.direction = this.direction.getCounterClockWise();
                    break;
                }
                case CLOCKWISE_90: {
                    this.direction = this.direction.getClockWise();
                    break;
                }
            }
        }
        float $$1 = Mth.wrapDegrees(this.getYRot());
        switch ($$0) {
            case CLOCKWISE_180: {
                return $$1 + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return $$1 + 90.0f;
            }
            case CLOCKWISE_90: {
                return $$1 + 270.0f;
            }
        }
        return $$1;
    }

    @Override
    public float mirror(Mirror $$0) {
        return this.rotate($$0.getRotation(this.direction));
    }

    @Override
    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
    }

    @Override
    public void refreshDimensions() {
    }
}