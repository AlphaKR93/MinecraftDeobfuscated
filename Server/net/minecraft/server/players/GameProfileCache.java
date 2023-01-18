/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.ProfileLookupCallback
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.BufferedWriter
 *  java.io.File
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.text.DateFormat
 *  java.text.ParseException
 *  java.text.SimpleDateFormat
 *  java.util.ArrayList
 *  java.util.Calendar
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.Date
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.atomic.AtomicLong
 *  java.util.concurrent.atomic.AtomicReference
 *  java.util.function.Consumer
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import org.slf4j.Logger;

public class GameProfileCache {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int GAMEPROFILES_MRU_LIMIT = 1000;
    private static final int GAMEPROFILES_EXPIRATION_MONTHS = 1;
    private static boolean usesAuthentication;
    private final Map<String, GameProfileInfo> profilesByName = Maps.newConcurrentMap();
    private final Map<UUID, GameProfileInfo> profilesByUUID = Maps.newConcurrentMap();
    private final Map<String, CompletableFuture<Optional<GameProfile>>> requests = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepository;
    private final Gson gson = new GsonBuilder().create();
    private final File file;
    private final AtomicLong operationCount = new AtomicLong();
    @Nullable
    private Executor executor;

    public GameProfileCache(GameProfileRepository $$0, File $$1) {
        this.profileRepository = $$0;
        this.file = $$1;
        Lists.reverse(this.load()).forEach(this::safeAdd);
    }

    private void safeAdd(GameProfileInfo $$0) {
        UUID $$3;
        GameProfile $$1 = $$0.getProfile();
        $$0.setLastAccess(this.getNextOperation());
        String $$2 = $$1.getName();
        if ($$2 != null) {
            this.profilesByName.put((Object)$$2.toLowerCase(Locale.ROOT), (Object)$$0);
        }
        if (($$3 = $$1.getId()) != null) {
            this.profilesByUUID.put((Object)$$3, (Object)$$0);
        }
    }

    private static Optional<GameProfile> lookupGameProfile(GameProfileRepository $$0, String $$1) {
        final AtomicReference $$2 = new AtomicReference();
        ProfileLookupCallback $$3 = new ProfileLookupCallback(){

            public void onProfileLookupSucceeded(GameProfile $$0) {
                $$2.set((Object)$$0);
            }

            public void onProfileLookupFailed(GameProfile $$0, Exception $$1) {
                $$2.set(null);
            }
        };
        $$0.findProfilesByNames(new String[]{$$1}, Agent.MINECRAFT, $$3);
        GameProfile $$4 = (GameProfile)$$2.get();
        if (!GameProfileCache.usesAuthentication() && $$4 == null) {
            UUID $$5 = UUIDUtil.getOrCreatePlayerUUID(new GameProfile(null, $$1));
            return Optional.of((Object)new GameProfile($$5, $$1));
        }
        return Optional.ofNullable((Object)$$4);
    }

    public static void setUsesAuthentication(boolean $$0) {
        usesAuthentication = $$0;
    }

    private static boolean usesAuthentication() {
        return usesAuthentication;
    }

    public void add(GameProfile $$0) {
        Calendar $$1 = Calendar.getInstance();
        $$1.setTime(new Date());
        $$1.add(2, 1);
        Date $$2 = $$1.getTime();
        GameProfileInfo $$3 = new GameProfileInfo($$0, $$2);
        this.safeAdd($$3);
        this.save();
    }

    private long getNextOperation() {
        return this.operationCount.incrementAndGet();
    }

    public Optional<GameProfile> get(String $$0) {
        Optional<GameProfile> $$5;
        String $$1 = $$0.toLowerCase(Locale.ROOT);
        GameProfileInfo $$2 = (GameProfileInfo)this.profilesByName.get((Object)$$1);
        boolean $$3 = false;
        if ($$2 != null && new Date().getTime() >= $$2.expirationDate.getTime()) {
            this.profilesByUUID.remove((Object)$$2.getProfile().getId());
            this.profilesByName.remove((Object)$$2.getProfile().getName().toLowerCase(Locale.ROOT));
            $$3 = true;
            $$2 = null;
        }
        if ($$2 != null) {
            $$2.setLastAccess(this.getNextOperation());
            Optional $$4 = Optional.of((Object)$$2.getProfile());
        } else {
            $$5 = GameProfileCache.lookupGameProfile(this.profileRepository, $$1);
            if ($$5.isPresent()) {
                this.add((GameProfile)$$5.get());
                $$3 = false;
            }
        }
        if ($$3) {
            this.save();
        }
        return $$5;
    }

