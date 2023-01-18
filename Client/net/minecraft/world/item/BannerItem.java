/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.Validate;

public class BannerItem
extends StandingAndWallBlockItem {
    private static final String PATTERN_PREFIX = "block.minecraft.banner.";

    public BannerItem(Block $$0, Block $$1, Item.Properties $$2) {
        super($$0, $$1, $$2, Direction.DOWN);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)$$0);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)$$1);
    }

    public static void appendHoverTextFromBannerBlockEntityTag(ItemStack $$02, List<Component> $$1) {
        CompoundTag $$22 = BlockItem.getBlockEntityData($$02);
        if ($$22 == null || !$$22.contains("Patterns")) {
            return;
        }
        ListTag $$3 = $$22.getList("Patterns", 10);
        for (int $$4 = 0; $$4 < $$3.size() && $$4 < 6; ++$$4) {
            CompoundTag $$5 = $$3.getCompound($$4);
            DyeColor $$6 = DyeColor.byId($$5.getInt("Color"));
            Holder<BannerPattern> $$7 = BannerPattern.byHash($$5.getString("Pattern"));
            if ($$7 == null) continue;
            $$7.unwrapKey().map($$0 -> $$0.location().toShortLanguageKey()).ifPresent($$2 -> $$1.add((Object)Component.translatable(PATTERN_PREFIX + $$2 + "." + $$6.getName()).withStyle(ChatFormatting.GRAY)));
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag($$0, $$2);
    }
}