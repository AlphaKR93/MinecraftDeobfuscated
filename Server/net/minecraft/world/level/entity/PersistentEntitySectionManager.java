/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectFunction
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.io.IOException
 *  java.io.UncheckedIOException
 *  java.io.Writer
 *  java.lang.AutoCloseable
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Queue
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;
import net.minecraft.world.level.entity.Visibility;
import org.slf4j.Logger;

public class PersistentEntitySectionManager<T extends EntityAccess>
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    final Set<UUID> knownUuids = Sets.newHashSet();
    final LevelCallback<T> callbacks;
    private final EntityPersistentStorage<T> permanentStorage;
    private final EntityLookup<T> visibleEntityStorage;
    final EntitySectionStorage<T> sectionStorage;
    private final LevelEntityGetter<T> entityGetter;
    private final Long2ObjectMap<Visibility> chunkVisibility = new Long2ObjectOpenHashMap();
    private final Long2ObjectMap<ChunkLoadStatus> chunkLoadStatuses = new Long2ObjectOpenHashMap();
    private final LongSet chunksToUnload = new LongOpenHashSet();
    private final Queue<ChunkEntities<T>> loadingInbox = Queues.newConcurrentLinkedQueue();

    public PersistentEntitySectionManager(Class<T> $$0, LevelCallback<T> $$1, EntityPersistentStorage<T> $$2) {
        this.visibleEntityStorage = new EntityLookup();
        this.sectionStorage = new EntitySectionStorage<T>($$0, (Long2ObjectFunction<Visibility>)this.chunkVisibility);
        this.chunkVisibility.defaultReturnValue((Object)Visibility.HIDDEN);
        this.chunkLoadStatuses.defaultReturnValue((Object)ChunkLoadStatus.FRESH);
        this.callbacks = $$1;
        this.permanentStorage = $$2;
        this.entityGetter = new LevelEntityGetterAdapter<T>(this.visibleEntityStorage, this.sectionStorage);
    }

    void removeSectionIfEmpty(long $$0, EntitySection<T> $$1) {
        if ($$1.isEmpty()) {
            this.sectionStorage.remove($$0);
        }
    }

    private boolean addEntityUuid(T $$0) {
        if (!this.knownUuids.add((Object)$$0.getUUID())) {
            LOGGER.warn("UUID of added entity already exists: {}", $$0);
            return false;
        }
        return true;
    }

    public boolean addNewEntity(T $$0) {
        return this.addEntity($$0, false);
    }

    private boolean addEntity(T $$0, boolean $$1) {
        Visibility $$4;
        if (!this.addEntityUuid($$0)) {
            return false;
        }
        long $$2 = SectionPos.asLong($$0.blockPosition());
        EntitySection<T> $$3 = this.sectionStorage.getOrCreateSection($$2);
        $$3.add($$0);
        $$0.setLevelCallback(new Callback(this, $$0, $$2, $$3));
        if (!$$1) {
            this.callbacks.onCreated($$0);
        }
        if (($$4 = PersistentEntitySectionManager.getEffectiveStatus($$0, $$3.getStatus())).isAccessible()) {
            this.startTracking($$0);
        }
        if ($$4.isTicking()) {
            this.startTicking($$0);
        }
        return true;
    }

    static <T extends EntityAccess> Visibility getEffectiveStatus(T $$0, Visibility $$1) {
        return $$0.isAlwaysTicking() ? Visibility.TICKING : $$1;
    }

    public void addLegacyChunkEntities(Stream<T> $$02) {
        $$02.forEach($$0 -> this.addEntity($$0, true));
    }

    public void addWorldGenChunkEntities(Stream<T> $$02) {
        $$02.forEach($$0 -> this.addEntity($$0, false));
    }

    void startTicking(T $$0) {
        this.callbacks.onTickingStart($$0);
    }

    void stopTicking(T $$0) {
        this.callbacks.onTickingEnd($$0);
    }

    void startTracking(T $$0) {
        this.visibleEntityStorage.add($$0);
        this.callbacks.onTrackingStart($$0);
    }

    void stopTracking(T $$0) {
        this.callbacks.onTrackingEnd($$0);
        this.visibleEntityStorage.remove($$0);
    }

    public void updateChunkStatus(ChunkPos $$0, ChunkHolder.FullChunkStatus $$1) {
        Visibility $$2 = Visibility.fromFullChunkStatus($$1);
        this.updateChunkStatus($$0, $$2);
    }

    public void updateChunkStatus(ChunkPos $$0, Visibility $$12) {
        long $$2 = $$0.toLong();
        if ($$12 == Visibility.HIDDEN) {
            this.chunkVisibility.remove($$2);
            this.chunksToUnload.add($$2);
        } else {
            this.chunkVisibility.put($$2, (Object)$$12);
            this.chunksToUnload.remove($$2);
            this.ensureChunkQueuedForLoad($$2);
        }
        this.sectionStorage.getExistingSectionsInChunk($$2).forEach($$1 -> {
            Visibility $$2 = $$1.updateChunkStatus($$12);
            boolean $$3 = $$2.isAccessible();
            boolean $$4 = $$12.isAccessible();
            boolean $$5 = $$2.isTicking();
            boolean $$6 = $$12.isTicking();
            if ($$5 && !$$6) {
                $$1.getEntities().filter($$0 -> !$$0.isAlwaysTicking()).forEach(this::stopTicking);
            }
            if ($$3 && !$$4) {
                $$1.getEntities().filter($$0 -> !$$0.isAlwaysTicking()).forEach(this::stopTracking);
            } else if (!$$3 && $$4) {
                $$1.getEntities().filter($$0 -> !$$0.isAlwaysTicking()).forEach(this::startTracking);
            }
            if (!$$5 && $$6) {
                $$1.getEntities().filter($$0 -> !$$0.isAlwaysTicking()).forEach(this::startTicking);
            }
        });
    }

    private void ensureChunkQueuedForLoad(long $$0) {
        ChunkLoadStatus $$1 = (ChunkLoadStatus)((Object)this.chunkLoadStatuses.get($$0));
        if ($$1 == ChunkLoadStatus.FRESH) {
            this.requestChunkLoad($$0);
        }
    }

    private boolean storeChunkSections(long $$02, Consumer<T> $$1) {
        ChunkLoadStatus $$2 = (ChunkLoadStatus)((Object)this.chunkLoadStatuses.get($$02));
        if ($$2 == ChunkLoadStatus.PENDING) {
            return false;
        }
        List $$3 = (List)this.sectionStorage.getExistingSectionsInChunk($$02).flatMap($$0 -> $$0.getEntities().filter(EntityAccess::shouldBeSaved)).collect(Collectors.toList());
        if ($$3.isEmpty()) {
            if ($$2 == ChunkLoadStatus.LOADED) {
                this.permanentStorage.storeEntities(new ChunkEntities(new ChunkPos($$02), ImmutableList.of()));
            }
            return true;
        }
        if ($$2 == ChunkLoadStatus.FRESH) {
            this.requestChunkLoad($$02);
            return false;
        }
        this.permanentStorage.storeEntities(new ChunkEntities(new ChunkPos($$02), $$3));
        $$3.forEach($$1);
        return true;
    }

    private void requestChunkLoad(long $$0) {
        this.chunkLoadStatuses.put($$0, (Object)ChunkLoadStatus.PENDING);
        ChunkPos $$12 = new ChunkPos($$0);
        this.permanentStorage.loadEntities($$12).thenAccept(arg_0 -> this.loadingInbox.add(arg_0)).exceptionally($$1 -> {
            LOGGER.error("Failed to read chunk {}", (Object)$$12, $$1);
            return null;
        });
    }

    private boolean processChunkUnload(long $$02) {
        boolean $$1 = this.storeChunkSections($$02, $$0 -> $$0.getPassengersAndSelf().forEach(this::unloadEntity));
        if (!$$1) {
            return false;
        }
        this.chunkLoadStatuses.remove($$02);
        return true;
    }

    private void unloadEntity(EntityAccess $$0) {
        $$0.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        $$0.setLevelCallback(EntityInLevelCallback.NULL);
    }

    private void processUnloads() {
        this.chunksToUnload.removeIf($$0 -> {
            if (this.chunkVisibility.get($$0) != Visibility.HIDDEN) {
                return true;
            }
            return this.processChunkUnload($$0);
        });
    }

    private void processPendingLoads() {
        ChunkEntities $$02;
        while (($$02 = (ChunkEntities)this.loadingInbox.poll()) != null) {
            $$02.getEntities().forEach($$0 -> this.addEntity($$0, true));
            this.chunkLoadStatuses.put($$02.getPos().toLong(), (Object)ChunkLoadStatus.LOADED);
        }
    }

    public void tick() {
        this.processPendingLoads();
        this.processUnloads();
    }

    private LongSet getAllChunksToSave() {
        LongSet $$0 = this.sectionStorage.getAllChunksWithExistingSections();
        for (Long2ObjectMap.Entry $$1 : Long2ObjectMaps.fastIterable(this.chunkLoadStatuses)) {
            if ($$1.getValue() != ChunkLoadStatus.LOADED) continue;
            $$0.add($$1.getLongKey());
        }
        return $$0;
    }

    public void autoSave() {
        this.getAllChunksToSave().forEach($$02 -> {
            boolean $$1;
            boolean bl = $$1 = this.chunkVisibility.get($$02) == Visibility.HIDDEN;
            if ($$1) {
                this.processChunkUnload($$02);
            } else {
                this.storeChunkSections($$02, $$0 -> {});
            }
        });
    }

    public void saveAll() {
        LongSet $$0 = this.getAllChunksToSave();
        while (!$$0.isEmpty()) {
            this.permanentStorage.flush(false);
            this.processPendingLoads();
            $$0.removeIf($$02 -> {
                boolean $$1 = this.chunkVisibility.get($$02) == Visibility.HIDDEN;
                return $$1 ? this.processChunkUnload($$02) : this.storeChunkSections($$02, $$0 -> {});
            });
        }
        this.permanentStorage.flush(true);
    }

    public void close() throws IOException {
        this.saveAll();
        this.permanentStorage.close();
    }

    public boolean isLoaded(UUID $$0) {
        return this.knownUuids.contains((Object)$$0);
    }

    public LevelEntityGetter<T> getEntityGetter() {
        return this.entityGetter;
    }

    public boolean canPositionTick(BlockPos $$0) {
        return ((Visibility)((Object)this.chunkVisibility.get(ChunkPos.asLong($$0)))).isTicking();
    }

    public boolean canPositionTick(ChunkPos $$0) {
        return ((Visibility)((Object)this.chunkVisibility.get($$0.toLong()))).isTicking();
    }

    public boolean areEntitiesLoaded(long $$0) {
        return this.chunkLoadStatuses.get($$0) == ChunkLoadStatus.LOADED;
    }

    public void dumpSections(Writer $$0) throws IOException {
        CsvOutput $$12 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("visibility").addColumn("load_status").addColumn("entity_count").build($$0);
        this.sectionStorage.getAllChunksWithExistingSections().forEach($$1 -> {
            ChunkLoadStatus $$22 = (ChunkLoadStatus)((Object)((Object)this.chunkLoadStatuses.get($$1)));
            this.sectionStorage.getExistingSectionPositionsInChunk($$1).forEach($$2 -> {
                EntitySection<T> $$3 = this.sectionStorage.getSection($$2);
                if ($$3 != null) {
                    try {
                        $$12.writeRow(new Object[]{SectionPos.x($$2), SectionPos.y($$2), SectionPos.z($$2), $$3.getStatus(), $$22, $$3.size()});
                    }
                    catch (IOException $$4) {
                        throw new UncheckedIOException($$4);
                    }
                }
            });
        });
    }

    @VisibleForDebug
    public String gatherStats() {
        return this.knownUuids.size() + "," + this.visibleEntityStorage.count() + "," + this.sectionStorage.count() + "," + this.chunkLoadStatuses.size() + "," + this.chunkVisibility.size() + "," + this.loadingInbox.size() + "," + this.chunksToUnload.size();
    }

    static enum ChunkLoadStatus {
        FRESH,
        PENDING,
        LOADED;

    }

    static class Callback
    implements EntityInLevelCallback {
        private final T entity;
        private long currentSectionKey;
        private EntitySection<T> currentSection;
        final /* synthetic */ PersistentEntitySectionManager this$0;

        /*
         * WARNING - Possible parameter corruption
         * WARNING - void declaration
         */
        Callback(T $$0, long $$1, EntitySection<T> $$2) {
            void var3_3;
            this.this$0 = l;
            this.entity = $$0;
            this.currentSectionKey = var3_3;
            this.currentSection = $$2;
        }

        @Override
        public void onMove() {
            BlockPos $$0 = this.entity.blockPosition();
            long $$1 = SectionPos.asLong($$0);
            if ($$1 != this.currentSectionKey) {
                Visibility $$2 = this.currentSection.getStatus();
                if (!this.currentSection.remove(this.entity)) {
                    LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), $$1});
                }
                this.this$0.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
                EntitySection $$3 = this.this$0.sectionStorage.getOrCreateSection($$1);
                $$3.add(this.entity);
                this.currentSection = $$3;
                this.currentSectionKey = $$1;
                this.updateStatus($$2, $$3.getStatus());
            }
        }

        private void updateStatus(Visibility $$0, Visibility $$1) {
            Visibility $$3;
            Visibility $$2 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, $$0);
            if ($$2 == ($$3 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, $$1))) {
                if ($$3.isAccessible()) {
                    this.this$0.callbacks.onSectionChange(this.entity);
                }
                return;
            }
            boolean $$4 = $$2.isAccessible();
            boolean $$5 = $$3.isAccessible();
            if ($$4 && !$$5) {
                this.this$0.stopTracking(this.entity);
            } else if (!$$4 && $$5) {
                this.this$0.startTracking(this.entity);
            }
            boolean $$6 = $$2.isTicking();
            boolean $$7 = $$3.isTicking();
            if ($$6 && !$$7) {
                this.this$0.stopTicking(this.entity);
            } else if (!$$6 && $$7) {
                this.this$0.startTicking(this.entity);
            }
            if ($$5) {
                this.this$0.callbacks.onSectionChange(this.entity);
            }
        }

        @Override
        public void onRemove(Entity.RemovalReason $$0) {
            Visibility $$1;
            if (!this.currentSection.remove(this.entity)) {
                LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), $$0});
            }
            if (($$1 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, this.currentSection.getStatus())).isTicking()) {
                this.this$0.stopTicking(this.entity);
            }
            if ($$1.isAccessible()) {
                this.this$0.stopTracking(this.entity);
            }
            if ($$0.shouldDestroy()) {
                this.this$0.callbacks.onDestroyed(this.entity);
            }
            this.this$0.knownUuids.remove((Object)this.entity.getUUID());
            this.entity.setLevelCallback(NULL);
            this.this$0.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
        }
    }
}