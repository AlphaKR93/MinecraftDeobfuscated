/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.animal.horse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public abstract class AbstractChestedHorse
extends AbstractHorse {
    private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData.defineId(AbstractChestedHorse.class, EntityDataSerializers.BOOLEAN);
    public static final int INV_CHEST_COUNT = 15;

    protected AbstractChestedHorse(EntityType<? extends AbstractChestedHorse> $$0, Level $$1) {
        super((EntityType<? extends AbstractHorse>)$$0, $$1);
        this.canGallop = false;
    }

    @Override
    protected void randomizeAttributes(RandomSource $$0) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.generateRandomMaxHealth($$0));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_CHEST, false);
    }

    public static AttributeSupplier.Builder createBaseChestedHorseAttributes() {
        return AbstractChestedHorse.createBaseHorseAttributes().add(Attributes.MOVEMENT_SPEED, 0.175f).add(Attributes.JUMP_STRENGTH, 0.5);
    }

    public boolean hasChest() {
        return this.entityData.get(DATA_ID_CHEST);
    }

    public void setChest(boolean $$0) {
        this.entityData.set(DATA_ID_CHEST, $$0);
    }

    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 17;
        }
        return super.getInventorySize();
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.25;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasChest()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }
            this.setChest(false);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            ListTag $$1 = new ListTag();
            for (int $$2 = 2; $$2 < this.inventory.getContainerSize(); ++$$2) {
                ItemStack $$3 = this.inventory.getItem($$2);
                if ($$3.isEmpty()) continue;
                CompoundTag $$4 = new CompoundTag();
                $$4.putByte("Slot", (byte)$$2);
                $$3.save($$4);
                $$1.add($$4);
            }
            $$0.put("Items", $$1);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setChest($$0.getBoolean("ChestedHorse"));
        this.createInventory();
        if (this.hasChest()) {
            ListTag $$1 = $$0.getList("Items", 10);
            for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
                CompoundTag $$3 = $$1.getCompound($$2);
                int $$4 = $$3.getByte("Slot") & 0xFF;
                if ($$4 < 2 || $$4 >= this.inventory.getContainerSize()) continue;
                this.inventory.setItem($$4, ItemStack.of($$3));
            }
        }
        this.updateContainerEquipment();
    }

    @Override
    public SlotAccess getSlot(int $$0) {
        if ($$0 == 499) {
            return new SlotAccess(){

                @Override
                public ItemStack get() {
                    return AbstractChestedHorse.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
                }

                @Override
                public boolean set(ItemStack $$0) {
                    if ($$0.isEmpty()) {
                        if (AbstractChestedHorse.this.hasChest()) {
                            AbstractChestedHorse.this.setChest(false);
                            AbstractChestedHorse.this.createInventory();
                        }
                        return true;
                    }
                    if ($$0.is(Items.CHEST)) {
                        if (!AbstractChestedHorse.this.hasChest()) {
                            AbstractChestedHorse.this.setChest(true);
                            AbstractChestedHorse.this.createInventory();
                        }
                        return true;
                    }
                    return false;
                }
            };
        }
        return super.getSlot($$0);
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
            if (!this.hasChest() && $$3.is(Items.CHEST)) {
                this.equipChest($$0, $$3);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
        return super.mobInteract($$0, $$1);
    }

    private void equipChest(Player $$0, ItemStack $$1) {
        this.setChest(true);
        this.playChestEquipsSound();
        if (!$$0.getAbilities().instabuild) {
            $$1.shrink(1);
        }
        this.createInventory();
    }

    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.DONKEY_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    public int getInventoryColumns() {
        return 5;
    }
}