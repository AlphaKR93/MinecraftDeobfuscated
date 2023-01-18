/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.JsonOps
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Properties
 *  java.util.concurrent.TimeUnit
 *  java.util.function.UnaryOperator
 *  java.util.regex.Pattern
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.slf4j.Logger;

public class DedicatedServerProperties
extends Settings<DedicatedServerProperties> {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern SHA1 = Pattern.compile((String)"^[a-fA-F0-9]{40}$");
    private static final Splitter COMMA_SPLITTER = Splitter.on((char)',').trimResults();
    public final boolean onlineMode = this.get("online-mode", true);
    public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
    public final String serverIp = this.get("server-ip", "");
    public final boolean spawnAnimals = this.get("spawn-animals", true);
    public final boolean spawnNpcs = this.get("spawn-npcs", true);
    public final boolean pvp = this.get("pvp", true);
    public final boolean allowFlight = this.get("allow-flight", false);
    public final String motd = this.get("motd", "A Minecraft Server");
    public final boolean forceGameMode = this.get("force-gamemode", false);
    public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
    public final Difficulty difficulty = this.get("difficulty", DedicatedServerProperties.dispatchNumberOrString(Difficulty::byId, Difficulty::byName), Difficulty::getKey, Difficulty.EASY);
    public final GameType gamemode = this.get("gamemode", DedicatedServerProperties.dispatchNumberOrString(GameType::byId, GameType::byName), GameType::getName, GameType.SURVIVAL);
    public final String levelName = this.get("level-name", "world");
    public final int serverPort = this.get("server-port", 25565);
    @Nullable
    public final Boolean announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
    public final boolean enableQuery = this.get("enable-query", false);
    public final int queryPort = this.get("query.port", 25565);
    public final boolean enableRcon = this.get("enable-rcon", false);
    public final int rconPort = this.get("rcon.port", 25575);
    public final String rconPassword = this.get("rcon.password", "");
    public final boolean hardcore = this.get("hardcore", false);
    public final boolean allowNether = this.get("allow-nether", true);
    public final boolean spawnMonsters = this.get("spawn-monsters", true);
    public final boolean useNativeTransport = this.get("use-native-transport", true);
    public final boolean enableCommandBlock = this.get("enable-command-block", false);
    public final int spawnProtection = this.get("spawn-protection", 16);
    public final int opPermissionLevel = this.get("op-permission-level", 4);
    public final int functionPermissionLevel = this.get("function-permission-level", 2);
    public final long maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
    public final int maxChainedNeighborUpdates = this.get("max-chained-neighbor-updates", 1000000);
    public final int rateLimitPacketsPerSecond = this.get("rate-limit", 0);
    public final int viewDistance = this.get("view-distance", 10);
    public final int simulationDistance = this.get("simulation-distance", 10);
    public final int maxPlayers = this.get("max-players", 20);
    public final int networkCompressionThreshold = this.get("network-compression-threshold", 256);
    public final boolean broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
    public final boolean broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
    public final int maxWorldSize = this.get("max-world-size", (UnaryOperator<Integer>)((UnaryOperator)$$0 -> Mth.clamp($$0, 1, 29999984)), 29999984);
    public final boolean syncChunkWrites = this.get("sync-chunk-writes", true);
    public final boolean enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
    public final boolean enableStatus = this.get("enable-status", true);
    public final boolean hideOnlinePlayers = this.get("hide-online-players", false);
    public final int entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", (UnaryOperator<Integer>)((UnaryOperator)$$0 -> Mth.clamp($$0, 10, 1000)), 100);
    public final String textFilteringConfig = this.get("text-filtering-config", "");
    public final Optional<MinecraftServer.ServerResourcePackInfo> serverResourcePackInfo;
    public final DataPackConfig initialDataPackConfiguration;
    public final Settings.MutableValue<Integer> playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
    public final Settings.MutableValue<Boolean> whiteList = this.getMutable("white-list", false);
    public final boolean enforceSecureProfile = this.get("enforce-secure-profile", true);
    private final WorldDimensionData worldDimensionData;
    public final WorldOptions worldOptions;

    public DedicatedServerProperties(Properties $$02) {
        super($$02);
        String $$1 = this.get("level-seed", "");
        boolean $$2 = this.get("generate-structures", true);
        long $$3 = WorldOptions.parseSeed($$1).orElse(WorldOptions.randomSeed());
        this.worldOptions = new WorldOptions($$3, $$2, false);
        this.worldDimensionData = new WorldDimensionData(this.get("generator-settings", $$0 -> GsonHelper.parse(!$$0.isEmpty() ? $$0 : "{}"), new JsonObject()), this.get("level-type", $$0 -> $$0.toLowerCase(Locale.ROOT), WorldPresets.NORMAL.location().toString()));
        this.serverResourcePackInfo = DedicatedServerProperties.getServerPackInfo(this.get("resource-pack", ""), this.get("resource-pack-sha1", ""), this.getLegacyString("resource-pack-hash"), this.get("require-resource-pack", false), this.get("resource-pack-prompt", ""));
        this.initialDataPackConfiguration = DedicatedServerProperties.getDatapackConfig(this.get("initial-enabled-packs", String.join((CharSequence)",", WorldDataConfiguration.DEFAULT.dataPacks().getEnabled())), this.get("initial-disabled-packs", String.join((CharSequence)",", WorldDataConfiguration.DEFAULT.dataPacks().getDisabled())));
    }

    public static DedicatedServerProperties fromFile(Path $$0) {
        return new DedicatedServerProperties(DedicatedServerProperties.loadFromFile($$0));
    }

    @Override
    protected DedicatedServerProperties reload(RegistryAccess $$0, Properties $$1) {
        return new DedicatedServerProperties($$1);
    }

    @Nullable
    private static Component parseResourcePackPrompt(String $$0) {
        if (!Strings.isNullOrEmpty((String)$$0)) {
            try {
                return Component.Serializer.fromJson($$0);
            }
            catch (Exception $$1) {
                LOGGER.warn("Failed to parse resource pack prompt '{}'", (Object)$$0, (Object)$$1);
            }
        }
        return null;
    }

    private static Optional<MinecraftServer.ServerResourcePackInfo> getServerPackInfo(String $$0, String $$1, @Nullable String $$2, boolean $$3, String $$4) {
        String $$7;
        if ($$0.isEmpty()) {
            return Optional.empty();
        }
        if (!$$1.isEmpty()) {
            String $$5 = $$1;
            if (!Strings.isNullOrEmpty((String)$$2)) {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        } else if (!Strings.isNullOrEmpty((String)$$2)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            String $$6 = $$2;
        } else {
            $$7 = "";
        }
        if ($$7.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        } else if (!SHA1.matcher((CharSequence)$$7).matches()) {
            LOGGER.warn("Invalid sha1 for resource-pack-sha1");
        }
        Component $$8 = DedicatedServerProperties.parseResourcePackPrompt($$4);
        return Optional.of((Object)((Object)new MinecraftServer.ServerResourcePackInfo($$0, $$7, $$3, $$8)));
    }

    private static DataPackConfig getDatapackConfig(String $$0, String $$1) {
        List $$2 = COMMA_SPLITTER.splitToList((CharSequence)$$0);
        List $$3 = COMMA_SPLITTER.splitToList((CharSequence)$$1);
        return new DataPackConfig((List<String>)$$2, (List<String>)$$3);
    }

    private static FeatureFlagSet getFeatures(String $$02) {
        return FeatureFlags.REGISTRY.fromNames((Iterable<ResourceLocation>)((Iterable)COMMA_SPLITTER.splitToStream((CharSequence)$$02).mapMulti(($$0, $$1) -> {
            ResourceLocation $$2 = ResourceLocation.tryParse($$0);
            if ($$2 == null) {
                LOGGER.warn("Invalid resource location {}, ignoring", $$0);
            } else {
                $$1.accept((Object)$$2);
            }
        }).collect(Collectors.toList())));
    }

    public WorldDimensions createDimensions(RegistryAccess $$0) {
        return this.worldDimensionData.create($$0);
    }

    record WorldDimensionData(JsonObject generatorSettings, String levelType) {
        private static final Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES = Map.of((Object)"default", WorldPresets.NORMAL, (Object)"largebiomes", WorldPresets.LARGE_BIOMES);

        public WorldDimensions create(RegistryAccess $$02) {
            Registry<WorldPreset> $$1 = $$02.registryOrThrow(Registries.WORLD_PRESET);
            Holder.Reference $$2 = (Holder.Reference)$$1.getHolder(WorldPresets.NORMAL).or(() -> $$1.holders().findAny()).orElseThrow(() -> new IllegalStateException("Invalid datapack contents: can't find default preset"));
            Holder $$3 = (Holder)Optional.ofNullable((Object)ResourceLocation.tryParse(this.levelType)).map($$0 -> ResourceKey.create(Registries.WORLD_PRESET, $$0)).or(() -> Optional.ofNullable((Object)((ResourceKey)LEGACY_PRESET_NAMES.get((Object)this.levelType)))).flatMap($$1::getHolder).orElseGet(() -> {
                LOGGER.warn("Failed to parse level-type {}, defaulting to {}", (Object)this.levelType, (Object)$$2.key().location());
                return $$2;
            });
            WorldDimensions $$4 = ((WorldPreset)$$3.value()).createWorldDimensions();
            if ($$3.is(WorldPresets.FLAT)) {
                RegistryOps $$5 = RegistryOps.create(JsonOps.INSTANCE, $$02);
                Optional $$6 = FlatLevelGeneratorSettings.CODEC.parse(new Dynamic($$5, (Object)this.generatorSettings())).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0));
                if ($$6.isPresent()) {
                    return $$4.replaceOverworldGenerator($$02, new FlatLevelSource((FlatLevelGeneratorSettings)$$6.get()));
                }
            }
            return $$4;
        }
    }
}