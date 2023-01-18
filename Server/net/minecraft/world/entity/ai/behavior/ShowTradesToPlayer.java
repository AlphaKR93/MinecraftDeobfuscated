/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class ShowTradesToPlayer
extends Behavior<Villager> {
    private static final int MAX_LOOK_TIME = 900;
    private static final int STARTING_LOOK_TIME = 40;
    @Nullable
    private ItemStack playerItemStack;
    private final List<ItemStack> displayItems = Lists.newArrayList();
    private int cycleCounter;
    private int displayIndex;
    private int lookTime;

    public ShowTradesToPlayer(int $$0, int $$1) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT)), $$0, $$1);
    }

    @Override
    public boolean checkExtraStartConditions(ServerLevel $$0, Villager $$1) {
        Brain<Villager> $$2 = $$1.getBrain();
        if (!$$2.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        }
        LivingEntity $$3 = (LivingEntity)$$2.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return $$3.getType() == EntityType.PLAYER && $$1.isAlive() && $$3.isAlive() && !$$1.isBaby() && $$1.distanceToSqr($$3) <= 17.0;
    }

    @Override
    public boolean canStillUse(ServerLevel $$0, Villager $$1, long $$2) {
        return this.checkExtraStartConditions($$0, $$1) && this.lookTime > 0 && $$1.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void start(ServerLevel $$0, Villager $$1, long $$2) {
        super.start($$0, $$1, $$2);
        this.lookAtTarget($$1);
        this.cycleCounter = 0;
        this.displayIndex = 0;
        this.lookTime = 40;
    }

    @Override
    public void tick(ServerLevel $$0, Villager $$1, long $$2) {
        LivingEntity $$3 = this.lookAtTarget($$1);
        this.findItemsToDisplay($$3, $$1);
        if (!this.displayItems.isEmpty()) {
            this.displayCyclingItems($$1);
        } else {
            ShowTradesToPlayer.clearHeldItem($$1);
            this.lookTime = Math.min((int)this.lookTime, (int)40);
        }
        --this.lookTime;
    }

    @Override
    public void start(ServerLevel $$0, Villager $$1, long $$2) {
        super.stop($$0, $$1, $$2);
        $$1.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        ShowTradesToPlayer.clearHeldItem($$1);
        this.playerItemStack = null;
    }

    private void findItemsToDisplay(LivingEntity $$0, Villager $$1) {
        boolean $$2 = false;
        ItemStack $$3 = $$0.getMainHandItem();
        if (this.playerItemStack == null || !ItemStack.isSame(this.playerItemStack, $$3)) {
            this.playerItemStack = $$3;
            $$2 = true;
            this.displayItems.clear();
        }
        if ($$2 && !this.playerItemStack.isEmpty()) {
            this.updateDisplayItems($$1);
            if (!this.displayItems.isEmpty()) {
                this.lookTime = 900;
                this.displayFirstItem($$1);
            }
        }
    }

    private void displayFirstItem(Villager $$0) {
        ShowTradesToPlayer.displayAsHeldItem($$0, (ItemStack)this.displayItems.get(0));
    }

    private void updateDisplayItems(Villager $$0) {
        Iterator iterator = $$0.getOffers().iterator();
        while (iterator.hasNext()) {
            MerchantOffer $$1 = (MerchantOffer)iterator.next();
            if ($$1.isOutOfStock() || !this.playerItemStackMatchesCostOfOffer($$1)) continue;
            this.displayItems.add((Object)$$1.getResult());
        }
    }

    private boolean playerItemStackMatchesCostOfOffer(MerchantOffer $$0) {
        return ItemStack.isSame(this.playerItemStack, $$0.getCostA()) || ItemStack.isSame(this.playerItemStack, $$0.getCostB());
    }

    private static void clearHeldItem(Villager $$0) {
        $$0.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        $$0.setDropChance(EquipmentSlot.MAINHAND, 0.085f);
    }

    private static void displayAsHeldItem(Villager $$0, ItemStack $$1) {
        $$0.setItemSlot(EquipmentSlot.MAINHAND, $$1);
        $$0.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    private LivingEntity lookAtTarget(Villager $$0) {
        Brain<Villager> $$1 = $$0.getBrain();
        LivingEntity $$2 = (LivingEntity)$$1.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        $$1.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker($$2, true));
        return $$2;
    }

    private void displayCyclingItems(Villager $$0) {
        if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40) {
            ++this.displayIndex;
            this.cycleCounter = 0;
            if (this.displayIndex > this.displayItems.size() - 1) {
                this.displayIndex = 0;
            }
            ShowTradesToPlayer.displayAsHeldItem($$0, (ItemStack)this.displayItems.get(this.displayIndex));
        }
    }
}