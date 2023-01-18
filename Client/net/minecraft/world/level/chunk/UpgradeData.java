/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.ThreadLocal
 *  java.util.EnumSet
 *  java.util.IdentityHashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public class UpgradeData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final UpgradeData EMPTY = new UpgradeData(EmptyBlockGetter.INSTANCE);
    private static final String TAG_INDICES = "Indices";
    private static final Direction8[] DIRECTIONS = Direction8.values();
    private final EnumSet<Direction8> sides = EnumSet.noneOf(Direction8.class);
    private final List<SavedTick<Block>> neighborBlockTicks = Lists.newArrayList();
    private final List<SavedTick<Fluid>> neighborFluidTicks = Lists.newArrayList();
    private final int[][] index;
    static final Map<Block, BlockFixer> MAP = new IdentityHashMap();
    static final Set<BlockFixer> CHUNKY_FIXERS = Sets.newHashSet();

    private UpgradeData(LevelHeightAccessor $$0) {
        this.index = new int[$$0.getSectionsCount()][];
    }

    public UpgradeData(CompoundTag $$02, LevelHeightAccessor $$1) {
        this($$1);
        if ($$02.contains(TAG_INDICES, 10)) {
            CompoundTag $$2 = $$02.getCompound(TAG_INDICES);
            for (int $$3 = 0; $$3 < this.index.length; ++$$3) {
                String $$4 = String.valueOf((int)$$3);
                if (!$$2.contains($$4, 11)) continue;
                this.index[$$3] = $$2.getIntArray($$4);
            }
        }
        int $$5 = $$02.getInt("Sides");
        for (Direction8 $$6 : Direction8.values()) {
            if (($$5 & 1 << $$6.ordinal()) == 0) continue;
            this.sides.add((Object)$$6);
        }
        UpgradeData.loadTicks($$02, "neighbor_block_ticks", $$0 -> BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse($$0)).or(() -> Optional.of((Object)Blocks.AIR)), this.neighborBlockTicks);
        UpgradeData.loadTicks($$02, "neighbor_fluid_ticks", $$0 -> BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse($$0)).or(() -> Optional.of((Object)Fluids.EMPTY)), this.neighborFluidTicks);
    }

    private static <T> void loadTicks(CompoundTag $$0, String $$1, Function<String, Optional<T>> $$2, List<SavedTick<T>> $$3) {
        if ($$0.contains($$1, 9)) {
            ListTag $$4 = $$0.getList($$1, 10);
            Iterator iterator = $$4.iterator();
            while (iterator.hasNext()) {
                Tag $$5 = (Tag)iterator.next();
                SavedTick.loadTick((CompoundTag)$$5, $$2).ifPresent(arg_0 -> $$3.add(arg_0));
            }
        }
    }

    public void upgrade(LevelChunk $$0) {
        this.upgradeInside($$0);
        for (Direction8 $$12 : DIRECTIONS) {
            UpgradeData.upgradeSides($$0, $$12);
        }
        Level $$2 = $$0.getLevel();
        this.neighborBlockTicks.forEach($$1 -> {
            Block $$2 = $$1.type() == Blocks.AIR ? $$2.getBlockState($$1.pos()).getBlock() : (Block)$$1.type();
            $$2.scheduleTick($$1.pos(), $$2, $$1.delay(), $$1.priority());
        });
        this.neighborFluidTicks.forEach($$1 -> {
            Fluid $$2 = $$1.type() == Fluids.EMPTY ? $$2.getFluidState($$1.pos()).getType() : (Fluid)$$1.type();
            $$2.scheduleTick($$1.pos(), $$2, $$1.delay(), $$1.priority());
        });
        CHUNKY_FIXERS.forEach($$1 -> $$1.processChunk($$2));
    }

    private static void upgradeSides(LevelChunk $$0, Direction8 $$1) {
        Level $$2 = $$0.getLevel();
        if (!$$0.getUpgradeData().sides.remove((Object)$$1)) {
            return;
        }
        Set<Direction> $$3 = $$1.getDirections();
        boolean $$4 = false;
        int $$5 = 15;
        boolean $$6 = $$3.contains((Object)Direction.EAST);
        boolean $$7 = $$3.contains((Object)Direction.WEST);
        boolean $$8 = $$3.contains((Object)Direction.SOUTH);
        boolean $$9 = $$3.contains((Object)Direction.NORTH);
        boolean $$10 = $$3.size() == 1;
        ChunkPos $$11 = $$0.getPos();
        int $$12 = $$11.getMinBlockX() + ($$10 && ($$9 || $$8) ? 1 : ($$7 ? 0 : 15));
        int $$13 = $$11.getMinBlockX() + ($$10 && ($$9 || $$8) ? 14 : ($$7 ? 0 : 15));
        int $$14 = $$11.getMinBlockZ() + ($$10 && ($$6 || $$7) ? 1 : ($$9 ? 0 : 15));
        int $$15 = $$11.getMinBlockZ() + ($$10 && ($$6 || $$7) ? 14 : ($$9 ? 0 : 15));
        Direction[] $$16 = Direction.values();
        BlockPos.MutableBlockPos $$17 = new BlockPos.MutableBlockPos();
        for (BlockPos $$18 : BlockPos.betweenClosed($$12, $$2.getMinBuildHeight(), $$14, $$13, $$2.getMaxBuildHeight() - 1, $$15)) {
            BlockState $$19;
            BlockState $$20 = $$19 = $$2.getBlockState($$18);
            for (Direction $$21 : $$16) {
                $$17.setWithOffset((Vec3i)$$18, $$21);
                $$20 = UpgradeData.updateState($$20, $$21, $$2, $$18, $$17);
            }
            Block.updateOrDestroy($$19, $$20, $$2, $$18, 18);
        }
    }

    private static BlockState updateState(BlockState $$0, Direction $$1, LevelAccessor $$2, BlockPos $$3, BlockPos $$4) {
        return ((BlockFixer)MAP.getOrDefault((Object)$$0.getBlock(), (Object)BlockFixers.DEFAULT)).updateShape($$0, $$1, $$2.getBlockState($$4), $$2, $$3, $$4);
    }

    private void upgradeInside(LevelChunk $$0) {
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos();
        ChunkPos $$3 = $$0.getPos();
        Level $$4 = $$0.getLevel();
        for (int $$5 = 0; $$5 < this.index.length; ++$$5) {
            LevelChunkSection $$6 = $$0.getSection($$5);
            int[] $$7 = this.index[$$5];
            this.index[$$5] = null;
            if ($$7 == null || $$7.length <= 0) continue;
            Direction[] $$8 = Direction.values();
            PalettedContainer<BlockState> $$9 = $$6.getStates();
            for (int $$10 : $$7) {
                BlockState $$14;
                int $$11 = $$10 & 0xF;
                int $$12 = $$10 >> 8 & 0xF;
                int $$13 = $$10 >> 4 & 0xF;
                $$1.set($$3.getMinBlockX() + $$11, $$6.bottomBlockY() + $$12, $$3.getMinBlockZ() + $$13);
                BlockState $$15 = $$14 = $$9.get($$10);
                for (Direction $$16 : $$8) {
                    $$2.setWithOffset((Vec3i)$$1, $$16);
                    if (SectionPos.blockToSectionCoord($$1.getX()) != $$3.x || SectionPos.blockToSectionCoord($$1.getZ()) != $$3.z) continue;
                    $$15 = UpgradeData.updateState($$15, $$16, $$4, $$1, $$2);
                }
                Block.updateOrDestroy($$14, $$15, $$4, $$1, 18);
            }
        }
        for (int $$17 = 0; $$17 < this.index.length; ++$$17) {
            if (this.index[$$17] != null) {
                LOGGER.warn("Discarding update data for section {} for chunk ({} {})", new Object[]{$$4.getSectionYFromSectionIndex($$17), $$3.x, $$3.z});
            }
            this.index[$$17] = null;
        }
    }

    public boolean isEmpty() {
        for (int[] $$0 : this.index) {
            if ($$0 == null) continue;
            return false;
        }
        return this.sides.isEmpty();
    }

    public CompoundTag write() {
        CompoundTag $$0 = new CompoundTag();
        CompoundTag $$12 = new CompoundTag();
        for (int $$2 = 0; $$2 < this.index.length; ++$$2) {
            String $$3 = String.valueOf((int)$$2);
            if (this.index[$$2] == null || this.index[$$2].length == 0) continue;
            $$12.putIntArray($$3, this.index[$$2]);
        }
        if (!$$12.isEmpty()) {
            $$0.put(TAG_INDICES, $$12);
        }
        int $$4 = 0;
        for (Direction8 $$5 : this.sides) {
            $$4 |= 1 << $$5.ordinal();
        }
        $$0.putByte("Sides", (byte)$$4);
        if (!this.neighborBlockTicks.isEmpty()) {
            ListTag $$6 = new ListTag();
            this.neighborBlockTicks.forEach($$1 -> $$6.add($$1.save($$0 -> BuiltInRegistries.BLOCK.getKey((Block)$$0).toString())));
            $$0.put("neighbor_block_ticks", $$6);
        }
        if (!this.neighborFluidTicks.isEmpty()) {
            ListTag $$7 = new ListTag();
            this.neighborFluidTicks.forEach($$1 -> $$7.add($$1.save($$0 -> BuiltInRegistries.FLUID.getKey((Fluid)$$0).toString())));
            $$0.put("neighbor_fluid_ticks", $$7);
        }
        return $$0;
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    static enum BlockFixers implements BlockFixer
    {
        BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN}){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                return $$0;
            }
        }
        ,
        DEFAULT(new Block[0]){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                return $$0.updateShape($$1, $$3.getBlockState($$5), $$3, $$4, $$5);
            }
        }
        ,
        CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                if ($$2.is($$0.getBlock()) && $$1.getAxis().isHorizontal() && $$0.getValue(ChestBlock.TYPE) == ChestType.SINGLE && $$2.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
                    Direction $$6 = $$0.getValue(ChestBlock.FACING);
                    if ($$1.getAxis() != $$6.getAxis() && $$6 == $$2.getValue(ChestBlock.FACING)) {
                        ChestType $$7 = $$1 == $$6.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                        $$3.setBlock($$5, (BlockState)$$2.setValue(ChestBlock.TYPE, $$7.getOpposite()), 18);
                        if ($$6 == Direction.NORTH || $$6 == Direction.EAST) {
                            BlockEntity $$8 = $$3.getBlockEntity($$4);
                            BlockEntity $$9 = $$3.getBlockEntity($$5);
                            if ($$8 instanceof ChestBlockEntity && $$9 instanceof ChestBlockEntity) {
                                ChestBlockEntity.swapContents((ChestBlockEntity)$$8, (ChestBlockEntity)$$9);
                            }
                        }
                        return (BlockState)$$0.setValue(ChestBlock.TYPE, $$7);
                    }
                }
                return $$0;
            }
        }
        ,
        LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}){
            private final ThreadLocal<List<ObjectSet<BlockPos>>> queue = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity((int)7));

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                BlockState $$6 = $$0.updateShape($$1, $$3.getBlockState($$5), $$3, $$4, $$5);
                if ($$0 != $$6) {
                    int $$7 = $$6.getValue(BlockStateProperties.DISTANCE);
                    List $$8 = (List)this.queue.get();
                    if ($$8.isEmpty()) {
                        for (int $$9 = 0; $$9 < 7; ++$$9) {
                            $$8.add((Object)new ObjectOpenHashSet());
                        }
                    }
                    ((ObjectSet)$$8.get($$7)).add((Object)$$4.immutable());
                }
                return $$0;
            }

            @Override
            public void processChunk(LevelAccessor $$0) {
                BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
                List $$2 = (List)this.queue.get();
                for (int $$3 = 2; $$3 < $$2.size(); ++$$3) {
                    int $$4 = $$3 - 1;
                    ObjectSet $$5 = (ObjectSet)$$2.get($$4);
                    ObjectSet $$6 = (ObjectSet)$$2.get($$3);
                    for (BlockPos $$7 : $$5) {
                        BlockState $$8 = $$0.getBlockState($$7);
                        if ($$8.getValue(BlockStateProperties.DISTANCE) < $$4) continue;
                        $$0.setBlock($$7, (BlockState)$$8.setValue(BlockStateProperties.DISTANCE, $$4), 18);
                        if ($$3 == 7) continue;
                        for (Direction $$9 : DIRECTIONS) {
                            $$1.setWithOffset((Vec3i)$$7, $$9);
                            BlockState $$10 = $$0.getBlockState($$1);
                            if (!$$10.hasProperty(BlockStateProperties.DISTANCE) || $$8.getValue(BlockStateProperties.DISTANCE) <= $$3) continue;
                            $$6.add((Object)$$1.immutable());
                        }
                    }
                }
                $$2.clear();
            }
        }
        ,
        STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                StemGrownBlock $$6;
                if ($$0.getValue(StemBlock.AGE) == 7 && $$2.is($$6 = ((StemBlock)$$0.getBlock()).getFruit())) {
                    return (BlockState)$$6.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, $$1);
                }
                return $$0;
            }
        };

        public static final Direction[] DIRECTIONS;

        BlockFixers(Block ... $$0) {
            this(false, $$0);
        }

        BlockFixers(boolean $$0, Block ... $$1) {
            for (Block $$2 : $$1) {
                MAP.put((Object)$$2, (Object)this);
            }
            if ($$0) {
                CHUNKY_FIXERS.add((Object)this);
            }
        }

        static {
            DIRECTIONS = Direction.values();
        }
    }

    public static interface BlockFixer {
        public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6);

        default public void processChunk(LevelAccessor $$0) {
        }
    }
}