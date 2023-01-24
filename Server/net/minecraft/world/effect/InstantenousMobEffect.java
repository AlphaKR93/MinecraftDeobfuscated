/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  net.minecraft.world.effect.MobEffect
 */
package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class InstantenousMobEffect
extends MobEffect {
    public InstantenousMobEffect(MobEffectCategory $$0, int $$1) {
        super($$0, $$1);
    }

    public boolean isInstantenous() {
        return true;
    }

    public boolean isDurationEffectTick(int $$0, int $$1) {
        return $$0 >= 1;
    }
}