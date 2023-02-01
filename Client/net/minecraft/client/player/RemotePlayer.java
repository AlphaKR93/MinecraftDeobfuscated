/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;

public class RemotePlayer
extends AbstractClientPlayer {
    public RemotePlayer(ClientLevel $$0, GameProfile $$1) {
        super($$0, $$1);
        this.maxUpStep = 1.0f;
        this.noPhysics = true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN((double)$$1)) {
            $$1 = 1.0;
        }
        return $$0 < ($$1 *= 64.0 * RemotePlayer.getViewScale()) * $$1;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateEntityAnimation(false);
    }

    @Override
    public void aiStep() {
        float $$4;
        if (this.lerpSteps > 0) {
            double $$0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double $$1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double $$2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            this.setYRot(this.getYRot() + (float)Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot()) / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos($$0, $$1, $$2);
            this.setRot(this.getYRot(), this.getXRot());
        }
        if (this.lerpHeadSteps > 0) {
            this.yHeadRot += (float)(Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }
        this.oBob = this.bob;
        this.updateSwingTime();
        if (!this.onGround || this.isDeadOrDying()) {
            float $$3 = 0.0f;
        } else {
            $$4 = (float)Math.min((double)0.1, (double)this.getDeltaMovement().horizontalDistance());
        }
        this.bob += ($$4 - this.bob) * 0.4f;
        this.level.getProfiler().push("push");
        this.pushEntities();
        this.level.getProfiler().pop();
    }

    @Override
    protected void updatePlayerPose() {
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        Minecraft $$1 = Minecraft.getInstance();
        $$1.gui.getChat().addMessage($$0);
    }
}