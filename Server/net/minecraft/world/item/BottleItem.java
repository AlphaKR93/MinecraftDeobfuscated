/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BottleItem
extends Item {
    public BottleItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$02, Player $$1, InteractionHand $$2) {
        List $$3 = $$02.getEntitiesOfClass(AreaEffectCloud.class, $$1.getBoundingBox().inflate(2.0), $$0 -> $$0 != null && $$0.isAlive() && $$0.getOwner() instanceof EnderDragon);
        ItemStack $$4 = $$1.getItemInHand($$2);
        if (!$$3.isEmpty()) {
            AreaEffectCloud $$5 = (AreaEffectCloud)$$3.get(0);
            $$5.setRadius($$5.getRadius() - 0.5f);
            $$02.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0f, 1.0f);
            $$02.gameEvent($$1, GameEvent.FLUID_PICKUP, $$1.position());
            return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem($$4, $$1, new ItemStack(Items.DRAGON_BREATH)), $$02.isClientSide());
        }
        BlockHitResult $$6 = BottleItem.getPlayerPOVHitResult($$02, $$1, ClipContext.Fluid.SOURCE_ONLY);
        if (((HitResult)$$6).getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass($$4);
        }
        if (((HitResult)$$6).getType() == HitResult.Type.BLOCK) {
            BlockPos $$7 = $$6.getBlockPos();
            if (!$$02.mayInteract($$1, $$7)) {
                return InteractionResultHolder.pass($$4);
            }
            if ($$02.getFluidState($$7).is(FluidTags.WATER)) {
                $$02.playSound($$1, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0f, 1.0f);
                $$02.gameEvent($$1, GameEvent.FLUID_PICKUP, $$7);
                return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem($$4, $$1, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), $$02.isClientSide());
            }
        }
        return InteractionResultHolder.pass($$4);
    }

    protected ItemStack turnBottleIntoItem(ItemStack $$0, Player $$1, ItemStack $$2) {
        $$1.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.createFilledResult($$0, $$1, $$2);
    }
}