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
import net.minecraft.world.entity.monster.Giant;

public class GiantZombieModel
extends AbstractZombieModel<Giant> {
    public GiantZombieModel(ModelPart $$0) {
        super($$0);
    }

    @Override
    public boolean isAggressive(Giant $$0) {
        return false;
    }
}