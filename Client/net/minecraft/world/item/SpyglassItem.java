/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class SpyglassItem
extends Item {
    public static final int USE_DURATION = 1200;
    public static final float ZOOM_FOV_MODIFIER = 0.1f;

    public SpyglassItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public int getUseDuration(ItemStack $$0) {
        return 1200;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        $$1.playSound(SoundEvents.SPYGLASS_USE, 1.0f, 1.0f);
        $$1.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.startUsingInstantly($$0, $$1, $$2);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
        this.stopUsing($$2);
        return $$0;
    }

    @Override
    public void releaseUsing(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3) {
        this.stopUsing($$2);
    }

    private void stopUsing(LivingEntity $$0) {
        $$0.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0f, 1.0f);
    }
}