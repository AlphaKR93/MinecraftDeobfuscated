/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss.enderdragon;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.dimension.end.EndDragonFight;

public class EndCrystal
extends Entity {
    private static final EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DATA_SHOW_BOTTOM = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.BOOLEAN);
    public int time;

    public EndCrystal(EntityType<? extends EndCrystal> $$0, Level $$1) {
        super($$0, $$1);
        this.blocksBuilding = true;
        this.time = this.random.nextInt(100000);
    }

    public EndCrystal(Level $$0, double $$1, double $$2, double $$3) {
        this((EntityType<? extends EndCrystal>)EntityType.END_CRYSTAL, $$0);
        this.setPos($$1, $$2, $$3);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_BEAM_TARGET, Optional.empty());
        this.getEntityData().define(DATA_SHOW_BOTTOM, true);
    }

    @Override
    public void tick() {
        ++this.time;
        if (this.level instanceof ServerLevel) {
            BlockPos $$0 = this.blockPosition();
            if (((ServerLevel)this.level).dragonFight() != null && this.level.getBlockState($$0).isAir()) {
                this.level.setBlockAndUpdate($$0, BaseFireBlock.getState(this.level, $$0));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        if (this.getBeamTarget() != null) {
            $$0.put("BeamTarget", NbtUtils.writeBlockPos(this.getBeamTarget()));
        }
        $$0.putBoolean("ShowBottom", this.showsBottom());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        if ($$0.contains("BeamTarget", 10)) {
            this.setBeamTarget(NbtUtils.readBlockPos($$0.getCompound("BeamTarget")));
        }
        if ($$0.contains("ShowBottom", 1)) {
            this.setShowBottom($$0.getBoolean("ShowBottom"));
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if ($$0.getEntity() instanceof EnderDragon) {
            return false;
        }
        if (!this.isRemoved() && !this.level.isClientSide) {
            this.remove(Entity.RemovalReason.KILLED);
            if (!$$0.isExplosion()) {
                DamageSource $$2 = $$0.getEntity() != null ? DamageSource.explosion(this, $$0.getEntity()) : null;
                this.level.explode(this, $$2, null, this.getX(), this.getY(), this.getZ(), 6.0f, false, Level.ExplosionInteraction.BLOCK);
            }
            this.onDestroyedBy($$0);
        }
        return true;
    }

    @Override
    public void kill() {
        this.onDestroyedBy(DamageSource.GENERIC);
        super.kill();
    }

    private void onDestroyedBy(DamageSource $$0) {
        EndDragonFight $$1;
        if (this.level instanceof ServerLevel && ($$1 = ((ServerLevel)this.level).dragonFight()) != null) {
            $$1.onCrystalDestroyed(this, $$0);
        }
    }

    public void setBeamTarget(@Nullable BlockPos $$0) {
        this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable((Object)$$0));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return (BlockPos)this.getEntityData().get(DATA_BEAM_TARGET).orElse(null);
    }

    public void setShowBottom(boolean $$0) {
        this.getEntityData().set(DATA_SHOW_BOTTOM, $$0);
    }

    public boolean showsBottom() {
        return this.getEntityData().get(DATA_SHOW_BOTTOM);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        return super.shouldRenderAtSqrDistance($$0) || this.getBeamTarget() != null;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.END_CRYSTAL);
    }
}