/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.mojang.logging.LogUtils
 *  java.lang.Deprecated
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.UUID
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Item
implements FeatureElement,
ItemLike {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Block, Item> BY_BLOCK = Maps.newHashMap();
    protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString((String)"CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString((String)"FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final int MAX_STACK_SIZE = 64;
    public static final int EAT_DURATION = 32;
    public static final int MAX_BAR_WIDTH = 13;
    private final Holder.Reference<Item> builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
    private final Rarity rarity;
    private final int maxStackSize;
    private final int maxDamage;
    private final boolean isFireResistant;
    @Nullable
    private final Item craftingRemainingItem;
    @Nullable
    private String descriptionId;
    @Nullable
    private final FoodProperties foodProperties;
    private final FeatureFlagSet requiredFeatures;

    public static int getId(Item $$0) {
        return $$0 == null ? 0 : BuiltInRegistries.ITEM.getId($$0);
    }

    public static Item byId(int $$0) {
        return BuiltInRegistries.ITEM.byId($$0);
    }

    @Deprecated
    public static Item byBlock(Block $$0) {
        return (Item)BY_BLOCK.getOrDefault((Object)$$0, (Object)Items.AIR);
    }

    public Item(Properties $$0) {
        String $$1;
        this.rarity = $$0.rarity;
        this.craftingRemainingItem = $$0.craftingRemainingItem;
        this.maxDamage = $$0.maxDamage;
        this.maxStackSize = $$0.maxStackSize;
        this.foodProperties = $$0.foodProperties;
        this.isFireResistant = $$0.isFireResistant;
        this.requiredFeatures = $$0.requiredFeatures;
        if (SharedConstants.IS_RUNNING_IN_IDE && !($$1 = this.getClass().getSimpleName()).endsWith("Item")) {
            LOGGER.error("Item classes should end with Item and {} doesn't.", (Object)$$1);
        }
    }

    @Deprecated
    public Holder.Reference<Item> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public void onUseTick(Level $$0, LivingEntity $$1, ItemStack $$2, int $$3) {
    }

    public void onDestroyed(ItemEntity $$0) {
    }

    public void verifyTagAfterLoad(CompoundTag $$0) {
    }

    public boolean canAttackBlock(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public InteractionResult useOn(UseOnContext $$0) {
        return InteractionResult.PASS;
    }

    public float getDestroySpeed(ItemStack $$0, BlockState $$1) {
        return 1.0f;
    }

    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        if (this.isEdible()) {
            ItemStack $$3 = $$1.getItemInHand($$2);
            if ($$1.canEat(this.getFoodProperties().canAlwaysEat())) {
                $$1.startUsingItem($$2);
                return InteractionResultHolder.consume($$3);
            }
            return InteractionResultHolder.fail($$3);
        }
        return InteractionResultHolder.pass($$1.getItemInHand($$2));
    }

    public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
        if (this.isEdible()) {
            return $$2.eat($$1, $$0);
        }
        return $$0;
    }

    public final int getMaxStackSize() {
        return this.maxStackSize;
    }

    public final int getMaxDamage() {
        return this.maxDamage;
    }

    public boolean canBeDepleted() {
        return this.maxDamage > 0;
    }

    public boolean isBarVisible(ItemStack $$0) {
        return $$0.isDamaged();
    }

    public int getBarWidth(ItemStack $$0) {
        return Math.round((float)(13.0f - (float)$$0.getDamageValue() * 13.0f / (float)this.maxDamage));
    }

    public int getBarColor(ItemStack $$0) {
        float $$1 = Math.max((float)0.0f, (float)(((float)this.maxDamage - (float)$$0.getDamageValue()) / (float)this.maxDamage));
        return Mth.hsvToRgb($$1 / 3.0f, 1.0f, 1.0f);
    }

    public boolean overrideStackedOnOther(ItemStack $$0, Slot $$1, ClickAction $$2, Player $$3) {
        return false;
    }

    public boolean overrideOtherStackedOnMe(ItemStack $$0, ItemStack $$1, Slot $$2, ClickAction $$3, Player $$4, SlotAccess $$5) {
        return false;
    }

    public boolean hurtEnemy(ItemStack $$0, LivingEntity $$1, LivingEntity $$2) {
        return false;
    }

    public boolean mineBlock(ItemStack $$0, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
        return false;
    }

    public boolean isCorrectToolForDrops(BlockState $$0) {
        return false;
    }

    public InteractionResult interactLivingEntity(ItemStack $$0, Player $$1, LivingEntity $$2, InteractionHand $$3) {
        return InteractionResult.PASS;
    }

    public Component getDescription() {
        return Component.translatable(this.getDescriptionId());
    }

    public String toString() {
        return BuiltInRegistries.ITEM.getKey(this).getPath();
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
        }
        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public String getDescriptionId(ItemStack $$0) {
        return this.getDescriptionId();
    }

    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }

    @Nullable
    public final Item getCraftingRemainingItem() {
        return this.craftingRemainingItem;
    }

    public boolean hasCraftingRemainingItem() {
        return this.craftingRemainingItem != null;
    }

    public void inventoryTick(ItemStack $$0, Level $$1, Entity $$2, int $$3, boolean $$4) {
    }

    public void onCraftedBy(ItemStack $$0, Level $$1, Player $$2) {
    }

    public boolean isComplex() {
        return false;
    }

    public UseAnim getUseAnimation(ItemStack $$0) {
        return $$0.getItem().isEdible() ? UseAnim.EAT : UseAnim.NONE;
    }

    public int getUseDuration(ItemStack $$0) {
        if ($$0.getItem().isEdible()) {
            return this.getFoodProperties().isFastFood() ? 16 : 32;
        }
        return 0;
    }

    public void releaseUsing(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3) {
    }

    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack $$0) {
        return Optional.empty();
    }

    public Component getName(ItemStack $$0) {
        return Component.translatable(this.getDescriptionId($$0));
    }

    public boolean isFoil(ItemStack $$0) {
        return $$0.isEnchanted();
    }

    public Rarity getRarity(ItemStack $$0) {
        if (!$$0.isEnchanted()) {
            return this.rarity;
        }
        switch (this.rarity) {
            case COMMON: 
            case UNCOMMON: {
                return Rarity.RARE;
            }
            case RARE: {
                return Rarity.EPIC;
            }
        }
        return this.rarity;
    }

    public boolean isEnchantable(ItemStack $$0) {
        return this.getMaxStackSize() == 1 && this.canBeDepleted();
    }

    protected static BlockHitResult getPlayerPOVHitResult(Level $$0, Player $$1, ClipContext.Fluid $$2) {
        float $$3 = $$1.getXRot();
        float $$4 = $$1.getYRot();
        Vec3 $$5 = $$1.getEyePosition();
        float $$6 = Mth.cos(-$$4 * ((float)Math.PI / 180) - (float)Math.PI);
        float $$7 = Mth.sin(-$$4 * ((float)Math.PI / 180) - (float)Math.PI);
        float $$8 = -Mth.cos(-$$3 * ((float)Math.PI / 180));
        float $$9 = Mth.sin(-$$3 * ((float)Math.PI / 180));
        float $$10 = $$7 * $$8;
        float $$11 = $$9;
        float $$12 = $$6 * $$8;
        double $$13 = 5.0;
        Vec3 $$14 = $$5.add((double)$$10 * 5.0, (double)$$11 * 5.0, (double)$$12 * 5.0);
        return $$0.clip(new ClipContext($$5, $$14, ClipContext.Block.OUTLINE, $$2, $$1));
    }

    public int getEnchantmentValue() {
        return 0;
    }

    public boolean isValidRepairItem(ItemStack $$0, ItemStack $$1) {
        return false;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot $$0) {
        return ImmutableMultimap.of();
    }

    public boolean useOnRelease(ItemStack $$0) {
        return false;
    }

    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    public boolean isEdible() {
        return this.foodProperties != null;
    }

    @Nullable
    public FoodProperties getFoodProperties() {
        return this.foodProperties;
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    public boolean isFireResistant() {
        return this.isFireResistant;
    }

    public boolean canBeHurtBy(DamageSource $$0) {
        return !this.isFireResistant || !$$0.isFire();
    }

    @Nullable
    public SoundEvent getEquipSound() {
        return null;
    }

    public boolean canFitInsideContainerItems() {
        return true;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    public static class Properties {
        int maxStackSize = 64;
        int maxDamage;
        @Nullable
        Item craftingRemainingItem;
        Rarity rarity = Rarity.COMMON;
        @Nullable
        FoodProperties foodProperties;
        boolean isFireResistant;
        FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

        public Properties food(FoodProperties $$0) {
            this.foodProperties = $$0;
            return this;
        }

        public Properties stacksTo(int $$0) {
            if (this.maxDamage > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            }
            this.maxStackSize = $$0;
            return this;
        }

        public Properties defaultDurability(int $$0) {
            return this.maxDamage == 0 ? this.durability($$0) : this;
        }

        public Properties durability(int $$0) {
            this.maxDamage = $$0;
            this.maxStackSize = 1;
            return this;
        }

        public Properties craftRemainder(Item $$0) {
            this.craftingRemainingItem = $$0;
            return this;
        }

        public Properties rarity(Rarity $$0) {
            this.rarity = $$0;
            return this;
        }

        public Properties fireResistant() {
            this.isFireResistant = true;
            return this;
        }

        public Properties requiredFeatures(FeatureFlag ... $$0) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset($$0);
            return this;
        }
    }
}