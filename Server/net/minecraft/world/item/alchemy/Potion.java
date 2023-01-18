/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.alchemy;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class Potion {
    @Nullable
    private final String name;
    private final ImmutableList<MobEffectInstance> effects;

    public static Potion byName(String $$0) {
        return BuiltInRegistries.POTION.get(ResourceLocation.tryParse($$0));
    }

    public Potion(MobEffectInstance ... $$0) {
        this((String)null, $$0);
    }

    public Potion(@Nullable String $$0, MobEffectInstance ... $$1) {
        this.name = $$0;
        this.effects = ImmutableList.copyOf((Object[])$$1);
    }

    public String getName(String $$0) {
        return $$0 + (this.name == null ? BuiltInRegistries.POTION.getKey(this).getPath() : this.name);
    }

    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }

    public boolean hasInstantEffects() {
        if (!this.effects.isEmpty()) {
            for (MobEffectInstance $$0 : this.effects) {
                if (!$$0.getEffect().isInstantenous()) continue;
                return true;
            }
        }
        return false;
    }
}