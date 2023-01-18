/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem
extends Item {
    public static final String EFFECTS_TAG = "Effects";
    public static final String EFFECT_ID_TAG = "EffectId";
    public static final String EFFECT_DURATION_TAG = "EffectDuration";
    public static final int DEFAULT_DURATION = 160;

    public SuspiciousStewItem(Item.Properties $$0) {
        super($$0);
    }

    public static void saveMobEffect(ItemStack $$0, MobEffect $$1, int $$2) {
        CompoundTag $$3 = $$0.getOrCreateTag();
        ListTag $$4 = $$3.getList(EFFECTS_TAG, 9);
        CompoundTag $$5 = new CompoundTag();
        $$5.putInt(EFFECT_ID_TAG, MobEffect.getId($$1));
        $$5.putInt(EFFECT_DURATION_TAG, $$2);
        $$4.add($$5);
        $$3.put(EFFECTS_TAG, $$4);
    }

    private static void listPotionEffects(ItemStack $$0, Consumer<MobEffectInstance> $$1) {
        CompoundTag $$2 = $$0.getTag();
        if ($$2 != null && $$2.contains(EFFECTS_TAG, 9)) {
            ListTag $$3 = $$2.getList(EFFECTS_TAG, 10);
            for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                int $$7;
                CompoundTag $$5 = $$3.getCompound($$4);
                if ($$5.contains(EFFECT_DURATION_TAG, 3)) {
                    int $$6 = $$5.getInt(EFFECT_DURATION_TAG);
                } else {
                    $$7 = 160;
                }
                MobEffect $$8 = MobEffect.byId($$5.getInt(EFFECT_ID_TAG));
                if ($$8 == null) continue;
                $$1.accept((Object)new MobEffectInstance($$8, $$7));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        if ($$3.isCreative()) {
            ArrayList $$4 = new ArrayList();
            SuspiciousStewItem.listPotionEffects($$0, (Consumer<MobEffectInstance>)((Consumer)arg_0 -> ((List)$$4).add(arg_0)));
            PotionUtils.addPotionTooltip((List<MobEffectInstance>)$$4, $$2, 1.0f);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
        ItemStack $$3 = super.finishUsingItem($$0, $$1, $$2);
        SuspiciousStewItem.listPotionEffects($$3, (Consumer<MobEffectInstance>)((Consumer)$$2::addEffect));
        if ($$2 instanceof Player && ((Player)$$2).getAbilities().instabuild) {
            return $$3;
        }
        return new ItemStack(Items.BOWL);
    }
}