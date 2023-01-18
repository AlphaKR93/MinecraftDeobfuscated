/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectFunction
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;
import net.minecraft.world.level.entity.Visibility;
import org.slf4j.Logger;

public class TransientEntitySectionManager<T extends EntityAccess> {
    static final Logger LOGGER = LogUtils.getLogger();
    final LevelCallback<T> callbacks;
    final EntityLookup<T> entityStorage;
    final EntitySectionStorage<T> sectionStorage;
    private final LongSet tickingChunks = new LongOpenHashSet();
    private final LevelEntityGetter<T> entityGetter;

    public TransientEntitySectionManager(Class<T> $$02, LevelCallback<T> $$1) {
        this.entityStorage = new EntityLookup();
        this.sectionStorage = new EntitySectionStorage<T>($$02, (Long2ObjectFunction<Visibility>)((Long2ObjectFunction)$$0 -> this.tickingChunks.contains($$0) ? Visibility.TICKING : Visibility.TRACKED));
        this.callbacks = $$1;
        this.entityGetter = new LevelEntityGetterAdapter<T>(this.entityStorage, this.sectionStorage);
    }

    public void startTicking(ChunkPos $$0) {
        long $$1 = $$0.toLong();
        this.tickingChunks.add($$1);
        this.sectionStorage.getExistingSectionsInChunk($$1).forEach($$02 -> {
            Visibility $$1 = $$02.updateChunkStatus(Visibility.TICKING);
            if (!$$1.isTicking()) {
                $$02.getEntities().filter($$0 -> !$$0.isAlwaysTicking()).forEach(this.callbacks::onTickingStart);
            }
        });
    }

    public void stopTicking(ChunkPos $$0) {
        long $$1 = $$0.toLong();
        this.tickingChunks.remove($$1);
        this.sectionStorage.getExistingSectionsInChunk($$1).forEach($$02 -> {
            Visibility $$1 = $$02.updateChunkStatus(Visibility.TRACKED);
            if ($$1.isTicking()) {
                $$02.getEntities().filter($$0 -> !$$0.isAlwaysTicking()).forEach(this.callbacks::onTickingEnd);
            }
        });
    }

    public LevelEntityGetter<T> getEntityGetter() {
        return this.entityGetter;
    }

    public void addEntity(T $$0) {
        this.entityStorage.add($$0);
        long $$1 = SectionPos.asLong($$0.blockPosition());
        EntitySection<T> $$2 = this.sectionStorage.getOrCreateSection($$1);
        $$2.add($$0);
        $$0.setLevelCallback(new Callback(this, $$0, $$1, $$2));
        this.callbacks.onCreated($$0);
        this.callbacks.onTrackingStart($$0);
        if ($$0.isAlwaysTicking() || $$2.getStatus().isTicking()) {
            this.callbacks.onTickingStart($$0);
        }
    }

    @VisibleForDebug
    public int count() {
        return this.entityStorage.count();
    }

    void removeSectionIfEmpty(long $$0, EntitySection<T> $$1) {
        if ($$1.isEmpty()) {
            this.sectionStorage.remove($$0);
        }
    }

    @VisibleForDebug
    public String gatherStats() {
        return this.entityStorage.count() + "," + this.sectionStorage.count() + "," + this.tickingChunks.size();
    }

    static class Callback
    implements EntityInLevelCallback {
        private final T entity;
        private long currentSectionKey;
        private EntitySection<T> currentSection;
        final /* synthetic */ TransientEntitySectionManager this$0;

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
                this.this$0.callbacks.onSectionChange(this.entity);
                if (!this.entity.isAlwaysTicking()) {
                    boolean $$4 = $$2.isTicking();
                    boolean $$5 = $$3.getStatus().isTicking();
                    if ($$4 && !$$5) {
                        this.this$0.callbacks.onTickingEnd(this.entity);
                    } else if (!$$4 && $$5) {
                        this.this$0.callbacks.onTickingStart(this.entity);
                    }
                }
            }
        }

        @Override
        public void onRemove(Entity.RemovalReason $$0) {
            Visibility $$1;
            if (!this.currentSection.remove(this.entity)) {
                LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), $$0});
            }
            if (($$1 = this.currentSection.getStatus()).isTicking() || this.entity.isAlwaysTicking()) {
                this.this$0.callbacks.onTickingEnd(this.entity);
            }
            this.this$0.callbacks.onTrackingEnd(this.entity);
            this.this$0.callbacks.onDestroyed(this.entity);
            this.this$0.entityStorage.remove(this.entity);
            this.entity.setLevelCallback(NULL);
            this.this$0.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
        }
    }
}