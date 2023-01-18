/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.ThreadLocal
 *  java.util.EnumMap
 *  java.util.Map
 *  java.util.Map$Entry
 */
package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FlowingFluid
extends Fluid {
    public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_FLOWING;
    private static final int CACHE_SIZE = 200;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> $$0 = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(200){

            protected void rehash(int $$0) {
            }
        };
        $$0.defaultReturnValue((byte)127);
        return $$0;
    });
    private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> $$0) {
        $$0.add(FALLING);
    }

    @Override
    public Vec3 getFlow(BlockGetter $$0, BlockPos $$1, FluidState $$2) {
        double $$3 = 0.0;
        double $$4 = 0.0;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            $$5.setWithOffset((Vec3i)$$1, $$6);
            FluidState $$7 = $$0.getFluidState($$5);
            if (!this.affectsFlow($$7)) continue;
            float $$8 = $$7.getOwnHeight();
            float $$9 = 0.0f;
            if ($$8 == 0.0f) {
                Vec3i $$10;
                FluidState $$11;
                if (!$$0.getBlockState($$5).getMaterial().blocksMotion() && this.affectsFlow($$11 = $$0.getFluidState((BlockPos)($$10 = $$5.below()))) && ($$8 = $$11.getOwnHeight()) > 0.0f) {
                    $$9 = $$2.getOwnHeight() - ($$8 - 0.8888889f);
                }
            } else if ($$8 > 0.0f) {
                $$9 = $$2.getOwnHeight() - $$8;
            }
            if ($$9 == 0.0f) continue;
            $$3 += (double)((float)$$6.getStepX() * $$9);
            $$4 += (double)((float)$$6.getStepZ() * $$9);
        }
        Vec3 $$12 = new Vec3($$3, 0.0, $$4);
        if ($$2.getValue(FALLING).booleanValue()) {
            for (Direction $$13 : Direction.Plane.HORIZONTAL) {
                $$5.setWithOffset((Vec3i)$$1, $$13);
                if (!this.isSolidFace($$0, $$5, $$13) && !this.isSolidFace($$0, (BlockPos)$$5.above(), $$13)) continue;
                $$12 = $$12.normalize().add(0.0, -6.0, 0.0);
                break;
            }
        }
        return $$12.normalize();
    }

    private boolean affectsFlow(FluidState $$0) {
        return $$0.isEmpty() || $$0.getType().isSame(this);
    }

    protected boolean isSolidFace(BlockGetter $$0, BlockPos $$1, Direction $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        FluidState $$4 = $$0.getFluidState($$1);
        if ($$4.getType().isSame(this)) {
            return false;
        }
        if ($$2 == Direction.UP) {
            return true;
        }
        if ($$3.getMaterial() == Material.ICE) {
            return false;
        }
        return $$3.isFaceSturdy($$0, $$1, $$2);
    }

    protected void spread(Level $$0, BlockPos $$1, FluidState $$2) {
        if ($$2.isEmpty()) {
            return;
        }
        BlockState $$3 = $$0.getBlockState($$1);
        Vec3i $$4 = $$1.below();
        BlockState $$5 = $$0.getBlockState((BlockPos)$$4);
        FluidState $$6 = this.getNewLiquid($$0, (BlockPos)$$4, $$5);
        if (this.canSpreadTo($$0, $$1, $$3, Direction.DOWN, (BlockPos)$$4, $$5, $$0.getFluidState((BlockPos)$$4), $$6.getType())) {
            this.spreadTo($$0, (BlockPos)$$4, $$5, Direction.DOWN, $$6);
            if (this.sourceNeighborCount($$0, $$1) >= 3) {
                this.spreadToSides($$0, $$1, $$2, $$3);
            }
        } else if ($$2.isSource() || !this.isWaterHole($$0, $$6.getType(), $$1, $$3, (BlockPos)$$4, $$5)) {
            this.spreadToSides($$0, $$1, $$2, $$3);
        }
    }

    private void spreadToSides(Level $$0, BlockPos $$1, FluidState $$2, BlockState $$3) {
        int $$4 = $$2.getAmount() - this.getDropOff($$0);
        if ($$2.getValue(FALLING).booleanValue()) {
            $$4 = 7;
        }
        if ($$4 <= 0) {
            return;
        }
        Map<Direction, FluidState> $$5 = this.getSpread($$0, $$1, $$3);
        for (Map.Entry $$6 : $$5.entrySet()) {
            BlockState $$10;
            Direction $$7 = (Direction)$$6.getKey();
            FluidState $$8 = (FluidState)$$6.getValue();
            Vec3i $$9 = $$1.relative($$7);
            if (!this.canSpreadTo($$0, $$1, $$3, $$7, (BlockPos)$$9, $$10 = $$0.getBlockState((BlockPos)$$9), $$0.getFluidState((BlockPos)$$9), $$8.getType())) continue;
            this.spreadTo($$0, (BlockPos)$$9, $$10, $$7, $$8);
        }
    }

    protected FluidState getNewLiquid(Level $$0, BlockPos $$1, BlockState $$2) {
        Vec3i $$11;
        BlockState $$12;
        FluidState $$13;
        int $$3 = 0;
        int $$4 = 0;
        for (Direction $$5 : Direction.Plane.HORIZONTAL) {
            Vec3i $$6 = $$1.relative($$5);
            BlockState $$7 = $$0.getBlockState((BlockPos)$$6);
            FluidState $$8 = $$7.getFluidState();
            if (!$$8.getType().isSame(this) || !this.canPassThroughWall($$5, $$0, $$1, $$2, (BlockPos)$$6, $$7)) continue;
            if ($$8.isSource()) {
                ++$$4;
            }
            $$3 = Math.max((int)$$3, (int)$$8.getAmount());
        }
        if (this.canConvertToSource($$0) && $$4 >= 2) {
            BlockState $$9 = $$0.getBlockState((BlockPos)$$1.below());
            FluidState $$10 = $$9.getFluidState();
            if ($$9.getMaterial().isSolid() || this.isSourceBlockOfThisType($$10)) {
                return this.getSource(false);
            }
        }
        if (!($$13 = ($$12 = $$0.getBlockState((BlockPos)($$11 = $$1.above()))).getFluidState()).isEmpty() && $$13.getType().isSame(this) && this.canPassThroughWall(Direction.UP, $$0, $$1, $$2, (BlockPos)$$11, $$12)) {
            return this.getFlowing(8, true);
        }
        int $$14 = $$3 - this.getDropOff($$0);
        if ($$14 <= 0) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return this.getFlowing($$14, false);
    }

    private boolean canPassThroughWall(Direction $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, BlockPos $$4, BlockState $$5) {
        VoxelShape $$12;
        VoxelShape $$11;
        boolean $$13;
        Object $$10;
        Object2ByteLinkedOpenHashMap $$7;
        if ($$3.getBlock().hasDynamicShape() || $$5.getBlock().hasDynamicShape()) {
            Object $$6 = null;
        } else {
            $$7 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
        }
        if ($$7 != null) {
            Block.BlockStatePairKey $$8 = new Block.BlockStatePairKey($$3, $$5, $$0);
            byte $$9 = $$7.getAndMoveToFirst((Object)$$8);
            if ($$9 != 127) {
                return $$9 != 0;
            }
        } else {
            $$10 = null;
        }
        boolean bl = $$13 = !Shapes.mergedFaceOccludes($$11 = $$3.getCollisionShape($$1, $$2), $$12 = $$5.getCollisionShape($$1, $$4), $$0);
        if ($$7 != null) {
            if ($$7.size() == 200) {
                $$7.removeLastByte();
            }
            $$7.putAndMoveToFirst($$10, (byte)($$13 ? 1 : 0));
        }
        return $$13;
    }

    public abstract Fluid getFlowing();

    public FluidState getFlowing(int $$0, boolean $$1) {
        return (FluidState)((FluidState)this.getFlowing().defaultFluidState().setValue(LEVEL, $$0)).setValue(FALLING, $$1);
    }

    public abstract Fluid getSource();

    public FluidState getSource(boolean $$0) {
        return (FluidState)this.getSource().defaultFluidState().setValue(FALLING, $$0);
    }

    protected abstract boolean canConvertToSource(Level var1);

    protected void spreadTo(LevelAccessor $$0, BlockPos $$1, BlockState $$2, Direction $$3, FluidState $$4) {
        if ($$2.getBlock() instanceof LiquidBlockContainer) {
            ((LiquidBlockContainer)((Object)$$2.getBlock())).placeLiquid($$0, $$1, $$2, $$4);
        } else {
            if (!$$2.isAir()) {
                this.beforeDestroyingBlock($$0, $$1, $$2);
            }
            $$0.setBlock($$1, $$4.createLegacyBlock(), 3);
        }
    }

    protected abstract void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3);

    private static short getCacheKey(BlockPos $$0, BlockPos $$1) {
        int $$2 = $$1.getX() - $$0.getX();
        int $$3 = $$1.getZ() - $$0.getZ();
        return (short)(($$2 + 128 & 0xFF) << 8 | $$3 + 128 & 0xFF);
    }

    protected int getSlopeDistance(LevelReader $$0, BlockPos $$1, int $$2, Direction $$3, BlockState $$4, BlockPos $$5, Short2ObjectMap<Pair<BlockState, FluidState>> $$6, Short2BooleanMap $$7) {
        int $$8 = 1000;
        for (Direction $$9 : Direction.Plane.HORIZONTAL) {
            int $$16;
            if ($$9 == $$3) continue;
            Vec3i $$10 = $$1.relative($$9);
            short $$11 = FlowingFluid.getCacheKey($$5, (BlockPos)$$10);
            Pair $$12 = (Pair)$$6.computeIfAbsent($$11, arg_0 -> FlowingFluid.lambda$getSlopeDistance$1($$0, (BlockPos)$$10, arg_0));
            BlockState $$13 = (BlockState)$$12.getFirst();
            FluidState $$14 = (FluidState)$$12.getSecond();
            if (!this.canPassThrough($$0, this.getFlowing(), $$1, $$4, $$9, (BlockPos)$$10, $$13, $$14)) continue;
            boolean $$15 = $$7.computeIfAbsent($$11, arg_0 -> this.lambda$getSlopeDistance$2((BlockPos)$$10, $$0, $$13, arg_0));
            if ($$15) {
                return $$2;
            }
            if ($$2 >= this.getSlopeFindDistance($$0) || ($$16 = this.getSlopeDistance($$0, (BlockPos)$$10, $$2 + 1, $$9.getOpposite(), $$13, $$5, $$6, $$7)) >= $$8) continue;
            $$8 = $$16;
        }
        return $$8;
    }

    private boolean isWaterHole(BlockGetter $$0, Fluid $$1, BlockPos $$2, BlockState $$3, BlockPos $$4, BlockState $$5) {
        if (!this.canPassThroughWall(Direction.DOWN, $$0, $$2, $$3, $$4, $$5)) {
            return false;
        }
        if ($$5.getFluidState().getType().isSame(this)) {
            return true;
        }
        return this.canHoldFluid($$0, $$4, $$5, $$1);
    }

    private boolean canPassThrough(BlockGetter $$0, Fluid $$1, BlockPos $$2, BlockState $$3, Direction $$4, BlockPos $$5, BlockState $$6, FluidState $$7) {
        return !this.isSourceBlockOfThisType($$7) && this.canPassThroughWall($$4, $$0, $$2, $$3, $$5, $$6) && this.canHoldFluid($$0, $$5, $$6, $$1);
    }

    private boolean isSourceBlockOfThisType(FluidState $$0) {
        return $$0.getType().isSame(this) && $$0.isSource();
    }

    protected abstract int getSlopeFindDistance(LevelReader var1);

    private int sourceNeighborCount(LevelReader $$0, BlockPos $$1) {
        int $$2 = 0;
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            Vec3i $$4 = $$1.relative($$3);
            FluidState $$5 = $$0.getFluidState((BlockPos)$$4);
            if (!this.isSourceBlockOfThisType($$5)) continue;
            ++$$2;
        }
        return $$2;
    }

    protected Map<Direction, FluidState> getSpread(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = 1000;
        EnumMap $$4 = Maps.newEnumMap(Direction.class);
        Short2ObjectOpenHashMap $$5 = new Short2ObjectOpenHashMap();
        Short2BooleanOpenHashMap $$6 = new Short2BooleanOpenHashMap();
        for (Direction $$7 : Direction.Plane.HORIZONTAL) {
            int $$17;
            Vec3i $$8 = $$1.relative($$7);
            short $$9 = FlowingFluid.getCacheKey($$1, (BlockPos)$$8);
            Pair $$10 = (Pair)$$5.computeIfAbsent($$9, arg_0 -> FlowingFluid.lambda$getSpread$3($$0, (BlockPos)$$8, arg_0));
            BlockState $$11 = (BlockState)$$10.getFirst();
            FluidState $$12 = (FluidState)$$10.getSecond();
            FluidState $$13 = this.getNewLiquid($$0, (BlockPos)$$8, $$11);
            if (!this.canPassThrough($$0, $$13.getType(), $$1, $$2, $$7, (BlockPos)$$8, $$11, $$12)) continue;
            Vec3i $$14 = ((BlockPos)$$8).below();
            boolean $$15 = $$6.computeIfAbsent($$9, arg_0 -> this.lambda$getSpread$4($$0, (BlockPos)$$14, (BlockPos)$$8, $$11, arg_0));
            if ($$15) {
                boolean $$16 = false;
            } else {
                $$17 = this.getSlopeDistance($$0, (BlockPos)$$8, 1, $$7.getOpposite(), $$11, $$1, (Short2ObjectMap<Pair<BlockState, FluidState>>)$$5, (Short2BooleanMap)$$6);
            }
            if ($$17 < $$3) {
                $$4.clear();
            }
            if ($$17 > $$3) continue;
            $$4.put((Object)$$7, (Object)$$13);
            $$3 = $$17;
        }
        return $$4;
    }

    private boolean canHoldFluid(BlockGetter $$0, BlockPos $$1, BlockState $$2, Fluid $$3) {
        Block $$4 = $$2.getBlock();
        if ($$4 instanceof LiquidBlockContainer) {
            return ((LiquidBlockContainer)((Object)$$4)).canPlaceLiquid($$0, $$1, $$2, $$3);
        }
        if ($$4 instanceof DoorBlock || $$2.is(BlockTags.SIGNS) || $$2.is(Blocks.LADDER) || $$2.is(Blocks.SUGAR_CANE) || $$2.is(Blocks.BUBBLE_COLUMN)) {
            return false;
        }
        Material $$5 = $$2.getMaterial();
        if ($$5 == Material.PORTAL || $$5 == Material.STRUCTURAL_AIR || $$5 == Material.WATER_PLANT || $$5 == Material.REPLACEABLE_WATER_PLANT) {
            return false;
        }
        return !$$5.blocksMotion();
    }

    protected boolean canSpreadTo(BlockGetter $$0, BlockPos $$1, BlockState $$2, Direction $$3, BlockPos $$4, BlockState $$5, FluidState $$6, Fluid $$7) {
        return $$6.canBeReplacedWith($$0, $$4, $$7, $$3) && this.canPassThroughWall($$3, $$0, $$1, $$2, $$4, $$5) && this.canHoldFluid($$0, $$4, $$5, $$7);
    }

    protected abstract int getDropOff(LevelReader var1);

    protected int getSpreadDelay(Level $$0, BlockPos $$1, FluidState $$2, FluidState $$3) {
        return this.getTickDelay($$0);
    }

    @Override
    public void tick(Level $$0, BlockPos $$1, FluidState $$2) {
        if (!$$2.isSource()) {
            FluidState $$3 = this.getNewLiquid($$0, $$1, $$0.getBlockState($$1));
            int $$4 = this.getSpreadDelay($$0, $$1, $$2, $$3);
            if ($$3.isEmpty()) {
                $$2 = $$3;
                $$0.setBlock($$1, Blocks.AIR.defaultBlockState(), 3);
            } else if (!$$3.equals($$2)) {
                $$2 = $$3;
                BlockState $$5 = $$2.createLegacyBlock();
                $$0.setBlock($$1, $$5, 2);
                $$0.scheduleTick($$1, $$2.getType(), $$4);
                $$0.updateNeighborsAt($$1, $$5.getBlock());
            }
        }
        this.spread($$0, $$1, $$2);
    }

    protected static int getLegacyLevel(FluidState $$0) {
        if ($$0.isSource()) {
            return 0;
        }
        return 8 - Math.min((int)$$0.getAmount(), (int)8) + ($$0.getValue(FALLING) != false ? 8 : 0);
    }

    private static boolean hasSameAbove(FluidState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getType().isSame($$1.getFluidState((BlockPos)$$2.above()).getType());
    }

    @Override
    public float getHeight(FluidState $$0, BlockGetter $$1, BlockPos $$2) {
        if (FlowingFluid.hasSameAbove($$0, $$1, $$2)) {
            return 1.0f;
        }
        return $$0.getOwnHeight();
    }

    @Override
    public float getOwnHeight(FluidState $$0) {
        return (float)$$0.getAmount() / 9.0f;
    }

    @Override
    public abstract int getAmount(FluidState var1);

    @Override
    public VoxelShape getShape(FluidState $$0, BlockGetter $$1, BlockPos $$22) {
        if ($$0.getAmount() == 9 && FlowingFluid.hasSameAbove($$0, $$1, $$22)) {
            return Shapes.block();
        }
        return (VoxelShape)this.shapes.computeIfAbsent((Object)$$0, $$2 -> Shapes.box(0.0, 0.0, 0.0, 1.0, $$2.getHeight($$1, $$22), 1.0));
    }

    private /* synthetic */ boolean lambda$getSpread$4(Level $$0, BlockPos $$1, BlockPos $$2, BlockState $$3, short $$4) {
        BlockState $$5 = $$0.getBlockState($$1);
        return this.isWaterHole($$0, this.getFlowing(), $$2, $$3, $$1, $$5);
    }

    private static /* synthetic */ Pair lambda$getSpread$3(Level $$0, BlockPos $$1, short $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        return Pair.of((Object)$$3, (Object)$$3.getFluidState());
    }

    private /* synthetic */ boolean lambda$getSlopeDistance$2(BlockPos $$0, LevelReader $$1, BlockState $$2, short $$3) {
        Vec3i $$4 = $$0.below();
        BlockState $$5 = $$1.getBlockState((BlockPos)$$4);
        return this.isWaterHole($$1, this.getFlowing(), $$0, $$2, (BlockPos)$$4, $$5);
    }

    private static /* synthetic */ Pair lambda$getSlopeDistance$1(LevelReader $$0, BlockPos $$1, short $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        return Pair.of((Object)$$3, (Object)$$3.getFluidState());
    }
}