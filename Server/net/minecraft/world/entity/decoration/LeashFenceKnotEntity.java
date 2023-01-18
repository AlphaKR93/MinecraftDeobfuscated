/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LeashFenceKnotEntity
extends HangingEntity {
    public static final double OFFSET_Y = 0.375;

    public LeashFenceKnotEntity(EntityType<? extends LeashFenceKnotEntity> $$0, Level $$1) {
        super((EntityType<? extends HangingEntity>)$$0, $$1);
    }

    public LeashFenceKnotEntity(Level $$0, BlockPos $$1) {
        super(EntityType.LEASH_KNOT, $$0, $$1);
        this.setPos($$1.getX(), $$1.getY(), $$1.getZ());
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.375, (double)this.pos.getZ() + 0.5);
        double $$0 = (double)this.getType().getWidth() / 2.0;
        double $$1 = this.getType().getHeight();
        this.setBoundingBox(new AABB(this.getX() - $$0, this.getY(), this.getZ() - $$0, this.getX() + $$0, this.getY() + $$1, this.getZ() + $$0));
    }

    @Override
    public void setDirection(Direction $$0) {
    }

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    protected float getEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 0.0625f;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        return $$0 < 1024.0;
    }

    @Override
    public void dropItem(@Nullable Entity $$0) {
        this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
    }

    @Override
    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        if (this.level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        boolean $$2 = false;
        double $$3 = 7.0;
        List $$4 = this.level.getEntitiesOfClass(Mob.class, new AABB(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0));
        for (Mob $$5 : $$4) {
            if ($$5.getLeashHolder() != $$0) continue;
            $$5.setLeashedTo(this, true);
            $$2 = true;
        }
        if (!$$2) {
            this.discard();
            if ($$0.getAbilities().instabuild) {
                for (Mob $$6 : $$4) {
                    if (!$$6.isLeashed() || $$6.getLeashHolder() != this) continue;
                    $$6.dropLeash(true, false);
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean survives() {
        return this.level.getBlockState(this.pos).is(BlockTags.FENCES);
    }

    public static LeashFenceKnotEntity getOrCreateKnot(Level $$0, BlockPos $$1) {
        int $$2 = $$1.getX();
        int $$3 = $$1.getY();
        int $$4 = $$1.getZ();
        List $$5 = $$0.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)$$2 - 1.0, (double)$$3 - 1.0, (double)$$4 - 1.0, (double)$$2 + 1.0, (double)$$3 + 1.0, (double)$$4 + 1.0));
        for (LeashFenceKnotEntity $$6 : $$5) {
            if (!$$6.getPos().equals($$1)) continue;
            return $$6;
        }
        LeashFenceKnotEntity $$7 = new LeashFenceKnotEntity($$0, $$1);
        $$0.addFreshEntity($$7);
        return $$7;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0f, 1.0f);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, 0, this.getPos());
    }

    @Override
    public Vec3 getRopeHoldPosition(float $$0) {
        return this.getPosition($$0).add(0.0, 0.2, 0.0);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.LEAD);
    }
}