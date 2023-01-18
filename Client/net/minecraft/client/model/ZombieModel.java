/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieModel<T extends Zombie>
extends AbstractZombieModel<T> {
    public ZombieModel(ModelPart $$0) {
        super($$0);
    }

    @Override
    public boolean isAggressive(T $$0) {
        return ((Mob)$$0).isAggressive();
    }
}