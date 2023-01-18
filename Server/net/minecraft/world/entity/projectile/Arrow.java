/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Set
 */
package net.minecraft.world.entity.projectile;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class Arrow
extends AbstractArrow {
    private static final int EXPOSED_POTION_DECAY_TIME = 600;
    private static final int NO_EFFECT_COLOR = -1;
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(Arrow.class, EntityDataSerializers.INT);
    private static final byte EVENT_POTION_PUFF = 0;
    private Potion potion = Potions.EMPTY;
    private final Set<MobEffectInstance> effects = Sets.newHashSet();
    private boolean fixedColor;

    public Arrow(EntityType<? extends Arrow> $$0, Level $$1) {
        super((EntityType<? extends AbstractArrow>)$$0, $$1);
    }

    public Arrow(Level $$0, double $$1, double $$2, double $$3) {
        super(EntityType.ARROW, $$1, $$2, $$3, $$0);
    }

    public Arrow(Level $$0, LivingEntity $$1) {
        super(EntityType.ARROW, $$1, $$0);
    }

    public void setEffectsFromItem(ItemStack $$0) {
        if ($$0.is(Items.TIPPED_ARROW)) {
            int $$3;
            this.potion = PotionUtils.getPotion($$0);
            List<MobEffectInstance> $$1 = PotionUtils.getCustomEffects($$0);
            if (!$$1.isEmpty()) {
                for (MobEffectInstance $$2 : $$1) {
                    this.effects.add((Object)new MobEffectInstance($$2));
                }
            }
            if (($$3 = Arrow.getCustomColor($$0)) == -1) {
                this.updateColor();
            } else {
                this.setFixedColor($$3);
            }
        } else if ($$0.is(Items.ARROW)) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, -1);
        }
    }

    public static int getCustomColor(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        if ($$1 != null && $$1.contains("CustomPotionColor", 99)) {
            return $$1.getInt("CustomPotionColor");
        }
        return -1;
    }

    private void updateColor() {
        this.fixedColor = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.entityData.set(ID_EFFECT_COLOR, -1);
        } else {
            this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(MobEffectInstance $$0) {
        this.effects.add((Object)$$0);
        this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }
            } else {
                this.makeParticle(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.level.broadcastEntityEvent(this, (byte)0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(ID_EFFECT_COLOR, -1);
        }
    }

    private void makeParticle(int $$0) {
        int $$1 = this.getColor();
        if ($$1 == -1 || $$0 <= 0) {
            return;
        }
        double $$2 = (double)($$1 >> 16 & 0xFF) / 255.0;
        double $$3 = (double)($$1 >> 8 & 0xFF) / 255.0;
        double $$4 = (double)($$1 >> 0 & 0xFF) / 255.0;
        for (int $$5 = 0; $$5 < $$0; ++$$5) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), $$2, $$3, $$4);
        }
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    private void setFixedColor(int $$0) {
        this.fixedColor = true;
        this.entityData.set(ID_EFFECT_COLOR, $$0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (this.potion != Potions.EMPTY) {
            $$0.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }
        if (this.fixedColor) {
            $$0.putInt("Color", this.getColor());
        }
        if (!this.effects.isEmpty()) {
            ListTag $$1 = new ListTag();
            for (MobEffectInstance $$2 : this.effects) {
                $$1.add($$2.save(new CompoundTag()));
            }
            $$0.put("CustomPotionEffects", $$1);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("Potion", 8)) {
            this.potion = PotionUtils.getPotion($$0);
        }
        for (MobEffectInstance $$1 : PotionUtils.getCustomEffects($$0)) {
            this.addEffect($$1);
        }
        if ($$0.contains("Color", 99)) {
            this.setFixedColor($$0.getInt("Color"));
        } else {
            this.updateColor();
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity $$0) {
        super.doPostHurtEffects($$0);
        Entity $$1 = this.getEffectSource();
        for (MobEffectInstance $$2 : this.potion.getEffects()) {
            $$0.addEffect(new MobEffectInstance($$2.getEffect(), Math.max((int)($$2.getDuration() / 8), (int)1), $$2.getAmplifier(), $$2.isAmbient(), $$2.isVisible()), $$1);
        }
        if (!this.effects.isEmpty()) {
            for (MobEffectInstance $$3 : this.effects) {
                $$0.addEffect($$3, $$1);
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        }
        ItemStack $$0 = new ItemStack(Items.TIPPED_ARROW);
        PotionUtils.setPotion($$0, this.potion);
        PotionUtils.setCustomEffects($$0, this.effects);
        if (this.fixedColor) {
            $$0.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
        }
        return $$0;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 0) {
            int $$1 = this.getColor();
            if ($$1 != -1) {
                double $$2 = (double)($$1 >> 16 & 0xFF) / 255.0;
                double $$3 = (double)($$1 >> 8 & 0xFF) / 255.0;
                double $$4 = (double)($$1 >> 0 & 0xFF) / 255.0;
                for (int $$5 = 0; $$5 < 20; ++$$5) {
                    this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), $$2, $$3, $$4);
                }
            }
        } else {
            super.handleEntityEvent($$0);
        }
    }
}