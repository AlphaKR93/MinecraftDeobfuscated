/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.item;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CompassItemPropertyFunction
implements ClampedItemPropertyFunction {
    public static final int DEFAULT_ROTATION = 0;
    private final CompassWobble wobble = new CompassWobble();
    private final CompassWobble wobbleRandom = new CompassWobble();
    public final CompassTarget compassTarget;

    public CompassItemPropertyFunction(CompassTarget $$0) {
        this.compassTarget = $$0;
    }

    @Override
    public float unclampedCall(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
        Entity $$4;
        Entity entity = $$4 = $$2 != null ? $$2 : $$0.getEntityRepresentation();
        if ($$4 == null) {
            return 0.0f;
        }
        if (($$1 = this.tryFetchLevelIfMissing($$4, $$1)) == null) {
            return 0.0f;
        }
        return this.getCompassRotation($$0, $$1, $$3, $$4);
    }

    private float getCompassRotation(ItemStack $$0, ClientLevel $$1, int $$2, Entity $$3) {
        GlobalPos $$4 = this.compassTarget.getPos($$1, $$0, $$3);
        long $$5 = $$1.getGameTime();
        if (!this.isValidCompassTargetPos($$3, $$4)) {
            return this.getRandomlySpinningRotation($$2, $$5);
        }
        return this.getRotationTowardsCompassTarget($$3, $$5, $$4.pos());
    }

    private float getRandomlySpinningRotation(int $$0, long $$1) {
        if (this.wobbleRandom.shouldUpdate($$1)) {
            this.wobbleRandom.update($$1, Math.random());
        }
        double $$2 = this.wobbleRandom.rotation + (double)((float)this.hash($$0) / 2.1474836E9f);
        return Mth.positiveModulo((float)$$2, 1.0f);
    }

    private float getRotationTowardsCompassTarget(Entity $$0, long $$1, BlockPos $$2) {
        double $$7;
        Player $$5;
        double $$3 = this.getAngleFromEntityToPos($$0, $$2);
        double $$4 = this.getWrappedVisualRotationY($$0);
        if ($$0 instanceof Player && ($$5 = (Player)$$0).isLocalPlayer()) {
            if (this.wobble.shouldUpdate($$1)) {
                this.wobble.update($$1, 0.5 - ($$4 - 0.25));
            }
            double $$6 = $$3 + this.wobble.rotation;
        } else {
            $$7 = 0.5 - ($$4 - 0.25 - $$3);
        }
        return Mth.positiveModulo((float)$$7, 1.0f);
    }

    @Nullable
    private ClientLevel tryFetchLevelIfMissing(Entity $$0, @Nullable ClientLevel $$1) {
        if ($$1 == null && $$0.level instanceof ClientLevel) {
            return (ClientLevel)$$0.level;
        }
        return $$1;
    }

    private boolean isValidCompassTargetPos(Entity $$0, @Nullable GlobalPos $$1) {
        return $$1 != null && $$1.dimension() == $$0.level.dimension() && !($$1.pos().distToCenterSqr($$0.position()) < (double)1.0E-5f);
    }

    private double getAngleFromEntityToPos(Entity $$0, BlockPos $$1) {
        Vec3 $$2 = Vec3.atCenterOf($$1);
        return Math.atan2((double)($$2.z() - $$0.getZ()), (double)($$2.x() - $$0.getX())) / 6.2831854820251465;
    }

    private double getWrappedVisualRotationY(Entity $$0) {
        return Mth.positiveModulo((double)($$0.getVisualRotationYInDegrees() / 360.0f), 1.0);
    }

    private int hash(int $$0) {
        return $$0 * 1327217883;
    }

    static class CompassWobble {
        double rotation;
        private double deltaRotation;
        private long lastUpdateTick;

        CompassWobble() {
        }

        boolean shouldUpdate(long $$0) {
            return this.lastUpdateTick != $$0;
        }

        void update(long $$0, double $$1) {
            this.lastUpdateTick = $$0;
            double $$2 = $$1 - this.rotation;
            $$2 = Mth.positiveModulo($$2 + 0.5, 1.0) - 0.5;
            this.deltaRotation += $$2 * 0.1;
            this.deltaRotation *= 0.8;
            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0);
        }
    }

    public static interface CompassTarget {
        @Nullable
        public GlobalPos getPos(ClientLevel var1, ItemStack var2, Entity var3);
    }
}