/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

public interface NumberProvider
extends LootContextUser {
    public float getFloat(LootContext var1);

    default public int getInt(LootContext $$0) {
        return Math.round((float)this.getFloat($$0));
    }

    public LootNumberProviderType getType();
}