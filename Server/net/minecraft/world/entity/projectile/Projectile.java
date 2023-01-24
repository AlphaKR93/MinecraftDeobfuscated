/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class Projectile
extends Entity
implements TraceableEntity {
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private boolean leftOwner;
    private boolean hasBeenShot;

    Projectile(EntityType<? extends Projectile> $$0, Level $$1) {
        super($$0, $$1);
    }

    public void setOwner(@Nullable Entity $$0) {
        if ($$0 != null) {
            this.ownerUUID = $$0.getUUID();
            this.cachedOwner = $$0;
        }
    }

    @Override
    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        }
        if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        }
        return null;
    }

    public Entity getEffectSource() {
        return (Entity)MoreObjects.firstNonNull((Object)this.getOwner(), (Object)this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        if (this.ownerUUID != null) {
            $$0.putUUID("Owner", this.ownerUUID);
        }
        if (this.leftOwner) {
            $$0.putBoolean("LeftOwner", true);
        }
        $$0.putBoolean("HasBeenShot", this.hasBeenShot);
    }

    protected boolean ownedBy(Entity $$0) {
        return $$0.getUUID().equals((Object)this.ownerUUID);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        if ($$0.hasUUID("Owner")) {
            this.ownerUUID = $$0.getUUID("Owner");
        }
        this.leftOwner = $$0.getBoolean("LeftOwner");
        this.hasBeenShot = $$0.getBoolean("HasBeenShot");
    }

    @Override
    public void tick() {
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        super.tick();
    }

    private boolean checkLeftOwner() {
        Entity $$02 = this.getOwner();
        if ($$02 != null) {
            for (Entity $$1 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), (Predicate<? super Entity>)((Predicate)$$0 -> !$$0.isSpectator() && $$0.isPickable()))) {
                if ($$1.getRootVehicle() != $$02.getRootVehicle()) continue;
                return false;
            }
        }
        return true;
    }

    public void shoot(double $$0, double $$1, double $$2, float $$3, float $$4) {
        Vec3 $$5 = new Vec3($$0, $$1, $$2).normalize().add(this.random.triangle(0.0, 0.0172275 * (double)$$4), this.random.triangle(0.0, 0.0172275 * (double)$$4), this.random.triangle(0.0, 0.0172275 * (double)$$4)).scale($$3);
        this.setDeltaMovement($$5);
        double $$6 = $$5.horizontalDistance();
        this.setYRot((float)(Mth.atan2($$5.x, $$5.z) * 57.2957763671875));
        this.setXRot((float)(Mth.atan2($$5.y, $$6) * 57.2957763671875));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = -Mth.sin($$2 * ((float)Math.PI / 180)) * Mth.cos($$1 * ((float)Math.PI / 180));
        float $$7 = -Mth.sin(($$1 + $$3) * ((float)Math.PI / 180));
        float $$8 = Mth.cos($$2 * ((float)Math.PI / 180)) * Mth.cos($$1 * ((float)Math.PI / 180));
        this.shoot($$6, $$7, $$8, $$4, $$5);
        Vec3 $$9 = $$0.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add($$9.x, $$0.isOnGround() ? 0.0 : $$9.y, $$9.z));
    }

    protected void onHit(HitResult $$0) {
        HitResult.Type $$1 = $$0.getType();
        if ($$1 == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)$$0);
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, $$0.getLocation(), GameEvent.Context.of(this, null));
        } else if ($$1 == HitResult.Type.BLOCK) {
            BlockHitResult $$2 = (BlockHitResult)$$0;
            this.onHitBlock($$2);
            BlockPos $$3 = $$2.getBlockPos();
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, $$3, GameEvent.Context.of(this, this.level.getBlockState($$3)));
        }
    }

    protected void onHitEntity(EntityHitResult $$0) {
    }

    protected void onHitBlock(BlockHitResult $$0) {
        BlockState $$1 = this.level.getBlockState($$0.getBlockPos());
        $$1.onProjectileHit(this.level, $$1, $$0, this);
    }

    @Override
    public void lerpMotion(double $$0, double $$1, double $$2) {
        this.setDeltaMovement($$0, $$1, $$2);
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            double $$3 = Math.sqrt((double)($$0 * $$0 + $$2 * $$2));
            this.setXRot((float)(Mth.atan2($$1, $$3) * 57.2957763671875));
            this.setYRot((float)(Mth.atan2($$0, $$2) * 57.2957763671875));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
    }

    protected boolean canHitEntity(Entity $$0) {
        if ($$0.isSpectator() || !$$0.isAlive() || !$$0.isPickable()) {
            return false;
        }
        Entity $$1 = this.getOwner();
        return $$1 == null || this.leftOwner || !$$1.isPassengerOfSameVehicle($$0);
    }

    protected void updateRotation() {
        Vec3 $$0 = this.getDeltaMovement();
        double $$1 = $$0.horizontalDistance();
        this.setXRot(Projectile.lerpRotation(this.xRotO, (float)(Mth.atan2($$0.y, $$1) * 57.2957763671875)));
        this.setYRot(Projectile.lerpRotation(this.yRotO, (float)(Mth.atan2($$0.x, $$0.z) * 57.2957763671875)));
    }

    protected static float lerpRotation(float $$0, float $$1) {
        while ($$1 - $$0 < -180.0f) {
            $$0 -= 360.0f;
        }
        while ($$1 - $$0 >= 180.0f) {
            $$0 += 360.0f;
        }
        return Mth.lerp(0.2f, $$0, $$1);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity $$0 = this.getOwner();
        return new ClientboundAddEntityPacket(this, $$0 == null ? 0 : $$0.getId());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        Entity $$1 = this.level.getEntity($$0.getData());
        if ($$1 != null) {
            this.setOwner($$1);
        }
    }

    @Override
    public boolean mayInteract(Level $$0, BlockPos $$1) {
        Entity $$2 = this.getOwner();
        if ($$2 instanceof Player) {
            return $$2.mayInteract($$0, $$1);
        }
        return $$2 == null || $$0.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }
}