/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class StructureTemplate {
    public static final String PALETTE_TAG = "palette";
    public static final String PALETTE_LIST_TAG = "palettes";
    public static final String ENTITIES_TAG = "entities";
    public static final String BLOCKS_TAG = "blocks";
    public static final String BLOCK_TAG_POS = "pos";
    public static final String BLOCK_TAG_STATE = "state";
    public static final String BLOCK_TAG_NBT = "nbt";
    public static final String ENTITY_TAG_POS = "pos";
    public static final String ENTITY_TAG_BLOCKPOS = "blockPos";
    public static final String ENTITY_TAG_NBT = "nbt";
    public static final String SIZE_TAG = "size";
    static final int CHUNK_SIZE = 16;
    private final List<Palette> palettes = Lists.newArrayList();
    private final List<StructureEntityInfo> entityInfoList = Lists.newArrayList();
    private Vec3i size = Vec3i.ZERO;
    private String author = "?";

    public Vec3i getSize() {
        return this.size;
    }

    public void setAuthor(String $$0) {
        this.author = $$0;
    }

    public String getAuthor() {
        return this.author;
    }

    public void fillFromWorld(Level $$0, BlockPos $$1, Vec3i $$2, boolean $$3, @Nullable Block $$4) {
        if ($$2.getX() < 1 || $$2.getY() < 1 || $$2.getZ() < 1) {
            return;
        }
        BlockPos $$5 = ((BlockPos)$$1.offset($$2)).offset(-1, -1, -1);
        ArrayList $$6 = Lists.newArrayList();
        ArrayList $$7 = Lists.newArrayList();
        ArrayList $$8 = Lists.newArrayList();
        BlockPos $$9 = new BlockPos(Math.min((int)$$1.getX(), (int)$$5.getX()), Math.min((int)$$1.getY(), (int)$$5.getY()), Math.min((int)$$1.getZ(), (int)$$5.getZ()));
        BlockPos $$10 = new BlockPos(Math.max((int)$$1.getX(), (int)$$5.getX()), Math.max((int)$$1.getY(), (int)$$5.getY()), Math.max((int)$$1.getZ(), (int)$$5.getZ()));
        this.size = $$2;
        for (BlockPos $$11 : BlockPos.betweenClosed($$9, $$10)) {
            StructureBlockInfo $$16;
            Vec3i $$12 = $$11.subtract($$9);
            BlockState $$13 = $$0.getBlockState($$11);
            if ($$4 != null && $$13.is($$4)) continue;
            BlockEntity $$14 = $$0.getBlockEntity($$11);
            if ($$14 != null) {
                StructureBlockInfo $$15 = new StructureBlockInfo((BlockPos)$$12, $$13, $$14.saveWithId());
            } else {
                $$16 = new StructureBlockInfo((BlockPos)$$12, $$13, null);
            }
            StructureTemplate.addToLists($$16, (List<StructureBlockInfo>)$$6, (List<StructureBlockInfo>)$$7, (List<StructureBlockInfo>)$$8);
        }
        List<StructureBlockInfo> $$17 = StructureTemplate.buildInfoList((List<StructureBlockInfo>)$$6, (List<StructureBlockInfo>)$$7, (List<StructureBlockInfo>)$$8);
        this.palettes.clear();
        this.palettes.add((Object)new Palette($$17));
        if ($$3) {
            this.fillEntityList($$0, $$9, $$10.offset(1, 1, 1));
        } else {
            this.entityInfoList.clear();
        }
    }

    private static void addToLists(StructureBlockInfo $$0, List<StructureBlockInfo> $$1, List<StructureBlockInfo> $$2, List<StructureBlockInfo> $$3) {
        if ($$0.nbt != null) {
            $$2.add((Object)$$0);
        } else if (!$$0.state.getBlock().hasDynamicShape() && $$0.state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
            $$1.add((Object)$$0);
        } else {
            $$3.add((Object)$$0);
        }
    }

    private static List<StructureBlockInfo> buildInfoList(List<StructureBlockInfo> $$02, List<StructureBlockInfo> $$1, List<StructureBlockInfo> $$2) {
        Comparator $$3 = Comparator.comparingInt($$0 -> $$0.pos.getY()).thenComparingInt($$0 -> $$0.pos.getX()).thenComparingInt($$0 -> $$0.pos.getZ());
        $$02.sort($$3);
        $$2.sort($$3);
        $$1.sort($$3);
        ArrayList $$4 = Lists.newArrayList();
        $$4.addAll($$02);
        $$4.addAll($$2);
        $$4.addAll($$1);
        return $$4;
    }

    private void fillEntityList(Level $$02, BlockPos $$1, BlockPos $$2) {
        List $$3 = $$02.getEntitiesOfClass(Entity.class, new AABB($$1, $$2), $$0 -> !($$0 instanceof Player));
        this.entityInfoList.clear();
        for (Entity $$4 : $$3) {
            BlockPos $$8;
            Vec3 $$5 = new Vec3($$4.getX() - (double)$$1.getX(), $$4.getY() - (double)$$1.getY(), $$4.getZ() - (double)$$1.getZ());
            CompoundTag $$6 = new CompoundTag();
            $$4.save($$6);
            if ($$4 instanceof Painting) {
                Vec3i $$7 = ((Painting)$$4).getPos().subtract($$1);
            } else {
                $$8 = new BlockPos($$5);
            }
            this.entityInfoList.add((Object)new StructureEntityInfo($$5, $$8, $$6.copy()));
        }
    }

    public List<StructureBlockInfo> filterBlocks(BlockPos $$0, StructurePlaceSettings $$1, Block $$2) {
        return this.filterBlocks($$0, $$1, $$2, true);
    }

    public ObjectArrayList<StructureBlockInfo> filterBlocks(BlockPos $$0, StructurePlaceSettings $$1, Block $$2, boolean $$3) {
        ObjectArrayList $$4 = new ObjectArrayList();
        BoundingBox $$5 = $$1.getBoundingBox();
        if (this.palettes.isEmpty()) {
            return $$4;
        }
        for (StructureBlockInfo $$6 : $$1.getRandomPalette(this.palettes, $$0).blocks($$2)) {
            BlockPos $$7;
            Vec3i vec3i = $$7 = $$3 ? StructureTemplate.calculateRelativePosition($$1, $$6.pos).offset($$0) : $$6.pos;
            if ($$5 != null && !$$5.isInside($$7)) continue;
            $$4.add((Object)new StructureBlockInfo($$7, $$6.state.rotate($$1.getRotation()), $$6.nbt));
        }
        return $$4;
    }

    public BlockPos calculateConnectedPosition(StructurePlaceSettings $$0, BlockPos $$1, StructurePlaceSettings $$2, BlockPos $$3) {
        BlockPos $$4 = StructureTemplate.calculateRelativePosition($$0, $$1);
        BlockPos $$5 = StructureTemplate.calculateRelativePosition($$2, $$3);
        return $$4.subtract($$5);
    }

    public static BlockPos calculateRelativePosition(StructurePlaceSettings $$0, BlockPos $$1) {
        return StructureTemplate.transform($$1, $$0.getMirror(), $$0.getRotation(), $$0.getRotationPivot());
    }

    public boolean placeInWorld(ServerLevelAccessor $$0, BlockPos $$1, BlockPos $$2, StructurePlaceSettings $$3, RandomSource $$4, int $$5) {
        if (this.palettes.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> $$6 = $$3.getRandomPalette(this.palettes, $$1).blocks();
        if ($$6.isEmpty() && ($$3.isIgnoreEntities() || this.entityInfoList.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BoundingBox $$7 = $$3.getBoundingBox();
        ArrayList $$8 = Lists.newArrayListWithCapacity((int)($$3.shouldKeepLiquids() ? $$6.size() : 0));
        ArrayList $$9 = Lists.newArrayListWithCapacity((int)($$3.shouldKeepLiquids() ? $$6.size() : 0));
        ArrayList $$10 = Lists.newArrayListWithCapacity((int)$$6.size());
        int $$11 = Integer.MAX_VALUE;
        int $$12 = Integer.MAX_VALUE;
        int $$13 = Integer.MAX_VALUE;
        int $$14 = Integer.MIN_VALUE;
        int $$15 = Integer.MIN_VALUE;
        int $$16 = Integer.MIN_VALUE;
        List<StructureBlockInfo> $$17 = StructureTemplate.processBlockInfos($$0, $$1, $$2, $$3, $$6);
        for (StructureBlockInfo $$18 : $$17) {
            BlockEntity $$23;
            BlockPos $$19 = $$18.pos;
            if ($$7 != null && !$$7.isInside($$19)) continue;
            FluidState $$20 = $$3.shouldKeepLiquids() ? $$0.getFluidState($$19) : null;
            BlockState $$21 = $$18.state.mirror($$3.getMirror()).rotate($$3.getRotation());
            if ($$18.nbt != null) {
                BlockEntity $$22 = $$0.getBlockEntity($$19);
                Clearable.tryClear($$22);
                $$0.setBlock($$19, Blocks.BARRIER.defaultBlockState(), 20);
            }
            if (!$$0.setBlock($$19, $$21, $$5)) continue;
            $$11 = Math.min((int)$$11, (int)$$19.getX());
            $$12 = Math.min((int)$$12, (int)$$19.getY());
            $$13 = Math.min((int)$$13, (int)$$19.getZ());
            $$14 = Math.max((int)$$14, (int)$$19.getX());
            $$15 = Math.max((int)$$15, (int)$$19.getY());
            $$16 = Math.max((int)$$16, (int)$$19.getZ());
            $$10.add((Object)Pair.of((Object)$$19, (Object)$$18.nbt));
            if ($$18.nbt != null && ($$23 = $$0.getBlockEntity($$19)) != null) {
                if ($$23 instanceof RandomizableContainerBlockEntity) {
                    $$18.nbt.putLong("LootTableSeed", $$4.nextLong());
                }
                $$23.load($$18.nbt);
            }
            if ($$20 == null) continue;
            if ($$21.getFluidState().isSource()) {
                $$9.add((Object)$$19);
                continue;
            }
            if (!($$21.getBlock() instanceof LiquidBlockContainer)) continue;
            ((LiquidBlockContainer)((Object)$$21.getBlock())).placeLiquid($$0, $$19, $$21, $$20);
            if ($$20.isSource()) continue;
            $$8.add((Object)$$19);
        }
        boolean $$24 = true;
        Direction[] $$25 = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        while ($$24 && !$$8.isEmpty()) {
            $$24 = false;
            Iterator $$26 = $$8.iterator();
            while ($$26.hasNext()) {
                BlockState $$32;
                Object $$33;
                BlockPos $$27 = (BlockPos)$$26.next();
                FluidState $$28 = $$0.getFluidState($$27);
                for (int $$29 = 0; $$29 < $$25.length && !$$28.isSource(); ++$$29) {
                    Vec3i $$30 = $$27.relative($$25[$$29]);
                    FluidState $$31 = $$0.getFluidState((BlockPos)$$30);
                    if (!$$31.isSource() || $$9.contains((Object)$$30)) continue;
                    $$28 = $$31;
                }
                if (!$$28.isSource() || !(($$33 = ($$32 = $$0.getBlockState($$27)).getBlock()) instanceof LiquidBlockContainer)) continue;
                ((LiquidBlockContainer)$$33).placeLiquid($$0, $$27, $$32, $$28);
                $$24 = true;
                $$26.remove();
            }
        }
        if ($$11 <= $$14) {
            if (!$$3.getKnownShape()) {
                BitSetDiscreteVoxelShape $$34 = new BitSetDiscreteVoxelShape($$14 - $$11 + 1, $$15 - $$12 + 1, $$16 - $$13 + 1);
                int $$35 = $$11;
                int $$36 = $$12;
                int $$37 = $$13;
                for (Pair $$38 : $$10) {
                    BlockPos $$39 = (BlockPos)$$38.getFirst();
                    ((DiscreteVoxelShape)$$34).fill($$39.getX() - $$35, $$39.getY() - $$36, $$39.getZ() - $$37);
                }
                StructureTemplate.updateShapeAtEdge($$0, $$5, $$34, $$35, $$36, $$37);
            }
            for (Pair $$40 : $$10) {
                BlockEntity $$44;
                BlockPos $$41 = (BlockPos)$$40.getFirst();
                if (!$$3.getKnownShape()) {
                    BlockState $$43;
                    BlockState $$42 = $$0.getBlockState($$41);
                    if ($$42 != ($$43 = Block.updateFromNeighbourShapes($$42, $$0, $$41))) {
                        $$0.setBlock($$41, $$43, $$5 & 0xFFFFFFFE | 0x10);
                    }
                    $$0.blockUpdated($$41, $$43.getBlock());
                }
                if ($$40.getSecond() == null || ($$44 = $$0.getBlockEntity($$41)) == null) continue;
                $$44.setChanged();
            }
        }
        if (!$$3.isIgnoreEntities()) {
            this.placeEntities($$0, $$1, $$3.getMirror(), $$3.getRotation(), $$3.getRotationPivot(), $$7, $$3.shouldFinalizeEntities());
        }
        return true;
    }

    public static void updateShapeAtEdge(LevelAccessor $$0, int $$1, DiscreteVoxelShape $$2, int $$3, int $$4, int $$52) {
        $$2.forAllFaces(($$5, $$6, $$7, $$8) -> {
            BlockState $$14;
            BlockState $$12;
            BlockState $$13;
            BlockPos $$9 = new BlockPos($$3 + $$6, $$4 + $$7, $$52 + $$8);
            Vec3i $$10 = $$9.relative($$5);
            BlockState $$11 = $$0.getBlockState($$9);
            if ($$11 != ($$13 = $$11.updateShape($$5, $$12 = $$0.getBlockState((BlockPos)$$10), $$0, $$9, (BlockPos)$$10))) {
                $$0.setBlock($$9, $$13, $$1 & 0xFFFFFFFE);
            }
            if ($$12 != ($$14 = $$12.updateShape($$5.getOpposite(), $$13, $$0, (BlockPos)$$10, $$9))) {
                $$0.setBlock((BlockPos)$$10, $$14, $$1 & 0xFFFFFFFE);
            }
        });
    }

    public static List<StructureBlockInfo> processBlockInfos(LevelAccessor $$0, BlockPos $$1, BlockPos $$2, StructurePlaceSettings $$3, List<StructureBlockInfo> $$4) {
        ArrayList $$5 = Lists.newArrayList();
        for (StructureBlockInfo $$6 : $$4) {
            Vec3i $$7 = StructureTemplate.calculateRelativePosition($$3, $$6.pos).offset($$1);
            StructureBlockInfo $$8 = new StructureBlockInfo((BlockPos)$$7, $$6.state, $$6.nbt != null ? $$6.nbt.copy() : null);
            Iterator $$9 = $$3.getProcessors().iterator();
            while ($$8 != null && $$9.hasNext()) {
                $$8 = ((StructureProcessor)$$9.next()).processBlock($$0, $$1, $$2, $$6, $$8, $$3);
            }
            if ($$8 == null) continue;
            $$5.add((Object)$$8);
        }
        return $$5;
    }

    private void placeEntities(ServerLevelAccessor $$0, BlockPos $$1, Mirror $$2, Rotation $$3, BlockPos $$4, @Nullable BoundingBox $$5, boolean $$62) {
        for (StructureEntityInfo $$7 : this.entityInfoList) {
            Vec3i $$8 = StructureTemplate.transform($$7.blockPos, $$2, $$3, $$4).offset($$1);
            if ($$5 != null && !$$5.isInside($$8)) continue;
            CompoundTag $$9 = $$7.nbt.copy();
            Vec3 $$10 = StructureTemplate.transform($$7.pos, $$2, $$3, $$4);
            Vec3 $$11 = $$10.add($$1.getX(), $$1.getY(), $$1.getZ());
            ListTag $$12 = new ListTag();
            $$12.add(DoubleTag.valueOf($$11.x));
            $$12.add(DoubleTag.valueOf($$11.y));
            $$12.add(DoubleTag.valueOf($$11.z));
            $$9.put("Pos", $$12);
            $$9.remove("UUID");
            StructureTemplate.createEntityIgnoreException($$0, $$9).ifPresent($$6 -> {
                float $$7 = $$6.rotate($$3);
                $$6.moveTo($$2.x, $$2.y, $$2.z, $$7 += $$6.mirror($$2) - $$6.getYRot(), $$6.getXRot());
                if ($$62 && $$6 instanceof Mob) {
                    ((Mob)$$6).finalizeSpawn($$0, $$0.getCurrentDifficultyAt(new BlockPos($$11)), MobSpawnType.STRUCTURE, null, $$9);
                }
                $$0.addFreshEntityWithPassengers((Entity)$$6);
            });
        }
    }

    private static Optional<Entity> createEntityIgnoreException(ServerLevelAccessor $$0, CompoundTag $$1) {
        try {
            return EntityType.create($$1, $$0.getLevel());
        }
        catch (Exception $$2) {
            return Optional.empty();
        }
    }

    public Vec3i getSize(Rotation $$0) {
        switch ($$0) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                return new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
            }
        }
        return this.size;
    }

    public static BlockPos transform(BlockPos $$0, Mirror $$1, Rotation $$2, BlockPos $$3) {
        int $$4 = $$0.getX();
        int $$5 = $$0.getY();
        int $$6 = $$0.getZ();
        boolean $$7 = true;
        switch ($$1) {
            case LEFT_RIGHT: {
                $$6 = -$$6;
                break;
            }
            case FRONT_BACK: {
                $$4 = -$$4;
                break;
            }
            default: {
                $$7 = false;
            }
        }
        int $$8 = $$3.getX();
        int $$9 = $$3.getZ();
        switch ($$2) {
            case CLOCKWISE_180: {
                return new BlockPos($$8 + $$8 - $$4, $$5, $$9 + $$9 - $$6);
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos($$8 - $$9 + $$6, $$5, $$8 + $$9 - $$4);
            }
            case CLOCKWISE_90: {
                return new BlockPos($$8 + $$9 - $$6, $$5, $$9 - $$8 + $$4);
            }
        }
        return $$7 ? new BlockPos($$4, $$5, $$6) : $$0;
    }

    public static Vec3 transform(Vec3 $$0, Mirror $$1, Rotation $$2, BlockPos $$3) {
        double $$4 = $$0.x;
        double $$5 = $$0.y;
        double $$6 = $$0.z;
        boolean $$7 = true;
        switch ($$1) {
            case LEFT_RIGHT: {
                $$6 = 1.0 - $$6;
                break;
            }
            case FRONT_BACK: {
                $$4 = 1.0 - $$4;
                break;
            }
            default: {
                $$7 = false;
            }
        }
        int $$8 = $$3.getX();
        int $$9 = $$3.getZ();
        switch ($$2) {
            case CLOCKWISE_180: {
                return new Vec3((double)($$8 + $$8 + 1) - $$4, $$5, (double)($$9 + $$9 + 1) - $$6);
            }
            case COUNTERCLOCKWISE_90: {
                return new Vec3((double)($$8 - $$9) + $$6, $$5, (double)($$8 + $$9 + 1) - $$4);
            }
            case CLOCKWISE_90: {
                return new Vec3((double)($$8 + $$9 + 1) - $$6, $$5, (double)($$9 - $$8) + $$4);
            }
        }
        return $$7 ? new Vec3($$4, $$5, $$6) : $$0;
    }

    public BlockPos getZeroPositionWithTransform(BlockPos $$0, Mirror $$1, Rotation $$2) {
        return StructureTemplate.getZeroPositionWithTransform($$0, $$1, $$2, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos getZeroPositionWithTransform(BlockPos $$0, Mirror $$1, Rotation $$2, int $$3, int $$4) {
        int $$5 = $$1 == Mirror.FRONT_BACK ? --$$3 : 0;
        int $$6 = $$1 == Mirror.LEFT_RIGHT ? --$$4 : 0;
        BlockPos $$7 = $$0;
        switch ($$2) {
            case NONE: {
                $$7 = $$0.offset($$5, 0, $$6);
                break;
            }
            case CLOCKWISE_90: {
                $$7 = $$0.offset($$4 - $$6, 0, $$5);
                break;
            }
            case CLOCKWISE_180: {
                $$7 = $$0.offset($$3 - $$5, 0, $$4 - $$6);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                $$7 = $$0.offset($$6, 0, $$3 - $$5);
            }
        }
        return $$7;
    }

    public BoundingBox getBoundingBox(StructurePlaceSettings $$0, BlockPos $$1) {
        return this.getBoundingBox($$1, $$0.getRotation(), $$0.getRotationPivot(), $$0.getMirror());
    }

    public BoundingBox getBoundingBox(BlockPos $$0, Rotation $$1, BlockPos $$2, Mirror $$3) {
        return StructureTemplate.getBoundingBox($$0, $$1, $$2, $$3, this.size);
    }

    @VisibleForTesting
    protected static BoundingBox getBoundingBox(BlockPos $$0, Rotation $$1, BlockPos $$2, Mirror $$3, Vec3i $$4) {
        Vec3i $$5 = $$4.offset(-1, -1, -1);
        BlockPos $$6 = StructureTemplate.transform(BlockPos.ZERO, $$3, $$1, $$2);
        BlockPos $$7 = StructureTemplate.transform((BlockPos)BlockPos.ZERO.offset($$5), $$3, $$1, $$2);
        return BoundingBox.fromCorners($$6, $$7).move($$0);
    }

    public CompoundTag save(CompoundTag $$0) {
        if (this.palettes.isEmpty()) {
            $$0.put(BLOCKS_TAG, new ListTag());
            $$0.put(PALETTE_TAG, new ListTag());
        } else {
            ArrayList $$1 = Lists.newArrayList();
            SimplePalette $$2 = new SimplePalette();
            $$1.add((Object)$$2);
            for (int $$3 = 1; $$3 < this.palettes.size(); ++$$3) {
                $$1.add((Object)new SimplePalette());
            }
            ListTag $$4 = new ListTag();
            List<StructureBlockInfo> $$5 = ((Palette)this.palettes.get(0)).blocks();
            for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
                StructureBlockInfo $$7 = (StructureBlockInfo)$$5.get($$6);
                CompoundTag $$8 = new CompoundTag();
                $$8.put("pos", this.newIntegerList($$7.pos.getX(), $$7.pos.getY(), $$7.pos.getZ()));
                int $$9 = $$2.idFor($$7.state);
                $$8.putInt(BLOCK_TAG_STATE, $$9);
                if ($$7.nbt != null) {
                    $$8.put("nbt", $$7.nbt);
                }
                $$4.add($$8);
                for (int $$10 = 1; $$10 < this.palettes.size(); ++$$10) {
                    SimplePalette $$11 = (SimplePalette)$$1.get($$10);
                    $$11.addMapping(((StructureBlockInfo)((Palette)this.palettes.get((int)$$10)).blocks().get((int)$$6)).state, $$9);
                }
            }
            $$0.put(BLOCKS_TAG, $$4);
            if ($$1.size() == 1) {
                ListTag $$12 = new ListTag();
                for (BlockState $$13 : $$2) {
                    $$12.add(NbtUtils.writeBlockState($$13));
                }
                $$0.put(PALETTE_TAG, $$12);
            } else {
                ListTag $$14 = new ListTag();
                for (SimplePalette $$15 : $$1) {
                    ListTag $$16 = new ListTag();
                    for (BlockState $$17 : $$15) {
                        $$16.add(NbtUtils.writeBlockState($$17));
                    }
                    $$14.add($$16);
                }
                $$0.put(PALETTE_LIST_TAG, $$14);
            }
        }
        ListTag $$18 = new ListTag();
        for (StructureEntityInfo $$19 : this.entityInfoList) {
            CompoundTag $$20 = new CompoundTag();
            $$20.put("pos", this.newDoubleList($$19.pos.x, $$19.pos.y, $$19.pos.z));
            $$20.put(ENTITY_TAG_BLOCKPOS, this.newIntegerList($$19.blockPos.getX(), $$19.blockPos.getY(), $$19.blockPos.getZ()));
            if ($$19.nbt != null) {
                $$20.put("nbt", $$19.nbt);
            }
            $$18.add($$20);
        }
        $$0.put(ENTITIES_TAG, $$18);
        $$0.put(SIZE_TAG, this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
        return NbtUtils.addCurrentDataVersion($$0);
    }

    public void load(HolderGetter<Block> $$0, CompoundTag $$1) {
        this.palettes.clear();
        this.entityInfoList.clear();
        ListTag $$2 = $$1.getList(SIZE_TAG, 3);
        this.size = new Vec3i($$2.getInt(0), $$2.getInt(1), $$2.getInt(2));
        ListTag $$3 = $$1.getList(BLOCKS_TAG, 10);
        if ($$1.contains(PALETTE_LIST_TAG, 9)) {
            ListTag $$4 = $$1.getList(PALETTE_LIST_TAG, 9);
            for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                this.loadPalette($$0, $$4.getList($$5), $$3);
            }
        } else {
            this.loadPalette($$0, $$1.getList(PALETTE_TAG, 10), $$3);
        }
        ListTag $$6 = $$1.getList(ENTITIES_TAG, 10);
        for (int $$7 = 0; $$7 < $$6.size(); ++$$7) {
            CompoundTag $$8 = $$6.getCompound($$7);
            ListTag $$9 = $$8.getList("pos", 6);
            Vec3 $$10 = new Vec3($$9.getDouble(0), $$9.getDouble(1), $$9.getDouble(2));
            ListTag $$11 = $$8.getList(ENTITY_TAG_BLOCKPOS, 3);
            BlockPos $$12 = new BlockPos($$11.getInt(0), $$11.getInt(1), $$11.getInt(2));
            if (!$$8.contains("nbt")) continue;
            CompoundTag $$13 = $$8.getCompound("nbt");
            this.entityInfoList.add((Object)new StructureEntityInfo($$10, $$12, $$13));
        }
    }

    private void loadPalette(HolderGetter<Block> $$0, ListTag $$1, ListTag $$2) {
        SimplePalette $$3 = new SimplePalette();
        for (int $$4 = 0; $$4 < $$1.size(); ++$$4) {
            $$3.addMapping(NbtUtils.readBlockState($$0, $$1.getCompound($$4)), $$4);
        }
        ArrayList $$5 = Lists.newArrayList();
        ArrayList $$6 = Lists.newArrayList();
        ArrayList $$7 = Lists.newArrayList();
        for (int $$8 = 0; $$8 < $$2.size(); ++$$8) {
            CompoundTag $$14;
            CompoundTag $$9 = $$2.getCompound($$8);
            ListTag $$10 = $$9.getList("pos", 3);
            BlockPos $$11 = new BlockPos($$10.getInt(0), $$10.getInt(1), $$10.getInt(2));
            BlockState $$12 = $$3.stateFor($$9.getInt(BLOCK_TAG_STATE));
            if ($$9.contains("nbt")) {
                CompoundTag $$13 = $$9.getCompound("nbt");
            } else {
                $$14 = null;
            }
            StructureBlockInfo $$15 = new StructureBlockInfo($$11, $$12, $$14);
            StructureTemplate.addToLists($$15, (List<StructureBlockInfo>)$$5, (List<StructureBlockInfo>)$$6, (List<StructureBlockInfo>)$$7);
        }
        List<StructureBlockInfo> $$16 = StructureTemplate.buildInfoList((List<StructureBlockInfo>)$$5, (List<StructureBlockInfo>)$$6, (List<StructureBlockInfo>)$$7);
        this.palettes.add((Object)new Palette($$16));
    }

    private ListTag newIntegerList(int ... $$0) {
        ListTag $$1 = new ListTag();
        for (int $$2 : $$0) {
            $$1.add(IntTag.valueOf($$2));
        }
        return $$1;
    }

    private ListTag newDoubleList(double ... $$0) {
        ListTag $$1 = new ListTag();
        for (double $$2 : $$0) {
            $$1.add(DoubleTag.valueOf($$2));
        }
        return $$1;
    }

    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag nbt;

        public StructureBlockInfo(BlockPos $$0, BlockState $$1, @Nullable CompoundTag $$2) {
            this.pos = $$0;
            this.state = $$1;
            this.nbt = $$2;
        }

        public String toString() {
            return String.format((Locale)Locale.ROOT, (String)"<StructureBlockInfo | %s | %s | %s>", (Object[])new Object[]{this.pos, this.state, this.nbt});
        }
    }

    public static final class Palette {
        private final List<StructureBlockInfo> blocks;
        private final Map<Block, List<StructureBlockInfo>> cache = Maps.newHashMap();

        Palette(List<StructureBlockInfo> $$0) {
            this.blocks = $$0;
        }

        public List<StructureBlockInfo> blocks() {
            return this.blocks;
        }

        public List<StructureBlockInfo> blocks(Block $$02) {
            return (List)this.cache.computeIfAbsent((Object)$$02, $$0 -> (List)this.blocks.stream().filter($$1 -> $$1.state.is((Block)$$0)).collect(Collectors.toList()));
        }
    }

    public static class StructureEntityInfo {
        public final Vec3 pos;
        public final BlockPos blockPos;
        public final CompoundTag nbt;

        public StructureEntityInfo(Vec3 $$0, BlockPos $$1, CompoundTag $$2) {
            this.pos = $$0;
            this.blockPos = $$1;
            this.nbt = $$2;
        }
    }

    static class SimplePalette
    implements Iterable<BlockState> {
        public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        private final IdMapper<BlockState> ids = new IdMapper(16);
        private int lastId;

        SimplePalette() {
        }

        public int idFor(BlockState $$0) {
            int $$1 = this.ids.getId($$0);
            if ($$1 == -1) {
                $$1 = this.lastId++;
                this.ids.addMapping($$0, $$1);
            }
            return $$1;
        }

        @Nullable
        public BlockState stateFor(int $$0) {
            BlockState $$1 = this.ids.byId($$0);
            return $$1 == null ? DEFAULT_BLOCK_STATE : $$1;
        }

        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(BlockState $$0, int $$1) {
            this.ids.addMapping($$0, $$1);
        }
    }
}