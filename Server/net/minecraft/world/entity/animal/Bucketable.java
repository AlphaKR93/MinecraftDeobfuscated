/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.world.entity.animal;

import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public interface Bucketable {
    public boolean fromBucket();

    public void setFromBucket(boolean var1);

    public void saveToBucketTag(ItemStack var1);

    public void loadFromBucketTag(CompoundTag var1);

    public ItemStack getBucketItemStack();

    public SoundEvent getPickupSound();

    @Deprecated
    public static void saveDefaultDataToBucketTag(Mob $$0, ItemStack $$1) {
        CompoundTag $$2 = $$1.getOrCreateTag();
        if ($$0.hasCustomName()) {
            $$1.setHoverName($$0.getCustomName());
        }
        if ($$0.isNoAi()) {
            $$2.putBoolean("NoAI", $$0.isNoAi());
        }
        if ($$0.isSilent()) {
            $$2.putBoolean("Silent", $$0.isSilent());
        }
        if ($$0.isNoGravity()) {
            $$2.putBoolean("NoGravity", $$0.isNoGravity());
        }
        if ($$0.hasGlowingTag()) {
            $$2.putBoolean("Glowing", $$0.hasGlowingTag());
        }
        if ($$0.isInvulnerable()) {
            $$2.putBoolean("Invulnerable", $$0.isInvulnerable());
        }
        $$2.putFloat("Health", $$0.getHealth());
    }

    @Deprecated
    public static void loadDefaultDataFromBucketTag(Mob $$0, CompoundTag $$1) {
        if ($$1.contains("NoAI")) {
            $$0.setNoAi($$1.getBoolean("NoAI"));
        }
        if ($$1.contains("Silent")) {
            $$0.setSilent($$1.getBoolean("Silent"));
        }
        if ($$1.contains("NoGravity")) {
            $$0.setNoGravity($$1.getBoolean("NoGravity"));
        }
        if ($$1.contains("Glowing")) {
            $$0.setGlowingTag($$1.getBoolean("Glowing"));
        }
        if ($$1.contains("Invulnerable")) {
            $$0.setInvulnerable($$1.getBoolean("Invulnerable"));
        }
        if ($$1.contains("Health", 99)) {
            $$0.setHealth($$1.getFloat("Health"));
        }
    }

    public static <T extends LivingEntity> Optional<InteractionResult> bucketMobPickup(Player $$0, InteractionHand $$1, T $$2) {
        ItemStack $$3 = $$0.getItemInHand($$1);
        if ($$3.getItem() == Items.WATER_BUCKET && $$2.isAlive()) {
            $$2.playSound(((Bucketable)((Object)$$2)).getPickupSound(), 1.0f, 1.0f);
            ItemStack $$4 = ((Bucketable)((Object)$$2)).getBucketItemStack();
            ((Bucketable)((Object)$$2)).saveToBucketTag($$4);
            ItemStack $$5 = ItemUtils.createFilledResult($$3, $$0, $$4, false);
            $$0.setItemInHand($$1, $$5);
            Level $$6 = $$2.level;
            if (!$$6.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)$$0, $$4);
            }
            $$2.discard();
            return Optional.of((Object)((Object)InteractionResult.sidedSuccess($$6.isClientSide)));
        }
        return Optional.empty();
    }
}