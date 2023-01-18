/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    public static final int MAX_PATTERNS = 6;
    public static final String TAG_PATTERNS = "Patterns";
    public static final String TAG_PATTERN = "Pattern";
    public static final String TAG_COLOR = "Color";
    @Nullable
    private Component name;
    private DyeColor baseColor;
    @Nullable
    private ListTag itemPatterns;
    @Nullable
    private List<Pair<Holder<BannerPattern>, DyeColor>> patterns;

    public BannerBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BANNER, $$0, $$1);
        this.baseColor = ((AbstractBannerBlock)$$1.getBlock()).getColor();
    }

    public BannerBlockEntity(BlockPos $$0, BlockState $$1, DyeColor $$2) {
        this($$0, $$1);
        this.baseColor = $$2;
    }

    @Nullable
    public static ListTag getItemPatterns(ItemStack $$0) {
        ListTag $$1 = null;
        CompoundTag $$2 = BlockItem.getBlockEntityData($$0);
        if ($$2 != null && $$2.contains(TAG_PATTERNS, 9)) {
            $$1 = $$2.getList(TAG_PATTERNS, 10).copy();
        }
        return $$1;
    }

    public void fromItem(ItemStack $$0, DyeColor $$1) {
        this.baseColor = $$1;
        this.fromItem($$0);
    }

    public void fromItem(ItemStack $$0) {
        this.itemPatterns = BannerBlockEntity.getItemPatterns($$0);
        this.patterns = null;
        this.name = $$0.hasCustomHoverName() ? $$0.getHoverName() : null;
    }

    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return Component.translatable("block.minecraft.banner");
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    public void setCustomName(Component $$0) {
        this.name = $$0;
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        if (this.itemPatterns != null) {
            $$0.put(TAG_PATTERNS, this.itemPatterns);
        }
        if (this.name != null) {
            $$0.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        if ($$0.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson($$0.getString("CustomName"));
        }
        this.itemPatterns = $$0.getList(TAG_PATTERNS, 10);
        this.patterns = null;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static int getPatternCount(ItemStack $$0) {
        CompoundTag $$1 = BlockItem.getBlockEntityData($$0);
        if ($$1 != null && $$1.contains(TAG_PATTERNS)) {
            return $$1.getList(TAG_PATTERNS, 10).size();
        }
        return 0;
    }

    public List<Pair<Holder<BannerPattern>, DyeColor>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = BannerBlockEntity.createPatterns(this.baseColor, this.itemPatterns);
        }
        return this.patterns;
    }

    public static List<Pair<Holder<BannerPattern>, DyeColor>> createPatterns(DyeColor $$0, @Nullable ListTag $$1) {
        ArrayList $$2 = Lists.newArrayList();
        $$2.add((Object)Pair.of(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(BannerPatterns.BASE), (Object)$$0));
        if ($$1 != null) {
            for (int $$3 = 0; $$3 < $$1.size(); ++$$3) {
                CompoundTag $$4 = $$1.getCompound($$3);
                Holder<BannerPattern> $$5 = BannerPattern.byHash($$4.getString(TAG_PATTERN));
                if ($$5 == null) continue;
                int $$6 = $$4.getInt(TAG_COLOR);
                $$2.add((Object)Pair.of($$5, (Object)DyeColor.byId($$6)));
            }
        }
        return $$2;
    }

    public static void removeLastPattern(ItemStack $$0) {
        CompoundTag $$1 = BlockItem.getBlockEntityData($$0);
        if ($$1 == null || !$$1.contains(TAG_PATTERNS, 9)) {
            return;
        }
        ListTag $$2 = $$1.getList(TAG_PATTERNS, 10);
        if ($$2.isEmpty()) {
            return;
        }
        $$2.remove($$2.size() - 1);
        if ($$2.isEmpty()) {
            $$1.remove(TAG_PATTERNS);
        }
        BlockItem.setBlockEntityData($$0, BlockEntityType.BANNER, $$1);
    }

    public ItemStack getItem() {
        ItemStack $$0 = new ItemStack(BannerBlock.byColor(this.baseColor));
        if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
            CompoundTag $$1 = new CompoundTag();
            $$1.put(TAG_PATTERNS, this.itemPatterns.copy());
            BlockItem.setBlockEntityData($$0, this.getType(), $$1);
        }
        if (this.name != null) {
            $$0.setHoverName(this.name);
        }
        return $$0;
    }

    public DyeColor getBaseColor() {
        return this.baseColor;
    }
}