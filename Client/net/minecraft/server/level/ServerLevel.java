/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  java.io.BufferedWriter
 *  java.io.IOException
 *  java.io.Writer
 *  java.lang.CharSequence
 *  java.lang.Deprecated
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.UUID
 *  java.util.concurrent.Executor
 *  java.util.function.BiConsumer
 *  java.util.function.BooleanSupplier
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTicks;
import org.slf4j.Logger;

public class ServerLevel
extends Level
implements WorldGenLevel {
    public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
    public static final IntProvider RAIN_DELAY = UniformInt.of(12000, 180000);
    public static final IntProvider RAIN_DURATION = UniformInt.of(12000, 24000);
    private static final IntProvider THUNDER_DELAY = UniformInt.of(12000, 180000);
    public static final IntProvider THUNDER_DURATION = UniformInt.of(3600, 15600);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int EMPTY_TIME_NO_TICK = 300;
    private static final int MAX_SCHEDULED_TICKS_PER_TICK = 65536;
    final List<ServerPlayer> players = Lists.newArrayList();
    private final ServerChunkCache chunkSource;
    private final MinecraftServer server;
    private final ServerLevelData serverLevelData;
    final EntityTickList entityTickList = new EntityTickList();
    private final PersistentEntitySectionManager<Entity> entityManager;
    private final GameEventDispatcher gameEventDispatcher;
    public boolean noSave;
    private final SleepStatus sleepStatus;
    private int emptyTime;
    private final PortalForcer portalForcer;
    private final LevelTicks<Block> blockTicks = new LevelTicks(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
    private final LevelTicks<Fluid> fluidTicks = new LevelTicks(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
    final Set<Mob> navigatingMobs = new ObjectOpenHashSet();
    volatile boolean isUpdatingNavigations;
    protected final Raids raids;
    private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet();
    private final List<BlockEventData> blockEventsToReschedule = new ArrayList(64);
    private boolean handlingTick;
    private final List<CustomSpawner> customSpawners;
    @Nullable
    private final EndDragonFight dragonFight;
    final Int2ObjectMap<EnderDragonPart> dragonParts = new Int2ObjectOpenHashMap();
    private final StructureManager structureManager;
    private final StructureCheck structureCheck;
    private final boolean tickTime;

    public ServerLevel(MinecraftServer $$02, Executor $$1, LevelStorageSource.LevelStorageAccess $$2, ServerLevelData $$3, ResourceKey<Level> $$4, LevelStem $$5, ChunkProgressListener $$6, boolean $$7, long $$8, List<CustomSpawner> $$9, boolean $$10) {
        super($$3, $$4, $$5.type(), (Supplier<ProfilerFiller>)((Supplier)$$02::getProfiler), false, $$7, $$8, $$02.getMaxChainedNeighborUpdates());
        this.tickTime = $$10;
        this.server = $$02;
        this.customSpawners = $$9;
        this.serverLevelData = $$3;
        ChunkGenerator $$11 = $$5.generator();
        boolean $$12 = $$02.forceSynchronousWrites();
        DataFixer $$13 = $$02.getFixerUpper();
        EntityStorage $$14 = new EntityStorage(this, $$2.getDimensionPath($$4).resolve("entities"), $$13, $$12, $$02);
        this.entityManager = new PersistentEntitySectionManager<Entity>(Entity.class, new EntityCallbacks(), $$14);
        this.chunkSource = new ServerChunkCache(this, $$2, $$13, $$02.getStructureManager(), $$1, $$11, $$02.getPlayerList().getViewDistance(), $$02.getPlayerList().getSimulationDistance(), $$12, $$6, this.entityManager::updateChunkStatus, (Supplier<DimensionDataStorage>)((Supplier)() -> $$02.overworld().getDataStorage()));
        this.chunkSource.getGeneratorState().ensureStructuresGenerated();
        this.portalForcer = new PortalForcer(this);
        this.updateSkyBrightness();
        this.prepareWeather();
        this.getWorldBorder().setAbsoluteMaxSize($$02.getAbsoluteMaxWorldSize());
        this.raids = (Raids)this.getDataStorage().computeIfAbsent($$0 -> Raids.load(this, $$0), () -> new Raids(this), Raids.getFileId(this.dimensionTypeRegistration()));
        if (!$$02.isSingleplayer()) {
            $$3.setGameType($$02.getDefaultGameType());
        }
        long $$15 = $$02.getWorldData().worldGenOptions().seed();
        this.structureCheck = new StructureCheck(this.chunkSource.chunkScanner(), this.registryAccess(), $$02.getStructureManager(), $$4, $$11, this.chunkSource.randomState(), this, $$11.getBiomeSource(), $$15, $$13);
        this.structureManager = new StructureManager(this, $$02.getWorldData().worldGenOptions(), this.structureCheck);
        this.dragonFight = this.dimension() == Level.END && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END) ? new EndDragonFight(this, $$15, $$02.getWorldData().endDragonFightData()) : null;
        this.sleepStatus = new SleepStatus();
        this.gameEventDispatcher = new GameEventDispatcher(this);
    }

    public void setWeatherParameters(int $$0, int $$1, boolean $$2, boolean $$3) {
        this.serverLevelData.setClearWeatherTime($$0);
        this.serverLevelData.setRainTime($$1);
        this.serverLevelData.setThunderTime($$1);
        this.serverLevelData.setRaining($$2);
        this.serverLevelData.setThundering($$3);
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int $$0, int $$1, int $$2) {
        return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome($$0, $$1, $$2, this.getChunkSource().randomState().sampler());
    }

    public StructureManager structureManager() {
        return this.structureManager;
    }

    public void tick(BooleanSupplier $$0) {
        boolean $$5;
        ProfilerFiller $$12 = this.getProfiler();
        this.handlingTick = true;
        $$12.push("world border");
        this.getWorldBorder().tick();
        $$12.popPush("weather");
        this.advanceWeatherCycle();
        int $$2 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        if (this.sleepStatus.areEnoughSleeping($$2) && this.sleepStatus.areEnoughDeepSleeping($$2, this.players)) {
            if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                long $$3 = this.levelData.getDayTime() + 24000L;
                this.setDayTime($$3 - $$3 % 24000L);
            }
            this.wakeUpAllPlayers();
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && this.isRaining()) {
                this.resetWeatherCycle();
            }
        }
        this.updateSkyBrightness();
        this.tickTime();
        $$12.popPush("tickPending");
        if (!this.isDebug()) {
            long $$4 = this.getGameTime();
            $$12.push("blockTicks");
            this.blockTicks.tick($$4, 65536, (BiConsumer<BlockPos, Block>)((BiConsumer)this::tickBlock));
            $$12.popPush("fluidTicks");
            this.fluidTicks.tick($$4, 65536, (BiConsumer<BlockPos, Fluid>)((BiConsumer)this::tickFluid));
            $$12.pop();
        }
        $$12.popPush("raid");
        this.raids.tick();
        $$12.popPush("chunkSource");
        this.getChunkSource().tick($$0, true);
        $$12.popPush("blockEvents");
        this.runBlockEvents();
        this.handlingTick = false;
        $$12.pop();
        boolean bl = $$5 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
        if ($$5) {
            this.resetEmptyTime();
        }
        if ($$5 || this.emptyTime++ < 300) {
            $$12.push("entities");
            if (this.dragonFight != null) {
                $$12.push("dragonFight");
                this.dragonFight.tick();
                $$12.pop();
            }
            this.entityTickList.forEach((Consumer<Entity>)((Consumer)$$1 -> {
                if ($$1.isRemoved()) {
                    return;
                }
                if (this.shouldDiscardEntity((Entity)$$1)) {
                    $$1.discard();
                    return;
                }
                $$12.push("checkDespawn");
                $$1.checkDespawn();
                $$12.pop();
                if (!this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange($$1.chunkPosition().toLong())) {
                    return;
                }
                Entity $$2 = $$1.getVehicle();
                if ($$2 != null) {
                    if ($$2.isRemoved() || !$$2.hasPassenger((Entity)$$1)) {
                        $$1.stopRiding();
                    } else {
                        return;
                    }
                }
                $$12.push("tick");
                this.guardEntityTick(this::tickNonPassenger, $$1);
                $$12.pop();
            }));
            $$12.pop();
            this.tickBlockEntities();
        }
        $$12.push("entityManagement");
        this.entityManager.tick();
        $$12.pop();
    }

    @Override
    public boolean shouldTickBlocksAt(long $$0) {
        return this.chunkSource.chunkMap.getDistanceManager().inBlockTickingRange($$0);
    }

    protected void tickTime() {
        if (!this.tickTime) {
            return;
        }
        long $$0 = this.levelData.getGameTime() + 1L;
        this.serverLevelData.setGameTime($$0);
        this.serverLevelData.getScheduledEvents().tick(this.server, $$0);
        if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
        }
    }

    public void setDayTime(long $$0) {
        this.serverLevelData.setDayTime($$0);
    }

    public void tickCustomSpawners(boolean $$0, boolean $$1) {
        for (CustomSpawner $$2 : this.customSpawners) {
            $$2.tick(this, $$0, $$1);
        }
    }

    private boolean shouldDiscardEntity(Entity $$0) {
        if (!this.server.isSpawningAnimals() && ($$0 instanceof Animal || $$0 instanceof WaterAnimal)) {
            return true;
        }
        return !this.server.areNpcsEnabled() && $$0 instanceof Npc;
    }

    private void wakeUpAllPlayers() {
        this.sleepStatus.removeAllSleepers();
        ((List)this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList())).forEach($$0 -> $$0.stopSleepInBed(false, false));
    }

    public void tickChunk(LevelChunk $$0, int $$1) {
        BlockPos $$7;
        ChunkPos $$2 = $$0.getPos();
        boolean $$3 = this.isRaining();
        int $$4 = $$2.getMinBlockX();
        int $$5 = $$2.getMinBlockZ();
        ProfilerFiller $$6 = this.getProfiler();
        $$6.push("thunder");
        if ($$3 && this.isThundering() && this.random.nextInt(100000) == 0 && this.isRainingAt($$7 = this.findLightningTargetAround(this.getBlockRandomPos($$4, 0, $$5, 15)))) {
            LightningBolt $$11;
            SkeletonHorse $$10;
            boolean $$9;
            DifficultyInstance $$8 = this.getCurrentDifficultyAt($$7);
            boolean bl = $$9 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)$$8.getEffectiveDifficulty() * 0.01 && !this.getBlockState((BlockPos)$$7.below()).is(Blocks.LIGHTNING_ROD);
            if ($$9 && ($$10 = EntityType.SKELETON_HORSE.create(this)) != null) {
                $$10.setTrap(true);
                $$10.setAge(0);
                $$10.setPos($$7.getX(), $$7.getY(), $$7.getZ());
                this.addFreshEntity($$10);
            }
            if (($$11 = EntityType.LIGHTNING_BOLT.create(this)) != null) {
                $$11.moveTo(Vec3.atBottomCenterOf($$7));
                $$11.setVisualOnly($$9);
                this.addFreshEntity($$11);
            }
        }
        $$6.popPush("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            BlockPos $$12 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos($$4, 0, $$5, 15));
            Vec3i $$13 = $$12.below();
            Biome $$14 = (Biome)this.getBiome($$12).value();
            if ($$14.shouldFreeze(this, (BlockPos)$$13)) {
                this.setBlockAndUpdate((BlockPos)$$13, Blocks.ICE.defaultBlockState());
            }
            if ($$3) {
                Biome.Precipitation $$19;
                int $$15 = this.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
                if ($$15 > 0 && $$14.shouldSnow(this, $$12)) {
                    BlockState $$16 = this.getBlockState($$12);
                    if ($$16.is(Blocks.SNOW)) {
                        int $$17 = $$16.getValue(SnowLayerBlock.LAYERS);
                        if ($$17 < Math.min((int)$$15, (int)8)) {
                            BlockState $$18 = (BlockState)$$16.setValue(SnowLayerBlock.LAYERS, $$17 + 1);
                            Block.pushEntitiesUp($$16, $$18, this, $$12);
                            this.setBlockAndUpdate($$12, $$18);
                        }
                    } else {
                        this.setBlockAndUpdate($$12, Blocks.SNOW.defaultBlockState());
                    }
                }
                if (($$19 = $$14.getPrecipitationAt((BlockPos)$$13)) != Biome.Precipitation.NONE) {
                    BlockState $$20 = this.getBlockState((BlockPos)$$13);
                    $$20.getBlock().handlePrecipitation($$20, this, (BlockPos)$$13, $$19);
                }
            }
        }
        $$6.popPush("tickBlocks");
        if ($$1 > 0) {
            for (LevelChunkSection $$21 : $$0.getSections()) {
                if (!$$21.isRandomlyTicking()) continue;
                int $$22 = $$21.bottomBlockY();
                for (int $$23 = 0; $$23 < $$1; ++$$23) {
                    FluidState $$26;
                    BlockPos $$24 = this.getBlockRandomPos($$4, $$22, $$5, 15);
                    $$6.push("randomTick");
                    BlockState $$25 = $$21.getBlockState($$24.getX() - $$4, $$24.getY() - $$22, $$24.getZ() - $$5);
                    if ($$25.isRandomlyTicking()) {
                        $$25.randomTick(this, $$24, this.random);
                    }
                    if (($$26 = $$25.getFluidState()).isRandomlyTicking()) {
                        $$26.randomTick(this, $$24, this.random);
                    }
                    $$6.pop();
                }
            }
        }
        $$6.pop();
    }

    private Optional<BlockPos> findLightningRod(BlockPos $$02) {
        Optional<BlockPos> $$1 = this.getPoiManager().findClosest((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypes.LIGHTNING_ROD)), (Predicate<BlockPos>)((Predicate)$$0 -> $$0.getY() == this.getHeight(Heightmap.Types.WORLD_SURFACE, $$0.getX(), $$0.getZ()) - 1), $$02, 128, PoiManager.Occupancy.ANY);
        return $$1.map($$0 -> $$0.above(1));
    }

    protected BlockPos findLightningTargetAround(BlockPos $$02) {
        Vec3i $$1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$02);
        Optional<BlockPos> $$2 = this.findLightningRod((BlockPos)$$1);
        if ($$2.isPresent()) {
            return (BlockPos)$$2.get();
        }
        AABB $$3 = new AABB((BlockPos)$$1, new BlockPos($$1.getX(), this.getMaxBuildHeight(), $$1.getZ())).inflate(3.0);
        List $$4 = this.getEntitiesOfClass(LivingEntity.class, $$3, $$0 -> $$0 != null && $$0.isAlive() && this.canSeeSky($$0.blockPosition()));
        if (!$$4.isEmpty()) {
            return ((LivingEntity)$$4.get(this.random.nextInt($$4.size()))).blockPosition();
        }
        if ($$1.getY() == this.getMinBuildHeight() - 1) {
            $$1 = $$1.above(2);
        }
        return $$1;
    }

    public boolean isHandlingTick() {
        return this.handlingTick;
    }

    public boolean canSleepThroughNights() {
        return this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE) <= 100;
    }

    private void announceSleepStatus() {
        MutableComponent $$2;
        if (!this.canSleepThroughNights()) {
            return;
        }
        if (this.getServer().isSingleplayer() && !this.getServer().isPublished()) {
            return;
        }
        int $$0 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        if (this.sleepStatus.areEnoughSleeping($$0)) {
            MutableComponent $$1 = Component.translatable("sleep.skipping_night");
        } else {
            $$2 = Component.translatable("sleep.players_sleeping", this.sleepStatus.amountSleeping(), this.sleepStatus.sleepersNeeded($$0));
        }
        for (ServerPlayer $$3 : this.players) {
            $$3.displayClientMessage($$2, true);
        }
    }

    public void updateSleepingPlayerList() {
        if (!this.players.isEmpty() && this.sleepStatus.update(this.players)) {
            this.announceSleepStatus();
        }
    }

    @Override
    public ServerScoreboard getScoreboard() {
        return this.server.getScoreboard();
    }

    private void advanceWeatherCycle() {
        boolean $$0 = this.isRaining();
        if (this.dimensionType().hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                int $$1 = this.serverLevelData.getClearWeatherTime();
                int $$2 = this.serverLevelData.getThunderTime();
                int $$3 = this.serverLevelData.getRainTime();
                boolean $$4 = this.levelData.isThundering();
                boolean $$5 = this.levelData.isRaining();
                if ($$1 > 0) {
                    --$$1;
                    $$2 = $$4 ? 0 : 1;
                    $$3 = $$5 ? 0 : 1;
                    $$4 = false;
                    $$5 = false;
                } else {
                    if ($$2 > 0) {
                        if (--$$2 == 0) {
                            $$4 = !$$4;
                        }
                    } else {
                        $$2 = $$4 ? THUNDER_DURATION.sample(this.random) : THUNDER_DELAY.sample(this.random);
                    }
                    if ($$3 > 0) {
                        if (--$$3 == 0) {
                            $$5 = !$$5;
                        }
                    } else {
                        $$3 = $$5 ? RAIN_DURATION.sample(this.random) : RAIN_DELAY.sample(this.random);
                    }
                }
                this.serverLevelData.setThunderTime($$2);
                this.serverLevelData.setRainTime($$3);
                this.serverLevelData.setClearWeatherTime($$1);
                this.serverLevelData.setThundering($$4);
                this.serverLevelData.setRaining($$5);
            }
            this.oThunderLevel = this.thunderLevel;
            this.thunderLevel = this.levelData.isThundering() ? (this.thunderLevel += 0.01f) : (this.thunderLevel -= 0.01f);
            this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0f, 1.0f);
            this.oRainLevel = this.rainLevel;
            this.rainLevel = this.levelData.isRaining() ? (this.rainLevel += 0.01f) : (this.rainLevel -= 0.01f);
            this.rainLevel = Mth.clamp(this.rainLevel, 0.0f, 1.0f);
        }
        if (this.oRainLevel != this.rainLevel) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
        }
        if (this.oThunderLevel != this.thunderLevel) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
        }
        if ($$0 != this.isRaining()) {
            if ($$0) {
                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0f));
            } else {
                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0f));
            }
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel));
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel));
        }
    }

    private void resetWeatherCycle() {
        this.serverLevelData.setRainTime(0);
        this.serverLevelData.setRaining(false);
        this.serverLevelData.setThunderTime(0);
        this.serverLevelData.setThundering(false);
    }

    public void resetEmptyTime() {
        this.emptyTime = 0;
    }

    private void tickFluid(BlockPos $$0, Fluid $$1) {
        FluidState $$2 = this.getFluidState($$0);
        if ($$2.is($$1)) {
            $$2.tick(this, $$0);
        }
    }

    private void tickBlock(BlockPos $$0, Block $$1) {
        BlockState $$2 = this.getBlockState($$0);
        if ($$2.is($$1)) {
            $$2.tick(this, $$0, this.random);
        }
    }

    public void tickNonPassenger(Entity $$0) {
        $$0.setOldPosAndRot();
        ProfilerFiller $$1 = this.getProfiler();
        ++$$0.tickCount;
        this.getProfiler().push((Supplier<String>)((Supplier)() -> BuiltInRegistries.ENTITY_TYPE.getKey($$0.getType()).toString()));
        $$1.incrementCounter("tickNonPassenger");
        $$0.tick();
        this.getProfiler().pop();
        for (Entity $$2 : $$0.getPassengers()) {
            this.tickPassenger($$0, $$2);
        }
    }

    private void tickPassenger(Entity $$0, Entity $$1) {
        if ($$1.isRemoved() || $$1.getVehicle() != $$0) {
            $$1.stopRiding();
            return;
        }
        if (!($$1 instanceof Player) && !this.entityTickList.contains($$1)) {
            return;
        }
        $$1.setOldPosAndRot();
        ++$$1.tickCount;
        ProfilerFiller $$2 = this.getProfiler();
        $$2.push((Supplier<String>)((Supplier)() -> BuiltInRegistries.ENTITY_TYPE.getKey($$1.getType()).toString()));
        $$2.incrementCounter("tickPassenger");
        $$1.rideTick();
        $$2.pop();
        for (Entity $$3 : $$1.getPassengers()) {
            this.tickPassenger($$1, $$3);
        }
    }

    @Override
    public boolean mayInteract(Player $$0, BlockPos $$1) {
        return !this.server.isUnderSpawnProtection(this, $$1, $$0) && this.getWorldBorder().isWithinBounds($$1);
    }

    public void save(@Nullable ProgressListener $$0, boolean $$1, boolean $$2) {
        ServerChunkCache $$3 = this.getChunkSource();
        if ($$2) {
            return;
        }
        if ($$0 != null) {
            $$0.progressStartNoAbort(Component.translatable("menu.savingLevel"));
        }
        this.saveLevelData();
        if ($$0 != null) {
            $$0.progressStage(Component.translatable("menu.savingChunks"));
        }
        $$3.save($$1);
        if ($$1) {
            this.entityManager.saveAll();
        } else {
            this.entityManager.autoSave();
        }
    }

    private void saveLevelData() {
        if (this.dragonFight != null) {
            this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
        }
        this.getChunkSource().getDataStorage().save();
    }

    public <T extends Entity> List<? extends T> getEntities(EntityTypeTest<Entity, T> $$0, Predicate<? super T> $$1) {
        ArrayList $$2 = Lists.newArrayList();
        this.getEntities($$0, $$1, (List<? super T>)$$2);
        return $$2;
    }

    public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, Predicate<? super T> $$1, List<? super T> $$2) {
        this.getEntities($$0, $$1, $$2, Integer.MAX_VALUE);
    }

    public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, Predicate<? super T> $$1, List<? super T> $$2, int $$32) {
        this.getEntities().get($$0, $$3 -> {
            if ($$1.test($$3)) {
                $$2.add($$3);
                if ($$2.size() >= $$32) {
                    return AbortableIterationConsumer.Continuation.ABORT;
                }
            }
            return AbortableIterationConsumer.Continuation.CONTINUE;
        });
    }

    public List<? extends EnderDragon> getDragons() {
        return this.getEntities(EntityType.ENDER_DRAGON, LivingEntity::isAlive);
    }

    public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> $$0) {
        return this.getPlayers($$0, Integer.MAX_VALUE);
    }

    public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> $$0, int $$1) {
        ArrayList $$2 = Lists.newArrayList();
        for (ServerPlayer $$3 : this.players) {
            if (!$$0.test((Object)$$3)) continue;
            $$2.add((Object)$$3);
            if ($$2.size() < $$1) continue;
            return $$2;
        }
        return $$2;
    }

    @Nullable
    public ServerPlayer getRandomPlayer() {
        List<ServerPlayer> $$0 = this.getPlayers((Predicate<? super ServerPlayer>)((Predicate)LivingEntity::isAlive));
        if ($$0.isEmpty()) {
            return null;
        }
        return (ServerPlayer)$$0.get(this.random.nextInt($$0.size()));
    }

    @Override
    public boolean addFreshEntity(Entity $$0) {
        return this.addEntity($$0);
    }

    public boolean addWithUUID(Entity $$0) {
        return this.addEntity($$0);
    }

    public void addDuringTeleport(Entity $$0) {
        this.addEntity($$0);
    }

    public void addDuringCommandTeleport(ServerPlayer $$0) {
        this.addPlayer($$0);
    }

    public void addDuringPortalTeleport(ServerPlayer $$0) {
        this.addPlayer($$0);
    }

    public void addNewPlayer(ServerPlayer $$0) {
        this.addPlayer($$0);
    }

    public void addRespawnedPlayer(ServerPlayer $$0) {
        this.addPlayer($$0);
    }

    private void addPlayer(ServerPlayer $$0) {
        Entity $$1 = this.getEntities().get($$0.getUUID());
        if ($$1 != null) {
            LOGGER.warn("Force-added player with duplicate UUID {}", (Object)$$0.getUUID().toString());
            $$1.unRide();
            this.removePlayerImmediately((ServerPlayer)$$1, Entity.RemovalReason.DISCARDED);
        }
        this.entityManager.addNewEntity($$0);
    }

    private boolean addEntity(Entity $$0) {
        if ($$0.isRemoved()) {
            LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getKey($$0.getType()));
            return false;
        }
        return this.entityManager.addNewEntity($$0);
    }

    public boolean tryAddFreshEntityWithPassengers(Entity $$0) {
        if ($$0.getSelfAndPassengers().map(Entity::getUUID).anyMatch(this.entityManager::isLoaded)) {
            return false;
        }
        this.addFreshEntityWithPassengers($$0);
        return true;
    }

    public void unload(LevelChunk $$0) {
        $$0.clearAllBlockEntities();
        $$0.unregisterTickContainerFromLevel(this);
    }

    public void removePlayerImmediately(ServerPlayer $$0, Entity.RemovalReason $$1) {
        $$0.remove($$1);
    }

    @Override
    public void destroyBlockProgress(int $$0, BlockPos $$1, int $$2) {
        for (ServerPlayer $$3 : this.server.getPlayerList().getPlayers()) {
            double $$6;
            double $$5;
            double $$4;
            if ($$3 == null || $$3.level != this || $$3.getId() == $$0 || !(($$4 = (double)$$1.getX() - $$3.getX()) * $$4 + ($$5 = (double)$$1.getY() - $$3.getY()) * $$5 + ($$6 = (double)$$1.getZ() - $$3.getZ()) * $$6 < 1024.0)) continue;
            $$3.connection.send(new ClientboundBlockDestructionPacket($$0, $$1, $$2));
        }
    }

    @Override
    public void playSeededSound(@Nullable Player $$0, double $$1, double $$2, double $$3, Holder<SoundEvent> $$4, SoundSource $$5, float $$6, float $$7, long $$8) {
        this.server.getPlayerList().broadcast($$0, $$1, $$2, $$3, $$4.value().getRange($$6), this.dimension(), new ClientboundSoundPacket($$4, $$5, $$1, $$2, $$3, $$6, $$7, $$8));
    }

    @Override
    public void playSeededSound(@Nullable Player $$0, Entity $$1, Holder<SoundEvent> $$2, SoundSource $$3, float $$4, float $$5, long $$6) {
        this.server.getPlayerList().broadcast($$0, $$1.getX(), $$1.getY(), $$1.getZ(), $$2.value().getRange($$4), this.dimension(), new ClientboundSoundEntityPacket($$2, $$3, $$1, $$4, $$5, $$6));
    }

    @Override
    public void globalLevelEvent(int $$0, BlockPos $$1, int $$2) {
        if (this.getGameRules().getBoolean(GameRules.RULE_GLOBAL_SOUND_EVENTS)) {
            this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket($$0, $$1, $$2, true));
        } else {
            this.levelEvent(null, $$0, $$1, $$2);
        }
    }

    @Override
    public void levelEvent(@Nullable Player $$0, int $$1, BlockPos $$2, int $$3) {
        this.server.getPlayerList().broadcast($$0, $$2.getX(), $$2.getY(), $$2.getZ(), 64.0, this.dimension(), new ClientboundLevelEventPacket($$1, $$2, $$3, false));
    }

    public int getLogicalHeight() {
        return this.dimensionType().logicalHeight();
    }

    @Override
    public void gameEvent(GameEvent $$0, Vec3 $$1, GameEvent.Context $$2) {
        this.gameEventDispatcher.post($$0, $$1, $$2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendBlockUpdated(BlockPos $$0, BlockState $$1, BlockState $$2, int $$3) {
        if (this.isUpdatingNavigations) {
            String $$4 = "recursive call to sendBlockUpdated";
            Util.logAndPauseIfInIde("recursive call to sendBlockUpdated", (Throwable)new IllegalStateException("recursive call to sendBlockUpdated"));
        }
        this.getChunkSource().blockChanged($$0);
        VoxelShape $$5 = $$1.getCollisionShape(this, $$0);
        VoxelShape $$6 = $$2.getCollisionShape(this, $$0);
        if (!Shapes.joinIsNotEmpty($$5, $$6, BooleanOp.NOT_SAME)) {
            return;
        }
        ObjectArrayList $$7 = new ObjectArrayList();
        for (Mob $$8 : this.navigatingMobs) {
            PathNavigation $$9 = $$8.getNavigation();
            if (!$$9.shouldRecomputePath($$0)) continue;
            $$7.add((Object)$$9);
        }
        try {
            this.isUpdatingNavigations = true;
            for (PathNavigation $$10 : $$7) {
                $$10.recomputePath();
            }
        }
        finally {
            this.isUpdatingNavigations = false;
        }
    }

    @Override
    public void updateNeighborsAt(BlockPos $$0, Block $$1) {
        this.neighborUpdater.updateNeighborsAtExceptFromFacing($$0, $$1, null);
    }

    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, Direction $$2) {
        this.neighborUpdater.updateNeighborsAtExceptFromFacing($$0, $$1, $$2);
    }

    @Override
    public void neighborChanged(BlockPos $$0, Block $$1, BlockPos $$2) {
        this.neighborUpdater.neighborChanged($$0, $$1, $$2);
    }

    @Override
    public void neighborChanged(BlockState $$0, BlockPos $$1, Block $$2, BlockPos $$3, boolean $$4) {
        this.neighborUpdater.neighborChanged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void broadcastEntityEvent(Entity $$0, byte $$1) {
        this.getChunkSource().broadcastAndSend($$0, new ClientboundEntityEventPacket($$0, $$1));
    }

    @Override
    public ServerChunkCache getChunkSource() {
        return this.chunkSource;
    }

    @Override
    public Explosion explode(@Nullable Entity $$0, @Nullable DamageSource $$1, @Nullable ExplosionDamageCalculator $$2, double $$3, double $$4, double $$5, float $$6, boolean $$7, Level.ExplosionInteraction $$8) {
        Explosion $$9 = this.explode($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, false);
        if (!$$9.interactsWithBlocks()) {
            $$9.clearToBlow();
        }
        for (ServerPlayer $$10 : this.players) {
            if (!($$10.distanceToSqr($$3, $$4, $$5) < 4096.0)) continue;
            $$10.connection.send(new ClientboundExplodePacket($$3, $$4, $$5, $$6, $$9.getToBlow(), (Vec3)$$9.getHitPlayers().get((Object)$$10)));
        }
        return $$9;
    }

    @Override
    public void blockEvent(BlockPos $$0, Block $$1, int $$2, int $$3) {
        this.blockEvents.add((Object)new BlockEventData($$0, $$1, $$2, $$3));
    }

    private void runBlockEvents() {
        this.blockEventsToReschedule.clear();
        while (!this.blockEvents.isEmpty()) {
            BlockEventData $$0 = (BlockEventData)((Object)this.blockEvents.removeFirst());
            if (this.shouldTickBlocksAt($$0.pos())) {
                if (!this.doBlockEvent($$0)) continue;
                this.server.getPlayerList().broadcast(null, $$0.pos().getX(), $$0.pos().getY(), $$0.pos().getZ(), 64.0, this.dimension(), new ClientboundBlockEventPacket($$0.pos(), $$0.block(), $$0.paramA(), $$0.paramB()));
                continue;
            }
            this.blockEventsToReschedule.add((Object)$$0);
        }
        this.blockEvents.addAll(this.blockEventsToReschedule);
    }

    private boolean doBlockEvent(BlockEventData $$0) {
        BlockState $$1 = this.getBlockState($$0.pos());
        if ($$1.is($$0.block())) {
            return $$1.triggerEvent(this, $$0.pos(), $$0.paramA(), $$0.paramB());
        }
        return false;
    }

    public LevelTicks<Block> getBlockTicks() {
        return this.blockTicks;
    }

    public LevelTicks<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    @Override
    @Nonnull
    public MinecraftServer getServer() {
        return this.server;
    }

    public PortalForcer getPortalForcer() {
        return this.portalForcer;
    }

    public StructureTemplateManager getStructureManager() {
        return this.server.getStructureManager();
    }

    public <T extends ParticleOptions> int sendParticles(T $$0, double $$1, double $$2, double $$3, int $$4, double $$5, double $$6, double $$7, double $$8) {
        ClientboundLevelParticlesPacket $$9 = new ClientboundLevelParticlesPacket($$0, false, $$1, $$2, $$3, (float)$$5, (float)$$6, (float)$$7, (float)$$8, $$4);
        int $$10 = 0;
        for (int $$11 = 0; $$11 < this.players.size(); ++$$11) {
            ServerPlayer $$12 = (ServerPlayer)this.players.get($$11);
            if (!this.sendParticles($$12, false, $$1, $$2, $$3, $$9)) continue;
            ++$$10;
        }
        return $$10;
    }

    public <T extends ParticleOptions> boolean sendParticles(ServerPlayer $$0, T $$1, boolean $$2, double $$3, double $$4, double $$5, int $$6, double $$7, double $$8, double $$9, double $$10) {
        ClientboundLevelParticlesPacket $$11 = new ClientboundLevelParticlesPacket($$1, $$2, $$3, $$4, $$5, (float)$$7, (float)$$8, (float)$$9, (float)$$10, $$6);
        return this.sendParticles($$0, $$2, $$3, $$4, $$5, $$11);
    }

    private boolean sendParticles(ServerPlayer $$0, boolean $$1, double $$2, double $$3, double $$4, Packet<?> $$5) {
        if ($$0.getLevel() != this) {
            return false;
        }
        BlockPos $$6 = $$0.blockPosition();
        if ($$6.closerToCenterThan(new Vec3($$2, $$3, $$4), $$1 ? 512.0 : 32.0)) {
            $$0.connection.send($$5);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getEntity(int $$0) {
        return this.getEntities().get($$0);
    }

    @Deprecated
    @Nullable
    public Entity getEntityOrPart(int $$0) {
        Entity $$1 = this.getEntities().get($$0);
        if ($$1 != null) {
            return $$1;
        }
        return (Entity)this.dragonParts.get($$0);
    }

    @Nullable
    public Entity getEntity(UUID $$0) {
        return this.getEntities().get($$0);
    }

    @Nullable
    public BlockPos findNearestMapStructure(TagKey<Structure> $$0, BlockPos $$1, int $$2, boolean $$3) {
        if (!this.server.getWorldData().worldGenOptions().generateStructures()) {
            return null;
        }
        Optional<HolderSet.Named<Structure>> $$4 = this.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag($$0);
        if ($$4.isEmpty()) {
            return null;
        }
        Pair<BlockPos, Holder<Structure>> $$5 = this.getChunkSource().getGenerator().findNearestMapStructure(this, (HolderSet)$$4.get(), $$1, $$2, $$3);
        return $$5 != null ? (BlockPos)$$5.getFirst() : null;
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(Predicate<Holder<Biome>> $$0, BlockPos $$1, int $$2, int $$3, int $$4) {
        return this.getChunkSource().getGenerator().getBiomeSource().findClosestBiome3d($$1, $$2, $$3, $$4, $$0, this.getChunkSource().randomState().sampler(), this);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.server.getRecipeManager();
    }

    @Override
    public boolean noSave() {
        return this.noSave;
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.server.registryAccess();
    }

    public DimensionDataStorage getDataStorage() {
        return this.getChunkSource().getDataStorage();
    }

    @Override
    @Nullable
    public MapItemSavedData getMapData(String $$0) {
        return (MapItemSavedData)this.getServer().overworld().getDataStorage().get(MapItemSavedData::load, $$0);
    }

    @Override
    public void setMapData(String $$0, MapItemSavedData $$1) {
        this.getServer().overworld().getDataStorage().set($$0, $$1);
    }

    @Override
    public int getFreeMapId() {
        return ((MapIndex)this.getServer().overworld().getDataStorage().computeIfAbsent(MapIndex::load, MapIndex::new, "idcounts")).getFreeAuxValueForMap();
    }

    public void setDefaultSpawnPos(BlockPos $$0, float $$1) {
        ChunkPos $$2 = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
        this.levelData.setSpawn($$0, $$1);
        this.getChunkSource().removeRegionTicket(TicketType.START, $$2, 11, Unit.INSTANCE);
        this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos($$0), 11, Unit.INSTANCE);
        this.getServer().getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket($$0, $$1));
    }

    public LongSet getForcedChunks() {
        ForcedChunksSavedData $$0 = (ForcedChunksSavedData)this.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
        return $$0 != null ? LongSets.unmodifiable((LongSet)$$0.getChunks()) : LongSets.EMPTY_SET;
    }

    public boolean setChunkForced(int $$0, int $$1, boolean $$2) {
        boolean $$7;
        ForcedChunksSavedData $$3 = (ForcedChunksSavedData)this.getDataStorage().computeIfAbsent(ForcedChunksSavedData::load, ForcedChunksSavedData::new, "chunks");
        ChunkPos $$4 = new ChunkPos($$0, $$1);
        long $$5 = $$4.toLong();
        if ($$2) {
            boolean $$6 = $$3.getChunks().add($$5);
            if ($$6) {
                this.getChunk($$0, $$1);
            }
        } else {
            $$7 = $$3.getChunks().remove($$5);
        }
        $$3.setDirty($$7);
        if ($$7) {
            this.getChunkSource().updateChunkForced($$4, $$2);
        }
        return $$7;
    }

    public List<ServerPlayer> players() {
        return this.players;
    }

    @Override
    public void onBlockStateChange(BlockPos $$0, BlockState $$12, BlockState $$2) {
        Optional<Holder<PoiType>> $$4;
        Optional<Holder<PoiType>> $$3 = PoiTypes.forState($$12);
        if (Objects.equals($$3, $$4 = PoiTypes.forState($$2))) {
            return;
        }
        BlockPos $$5 = $$0.immutable();
        $$3.ifPresent($$1 -> this.getServer().execute(() -> {
            this.getPoiManager().remove($$5);
            DebugPackets.sendPoiRemovedPacket(this, $$5);
        }));
        $$4.ifPresent($$1 -> this.getServer().execute(() -> {
            this.getPoiManager().add($$5, (Holder<PoiType>)$$1);
            DebugPackets.sendPoiAddedPacket(this, $$5);
        }));
    }

    public PoiManager getPoiManager() {
        return this.getChunkSource().getPoiManager();
    }

    public boolean isVillage(BlockPos $$0) {
        return this.isCloseToVillage($$0, 1);
    }

    public boolean isVillage(SectionPos $$0) {
        return this.isVillage($$0.center());
    }

    public boolean isCloseToVillage(BlockPos $$0, int $$1) {
        if ($$1 > 6) {
            return false;
        }
        return this.sectionsToVillage(SectionPos.of($$0)) <= $$1;
    }

    public int sectionsToVillage(SectionPos $$0) {
        return this.getPoiManager().sectionsToVillage($$0);
    }

    public Raids getRaids() {
        return this.raids;
    }

    @Nullable
    public Raid getRaidAt(BlockPos $$0) {
        return this.raids.getNearbyRaid($$0, 9216);
    }

    public boolean isRaided(BlockPos $$0) {
        return this.getRaidAt($$0) != null;
    }

    public void onReputationEvent(ReputationEventType $$0, Entity $$1, ReputationEventHandler $$2) {
        $$2.onReputationEventFrom($$0, $$1);
    }

    public void saveDebugReport(Path $$0) throws IOException {
        ChunkMap $$1 = this.getChunkSource().chunkMap;
        try (BufferedWriter $$2 = Files.newBufferedWriter((Path)$$0.resolve("stats.txt"), (OpenOption[])new OpenOption[0]);){
            $$2.write(String.format((Locale)Locale.ROOT, (String)"spawning_chunks: %d\n", (Object[])new Object[]{$$1.getDistanceManager().getNaturalSpawnChunkCount()}));
            NaturalSpawner.SpawnState $$3 = this.getChunkSource().getLastSpawnState();
            if ($$3 != null) {
                for (Object2IntMap.Entry $$4 : $$3.getMobCategoryCounts().object2IntEntrySet()) {
                    $$2.write(String.format((Locale)Locale.ROOT, (String)"spawn_count.%s: %d\n", (Object[])new Object[]{((MobCategory)$$4.getKey()).getName(), $$4.getIntValue()}));
                }
            }
            $$2.write(String.format((Locale)Locale.ROOT, (String)"entities: %s\n", (Object[])new Object[]{this.entityManager.gatherStats()}));
            $$2.write(String.format((Locale)Locale.ROOT, (String)"block_entity_tickers: %d\n", (Object[])new Object[]{this.blockEntityTickers.size()}));
            $$2.write(String.format((Locale)Locale.ROOT, (String)"block_ticks: %d\n", (Object[])new Object[]{((LevelTicks)this.getBlockTicks()).count()}));
            $$2.write(String.format((Locale)Locale.ROOT, (String)"fluid_ticks: %d\n", (Object[])new Object[]{((LevelTicks)this.getFluidTicks()).count()}));
            $$2.write("distance_manager: " + $$1.getDistanceManager().getDebugStatus() + "\n");
            $$2.write(String.format((Locale)Locale.ROOT, (String)"pending_tasks: %d\n", (Object[])new Object[]{this.getChunkSource().getPendingTasksCount()}));
        }
        CrashReport $$5 = new CrashReport("Level dump", new Exception("dummy"));
        this.fillReportDetails($$5);
        try (BufferedWriter $$6 = Files.newBufferedWriter((Path)$$0.resolve("example_crash.txt"), (OpenOption[])new OpenOption[0]);){
            $$6.write($$5.getFriendlyReport());
        }
        Path $$7 = $$0.resolve("chunks.csv");
        try (BufferedWriter $$8 = Files.newBufferedWriter((Path)$$7, (OpenOption[])new OpenOption[0]);){
            $$1.dumpChunks((Writer)$$8);
        }
        Path $$9 = $$0.resolve("entity_chunks.csv");
        try (BufferedWriter $$10 = Files.newBufferedWriter((Path)$$9, (OpenOption[])new OpenOption[0]);){
            this.entityManager.dumpSections((Writer)$$10);
        }
        Path $$11 = $$0.resolve("entities.csv");
        try (BufferedWriter $$12 = Files.newBufferedWriter((Path)$$11, (OpenOption[])new OpenOption[0]);){
            ServerLevel.dumpEntities((Writer)$$12, this.getEntities().getAll());
        }
        Path $$13 = $$0.resolve("block_entities.csv");
        try (BufferedWriter $$14 = Files.newBufferedWriter((Path)$$13, (OpenOption[])new OpenOption[0]);){
            this.dumpBlockEntityTickers((Writer)$$14);
        }
    }

    private static void dumpEntities(Writer $$0, Iterable<Entity> $$1) throws IOException {
        CsvOutput $$2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build($$0);
        for (Entity $$3 : $$1) {
            Component $$4 = $$3.getCustomName();
            Component $$5 = $$3.getDisplayName();
            $$2.writeRow($$3.getX(), $$3.getY(), $$3.getZ(), $$3.getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey($$3.getType()), $$3.isAlive(), $$5.getString(), $$4 != null ? $$4.getString() : null);
        }
    }

    private void dumpBlockEntityTickers(Writer $$0) throws IOException {
        CsvOutput $$1 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build($$0);
        for (TickingBlockEntity $$2 : this.blockEntityTickers) {
            BlockPos $$3 = $$2.getPos();
            $$1.writeRow($$3.getX(), $$3.getY(), $$3.getZ(), $$2.getType());
        }
    }

    @VisibleForTesting
    public void clearBlockEvents(BoundingBox $$0) {
        this.blockEvents.removeIf($$1 -> $$0.isInside($$1.pos()));
    }

    @Override
    public void blockUpdated(BlockPos $$0, Block $$1) {
        if (!this.isDebug()) {
            this.updateNeighborsAt($$0, $$1);
        }
    }

    @Override
    public float getShade(Direction $$0, boolean $$1) {
        return 1.0f;
    }

    public Iterable<Entity> getAllEntities() {
        return this.getEntities().getAll();
    }

    public String toString() {
        return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
    }

    public boolean isFlat() {
        return this.server.getWorldData().isFlatWorld();
    }

    @Override
    public long getSeed() {
        return this.server.getWorldData().worldGenOptions().seed();
    }

    @Nullable
    public EndDragonFight dragonFight() {
        return this.dragonFight;
    }

    @Override
    public ServerLevel getLevel() {
        return this;
    }

    @VisibleForTesting
    public String getWatchdogStats() {
        return String.format((Locale)Locale.ROOT, (String)"players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", (Object[])new Object[]{this.players.size(), this.entityManager.gatherStats(), ServerLevel.getTypeCount(this.entityManager.getEntityGetter().getAll(), $$0 -> BuiltInRegistries.ENTITY_TYPE.getKey($$0.getType()).toString()), this.blockEntityTickers.size(), ServerLevel.getTypeCount(this.blockEntityTickers, TickingBlockEntity::getType), ((LevelTicks)this.getBlockTicks()).count(), ((LevelTicks)this.getFluidTicks()).count(), this.gatherChunkSourceStats()});
    }

    private static <T> String getTypeCount(Iterable<T> $$02, Function<T, String> $$1) {
        try {
            Object2IntOpenHashMap $$2 = new Object2IntOpenHashMap();
            for (Object $$3 : $$02) {
                String $$4 = (String)$$1.apply($$3);
                $$2.addTo((Object)$$4, 1);
            }
            return (String)$$2.object2IntEntrySet().stream().sorted(Comparator.comparing(Object2IntMap.Entry::getIntValue).reversed()).limit(5L).map($$0 -> (String)$$0.getKey() + ":" + $$0.getIntValue()).collect(Collectors.joining((CharSequence)","));
        }
        catch (Exception $$5) {
            return "";
        }
    }

    public static void makeObsidianPlatform(ServerLevel $$0) {
        BlockPos $$12 = END_SPAWN_POINT;
        int $$2 = $$12.getX();
        int $$3 = $$12.getY() - 2;
        int $$4 = $$12.getZ();
        BlockPos.betweenClosed($$2 - 2, $$3 + 1, $$4 - 2, $$2 + 2, $$3 + 3, $$4 + 2).forEach($$1 -> $$0.setBlockAndUpdate((BlockPos)$$1, Blocks.AIR.defaultBlockState()));
        BlockPos.betweenClosed($$2 - 2, $$3, $$4 - 2, $$2 + 2, $$3, $$4 + 2).forEach($$1 -> $$0.setBlockAndUpdate((BlockPos)$$1, Blocks.OBSIDIAN.defaultBlockState()));
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return this.entityManager.getEntityGetter();
    }

    public void addLegacyChunkEntities(Stream<Entity> $$0) {
        this.entityManager.addLegacyChunkEntities($$0);
    }

    public void addWorldGenChunkEntities(Stream<Entity> $$0) {
        this.entityManager.addWorldGenChunkEntities($$0);
    }

    public void startTickingChunk(LevelChunk $$0) {
        $$0.unpackTicks(this.getLevelData().getGameTime());
    }

    public void onStructureStartsAvailable(ChunkAccess $$0) {
        this.server.execute(() -> this.structureCheck.onStructureLoad($$0.getPos(), $$0.getAllStarts()));
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.entityManager.close();
    }

    @Override
    public String gatherChunkSourceStats() {
        return "Chunks[S] W: " + this.chunkSource.gatherStats() + " E: " + this.entityManager.gatherStats();
    }

    public boolean areEntitiesLoaded(long $$0) {
        return this.entityManager.areEntitiesLoaded($$0);
    }

    private boolean isPositionTickingWithEntitiesLoaded(long $$0) {
        return this.areEntitiesLoaded($$0) && this.chunkSource.isPositionTicking($$0);
    }

    public boolean isPositionEntityTicking(BlockPos $$0) {
        return this.entityManager.canPositionTick($$0) && this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(ChunkPos.asLong($$0));
    }

    public boolean isNaturalSpawningAllowed(BlockPos $$0) {
        return this.entityManager.canPositionTick($$0);
    }

    public boolean isNaturalSpawningAllowed(ChunkPos $$0) {
        return this.entityManager.canPositionTick($$0);
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.server.getWorldData().enabledFeatures();
    }

    final class EntityCallbacks
    implements LevelCallback<Entity> {
        EntityCallbacks() {
        }

        @Override
        public void onSectionChange(Entity $$0) {
        }

        @Override
        public void onDestroyed(Entity $$0) {
            ServerLevel.this.getScoreboard().entityRemoved($$0);
        }

        @Override
        public void onTrackingStart(Entity $$0) {
            ServerLevel.this.entityTickList.add($$0);
        }

        @Override
        public void onTickingEnd(Entity $$0) {
            ServerLevel.this.entityTickList.remove($$0);
        }

        @Override
        public void onTrackingStart(Entity $$0) {
            ServerLevel.this.getChunkSource().addEntity($$0);
            if ($$0 instanceof ServerPlayer) {
                ServerPlayer $$1 = (ServerPlayer)$$0;
                ServerLevel.this.players.add((Object)$$1);
                ServerLevel.this.updateSleepingPlayerList();
            }
            if ($$0 instanceof Mob) {
                Mob $$2 = (Mob)$$0;
                if (ServerLevel.this.isUpdatingNavigations) {
                    String $$3 = "onTrackingStart called during navigation iteration";
                    Util.logAndPauseIfInIde("onTrackingStart called during navigation iteration", (Throwable)new IllegalStateException("onTrackingStart called during navigation iteration"));
                }
                ServerLevel.this.navigatingMobs.add((Object)$$2);
            }
            if ($$0 instanceof EnderDragon) {
                EnderDragon $$4 = (EnderDragon)$$0;
                for (EnderDragonPart $$5 : $$4.getSubEntities()) {
                    ServerLevel.this.dragonParts.put($$5.getId(), (Object)$$5);
                }
            }
            $$0.updateDynamicGameEventListener(DynamicGameEventListener::add);
        }

        @Override
        public void onDestroyed(Entity $$0) {
            ServerLevel.this.getChunkSource().removeEntity($$0);
            if ($$0 instanceof ServerPlayer) {
                ServerPlayer $$1 = (ServerPlayer)$$0;
                ServerLevel.this.players.remove((Object)$$1);
                ServerLevel.this.updateSleepingPlayerList();
            }
            if ($$0 instanceof Mob) {
                Mob $$2 = (Mob)$$0;
                if (ServerLevel.this.isUpdatingNavigations) {
                    String $$3 = "onTrackingStart called during navigation iteration";
                    Util.logAndPauseIfInIde("onTrackingStart called during navigation iteration", (Throwable)new IllegalStateException("onTrackingStart called during navigation iteration"));
                }
                ServerLevel.this.navigatingMobs.remove((Object)$$2);
            }
            if ($$0 instanceof EnderDragon) {
                EnderDragon $$4 = (EnderDragon)$$0;
                for (EnderDragonPart $$5 : $$4.getSubEntities()) {
                    ServerLevel.this.dragonParts.remove($$5.getId());
                }
            }
            $$0.updateDynamicGameEventListener(DynamicGameEventListener::remove);
        }

        @Override
        public void onSectionChange(Entity $$0) {
            $$0.updateDynamicGameEventListener(DynamicGameEventListener::move);
        }
    }
}