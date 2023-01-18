/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class EntityDamageSource
extends DamageSource {
    protected final Entity entity;
    private boolean isThorns;

    public EntityDamageSource(String $$0, Entity $$1) {
        super($$0);
        this.entity = $$1;
    }

    public EntityDamageSource setThorns() {
        this.isThorns = true;
        return this;
    }

    public boolean isThorns() {
        return this.isThorns;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity $$0) {
        ItemStack itemStack;
        Entity entity = this.entity;
        if (entity instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)entity;
            itemStack = $$1.getMainHandItem();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        ItemStack $$2 = itemStack;
        String $$3 = "death.attack." + this.msgId;
        if (!$$2.isEmpty() && $$2.hasCustomHoverName()) {
            return Component.translatable($$3 + ".item", $$0.getDisplayName(), this.entity.getDisplayName(), $$2.getDisplayName());
        }
        return Component.translatable($$3, $$0.getDisplayName(), this.entity.getDisplayName());
    }

    @Override
    public boolean scalesWithDifficulty() {
        return this.entity instanceof LivingEntity && !(this.entity instanceof Player);
    }

    @Override
    @Nullable
    public Vec3 getSourcePosition() {
        return this.entity.position();
    }

    @Override
    public String toString() {
        return "EntityDamageSource (" + this.entity + ")";
    }
}