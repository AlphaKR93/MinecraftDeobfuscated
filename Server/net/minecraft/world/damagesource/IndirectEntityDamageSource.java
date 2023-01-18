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
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class IndirectEntityDamageSource
extends EntityDamageSource {
    @Nullable
    private final Entity cause;

    public IndirectEntityDamageSource(String $$0, Entity $$1, @Nullable Entity $$2) {
        super($$0, $$1);
        this.cause = $$2;
    }

    @Override
    @Nullable
    public Entity getDirectEntity() {
        return this.entity;
    }

    @Override
    @Nullable
    public Entity getEntity() {
        return this.cause;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity $$0) {
        ItemStack itemStack;
        Component $$1 = this.cause == null ? this.entity.getDisplayName() : this.cause.getDisplayName();
        Entity entity = this.cause;
        if (entity instanceof LivingEntity) {
            LivingEntity $$2 = (LivingEntity)entity;
            itemStack = $$2.getMainHandItem();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        ItemStack $$3 = itemStack;
        String $$4 = "death.attack." + this.msgId;
        if (!$$3.isEmpty() && $$3.hasCustomHoverName()) {
            String $$5 = $$4 + ".item";
            return Component.translatable($$5, $$0.getDisplayName(), $$1, $$3.getDisplayName());
        }
        return Component.translatable($$4, $$0.getDisplayName(), $$1);
    }
}