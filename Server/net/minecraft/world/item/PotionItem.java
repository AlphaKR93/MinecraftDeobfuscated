/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionItem
extends Item {
    private static final int DRINK_DURATION = 32;

    public PotionItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
        Player $$3;
        Player player = $$3 = $$2 instanceof Player ? (Player)$$2 : null;
        if ($$3 instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)$$3, $$0);
        }
        if (!$$1.isClientSide) {
            List<MobEffectInstance> $$4 = PotionUtils.getMobEffects($$0);
            for (MobEffectInstance $$5 : $$4) {
                if ($$5.getEffect().isInstantenous()) {
                    $$5.getEffect().applyInstantenousEffect($$3, $$3, $$2, $$5.getAmplifier(), 1.0);
                    continue;
                }
                $$2.addEffect(new MobEffectInstance($$5));
            }
        }
        if ($$3 != null) {
            $$3.awardStat(Stats.ITEM_USED.get(this));
            if (!$$3.getAbilities().instabuild) {
                $$0.shrink(1);
            }
        }
        if ($$3 == null || !$$3.getAbilities().instabuild) {
            if ($$0.isEmpty()) {
                return new ItemStack((ItemLike)Items.GLASS_BOTTLE);
            }
            if ($$3 != null) {
                $$3.getInventory().add(new ItemStack((ItemLike)Items.GLASS_BOTTLE));
            }
        }
        $$2.gameEvent(GameEvent.DRINK);
        return $$0;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        Player $$3 = $$0.getPlayer();
        ItemStack $$4 = $$0.getItemInHand();
        BlockState $$5 = $$1.getBlockState($$2);
        if ($$0.getClickedFace() != Direction.DOWN && $$5.is(BlockTags.CONVERTABLE_TO_MUD) && PotionUtils.getPotion($$4) == Potions.WATER) {
            $$1.playSound(null, $$2, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$3.setItemInHand($$0.getHand(), ItemUtils.createFilledResult($$4, $$3, new ItemStack((ItemLike)Items.GLASS_BOTTLE)));
            $$3.awardStat(Stats.ITEM_USED.get($$4.getItem()));
            if (!$$1.isClientSide) {
                ServerLevel $$6 = (ServerLevel)$$1;
                for (int $$7 = 0; $$7 < 5; ++$$7) {
                    $$6.sendParticles(ParticleTypes.SPLASH, (double)$$2.getX() + $$1.random.nextDouble(), $$2.getY() + 1, (double)$$2.getZ() + $$1.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                }
            }
            $$1.playSound(null, $$2, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$1.gameEvent(null, GameEvent.FLUID_PLACE, $$2);
            $$1.setBlockAndUpdate($$2, Blocks.MUD.defaultBlockState());
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(ItemStack $$0) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        return ItemUtils.startUsingInstantly($$0, $$1, $$2);
    }

    @Override
    public String getDescriptionId(ItemStack $$0) {
        return PotionUtils.getPotion($$0).getName(this.getDescriptionId() + ".effect.");
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        PotionUtils.addPotionTooltip($$0, $$2, 1.0f);
    }

    @Override
    public boolean isFoil(ItemStack $$0) {
        return super.isFoil($$0) || !PotionUtils.getMobEffects($$0).isEmpty();
    }
}