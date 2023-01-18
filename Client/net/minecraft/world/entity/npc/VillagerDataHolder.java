/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.npc;

import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;

public interface VillagerDataHolder
extends VariantHolder<VillagerType> {
    public VillagerData getVillagerData();

    public void setVillagerData(VillagerData var1);

    @Override
    default public VillagerType getVariant() {
        return this.getVillagerData().getType();
    }

    @Override
    default public void setVariant(VillagerType $$0) {
        this.setVillagerData(this.getVillagerData().setType($$0));
    }
}