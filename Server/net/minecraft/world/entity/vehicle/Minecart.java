/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.vehicle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Minecart
extends AbstractMinecart {
    public Minecart(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }

    public Minecart(Level $$0, double $$1, double $$2, double $$3) {
        super(EntityType.MINECART, $$0, $$1, $$2, $$3);
    }

    @Override
    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        if ($$0.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (this.isVehicle()) {
            return InteractionResult.PASS;
        }
        if (!this.level.isClientSide) {
            return $$0.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected Item getDropItem() {
        return Items.MINECART;
    }

    @Override
    public void activateMinecart(int $$0, int $$1, int $$2, boolean $$3) {
        if ($$3) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }
            if (this.getHurtTime() == 0) {
                this.setHurtDir(-this.getHurtDir());
                this.setHurtTime(10);
                this.setDamage(50.0f);
                this.markHurt();
            }
        }
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.RIDEABLE;
    }
}