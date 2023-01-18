/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CombatTracker {
    public static final int RESET_DAMAGE_STATUS_TIME = 100;
    public static final int RESET_COMBAT_STATUS_TIME = 300;
    private final List<CombatEntry> entries = Lists.newArrayList();
    private final LivingEntity mob;
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;
    @Nullable
    private String nextLocation;

    public CombatTracker(LivingEntity $$0) {
        this.mob = $$0;
    }

    public void prepareForDamage() {
        this.resetPreparedStatus();
        Optional<BlockPos> $$0 = this.mob.getLastClimbablePos();
        if ($$0.isPresent()) {
            BlockState $$1 = this.mob.level.getBlockState((BlockPos)$$0.get());
            this.nextLocation = $$1.is(Blocks.LADDER) || $$1.is(BlockTags.TRAPDOORS) ? "ladder" : ($$1.is(Blocks.VINE) ? "vines" : ($$1.is(Blocks.WEEPING_VINES) || $$1.is(Blocks.WEEPING_VINES_PLANT) ? "weeping_vines" : ($$1.is(Blocks.TWISTING_VINES) || $$1.is(Blocks.TWISTING_VINES_PLANT) ? "twisting_vines" : ($$1.is(Blocks.SCAFFOLDING) ? "scaffolding" : "other_climbable"))));
        } else if (this.mob.isInWater()) {
            this.nextLocation = "water";
        }
    }

    public void recordDamage(DamageSource $$0, float $$1, float $$2) {
        this.recheckStatus();
        this.prepareForDamage();
        CombatEntry $$3 = new CombatEntry($$0, this.mob.tickCount, $$1, $$2, this.nextLocation, this.mob.fallDistance);
        this.entries.add((Object)$$3);
        this.lastDamageTime = this.mob.tickCount;
        this.takingDamage = true;
        if ($$3.isCombatRelated() && !this.inCombat && this.mob.isAlive()) {
            this.inCombat = true;
            this.combatEndTime = this.combatStartTime = this.mob.tickCount;
            this.mob.onEnterCombat();
        }
    }

    public Component getDeathMessage() {
        Component $$16;
        if (this.entries.isEmpty()) {
            return Component.translatable("death.attack.generic", this.mob.getDisplayName());
        }
        CombatEntry $$0 = this.getMostSignificantFall();
        CombatEntry $$1 = (CombatEntry)this.entries.get(this.entries.size() - 1);
        Component $$2 = $$1.getAttackerName();
        Entity $$3 = $$1.getSource().getEntity();
        if ($$0 != null && $$1.getSource() == DamageSource.FALL) {
            Component $$4 = $$0.getAttackerName();
            if ($$0.getSource() == DamageSource.FALL || $$0.getSource() == DamageSource.OUT_OF_WORLD) {
                MutableComponent $$5 = Component.translatable("death.fell.accident." + this.getFallLocation($$0), this.mob.getDisplayName());
            } else if ($$4 != null && !$$4.equals($$2)) {
                ItemStack $$8;
                Entity $$6 = $$0.getSource().getEntity();
                if ($$6 instanceof LivingEntity) {
                    LivingEntity $$7 = (LivingEntity)$$6;
                    v0 = $$7.getMainHandItem();
                } else {
                    v0 = $$8 = ItemStack.EMPTY;
                }
                if (!$$8.isEmpty() && $$8.hasCustomHoverName()) {
                    MutableComponent $$9 = Component.translatable("death.fell.assist.item", this.mob.getDisplayName(), $$4, $$8.getDisplayName());
                } else {
                    MutableComponent $$10 = Component.translatable("death.fell.assist", this.mob.getDisplayName(), $$4);
                }
            } else if ($$2 != null) {
                ItemStack $$12;
                if ($$3 instanceof LivingEntity) {
                    LivingEntity $$11 = (LivingEntity)$$3;
                    v1 = $$11.getMainHandItem();
                } else {
                    v1 = $$12 = ItemStack.EMPTY;
                }
                if (!$$12.isEmpty() && $$12.hasCustomHoverName()) {
                    MutableComponent $$13 = Component.translatable("death.fell.finish.item", this.mob.getDisplayName(), $$2, $$12.getDisplayName());
                } else {
                    MutableComponent $$14 = Component.translatable("death.fell.finish", this.mob.getDisplayName(), $$2);
                }
            } else {
                MutableComponent $$15 = Component.translatable("death.fell.killer", this.mob.getDisplayName());
            }
        } else {
            $$16 = $$1.getSource().getLocalizedDeathMessage(this.mob);
        }
        return $$16;
    }

    @Nullable
    public LivingEntity getKiller() {
        LivingEntity $$0 = null;
        Player $$1 = null;
        float $$2 = 0.0f;
        float $$3 = 0.0f;
        for (CombatEntry $$4 : this.entries) {
            Entity entity = $$4.getSource().getEntity();
            if (entity instanceof Player) {
                Player $$5 = (Player)entity;
                if ($$1 == null || $$4.getDamage() > $$3) {
                    $$3 = $$4.getDamage();
                    $$1 = $$5;
                }
            }
            if (!((entity = $$4.getSource().getEntity()) instanceof LivingEntity)) continue;
            LivingEntity $$6 = (LivingEntity)entity;
            if ($$0 != null && !($$4.getDamage() > $$2)) continue;
            $$2 = $$4.getDamage();
            $$0 = $$6;
        }
        if ($$1 != null && $$3 >= $$2 / 3.0f) {
            return $$1;
        }
        return $$0;
    }

    @Nullable
    private CombatEntry getMostSignificantFall() {
        CombatEntry $$0 = null;
        CombatEntry $$1 = null;
        float $$2 = 0.0f;
        float $$3 = 0.0f;
        for (int $$4 = 0; $$4 < this.entries.size(); ++$$4) {
            CombatEntry $$6;
            CombatEntry $$5 = (CombatEntry)this.entries.get($$4);
            CombatEntry combatEntry = $$6 = $$4 > 0 ? (CombatEntry)this.entries.get($$4 - 1) : null;
            if (($$5.getSource() == DamageSource.FALL || $$5.getSource() == DamageSource.OUT_OF_WORLD) && $$5.getFallDistance() > 0.0f && ($$0 == null || $$5.getFallDistance() > $$3)) {
                $$0 = $$4 > 0 ? $$6 : $$5;
                $$3 = $$5.getFallDistance();
            }
            if ($$5.getLocation() == null || $$1 != null && !($$5.getDamage() > $$2)) continue;
            $$1 = $$5;
            $$2 = $$5.getDamage();
        }
        if ($$3 > 5.0f && $$0 != null) {
            return $$0;
        }
        if ($$2 > 5.0f && $$1 != null) {
            return $$1;
        }
        return null;
    }

    private String getFallLocation(CombatEntry $$0) {
        return $$0.getLocation() == null ? "generic" : $$0.getLocation();
    }

    public boolean isTakingDamage() {
        this.recheckStatus();
        return this.takingDamage;
    }

    public boolean isInCombat() {
        this.recheckStatus();
        return this.inCombat;
    }

    public int getCombatDuration() {
        if (this.inCombat) {
            return this.mob.tickCount - this.combatStartTime;
        }
        return this.combatEndTime - this.combatStartTime;
    }

    private void resetPreparedStatus() {
        this.nextLocation = null;
    }

    public void recheckStatus() {
        int $$0;
        int n = $$0 = this.inCombat ? 300 : 100;
        if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > $$0)) {
            boolean $$1 = this.inCombat;
            this.takingDamage = false;
            this.inCombat = false;
            this.combatEndTime = this.mob.tickCount;
            if ($$1) {
                this.mob.onLeaveCombat();
            }
            this.entries.clear();
        }
    }

    public LivingEntity getMob() {
        return this.mob;
    }

    @Nullable
    public CombatEntry getLastEntry() {
        if (this.entries.isEmpty()) {
            return null;
        }
        return (CombatEntry)this.entries.get(this.entries.size() - 1);
    }

    public int getKillerId() {
        LivingEntity $$0 = this.getKiller();
        return $$0 == null ? -1 : $$0.getId();
    }
}