/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.OptionalInt
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.trading;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public interface Merchant {
    public void setTradingPlayer(@Nullable Player var1);

    @Nullable
    public Player getTradingPlayer();

    public MerchantOffers getOffers();

    public void overrideOffers(MerchantOffers var1);

    public void notifyTrade(MerchantOffer var1);

    public void notifyTradeUpdated(ItemStack var1);

    public int getVillagerXp();

    public void overrideXp(int var1);

    public boolean showProgressBar();

    public SoundEvent getNotifyTradeSound();

    default public boolean canRestock() {
        return false;
    }

    default public void openTradingScreen(Player $$02, Component $$12, int $$22) {
        MerchantOffers $$4;
        OptionalInt $$3 = $$02.openMenu(new SimpleMenuProvider(($$0, $$1, $$2) -> new MerchantMenu($$0, $$1, this), $$12));
        if ($$3.isPresent() && !($$4 = this.getOffers()).isEmpty()) {
            $$02.sendMerchantOffers($$3.getAsInt(), $$4, $$22, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
        }
    }

    public boolean isClientSide();
}