/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.util.Map
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;

public class BlockColors {
    private static final int DEFAULT = -1;
    private final IdMapper<BlockColor> blockColors = new IdMapper(32);
    private final Map<Block, Set<Property<?>>> coloringStates = Maps.newHashMap();

    public static BlockColors createDefault() {
        BlockColors $$02 = new BlockColors();
        $$02.register(($$0, $$1, $$2, $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -1;
            }
            return BiomeColors.getAverageGrassColor($$1, (BlockPos)($$0.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? $$2.below() : $$2));
        }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        $$02.addColoringState(DoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        $$02.register(($$0, $$1, $$2, $$3) -> {
            if ($$1 == null || $$2 == null) {
                return GrassColor.get(0.5, 1.0);
            }
            return BiomeColors.getAverageGrassColor($$1, $$2);
        }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
        $$02.register(($$0, $$1, $$2, $$3) -> FoliageColor.getEvergreenColor(), Blocks.SPRUCE_LEAVES);
        $$02.register(($$0, $$1, $$2, $$3) -> FoliageColor.getBirchColor(), Blocks.BIRCH_LEAVES);
        $$02.register(($$0, $$1, $$2, $$3) -> {
            if ($$1 == null || $$2 == null) {
                return FoliageColor.getDefaultColor();
            }
            return BiomeColors.getAverageFoliageColor($$1, $$2);
        }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE, Blocks.MANGROVE_LEAVES);
        $$02.register(($$0, $$1, $$2, $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -1;
            }
            return BiomeColors.getAverageWaterColor($$1, $$2);
        }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON);
        $$02.register(($$0, $$1, $$2, $$3) -> RedStoneWireBlock.getColorForPower($$0.getValue(RedStoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);
        $$02.addColoringState(RedStoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
        $$02.register(($$0, $$1, $$2, $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -1;
            }
            return BiomeColors.getAverageGrassColor($$1, $$2);
        }, Blocks.SUGAR_CANE);
        $$02.register(($$0, $$1, $$2, $$3) -> 14731036, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        $$02.register(($$0, $$1, $$2, $$3) -> {
            int $$4 = $$0.getValue(StemBlock.AGE);
            int $$5 = $$4 * 32;
            int $$6 = 255 - $$4 * 8;
            int $$7 = $$4 * 4;
            return $$5 << 16 | $$6 << 8 | $$7;
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        $$02.addColoringState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        $$02.register(($$0, $$1, $$2, $$3) -> {
            if ($$1 == null || $$2 == null) {
                return 7455580;
            }
            return 2129968;
        }, Blocks.LILY_PAD);
        return $$02;
    }

    public int getColor(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockColor $$3 = this.blockColors.byId(BuiltInRegistries.BLOCK.getId($$0.getBlock()));
        if ($$3 != null) {
            return $$3.getColor($$0, null, null, 0);
        }
        MaterialColor $$4 = $$0.getMapColor($$1, $$2);
        return $$4 != null ? $$4.col : -1;
    }

    public int getColor(BlockState $$0, @Nullable BlockAndTintGetter $$1, @Nullable BlockPos $$2, int $$3) {
        BlockColor $$4 = this.blockColors.byId(BuiltInRegistries.BLOCK.getId($$0.getBlock()));
        return $$4 == null ? -1 : $$4.getColor($$0, $$1, $$2, $$3);
    }

    public void register(BlockColor $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            this.blockColors.addMapping($$0, BuiltInRegistries.BLOCK.getId($$2));
        }
    }

    private void addColoringStates(Set<Property<?>> $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            this.coloringStates.put((Object)$$2, $$0);
        }
    }

    private void addColoringState(Property<?> $$0, Block ... $$1) {
        this.addColoringStates((Set<Property<?>>)ImmutableSet.of($$0), $$1);
    }

    public Set<Property<?>> getColoringProperties(Block $$0) {
        return (Set)this.coloringStates.getOrDefault((Object)$$0, (Object)ImmutableSet.of());
    }
}