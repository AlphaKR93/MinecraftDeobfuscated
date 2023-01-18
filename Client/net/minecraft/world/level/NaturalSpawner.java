/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Throwable
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.PotentialCalculator;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public final class NaturalSpawner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MIN_SPAWN_DISTANCE = 24;
    public static final int SPAWN_DISTANCE_CHUNK = 8;
    public static final int SPAWN_DISTANCE_BLOCK = 128;
    static final int MAGIC_NUMBER = (int)Math.pow((double)17.0, (double)2.0);
    private static final MobCategory[] SPAWNING_CATEGORIES = (MobCategory[])Stream.of((Object[])MobCategory.values()).filter($$0 -> $$0 != MobCategory.MISC).toArray(MobCategory[]::new);

    private NaturalSpawner() {
    }

    public static SpawnState createState(int $$0, Iterable<Entity> $$1, ChunkGetter $$2, LocalMobCapCalculator $$3) {
        PotentialCalculator $$4 = new PotentialCalculator();
        Object2IntOpenHashMap $$5 = new Object2IntOpenHashMap();
        for (Entity $$62 : $$1) {
            MobCategory $$8;
            Mob $$7;
            if ($$62 instanceof Mob && (($$7 = (Mob)$$62).isPersistenceRequired() || $$7.requiresCustomPersistence()) || ($$8 = $$62.getType().getCategory()) == MobCategory.MISC) continue;
            BlockPos $$9 = $$62.blockPosition();
            $$2.query(ChunkPos.asLong($$9), (Consumer<LevelChunk>)((Consumer)$$6 -> {
                MobSpawnSettings.MobSpawnCost $$7 = NaturalSpawner.getRoughBiome($$9, $$6).getMobSettings().getMobSpawnCost($$62.getType());
                if ($$7 != null) {
                    $$4.addCharge($$62.blockPosition(), $$7.getCharge());
                }
                if ($$62 instanceof Mob) {
                    $$3.addMob($$6.getPos(), $$8);
                }
                $$5.addTo((Object)$$8, 1);
            }));
        }
        return new SpawnState($$0, (Object2IntOpenHashMap<MobCategory>)$$5, $$4, $$3);
    }

    static Biome getRoughBiome(BlockPos $$0, ChunkAccess $$1) {
        return $$1.getNoiseBiome(QuartPos.fromBlock($$0.getX()), QuartPos.fromBlock($$0.getY()), QuartPos.fromBlock($$0.getZ())).value();
    }

    public static void spawnForChunk(ServerLevel $$0, LevelChunk $$1, SpawnState $$2, boolean $$3, boolean $$4, boolean $$5) {
        $$0.getProfiler().push("spawner");
        for (MobCategory $$6 : SPAWNING_CATEGORIES) {
            if (!$$3 && $$6.isFriendly() || !$$4 && !$$6.isFriendly() || !$$5 && $$6.isPersistent() || !$$2.canSpawnForCategory($$6, $$1.getPos())) continue;
            NaturalSpawner.spawnCategoryForChunk($$6, $$0, $$1, $$2::canSpawn, $$2::afterSpawn);
        }
        $$0.getProfiler().pop();
    }

    public static void spawnCategoryForChunk(MobCategory $$0, ServerLevel $$1, LevelChunk $$2, SpawnPredicate $$3, AfterSpawnCallback $$4) {
        BlockPos $$5 = NaturalSpawner.getRandomPosWithin($$1, $$2);
        if ($$5.getY() < $$1.getMinBuildHeight() + 1) {
            return;
        }
        NaturalSpawner.spawnCategoryForPosition($$0, $$1, $$2, $$5, $$3, $$4);
    }

    @VisibleForDebug
    public static void spawnCategoryForPosition(MobCategory $$02, ServerLevel $$12, BlockPos $$22) {
        NaturalSpawner.spawnCategoryForPosition($$02, $$12, $$12.getChunk($$22), $$22, ($$0, $$1, $$2) -> true, ($$0, $$1) -> {});
    }

    public static void spawnCategoryForPosition(MobCategory $$0, ServerLevel $$1, ChunkAccess $$2, BlockPos $$3, SpawnPredicate $$4, AfterSpawnCallback $$5) {
        StructureManager $$6 = $$1.structureManager();
        ChunkGenerator $$7 = $$1.getChunkSource().getGenerator();
        int $$8 = $$3.getY();
        BlockState $$9 = $$2.getBlockState($$3);
        if ($$9.isRedstoneConductor($$2, $$3)) {
            return;
        }
        BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();
        int $$11 = 0;
        block0: for (int $$12 = 0; $$12 < 3; ++$$12) {
            int $$13 = $$3.getX();
            int $$14 = $$3.getZ();
            int $$15 = 6;
            MobSpawnSettings.SpawnerData $$16 = null;
            SpawnGroupData $$17 = null;
            int $$18 = Mth.ceil($$1.random.nextFloat() * 4.0f);
            int $$19 = 0;
            for (int $$20 = 0; $$20 < $$18; ++$$20) {
                double $$24;
                $$10.set($$13 += $$1.random.nextInt(6) - $$1.random.nextInt(6), $$8, $$14 += $$1.random.nextInt(6) - $$1.random.nextInt(6));
                double $$21 = (double)$$13 + 0.5;
                double $$22 = (double)$$14 + 0.5;
                Player $$23 = $$1.getNearestPlayer($$21, $$8, $$22, -1.0, false);
                if ($$23 == null || !NaturalSpawner.isRightDistanceToPlayerAndSpawnPoint($$1, $$2, $$10, $$24 = $$23.distanceToSqr($$21, $$8, $$22))) continue;
                if ($$16 == null) {
                    Optional<MobSpawnSettings.SpawnerData> $$25 = NaturalSpawner.getRandomSpawnMobAt($$1, $$6, $$7, $$0, $$1.random, $$10);
                    if ($$25.isEmpty()) continue block0;
                    $$16 = (MobSpawnSettings.SpawnerData)$$25.get();
                    $$18 = $$16.minCount + $$1.random.nextInt(1 + $$16.maxCount - $$16.minCount);
                }
                if (!NaturalSpawner.isValidSpawnPostitionForType($$1, $$0, $$6, $$7, $$16, $$10, $$24) || !$$4.test($$16.type, $$10, $$2)) continue;
                Mob $$26 = NaturalSpawner.getMobForSpawn($$1, $$16.type);
                if ($$26 == null) {
                    return;
                }
                $$26.moveTo($$21, $$8, $$22, $$1.random.nextFloat() * 360.0f, 0.0f);
                if (!NaturalSpawner.isValidPositionForMob($$1, $$26, $$24)) continue;
                $$17 = $$26.finalizeSpawn($$1, $$1.getCurrentDifficultyAt($$26.blockPosition()), MobSpawnType.NATURAL, $$17, null);
                ++$$19;
                $$1.addFreshEntityWithPassengers($$26);
                $$5.run($$26, $$2);
                if (++$$11 >= $$26.getMaxSpawnClusterSize()) {
                    return;
                }
                if ($$26.isMaxGroupSizeReached($$19)) continue block0;
            }
        }
    }

    private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel $$0, ChunkAccess $$1, BlockPos.MutableBlockPos $$2, double $$3) {
        if ($$3 <= 576.0) {
            return false;
        }
        if ($$0.getSharedSpawnPos().closerToCenterThan(new Vec3((double)$$2.getX() + 0.5, $$2.getY(), (double)$$2.getZ() + 0.5), 24.0)) {
            return false;
        }
        return Objects.equals((Object)new ChunkPos($$2), (Object)$$1.getPos()) || $$0.isNaturalSpawningAllowed($$2);
    }

    private static boolean isValidSpawnPostitionForType(ServerLevel $$0, MobCategory $$1, StructureManager $$2, ChunkGenerator $$3, MobSpawnSettings.SpawnerData $$4, BlockPos.MutableBlockPos $$5, double $$6) {
        EntityType<?> $$7 = $$4.type;
        if ($$7.getCategory() == MobCategory.MISC) {
            return false;
        }
        if (!$$7.canSpawnFarFromPlayer() && $$6 > (double)($$7.getCategory().getDespawnDistance() * $$7.getCategory().getDespawnDistance())) {
            return false;
        }
        if (!$$7.canSummon() || !NaturalSpawner.canSpawnMobAt($$0, $$2, $$3, $$1, $$4, $$5)) {
            return false;
        }
        SpawnPlacements.Type $$8 = SpawnPlacements.getPlacementType($$7);
        if (!NaturalSpawner.isSpawnPositionOk($$8, $$0, $$5, $$7)) {
            return false;
        }
        if (!SpawnPlacements.checkSpawnRules($$7, $$0, MobSpawnType.NATURAL, $$5, $$0.random)) {
            return false;
        }
        return $$0.noCollision($$7.getAABB((double)$$5.getX() + 0.5, $$5.getY(), (double)$$5.getZ() + 0.5));
    }

    @Nullable
    private static Mob getMobForSpawn(ServerLevel $$0, EntityType<?> $$1) {
        try {
            Object obj = $$1.create($$0);
            if (obj instanceof Mob) {
                Mob $$2 = (Mob)obj;
                return $$2;
            }
            LOGGER.warn("Can't spawn entity of type: {}", (Object)BuiltInRegistries.ENTITY_TYPE.getKey($$1));
        }
        catch (Exception $$3) {
            LOGGER.warn("Failed to create mob", (Throwable)$$3);
        }
        return null;
    }

    private static boolean isValidPositionForMob(ServerLevel $$0, Mob $$1, double $$2) {
        if ($$2 > (double)($$1.getType().getCategory().getDespawnDistance() * $$1.getType().getCategory().getDespawnDistance()) && $$1.removeWhenFarAway($$2)) {
            return false;
        }
        return $$1.checkSpawnRules($$0, MobSpawnType.NATURAL) && $$1.checkSpawnObstruction($$0);
    }

    private static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnMobAt(ServerLevel $$0, StructureManager $$1, ChunkGenerator $$2, MobCategory $$3, RandomSource $$4, BlockPos $$5) {
        Holder $$6 = $$0.getBiome($$5);
        if ($$3 == MobCategory.WATER_AMBIENT && $$6.is(BiomeTags.REDUCED_WATER_AMBIENT_SPAWNS) && $$4.nextFloat() < 0.98f) {
            return Optional.empty();
        }
        return NaturalSpawner.mobsAt($$0, $$1, $$2, $$3, $$5, $$6).getRandom($$4);
    }

    private static boolean canSpawnMobAt(ServerLevel $$0, StructureManager $$1, ChunkGenerator $$2, MobCategory $$3, MobSpawnSettings.SpawnerData $$4, BlockPos $$5) {
        return NaturalSpawner.mobsAt($$0, $$1, $$2, $$3, $$5, null).unwrap().contains((Object)$$4);
    }

    private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel $$0, StructureManager $$1, ChunkGenerator $$2, MobCategory $$3, BlockPos $$4, @Nullable Holder<Biome> $$5) {
        if (NaturalSpawner.isInNetherFortressBounds($$4, $$0, $$3, $$1)) {
            return NetherFortressStructure.FORTRESS_ENEMIES;
        }
        return $$2.getMobsAt($$5 != null ? $$5 : $$0.getBiome($$4), $$1, $$3, $$4);
    }

    public static boolean isInNetherFortressBounds(BlockPos $$0, ServerLevel $$1, MobCategory $$2, StructureManager $$3) {
        if ($$2 != MobCategory.MONSTER || !$$1.getBlockState((BlockPos)$$0.below()).is(Blocks.NETHER_BRICKS)) {
            return false;
        }
        Structure $$4 = $$3.registryAccess().registryOrThrow(Registries.STRUCTURE).get(BuiltinStructures.FORTRESS);
        if ($$4 == null) {
            return false;
        }
        return $$3.getStructureAt($$0, $$4).isValid();
    }

    private static BlockPos getRandomPosWithin(Level $$0, LevelChunk $$1) {
        ChunkPos $$2 = $$1.getPos();
        int $$3 = $$2.getMinBlockX() + $$0.random.nextInt(16);
        int $$4 = $$2.getMinBlockZ() + $$0.random.nextInt(16);
        int $$5 = $$1.getHeight(Heightmap.Types.WORLD_SURFACE, $$3, $$4) + 1;
        int $$6 = Mth.randomBetweenInclusive($$0.random, $$0.getMinBuildHeight(), $$5);
        return new BlockPos($$3, $$6, $$4);
    }

    public static boolean isValidEmptySpawnBlock(BlockGetter $$0, BlockPos $$1, BlockState $$2, FluidState $$3, EntityType<?> $$4) {
        if ($$2.isCollisionShapeFullBlock($$0, $$1)) {
            return false;
        }
        if ($$2.isSignalSource()) {
            return false;
        }
        if (!$$3.isEmpty()) {
            return false;
        }
        if ($$2.is(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
            return false;
        }
        return !$$4.isBlockDangerous($$2);
    }

    public static boolean isSpawnPositionOk(SpawnPlacements.Type $$0, LevelReader $$1, BlockPos $$2, @Nullable EntityType<?> $$3) {
        if ($$0 == SpawnPlacements.Type.NO_RESTRICTIONS) {
            return true;
        }
        if ($$3 == null || !$$1.getWorldBorder().isWithinBounds($$2)) {
            return false;
        }
        BlockState $$4 = $$1.getBlockState($$2);
        FluidState $$5 = $$1.getFluidState($$2);
        Vec3i $$6 = $$2.above();
        Vec3i $$7 = $$2.below();
        switch ($$0) {
            case IN_WATER: {
                return $$5.is(FluidTags.WATER) && !$$1.getBlockState((BlockPos)$$6).isRedstoneConductor($$1, (BlockPos)$$6);
            }
            case IN_LAVA: {
                return $$5.is(FluidTags.LAVA);
            }
        }
        BlockState $$8 = $$1.getBlockState((BlockPos)$$7);
        if (!$$8.isValidSpawn($$1, (BlockPos)$$7, $$3)) {
            return false;
        }
        return NaturalSpawner.isValidEmptySpawnBlock($$1, $$2, $$4, $$5, $$3) && NaturalSpawner.isValidEmptySpawnBlock($$1, (BlockPos)$$6, $$1.getBlockState((BlockPos)$$6), $$1.getFluidState((BlockPos)$$6), $$3);
    }

    /*
     * WARNING - void declaration
     */
    public static void spawnMobsForChunkGeneration(ServerLevelAccessor $$0, Holder<Biome> $$1, ChunkPos $$2, RandomSource $$3) {
        MobSpawnSettings $$4 = $$1.value().getMobSettings();
        WeightedRandomList<MobSpawnSettings.SpawnerData> $$5 = $$4.getMobs(MobCategory.CREATURE);
        if ($$5.isEmpty()) {
            return;
        }
        int $$6 = $$2.getMinBlockX();
        int $$7 = $$2.getMinBlockZ();
        while ($$3.nextFloat() < $$4.getCreatureProbability()) {
            Optional<MobSpawnSettings.SpawnerData> $$8 = $$5.getRandom($$3);
            if (!$$8.isPresent()) continue;
            MobSpawnSettings.SpawnerData $$9 = (MobSpawnSettings.SpawnerData)$$8.get();
            int $$10 = $$9.minCount + $$3.nextInt(1 + $$9.maxCount - $$9.minCount);
            SpawnGroupData $$11 = null;
            int $$12 = $$6 + $$3.nextInt(16);
            int $$13 = $$7 + $$3.nextInt(16);
            int $$14 = $$12;
            int $$15 = $$13;
            for (int $$16 = 0; $$16 < $$10; ++$$16) {
                boolean $$17 = false;
                for (int $$18 = 0; !$$17 && $$18 < 4; ++$$18) {
                    BlockPos $$19 = NaturalSpawner.getTopNonCollidingPos($$0, $$9.type, $$12, $$13);
                    if ($$9.type.canSummon() && NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType($$9.type), $$0, $$19, $$9.type)) {
                        Mob $$26;
                        void $$25;
                        float $$20 = $$9.type.getWidth();
                        double $$21 = Mth.clamp((double)$$12, (double)$$6 + (double)$$20, (double)$$6 + 16.0 - (double)$$20);
                        double $$22 = Mth.clamp((double)$$13, (double)$$7 + (double)$$20, (double)$$7 + 16.0 - (double)$$20);
                        if (!$$0.noCollision($$9.type.getAABB($$21, $$19.getY(), $$22)) || !SpawnPlacements.checkSpawnRules($$9.type, $$0, MobSpawnType.CHUNK_GENERATION, new BlockPos($$21, (double)$$19.getY(), $$22), $$0.getRandom())) continue;
                        try {
                            Object $$23 = $$9.type.create($$0.getLevel());
                        }
                        catch (Exception $$24) {
                            LOGGER.warn("Failed to create mob", (Throwable)$$24);
                            continue;
                        }
                        if ($$25 == null) continue;
                        $$25.moveTo($$21, $$19.getY(), $$22, $$3.nextFloat() * 360.0f, 0.0f);
                        if ($$25 instanceof Mob && ($$26 = (Mob)$$25).checkSpawnRules($$0, MobSpawnType.CHUNK_GENERATION) && $$26.checkSpawnObstruction($$0)) {
                            $$11 = $$26.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$26.blockPosition()), MobSpawnType.CHUNK_GENERATION, $$11, null);
                            $$0.addFreshEntityWithPassengers($$26);
                            $$17 = true;
                        }
                    }
                    $$12 += $$3.nextInt(5) - $$3.nextInt(5);
                    $$13 += $$3.nextInt(5) - $$3.nextInt(5);
                    while ($$12 < $$6 || $$12 >= $$6 + 16 || $$13 < $$7 || $$13 >= $$7 + 16) {
                        $$12 = $$14 + $$3.nextInt(5) - $$3.nextInt(5);
                        $$13 = $$15 + $$3.nextInt(5) - $$3.nextInt(5);
                    }
                }
            }
        }
    }

    private static BlockPos getTopNonCollidingPos(LevelReader $$0, EntityType<?> $$1, int $$2, int $$3) {
        Vec3i $$6;
        int $$4 = $$0.getHeight(SpawnPlacements.getHeightmapType($$1), $$2, $$3);
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos($$2, $$4, $$3);
        if ($$0.dimensionType().hasCeiling()) {
            do {
                $$5.move(Direction.DOWN);
            } while (!$$0.getBlockState($$5).isAir());
            do {
                $$5.move(Direction.DOWN);
            } while ($$0.getBlockState($$5).isAir() && $$5.getY() > $$0.getMinBuildHeight());
        }
        if (SpawnPlacements.getPlacementType($$1) == SpawnPlacements.Type.ON_GROUND && $$0.getBlockState((BlockPos)($$6 = $$5.below())).isPathfindable($$0, (BlockPos)$$6, PathComputationType.LAND)) {
            return $$6;
        }
        return $$5.immutable();
    }

    @FunctionalInterface
    public static interface ChunkGetter {
        public void query(long var1, Consumer<LevelChunk> var3);
    }

    public static class SpawnState {
        private final int spawnableChunkCount;
        private final Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
        private final PotentialCalculator spawnPotential;
        private final Object2IntMap<MobCategory> unmodifiableMobCategoryCounts;
        private final LocalMobCapCalculator localMobCapCalculator;
        @Nullable
        private BlockPos lastCheckedPos;
        @Nullable
        private EntityType<?> lastCheckedType;
        private double lastCharge;

        SpawnState(int $$0, Object2IntOpenHashMap<MobCategory> $$1, PotentialCalculator $$2, LocalMobCapCalculator $$3) {
            this.spawnableChunkCount = $$0;
            this.mobCategoryCounts = $$1;
            this.spawnPotential = $$2;
            this.localMobCapCalculator = $$3;
            this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable($$1);
        }

        private boolean canSpawn(EntityType<?> $$0, BlockPos $$1, ChunkAccess $$2) {
            double $$4;
            this.lastCheckedPos = $$1;
            this.lastCheckedType = $$0;
            MobSpawnSettings.MobSpawnCost $$3 = NaturalSpawner.getRoughBiome($$1, $$2).getMobSettings().getMobSpawnCost($$0);
            if ($$3 == null) {
                this.lastCharge = 0.0;
                return true;
            }
            this.lastCharge = $$4 = $$3.getCharge();
            double $$5 = this.spawnPotential.getPotentialEnergyChange($$1, $$4);
            return $$5 <= $$3.getEnergyBudget();
        }

        private void afterSpawn(Mob $$0, ChunkAccess $$1) {
            double $$7;
            EntityType<?> $$2 = $$0.getType();
            BlockPos $$3 = $$0.blockPosition();
            if ($$3.equals(this.lastCheckedPos) && $$2 == this.lastCheckedType) {
                double $$4 = this.lastCharge;
            } else {
                MobSpawnSettings.MobSpawnCost $$5 = NaturalSpawner.getRoughBiome($$3, $$1).getMobSettings().getMobSpawnCost($$2);
                if ($$5 != null) {
                    double $$6 = $$5.getCharge();
                } else {
                    $$7 = 0.0;
                }
            }
            this.spawnPotential.addCharge($$3, $$7);
            MobCategory $$8 = $$2.getCategory();
            this.mobCategoryCounts.addTo((Object)$$8, 1);
            this.localMobCapCalculator.addMob(new ChunkPos($$3), $$8);
        }

        public int getSpawnableChunkCount() {
            return this.spawnableChunkCount;
        }

        public Object2IntMap<MobCategory> getMobCategoryCounts() {
            return this.unmodifiableMobCategoryCounts;
        }

        boolean canSpawnForCategory(MobCategory $$0, ChunkPos $$1) {
            int $$2 = $$0.getMaxInstancesPerChunk() * this.spawnableChunkCount / MAGIC_NUMBER;
            if (this.mobCategoryCounts.getInt((Object)$$0) >= $$2) {
                return false;
            }
            return this.localMobCapCalculator.canSpawn($$0, $$1);
        }
    }

    @FunctionalInterface
    public static interface SpawnPredicate {
        public boolean test(EntityType<?> var1, BlockPos var2, ChunkAccess var3);
    }

    @FunctionalInterface
    public static interface AfterSpawnCallback {
        public void run(Mob var1, ChunkAccess var2);
    }
}