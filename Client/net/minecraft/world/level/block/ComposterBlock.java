/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ComposterBlock
extends Block
implements WorldlyContainerHolder {
    public static final int READY = 8;
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 7;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
    public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap();
    private static final int AABB_SIDE_THICKNESS = 2;
    private static final VoxelShape OUTER_SHAPE = Shapes.block();
    private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[9], $$0 -> {
        for (int $$1 = 0; $$1 < 8; ++$$1) {
            $$0[$$1] = Shapes.join(OUTER_SHAPE, Block.box(2.0, Math.max((int)2, (int)(1 + $$1 * 2)), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
        }
        $$0[8] = $$0[7];
    });

    public static void bootStrap() {
        COMPOSTABLES.defaultReturnValue(-1.0f);
        float $$0 = 0.3f;
        float $$1 = 0.5f;
        float $$2 = 0.65f;
        float $$3 = 0.85f;
        float $$4 = 1.0f;
        ComposterBlock.add(0.3f, Items.JUNGLE_LEAVES);
        ComposterBlock.add(0.3f, Items.OAK_LEAVES);
        ComposterBlock.add(0.3f, Items.SPRUCE_LEAVES);
        ComposterBlock.add(0.3f, Items.DARK_OAK_LEAVES);
        ComposterBlock.add(0.3f, Items.ACACIA_LEAVES);
        ComposterBlock.add(0.3f, Items.BIRCH_LEAVES);
        ComposterBlock.add(0.3f, Items.AZALEA_LEAVES);
        ComposterBlock.add(0.3f, Items.MANGROVE_LEAVES);
        ComposterBlock.add(0.3f, Items.OAK_SAPLING);
        ComposterBlock.add(0.3f, Items.SPRUCE_SAPLING);
        ComposterBlock.add(0.3f, Items.BIRCH_SAPLING);
        ComposterBlock.add(0.3f, Items.JUNGLE_SAPLING);
        ComposterBlock.add(0.3f, Items.ACACIA_SAPLING);
        ComposterBlock.add(0.3f, Items.DARK_OAK_SAPLING);
        ComposterBlock.add(0.3f, Items.MANGROVE_PROPAGULE);
        ComposterBlock.add(0.3f, Items.BEETROOT_SEEDS);
        ComposterBlock.add(0.3f, Items.DRIED_KELP);
        ComposterBlock.add(0.3f, Items.GRASS);
        ComposterBlock.add(0.3f, Items.KELP);
        ComposterBlock.add(0.3f, Items.MELON_SEEDS);
        ComposterBlock.add(0.3f, Items.PUMPKIN_SEEDS);
        ComposterBlock.add(0.3f, Items.SEAGRASS);
        ComposterBlock.add(0.3f, Items.SWEET_BERRIES);
        ComposterBlock.add(0.3f, Items.GLOW_BERRIES);
        ComposterBlock.add(0.3f, Items.WHEAT_SEEDS);
        ComposterBlock.add(0.3f, Items.MOSS_CARPET);
        ComposterBlock.add(0.3f, Items.SMALL_DRIPLEAF);
        ComposterBlock.add(0.3f, Items.HANGING_ROOTS);
        ComposterBlock.add(0.3f, Items.MANGROVE_ROOTS);
        ComposterBlock.add(0.5f, Items.DRIED_KELP_BLOCK);
        ComposterBlock.add(0.5f, Items.TALL_GRASS);
        ComposterBlock.add(0.5f, Items.FLOWERING_AZALEA_LEAVES);
        ComposterBlock.add(0.5f, Items.CACTUS);
        ComposterBlock.add(0.5f, Items.SUGAR_CANE);
        ComposterBlock.add(0.5f, Items.VINE);
        ComposterBlock.add(0.5f, Items.NETHER_SPROUTS);
        ComposterBlock.add(0.5f, Items.WEEPING_VINES);
        ComposterBlock.add(0.5f, Items.TWISTING_VINES);
        ComposterBlock.add(0.5f, Items.MELON_SLICE);
        ComposterBlock.add(0.5f, Items.GLOW_LICHEN);
        ComposterBlock.add(0.65f, Items.SEA_PICKLE);
        ComposterBlock.add(0.65f, Items.LILY_PAD);
        ComposterBlock.add(0.65f, Items.PUMPKIN);
        ComposterBlock.add(0.65f, Items.CARVED_PUMPKIN);
        ComposterBlock.add(0.65f, Items.MELON);
        ComposterBlock.add(0.65f, Items.APPLE);
        ComposterBlock.add(0.65f, Items.BEETROOT);
        ComposterBlock.add(0.65f, Items.CARROT);
        ComposterBlock.add(0.65f, Items.COCOA_BEANS);
        ComposterBlock.add(0.65f, Items.POTATO);
        ComposterBlock.add(0.65f, Items.WHEAT);
        ComposterBlock.add(0.65f, Items.BROWN_MUSHROOM);
        ComposterBlock.add(0.65f, Items.RED_MUSHROOM);
        ComposterBlock.add(0.65f, Items.MUSHROOM_STEM);
        ComposterBlock.add(0.65f, Items.CRIMSON_FUNGUS);
        ComposterBlock.add(0.65f, Items.WARPED_FUNGUS);
        ComposterBlock.add(0.65f, Items.NETHER_WART);
        ComposterBlock.add(0.65f, Items.CRIMSON_ROOTS);
        ComposterBlock.add(0.65f, Items.WARPED_ROOTS);
        ComposterBlock.add(0.65f, Items.SHROOMLIGHT);
        ComposterBlock.add(0.65f, Items.DANDELION);
        ComposterBlock.add(0.65f, Items.POPPY);
        ComposterBlock.add(0.65f, Items.BLUE_ORCHID);
        ComposterBlock.add(0.65f, Items.ALLIUM);
        ComposterBlock.add(0.65f, Items.AZURE_BLUET);
        ComposterBlock.add(0.65f, Items.RED_TULIP);
        ComposterBlock.add(0.65f, Items.ORANGE_TULIP);
        ComposterBlock.add(0.65f, Items.WHITE_TULIP);
        ComposterBlock.add(0.65f, Items.PINK_TULIP);
        ComposterBlock.add(0.65f, Items.OXEYE_DAISY);
        ComposterBlock.add(0.65f, Items.CORNFLOWER);
        ComposterBlock.add(0.65f, Items.LILY_OF_THE_VALLEY);
        ComposterBlock.add(0.65f, Items.WITHER_ROSE);
        ComposterBlock.add(0.65f, Items.FERN);
        ComposterBlock.add(0.65f, Items.SUNFLOWER);
        ComposterBlock.add(0.65f, Items.LILAC);
        ComposterBlock.add(0.65f, Items.ROSE_BUSH);
        ComposterBlock.add(0.65f, Items.PEONY);
        ComposterBlock.add(0.65f, Items.LARGE_FERN);
        ComposterBlock.add(0.65f, Items.SPORE_BLOSSOM);
        ComposterBlock.add(0.65f, Items.AZALEA);
        ComposterBlock.add(0.65f, Items.MOSS_BLOCK);
        ComposterBlock.add(0.65f, Items.BIG_DRIPLEAF);
        ComposterBlock.add(0.85f, Items.HAY_BLOCK);
        ComposterBlock.add(0.85f, Items.BROWN_MUSHROOM_BLOCK);
        ComposterBlock.add(0.85f, Items.RED_MUSHROOM_BLOCK);
        ComposterBlock.add(0.85f, Items.NETHER_WART_BLOCK);
        ComposterBlock.add(0.85f, Items.WARPED_WART_BLOCK);
        ComposterBlock.add(0.85f, Items.FLOWERING_AZALEA);
        ComposterBlock.add(0.85f, Items.BREAD);
        ComposterBlock.add(0.85f, Items.BAKED_POTATO);
        ComposterBlock.add(0.85f, Items.COOKIE);
        ComposterBlock.add(1.0f, Items.CAKE);
        ComposterBlock.add(1.0f, Items.PUMPKIN_PIE);
    }

    private static void add(float $$0, ItemLike $$1) {
        COMPOSTABLES.put((Object)$$1.asItem(), $$0);
    }

    public ComposterBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
    }

    public static void handleFill(Level $$0, BlockPos $$1, boolean $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        $$0.playLocalSound($$1, $$2 ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        double $$4 = $$3.getShape($$0, $$1).max(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        double $$5 = 0.13125f;
        double $$6 = 0.7375f;
        RandomSource $$7 = $$0.getRandom();
        for (int $$8 = 0; $$8 < 10; ++$$8) {
            double $$9 = $$7.nextGaussian() * 0.02;
            double $$10 = $$7.nextGaussian() * 0.02;
            double $$11 = $$7.nextGaussian() * 0.02;
            $$0.addParticle(ParticleTypes.COMPOSTER, (double)$$1.getX() + (double)0.13125f + (double)0.7375f * (double)$$7.nextFloat(), (double)$$1.getY() + $$4 + (double)$$7.nextFloat() * (1.0 - $$4), (double)$$1.getZ() + (double)0.13125f + (double)0.7375f * (double)$$7.nextFloat(), $$9, $$10, $$11);
        }
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES[$$0.getValue(LEVEL)];
    }

    @Override
    public VoxelShape getInteractionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return OUTER_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES[0];
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.getValue(LEVEL) == 7) {
            $$1.scheduleTick($$2, $$0.getBlock(), 20);
        }
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        int $$6 = $$0.getValue(LEVEL);
        ItemStack $$7 = $$3.getItemInHand($$4);
        if ($$6 < 8 && COMPOSTABLES.containsKey((Object)$$7.getItem())) {
            if ($$6 < 7 && !$$1.isClientSide) {
                BlockState $$8 = ComposterBlock.addItem($$0, $$1, $$2, $$7);
                $$1.levelEvent(1500, $$2, $$0 != $$8 ? 1 : 0);
                $$3.awardStat(Stats.ITEM_USED.get($$7.getItem()));
                if (!$$3.getAbilities().instabuild) {
                    $$7.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        if ($$6 == 8) {
            ComposterBlock.extractProduce($$0, $$1, $$2);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static BlockState insertItem(BlockState $$0, ServerLevel $$1, ItemStack $$2, BlockPos $$3) {
        int $$4 = $$0.getValue(LEVEL);
        if ($$4 < 7 && COMPOSTABLES.containsKey((Object)$$2.getItem())) {
            BlockState $$5 = ComposterBlock.addItem($$0, $$1, $$3, $$2);
            $$2.shrink(1);
            return $$5;
        }
        return $$0;
    }

    public static BlockState extractProduce(BlockState $$0, Level $$1, BlockPos $$2) {
        if (!$$1.isClientSide) {
            float $$3 = 0.7f;
            double $$4 = (double)($$1.random.nextFloat() * 0.7f) + (double)0.15f;
            double $$5 = (double)($$1.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
            double $$6 = (double)($$1.random.nextFloat() * 0.7f) + (double)0.15f;
            ItemEntity $$7 = new ItemEntity($$1, (double)$$2.getX() + $$4, (double)$$2.getY() + $$5, (double)$$2.getZ() + $$6, new ItemStack(Items.BONE_MEAL));
            $$7.setDefaultPickUpDelay();
            $$1.addFreshEntity($$7);
        }
        BlockState $$8 = ComposterBlock.empty($$0, $$1, $$2);
        $$1.playSound(null, $$2, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
        return $$8;
    }

    static BlockState empty(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
        BlockState $$3 = (BlockState)$$0.setValue(LEVEL, 0);
        $$1.setBlock($$2, $$3, 3);
        return $$3;
    }

    static BlockState addItem(BlockState $$0, LevelAccessor $$1, BlockPos $$2, ItemStack $$3) {
        int $$4 = $$0.getValue(LEVEL);
        float $$5 = COMPOSTABLES.getFloat((Object)$$3.getItem());
        if ($$4 == 0 && $$5 > 0.0f || $$1.getRandom().nextDouble() < (double)$$5) {
            int $$6 = $$4 + 1;
            BlockState $$7 = (BlockState)$$0.setValue(LEVEL, $$6);
            $$1.setBlock($$2, $$7, 3);
            if ($$6 == 7) {
                $$1.scheduleTick($$2, $$0.getBlock(), 20);
            }
            return $$7;
        }
        return $$0;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(LEVEL) == 7) {
            $$1.setBlock($$2, (BlockState)$$0.cycle(LEVEL), 3);
            $$1.playSound(null, $$2, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return $$0.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(LEVEL);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    @Override
    public WorldlyContainer getContainer(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
        int $$3 = $$0.getValue(LEVEL);
        if ($$3 == 8) {
            return new OutputContainer($$0, $$1, $$2, new ItemStack(Items.BONE_MEAL));
        }
        if ($$3 < 7) {
            return new InputContainer($$0, $$1, $$2);
        }
        return new EmptyContainer();
    }

    static class OutputContainer
    extends SimpleContainer
    implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public OutputContainer(BlockState $$0, LevelAccessor $$1, BlockPos $$2, ItemStack $$3) {
            super($$3);
            this.state = $$0;
            this.level = $$1;
            this.pos = $$2;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(Direction $$0) {
            int[] nArray;
            if ($$0 == Direction.DOWN) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canPlaceItemThroughFace(int $$0, ItemStack $$1, @Nullable Direction $$2) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int $$0, ItemStack $$1, Direction $$2) {
            return !this.changed && $$2 == Direction.DOWN && $$1.is(Items.BONE_MEAL);
        }

        @Override
        public void setChanged() {
            ComposterBlock.empty(this.state, this.level, this.pos);
            this.changed = true;
        }
    }

    static class InputContainer
    extends SimpleContainer
    implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public InputContainer(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
            super(1);
            this.state = $$0;
            this.level = $$1;
            this.pos = $$2;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(Direction $$0) {
            int[] nArray;
            if ($$0 == Direction.UP) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canPlaceItemThroughFace(int $$0, ItemStack $$1, @Nullable Direction $$2) {
            return !this.changed && $$2 == Direction.UP && COMPOSTABLES.containsKey((Object)$$1.getItem());
        }

        @Override
        public boolean canTakeItemThroughFace(int $$0, ItemStack $$1, Direction $$2) {
            return false;
        }

        @Override
        public void setChanged() {
            ItemStack $$0 = this.getItem(0);
            if (!$$0.isEmpty()) {
                this.changed = true;
                BlockState $$1 = ComposterBlock.addItem(this.state, this.level, this.pos, $$0);
                this.level.levelEvent(1500, this.pos, $$1 != this.state ? 1 : 0);
                this.removeItemNoUpdate(0);
            }
        }
    }

    static class EmptyContainer
    extends SimpleContainer
    implements WorldlyContainer {
        public EmptyContainer() {
            super(0);
        }

        @Override
        public int[] getSlotsForFace(Direction $$0) {
            return new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int $$0, ItemStack $$1, @Nullable Direction $$2) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int $$0, ItemStack $$1, Direction $$2) {
            return false;
        }
    }
}