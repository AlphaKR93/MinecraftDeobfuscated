/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  java.lang.Deprecated
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.slf4j.Logger;

public class PrimaryLevelData
implements ServerLevelData,
WorldData {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static final String PLAYER = "Player";
    protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
    private LevelSettings settings;
    private final WorldOptions worldOptions;
    private final SpecialWorldProperty specialWorldProperty;
    private final Lifecycle worldGenSettingsLifecycle;
    private int xSpawn;
    private int ySpawn;
    private int zSpawn;
    private float spawnAngle;
    private long gameTime;
    private long dayTime;
    @Nullable
    private final DataFixer fixerUpper;
    private final int playerDataVersion;
    private boolean upgradedPlayerTag;
    @Nullable
    private CompoundTag loadedPlayerTag;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.Settings worldBorder;
    private CompoundTag endDragonFightData;
    @Nullable
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderId;
    private final Set<String> knownServerBrands;
    private boolean wasModded;
    private final TimerQueue<MinecraftServer> scheduledEvents;

    private PrimaryLevelData(@Nullable DataFixer $$0, int $$1, @Nullable CompoundTag $$2, boolean $$3, int $$4, int $$5, int $$6, float $$7, long $$8, long $$9, int $$10, int $$11, int $$12, boolean $$13, int $$14, boolean $$15, boolean $$16, boolean $$17, WorldBorder.Settings $$18, int $$19, int $$20, @Nullable UUID $$21, Set<String> $$22, TimerQueue<MinecraftServer> $$23, @Nullable CompoundTag $$24, CompoundTag $$25, LevelSettings $$26, WorldOptions $$27, SpecialWorldProperty $$28, Lifecycle $$29) {
        this.fixerUpper = $$0;
        this.wasModded = $$3;
        this.xSpawn = $$4;
        this.ySpawn = $$5;
        this.zSpawn = $$6;
        this.spawnAngle = $$7;
        this.gameTime = $$8;
        this.dayTime = $$9;
        this.version = $$10;
        this.clearWeatherTime = $$11;
        this.rainTime = $$12;
        this.raining = $$13;
        this.thunderTime = $$14;
        this.thundering = $$15;
        this.initialized = $$16;
        this.difficultyLocked = $$17;
        this.worldBorder = $$18;
        this.wanderingTraderSpawnDelay = $$19;
        this.wanderingTraderSpawnChance = $$20;
        this.wanderingTraderId = $$21;
        this.knownServerBrands = $$22;
        this.loadedPlayerTag = $$2;
        this.playerDataVersion = $$1;
        this.scheduledEvents = $$23;
        this.customBossEvents = $$24;
        this.endDragonFightData = $$25;
        this.settings = $$26;
        this.worldOptions = $$27;
        this.specialWorldProperty = $$28;
        this.worldGenSettingsLifecycle = $$29;
    }

    public PrimaryLevelData(LevelSettings $$0, WorldOptions $$1, SpecialWorldProperty $$2, Lifecycle $$3) {
        this(null, SharedConstants.getCurrentVersion().getWorldVersion(), null, false, 0, 0, 0, 0.0f, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, null, (Set<String>)Sets.newLinkedHashSet(), new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS), null, new CompoundTag(), $$0.copy(), $$1, $$2, $$3);
    }

    public static PrimaryLevelData parse(Dynamic<Tag> $$02, DataFixer $$1, int $$2, @Nullable CompoundTag $$3, LevelSettings $$4, LevelVersion $$5, SpecialWorldProperty $$6, WorldOptions $$7, Lifecycle $$8) {
        long $$9 = $$02.get("Time").asLong(0L);
        CompoundTag $$10 = (CompoundTag)$$02.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> (Tag)$$02.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
        return new PrimaryLevelData($$1, $$2, $$3, $$02.get("WasModded").asBoolean(false), $$02.get("SpawnX").asInt(0), $$02.get("SpawnY").asInt(0), $$02.get("SpawnZ").asInt(0), $$02.get("SpawnAngle").asFloat(0.0f), $$9, $$02.get("DayTime").asLong($$9), $$5.levelDataVersion(), $$02.get("clearWeatherTime").asInt(0), $$02.get("rainTime").asInt(0), $$02.get("raining").asBoolean(false), $$02.get("thunderTime").asInt(0), $$02.get("thundering").asBoolean(false), $$02.get("initialized").asBoolean(true), $$02.get("DifficultyLocked").asBoolean(false), WorldBorder.Settings.read($$02, WorldBorder.DEFAULT_SETTINGS), $$02.get("WanderingTraderSpawnDelay").asInt(0), $$02.get("WanderingTraderSpawnChance").asInt(0), (UUID)$$02.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse(null), (Set<String>)((Set)$$02.get("ServerBrands").asStream().flatMap($$0 -> $$0.asString().result().stream()).collect(Collectors.toCollection(Sets::newLinkedHashSet))), new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS, (Stream<Dynamic<Tag>>)$$02.get("ScheduledEvents").asStream()), (CompoundTag)$$02.get("CustomBossEvents").orElseEmptyMap().getValue(), $$10, $$4, $$7, $$6, $$8);
    }

    @Override
    public CompoundTag createTag(RegistryAccess $$0, @Nullable CompoundTag $$1) {
        this.updatePlayerTag();
        if ($$1 == null) {
            $$1 = this.loadedPlayerTag;
        }
        CompoundTag $$2 = new CompoundTag();
        this.setTagData($$0, $$2, $$1);
        return $$2;
    }

    private void setTagData(RegistryAccess $$02, CompoundTag $$12, @Nullable CompoundTag $$2) {
        ListTag $$3 = new ListTag();
        this.knownServerBrands.stream().map(StringTag::valueOf).forEach(arg_0 -> ((ListTag)$$3).add(arg_0));
        $$12.put("ServerBrands", $$3);
        $$12.putBoolean("WasModded", this.wasModded);
        CompoundTag $$4 = new CompoundTag();
        $$4.putString("Name", SharedConstants.getCurrentVersion().getName());
        $$4.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        $$4.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
        $$4.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
        $$12.put("Version", $$4);
        $$12.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        RegistryOps<Tag> $$5 = RegistryOps.create(NbtOps.INSTANCE, $$02);
        WorldGenSettings.encode($$5, this.worldOptions, $$02).resultOrPartial(Util.prefix("WorldGenSettings: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0)))).ifPresent($$1 -> $$12.put(WORLD_GEN_SETTINGS, (Tag)$$1));
        $$12.putInt("GameType", this.settings.gameType().getId());
        $$12.putInt("SpawnX", this.xSpawn);
        $$12.putInt("SpawnY", this.ySpawn);
        $$12.putInt("SpawnZ", this.zSpawn);
        $$12.putFloat("SpawnAngle", this.spawnAngle);
        $$12.putLong("Time", this.gameTime);
        $$12.putLong("DayTime", this.dayTime);
        $$12.putLong("LastPlayed", Util.getEpochMillis());
        $$12.putString("LevelName", this.settings.levelName());
        $$12.putInt("version", 19133);
        $$12.putInt("clearWeatherTime", this.clearWeatherTime);
        $$12.putInt("rainTime", this.rainTime);
        $$12.putBoolean("raining", this.raining);
        $$12.putInt("thunderTime", this.thunderTime);
        $$12.putBoolean("thundering", this.thundering);
        $$12.putBoolean("hardcore", this.settings.hardcore());
        $$12.putBoolean("allowCommands", this.settings.allowCommands());
        $$12.putBoolean("initialized", this.initialized);
        this.worldBorder.write($$12);
        $$12.putByte("Difficulty", (byte)this.settings.difficulty().getId());
        $$12.putBoolean("DifficultyLocked", this.difficultyLocked);
        $$12.put("GameRules", this.settings.gameRules().createTag());
        $$12.put("DragonFight", this.endDragonFightData);
        if ($$2 != null) {
            $$12.put(PLAYER, $$2);
        }
        DataResult $$6 = WorldDataConfiguration.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.settings.getDataConfiguration());
        $$6.get().ifLeft($$1 -> $$12.merge((CompoundTag)$$1)).ifRight($$0 -> LOGGER.warn("Failed to encode configuration {}", (Object)$$0.message()));
        if (this.customBossEvents != null) {
            $$12.put("CustomBossEvents", this.customBossEvents);
        }
        $$12.put("ScheduledEvents", this.scheduledEvents.store());
        $$12.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        $$12.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            $$12.putUUID("WanderingTraderId", this.wanderingTraderId);
        }
    }

    @Override
    public int getXSpawn() {
        return this.xSpawn;
    }

    @Override
    public int getYSpawn() {
        return this.ySpawn;
    }

    @Override
    public int getZSpawn() {
        return this.zSpawn;
    }

    @Override
    public float getSpawnAngle() {
        return this.spawnAngle;
    }

    @Override
    public long getGameTime() {
        return this.gameTime;
    }

    @Override
    public long getDayTime() {
        return this.dayTime;
    }

    private void updatePlayerTag() {
        if (this.upgradedPlayerTag || this.loadedPlayerTag == null) {
            return;
        }
        if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if (this.fixerUpper == null) {
                throw Util.pauseInIde(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }
            this.loadedPlayerTag = NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
        }
        this.upgradedPlayerTag = true;
    }

    @Override
    public CompoundTag getLoadedPlayerTag() {
        this.updatePlayerTag();
        return this.loadedPlayerTag;
    }

    @Override
    public void setXSpawn(int $$0) {
        this.xSpawn = $$0;
    }

    @Override
    public void setYSpawn(int $$0) {
        this.ySpawn = $$0;
    }

    @Override
    public void setZSpawn(int $$0) {
        this.zSpawn = $$0;
    }

    @Override
    public void setSpawnAngle(float $$0) {
        this.spawnAngle = $$0;
    }

    @Override
    public void setGameTime(long $$0) {
        this.gameTime = $$0;
    }

    @Override
    public void setDayTime(long $$0) {
        this.dayTime = $$0;
    }

    @Override
    public void setSpawn(BlockPos $$0, float $$1) {
        this.xSpawn = $$0.getX();
        this.ySpawn = $$0.getY();
        this.zSpawn = $$0.getZ();
        this.spawnAngle = $$1;
    }

    @Override
    public String getLevelName() {
        return this.settings.levelName();
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    @Override
    public void setClearWeatherTime(int $$0) {
        this.clearWeatherTime = $$0;
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean $$0) {
        this.thundering = $$0;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int $$0) {
        this.thunderTime = $$0;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean $$0) {
        this.raining = $$0;
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int $$0) {
        this.rainTime = $$0;
    }

    @Override
    public GameType getGameType() {
        return this.settings.gameType();
    }

    @Override
    public void setGameType(GameType $$0) {
        this.settings = this.settings.withGameType($$0);
    }

    @Override
    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    @Override
    public boolean getAllowCommands() {
        return this.settings.allowCommands();
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean $$0) {
        this.initialized = $$0;
    }

    @Override
    public GameRules getGameRules() {
        return this.settings.gameRules();
    }

    @Override
    public WorldBorder.Settings getWorldBorder() {
        return this.worldBorder;
    }

    @Override
    public void setWorldBorder(WorldBorder.Settings $$0) {
        this.worldBorder = $$0;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.settings.difficulty();
    }

    @Override
    public void setDifficulty(Difficulty $$0) {
        this.settings = this.settings.withDifficulty($$0);
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean $$0) {
        this.difficultyLocked = $$0;
    }

    @Override
    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory $$0, LevelHeightAccessor $$1) {
        ServerLevelData.super.fillCrashReportCategory($$0, $$1);
        WorldData.super.fillCrashReportCategory($$0);
    }

    @Override
    public WorldOptions worldGenOptions() {
        return this.worldOptions;
    }

    @Override
    public boolean isFlatWorld() {
        return this.specialWorldProperty == SpecialWorldProperty.FLAT;
    }

    @Override
    public boolean isDebugWorld() {
        return this.specialWorldProperty == SpecialWorldProperty.DEBUG;
    }

    @Override
    public Lifecycle worldGenSettingsLifecycle() {
        return this.worldGenSettingsLifecycle;
    }

    @Override
    public CompoundTag endDragonFightData() {
        return this.endDragonFightData;
    }

    @Override
    public void setEndDragonFightData(CompoundTag $$0) {
        this.endDragonFightData = $$0;
    }

    @Override
    public WorldDataConfiguration getDataConfiguration() {
        return this.settings.getDataConfiguration();
    }

    @Override
    public void setDataConfiguration(WorldDataConfiguration $$0) {
        this.settings = this.settings.withDataConfiguration($$0);
    }

    @Override
    @Nullable
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }

    @Override
    public void setCustomBossEvents(@Nullable CompoundTag $$0) {
        this.customBossEvents = $$0;
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int $$0) {
        this.wanderingTraderSpawnDelay = $$0;
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    @Override
    public void setWanderingTraderSpawnChance(int $$0) {
        this.wanderingTraderSpawnChance = $$0;
    }

    @Override
    @Nullable
    public UUID getWanderingTraderId() {
        return this.wanderingTraderId;
    }

    @Override
    public void setWanderingTraderId(UUID $$0) {
        this.wanderingTraderId = $$0;
    }

    @Override
    public void setModdedInfo(String $$0, boolean $$1) {
        this.knownServerBrands.add((Object)$$0);
        this.wasModded |= $$1;
    }

    @Override
    public boolean wasModded() {
        return this.wasModded;
    }

    @Override
    public Set<String> getKnownServerBrands() {
        return ImmutableSet.copyOf(this.knownServerBrands);
    }

    @Override
    public ServerLevelData overworldData() {
        return this;
    }

    @Override
    public LevelSettings getLevelSettings() {
        return this.settings.copy();
    }

    @Deprecated
    public static enum SpecialWorldProperty {
        NONE,
        FLAT,
        DEBUG;

    }
}