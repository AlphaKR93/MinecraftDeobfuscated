/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface LootContextUser {
    default public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    default public void validate(ValidationContext $$0) {
        $$0.validateUser(this);
    }
}