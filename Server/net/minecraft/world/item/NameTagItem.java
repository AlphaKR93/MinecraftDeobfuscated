/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class NameTagItem
extends Item {
    public NameTagItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack $$0, Player $$1, LivingEntity $$2, InteractionHand $$3) {
        if ($$0.hasCustomHoverName() && !($$2 instanceof Player)) {
            if (!$$1.level.isClientSide && $$2.isAlive()) {
                $$2.setCustomName($$0.getHoverName());
                if ($$2 instanceof Mob) {
                    ((Mob)$$2).setPersistenceRequired();
                }
                $$0.shrink(1);
            }
            return InteractionResult.sidedSuccess($$1.level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}