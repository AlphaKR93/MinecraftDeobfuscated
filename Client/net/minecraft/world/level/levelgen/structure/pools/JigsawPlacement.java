/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Deque
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class JigsawPlacement {
    static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext $$02, Holder<StructureTemplatePool> $$1, Optional<ResourceLocation> $$2, int $$3, BlockPos $$4, boolean $$5, Optional<Heightmap.Types> $$6, int $$7) {
        int $$28;
        BlockPos $$20;
        RegistryAccess $$8 = $$02.registryAccess();
        ChunkGenerator $$9 = $$02.chunkGenerator();
        StructureTemplateManager $$10 = $$02.structureTemplateManager();
        LevelHeightAccessor $$11 = $$02.heightAccessor();
        WorldgenRandom $$12 = $$02.random();
        Registry<StructureTemplatePool> $$13 = $$8.registryOrThrow(Registries.TEMPLATE_POOL);
        Rotation $$142 = Rotation.getRandom($$12);
        StructureTemplatePool $$15 = $$1.value();
        StructurePoolElement $$16 = $$15.getRandomTemplate($$12);
        if ($$16 == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }
        if ($$2.isPresent()) {
            ResourceLocation $$17 = (ResourceLocation)$$2.get();
            Optional<BlockPos> $$18 = JigsawPlacement.getRandomNamedJigsaw($$16, $$17, $$4, $$142, $$10, $$12);
            if ($$18.isEmpty()) {
                LOGGER.error("No starting jigsaw {} found in start pool {}", (Object)$$17, $$1.unwrapKey().map($$0 -> $$0.location().toString()).orElse((Object)"<unregistered>"));
                return Optional.empty();
            }
            BlockPos $$19 = (BlockPos)$$18.get();
        } else {
            $$20 = $$4;
        }
        Vec3i $$21 = $$20.subtract($$4);
        Vec3i $$22 = $$4.subtract($$21);
        PoolElementStructurePiece $$23 = new PoolElementStructurePiece($$10, $$16, (BlockPos)$$22, $$16.getGroundLevelDelta(), $$142, $$16.getBoundingBox($$10, (BlockPos)$$22, $$142));
        BoundingBox $$24 = $$23.getBoundingBox();
        int $$25 = ($$24.maxX() + $$24.minX()) / 2;
        int $$26 = ($$24.maxZ() + $$24.minZ()) / 2;
        if ($$6.isPresent()) {
            int $$27 = $$4.getY() + $$9.getFirstFreeHeight($$25, $$26, (Heightmap.Types)$$6.get(), $$11, $$02.randomState());
        } else {
            $$28 = $$22.getY();
        }
        int $$29 = $$24.minY() + $$23.getGroundLevelDelta();
        $$23.move(0, $$28 - $$29, 0);
        int $$30 = $$28 + $$21.getY();
        return Optional.of((Object)((Object)new Structure.GenerationStub(new BlockPos($$25, $$30, $$26), (Consumer<StructurePiecesBuilder>)((Consumer)$$14 -> {
            ArrayList $$15 = Lists.newArrayList();
            $$15.add((Object)$$23);
            if ($$3 <= 0) {
                return;
            }
            AABB $$16 = new AABB($$25 - $$7, $$30 - $$7, $$26 - $$7, $$25 + $$7 + 1, $$30 + $$7 + 1, $$26 + $$7 + 1);
            VoxelShape $$17 = Shapes.join(Shapes.create($$16), Shapes.create(AABB.of($$24)), BooleanOp.ONLY_FIRST);
            JigsawPlacement.addPieces($$02.randomState(), $$3, $$5, $$9, $$10, $$11, $$12, $$13, $$23, (List<PoolElementStructurePiece>)$$15, $$17);
            $$15.forEach($$14::addPiece);
        }))));
    }

    private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement $$0, ResourceLocation $$1, BlockPos $$2, Rotation $$3, StructureTemplateManager $$4, WorldgenRandom $$5) {
        List<StructureTemplate.StructureBlockInfo> $$6 = $$0.getShuffledJigsawBlocks($$4, $$2, $$3, $$5);
        Optional $$7 = Optional.empty();
        for (StructureTemplate.StructureBlockInfo $$8 : $$6) {
            ResourceLocation $$9 = ResourceLocation.tryParse($$8.nbt.getString("name"));
            if (!$$1.equals($$9)) continue;
            $$7 = Optional.of((Object)$$8.pos);
            break;
        }
        return $$7;
    }

    private static void addPieces(RandomState $$0, int $$1, boolean $$2, ChunkGenerator $$3, StructureTemplateManager $$4, LevelHeightAccessor $$5, RandomSource $$6, Registry<StructureTemplatePool> $$7, PoolElementStructurePiece $$8, List<PoolElementStructurePiece> $$9, VoxelShape $$10) {
        Placer $$11 = new Placer($$7, $$1, $$3, $$4, $$9, $$6);
        $$11.placing.addLast((Object)new PieceState($$8, (MutableObject<VoxelShape>)new MutableObject((Object)$$10), 0));
        while (!$$11.placing.isEmpty()) {
            PieceState $$12 = (PieceState)$$11.placing.removeFirst();
            $$11.tryPlacingChildren($$12.piece, $$12.free, $$12.depth, $$2, $$5, $$0);
        }
    }

    public static boolean generateJigsaw(ServerLevel $$02, Holder<StructureTemplatePool> $$1, ResourceLocation $$2, int $$3, BlockPos $$4, boolean $$5) {
        ChunkGenerator $$6 = $$02.getChunkSource().getGenerator();
        StructureTemplateManager $$7 = $$02.getStructureManager();
        StructureManager $$8 = $$02.structureManager();
        RandomSource $$9 = $$02.getRandom();
        Structure.GenerationContext $$10 = new Structure.GenerationContext($$02.registryAccess(), $$6, $$6.getBiomeSource(), $$02.getChunkSource().randomState(), $$7, $$02.getSeed(), new ChunkPos($$4), $$02, (Predicate<Holder<Biome>>)((Predicate)$$0 -> true));
        Optional<Structure.GenerationStub> $$11 = JigsawPlacement.addPieces($$10, $$1, (Optional<ResourceLocation>)Optional.of((Object)$$2), $$3, $$4, false, (Optional<Heightmap.Types>)Optional.empty(), 128);
        if ($$11.isPresent()) {
            StructurePiecesBuilder $$12 = ((Structure.GenerationStub)((Object)$$11.get())).getPiecesBuilder();
            for (StructurePiece $$13 : $$12.build().pieces()) {
                if (!($$13 instanceof PoolElementStructurePiece)) continue;
                PoolElementStructurePiece $$14 = (PoolElementStructurePiece)$$13;
                $$14.place($$02, $$8, $$6, $$9, BoundingBox.infinite(), $$4, $$5);
            }
            return true;
        }
        return false;
    }

    static final class Placer {
        private final Registry<StructureTemplatePool> pools;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final RandomSource random;
        final Deque<PieceState> placing = Queues.newArrayDeque();

        Placer(Registry<StructureTemplatePool> $$0, int $$1, ChunkGenerator $$2, StructureTemplateManager $$3, List<? super PoolElementStructurePiece> $$4, RandomSource $$5) {
            this.pools = $$0;
            this.maxDepth = $$1;
            this.chunkGenerator = $$2;
            this.structureTemplateManager = $$3;
            this.pieces = $$4;
            this.random = $$5;
        }

        void tryPlacingChildren(PoolElementStructurePiece $$02, MutableObject<VoxelShape> $$12, int $$2, boolean $$3, LevelHeightAccessor $$4, RandomState $$5) {
            StructurePoolElement $$6 = $$02.getElement();
            BlockPos $$7 = $$02.getPosition();
            Rotation $$8 = $$02.getRotation();
            StructureTemplatePool.Projection $$9 = $$6.getProjection();
            boolean $$10 = $$9 == StructureTemplatePool.Projection.RIGID;
            MutableObject $$11 = new MutableObject();
            BoundingBox $$122 = $$02.getBoundingBox();
            int $$13 = $$122.minY();
            block0: for (StructureTemplate.StructureBlockInfo $$14 : $$6.getShuffledJigsawBlocks(this.structureTemplateManager, $$7, $$8, this.random)) {
                StructurePoolElement $$28;
                MutableObject<VoxelShape> $$26;
                Direction $$15 = JigsawBlock.getFrontFacing($$14.state);
                BlockPos $$16 = $$14.pos;
                Vec3i $$17 = $$16.relative($$15);
                int $$18 = $$16.getY() - $$13;
                int $$19 = -1;
                ResourceKey<StructureTemplatePool> $$20 = Placer.readPoolName($$14);
                Optional<Holder.Reference<StructureTemplatePool>> $$21 = this.pools.getHolder($$20);
                if ($$21.isEmpty()) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)$$20.location());
                    continue;
                }
                Holder $$22 = (Holder)$$21.get();
                if (((StructureTemplatePool)$$22.value()).size() == 0 && !$$22.is(Pools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)$$20.location());
                    continue;
                }
                Holder<StructureTemplatePool> $$23 = ((StructureTemplatePool)$$22.value()).getFallback();
                if ($$23.value().size() == 0 && !$$23.is(Pools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent fallback pool: {}", $$23.unwrapKey().map($$0 -> $$0.location().toString()).orElse((Object)"<unregistered>"));
                    continue;
                }
                boolean $$24 = $$122.isInside($$17);
                if ($$24) {
                    MutableObject $$25 = $$11;
                    if ($$11.getValue() == null) {
                        $$11.setValue((Object)Shapes.create(AABB.of($$122)));
                    }
                } else {
                    $$26 = $$12;
                }
                ArrayList $$27 = Lists.newArrayList();
                if ($$2 != this.maxDepth) {
                    $$27.addAll(((StructureTemplatePool)$$22.value()).getShuffledTemplates(this.random));
                }
                $$27.addAll($$23.value().getShuffledTemplates(this.random));
                Iterator iterator = $$27.iterator();
                while (iterator.hasNext() && ($$28 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (Rotation $$29 : Rotation.getShuffled(this.random)) {
                        int $$33;
                        List<StructureTemplate.StructureBlockInfo> $$30 = $$28.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, $$29, this.random);
                        BoundingBox $$31 = $$28.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, $$29);
                        if (!$$3 || $$31.getYSpan() > 16) {
                            boolean $$32 = false;
                        } else {
                            $$33 = $$30.stream().mapToInt($$1 -> {
                                if (!$$31.isInside($$1.pos.relative(JigsawBlock.getFrontFacing($$1.state)))) {
                                    return 0;
                                }
                                ResourceKey<StructureTemplatePool> $$2 = Placer.readPoolName($$1);
                                Optional<Holder.Reference<StructureTemplatePool>> $$3 = this.pools.getHolder($$2);
                                Optional $$4 = $$3.map($$0 -> ((StructureTemplatePool)$$0.value()).getFallback());
                                int $$5 = (Integer)$$3.map($$0 -> ((StructureTemplatePool)$$0.value()).getMaxSize(this.structureTemplateManager)).orElse((Object)0);
                                int $$6 = (Integer)$$4.map($$0 -> ((StructureTemplatePool)$$0.value()).getMaxSize(this.structureTemplateManager)).orElse((Object)0);
                                return Math.max((int)$$5, (int)$$6);
                            }).max().orElse(0);
                        }
                        for (StructureTemplate.StructureBlockInfo $$34 : $$30) {
                            int $$55;
                            int $$51;
                            int $$44;
                            if (!JigsawBlock.canAttach($$14, $$34)) continue;
                            BlockPos $$35 = $$34.pos;
                            Vec3i $$36 = ((BlockPos)$$17).subtract($$35);
                            BoundingBox $$37 = $$28.getBoundingBox(this.structureTemplateManager, (BlockPos)$$36, $$29);
                            int $$38 = $$37.minY();
                            StructureTemplatePool.Projection $$39 = $$28.getProjection();
                            boolean $$40 = $$39 == StructureTemplatePool.Projection.RIGID;
                            int $$41 = $$35.getY();
                            int $$42 = $$18 - $$41 + JigsawBlock.getFrontFacing($$14.state).getStepY();
                            if ($$10 && $$40) {
                                int $$43 = $$13 + $$42;
                            } else {
                                if ($$19 == -1) {
                                    $$19 = this.chunkGenerator.getFirstFreeHeight($$16.getX(), $$16.getZ(), Heightmap.Types.WORLD_SURFACE_WG, $$4, $$5);
                                }
                                $$44 = $$19 - $$41;
                            }
                            int $$45 = $$44 - $$38;
                            BoundingBox $$46 = $$37.moved(0, $$45, 0);
                            BlockPos $$47 = ((BlockPos)$$36).offset(0, $$45, 0);
                            if ($$33 > 0) {
                                int $$48 = Math.max((int)($$33 + 1), (int)($$46.maxY() - $$46.minY()));
                                $$46.encapsulate(new BlockPos($$46.minX(), $$46.minY() + $$48, $$46.minZ()));
                            }
                            if (Shapes.joinIsNotEmpty((VoxelShape)$$26.getValue(), Shapes.create(AABB.of($$46).deflate(0.25)), BooleanOp.ONLY_SECOND)) continue;
                            $$26.setValue((Object)Shapes.joinUnoptimized((VoxelShape)$$26.getValue(), Shapes.create(AABB.of($$46)), BooleanOp.ONLY_FIRST));
                            int $$49 = $$02.getGroundLevelDelta();
                            if ($$40) {
                                int $$50 = $$49 - $$42;
                            } else {
                                $$51 = $$28.getGroundLevelDelta();
                            }
                            PoolElementStructurePiece $$52 = new PoolElementStructurePiece(this.structureTemplateManager, $$28, $$47, $$51, $$29, $$46);
                            if ($$10) {
                                int $$53 = $$13 + $$18;
                            } else if ($$40) {
                                int $$54 = $$44 + $$41;
                            } else {
                                if ($$19 == -1) {
                                    $$19 = this.chunkGenerator.getFirstFreeHeight($$16.getX(), $$16.getZ(), Heightmap.Types.WORLD_SURFACE_WG, $$4, $$5);
                                }
                                $$55 = $$19 + $$42 / 2;
                            }
                            $$02.addJunction(new JigsawJunction($$17.getX(), (int)($$55 - $$18 + $$49), $$17.getZ(), $$42, $$39));
                            $$52.addJunction(new JigsawJunction($$16.getX(), $$55 - $$41 + $$51, $$16.getZ(), -$$42, $$9));
                            this.pieces.add((Object)$$52);
                            if ($$2 + 1 > this.maxDepth) continue block0;
                            this.placing.addLast((Object)new PieceState($$52, $$26, $$2 + 1));
                            continue block0;
                        }
                    }
                }
            }
        }

        private static ResourceKey<StructureTemplatePool> readPoolName(StructureTemplate.StructureBlockInfo $$0) {
            return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation($$0.nbt.getString("pool")));
        }
    }

    static final class PieceState {
        final PoolElementStructurePiece piece;
        final MutableObject<VoxelShape> free;
        final int depth;

        PieceState(PoolElementStructurePiece $$0, MutableObject<VoxelShape> $$1, int $$2) {
            this.piece = $$0;
            this.free = $$1;
            this.depth = $$2;
        }
    }
}