/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.shorts.ShortOpenHashSet
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.BitSet
 *  java.util.List
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.atomic.AtomicReferenceArray
 *  java.util.function.BiConsumer
 *  java.util.function.IntConsumer
 *  java.util.function.IntSupplier
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.DebugBuffer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder {
    public static final Either<ChunkAccess, ChunkLoadingFailure> UNLOADED_CHUNK = Either.right((Object)ChunkLoadingFailure.UNLOADED);
    public static final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
    public static final Either<LevelChunk, ChunkLoadingFailure> UNLOADED_LEVEL_CHUNK = Either.right((Object)ChunkLoadingFailure.UNLOADED);
    private static final Either<ChunkAccess, ChunkLoadingFailure> NOT_DONE_YET = Either.right((Object)ChunkLoadingFailure.UNLOADED);
    private static final CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
    private static final FullChunkStatus[] FULL_CHUNK_STATUSES = FullChunkStatus.values();
    private static final int BLOCKS_BEFORE_RESEND_FUDGE = 64;
    private final AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> futures = new AtomicReferenceArray(CHUNK_STATUSES.size());
    private final LevelHeightAccessor levelHeightAccessor;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private CompletableFuture<ChunkAccess> chunkToSave = CompletableFuture.completedFuture(null);
    @Nullable
    private final DebugBuffer<ChunkSaveDebug> chunkToSaveHistory = null;
    private int oldTicketLevel;
    private int ticketLevel;
    private int queueLevel;
    final ChunkPos pos;
    private boolean hasChangedSections;
    private final ShortSet[] changedBlocksPerSection;
    private final BitSet blockChangedLightSectionFilter = new BitSet();
    private final BitSet skyChangedLightSectionFilter = new BitSet();
    private final LevelLightEngine lightEngine;
    private final LevelChangeListener onLevelChange;
    private final PlayerProvider playerProvider;
    private boolean wasAccessibleSinceLastSave;
    private boolean resendLight;
    private CompletableFuture<Void> pendingFullStateConfirmation = CompletableFuture.completedFuture(null);

    public ChunkHolder(ChunkPos $$0, int $$1, LevelHeightAccessor $$2, LevelLightEngine $$3, LevelChangeListener $$4, PlayerProvider $$5) {
        this.pos = $$0;
        this.levelHeightAccessor = $$2;
        this.lightEngine = $$3;
        this.onLevelChange = $$4;
        this.playerProvider = $$5;
        this.ticketLevel = this.oldTicketLevel = ChunkMap.MAX_CHUNK_DISTANCE + 1;
        this.queueLevel = this.oldTicketLevel;
        this.setTicketLevel($$1);
        this.changedBlocksPerSection = new ShortSet[$$2.getSectionsCount()];
    }

    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getFutureIfPresentUnchecked(ChunkStatus $$0) {
        CompletableFuture $$1 = (CompletableFuture)this.futures.get($$0.getIndex());
        return $$1 == null ? UNLOADED_CHUNK_FUTURE : $$1;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getFutureIfPresent(ChunkStatus $$0) {
        if (ChunkHolder.getStatus(this.ticketLevel).isOrAfter($$0)) {
            return this.getFutureIfPresentUnchecked($$0);
        }
        return UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getTickingChunkFuture() {
        return this.tickingChunkFuture;
    }

    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getEntityTickingChunkFuture() {
        return this.entityTickingChunkFuture;
    }

    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getFullChunkFuture() {
        return this.fullChunkFuture;
    }

    @Nullable
    public LevelChunk getTickingChunk() {
        CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> $$0 = this.getTickingChunkFuture();
        Either $$1 = (Either)$$0.getNow(null);
        if ($$1 == null) {
            return null;
        }
        return (LevelChunk)$$1.left().orElse(null);
    }

    @Nullable
    public LevelChunk getFullChunk() {
        CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> $$0 = this.getFullChunkFuture();
        Either $$1 = (Either)$$0.getNow(null);
        if ($$1 == null) {
            return null;
        }
        return (LevelChunk)$$1.left().orElse(null);
    }

    @Nullable
    public ChunkStatus getLastAvailableStatus() {
        for (int $$0 = CHUNK_STATUSES.size() - 1; $$0 >= 0; --$$0) {
            ChunkStatus $$1 = (ChunkStatus)CHUNK_STATUSES.get($$0);
            CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> $$2 = this.getFutureIfPresentUnchecked($$1);
            if (!((Either)$$2.getNow(UNLOADED_CHUNK)).left().isPresent()) continue;
            return $$1;
        }
        return null;
    }

    @Nullable
    public ChunkAccess getLastAvailable() {
        for (int $$0 = CHUNK_STATUSES.size() - 1; $$0 >= 0; --$$0) {
            Optional $$3;
            ChunkStatus $$1 = (ChunkStatus)CHUNK_STATUSES.get($$0);
            CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> $$2 = this.getFutureIfPresentUnchecked($$1);
            if ($$2.isCompletedExceptionally() || !($$3 = ((Either)$$2.getNow(UNLOADED_CHUNK)).left()).isPresent()) continue;
            return (ChunkAccess)$$3.get();
        }
        return null;
    }

    public CompletableFuture<ChunkAccess> getChunkToSave() {
        return this.chunkToSave;
    }

    public void blockChanged(BlockPos $$0) {
        LevelChunk $$1 = this.getTickingChunk();
        if ($$1 == null) {
            return;
        }
        int $$2 = this.levelHeightAccessor.getSectionIndex($$0.getY());
        if (this.changedBlocksPerSection[$$2] == null) {
            this.hasChangedSections = true;
            this.changedBlocksPerSection[$$2] = new ShortOpenHashSet();
        }
        this.changedBlocksPerSection[$$2].add(SectionPos.sectionRelativePos($$0));
    }

    public void sectionLightChanged(LightLayer $$0, int $$1) {
        Either $$2 = (Either)this.getFutureIfPresent(ChunkStatus.FEATURES).getNow(null);
        if ($$2 == null) {
            return;
        }
        ChunkAccess $$3 = (ChunkAccess)$$2.left().orElse(null);
        if ($$3 == null) {
            return;
        }
        $$3.setUnsaved(true);
        LevelChunk $$4 = this.getTickingChunk();
        if ($$4 == null) {
            return;
        }
        int $$5 = this.lightEngine.getMinLightSection();
        int $$6 = this.lightEngine.getMaxLightSection();
        if ($$1 < $$5 || $$1 > $$6) {
            return;
        }
        int $$7 = $$1 - $$5;
        if ($$0 == LightLayer.SKY) {
            this.skyChangedLightSectionFilter.set($$7);
        } else {
            this.blockChangedLightSectionFilter.set($$7);
        }
    }

    public void broadcastChanges(LevelChunk $$0) {
        if (!this.hasChangedSections && this.skyChangedLightSectionFilter.isEmpty() && this.blockChangedLightSectionFilter.isEmpty()) {
            return;
        }
        Level $$12 = $$0.getLevel();
        int $$22 = 0;
        for (int $$3 = 0; $$3 < this.changedBlocksPerSection.length; ++$$3) {
            $$22 += this.changedBlocksPerSection[$$3] != null ? this.changedBlocksPerSection[$$3].size() : 0;
        }
        this.resendLight |= $$22 >= 64;
        if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
            this.broadcast(new ClientboundLightUpdatePacket($$0.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter, true), !this.resendLight);
            this.skyChangedLightSectionFilter.clear();
            this.blockChangedLightSectionFilter.clear();
        }
        for (int $$4 = 0; $$4 < this.changedBlocksPerSection.length; ++$$4) {
            ShortSet $$5 = this.changedBlocksPerSection[$$4];
            if ($$5 == null) continue;
            int $$6 = this.levelHeightAccessor.getSectionYFromSectionIndex($$4);
            SectionPos $$7 = SectionPos.of($$0.getPos(), $$6);
            if ($$5.size() == 1) {
                BlockPos $$8 = $$7.relativeToBlockPos($$5.iterator().nextShort());
                BlockState $$9 = $$12.getBlockState($$8);
                this.broadcast(new ClientboundBlockUpdatePacket($$8, $$9), false);
                this.broadcastBlockEntityIfNeeded($$12, $$8, $$9);
            } else {
                LevelChunkSection $$10 = $$0.getSection($$4);
                ClientboundSectionBlocksUpdatePacket $$11 = new ClientboundSectionBlocksUpdatePacket($$7, $$5, $$10, this.resendLight);
                this.broadcast($$11, false);
                $$11.runUpdates((BiConsumer<BlockPos, BlockState>)((BiConsumer)($$1, $$2) -> this.broadcastBlockEntityIfNeeded($$12, (BlockPos)$$1, (BlockState)$$2)));
            }
            this.changedBlocksPerSection[$$4] = null;
        }
        this.hasChangedSections = false;
    }

    private void broadcastBlockEntityIfNeeded(Level $$0, BlockPos $$1, BlockState $$2) {
        if ($$2.hasBlockEntity()) {
            this.broadcastBlockEntity($$0, $$1);
        }
    }

    private void broadcastBlockEntity(Level $$0, BlockPos $$1) {
        Packet<ClientGamePacketListener> $$3;
        BlockEntity $$2 = $$0.getBlockEntity($$1);
        if ($$2 != null && ($$3 = $$2.getUpdatePacket()) != null) {
            this.broadcast($$3, false);
        }
    }

    private void broadcast(Packet<?> $$0, boolean $$12) {
        this.playerProvider.getPlayers(this.pos, $$12).forEach($$1 -> $$1.connection.send($$0));
    }

    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getOrScheduleFuture(ChunkStatus $$0, ChunkMap $$1) {
        int $$2 = $$0.getIndex();
        CompletableFuture $$3 = (CompletableFuture)this.futures.get($$2);
        if ($$3 != null) {
            Either $$4 = (Either)$$3.getNow(NOT_DONE_YET);
            if ($$4 == null) {
                String $$5 = "value in future for status: " + $$0 + " was incorrectly set to null at chunk: " + this.pos;
                throw $$1.debugFuturesAndCreateReportedException(new IllegalStateException("null value previously set for chunk status"), $$5);
            }
            if ($$4 == NOT_DONE_YET || $$4.right().isEmpty()) {
                return $$3;
            }
        }
        if (ChunkHolder.getStatus(this.ticketLevel).isOrAfter($$0)) {
            CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> $$6 = $$1.schedule(this, $$0);
            this.updateChunkToSave($$6, "schedule " + $$0);
            this.futures.set($$2, $$6);
            return $$6;
        }
        return $$3 == null ? UNLOADED_CHUNK_FUTURE : $$3;
    }

    protected void addSaveDependency(String $$02, CompletableFuture<?> $$12) {
        if (this.chunkToSaveHistory != null) {
            this.chunkToSaveHistory.push(new ChunkSaveDebug(Thread.currentThread(), $$12, $$02));
        }
        this.chunkToSave = this.chunkToSave.thenCombine($$12, ($$0, $$1) -> $$0);
    }

    private void updateChunkToSave(CompletableFuture<? extends Either<? extends ChunkAccess, ChunkLoadingFailure>> $$0, String $$1) {
        if (this.chunkToSaveHistory != null) {
            this.chunkToSaveHistory.push(new ChunkSaveDebug(Thread.currentThread(), $$0, $$1));
        }
        this.chunkToSave = this.chunkToSave.thenCombine($$0, ($$02, $$12) -> (ChunkAccess)$$12.map($$0 -> $$0, $$1 -> $$02));
    }

    public FullChunkStatus getFullStatus() {
        return ChunkHolder.getFullChunkStatus(this.ticketLevel);
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public int getTicketLevel() {
        return this.ticketLevel;
    }

    public int getQueueLevel() {
        return this.queueLevel;
    }

    private void setQueueLevel(int $$0) {
        this.queueLevel = $$0;
    }

    public void setTicketLevel(int $$0) {
        this.ticketLevel = $$0;
    }

    private void scheduleFullChunkPromotion(ChunkMap $$0, CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> $$1, Executor $$2, FullChunkStatus $$3) {
        this.pendingFullStateConfirmation.cancel(false);
        CompletableFuture $$4 = new CompletableFuture();
        $$4.thenRunAsync(() -> $$0.onFullChunkStatusChange(this.pos, $$3), $$2);
        this.pendingFullStateConfirmation = $$4;
        $$1.thenAccept($$12 -> $$12.ifLeft($$1 -> $$4.complete(null)));
    }

    private void demoteFullChunk(ChunkMap $$0, FullChunkStatus $$1) {
        this.pendingFullStateConfirmation.cancel(false);
        $$0.onFullChunkStatusChange(this.pos, $$1);
    }

    protected void updateFutures(ChunkMap $$0, Executor $$1) {
        ChunkStatus $$2 = ChunkHolder.getStatus(this.oldTicketLevel);
        ChunkStatus $$3 = ChunkHolder.getStatus(this.ticketLevel);
        boolean $$4 = this.oldTicketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
        boolean $$5 = this.ticketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
        FullChunkStatus $$6 = ChunkHolder.getFullChunkStatus(this.oldTicketLevel);
        FullChunkStatus $$7 = ChunkHolder.getFullChunkStatus(this.ticketLevel);
        if ($$4) {
            int $$9;
            Either $$8 = Either.right((Object)new ChunkLoadingFailure(){

                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos;
                }
            });
            int n = $$9 = $$5 ? $$3.getIndex() + 1 : 0;
            while ($$9 <= $$2.getIndex()) {
                CompletableFuture $$10 = (CompletableFuture)this.futures.get($$9);
                if ($$10 == null) {
                    this.futures.set($$9, (Object)CompletableFuture.completedFuture((Object)$$8));
                }
                ++$$9;
            }
        }
        boolean $$11 = $$6.isOrAfter(FullChunkStatus.BORDER);
        boolean $$12 = $$7.isOrAfter(FullChunkStatus.BORDER);
        this.wasAccessibleSinceLastSave |= $$12;
        if (!$$11 && $$12) {
            this.fullChunkFuture = $$0.prepareAccessibleChunk(this);
            this.scheduleFullChunkPromotion($$0, this.fullChunkFuture, $$1, FullChunkStatus.BORDER);
            this.updateChunkToSave(this.fullChunkFuture, "full");
        }
        if ($$11 && !$$12) {
            this.fullChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }
        boolean $$13 = $$6.isOrAfter(FullChunkStatus.TICKING);
        boolean $$14 = $$7.isOrAfter(FullChunkStatus.TICKING);
        if (!$$13 && $$14) {
            this.tickingChunkFuture = $$0.prepareTickingChunk(this);
            this.scheduleFullChunkPromotion($$0, this.tickingChunkFuture, $$1, FullChunkStatus.TICKING);
            this.updateChunkToSave(this.tickingChunkFuture, "ticking");
        }
        if ($$13 && !$$14) {
            this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }
        boolean $$15 = $$6.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        boolean $$16 = $$7.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        if (!$$15 && $$16) {
            if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
                throw Util.pauseInIde(new IllegalStateException());
            }
            this.entityTickingChunkFuture = $$0.prepareEntityTickingChunk(this.pos);
            this.scheduleFullChunkPromotion($$0, this.entityTickingChunkFuture, $$1, FullChunkStatus.ENTITY_TICKING);
            this.updateChunkToSave(this.entityTickingChunkFuture, "entity ticking");
        }
        if ($$15 && !$$16) {
            this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }
        if (!$$7.isOrAfter($$6)) {
            this.demoteFullChunk($$0, $$7);
        }
        this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
        this.oldTicketLevel = this.ticketLevel;
    }

    public static ChunkStatus getStatus(int $$0) {
        if ($$0 < 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.getStatusAroundFullChunk($$0 - 33);
    }

    public static FullChunkStatus getFullChunkStatus(int $$0) {
        return FULL_CHUNK_STATUSES[Mth.clamp(33 - $$0 + 1, 0, FULL_CHUNK_STATUSES.length - 1)];
    }

    public boolean wasAccessibleSinceLastSave() {
        return this.wasAccessibleSinceLastSave;
    }

    public void refreshAccessibility() {
        this.wasAccessibleSinceLastSave = ChunkHolder.getFullChunkStatus(this.ticketLevel).isOrAfter(FullChunkStatus.BORDER);
    }

    public void replaceProtoChunk(ImposterProtoChunk $$0) {
        for (int $$1 = 0; $$1 < this.futures.length(); ++$$1) {
            Optional $$3;
            CompletableFuture $$2 = (CompletableFuture)this.futures.get($$1);
            if ($$2 == null || ($$3 = ((Either)$$2.getNow(UNLOADED_CHUNK)).left()).isEmpty() || !($$3.get() instanceof ProtoChunk)) continue;
            this.futures.set($$1, (Object)CompletableFuture.completedFuture((Object)Either.left((Object)$$0)));
        }
        this.updateChunkToSave((CompletableFuture<? extends Either<? extends ChunkAccess, ChunkLoadingFailure>>)CompletableFuture.completedFuture((Object)Either.left((Object)$$0.getWrapped())), "replaceProto");
    }

    public List<Pair<ChunkStatus, CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>>> getAllFutures() {
        ArrayList $$0 = new ArrayList();
        for (int $$1 = 0; $$1 < CHUNK_STATUSES.size(); ++$$1) {
            $$0.add((Object)Pair.of((Object)((ChunkStatus)CHUNK_STATUSES.get($$1)), (Object)((CompletableFuture)this.futures.get($$1))));
        }
        return $$0;
    }

    @FunctionalInterface
    public static interface LevelChangeListener {
        public void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
    }

    public static interface PlayerProvider {
        public List<ServerPlayer> getPlayers(ChunkPos var1, boolean var2);
    }

    static final class ChunkSaveDebug {
        private final Thread thread;
        private final CompletableFuture<?> future;
        private final String source;

        ChunkSaveDebug(Thread $$0, CompletableFuture<?> $$1, String $$2) {
            this.thread = $$0;
            this.future = $$1;
            this.source = $$2;
        }
    }

    public static enum FullChunkStatus {
        INACCESSIBLE,
        BORDER,
        TICKING,
        ENTITY_TICKING;


        public boolean isOrAfter(FullChunkStatus $$0) {
            return this.ordinal() >= $$0.ordinal();
        }
    }

    public static interface ChunkLoadingFailure {
        public static final ChunkLoadingFailure UNLOADED = new ChunkLoadingFailure(){

            public String toString() {
                return "UNLOADED";
            }
        };
    }
}