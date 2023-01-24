/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  net.minecraft.world.entity.player.Player
 */
package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DyeItem
extends Item {
    private static final Map<DyeColor, DyeItem> ITEM_BY_COLOR = Maps.newEnumMap(DyeColor.class);
    private final DyeColor dyeColor;

    public DyeItem(DyeColor $$0, Item.Properties $$1) {
        super($$1);
        this.dyeColor = $$0;
        ITEM_BY_COLOR.put((Object)$$0, (Object)this);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack $$0, Player $$1, LivingEntity $$2, InteractionHand $$3) {
        Sheep $$4;
        if ($$2 instanceof Sheep && ($$4 = (Sheep)$$2).isAlive() && !$$4.isSheared() && $$4.getColor() != this.dyeColor) {
            $$4.level.playSound($$1, $$4, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
            if (!$$1.level.isClientSide) {
                $$4.setColor(this.dyeColor);
                $$0.shrink(1);
            }
            return InteractionResult.sidedSuccess($$1.level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public static DyeItem byColor(DyeColor $$0) {
        return (DyeItem)ITEM_BY_COLOR.get((Object)$$0);
    }
}