/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.item.trading;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class MerchantOffer {
    private final ItemStack baseCostA;
    private final ItemStack costB;
    private final ItemStack result;
    private int uses;
    private final int maxUses;
    private boolean rewardExp = true;
    private int specialPriceDiff;
    private int demand;
    private float priceMultiplier;
    private int xp = 1;

    public MerchantOffer(CompoundTag $$0) {
        this.baseCostA = ItemStack.of($$0.getCompound("buy"));
        this.costB = ItemStack.of($$0.getCompound("buyB"));
        this.result = ItemStack.of($$0.getCompound("sell"));
        this.uses = $$0.getInt("uses");
        this.maxUses = $$0.contains("maxUses", 99) ? $$0.getInt("maxUses") : 4;
        if ($$0.contains("rewardExp", 1)) {
            this.rewardExp = $$0.getBoolean("rewardExp");
        }
        if ($$0.contains("xp", 3)) {
            this.xp = $$0.getInt("xp");
        }
        if ($$0.contains("priceMultiplier", 5)) {
            this.priceMultiplier = $$0.getFloat("priceMultiplier");
        }
        this.specialPriceDiff = $$0.getInt("specialPrice");
        this.demand = $$0.getInt("demand");
    }

    public MerchantOffer(ItemStack $$0, ItemStack $$1, int $$2, int $$3, float $$4) {
        this($$0, ItemStack.EMPTY, $$1, $$2, $$3, $$4);
    }

    public MerchantOffer(ItemStack $$0, ItemStack $$1, ItemStack $$2, int $$3, int $$4, float $$5) {
        this($$0, $$1, $$2, 0, $$3, $$4, $$5);
    }

    public MerchantOffer(ItemStack $$0, ItemStack $$1, ItemStack $$2, int $$3, int $$4, int $$5, float $$6) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, 0);
    }

    public MerchantOffer(ItemStack $$0, ItemStack $$1, ItemStack $$2, int $$3, int $$4, int $$5, float $$6, int $$7) {
        this.baseCostA = $$0;
        this.costB = $$1;
        this.result = $$2;
        this.uses = $$3;
        this.maxUses = $$4;
        this.xp = $$5;
        this.priceMultiplier = $$6;
        this.demand = $$7;
    }

    public ItemStack getBaseCostA() {
        return this.baseCostA;
    }

    public ItemStack getCostA() {
        int $$0 = this.baseCostA.getCount();
        ItemStack $$1 = this.baseCostA.copy();
        int $$2 = Math.max((int)0, (int)Mth.floor((float)($$0 * this.demand) * this.priceMultiplier));
        $$1.setCount(Mth.clamp($$0 + $$2 + this.specialPriceDiff, 1, this.baseCostA.getItem().getMaxStackSize()));
        return $$1;
    }

    public ItemStack getCostB() {
        return this.costB;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void updateDemand() {
        this.demand = this.demand + this.uses - (this.maxUses - this.uses);
    }

    public ItemStack assemble() {
        return this.result.copy();
    }

    public int getUses() {
        return this.uses;
    }

    public void resetUses() {
        this.uses = 0;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void increaseUses() {
        ++this.uses;
    }

    public int getDemand() {
        return this.demand;
    }

    public void addToSpecialPriceDiff(int $$0) {
        this.specialPriceDiff += $$0;
    }

    public void resetSpecialPriceDiff() {
        this.specialPriceDiff = 0;
    }

    public int getSpecialPriceDiff() {
        return this.specialPriceDiff;
    }

    public void setSpecialPriceDiff(int $$0) {
        this.specialPriceDiff = $$0;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getXp() {
        return this.xp;
    }

    public boolean isOutOfStock() {
        return this.uses >= this.maxUses;
    }

    public void setToOutOfStock() {
        this.uses = this.maxUses;
    }

    public boolean needsRestock() {
        return this.uses > 0;
    }

    public boolean shouldRewardExp() {
        return this.rewardExp;
    }

    public CompoundTag createTag() {
        CompoundTag $$0 = new CompoundTag();
        $$0.put("buy", this.baseCostA.save(new CompoundTag()));
        $$0.put("sell", this.result.save(new CompoundTag()));
        $$0.put("buyB", this.costB.save(new CompoundTag()));
        $$0.putInt("uses", this.uses);
        $$0.putInt("maxUses", this.maxUses);
        $$0.putBoolean("rewardExp", this.rewardExp);
        $$0.putInt("xp", this.xp);
        $$0.putFloat("priceMultiplier", this.priceMultiplier);
        $$0.putInt("specialPrice", this.specialPriceDiff);
        $$0.putInt("demand", this.demand);
        return $$0;
    }

    public boolean satisfiedBy(ItemStack $$0, ItemStack $$1) {
        return this.isRequiredItem($$0, this.getCostA()) && $$0.getCount() >= this.getCostA().getCount() && this.isRequiredItem($$1, this.costB) && $$1.getCount() >= this.costB.getCount();
    }

    private boolean isRequiredItem(ItemStack $$0, ItemStack $$1) {
        if ($$1.isEmpty() && $$0.isEmpty()) {
            return true;
        }
        ItemStack $$2 = $$0.copy();
        if ($$2.getItem().canBeDepleted()) {
            $$2.setDamageValue($$2.getDamageValue());
        }
        return ItemStack.isSame($$2, $$1) && (!$$1.hasTag() || $$2.hasTag() && NbtUtils.compareNbt($$1.getTag(), $$2.getTag(), false));
    }

    public boolean take(ItemStack $$0, ItemStack $$1) {
        if (!this.satisfiedBy($$0, $$1)) {
            return false;
        }
        $$0.shrink(this.getCostA().getCount());
        if (!this.getCostB().isEmpty()) {
            $$1.shrink(this.getCostB().getCount());
        }
        return true;
    }
}