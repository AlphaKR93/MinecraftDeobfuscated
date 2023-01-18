/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class Fireball
extends AbstractHurtingProjectile
implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);

    public Fireball(EntityType<? extends Fireball> $$0, Level $$1) {
        super((EntityType<? extends AbstractHurtingProjectile>)$$0, $$1);
    }

    public Fireball(EntityType<? extends Fireball> $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, Level $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public Fireball(EntityType<? extends Fireball> $$0, LivingEntity $$1, double $$2, double $$3, double $$4, Level $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
    }

    public void setItem(ItemStack $$02) {
        if (!$$02.is(Items.FIRE_CHARGE) || $$02.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, Util.make($$02.copy(), $$0 -> $$0.setCount(1)));
        }
    }

    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getItem() {
        ItemStack $$0 = this.getItemRaw();
        return $$0.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : $$0;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        ItemStack $$1 = this.getItemRaw();
        if (!$$1.isEmpty()) {
            $$0.put("Item", $$1.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        ItemStack $$1 = ItemStack.of($$0.getCompound("Item"));
        this.setItem($$1);
    }
}