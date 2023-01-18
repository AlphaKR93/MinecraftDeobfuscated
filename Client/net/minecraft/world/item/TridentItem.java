/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TridentItem
extends Item
implements Vanishable {
    public static final int THROW_THRESHOLD_TIME = 10;
    public static final float BASE_DAMAGE = 8.0f;
    public static final float SHOOT_POWER = 2.5f;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public TridentItem(Item.Properties $$0) {
        super($$0);
        ImmutableMultimap.Builder $$1 = ImmutableMultimap.builder();
        $$1.put((Object)Attributes.ATTACK_DAMAGE, (Object)new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0, AttributeModifier.Operation.ADDITION));
        $$1.put((Object)Attributes.ATTACK_SPEED, (Object)new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)-2.9f, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = $$1.build();
    }

    @Override
    public boolean canAttackBlock(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        return !$$3.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack $$0) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack $$0, Level $$12, LivingEntity $$2, int $$3) {
        if (!($$2 instanceof Player)) {
            return;
        }
        Player $$4 = (Player)$$2;
        int $$5 = this.getUseDuration($$0) - $$3;
        if ($$5 < 10) {
            return;
        }
        int $$6 = EnchantmentHelper.getRiptide($$0);
        if ($$6 > 0 && !$$4.isInWaterOrRain()) {
            return;
        }
        if (!$$12.isClientSide) {
            $$0.hurtAndBreak(1, $$4, $$1 -> $$1.broadcastBreakEvent($$2.getUsedItemHand()));
            if ($$6 == 0) {
                ThrownTrident $$7 = new ThrownTrident($$12, (LivingEntity)$$4, $$0);
                $$7.shootFromRotation($$4, $$4.getXRot(), $$4.getYRot(), 0.0f, 2.5f + (float)$$6 * 0.5f, 1.0f);
                if ($$4.getAbilities().instabuild) {
                    $$7.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                $$12.addFreshEntity($$7);
                $$12.playSound(null, $$7, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0f, 1.0f);
                if (!$$4.getAbilities().instabuild) {
                    $$4.getInventory().removeItem($$0);
                }
            }
        }
        $$4.awardStat(Stats.ITEM_USED.get(this));
        if ($$6 > 0) {
            SoundEvent $$18;
            float $$8 = $$4.getYRot();
            float $$9 = $$4.getXRot();
            float $$10 = -Mth.sin($$8 * ((float)Math.PI / 180)) * Mth.cos($$9 * ((float)Math.PI / 180));
            float $$11 = -Mth.sin($$9 * ((float)Math.PI / 180));
            float $$122 = Mth.cos($$8 * ((float)Math.PI / 180)) * Mth.cos($$9 * ((float)Math.PI / 180));
            float $$13 = Mth.sqrt($$10 * $$10 + $$11 * $$11 + $$122 * $$122);
            float $$14 = 3.0f * ((1.0f + (float)$$6) / 4.0f);
            $$4.push($$10 *= $$14 / $$13, $$11 *= $$14 / $$13, $$122 *= $$14 / $$13);
            $$4.startAutoSpinAttack(20);
            if ($$4.isOnGround()) {
                float $$15 = 1.1999999f;
                $$4.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
            }
            if ($$6 >= 3) {
                SoundEvent $$16 = SoundEvents.TRIDENT_RIPTIDE_3;
            } else if ($$6 == 2) {
                SoundEvent $$17 = SoundEvents.TRIDENT_RIPTIDE_2;
            } else {
                $$18 = SoundEvents.TRIDENT_RIPTIDE_1;
            }
            $$12.playSound(null, $$4, $$18, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        if ($$3.getDamageValue() >= $$3.getMaxDamage() - 1) {
            return InteractionResultHolder.fail($$3);
        }
        if (EnchantmentHelper.getRiptide($$3) > 0 && !$$1.isInWaterOrRain()) {
            return InteractionResultHolder.fail($$3);
        }
        $$1.startUsingItem($$2);
        return InteractionResultHolder.consume($$3);
    }

    @Override
    public boolean hurtEnemy(ItemStack $$02, LivingEntity $$1, LivingEntity $$2) {
        $$02.hurtAndBreak(1, $$2, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack $$02, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
        if ((double)$$2.getDestroySpeed($$1, $$3) != 0.0) {
            $$02.hurtAndBreak(2, $$4, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot $$0) {
        if ($$0 == EquipmentSlot.MAINHAND) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers($$0);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}