/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;

public class EnderDragonPart
extends Entity {
    public final EnderDragon parentMob;
    public final String name;
    private final EntityDimensions size;

    public EnderDragonPart(EnderDragon $$0, String $$1, float $$2, float $$3) {
        super($$0.getType(), $$0.level);
        this.size = EntityDimensions.scalable($$2, $$3);
        this.refreshDimensions();
        this.parentMob = $$0;
        this.name = $$1;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    @Nullable
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        return this.parentMob.hurt(this, $$0, $$1);
    }

    @Override
    public boolean is(Entity $$0) {
        return this == $$0 || this.parentMob == $$0;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityDimensions getDimensions(Pose $$0) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}