/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TOLERANCE_LEVEL_ROTATION = 1;
    private final ServerLevel level;
    private final Entity entity;
    private final int updateInterval;
    private final boolean trackDelta;
    private final Consumer<Packet<?>> broadcast;
    private final VecDeltaCodec positionCodec = new VecDeltaCodec();
    private int yRotp;
    private int xRotp;
    private int yHeadRotp;
    private Vec3 ap = Vec3.ZERO;
    private int tickCount;
    private int teleportDelay;
    private List<Entity> lastPassengers = Collections.emptyList();
    private boolean wasRiding;
    private boolean wasOnGround;
    @Nullable
    private List<SynchedEntityData.DataValue<?>> trackedDataValues;

    public ServerEntity(ServerLevel $$0, Entity $$1, int $$2, boolean $$3, Consumer<Packet<?>> $$4) {
        this.level = $$0;
        this.broadcast = $$4;
        this.entity = $$1;
        this.updateInterval = $$2;
        this.trackDelta = $$3;
        this.positionCodec.setBase($$1.trackingPosition());
        this.yRotp = Mth.floor($$1.getYRot() * 256.0f / 360.0f);
        this.xRotp = Mth.floor($$1.getXRot() * 256.0f / 360.0f);
        this.yHeadRotp = Mth.floor($$1.getYHeadRot() * 256.0f / 360.0f);
        this.wasOnGround = $$1.isOnGround();
        this.trackedDataValues = $$1.getEntityData().getNonDefaultValues();
    }

    public void sendChanges() {
        Entity entity;
        List<Entity> $$0 = this.entity.getPassengers();
        if (!$$0.equals(this.lastPassengers)) {
            this.lastPassengers = $$0;
            this.broadcast.accept((Object)new ClientboundSetPassengersPacket(this.entity));
        }
        if ((entity = this.entity) instanceof ItemFrame) {
            ItemFrame $$1 = (ItemFrame)entity;
            if (this.tickCount % 10 == 0) {
                Integer $$3;
                MapItemSavedData $$4;
                ItemStack $$2 = $$1.getItem();
                if ($$2.getItem() instanceof MapItem && ($$4 = MapItem.getSavedData($$3 = MapItem.getMapId($$2), (Level)this.level)) != null) {
                    for (ServerPlayer $$5 : this.level.players()) {
                        $$4.tickCarriedBy($$5, $$2);
                        Packet<?> $$6 = $$4.getUpdatePacket($$3, $$5);
                        if ($$6 == null) continue;
                        $$5.connection.send($$6);
                    }
                }
                this.sendDirtyEntityData();
            }
        }
        if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
            if (this.entity.isPassenger()) {
                boolean $$9;
                int $$7 = Mth.floor(this.entity.getYRot() * 256.0f / 360.0f);
                int $$8 = Mth.floor(this.entity.getXRot() * 256.0f / 360.0f);
                boolean bl = $$9 = Math.abs((int)($$7 - this.yRotp)) >= 1 || Math.abs((int)($$8 - this.xRotp)) >= 1;
                if ($$9) {
                    this.broadcast.accept((Object)new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)$$7, (byte)$$8, this.entity.isOnGround()));
                    this.yRotp = $$7;
                    this.xRotp = $$8;
                }
                this.positionCodec.setBase(this.entity.trackingPosition());
                this.sendDirtyEntityData();
                this.wasRiding = true;
            } else {
                Vec3 $$21;
                double $$22;
                boolean $$16;
                ++this.teleportDelay;
                int $$10 = Mth.floor(this.entity.getYRot() * 256.0f / 360.0f);
                int $$11 = Mth.floor(this.entity.getXRot() * 256.0f / 360.0f);
                Vec3 $$12 = this.entity.trackingPosition();
                boolean $$13 = this.positionCodec.delta($$12).lengthSqr() >= 7.62939453125E-6;
                Packet<ClientGamePacketListener> $$14 = null;
                boolean $$15 = $$13 || this.tickCount % 60 == 0;
                boolean bl = $$16 = Math.abs((int)($$10 - this.yRotp)) >= 1 || Math.abs((int)($$11 - this.xRotp)) >= 1;
                if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
                    boolean $$20;
                    long $$17 = this.positionCodec.encodeX($$12);
                    long $$18 = this.positionCodec.encodeY($$12);
                    long $$19 = this.positionCodec.encodeZ($$12);
                    boolean bl2 = $$20 = $$17 < -32768L || $$17 > 32767L || $$18 < -32768L || $$18 > 32767L || $$19 < -32768L || $$19 > 32767L;
                    if ($$20 || this.teleportDelay > 400 || this.wasRiding || this.wasOnGround != this.entity.isOnGround()) {
                        this.wasOnGround = this.entity.isOnGround();
                        this.teleportDelay = 0;
                        $$14 = new ClientboundTeleportEntityPacket(this.entity);
                    } else if ($$15 && $$16 || this.entity instanceof AbstractArrow) {
                        $$14 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)$$17, (short)$$18, (short)$$19, (byte)$$10, (byte)$$11, this.entity.isOnGround());
                    } else if ($$15) {
                        $$14 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)$$17, (short)$$18, (short)$$19, this.entity.isOnGround());
                    } else if ($$16) {
                        $$14 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)$$10, (byte)$$11, this.entity.isOnGround());
                    }
                }
                if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.tickCount > 0 && (($$22 = ($$21 = this.entity.getDeltaMovement()).distanceToSqr(this.ap)) > 1.0E-7 || $$22 > 0.0 && $$21.lengthSqr() == 0.0)) {
                    this.ap = $$21;
                    this.broadcast.accept((Object)new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
                }
                if ($$14 != null) {
                    this.broadcast.accept($$14);
                }
                this.sendDirtyEntityData();
                if ($$15) {
                    this.positionCodec.setBase($$12);
                }
                if ($$16) {
                    this.yRotp = $$10;
                    this.xRotp = $$11;
                }
                this.wasRiding = false;
            }
            int $$23 = Mth.floor(this.entity.getYHeadRot() * 256.0f / 360.0f);
            if (Math.abs((int)($$23 - this.yHeadRotp)) >= 1) {
                this.broadcast.accept((Object)new ClientboundRotateHeadPacket(this.entity, (byte)$$23));
                this.yHeadRotp = $$23;
            }
            this.entity.hasImpulse = false;
        }
        ++this.tickCount;
        if (this.entity.hurtMarked) {
            this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
            this.entity.hurtMarked = false;
        }
    }

    public void removePairing(ServerPlayer $$0) {
        this.entity.stopSeenByPlayer($$0);
        $$0.connection.send(new ClientboundRemoveEntitiesPacket(this.entity.getId()));
    }

    public void addPairing(ServerPlayer $$0) {
        ArrayList $$1 = new ArrayList();
        this.sendPairingData((Consumer<Packet<ClientGamePacketListener>>)((Consumer)arg_0 -> ((List)$$1).add(arg_0)));
        $$0.connection.send(new ClientboundBundlePacket((Iterable<Packet<ClientGamePacketListener>>)$$1));
        this.entity.startSeenByPlayer($$0);
    }

    public void sendPairingData(Consumer<Packet<ClientGamePacketListener>> $$0) {
        Mob $$9;
        if (this.entity.isRemoved()) {
            LOGGER.warn("Fetching packet for removed entity {}", (Object)this.entity);
        }
        Packet<ClientGamePacketListener> $$1 = this.entity.getAddEntityPacket();
        this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0f / 360.0f);
        $$0.accept($$1);
        if (this.trackedDataValues != null) {
            $$0.accept((Object)new ClientboundSetEntityDataPacket(this.entity.getId(), this.trackedDataValues));
        }
        boolean $$2 = this.trackDelta;
        if (this.entity instanceof LivingEntity) {
            Collection<AttributeInstance> $$3 = ((LivingEntity)this.entity).getAttributes().getSyncableAttributes();
            if (!$$3.isEmpty()) {
                $$0.accept((Object)new ClientboundUpdateAttributesPacket(this.entity.getId(), $$3));
            }
            if (((LivingEntity)this.entity).isFallFlying()) {
                $$2 = true;
            }
        }
        this.ap = this.entity.getDeltaMovement();
        if ($$2 && !(this.entity instanceof LivingEntity)) {
            $$0.accept((Object)new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
        }
        if (this.entity instanceof LivingEntity) {
            ArrayList $$4 = Lists.newArrayList();
            for (Iterator $$5 : EquipmentSlot.values()) {
                ItemStack $$6 = ((LivingEntity)this.entity).getItemBySlot((EquipmentSlot)$$5);
                if ($$6.isEmpty()) continue;
                $$4.add((Object)Pair.of((Object)$$5, (Object)$$6.copy()));
            }
            if (!$$4.isEmpty()) {
                $$0.accept((Object)new ClientboundSetEquipmentPacket(this.entity.getId(), (List<Pair<EquipmentSlot, ItemStack>>)$$4));
            }
        }
        if (this.entity instanceof LivingEntity) {
            LivingEntity $$7 = (LivingEntity)this.entity;
            for (MobEffectInstance $$8 : $$7.getActiveEffects()) {
                $$0.accept((Object)new ClientboundUpdateMobEffectPacket(this.entity.getId(), $$8));
            }
        }
        if (!this.entity.getPassengers().isEmpty()) {
            $$0.accept((Object)new ClientboundSetPassengersPacket(this.entity));
        }
        if (this.entity.isPassenger()) {
            $$0.accept((Object)new ClientboundSetPassengersPacket(this.entity.getVehicle()));
        }
        if (this.entity instanceof Mob && ($$9 = (Mob)this.entity).isLeashed()) {
            $$0.accept((Object)new ClientboundSetEntityLinkPacket($$9, $$9.getLeashHolder()));
        }
    }

    private void sendDirtyEntityData() {
        SynchedEntityData $$0 = this.entity.getEntityData();
        List<SynchedEntityData.DataValue<?>> $$1 = $$0.packDirty();
        if ($$1 != null) {
            this.trackedDataValues = $$0.getNonDefaultValues();
            this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), $$1));
        }
        if (this.entity instanceof LivingEntity) {
            Set<AttributeInstance> $$2 = ((LivingEntity)this.entity).getAttributes().getDirtyAttributes();
            if (!$$2.isEmpty()) {
                this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), (Collection<AttributeInstance>)$$2));
            }
            $$2.clear();
        }
    }

    private void broadcastAndSend(Packet<?> $$0) {
        this.broadcast.accept($$0);
        if (this.entity instanceof ServerPlayer) {
            ((ServerPlayer)this.entity).connection.send($$0);
        }
    }
}