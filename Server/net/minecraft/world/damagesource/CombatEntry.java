/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CombatEntry {
    private final DamageSource source;
    private final int time;
    private final float damage;
    private final float health;
    @Nullable
    private final String location;
    private final float fallDistance;

    public CombatEntry(DamageSource $$0, int $$1, float $$2, float $$3, @Nullable String $$4, float $$5) {
        this.source = $$0;
        this.time = $$1;
        this.damage = $$3;
        this.health = $$2;
        this.location = $$4;
        this.fallDistance = $$5;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public int getTime() {
        return this.time;
    }

    public float getDamage() {
        return this.damage;
    }

    public float getHealthBeforeDamage() {
        return this.health;
    }

    public float getHealthAfterDamage() {
        return this.health - this.damage;
    }

    public boolean isCombatRelated() {
        return this.source.getEntity() instanceof LivingEntity;
    }

    @Nullable
    public String getLocation() {
        return this.location;
    }

    @Nullable
    public Component getAttackerName() {
        return this.getSource().getEntity() == null ? null : this.getSource().getEntity().getDisplayName();
    }

    @Nullable
    public Entity getAttacker() {
        return this.getSource().getEntity();
    }

    public float getFallDistance() {
        if (this.source == DamageSource.OUT_OF_WORLD) {
            return Float.MAX_VALUE;
        }
        return this.fallDistance;
    }
}