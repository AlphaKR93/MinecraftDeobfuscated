/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

public class SaddleItem
extends Item {
    public SaddleItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack $$0, Player $$1, LivingEntity $$2, InteractionHand $$3) {
        Saddleable $$4;
        if ($$2 instanceof Saddleable && $$2.isAlive() && !($$4 = (Saddleable)((Object)$$2)).isSaddled() && $$4.isSaddleable()) {
            if (!$$1.level.isClientSide) {
                $$4.equipSaddle(SoundSource.NEUTRAL);
                $$2.level.gameEvent($$2, GameEvent.EQUIP, $$2.position());
                $$0.shrink(1);
            }
            return InteractionResult.sidedSuccess($$1.level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}