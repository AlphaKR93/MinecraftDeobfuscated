/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Deprecated
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.text.DecimalFormat
 *  java.text.DecimalFormatSymbols
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  java.util.function.UnaryOperator
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.AdventureModeCheck;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.slf4j.Logger;

public final class ItemStack {
    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter($$0 -> $$0.item), (App)Codec.INT.fieldOf("Count").forGetter($$0 -> $$0.count), (App)CompoundTag.CODEC.optionalFieldOf("tag").forGetter($$0 -> Optional.ofNullable((Object)$$0.tag))).apply((Applicative)$$02, ItemStack::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemStack EMPTY = new ItemStack((ItemLike)null);
    public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), $$0 -> $$0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance((Locale)Locale.ROOT)));
    public static final String TAG_ENCH = "Enchantments";
    public static final String TAG_DISPLAY = "display";
    public static final String TAG_DISPLAY_NAME = "Name";
    public static final String TAG_LORE = "Lore";
    public static final String TAG_DAMAGE = "Damage";
    public static final String TAG_COLOR = "color";
    private static final String TAG_UNBREAKABLE = "Unbreakable";
    private static final String TAG_REPAIR_COST = "RepairCost";
    private static final String TAG_CAN_DESTROY_BLOCK_LIST = "CanDestroy";
    private static final String TAG_CAN_PLACE_ON_BLOCK_LIST = "CanPlaceOn";
    private static final String TAG_HIDE_FLAGS = "HideFlags";
    private static final Component DISABLED_ITEM_TOOLTIP = Component.translatable("item.disabled").withStyle(ChatFormatting.RED);
    private static final int DONT_HIDE_TOOLTIP = 0;
    private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
    private int count;
    private int popTime;
    @Deprecated
    private final Item item;
    @Nullable
    private CompoundTag tag;
    private boolean emptyCacheFlag;
    @Nullable
    private Entity entityRepresentation;
    @Nullable
    private AdventureModeCheck adventureBreakCheck;
    @Nullable
    private AdventureModeCheck adventurePlaceCheck;

    public Optional<TooltipComponent> getTooltipImage() {
        return this.getItem().getTooltipImage(this);
    }

    public ItemStack(ItemLike $$0) {
        this($$0, 1);
    }

    public ItemStack(Holder<Item> $$0) {
        this($$0.value(), 1);
    }

    private ItemStack(ItemLike $$0, int $$1, Optional<CompoundTag> $$2) {
        this($$0, $$1);
        $$2.ifPresent(this::setTag);
    }

    public ItemStack(Holder<Item> $$0, int $$1) {
        this($$0.value(), $$1);
    }

    public ItemStack(ItemLike $$0, int $$1) {
        this.item = $$0 == null ? null : $$0.asItem();
        this.count = $$1;
        if (this.item != null && this.item.canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
        this.updateEmptyCacheFlag();
    }

    private void updateEmptyCacheFlag() {
        this.emptyCacheFlag = false;
        this.emptyCacheFlag = this.isEmpty();
    }

    private ItemStack(CompoundTag $$0) {
        this.item = BuiltInRegistries.ITEM.get(new ResourceLocation($$0.getString("id")));
        this.count = $$0.getByte("Count");
        if ($$0.contains("tag", 10)) {
            this.tag = $$0.getCompound("tag");
            this.getItem().verifyTagAfterLoad(this.tag);
        }
        if (this.getItem().canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
        this.updateEmptyCacheFlag();
    }

    public static ItemStack of(CompoundTag $$0) {
        try {
            return new ItemStack($$0);
        }
        catch (RuntimeException $$1) {
            LOGGER.debug("Tried to load invalid item: {}", (Object)$$0, (Object)$$1);
            return EMPTY;
        }
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        }
        if (this.getItem() == null || this.is(Items.AIR)) {
            return true;
        }
        return this.count <= 0;
    }

    public boolean isItemEnabled(FeatureFlagSet $$0) {
        return this.isEmpty() || this.getItem().isEnabled($$0);
    }

    public ItemStack split(int $$0) {
        int $$1 = Math.min((int)$$0, (int)this.count);
        ItemStack $$2 = this.copy();
        $$2.setCount($$1);
        this.shrink($$1);
        return $$2;
    }

    public Item getItem() {
        return this.emptyCacheFlag ? Items.AIR : this.item;
    }

    public Holder<Item> getItemHolder() {
        return this.getItem().builtInRegistryHolder();
    }

    public boolean is(TagKey<Item> $$0) {
        return this.getItem().builtInRegistryHolder().is($$0);
    }

    public boolean is(Item $$0) {
        return this.getItem() == $$0;
    }

    public boolean is(Predicate<Holder<Item>> $$0) {
        return $$0.test(this.getItem().builtInRegistryHolder());
    }

    public boolean is(Holder<Item> $$0) {
        return this.getItem().builtInRegistryHolder() == $$0;
    }

    public Stream<TagKey<Item>> getTags() {
        return this.getItem().builtInRegistryHolder().tags();
    }

    public InteractionResult useOn(UseOnContext $$0) {
        Player $$1 = $$0.getPlayer();
        BlockPos $$2 = $$0.getClickedPos();
        BlockInWorld $$3 = new BlockInWorld($$0.getLevel(), $$2, false);
        if ($$1 != null && !$$1.getAbilities().mayBuild && !this.hasAdventureModePlaceTagForBlock($$0.getLevel().registryAccess().registryOrThrow(Registries.BLOCK), $$3)) {
            return InteractionResult.PASS;
        }
        Item $$4 = this.getItem();
        InteractionResult $$5 = $$4.useOn($$0);
        if ($$1 != null && $$5.shouldAwardStats()) {
            $$1.awardStat(Stats.ITEM_USED.get($$4));
        }
        return $$5;
    }

    public float getDestroySpeed(BlockState $$0) {
        return this.getItem().getDestroySpeed(this, $$0);
    }

    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        return this.getItem().use($$0, $$1, $$2);
    }

    public ItemStack finishUsingItem(Level $$0, LivingEntity $$1) {
        return this.getItem().finishUsingItem(this, $$0, $$1);
    }

    public CompoundTag save(CompoundTag $$0) {
        ResourceLocation $$1 = BuiltInRegistries.ITEM.getKey(this.getItem());
        $$0.putString("id", $$1 == null ? "minecraft:air" : $$1.toString());
        $$0.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            $$0.put("tag", this.tag.copy());
        }
        return $$0;
    }

    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
    }

    public boolean isDamageableItem() {
        if (this.emptyCacheFlag || this.getItem().getMaxDamage() <= 0) {
            return false;
        }
        CompoundTag $$0 = this.getTag();
        return $$0 == null || !$$0.getBoolean(TAG_UNBREAKABLE);
    }

    public boolean isDamaged() {
        return this.isDamageableItem() && this.getDamageValue() > 0;
    }

    public int getDamageValue() {
        return this.tag == null ? 0 : this.tag.getInt(TAG_DAMAGE);
    }

    public void setDamageValue(int $$0) {
        this.getOrCreateTag().putInt(TAG_DAMAGE, Math.max((int)0, (int)$$0));
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public boolean hurt(int $$0, RandomSource $$1, @Nullable ServerPlayer $$2) {
        if (!this.isDamageableItem()) {
            return false;
        }
        if ($$0 > 0) {
            int $$3 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int $$4 = 0;
            for (int $$5 = 0; $$3 > 0 && $$5 < $$0; ++$$5) {
                if (!DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, $$3, $$1)) continue;
                ++$$4;
            }
            if (($$0 -= $$4) <= 0) {
                return false;
            }
        }
        if ($$2 != null && $$0 != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger($$2, this, this.getDamageValue() + $$0);
        }
        int $$6 = this.getDamageValue() + $$0;
        this.setDamageValue($$6);
        return $$6 >= this.getMaxDamage();
    }

    public <T extends LivingEntity> void hurtAndBreak(int $$0, T $$1, Consumer<T> $$2) {
        if ($$1.level.isClientSide || $$1 instanceof Player && ((Player)$$1).getAbilities().instabuild) {
            return;
        }
        if (!this.isDamageableItem()) {
            return;
        }
        if (this.hurt($$0, $$1.getRandom(), $$1 instanceof ServerPlayer ? (ServerPlayer)$$1 : null)) {
            $$2.accept($$1);
            Item $$3 = this.getItem();
            this.shrink(1);
            if ($$1 instanceof Player) {
                ((Player)$$1).awardStat(Stats.ITEM_BROKEN.get($$3));
            }
            this.setDamageValue(0);
        }
    }

    public boolean isBarVisible() {
        return this.item.isBarVisible(this);
    }

    public int getBarWidth() {
        return this.item.getBarWidth(this);
    }

    public int getBarColor() {
        return this.item.getBarColor(this);
    }

    public boolean overrideStackedOnOther(Slot $$0, ClickAction $$1, Player $$2) {
        return this.getItem().overrideStackedOnOther(this, $$0, $$1, $$2);
    }

    public boolean overrideOtherStackedOnMe(ItemStack $$0, Slot $$1, ClickAction $$2, Player $$3, SlotAccess $$4) {
        return this.getItem().overrideOtherStackedOnMe(this, $$0, $$1, $$2, $$3, $$4);
    }

    public void hurtEnemy(LivingEntity $$0, Player $$1) {
        Item $$2 = this.getItem();
        if ($$2.hurtEnemy(this, $$0, $$1)) {
            $$1.awardStat(Stats.ITEM_USED.get($$2));
        }
    }

    public void mineBlock(Level $$0, BlockState $$1, BlockPos $$2, Player $$3) {
        Item $$4 = this.getItem();
        if ($$4.mineBlock(this, $$0, $$1, $$2, $$3)) {
            $$3.awardStat(Stats.ITEM_USED.get($$4));
        }
    }

    public boolean isCorrectToolForDrops(BlockState $$0) {
        return this.getItem().isCorrectToolForDrops($$0);
    }

    public InteractionResult interactLivingEntity(Player $$0, LivingEntity $$1, InteractionHand $$2) {
        return this.getItem().interactLivingEntity(this, $$0, $$1, $$2);
    }

    public ItemStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        }
        ItemStack $$0 = new ItemStack(this.getItem(), this.count);
        $$0.setPopTime(this.getPopTime());
        if (this.tag != null) {
            $$0.tag = this.tag.copy();
        }
        return $$0;
    }

    public ItemStack copyWithCount(int $$0) {
        ItemStack $$1 = this.copy();
        $$1.setCount($$0);
        return $$1;
    }

    public static boolean tagMatches(ItemStack $$0, ItemStack $$1) {
        if ($$0.isEmpty() && $$1.isEmpty()) {
            return true;
        }
        if ($$0.isEmpty() || $$1.isEmpty()) {
            return false;
        }
        if ($$0.tag == null && $$1.tag != null) {
            return false;
        }
        return $$0.tag == null || $$0.tag.equals($$1.tag);
    }

    public static boolean matches(ItemStack $$0, ItemStack $$1) {
        if ($$0.isEmpty() && $$1.isEmpty()) {
            return true;
        }
        if ($$0.isEmpty() || $$1.isEmpty()) {
            return false;
        }
        return $$0.matches($$1);
    }

    private boolean matches(ItemStack $$0) {
        if (this.count != $$0.count) {
            return false;
        }
        if (!this.is($$0.getItem())) {
            return false;
        }
        if (this.tag == null && $$0.tag != null) {
            return false;
        }
        return this.tag == null || this.tag.equals($$0.tag);
    }

    public static boolean isSame(ItemStack $$0, ItemStack $$1) {
        if ($$0 == $$1) {
            return true;
        }
        if (!$$0.isEmpty() && !$$1.isEmpty()) {
            return $$0.sameItem($$1);
        }
        return false;
    }

    public boolean sameItem(ItemStack $$0) {
        return !$$0.isEmpty() && this.is($$0.getItem());
    }

    public static boolean isSameItemSameTags(ItemStack $$0, ItemStack $$1) {
        return $$0.is($$1.getItem()) && ItemStack.tagMatches($$0, $$1);
    }

    public String getDescriptionId() {
        return this.getItem().getDescriptionId(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(Level $$0, Entity $$1, int $$2, boolean $$3) {
        if (this.popTime > 0) {
            --this.popTime;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, $$0, $$1, $$2, $$3);
        }
    }

    public void onCraftedBy(Level $$0, Player $$1, int $$2) {
        $$1.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), $$2);
        this.getItem().onCraftedBy(this, $$0, $$1);
    }

    public int getUseDuration() {
        return this.getItem().getUseDuration(this);
    }

    public UseAnim getUseAnimation() {
        return this.getItem().getUseAnimation(this);
    }

    public void releaseUsing(Level $$0, LivingEntity $$1, int $$2) {
        this.getItem().releaseUsing(this, $$0, $$1, $$2);
    }

    public boolean useOnRelease() {
        return this.getItem().useOnRelease(this);
    }

    public boolean hasTag() {
        return !this.emptyCacheFlag && this.tag != null && !this.tag.isEmpty();
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
        }
        return this.tag;
    }

    public CompoundTag getOrCreateTagElement(String $$0) {
        if (this.tag == null || !this.tag.contains($$0, 10)) {
            CompoundTag $$1 = new CompoundTag();
            this.addTagElement($$0, $$1);
            return $$1;
        }
        return this.tag.getCompound($$0);
    }

    @Nullable
    public CompoundTag getTagElement(String $$0) {
        if (this.tag == null || !this.tag.contains($$0, 10)) {
            return null;
        }
        return this.tag.getCompound($$0);
    }

    public void removeTagKey(String $$0) {
        if (this.tag != null && this.tag.contains($$0)) {
            this.tag.remove($$0);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }
    }

    public ListTag getEnchantmentTags() {
        if (this.tag != null) {
            return this.tag.getList(TAG_ENCH, 10);
        }
        return new ListTag();
    }

    public void setTag(@Nullable CompoundTag $$0) {
        this.tag = $$0;
        if (this.getItem().canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
        if ($$0 != null) {
            this.getItem().verifyTagAfterLoad($$0);
        }
    }

    public Component getHoverName() {
        CompoundTag $$0 = this.getTagElement(TAG_DISPLAY);
        if ($$0 != null && $$0.contains(TAG_DISPLAY_NAME, 8)) {
            try {
                MutableComponent $$1 = Component.Serializer.fromJson($$0.getString(TAG_DISPLAY_NAME));
                if ($$1 != null) {
                    return $$1;
                }
                $$0.remove(TAG_DISPLAY_NAME);
            }
            catch (Exception $$2) {
                $$0.remove(TAG_DISPLAY_NAME);
            }
        }
        return this.getItem().getName(this);
    }

    public ItemStack setHoverName(@Nullable Component $$0) {
        CompoundTag $$1 = this.getOrCreateTagElement(TAG_DISPLAY);
        if ($$0 != null) {
            $$1.putString(TAG_DISPLAY_NAME, Component.Serializer.toJson($$0));
        } else {
            $$1.remove(TAG_DISPLAY_NAME);
        }
        return this;
    }

    public void resetHoverName() {
        CompoundTag $$0 = this.getTagElement(TAG_DISPLAY);
        if ($$0 != null) {
            $$0.remove(TAG_DISPLAY_NAME);
            if ($$0.isEmpty()) {
                this.removeTagKey(TAG_DISPLAY);
            }
        }
        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }
    }

    public boolean hasCustomHoverName() {
        CompoundTag $$0 = this.getTagElement(TAG_DISPLAY);
        return $$0 != null && $$0.contains(TAG_DISPLAY_NAME, 8);
    }

    public List<Component> getTooltipLines(@Nullable Player $$0, TooltipFlag $$1) {
        int $$5;
        Integer $$4;
        ArrayList $$2 = Lists.newArrayList();
        MutableComponent $$3 = Component.empty().append(this.getHoverName()).withStyle(this.getRarity().color);
        if (this.hasCustomHoverName()) {
            $$3.withStyle(ChatFormatting.ITALIC);
        }
        $$2.add((Object)$$3);
        if (!$$1.isAdvanced() && !this.hasCustomHoverName() && this.is(Items.FILLED_MAP) && ($$4 = MapItem.getMapId(this)) != null) {
            $$2.add((Object)Component.literal("#" + $$4).withStyle(ChatFormatting.GRAY));
        }
        if (ItemStack.shouldShowInTooltip($$5 = this.getHideFlags(), TooltipPart.ADDITIONAL)) {
            this.getItem().appendHoverText(this, $$0 == null ? null : $$0.level, (List<Component>)$$2, $$1);
        }
        if (this.hasTag()) {
            if (ItemStack.shouldShowInTooltip($$5, TooltipPart.ENCHANTMENTS)) {
                ItemStack.appendEnchantmentNames((List<Component>)$$2, this.getEnchantmentTags());
            }
            if (this.tag.contains(TAG_DISPLAY, 10)) {
                CompoundTag $$6 = this.tag.getCompound(TAG_DISPLAY);
                if (ItemStack.shouldShowInTooltip($$5, TooltipPart.DYE) && $$6.contains(TAG_COLOR, 99)) {
                    if ($$1.isAdvanced()) {
                        $$2.add((Object)Component.translatable("item.color", String.format((Locale)Locale.ROOT, (String)"#%06X", (Object[])new Object[]{$$6.getInt(TAG_COLOR)})).withStyle(ChatFormatting.GRAY));
                    } else {
                        $$2.add((Object)Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    }
                }
                if ($$6.getTagType(TAG_LORE) == 9) {
                    ListTag $$7 = $$6.getList(TAG_LORE, 8);
                    for (int $$8 = 0; $$8 < $$7.size(); ++$$8) {
                        String $$9 = $$7.getString($$8);
                        try {
                            MutableComponent $$10 = Component.Serializer.fromJson($$9);
                            if ($$10 == null) continue;
                            $$2.add((Object)ComponentUtils.mergeStyles($$10, LORE_STYLE));
                            continue;
                        }
                        catch (Exception $$11) {
                            $$6.remove(TAG_LORE);
                        }
                    }
                }
            }
        }
        if (ItemStack.shouldShowInTooltip($$5, TooltipPart.MODIFIERS)) {
            for (EquipmentSlot $$12 : EquipmentSlot.values()) {
                Multimap<Attribute, AttributeModifier> $$13 = this.getAttributeModifiers($$12);
                if ($$13.isEmpty()) continue;
                $$2.add((Object)CommonComponents.EMPTY);
                $$2.add((Object)Component.translatable("item.modifiers." + $$12.getName()).withStyle(ChatFormatting.GRAY));
                for (Map.Entry $$14 : $$13.entries()) {
                    double $$20;
                    AttributeModifier $$15 = (AttributeModifier)$$14.getValue();
                    double $$16 = $$15.getAmount();
                    boolean $$17 = false;
                    if ($$0 != null) {
                        if ($$15.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                            $$16 += $$0.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                            $$16 += (double)EnchantmentHelper.getDamageBonus(this, MobType.UNDEFINED);
                            $$17 = true;
                        } else if ($$15.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                            $$16 += $$0.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                            $$17 = true;
                        }
                    }
                    if ($$15.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE || $$15.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        double $$18 = $$16 * 100.0;
                    } else if (((Attribute)$$14.getKey()).equals(Attributes.KNOCKBACK_RESISTANCE)) {
                        double $$19 = $$16 * 10.0;
                    } else {
                        $$20 = $$16;
                    }
                    if ($$17) {
                        $$2.add((Object)CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + $$15.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format($$20), Component.translatable(((Attribute)$$14.getKey()).getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                        continue;
                    }
                    if ($$16 > 0.0) {
                        $$2.add((Object)Component.translatable("attribute.modifier.plus." + $$15.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format($$20), Component.translatable(((Attribute)$$14.getKey()).getDescriptionId())).withStyle(ChatFormatting.BLUE));
                        continue;
                    }
                    if (!($$16 < 0.0)) continue;
                    $$2.add((Object)Component.translatable("attribute.modifier.take." + $$15.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format($$20 *= -1.0), Component.translatable(((Attribute)$$14.getKey()).getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }
        if (this.hasTag()) {
            ListTag $$23;
            ListTag $$21;
            if (ItemStack.shouldShowInTooltip($$5, TooltipPart.UNBREAKABLE) && this.tag.getBoolean(TAG_UNBREAKABLE)) {
                $$2.add((Object)Component.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE));
            }
            if (ItemStack.shouldShowInTooltip($$5, TooltipPart.CAN_DESTROY) && this.tag.contains(TAG_CAN_DESTROY_BLOCK_LIST, 9) && !($$21 = this.tag.getList(TAG_CAN_DESTROY_BLOCK_LIST, 8)).isEmpty()) {
                $$2.add((Object)CommonComponents.EMPTY);
                $$2.add((Object)Component.translatable("item.canBreak").withStyle(ChatFormatting.GRAY));
                for (int $$22 = 0; $$22 < $$21.size(); ++$$22) {
                    $$2.addAll(ItemStack.expandBlockState($$21.getString($$22)));
                }
            }
            if (ItemStack.shouldShowInTooltip($$5, TooltipPart.CAN_PLACE) && this.tag.contains(TAG_CAN_PLACE_ON_BLOCK_LIST, 9) && !($$23 = this.tag.getList(TAG_CAN_PLACE_ON_BLOCK_LIST, 8)).isEmpty()) {
                $$2.add((Object)CommonComponents.EMPTY);
                $$2.add((Object)Component.translatable("item.canPlace").withStyle(ChatFormatting.GRAY));
                for (int $$24 = 0; $$24 < $$23.size(); ++$$24) {
                    $$2.addAll(ItemStack.expandBlockState($$23.getString($$24)));
                }
            }
        }
        if ($$1.isAdvanced()) {
            if (this.isDamaged()) {
                $$2.add((Object)Component.translatable("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
            }
            $$2.add((Object)Component.literal(BuiltInRegistries.ITEM.getKey(this.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
            if (this.hasTag()) {
                $$2.add((Object)Component.translatable("item.nbt_tags", this.tag.getAllKeys().size()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        if ($$0 != null && !this.getItem().isEnabled($$0.getLevel().enabledFeatures())) {
            $$2.add((Object)DISABLED_ITEM_TOOLTIP);
        }
        return $$2;
    }

    private static boolean shouldShowInTooltip(int $$0, TooltipPart $$1) {
        return ($$0 & $$1.getMask()) == 0;
    }

    private int getHideFlags() {
        if (this.hasTag() && this.tag.contains(TAG_HIDE_FLAGS, 99)) {
            return this.tag.getInt(TAG_HIDE_FLAGS);
        }
        return 0;
    }

    public void hideTooltipPart(TooltipPart $$0) {
        CompoundTag $$1 = this.getOrCreateTag();
        $$1.putInt(TAG_HIDE_FLAGS, $$1.getInt(TAG_HIDE_FLAGS) | $$0.getMask());
    }

    public static void appendEnchantmentNames(List<Component> $$0, ListTag $$1) {
        for (int $$22 = 0; $$22 < $$1.size(); ++$$22) {
            CompoundTag $$3 = $$1.getCompound($$22);
            BuiltInRegistries.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId($$3)).ifPresent($$2 -> $$0.add((Object)$$2.getFullname(EnchantmentHelper.getEnchantmentLevel($$3))));
        }
    }

    private static Collection<Component> expandBlockState(String $$03) {
        try {
            return (Collection)BlockStateParser.parseForTesting((HolderLookup<Block>)BuiltInRegistries.BLOCK.asLookup(), $$03, true).map($$0 -> Lists.newArrayList((Object[])new Component[]{$$0.blockState().getBlock().getName().withStyle(ChatFormatting.DARK_GRAY)}), $$02 -> (List)$$02.tag().stream().map($$0 -> ((Block)$$0.value()).getName().withStyle(ChatFormatting.DARK_GRAY)).collect(Collectors.toList()));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return Lists.newArrayList((Object[])new Component[]{Component.literal("missingno").withStyle(ChatFormatting.DARK_GRAY)});
        }
    }

    public boolean hasFoil() {
        return this.getItem().isFoil(this);
    }

    public Rarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isEnchantable() {
        if (!this.getItem().isEnchantable(this)) {
            return false;
        }
        return !this.isEnchanted();
    }

    public void enchant(Enchantment $$0, int $$1) {
        this.getOrCreateTag();
        if (!this.tag.contains(TAG_ENCH, 9)) {
            this.tag.put(TAG_ENCH, new ListTag());
        }
        ListTag $$2 = this.tag.getList(TAG_ENCH, 10);
        $$2.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId($$0), (byte)$$1));
    }

    public boolean isEnchanted() {
        if (this.tag != null && this.tag.contains(TAG_ENCH, 9)) {
            return !this.tag.getList(TAG_ENCH, 10).isEmpty();
        }
        return false;
    }

    public void addTagElement(String $$0, Tag $$1) {
        this.getOrCreateTag().put($$0, $$1);
    }

    public boolean isFramed() {
        return this.entityRepresentation instanceof ItemFrame;
    }

    public void setEntityRepresentation(@Nullable Entity $$0) {
        this.entityRepresentation = $$0;
    }

    @Nullable
    public ItemFrame getFrame() {
        return this.entityRepresentation instanceof ItemFrame ? (ItemFrame)this.getEntityRepresentation() : null;
    }

    @Nullable
    public Entity getEntityRepresentation() {
        return !this.emptyCacheFlag ? this.entityRepresentation : null;
    }

    public int getBaseRepairCost() {
        if (this.hasTag() && this.tag.contains(TAG_REPAIR_COST, 3)) {
            return this.tag.getInt(TAG_REPAIR_COST);
        }
        return 0;
    }

    public void setRepairCost(int $$0) {
        this.getOrCreateTag().putInt(TAG_REPAIR_COST, $$0);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot $$0) {
        Multimap<Attribute, AttributeModifier> $$7;
        if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
            HashMultimap $$1 = HashMultimap.create();
            ListTag $$2 = this.tag.getList("AttributeModifiers", 10);
            for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                AttributeModifier $$6;
                Optional<Attribute> $$5;
                CompoundTag $$4 = $$2.getCompound($$3);
                if ($$4.contains("Slot", 8) && !$$4.getString("Slot").equals((Object)$$0.getName()) || !($$5 = BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse($$4.getString("AttributeName")))).isPresent() || ($$6 = AttributeModifier.load($$4)) == null || $$6.getId().getLeastSignificantBits() == 0L || $$6.getId().getMostSignificantBits() == 0L) continue;
                $$1.put((Object)((Attribute)$$5.get()), (Object)$$6);
            }
        } else {
            $$7 = this.getItem().getDefaultAttributeModifiers($$0);
        }
        return $$7;
    }

    public void addAttributeModifier(Attribute $$0, AttributeModifier $$1, @Nullable EquipmentSlot $$2) {
        this.getOrCreateTag();
        if (!this.tag.contains("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new ListTag());
        }
        ListTag $$3 = this.tag.getList("AttributeModifiers", 10);
        CompoundTag $$4 = $$1.save();
        $$4.putString("AttributeName", BuiltInRegistries.ATTRIBUTE.getKey($$0).toString());
        if ($$2 != null) {
            $$4.putString("Slot", $$2.getName());
        }
        $$3.add($$4);
    }

    public Component getDisplayName() {
        MutableComponent $$02 = Component.empty().append(this.getHoverName());
        if (this.hasCustomHoverName()) {
            $$02.withStyle(ChatFormatting.ITALIC);
        }
        MutableComponent $$1 = ComponentUtils.wrapInSquareBrackets($$02);
        if (!this.emptyCacheFlag) {
            $$1.withStyle(this.getRarity().color).withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(this)))));
        }
        return $$1;
    }

    public boolean hasAdventureModePlaceTagForBlock(Registry<Block> $$0, BlockInWorld $$1) {
        if (this.adventurePlaceCheck == null) {
            this.adventurePlaceCheck = new AdventureModeCheck(TAG_CAN_PLACE_ON_BLOCK_LIST);
        }
        return this.adventurePlaceCheck.test(this, $$0, $$1);
    }

    public boolean hasAdventureModeBreakTagForBlock(Registry<Block> $$0, BlockInWorld $$1) {
        if (this.adventureBreakCheck == null) {
            this.adventureBreakCheck = new AdventureModeCheck(TAG_CAN_DESTROY_BLOCK_LIST);
        }
        return this.adventureBreakCheck.test(this, $$0, $$1);
    }

    public int getPopTime() {
        return this.popTime;
    }

    public void setPopTime(int $$0) {
        this.popTime = $$0;
    }

    public int getCount() {
        return this.emptyCacheFlag ? 0 : this.count;
    }

    public void setCount(int $$0) {
        this.count = $$0;
        this.updateEmptyCacheFlag();
    }

    public void grow(int $$0) {
        this.setCount(this.count + $$0);
    }

    public void shrink(int $$0) {
        this.grow(-$$0);
    }

    public void onUseTick(Level $$0, LivingEntity $$1, int $$2) {
        this.getItem().onUseTick($$0, $$1, this, $$2);
    }

    public void onDestroyed(ItemEntity $$0) {
        this.getItem().onDestroyed($$0);
    }

    public boolean isEdible() {
        return this.getItem().isEdible();
    }

    public SoundEvent getDrinkingSound() {
        return this.getItem().getDrinkingSound();
    }

    public SoundEvent getEatingSound() {
        return this.getItem().getEatingSound();
    }

    @Nullable
    public SoundEvent getEquipSound() {
        return this.getItem().getEquipSound();
    }

    public static enum TooltipPart {
        ENCHANTMENTS,
        MODIFIERS,
        UNBREAKABLE,
        CAN_DESTROY,
        CAN_PLACE,
        ADDITIONAL,
        DYE;

        private final int mask = 1 << this.ordinal();

        public int getMask() {
            return this.mask;
        }
    }
}