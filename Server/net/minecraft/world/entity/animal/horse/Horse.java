/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;

public class Horse
extends AbstractHorse
implements VariantHolder<Variant> {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString((String)"556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.INT);

    public Horse(EntityType<? extends Horse> $$0, Level $$1) {
        super((EntityType<? extends AbstractHorse>)$$0, $$1);
    }

    @Override
    protected void randomizeAttributes(RandomSource $$0) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.generateRandomMaxHealth($$0));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed($$0));
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength($$0));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Variant", this.getTypeVariant());
        if (!this.inventory.getItem(1).isEmpty()) {
            $$0.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
        }
    }

    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }

    private void setArmor(ItemStack $$0) {
        this.setItemSlot(EquipmentSlot.CHEST, $$0);
        this.setDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        ItemStack $$1;
        super.readAdditionalSaveData($$0);
        this.setTypeVariant($$0.getInt("Variant"));
        if ($$0.contains("ArmorItem", 10) && !($$1 = ItemStack.of($$0.getCompound("ArmorItem"))).isEmpty() && this.isArmor($$1)) {
            this.inventory.setItem(1, $$1);
        }
        this.updateContainerEquipment();
    }

    private void setTypeVariant(int $$0) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, $$0);
    }

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariantAndMarkings(Variant $$0, Markings $$1) {
        this.setTypeVariant($$0.getId() & 0xFF | $$1.getId() << 8 & 0xFF00);
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(this.getTypeVariant() & 0xFF);
    }

    @Override
    public void setVariant(Variant $$0) {
        this.setTypeVariant($$0.getId() & 0xFF | this.getTypeVariant() & 0xFFFFFF00);
    }

    public Markings getMarkings() {
        return Markings.byId((this.getTypeVariant() & 0xFF00) >> 8);
    }

    @Override
    protected void updateContainerEquipment() {
        if (this.level.isClientSide) {
            return;
        }
        super.updateContainerEquipment();
        this.setArmorEquipment(this.inventory.getItem(1));
        this.setDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    private void setArmorEquipment(ItemStack $$0) {
        this.setArmor($$0);
        if (!this.level.isClientSide) {
            int $$1;
            this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            if (this.isArmor($$0) && ($$1 = ((HorseArmorItem)$$0.getItem()).getProtection()) != 0) {
                this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)$$1, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void containerChanged(Container $$0) {
        ItemStack $$1 = this.getArmor();
        super.containerChanged($$0);
        ItemStack $$2 = this.getArmor();
        if (this.tickCount > 20 && this.isArmor($$2) && $$1 != $$2) {
            this.playSound(SoundEvents.HORSE_ARMOR, 0.5f, 1.0f);
        }
    }

    @Override
    protected void playGallopSound(SoundType $$0) {
        super.playGallopSound($$0);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.HORSE_BREATHE, $$0.getVolume() * 0.6f, $$0.getPitch());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.HORSE_ANGRY;
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        boolean $$2;
        boolean bl = $$2 = !this.isBaby() && this.isTamed() && $$0.isSecondaryUseActive();
        if (this.isVehicle() || $$2) {
            return super.mobInteract($$0, $$1);
        }
        ItemStack $$3 = $$0.getItemInHand($$1);
        if (!$$3.isEmpty()) {
            if (this.isFood($$3)) {
                return this.fedFood($$0, $$3);
            }
            if (!this.isTamed()) {
                this.makeMad();
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
        return super.mobInteract($$0, $$1);
    }

    @Override
    public boolean canMate(Animal $$0) {
        if ($$0 == this) {
            return false;
        }
        if ($$0 instanceof Donkey || $$0 instanceof Horse) {
            return this.canParent() && ((AbstractHorse)$$0).canParent();
        }
        return false;
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        if ($$1 instanceof Donkey) {
            Mule $$2 = EntityType.MULE.create($$0);
            if ($$2 != null) {
                this.setOffspringAttributes($$1, $$2);
            }
            return $$2;
        }
        Horse $$3 = (Horse)$$1;
        Horse $$4 = EntityType.HORSE.create($$0);
        if ($$4 != null) {
            Markings $$12;
            Variant $$8;
            int $$5 = this.random.nextInt(9);
            if ($$5 < 4) {
                Variant $$6 = this.getVariant();
            } else if ($$5 < 8) {
                Variant $$7 = $$3.getVariant();
            } else {
                $$8 = Util.getRandom(Variant.values(), this.random);
            }
            int $$9 = this.random.nextInt(5);
            if ($$9 < 2) {
                Markings $$10 = this.getMarkings();
            } else if ($$9 < 4) {
                Markings $$11 = $$3.getMarkings();
            } else {
                $$12 = Util.getRandom(Markings.values(), this.random);
            }
            $$4.setVariantAndMarkings($$8, $$12);
            this.setOffspringAttributes($$1, $$4);
        }
        return $$4;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean isArmor(ItemStack $$0) {
        return $$0.getItem() instanceof HorseArmorItem;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        Variant $$7;
        RandomSource $$5 = $$0.getRandom();
        if ($$3 instanceof HorseGroupData) {
            Variant $$6 = ((HorseGroupData)$$3).variant;
        } else {
            $$7 = Util.getRandom(Variant.values(), $$5);
            $$3 = new HorseGroupData($$7);
        }
        this.setVariantAndMarkings($$7, Util.getRandom(Markings.values(), $$5));
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    public static class HorseGroupData
    extends AgeableMob.AgeableMobGroupData {
        public final Variant variant;

        public HorseGroupData(Variant $$0) {
            super(true);
            this.variant = $$0;
        }
    }
}