/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.slf4j.Logger;

public class PoiSection {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Short2ObjectMap<PoiRecord> records = new Short2ObjectOpenHashMap();
    private final Map<Holder<PoiType>, Set<PoiRecord>> byType = Maps.newHashMap();
    private final Runnable setDirty;
    private boolean isValid;

    public static Codec<PoiSection> codec(Runnable $$0) {
        return RecordCodecBuilder.create($$1 -> $$1.group((App)RecordCodecBuilder.point((Object)$$0), (App)Codec.BOOL.optionalFieldOf("Valid", (Object)false).forGetter($$0 -> $$0.isValid), (App)PoiRecord.codec($$0).listOf().fieldOf("Records").forGetter($$0 -> ImmutableList.copyOf((Collection)$$0.records.values()))).apply((Applicative)$$1, PoiSection::new)).orElseGet(Util.prefix("Failed to read POI section: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0))), () -> new PoiSection($$0, false, (List<PoiRecord>)ImmutableList.of()));
    }

    public PoiSection(Runnable $$0) {
        this($$0, true, (List<PoiRecord>)ImmutableList.of());
    }

    private PoiSection(Runnable $$0, boolean $$1, List<PoiRecord> $$2) {
        this.setDirty = $$0;
        this.isValid = $$1;
        $$2.forEach(this::add);
    }

    public Stream<PoiRecord> getRecords(Predicate<Holder<PoiType>> $$02, PoiManager.Occupancy $$12) {
        return this.byType.entrySet().stream().filter($$1 -> $$02.test((Object)((Holder)$$1.getKey()))).flatMap($$0 -> ((Set)$$0.getValue()).stream()).filter($$12.getTest());
    }

    public void add(BlockPos $$02, Holder<PoiType> $$1) {
        if (this.add(new PoiRecord($$02, $$1, this.setDirty))) {
            LOGGER.debug("Added POI of type {} @ {}", $$1.unwrapKey().map($$0 -> $$0.location().toString()).orElse((Object)"[unregistered]"), (Object)$$02);
            this.setDirty.run();
        }
    }

    private boolean add(PoiRecord $$02) {
        BlockPos $$1 = $$02.getPos();
        Holder<PoiType> $$2 = $$02.getPoiType();
        short $$3 = SectionPos.sectionRelativePos($$1);
        PoiRecord $$4 = (PoiRecord)this.records.get($$3);
        if ($$4 != null) {
            if ($$2.equals($$4.getPoiType())) {
                return false;
            }
            Util.logAndPauseIfInIde("POI data mismatch: already registered at " + $$1);
        }
        this.records.put($$3, (Object)$$02);
        ((Set)this.byType.computeIfAbsent($$2, $$0 -> Sets.newHashSet())).add((Object)$$02);
        return true;
    }

    public void remove(BlockPos $$0) {
        PoiRecord $$1 = (PoiRecord)this.records.remove(SectionPos.sectionRelativePos($$0));
        if ($$1 == null) {
            LOGGER.error("POI data mismatch: never registered at {}", (Object)$$0);
            return;
        }
        ((Set)this.byType.get($$1.getPoiType())).remove((Object)$$1);
        LOGGER.debug("Removed POI of type {} @ {}", LogUtils.defer($$1::getPoiType), LogUtils.defer($$1::getPos));
        this.setDirty.run();
    }

    @Deprecated
    @VisibleForDebug
    public int getFreeTickets(BlockPos $$0) {
        return (Integer)this.getPoiRecord($$0).map(PoiRecord::getFreeTickets).orElse((Object)0);
    }

    public boolean release(BlockPos $$0) {
        PoiRecord $$1 = (PoiRecord)this.records.get(SectionPos.sectionRelativePos($$0));
        if ($$1 == null) {
            throw Util.pauseInIde(new IllegalStateException("POI never registered at " + $$0));
        }
        boolean $$2 = $$1.releaseTicket();
        this.setDirty.run();
        return $$2;
    }

    public boolean exists(BlockPos $$0, Predicate<Holder<PoiType>> $$1) {
        return this.getType($$0).filter($$1).isPresent();
    }

    public Optional<Holder<PoiType>> getType(BlockPos $$0) {
        return this.getPoiRecord($$0).map(PoiRecord::getPoiType);
    }

    private Optional<PoiRecord> getPoiRecord(BlockPos $$0) {
        return Optional.ofNullable((Object)((PoiRecord)this.records.get(SectionPos.sectionRelativePos($$0))));
    }

    public void refresh(Consumer<BiConsumer<BlockPos, Holder<PoiType>>> $$0) {
        if (!this.isValid) {
            Short2ObjectOpenHashMap $$1 = new Short2ObjectOpenHashMap(this.records);
            this.clear();
            $$0.accept((arg_0, arg_1) -> this.lambda$refresh$9((Short2ObjectMap)$$1, arg_0, arg_1));
            this.isValid = true;
            this.setDirty.run();
        }
    }

    private void clear() {
        this.records.clear();
        this.byType.clear();
    }

    boolean isValid() {
        return this.isValid;
    }

    private /* synthetic */ void lambda$refresh$9(Short2ObjectMap $$0, BlockPos $$1, Holder $$22) {
        short $$3 = SectionPos.sectionRelativePos($$1);
        PoiRecord $$4 = (PoiRecord)$$0.computeIfAbsent($$3, $$2 -> new PoiRecord($$1, $$22, this.setDirty));
        this.add($$4);
    }
}