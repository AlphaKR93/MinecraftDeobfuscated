/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  io.netty.util.ResourceLeakDetector
 *  io.netty.util.ResourceLeakDetector$Level
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.time.Duration
 *  javax.annotation.Nullable
 */
package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.DetectedVersion;
import net.minecraft.WorldVersion;
import net.minecraft.commands.BrigadierExceptions;
import net.minecraft.util.datafix.DataFixerOptimizationOption;
import net.minecraft.world.level.ChunkPos;

public class SharedConstants {
    @Deprecated
    public static final boolean SNAPSHOT = false;
    @Deprecated
    public static final int WORLD_VERSION = 3218;
    @Deprecated
    public static final String SERIES = "main";
    @Deprecated
    public static final String VERSION_STRING = "1.19.3";
    @Deprecated
    public static final int RELEASE_NETWORK_PROTOCOL_VERSION = 761;
    @Deprecated
    public static final int SNAPSHOT_NETWORK_PROTOCOL_VERSION = 114;
    public static final int SNBT_NAG_VERSION = 3200;
    private static final int SNAPSHOT_PROTOCOL_BIT = 30;
    public static final boolean THROW_ON_TASK_FAILURE = false;
    @Deprecated
    public static final int RESOURCE_PACK_FORMAT = 12;
    @Deprecated
    public static final int DATA_PACK_FORMAT = 10;
    @Deprecated
    public static final int LANGUAGE_FORMAT = 1;
    public static final int REPORT_FORMAT_VERSION = 1;
    public static final String DATA_VERSION_TAG = "DataVersion";
    public static final boolean USE_NEW_RENDERSYSTEM = false;
    public static final boolean MULTITHREADED_RENDERING = false;
    public static final boolean FIX_TNT_DUPE = false;
    public static final boolean FIX_SAND_DUPE = false;
    public static final boolean USE_DEBUG_FEATURES = false;
    public static final boolean DEBUG_OPEN_INCOMPATIBLE_WORLDS = false;
    public static final boolean DEBUG_ALLOW_LOW_SIM_DISTANCE = false;
    public static final boolean DEBUG_HOTKEYS = false;
    public static final boolean DEBUG_UI_NARRATION = false;
    public static final boolean DEBUG_RENDER = false;
    public static final boolean DEBUG_PATHFINDING = false;
    public static final boolean DEBUG_WATER = false;
    public static final boolean DEBUG_HEIGHTMAP = false;
    public static final boolean DEBUG_COLLISION = false;
    public static final boolean DEBUG_SHAPES = false;
    public static final boolean DEBUG_NEIGHBORSUPDATE = false;
    public static final boolean DEBUG_STRUCTURES = false;
    public static final boolean DEBUG_LIGHT = false;
    public static final boolean DEBUG_WORLDGENATTEMPT = false;
    public static final boolean DEBUG_SOLID_FACE = false;
    public static final boolean DEBUG_CHUNKS = false;
    public static final boolean DEBUG_GAME_EVENT_LISTENERS = false;
    public static final boolean DEBUG_DUMP_TEXTURE_ATLAS = false;
    public static final boolean DEBUG_DUMP_INTERPOLATED_TEXTURE_FRAMES = false;
    public static final boolean DEBUG_STRUCTURE_EDIT_MODE = false;
    public static final boolean DEBUG_SAVE_STRUCTURES_AS_SNBT = false;
    public static final boolean DEBUG_SYNCHRONOUS_GL_LOGS = false;
    public static final boolean DEBUG_VERBOSE_SERVER_EVENTS = false;
    public static final boolean DEBUG_NAMED_RUNNABLES = false;
    public static final boolean DEBUG_GOAL_SELECTOR = false;
    public static final boolean DEBUG_VILLAGE_SECTIONS = false;
    public static final boolean DEBUG_BRAIN = false;
    public static final boolean DEBUG_BEES = false;
    public static final boolean DEBUG_RAIDS = false;
    public static final boolean DEBUG_BLOCK_BREAK = false;
    public static final boolean DEBUG_RESOURCE_LOAD_TIMES = false;
    public static final boolean DEBUG_MONITOR_TICK_TIMES = false;
    public static final boolean DEBUG_KEEP_JIGSAW_BLOCKS_DURING_STRUCTURE_GEN = false;
    public static final boolean DEBUG_DONT_SAVE_WORLD = false;
    public static final boolean DEBUG_LARGE_DRIPSTONE = false;
    public static final boolean DEBUG_PACKET_SERIALIZATION = false;
    public static final boolean DEBUG_CARVERS = false;
    public static final boolean DEBUG_ORE_VEINS = false;
    public static final boolean DEBUG_SCULK_CATALYST = false;
    public static final boolean DEBUG_BYPASS_REALMS_VERSION_CHECK = false;
    public static final boolean DEBUG_SOCIAL_INTERACTIONS = false;
    public static final boolean DEBUG_VALIDATE_RESOURCE_PATH_CASE = false;
    public static final boolean DEBUG_IGNORE_LOCAL_MOB_CAP = false;
    public static final boolean DEBUG_SMALL_SPAWN = false;
    public static final boolean DEBUG_DISABLE_LIQUID_SPREADING = false;
    public static final boolean DEBUG_AQUIFERS = false;
    public static final boolean DEBUG_JFR_PROFILING_ENABLE_LEVEL_LOADING = false;
    public static boolean debugGenerateSquareTerrainWithoutNoise = false;
    public static boolean debugGenerateStripedTerrainWithoutNoise = false;
    public static final boolean DEBUG_ONLY_GENERATE_HALF_THE_WORLD = false;
    public static final boolean DEBUG_DISABLE_FLUID_GENERATION = false;
    public static final boolean DEBUG_DISABLE_AQUIFERS = false;
    public static final boolean DEBUG_DISABLE_SURFACE = false;
    public static final boolean DEBUG_DISABLE_CARVERS = false;
    public static final boolean DEBUG_DISABLE_STRUCTURES = false;
    public static final boolean DEBUG_DISABLE_FEATURES = false;
    public static final boolean DEBUG_DISABLE_ORE_VEINS = false;
    public static final boolean DEBUG_DISABLE_BLENDING = false;
    public static final boolean DEBUG_DISABLE_BELOW_ZERO_RETROGENERATION = false;
    public static final int DEFAULT_MINECRAFT_PORT = 25565;
    public static final boolean INGAME_DEBUG_OUTPUT = false;
    public static final boolean DEBUG_SUBTITLES = false;
    public static final int FAKE_MS_LATENCY = 0;
    public static final int FAKE_MS_JITTER = 0;
    public static final ResourceLeakDetector.Level NETTY_LEAK_DETECTION = ResourceLeakDetector.Level.DISABLED;
    public static final boolean COMMAND_STACK_TRACES = false;
    public static final boolean DEBUG_WORLD_RECREATE = false;
    public static final boolean DEBUG_SHOW_SERVER_DEBUG_VALUES = false;
    public static final boolean DEBUG_STORE_CHUNK_STACKTRACES = false;
    public static final boolean DEBUG_FEATURE_COUNT = false;
    public static final boolean DEBUG_RESOURCE_GENERATION_OVERRIDE = false;
    public static final boolean DEBUG_FORCE_TELEMETRY = false;
    public static final long MAXIMUM_TICK_TIME_NANOS = Duration.ofMillis((long)300L).toNanos();
    public static boolean CHECK_DATA_FIXER_SCHEMA = true;
    public static boolean IS_RUNNING_IN_IDE;
    public static DataFixerOptimizationOption DATAFIXER_OPTIMIZATION_OPTION;
    public static final int WORLD_RESOLUTION = 16;
    public static final int MAX_CHAT_LENGTH = 256;
    public static final int MAX_COMMAND_LENGTH = 32500;
    public static final int MAX_CHAINED_NEIGHBOR_UPDATES = 1000000;
    public static final int MAX_RENDER_DISTANCE = 32;
    public static final char[] ILLEGAL_FILE_CHARACTERS;
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_MINUTE = 1200;
    public static final int TICKS_PER_GAME_DAY = 24000;
    public static final float AVERAGE_GAME_TICKS_PER_RANDOM_TICK_PER_BLOCK = 1365.3334f;
    public static final float AVERAGE_RANDOM_TICKS_PER_BLOCK_PER_MINUTE = 0.87890625f;
    public static final float AVERAGE_RANDOM_TICKS_PER_BLOCK_PER_GAME_DAY = 17.578125f;
    @Nullable
    private static WorldVersion CURRENT_VERSION;

