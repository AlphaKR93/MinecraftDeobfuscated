/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Deprecated
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Short
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Spliterator
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public class LevelChunk
extends ChunkAccess {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final TickingBlockEntity NULL_TICKER = new TickingBlockEntity(){

        @Override
        public void tick() {
        }

        @Override
        public boolean isRemoved() {
            return true;
        }

        @Override
        public BlockPos getPos() {
            return BlockPos.ZERO;
        }

        @Override
        public String getType() {
            return "<null>";
        }
    };
    private final Map<BlockPos, RebindableTickingBlockEntityWrapper> tickersInLevel = Maps.newHashMap();
    private boolean loaded;
    private boolean clientLightReady = false;
    final Level level;
    @Nullable
    private Supplier<ChunkHolder.FullChunkStatus> fullStatus;
    @Nullable
    private PostLoadProcessor postLoad;
    private final Int2ObjectMap<GameEventListenerRegistry> gameEventListenerRegistrySections;
    private final LevelChunkTicks<Block> blockTicks;
    private final LevelChunkTicks<Fluid> fluidTicks;

    public LevelChunk(Level $$0, ChunkPos $$1) {
        this($$0, $$1, UpgradeData.EMPTY, new LevelChunkTicks<Block>(), new LevelChunkTicks<Fluid>(), 0L, null, null, null);
    }

    public LevelChunk(Level $$0, ChunkPos $$1, UpgradeData $$2, LevelChunkTicks<Block> $$3, LevelChunkTicks<Fluid> $$4, long $$5, @Nullable LevelChunkSection[] $$6, @Nullable PostLoadProcessor $$7, @Nullable BlendingData $$8) {
        super($$1, $$2, $$0, $$0.registryAccess().registryOrThrow(Registries.BIOME), $$5, $$6, $$8);
        this.level = $$0;
        this.gameEventListenerRegistrySections = new Int2ObjectOpenHashMap();
        for (Heightmap.Types $$9 : Heightmap.Types.values()) {
            if (!ChunkStatus.FULL.heightmapsAfter().contains((Object)$$9)) continue;
            this.heightmaps.put((Object)$$9, (Object)new Heightmap(this, $$9));
        }
        this.postLoad = $$7;
        this.blockTicks = $$3;
        this.fluidTicks = $$4;
    }

    public LevelChunk(ServerLevel $$0, ProtoChunk $$1, @Nullable PostLoadProcessor $$2) {
        this($$0, $$1.getPos(), $$1.getUpgradeData(), $$1.unpackBlockTicks(), $$1.unpackFluidTicks(), $$1.getInhabitedTime(), $$1.getSections(), $$2, $$1.getBlendingData());
        for (BlockEntity $$3 : $$1.getBlockEntities().values()) {
            this.setBlockEntity($$3);
        }
        this.pendingBlockEntities.putAll($$1.getBlockEntityNbts());
        for (int $$4 = 0; $$4 < $$1.getPostProcessing().length; ++$$4) {
            this.postProcessing[$$4] = $$1.getPostProcessing()[$$4];
        }
        this.setAllStarts($$1.getAllStarts());
        this.setAllReferences($$1.getAllReferences());
        for (Map.Entry $$5 : $$1.getHeightmaps()) {
            if (!ChunkStatus.FULL.heightmapsAfter().contains($$5.getKey())) continue;
            this.setHeightmap((Heightmap.Types)$$5.getKey(), ((Heightmap)$$5.getValue()).getRawData());
        }
        this.setLightCorrect($$1.isLightCorrect());
        this.unsaved = true;
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public TickContainerAccess<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    @Override
    public ChunkAccess.TicksToSave getTicksForSerialization() {
        return new ChunkAccess.TicksToSave(this.blockTicks, this.fluidTicks);
    }

    @Override
    public GameEventListenerRegistry getListenerRegistry(int $$0) {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$12 = (ServerLevel)level;
            return (GameEventListenerRegistry)this.gameEventListenerRegistrySections.computeIfAbsent($$0, $$1 -> new EuclideanGameEventListenerRegistry($$12));
        }
        return super.getListenerRegistry($$0);
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        int $$1 = $$0.getX();
        int $$2 = $$0.getY();
        int $$3 = $$0.getZ();
        if (this.level.isDebug()) {
            BlockState $$4 = null;
            if ($$2 == 60) {
                $$4 = Blocks.BARRIER.defaultBlockState();
            }
            if ($$2 == 70) {
                $$4 = DebugLevelSource.getBlockStateFor($$1, $$3);
            }
            return $$4 == null ? Blocks.AIR.defaultBlockState() : $$4;
        }
        try {
            LevelChunkSection $$6;
            int $$5 = this.getSectionIndex($$2);
            if ($$5 >= 0 && $$5 < this.sections.length && !($$6 = this.sections[$$5]).hasOnlyAir()) {
                return $$6.getBlockState($$1 & 0xF, $$2 & 0xF, $$3 & 0xF);
            }
            return Blocks.AIR.defaultBlockState();
        }
        catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Getting block state");
            CrashReportCategory $$9 = $$8.addCategory("Block being got");
            $$9.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, $$1, $$2, $$3));
            throw new ReportedException($$8);
        }
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        return this.getFluidState($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public FluidState getFluidState(int $$0, int $$1, int $$2) {
        try {
            LevelChunkSection $$4;
            int $$3 = this.getSectionIndex($$1);
            if ($$3 >= 0 && $$3 < this.sections.length && !($$4 = this.sections[$$3]).hasOnlyAir()) {
                return $$4.getFluidState($$0 & 0xF, $$1 & 0xF, $$2 & 0xF);
            }
            return Fluids.EMPTY.defaultFluidState();
        }
        catch (Throwable $$5) {
            CrashReport $$6 = CrashReport.forThrowable($$5, "Getting fluid state");
            CrashReportCategory $$7 = $$6.addCategory("Block being got");
            $$7.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, $$0, $$1, $$2));
            throw new ReportedException($$6);
        }
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos $$0, BlockState $$1, boolean $$2) {
        int $$8;
        int $$7;
        int $$3 = $$0.getY();
        LevelChunkSection $$4 = this.getSection(this.getSectionIndex($$3));
        boolean $$5 = $$4.hasOnlyAir();
        if ($$5 && $$1.isAir()) {
            return null;
        }
        int $$6 = $$0.getX() & 0xF;
        BlockState $$9 = $$4.setBlockState($$6, $$7 = $$3 & 0xF, $$8 = $$0.getZ() & 0xF, $$1);
        if ($$9 == $$1) {
            return null;
        }
        Block $$10 = $$1.getBlock();
        ((Heightmap)this.heightmaps.get((Object)Heightmap.Types.MOTION_BLOCKING)).update($$6, $$3, $$8, $$1);
        ((Heightmap)this.heightmaps.get((Object)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update($$6, $$3, $$8, $$1);
        ((Heightmap)this.heightmaps.get((Object)Heightmap.Types.OCEAN_FLOOR)).update($$6, $$3, $$8, $$1);
        ((Heightmap)this.heightmaps.get((Object)Heightmap.Types.WORLD_SURFACE)).update($$6, $$3, $$8, $$1);
        boolean $$11 = $$4.hasOnlyAir();
        if ($$5 != $$11) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus($$0, $$11);
        }
        boolean $$12 = $$9.hasBlockEntity();
        if (!this.level.isClientSide) {
            $$9.onRemove(this.level, $$0, $$1, $$2);
        } else if (!$$9.is($$10) && $$12) {
            this.removeBlockEntity($$0);
        }
        if (!$$4.getBlockState($$6, $$7, $$8).is($$10)) {
            return null;
        }
        if (!this.level.isClientSide) {
            $$1.onPlace(this.level, $$0, $$9, $$2);
        }
        if ($$1.hasBlockEntity()) {
            BlockEntity $$13 = this.getBlockEntity($$0, EntityCreationType.CHECK);
            if ($$13 == null) {
                $$13 = ((EntityBlock)((Object)$$10)).newBlockEntity($$0, $$1);
                if ($$13 != null) {
                    this.addAndRegisterBlockEntity($$13);
                }
            } else {
                $$13.setBlockState($$1);
                this.updateBlockEntityTicker($$13);
            }
        }
        this.unsaved = true;
        return $$9;
    }

    @Override
    @Deprecated
    public void addEntity(Entity $$0) {
    }

    @Nullable
    private BlockEntity createBlockEntity(BlockPos $$0) {
        BlockState $$1 = this.getBlockState($$0);
        if (!$$1.hasBlockEntity()) {
            return null;
        }
        return ((EntityBlock)((Object)$$1.getBlock())).newBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return this.getBlockEntity($$0, EntityCreationType.CHECK);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0, EntityCreationType $$1) {
        BlockEntity $$4;
        CompoundTag $$3;
        BlockEntity $$2 = (BlockEntity)this.blockEntities.get((Object)$$0);
        if ($$2 == null && ($$3 = (CompoundTag)this.pendingBlockEntities.remove((Object)$$0)) != null && ($$4 = this.promotePendingBlockEntity($$0, $$3)) != null) {
            return $$4;
        }
        if ($$2 == null) {
            if ($$1 == EntityCreationType.IMMEDIATE && ($$2 = this.createBlockEntity($$0)) != null) {
                this.addAndRegisterBlockEntity($$2);
            }
        } else if ($$2.isRemoved()) {
            this.blockEntities.remove((Object)$$0);
            return null;
        }
        return $$2;
    }

    public void addAndRegisterBlockEntity(BlockEntity $$0) {
        this.setBlockEntity($$0);
        if (this.isInLevel()) {
            Level level = this.level;
            if (level instanceof ServerLevel) {
                ServerLevel $$1 = (ServerLevel)level;
                this.addGameEventListener($$0, $$1);
            }
            this.updateBlockEntityTicker($$0);
        }
    }

    private boolean isInLevel() {
        return this.loaded || this.level.isClientSide();
    }

    boolean isTicking(BlockPos $$0) {
        if (!this.level.getWorldBorder().isWithinBounds($$0)) {
            return false;
        }
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            return this.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING) && $$1.areEntitiesLoaded(ChunkPos.asLong($$0));
        }
        return true;
    }

    @Override
    public void setBlockEntity(BlockEntity $$0) {
        BlockPos $$1 = $$0.getBlockPos();
        if (!this.getBlockState($$1).hasBlockEntity()) {
            return;
        }
        $$0.setLevel(this.level);
        $$0.clearRemoved();
        BlockEntity $$2 = (BlockEntity)this.blockEntities.put((Object)$$1.immutable(), (Object)$$0);
        if ($$2 != null && $$2 != $$0) {
            $$2.setRemoved();
        }
    }

    @Override
    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(BlockPos $$0) {
        BlockEntity $$1 = this.getBlockEntity($$0);
        if ($$1 != null && !$$1.isRemoved()) {
            CompoundTag $$2 = $$1.saveWithFullMetadata();
            $$2.putBoolean("keepPacked", false);
            return $$2;
        }
        CompoundTag $$3 = (CompoundTag)this.pendingBlockEntities.get((Object)$$0);
        if ($$3 != null) {
            $$3 = $$3.copy();
            $$3.putBoolean("keepPacked", true);
        }
        return $$3;
    }

    @Override
    public void removeBlockEntity(BlockPos $$0) {
        BlockEntity $$1;
        if (this.isInLevel() && ($$1 = (BlockEntity)this.blockEntities.remove((Object)$$0)) != null) {
            Level level = this.level;
            if (level instanceof ServerLevel) {
                ServerLevel $$2 = (ServerLevel)level;
                this.removeGameEventListener($$1, $$2);
            }
            $$1.setRemoved();
        }
        this.removeBlockEntityTicker($$0);
    }

    private <T extends BlockEntity> void removeGameEventListener(T $$0, ServerLevel $$1) {
        GameEventListener $$3;
        Block $$2 = $$0.getBlockState().getBlock();
        if ($$2 instanceof EntityBlock && ($$3 = ((EntityBlock)((Object)$$2)).getListener($$1, $$0)) != null) {
            int $$4 = SectionPos.blockToSectionCoord($$0.getBlockPos().getY());
            GameEventListenerRegistry $$5 = this.getListenerRegistry($$4);
            $$5.unregister($$3);
            if ($$5.isEmpty()) {
                this.gameEventListenerRegistrySections.remove($$4);
            }
        }
    }

    private void removeBlockEntityTicker(BlockPos $$0) {
        RebindableTickingBlockEntityWrapper $$1 = (RebindableTickingBlockEntityWrapper)this.tickersInLevel.remove((Object)$$0);
        if ($$1 != null) {
            $$1.rebind(NULL_TICKER);
        }
    }

    public void runPostLoad() {
        if (this.postLoad != null) {
            this.postLoad.run(this);
            this.postLoad = null;
        }
    }

    public boolean isEmpty() {
        return false;
    }

    public void replaceWithPacketData(FriendlyByteBuf $$02, CompoundTag $$12, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> $$22) {
        this.clearAllBlockEntities();
        for (LevelChunkSection $$3 : this.sections) {
            $$3.read($$02);
        }
        for (Heightmap.Types $$4 : Heightmap.Types.values()) {
            String $$5 = $$4.getSerializationKey();
            if (!$$12.contains($$5, 12)) continue;
            this.setHeightmap($$4, $$12.getLongArray($$5));
        }
        $$22.accept(($$0, $$1, $$2) -> {
            BlockEntity $$3 = this.getBlockEntity($$0, EntityCreationType.IMMEDIATE);
            if ($$3 != null && $$2 != null && $$3.getType() == $$1) {
                $$3.load($$2);
            }
        });
    }

    public void setLoaded(boolean $$0) {
        this.loaded = $$0;
    }

    public Level getLevel() {
        return this.level;
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    @Override
    public Stream<BlockPos> getLights() {
        return StreamSupport.stream((Spliterator)BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), this.getMinBuildHeight(), this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), this.getMaxBuildHeight() - 1, this.chunkPos.getMaxBlockZ()).spliterator(), (boolean)false).filter($$0 -> this.getBlockState((BlockPos)$$0).getLightEmission() != 0);
    }

    public void postProcessGeneration() {
        ChunkPos $$0 = this.getPos();
        for (int $$1 = 0; $$1 < this.postProcessing.length; ++$$1) {
            if (this.postProcessing[$$1] == null) continue;
            for (Short $$2 : this.postProcessing[$$1]) {
                BlockPos $$3 = ProtoChunk.unpackOffsetCoordinates($$2, this.getSectionYFromSectionIndex($$1), $$0);
                BlockState $$4 = this.getBlockState($$3);
                FluidState $$5 = $$4.getFluidState();
                if (!$$5.isEmpty()) {
                    $$5.tick(this.level, $$3);
                }
                if ($$4.getBlock() instanceof LiquidBlock) continue;
                BlockState $$6 = Block.updateFromNeighbourShapes($$4, this.level, $$3);
                this.level.setBlock($$3, $$6, 20);
            }
            this.postProcessing[$$1].clear();
        }
        for (BlockPos $$7 : ImmutableList.copyOf((Collection)this.pendingBlockEntities.keySet())) {
            this.getBlockEntity($$7);
        }
        this.pendingBlockEntities.clear();
        this.upgradeData.upgrade(this);
    }

    @Nullable
    private BlockEntity promotePendingBlockEntity(BlockPos $$0, CompoundTag $$1) {
        BlockEntity $$5;
        BlockState $$2 = this.getBlockState($$0);
        if ("DUMMY".equals((Object)$$1.getString("id"))) {
            if ($$2.hasBlockEntity()) {
                BlockEntity $$3 = ((EntityBlock)((Object)$$2.getBlock())).newBlockEntity($$0, $$2);
            } else {
                Object $$4 = null;
                LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", (Object)$$0, (Object)$$2);
            }
        } else {
            $$5 = BlockEntity.loadStatic($$0, $$2, $$1);
        }
        if ($$5 != null) {
            $$5.setLevel(this.level);
            this.addAndRegisterBlockEntity($$5);
        } else {
            LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", (Object)$$2, (Object)$$0);
        }
        return $$5;
    }

    public void unpackTicks(long $$0) {
        this.blockTicks.unpack($$0);
        this.fluidTicks.unpack($$0);
    }

    public void registerTickContainerInLevel(ServerLevel $$0) {
        ((LevelTicks)$$0.getBlockTicks()).addContainer(this.chunkPos, this.blockTicks);
        ((LevelTicks)$$0.getFluidTicks()).addContainer(this.chunkPos, this.fluidTicks);
    }

    public void unregisterTickContainerFromLevel(ServerLevel $$0) {
        ((LevelTicks)$$0.getBlockTicks()).removeContainer(this.chunkPos);
        ((LevelTicks)$$0.getFluidTicks()).removeContainer(this.chunkPos);
    }

    @Override
    public ChunkStatus getStatus() {
        return ChunkStatus.FULL;
    }

    public ChunkHolder.FullChunkStatus getFullStatus() {
        if (this.fullStatus == null) {
            return ChunkHolder.FullChunkStatus.BORDER;
        }
        return (ChunkHolder.FullChunkStatus)((Object)this.fullStatus.get());
    }

    public void setFullStatus(Supplier<ChunkHolder.FullChunkStatus> $$0) {
        this.fullStatus = $$0;
    }

    public void clearAllBlockEntities() {
        this.blockEntities.values().forEach(BlockEntity::setRemoved);
        this.blockEntities.clear();
        this.tickersInLevel.values().forEach($$0 -> $$0.rebind(NULL_TICKER));
        this.tickersInLevel.clear();
    }

    public void registerAllBlockEntitiesAfterLevelLoad() {
        this.blockEntities.values().forEach($$0 -> {
            Level $$1 = this.level;
            if ($$1 instanceof ServerLevel) {
                ServerLevel $$2 = (ServerLevel)$$1;
                this.addGameEventListener($$0, $$2);
            }
            this.updateBlockEntityTicker($$0);
        });
    }

    private <T extends BlockEntity> void addGameEventListener(T $$0, ServerLevel $$1) {
        GameEventListener $$3;
        Block $$2 = $$0.getBlockState().getBlock();
        if ($$2 instanceof EntityBlock && ($$3 = ((EntityBlock)((Object)$$2)).getListener($$1, $$0)) != null) {
            this.getListenerRegistry(SectionPos.blockToSectionCoord($$0.getBlockPos().getY())).register($$3);
        }
    }

    private <T extends BlockEntity> void updateBlockEntityTicker(T $$0) {
        BlockState $$1 = $$0.getBlockState();
        BlockEntityTicker<?> $$22 = $$1.getTicker(this.level, $$0.getType());
        if ($$22 == null) {
            this.removeBlockEntityTicker($$0.getBlockPos());
        } else {
            this.tickersInLevel.compute((Object)$$0.getBlockPos(), ($$2, $$3) -> {
                TickingBlockEntity $$4 = this.createTicker($$0, $$22);
                if ($$3 != null) {
                    $$3.rebind($$4);
                    return $$3;
                }
                if (this.isInLevel()) {
                    RebindableTickingBlockEntityWrapper $$5 = new RebindableTickingBlockEntityWrapper($$4);
                    this.level.addBlockEntityTicker($$5);
                    return $$5;
                }
                return null;
            });
        }
    }

    private <T extends BlockEntity> TickingBlockEntity createTicker(T $$0, BlockEntityTicker<T> $$1) {
        return new BoundTickingBlockEntity(this, $$0, $$1);
    }

    public boolean isClientLightReady() {
        return this.clientLightReady;
    }

    public void setClientLightReady(boolean $$0) {
        this.clientLightReady = $$0;
    }

    @FunctionalInterface
    public static interface PostLoadProcessor {
        public void run(LevelChunk var1);
    }

    public static enum EntityCreationType {
        IMMEDIATE,
        QUEUED,
        CHECK;

    }

    class RebindableTickingBlockEntityWrapper
    implements TickingBlockEntity {
        private TickingBlockEntity ticker;

        RebindableTickingBlockEntityWrapper(TickingBlockEntity $$0) {
            this.ticker = $$0;
        }

        void rebind(TickingBlockEntity $$0) {
            this.ticker = $$0;
        }

        @Override
        public void tick() {
            this.ticker.tick();
        }

        @Override
        public boolean isRemoved() {
            return this.ticker.isRemoved();
        }

        @Override
        public BlockPos getPos() {
            return this.ticker.getPos();
        }

        @Override
        public String getType() {
            return this.ticker.getType();
        }

        public String toString() {
            return this.ticker.toString() + " <wrapped>";
        }
    }

    static class BoundTickingBlockEntity<T extends BlockEntity>
    implements TickingBlockEntity {
        private final T blockEntity;
        private final BlockEntityTicker<T> ticker;
        private boolean loggedInvalidBlockState;
        final /* synthetic */ LevelChunk this$0;

        BoundTickingBlockEntity(T $$0, BlockEntityTicker<T> $$1) {
            this.this$0 = var1_1;
            this.blockEntity = $$0;
            this.ticker = $$1;
        }

        @Override
        public void tick() {
            BlockPos $$0;
            if (!((BlockEntity)this.blockEntity).isRemoved() && ((BlockEntity)this.blockEntity).hasLevel() && this.this$0.isTicking($$0 = ((BlockEntity)this.blockEntity).getBlockPos())) {
                try {
                    ProfilerFiller $$1 = this.this$0.level.getProfiler();
                    $$1.push((Supplier<String>)((Supplier)this::getType));
                    BlockState $$2 = this.this$0.getBlockState($$0);
                    if (((BlockEntity)this.blockEntity).getType().isValid($$2)) {
                        this.ticker.tick(this.this$0.level, ((BlockEntity)this.blockEntity).getBlockPos(), $$2, this.blockEntity);
                        this.loggedInvalidBlockState = false;
                    } else if (!this.loggedInvalidBlockState) {
                        this.loggedInvalidBlockState = true;
                        LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", new Object[]{LogUtils.defer(this::getType), LogUtils.defer(this::getPos), $$2});
                    }
                    $$1.pop();
                }
                catch (Throwable $$3) {
                    CrashReport $$4 = CrashReport.forThrowable($$3, "Ticking block entity");
                    CrashReportCategory $$5 = $$4.addCategory("Block entity being ticked");
                    ((BlockEntity)this.blockEntity).fillCrashReportCategory($$5);
                    throw new ReportedException($$4);
                }
            }
        }

        @Override
        public boolean isRemoved() {
            return ((BlockEntity)this.blockEntity).isRemoved();
        }

        @Override
        public BlockPos getPos() {
            return ((BlockEntity)this.blockEntity).getBlockPos();
        }

        @Override
        public String getType() {
            return BlockEntityType.getKey(((BlockEntity)this.blockEntity).getType()).toString();
        }

        public String toString() {
            return "Level ticker for " + this.getType() + "@" + this.getPos();
        }
    }
}