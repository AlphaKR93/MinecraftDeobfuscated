/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.internal.Streams
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.io.File
 *  java.io.IOException
 *  java.io.Reader
 *  java.io.StringReader
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatsCounter
extends StatsCounter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftServer server;
    private final File file;
    private final Set<Stat<?>> dirty = Sets.newHashSet();

    public ServerStatsCounter(MinecraftServer $$0, File $$1) {
        this.server = $$0;
        this.file = $$1;
        if ($$1.isFile()) {
            try {
                this.parseLocal($$0.getFixerUpper(), FileUtils.readFileToString((File)$$1));
            }
            catch (IOException $$2) {
                LOGGER.error("Couldn't read statistics file {}", (Object)$$1, (Object)$$2);
            }
            catch (JsonParseException $$3) {
                LOGGER.error("Couldn't parse statistics file {}", (Object)$$1, (Object)$$3);
            }
        }
    }

    public void save() {
        try {
            FileUtils.writeStringToFile((File)this.file, (String)this.toJson());
        }
        catch (IOException $$0) {
            LOGGER.error("Couldn't save stats", (Throwable)$$0);
        }
    }

    @Override
    public void setValue(Player $$0, Stat<?> $$1, int $$2) {
        super.setValue($$0, $$1, $$2);
        this.dirty.add($$1);
    }

    private Set<Stat<?>> getDirty() {
        HashSet $$0 = Sets.newHashSet(this.dirty);
        this.dirty.clear();
        return $$0;
    }

    public void parseLocal(DataFixer $$0, String $$1) {
        try (JsonReader $$2 = new JsonReader((Reader)new StringReader($$1));){
            $$2.setLenient(false);
            JsonElement $$3 = Streams.parse((JsonReader)$$2);
            if ($$3.isJsonNull()) {
                LOGGER.error("Unable to parse Stat data from {}", (Object)this.file);
                return;
            }
            CompoundTag $$4 = ServerStatsCounter.fromJson($$3.getAsJsonObject());
            if (($$4 = DataFixTypes.STATS.updateToCurrentVersion($$0, $$4, NbtUtils.getDataVersion($$4, 1343))).contains("stats", 10)) {
                CompoundTag $$5 = $$4.getCompound("stats");
                for (String $$6 : $$5.getAllKeys()) {
                    if (!$$5.contains($$6, 10)) continue;
                    Util.ifElse(BuiltInRegistries.STAT_TYPE.getOptional(new ResourceLocation($$6)), $$22 -> {
                        CompoundTag $$3 = $$5.getCompound($$6);
                        for (String $$4 : $$3.getAllKeys()) {
                            if ($$3.contains($$4, 99)) {
                                Util.ifElse(this.getStat((StatType)$$22, $$4), $$2 -> this.stats.put($$2, $$3.getInt($$4)), () -> LOGGER.warn("Invalid statistic in {}: Don't know what {} is", (Object)this.file, (Object)$$4));
                                continue;
                            }
                            LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", new Object[]{this.file, $$3.get($$4), $$4});
                        }
                    }, () -> LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", (Object)this.file, (Object)$$6));
                }
            }
        }
        catch (JsonParseException | IOException $$7) {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.file, (Object)$$7);
        }
    }

    private <T> Optional<Stat<T>> getStat(StatType<T> $$0, String $$1) {
        return Optional.ofNullable((Object)ResourceLocation.tryParse($$1)).flatMap($$0.getRegistry()::getOptional).map($$0::get);
    }

    private static CompoundTag fromJson(JsonObject $$0) {
        CompoundTag $$1 = new CompoundTag();
        for (Map.Entry $$2 : $$0.entrySet()) {
            JsonPrimitive $$4;
            JsonElement $$3 = (JsonElement)$$2.getValue();
            if ($$3.isJsonObject()) {
                $$1.put((String)$$2.getKey(), ServerStatsCounter.fromJson($$3.getAsJsonObject()));
                continue;
            }
            if (!$$3.isJsonPrimitive() || !($$4 = $$3.getAsJsonPrimitive()).isNumber()) continue;
            $$1.putInt((String)$$2.getKey(), $$4.getAsInt());
        }
        return $$1;
    }

    protected String toJson() {
        HashMap $$02 = Maps.newHashMap();
        for (Object2IntMap.Entry $$1 : this.stats.object2IntEntrySet()) {
            Stat $$2 = (Stat)$$1.getKey();
            ((JsonObject)$$02.computeIfAbsent($$2.getType(), $$0 -> new JsonObject())).addProperty(ServerStatsCounter.getKey($$2).toString(), (Number)Integer.valueOf((int)$$1.getIntValue()));
        }
        JsonObject $$3 = new JsonObject();
        for (Map.Entry $$4 : $$02.entrySet()) {
            $$3.add(BuiltInRegistries.STAT_TYPE.getKey((StatType)$$4.getKey()).toString(), (JsonElement)$$4.getValue());
        }
        JsonObject $$5 = new JsonObject();
        $$5.add("stats", (JsonElement)$$3);
        $$5.addProperty("DataVersion", (Number)Integer.valueOf((int)SharedConstants.getCurrentVersion().getDataVersion().getVersion()));
        return $$5.toString();
    }

    private static <T> ResourceLocation getKey(Stat<T> $$0) {
        return $$0.getType().getRegistry().getKey($$0.getValue());
    }

    public void markAllDirty() {
        this.dirty.addAll((Collection)this.stats.keySet());
    }

    public void sendStats(ServerPlayer $$0) {
        Object2IntOpenHashMap $$1 = new Object2IntOpenHashMap();
        for (Stat $$2 : this.getDirty()) {
            $$1.put((Object)$$2, this.getValue($$2));
        }
        $$0.connection.send(new ClientboundAwardStatsPacket((Object2IntMap<Stat<?>>)$$1));
    }
}