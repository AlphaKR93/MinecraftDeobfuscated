/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class CrossbowItem
extends ProjectileWeaponItem
implements Vanishable {
    private static final String TAG_CHARGED = "Charged";
    private static final String TAG_CHARGED_PROJECTILES = "ChargedProjectiles";
    private static final int MAX_CHARGE_DURATION = 25;
    public static final int DEFAULT_RANGE = 8;
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;
    private static final float START_SOUND_PERCENT = 0.2f;
    private static final float MID_SOUND_PERCENT = 0.5f;
    private static final float ARROW_POWER = 3.15f;
    private static final float FIREWORK_POWER = 1.6f;

    public CrossbowItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return ARROW_OR_FIREWORK;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        if (CrossbowItem.isCharged($$3)) {
            CrossbowItem.performShooting($$0, $$1, $$2, $$3, CrossbowItem.getShootingPower($$3), 1.0f);
            CrossbowItem.setCharged($$3, false);
            return InteractionResultHolder.consume($$3);
        }
        if (!$$1.getProjectile($$3).isEmpty()) {
            if (!CrossbowItem.isCharged($$3)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                $$1.startUsingItem($$2);
            }
            return InteractionResultHolder.consume($$3);
        }
        return InteractionResultHolder.fail($$3);
    }

    private static float getShootingPower(ItemStack $$0) {
        if (CrossbowItem.containsChargedProjectile($$0, Items.FIREWORK_ROCKET)) {
            return 1.6f;
        }
        return 3.15f;
    }

    @Override
    public void releaseUsing(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3) {
        int $$4 = this.getUseDuration($$0) - $$3;
        float $$5 = CrossbowItem.getPowerForTime($$4, $$0);
        if ($$5 >= 1.0f && !CrossbowItem.isCharged($$0) && CrossbowItem.tryLoadProjectiles($$2, $$0)) {
            CrossbowItem.setCharged($$0, true);
            SoundSource $$6 = $$2 instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            $$1.playSound(null, $$2.getX(), $$2.getY(), $$2.getZ(), SoundEvents.CROSSBOW_LOADING_END, $$6, 1.0f, 1.0f / ($$1.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    private static boolean tryLoadProjectiles(LivingEntity $$0, ItemStack $$1) {
        int $$2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, $$1);
        int $$3 = $$2 == 0 ? 1 : 3;
        boolean $$4 = $$0 instanceof Player && ((Player)$$0).getAbilities().instabuild;
        ItemStack $$5 = $$0.getProjectile($$1);
        ItemStack $$6 = $$5.copy();
        for (int $$7 = 0; $$7 < $$3; ++$$7) {
            if ($$7 > 0) {
                $$5 = $$6.copy();
            }
            if ($$5.isEmpty() && $$4) {
                $$5 = new ItemStack(Items.ARROW);
                $$6 = $$5.copy();
            }
            if (CrossbowItem.loadProjectile($$0, $$1, $$5, $$7 > 0, $$4)) continue;
            return false;
        }
        return true;
    }

    private static boolean loadProjectile(LivingEntity $$0, ItemStack $$1, ItemStack $$2, boolean $$3, boolean $$4) {
        ItemStack $$7;
        boolean $$5;
        if ($$2.isEmpty()) {
            return false;
        }
        boolean bl = $$5 = $$4 && $$2.getItem() instanceof ArrowItem;
        if (!($$5 || $$4 || $$3)) {
            ItemStack $$6 = $$2.split(1);
            if ($$2.isEmpty() && $$0 instanceof Player) {
                ((Player)$$0).getInventory().removeItem($$2);
            }
        } else {
            $$7 = $$2.copy();
        }
        CrossbowItem.addChargedProjectile($$1, $$7);
        return true;
    }

    public static boolean isCharged(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        return $$1 != null && $$1.getBoolean(TAG_CHARGED);
    }

    public static void setCharged(ItemStack $$0, boolean $$1) {
        CompoundTag $$2 = $$0.getOrCreateTag();
        $$2.putBoolean(TAG_CHARGED, $$1);
    }

    private static void addChargedProjectile(ItemStack $$0, ItemStack $$1) {
        ListTag $$4;
        CompoundTag $$2 = $$0.getOrCreateTag();
        if ($$2.contains(TAG_CHARGED_PROJECTILES, 9)) {
            ListTag $$3 = $$2.getList(TAG_CHARGED_PROJECTILES, 10);
        } else {
            $$4 = new ListTag();
        }
        CompoundTag $$5 = new CompoundTag();
        $$1.save($$5);
        $$4.add($$5);
        $$2.put(TAG_CHARGED_PROJECTILES, $$4);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack $$0) {
        ListTag $$3;
        ArrayList $$1 = Lists.newArrayList();
        CompoundTag $$2 = $$0.getTag();
        if ($$2 != null && $$2.contains(TAG_CHARGED_PROJECTILES, 9) && ($$3 = $$2.getList(TAG_CHARGED_PROJECTILES, 10)) != null) {
            for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                CompoundTag $$5 = $$3.getCompound($$4);
                $$1.add((Object)ItemStack.of($$5));
            }
        }
        return $$1;
    }

    private static void clearChargedProjectiles(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        if ($$1 != null) {
            ListTag $$2 = $$1.getList(TAG_CHARGED_PROJECTILES, 9);
            $$2.clear();
            $$1.put(TAG_CHARGED_PROJECTILES, $$2);
        }
    }

    public static boolean containsChargedProjectile(ItemStack $$0, Item $$12) {
        return CrossbowItem.getChargedProjectiles($$0).stream().anyMatch($$1 -> $$1.is($$12));
    }

    private static void shootProjectile(Level $$0, LivingEntity $$12, InteractionHand $$2, ItemStack $$3, ItemStack $$4, float $$5, boolean $$6, float $$7, float $$8, float $$9) {
        AbstractArrow $$122;
        if ($$0.isClientSide) {
            return;
        }
        boolean $$10 = $$4.is(Items.FIREWORK_ROCKET);
        if ($$10) {
            FireworkRocketEntity $$11 = new FireworkRocketEntity($$0, $$4, $$12, $$12.getX(), $$12.getEyeY() - (double)0.15f, $$12.getZ(), true);
        } else {
            $$122 = CrossbowItem.getArrow($$0, $$12, $$3, $$4);
            if ($$6 || $$9 != 0.0f) {
                $$122.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
        }
        if ($$12 instanceof CrossbowAttackMob) {
            CrossbowAttackMob $$13 = (CrossbowAttackMob)((Object)$$12);
            $$13.shootCrossbowProjectile($$13.getTarget(), $$3, $$122, $$9);
        } else {
            Vec3 $$14 = $$12.getUpVector(1.0f);
            Quaternionf $$15 = new Quaternionf().setAngleAxis((double)($$9 * ((float)Math.PI / 180)), $$14.x, $$14.y, $$14.z);
            Vec3 $$16 = $$12.getViewVector(1.0f);
            Vector3f $$17 = $$16.toVector3f().rotate((Quaternionfc)$$15);
            ((Projectile)$$122).shoot($$17.x(), $$17.y(), $$17.z(), $$7, $$8);
        }
        $$3.hurtAndBreak($$10 ? 3 : 1, $$12, $$1 -> $$1.broadcastBreakEvent($$2));
        $$0.addFreshEntity($$122);
        $$0.playSound(null, $$12.getX(), $$12.getY(), $$12.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0f, $$5);
    }

    private static AbstractArrow getArrow(Level $$0, LivingEntity $$1, ItemStack $$2, ItemStack $$3) {
        ArrowItem $$4 = (ArrowItem)($$3.getItem() instanceof ArrowItem ? $$3.getItem() : Items.ARROW);
        AbstractArrow $$5 = $$4.createArrow($$0, $$3, $$1);
        if ($$1 instanceof Player) {
            $$5.setCritArrow(true);
        }
        $$5.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        $$5.setShotFromCrossbow(true);
        int $$6 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, $$2);
        if ($$6 > 0) {
            $$5.setPierceLevel((byte)$$6);
        }
        return $$5;
    }

    public static void performShooting(Level $$0, LivingEntity $$1, InteractionHand $$2, ItemStack $$3, float $$4, float $$5) {
        List<ItemStack> $$6 = CrossbowItem.getChargedProjectiles($$3);
        float[] $$7 = CrossbowItem.getShotPitches($$1.getRandom());
        for (int $$8 = 0; $$8 < $$6.size(); ++$$8) {
            boolean $$10;
            ItemStack $$9 = (ItemStack)$$6.get($$8);
            boolean bl = $$10 = $$1 instanceof Player && ((Player)$$1).getAbilities().instabuild;
            if ($$9.isEmpty()) continue;
            if ($$8 == 0) {
                CrossbowItem.shootProjectile($$0, $$1, $$2, $$3, $$9, $$7[$$8], $$10, $$4, $$5, 0.0f);
                continue;
            }
            if ($$8 == 1) {
                CrossbowItem.shootProjectile($$0, $$1, $$2, $$3, $$9, $$7[$$8], $$10, $$4, $$5, -10.0f);
                continue;
            }
            if ($$8 != 2) continue;
            CrossbowItem.shootProjectile($$0, $$1, $$2, $$3, $$9, $$7[$$8], $$10, $$4, $$5, 10.0f);
        }
        CrossbowItem.onCrossbowShot($$0, $$1, $$3);
    }

    private static float[] getShotPitches(RandomSource $$0) {
        boolean $$1 = $$0.nextBoolean();
        return new float[]{1.0f, CrossbowItem.getRandomShotPitch($$1, $$0), CrossbowItem.getRandomShotPitch(!$$1, $$0)};
    }

    private static float getRandomShotPitch(boolean $$0, RandomSource $$1) {
        float $$2 = $$0 ? 0.63f : 0.43f;
        return 1.0f / ($$1.nextFloat() * 0.5f + 1.8f) + $$2;
    }

    private static void onCrossbowShot(Level $$0, LivingEntity $$1, ItemStack $$2) {
        if ($$1 instanceof ServerPlayer) {
            ServerPlayer $$3 = (ServerPlayer)$$1;
            if (!$$0.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger($$3, $$2);
            }
            $$3.awardStat(Stats.ITEM_USED.get($$2.getItem()));
        }
        CrossbowItem.clearChargedProjectiles($$2);
    }

    @Override
    public void onUseTick(Level $$0, LivingEntity $$1, ItemStack $$2, int $$3) {
        if (!$$0.isClientSide) {
            int $$4 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, $$2);
            SoundEvent $$5 = this.getStartSound($$4);
            SoundEvent $$6 = $$4 == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            float $$7 = (float)($$2.getUseDuration() - $$3) / (float)CrossbowItem.getChargeDuration($$2);
            if ($$7 < 0.2f) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }
            if ($$7 >= 0.2f && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), $$5, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
            if ($$7 >= 0.5f && $$6 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), $$6, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack $$0) {
        return CrossbowItem.getChargeDuration($$0) + 3;
    }

    public static int getChargeDuration(ItemStack $$0) {
        int $$1 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, $$0);
        return $$1 == 0 ? 25 : 25 - 5 * $$1;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.CROSSBOW;
    }

    private SoundEvent getStartSound(int $$0) {
        switch ($$0) {
            case 1: {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            }
            case 2: {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            }
            case 3: {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            }
        }
        return SoundEvents.CROSSBOW_LOADING_START;
    }

    private static float getPowerForTime(int $$0, ItemStack $$1) {
        float $$2 = (float)$$0 / (float)CrossbowItem.getChargeDuration($$1);
        if ($$2 > 1.0f) {
            $$2 = 1.0f;
        }
        return $$2;
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        List<ItemStack> $$4 = CrossbowItem.getChargedProjectiles($$0);
        if (!CrossbowItem.isCharged($$0) || $$4.isEmpty()) {
            return;
        }
        ItemStack $$5 = (ItemStack)$$4.get(0);
        $$2.add((Object)Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append($$5.getDisplayName()));
        if ($$3.isAdvanced() && $$5.is(Items.FIREWORK_ROCKET)) {
            ArrayList $$6 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText($$5, $$1, (List<Component>)$$6, $$3);
            if (!$$6.isEmpty()) {
                for (int $$7 = 0; $$7 < $$6.size(); ++$$7) {
                    $$6.set($$7, (Object)Component.literal("  ").append((Component)$$6.get($$7)).withStyle(ChatFormatting.GRAY));
                }
                $$2.addAll((Collection)$$6);
            }
        }
    }

    @Override
    public boolean useOnRelease(ItemStack $$0) {
        return $$0.is(this);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }
}