    public void getAsync(String $$0, Consumer<Optional<GameProfile>> $$12) {
        if (this.executor == null) {
            throw new IllegalStateException("No executor");
        }
        CompletableFuture $$22 = (CompletableFuture)this.requests.get((Object)$$0);
        if ($$22 != null) {
            this.requests.put((Object)$$0, (Object)$$22.whenCompleteAsync(($$1, $$2) -> $$12.accept($$1), this.executor));
        } else {
            this.requests.put((Object)$$0, (Object)CompletableFuture.supplyAsync(() -> this.get($$0), (Executor)Util.backgroundExecutor()).whenCompleteAsync(($$1, $$2) -> this.requests.remove((Object)$$0), this.executor).whenCompleteAsync(($$1, $$2) -> $$12.accept($$1), this.executor));
        }
    }

    public Optional<GameProfile> get(UUID $$0) {
        GameProfileInfo $$1 = (GameProfileInfo)this.profilesByUUID.get((Object)$$0);
        if ($$1 == null) {
            return Optional.empty();
        }
        $$1.setLastAccess(this.getNextOperation());
        return Optional.of((Object)$$1.getProfile());
    }

    public void setExecutor(Executor $$0) {
        this.executor = $$0;
    }

    public void clearExecutor() {
        this.executor = null;
    }

    private static DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<GameProfileInfo> load() {
        ArrayList $$0 = Lists.newArrayList();
        try (BufferedReader $$12222 = Files.newReader((File)this.file, (Charset)StandardCharsets.UTF_8);){
            JsonArray $$2 = (JsonArray)this.gson.fromJson((Reader)$$12222, JsonArray.class);
            if ($$2 == null) {
                ArrayList arrayList = $$0;
                return arrayList;
            }
            DateFormat $$3 = GameProfileCache.createDateFormat();
            $$2.forEach(arg_0 -> GameProfileCache.lambda$load$4($$3, (List)$$0, arg_0));
            return $$0;
        }
        catch (FileNotFoundException $$12222) {
            return $$0;
        }
        catch (JsonParseException | IOException $$4) {
            LOGGER.warn("Failed to load profile cache {}", (Object)this.file, (Object)$$4);
        }
        return $$0;
    }

    public void save() {
        JsonArray $$0 = new JsonArray();
        DateFormat $$1 = GameProfileCache.createDateFormat();
        this.getTopMRUProfiles(1000).forEach($$2 -> $$0.add(GameProfileCache.writeGameProfile($$2, $$1)));
        String $$22 = this.gson.toJson((JsonElement)$$0);
        try (BufferedWriter $$3 = Files.newWriter((File)this.file, (Charset)StandardCharsets.UTF_8);){
            $$3.write($$22);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private Stream<GameProfileInfo> getTopMRUProfiles(int $$0) {
        return ImmutableList.copyOf((Collection)this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileInfo::getLastAccess).reversed()).limit((long)$$0);
    }

    private static JsonElement writeGameProfile(GameProfileInfo $$0, DateFormat $$1) {
        JsonObject $$2 = new JsonObject();
        $$2.addProperty("name", $$0.getProfile().getName());
        UUID $$3 = $$0.getProfile().getId();
        $$2.addProperty("uuid", $$3 == null ? "" : $$3.toString());
        $$2.addProperty("expiresOn", $$1.format($$0.getExpirationDate()));
        return $$2;
    }

    /*
     * WARNING - void declaration
     */
    private static Optional<GameProfileInfo> readGameProfile(JsonElement $$0, DateFormat $$1) {
        if ($$0.isJsonObject()) {
            void $$11;
            JsonObject $$2 = $$0.getAsJsonObject();
            JsonElement $$3 = $$2.get("name");
            JsonElement $$4 = $$2.get("uuid");
            JsonElement $$5 = $$2.get("expiresOn");
            if ($$3 == null || $$4 == null) {
                return Optional.empty();
            }
            String $$6 = $$4.getAsString();
            String $$7 = $$3.getAsString();
            Date $$8 = null;
            if ($$5 != null) {
                try {
                    $$8 = $$1.parse($$5.getAsString());
                }
                catch (ParseException parseException) {
                    // empty catch block
                }
            }
            if ($$7 == null || $$6 == null || $$8 == null) {
                return Optional.empty();
            }
            try {
                UUID $$9 = UUID.fromString((String)$$6);
            }
            catch (Throwable $$10) {
                return Optional.empty();
            }
            return Optional.of((Object)new GameProfileInfo(new GameProfile((UUID)$$11, $$7), $$8));
        }
        return Optional.empty();
    }

    private static /* synthetic */ void lambda$load$4(DateFormat $$0, List $$1, JsonElement $$2) {
        GameProfileCache.readGameProfile($$2, $$0).ifPresent(arg_0 -> ((List)$$1).add(arg_0));
    }

    static class GameProfileInfo {
        private final GameProfile profile;
        final Date expirationDate;
        private volatile long lastAccess;

        GameProfileInfo(GameProfile $$0, Date $$1) {
            this.profile = $$0;
            this.expirationDate = $$1;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void setLastAccess(long $$0) {
            this.lastAccess = $$0;
        }

        public long getLastAccess() {
            return this.lastAccess;
        }
    }
}