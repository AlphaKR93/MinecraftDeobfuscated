/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BlackstoneReplaceProcessor
extends StructureProcessor {
    public static final Codec<BlackstoneReplaceProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public static final BlackstoneReplaceProcessor INSTANCE = new BlackstoneReplaceProcessor();
    private final Map<Block, Block> replacements = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)Blocks.COBBLESTONE, (Object)Blocks.BLACKSTONE);
        $$0.put((Object)Blocks.MOSSY_COBBLESTONE, (Object)Blocks.BLACKSTONE);
        $$0.put((Object)Blocks.STONE, (Object)Blocks.POLISHED_BLACKSTONE);
        $$0.put((Object)Blocks.STONE_BRICKS, (Object)Blocks.POLISHED_BLACKSTONE_BRICKS);
        $$0.put((Object)Blocks.MOSSY_STONE_BRICKS, (Object)Blocks.POLISHED_BLACKSTONE_BRICKS);
        $$0.put((Object)Blocks.COBBLESTONE_STAIRS, (Object)Blocks.BLACKSTONE_STAIRS);
        $$0.put((Object)Blocks.MOSSY_COBBLESTONE_STAIRS, (Object)Blocks.BLACKSTONE_STAIRS);
        $$0.put((Object)Blocks.STONE_STAIRS, (Object)Blocks.POLISHED_BLACKSTONE_STAIRS);
        $$0.put((Object)Blocks.STONE_BRICK_STAIRS, (Object)Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        $$0.put((Object)Blocks.MOSSY_STONE_BRICK_STAIRS, (Object)Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        $$0.put((Object)Blocks.COBBLESTONE_SLAB, (Object)Blocks.BLACKSTONE_SLAB);
        $$0.put((Object)Blocks.MOSSY_COBBLESTONE_SLAB, (Object)Blocks.BLACKSTONE_SLAB);
        $$0.put((Object)Blocks.SMOOTH_STONE_SLAB, (Object)Blocks.POLISHED_BLACKSTONE_SLAB);
        $$0.put((Object)Blocks.STONE_SLAB, (Object)Blocks.POLISHED_BLACKSTONE_SLAB);
        $$0.put((Object)Blocks.STONE_BRICK_SLAB, (Object)Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        $$0.put((Object)Blocks.MOSSY_STONE_BRICK_SLAB, (Object)Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        $$0.put((Object)Blocks.STONE_BRICK_WALL, (Object)Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        $$0.put((Object)Blocks.MOSSY_STONE_BRICK_WALL, (Object)Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        $$0.put((Object)Blocks.COBBLESTONE_WALL, (Object)Blocks.BLACKSTONE_WALL);
        $$0.put((Object)Blocks.MOSSY_COBBLESTONE_WALL, (Object)Blocks.BLACKSTONE_WALL);
        $$0.put((Object)Blocks.CHISELED_STONE_BRICKS, (Object)Blocks.CHISELED_POLISHED_BLACKSTONE);
        $$0.put((Object)Blocks.CRACKED_STONE_BRICKS, (Object)Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        $$0.put((Object)Blocks.IRON_BARS, (Object)Blocks.CHAIN);
    });

    private BlackstoneReplaceProcessor() {
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader $$0, BlockPos $$1, BlockPos $$2, StructureTemplate.StructureBlockInfo $$3, StructureTemplate.StructureBlockInfo $$4, StructurePlaceSettings $$5) {
        Block $$6 = (Block)this.replacements.get((Object)$$4.state.getBlock());
        if ($$6 == null) {
            return $$4;
        }
        BlockState $$7 = $$4.state;
        BlockState $$8 = $$6.defaultBlockState();
        if ($$7.hasProperty(StairBlock.FACING)) {
            $$8 = (BlockState)$$8.setValue(StairBlock.FACING, $$7.getValue(StairBlock.FACING));
        }
        if ($$7.hasProperty(StairBlock.HALF)) {
            $$8 = (BlockState)$$8.setValue(StairBlock.HALF, $$7.getValue(StairBlock.HALF));
        }
        if ($$7.hasProperty(SlabBlock.TYPE)) {
            $$8 = (BlockState)$$8.setValue(SlabBlock.TYPE, $$7.getValue(SlabBlock.TYPE));
        }
        return new StructureTemplate.StructureBlockInfo($$4.pos, $$8, $$4.nbt);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLACKSTONE_REPLACE;
    }
}