    public static boolean isAllowedChatCharacter(char $$0) {
        return $$0 != '\u00a7' && $$0 >= ' ' && $$0 != '\u007f';
    }

    public static String filterText(String $$0) {
        return SharedConstants.filterText($$0, false);
    }

    public static String filterText(String $$0, boolean $$1) {
        StringBuilder $$2 = new StringBuilder();
        for (char $$3 : $$0.toCharArray()) {
            if (SharedConstants.isAllowedChatCharacter($$3)) {
                $$2.append($$3);
                continue;
            }
            if (!$$1 || $$3 != '\n') continue;
            $$2.append($$3);
        }
        return $$2.toString();
    }

    public static void setVersion(WorldVersion $$0) {
        if (CURRENT_VERSION == null) {
            CURRENT_VERSION = $$0;
        } else if ($$0 != CURRENT_VERSION) {
            throw new IllegalStateException("Cannot override the current game version!");
        }
    }

    public static void tryDetectVersion() {
        if (CURRENT_VERSION == null) {
            CURRENT_VERSION = DetectedVersion.tryDetectVersion();
        }
    }

    public static WorldVersion getCurrentVersion() {
        if (CURRENT_VERSION == null) {
            throw new IllegalStateException("Game version not set");
        }
        return CURRENT_VERSION;
    }

    public static int getProtocolVersion() {
        return 761;
    }

    public static boolean debugVoidTerrain(ChunkPos $$0) {
        int $$1 = $$0.getMinBlockX();
        int $$2 = $$0.getMinBlockZ();
        if (debugGenerateSquareTerrainWithoutNoise) {
            return $$1 > 8192 || $$1 < 0 || $$2 > 1024 || $$2 < 0;
        }
        return false;
    }

    public static void enableDataFixerOptimizations() {
        DATAFIXER_OPTIMIZATION_OPTION = switch (DATAFIXER_OPTIMIZATION_OPTION) {
            case DataFixerOptimizationOption.INITIALIZED_UNOPTIMIZED -> throw new IllegalStateException("Tried to enable datafixer optimization after unoptimized initialization");
            case DataFixerOptimizationOption.INITIALIZED_OPTIMIZED -> DataFixerOptimizationOption.INITIALIZED_OPTIMIZED;
            default -> DataFixerOptimizationOption.UNINITIALIZED_OPTIMIZED;
        };
    }

    static {
        DATAFIXER_OPTIMIZATION_OPTION = DataFixerOptimizationOption.UNINITIALIZED_UNOPTIMIZED;
        ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        ResourceLeakDetector.setLevel((ResourceLeakDetector.Level)NETTY_LEAK_DETECTION);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new BrigadierExceptions();
    }
}