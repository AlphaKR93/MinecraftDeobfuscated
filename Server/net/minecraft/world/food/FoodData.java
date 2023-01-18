/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public class FoodData {
    private int foodLevel = 20;
    private float saturationLevel = 5.0f;
    private float exhaustionLevel;
    private int tickTimer;
    private int lastFoodLevel = 20;

    public void eat(int $$0, float $$1) {
        this.foodLevel = Math.min((int)($$0 + this.foodLevel), (int)20);
        this.saturationLevel = Math.min((float)(this.saturationLevel + (float)$$0 * $$1 * 2.0f), (float)this.foodLevel);
    }

    public void eat(Item $$0, ItemStack $$1) {
        if ($$0.isEdible()) {
            FoodProperties $$2 = $$0.getFoodProperties();
            this.eat($$2.getNutrition(), $$2.getSaturationModifier());
        }
    }

    public void tick(Player $$0) {
        boolean $$2;
        Difficulty $$1 = $$0.level.getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0f) {
            this.exhaustionLevel -= 4.0f;
            if (this.saturationLevel > 0.0f) {
                this.saturationLevel = Math.max((float)(this.saturationLevel - 1.0f), (float)0.0f);
            } else if ($$1 != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max((int)(this.foodLevel - 1), (int)0);
            }
        }
        if (($$2 = $$0.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) && this.saturationLevel > 0.0f && $$0.isHurt() && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= 10) {
                float $$3 = Math.min((float)this.saturationLevel, (float)6.0f);
                $$0.heal($$3 / 6.0f);
                this.addExhaustion($$3);
                this.tickTimer = 0;
            }
        } else if ($$2 && this.foodLevel >= 18 && $$0.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                $$0.heal(1.0f);
                this.addExhaustion(6.0f);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if ($$0.getHealth() > 10.0f || $$1 == Difficulty.HARD || $$0.getHealth() > 1.0f && $$1 == Difficulty.NORMAL) {
                    $$0.hurt(DamageSource.STARVE, 1.0f);
                }
                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }

    public void readAdditionalSaveData(CompoundTag $$0) {
        if ($$0.contains("foodLevel", 99)) {
            this.foodLevel = $$0.getInt("foodLevel");
            this.tickTimer = $$0.getInt("foodTickTimer");
            this.saturationLevel = $$0.getFloat("foodSaturationLevel");
            this.exhaustionLevel = $$0.getFloat("foodExhaustionLevel");
        }
    }

    public void addAdditionalSaveData(CompoundTag $$0) {
        $$0.putInt("foodLevel", this.foodLevel);
        $$0.putInt("foodTickTimer", this.tickTimer);
        $$0.putFloat("foodSaturationLevel", this.saturationLevel);
        $$0.putFloat("foodExhaustionLevel", this.exhaustionLevel);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public int getLastFoodLevel() {
        return this.lastFoodLevel;
    }

    public boolean needsFood() {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float $$0) {
        this.exhaustionLevel = Math.min((float)(this.exhaustionLevel + $$0), (float)40.0f);
    }

    public float getExhaustionLevel() {
        return this.exhaustionLevel;
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    public void setFoodLevel(int $$0) {
        this.foodLevel = $$0;
    }

    public void setSaturation(float $$0) {
        this.saturationLevel = $$0;
    }

    public void setExhaustion(float $$0) {
        this.exhaustionLevel = $$0;
    }
}