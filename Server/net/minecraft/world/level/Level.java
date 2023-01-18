/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  java.io.IOException
 *  java.lang.AutoCloseable
 *  java.lang.Deprecated
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.lang.UnsupportedOperationException
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;

public abstract class Level
implements LevelAccessor,
AutoCloseable {
    public static final Codec<ResourceKey<Level>> RESOURCE_KEY_CODEC = ResourceKey.codec(Registries.DIMENSION);
    public static final ResourceKey<Level> OVERWORLD = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("overworld"));
    public static final ResourceKey<Level> NETHER = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_nether"));
    public static final ResourceKey<Level> END = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_end"));
    public static final int MAX_LEVEL_SIZE = 30000000;
    public static final int LONG_PARTICLE_CLIP_RANGE = 512;
    public static final int SHORT_PARTICLE_CLIP_RANGE = 32;
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final int MAX_BRIGHTNESS = 15;
    public static final int TICKS_PER_DAY = 24000;
    public static final int MAX_ENTITY_SPAWN_Y = 20000000;
    public static final int MIN_ENTITY_SPAWN_Y = -20000000;
    protected final List<TickingBlockEntity> blockEntityTickers = Lists.newArrayList();
    protected final NeighborUpdater neighborUpdater;
    private final List<TickingBlockEntity> pendingBlockEntityTickers = Lists.newArrayList();
    private boolean tickingBlockEntities;
    private final Thread thread;
    private final boolean isDebug;
    private int skyDarken;
    protected int randValue = RandomSource.create().nextInt();
    protected final int addend = 1013904223;
    protected float oRainLevel;
    protected float rainLevel;
    protected float oThunderLevel;
    protected float thunderLevel;
    public final RandomSource random = RandomSource.create();
    @Deprecated
    private final RandomSource threadSafeRandom = RandomSource.createThreadSafe();
    private final ResourceKey<DimensionType> dimensionTypeId;
    private final Holder<DimensionType> dimensionTypeRegistration;
    protected final WritableLevelData levelData;
    private final Supplier<ProfilerFiller> profiler;
    public final boolean isClientSide;
    private final WorldBorder worldBorder;
    private final BiomeManager biomeManager;
    private final ResourceKey<Level> dimension;
    private long subTickCount;

    protected Level(WritableLevelData $$0, ResourceKey<Level> $$1, Holder<DimensionType> $$2, Supplier<ProfilerFiller> $$3, boolean $$4, boolean $$5, long $$6, int $$7) {
        this.profiler = $$3;
        this.levelData = $$0;
        this.dimensionTypeRegistration = $$2;
        this.dimensionTypeId = (ResourceKey)$$2.unwrapKey().orElseThrow(() -> new IllegalArgumentException("Dimension must be registered, got " + $$2));
        final DimensionType $$8 = $$2.value();
        this.dimension = $$1;
        this.isClientSide = $$4;
        this.worldBorder = $$8.coordinateScale() != 1.0 ? new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / $$8.coordinateScale();
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / $$8.coordinateScale();
            }
        } : new WorldBorder();
        this.thread = Thread.currentThread();
        this.biomeManager = new BiomeManager(this, $$6);
        this.isDebug = $$5;
        this.neighborUpdater = new CollectingNeighborUpdater(this, $$7);
    }

    @Override
    public boolean isClientSide() {
        return this.isClientSide;
    }

    @Override
    @Nullable
    public MinecraftServer getServer() {
        return null;
    }

    public boolean isInWorldBounds(BlockPos $$0) {
        return !this.isOutsideBuildHeight($$0) && Level.isInWorldBoundsHorizontal($$0);
    }

    public static boolean isInSpawnableBounds(BlockPos $$0) {
        return !Level.isOutsideSpawnableHeight($$0.getY()) && Level.isInWorldBoundsHorizontal($$0);
    }

    private static boolean isInWorldBoundsHorizontal(BlockPos $$0) {
        return $$0.getX() >= -30000000 && $$0.getZ() >= -30000000 && $$0.getX() < 30000000 && $$0.getZ() < 30000000;
    }

    private static boolean isOutsideSpawnableHeight(int $$0) {
        return $$0 < -20000000 || $$0 >= 20000000;
    }

    public LevelChunk getChunkAt(BlockPos $$0) {
        return this.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
    }

    @Override
    public LevelChunk getChunk(int $$0, int $$1) {
        return (LevelChunk)this.getChunk($$0, $$1, ChunkStatus.FULL);
    }

    @Override
    @Nullable
    public ChunkAccess getChunk(int $$0, int $$1, ChunkStatus $$2, boolean $$3) {
        ChunkAccess $$4 = this.getChunkSource().getChunk($$0, $$1, $$2, $$3);
        if ($$4 == null && $$3) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return $$4;
    }

    @Override
    public boolean setBlock(BlockPos $$0, BlockState $$1, int $$2) {
        return this.setBlock($$0, $$1, $$2, 512);
    }

    @Override
    public boolean setBlock(BlockPos $$0, BlockState $$1, int $$2, int $$3) {
        if (this.isOutsideBuildHeight($$0)) {
            return false;
        }
        if (!this.isClientSide && this.isDebug()) {
            return false;
        }
        LevelChunk $$4 = this.getChunkAt($$0);
        Block $$5 = $$1.getBlock();
        BlockState $$6 = $$4.setBlockState($$0, $$1, ($$2 & 0x40) != 0);
        if ($$6 != null) {
            BlockState $$7 = this.getBlockState($$0);
            if (($$2 & 0x80) == 0 && $$7 != $$6 && ($$7.getLightBlock(this, $$0) != $$6.getLightBlock(this, $$0) || $$7.getLightEmission() != $$6.getLightEmission() || $$7.useShapeForLightOcclusion() || $$6.useShapeForLightOcclusion())) {
                this.getProfiler().push("queueCheckLight");
                this.getChunkSource().getLightEngine().checkBlock($$0);
                this.getProfiler().pop();
            }
            if ($$7 == $$1) {
                if ($$6 != $$7) {
                    this.setBlocksDirty($$0, $$6, $$7);
                }
                if (($$2 & 2) != 0 && (!this.isClientSide || ($$2 & 4) == 0) && (this.isClientSide || $$4.getFullStatus() != null && $$4.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING))) {
                    this.sendBlockUpdated($$0, $$6, $$1, $$2);
                }
                if (($$2 & 1) != 0) {
                    this.blockUpdated($$0, $$6.getBlock());
                    if (!this.isClientSide && $$1.hasAnalogOutputSignal()) {
                        this.updateNeighbourForOutputSignal($$0, $$5);
                    }
                }
                if (($$2 & 0x10) == 0 && $$3 > 0) {
                    int $$8 = $$2 & 0xFFFFFFDE;
                    $$6.updateIndirectNeighbourShapes(this, $$0, $$8, $$3 - 1);
                    $$1.updateNeighbourShapes(this, $$0, $$8, $$3 - 1);
                    $$1.updateIndirectNeighbourShapes(this, $$0, $$8, $$3 - 1);
                }
                this.onBlockStateChange($$0, $$6, $$7);
            }
            return true;
        }
        return false;
    }

    public void onBlockStateChange(BlockPos $$0, BlockState $$1, BlockState $$2) {
    }

    @Override
    public boolean removeBlock(BlockPos $$0, boolean $$1) {
        FluidState $$2 = this.getFluidState($$0);
        return this.setBlock($$0, $$2.createLegacyBlock(), 3 | ($$1 ? 64 : 0));
    }

    @Override
    public boolean destroyBlock(BlockPos $$0, boolean $$1, @Nullable Entity $$2, int $$3) {
        boolean $$7;
        BlockState $$4 = this.getBlockState($$0);
        if ($$4.isAir()) {
            return false;
        }
        FluidState $$5 = this.getFluidState($$0);
        if (!($$4.getBlock() instanceof BaseFireBlock)) {
            this.levelEvent(2001, $$0, Block.getId($$4));
        }
        if ($$1) {
            BlockEntity $$6 = $$4.hasBlockEntity() ? this.getBlockEntity($$0) : null;
            Block.dropResources($$4, this, $$0, $$6, $$2, ItemStack.EMPTY);
        }
        if ($$7 = this.setBlock($$0, $$5.createLegacyBlock(), 3, $$3)) {
            this.gameEvent(GameEvent.BLOCK_DESTROY, $$0, GameEvent.Context.of($$2, $$4));
        }
        return $$7;
    }

    public void addDestroyBlockEffect(BlockPos $$0, BlockState $$1) {
    }

    public boolean setBlockAndUpdate(BlockPos $$0, BlockState $$1) {
        return this.setBlock($$0, $$1, 3);
    }

    public abstract void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4);

    public void setBlocksDirty(BlockPos $$0, BlockState $$1, BlockState $$2) {
    }

    public void updateNeighborsAt(BlockPos $$0, Block $$1) {
    }

    public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, Direction $$2) {
    }

    public void neighborChanged(BlockPos $$0, Block $$1, BlockPos $$2) {
    }

    public void neighborChanged(BlockState $$0, BlockPos $$1, Block $$2, BlockPos $$3, boolean $$4) {
    }

    @Override
    public void neighborShapeChanged(Direction $$0, BlockState $$1, BlockPos $$2, BlockPos $$3, int $$4, int $$5) {
        this.neighborUpdater.shapeUpdate($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public int getHeight(Heightmap.Types $$0, int $$1, int $$2) {
        int $$5;
        if ($$1 < -30000000 || $$2 < -30000000 || $$1 >= 30000000 || $$2 >= 30000000) {
            int $$3 = this.getSeaLevel() + 1;
        } else if (this.hasChunk(SectionPos.blockToSectionCoord($$1), SectionPos.blockToSectionCoord($$2))) {
            int $$4 = this.getChunk(SectionPos.blockToSectionCoord($$1), SectionPos.blockToSectionCoord($$2)).getHeight($$0, $$1 & 0xF, $$2 & 0xF) + 1;
        } else {
            $$5 = this.getMinBuildHeight();
        }
        return $$5;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.getChunkSource().getLightEngine();
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        if (this.isOutsideBuildHeight($$0)) {
            return Blocks.VOID_AIR.defaultBlockState();
        }
        LevelChunk $$1 = this.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
        return $$1.getBlockState($$0);
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        if (this.isOutsideBuildHeight($$0)) {
            return Fluids.EMPTY.defaultFluidState();
        }
        LevelChunk $$1 = this.getChunkAt($$0);
        return $$1.getFluidState($$0);
    }

    public boolean isDay() {
        return !this.dimensionType().hasFixedTime() && this.skyDarken < 4;
    }

    public boolean isNight() {
        return !this.dimensionType().hasFixedTime() && !this.isDay();
    }

    public void playSound(@Nullable Entity $$0, BlockPos $$1, SoundEvent $$2, SoundSource $$3, float $$4, float $$5) {
        Player $$6;
        this.playSound($$0 instanceof Player ? ($$6 = (Player)$$0) : null, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void playSound(@Nullable Player $$0, BlockPos $$1, SoundEvent $$2, SoundSource $$3, float $$4, float $$5) {
        this.playSound($$0, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, $$2, $$3, $$4, $$5);
    }

    public abstract void playSeededSound(@Nullable Player var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12);

    public void playSeededSound(@Nullable Player $$0, double $$1, double $$2, double $$3, SoundEvent $$4, SoundSource $$5, float $$6, float $$7, long $$8) {
        this.playSeededSound($$0, $$1, $$2, $$3, BuiltInRegistries.SOUND_EVENT.wrapAsHolder($$4), $$5, $$6, $$7, $$8);
    }

    public abstract void playSeededSound(@Nullable Player var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7);

    public void playSound(@Nullable Player $$0, double $$1, double $$2, double $$3, SoundEvent $$4, SoundSource $$5, float $$6, float $$7) {
        this.playSeededSound($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.threadSafeRandom.nextLong());
    }

    public void playSound(@Nullable Player $$0, Entity $$1, SoundEvent $$2, SoundSource $$3, float $$4, float $$5) {
        this.playSeededSound($$0, $$1, BuiltInRegistries.SOUND_EVENT.wrapAsHolder($$2), $$3, $$4, $$5, this.threadSafeRandom.nextLong());
    }

    public void playLocalSound(BlockPos $$0, SoundEvent $$1, SoundSource $$2, float $$3, float $$4, boolean $$5) {
        this.playLocalSound((double)$$0.getX() + 0.5, (double)$$0.getY() + 0.5, (double)$$0.getZ() + 0.5, $$1, $$2, $$3, $$4, $$5);
    }

    public void playLocalSound(double $$0, double $$1, double $$2, SoundEvent $$3, SoundSource $$4, float $$5, float $$6, boolean $$7) {
    }

    @Override
    public void addParticle(ParticleOptions $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
    }

    public void addParticle(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
    }

    public void addAlwaysVisibleParticle(ParticleOptions $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
    }

    public void addAlwaysVisibleParticle(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
    }

    public float getSunAngle(float $$0) {
        float $$1 = this.getTimeOfDay($$0);
        return $$1 * ((float)Math.PI * 2);
    }

    public void addBlockEntityTicker(TickingBlockEntity $$0) {
        (this.tickingBlockEntities ? this.pendingBlockEntityTickers : this.blockEntityTickers).add((Object)$$0);
    }

    protected void tickBlockEntities() {
        ProfilerFiller $$0 = this.getProfiler();
        $$0.push("blockEntities");
        this.tickingBlockEntities = true;
        if (!this.pendingBlockEntityTickers.isEmpty()) {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }
        Iterator $$1 = this.blockEntityTickers.iterator();
        while ($$1.hasNext()) {
            TickingBlockEntity $$2 = (TickingBlockEntity)$$1.next();
            if ($$2.isRemoved()) {
                $$1.remove();
                continue;
            }
            if (!this.shouldTickBlocksAt($$2.getPos())) continue;
            $$2.tick();
        }
        this.tickingBlockEntities = false;
        $$0.pop();
    }

    public <T extends Entity> void guardEntityTick(Consumer<T> $$0, T $$1) {
        try {
            $$0.accept($$1);
        }
        catch (Throwable $$2) {
            CrashReport $$3 = CrashReport.forThrowable($$2, "Ticking entity");
            CrashReportCategory $$4 = $$3.addCategory("Entity being ticked");
            $$1.fillCrashReportCategory($$4);
            throw new ReportedException($$3);
        }
    }

    public boolean shouldTickDeath(Entity $$0) {
        return true;
    }

    public boolean shouldTickBlocksAt(long $$0) {
        return true;
    }

    public boolean shouldTickBlocksAt(BlockPos $$0) {
        return this.shouldTickBlocksAt(ChunkPos.asLong($$0));
    }

    public Explosion explode(@Nullable Entity $$0, double $$1, double $$2, double $$3, float $$4, ExplosionInteraction $$5) {
        return this.explode($$0, null, null, $$1, $$2, $$3, $$4, false, $$5);
    }

    public Explosion explode(@Nullable Entity $$0, double $$1, double $$2, double $$3, float $$4, boolean $$5, ExplosionInteraction $$6) {
        return this.explode($$0, null, null, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public Explosion explode(@Nullable Entity $$0, @Nullable DamageSource $$1, @Nullable ExplosionDamageCalculator $$2, Vec3 $$3, float $$4, boolean $$5, ExplosionInteraction $$6) {
        return this.explode($$0, $$1, $$2, $$3.x(), $$3.y(), $$3.z(), $$4, $$5, $$6);
    }

    public Explosion explode(@Nullable Entity $$0, @Nullable DamageSource $$1, @Nullable ExplosionDamageCalculator $$2, double $$3, double $$4, double $$5, float $$6, boolean $$7, ExplosionInteraction $$8) {
        return this.explode($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, true);
    }

    public Explosion explode(@Nullable Entity $$0, @Nullable DamageSource $$1, @Nullable ExplosionDamageCalculator $$2, double $$3, double $$4, double $$5, float $$6, boolean $$7, ExplosionInteraction $$8, boolean $$9) {
        Explosion.BlockInteraction $$10 = switch ($$8) {
            default -> throw new IncompatibleClassChangeError();
            case ExplosionInteraction.NONE -> Explosion.BlockInteraction.KEEP;
            case ExplosionInteraction.BLOCK -> this.getDestroyType(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
            case ExplosionInteraction.MOB -> {
                if (this.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    yield this.getDestroyType(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY);
                }
                yield Explosion.BlockInteraction.KEEP;
            }
            case ExplosionInteraction.TNT -> this.getDestroyType(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
        };
        Explosion $$11 = new Explosion(this, $$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$10);
        $$11.explode();
        $$11.finalizeExplosion($$9);
        return $$11;
    }

    private Explosion.BlockInteraction getDestroyType(GameRules.Key<GameRules.BooleanValue> $$0) {
        return this.getGameRules().getBoolean($$0) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
    }

    public abstract String gatherChunkSourceStats();

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        if (this.isOutsideBuildHeight($$0)) {
            return null;
        }
        if (!this.isClientSide && Thread.currentThread() != this.thread) {
            return null;
        }
        return this.getChunkAt($$0).getBlockEntity($$0, LevelChunk.EntityCreationType.IMMEDIATE);
    }

    public void setBlockEntity(BlockEntity $$0) {
        BlockPos $$1 = $$0.getBlockPos();
        if (this.isOutsideBuildHeight($$1)) {
            return;
        }
        this.getChunkAt($$1).addAndRegisterBlockEntity($$0);
    }

    public void removeBlockEntity(BlockPos $$0) {
        if (this.isOutsideBuildHeight($$0)) {
            return;
        }
        this.getChunkAt($$0).removeBlockEntity($$0);
    }

    public boolean isLoaded(BlockPos $$0) {
        if (this.isOutsideBuildHeight($$0)) {
            return false;
        }
        return this.getChunkSource().hasChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
    }

    public boolean loadedAndEntityCanStandOnFace(BlockPos $$0, Entity $$1, Direction $$2) {
        if (this.isOutsideBuildHeight($$0)) {
            return false;
        }
        ChunkAccess $$3 = this.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()), ChunkStatus.FULL, false);
        if ($$3 == null) {
            return false;
        }
        return $$3.getBlockState($$0).entityCanStandOnFace(this, $$0, $$1, $$2);
    }

    public boolean loadedAndEntityCanStandOn(BlockPos $$0, Entity $$1) {
        return this.loadedAndEntityCanStandOnFace($$0, $$1, Direction.UP);
    }

    public void updateSkyBrightness() {
        double $$0 = 1.0 - (double)(this.getRainLevel(1.0f) * 5.0f) / 16.0;
        double $$1 = 1.0 - (double)(this.getThunderLevel(1.0f) * 5.0f) / 16.0;
        double $$2 = 0.5 + 2.0 * Mth.clamp((double)Mth.cos(this.getTimeOfDay(1.0f) * ((float)Math.PI * 2)), -0.25, 0.25);
        this.skyDarken = (int)((1.0 - $$2 * $$0 * $$1) * 11.0);
    }

    public void setSpawnSettings(boolean $$0, boolean $$1) {
        this.getChunkSource().setSpawnSettings($$0, $$1);
    }

    public BlockPos getSharedSpawnPos() {
        BlockPos $$0 = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
        if (!this.getWorldBorder().isWithinBounds($$0)) {
            $$0 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return $$0;
    }

    public float getSharedSpawnAngle() {
        return this.levelData.getSpawnAngle();
    }

    protected void prepareWeather() {
        if (this.levelData.isRaining()) {
            this.rainLevel = 1.0f;
            if (this.levelData.isThundering()) {
                this.thunderLevel = 1.0f;
            }
        }
    }

    public void close() throws IOException {
        this.getChunkSource().close();
    }

    @Override
    @Nullable
    public BlockGetter getChunkForCollisions(int $$0, int $$1) {
        return this.getChunk($$0, $$1, ChunkStatus.FULL, false);
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity $$0, AABB $$1, Predicate<? super Entity> $$2) {
        this.getProfiler().incrementCounter("getEntities");
        ArrayList $$3 = Lists.newArrayList();
        this.getEntities().get($$1, (Consumer<Entity>)((Consumer)arg_0 -> Level.lambda$getEntities$1($$0, $$2, (List)$$3, arg_0)));
        return $$3;
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> $$0, AABB $$1, Predicate<? super T> $$2) {
        ArrayList $$3 = Lists.newArrayList();
        this.getEntities($$0, $$1, $$2, (List<? super T>)$$3);
        return $$3;
    }

    public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, AABB $$1, Predicate<? super T> $$2, List<? super T> $$3) {
        this.getEntities($$0, $$1, $$2, $$3, Integer.MAX_VALUE);
    }

    public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, AABB $$1, Predicate<? super T> $$2, List<? super T> $$3, int $$42) {
        this.getProfiler().incrementCounter("getEntities");
        this.getEntities().get($$0, $$1, $$4 -> {
            if ($$2.test($$4)) {
                $$3.add($$4);
                if ($$3.size() >= $$42) {
                    return AbortableIterationConsumer.Continuation.ABORT;
                }
            }
            if ($$4 instanceof EnderDragon) {
                EnderDragon $$5 = (EnderDragon)$$4;
                for (EnderDragonPart $$6 : $$5.getSubEntities()) {
                    Entity $$7 = (Entity)$$0.tryCast($$6);
                    if ($$7 == null || !$$2.test((Object)$$7)) continue;
                    $$3.add((Object)$$7);
                    if ($$3.size() < $$42) continue;
                    return AbortableIterationConsumer.Continuation.ABORT;
                }
            }
            return AbortableIterationConsumer.Continuation.CONTINUE;
        });
    }

    @Nullable
    public abstract Entity getEntity(int var1);

    public void blockEntityChanged(BlockPos $$0) {
        if (this.hasChunkAt($$0)) {
            this.getChunkAt($$0).setUnsaved(true);
        }
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    public int getDirectSignalTo(BlockPos $$0) {
        int $$1 = 0;
        if (($$1 = Math.max((int)$$1, (int)this.getDirectSignal((BlockPos)$$0.below(), Direction.DOWN))) >= 15) {
            return $$1;
        }
        if (($$1 = Math.max((int)$$1, (int)this.getDirectSignal((BlockPos)$$0.above(), Direction.UP))) >= 15) {
            return $$1;
        }
        if (($$1 = Math.max((int)$$1, (int)this.getDirectSignal((BlockPos)$$0.north(), Direction.NORTH))) >= 15) {
            return $$1;
        }
        if (($$1 = Math.max((int)$$1, (int)this.getDirectSignal((BlockPos)$$0.south(), Direction.SOUTH))) >= 15) {
            return $$1;
        }
        if (($$1 = Math.max((int)$$1, (int)this.getDirectSignal((BlockPos)$$0.west(), Direction.WEST))) >= 15) {
            return $$1;
        }
        if (($$1 = Math.max((int)$$1, (int)this.getDirectSignal((BlockPos)$$0.east(), Direction.EAST))) >= 15) {
            return $$1;
        }
        return $$1;
    }

    public boolean hasSignal(BlockPos $$0, Direction $$1) {
        return this.getSignal($$0, $$1) > 0;
    }

    public int getSignal(BlockPos $$0, Direction $$1) {
        BlockState $$2 = this.getBlockState($$0);
        int $$3 = $$2.getSignal(this, $$0, $$1);
        if ($$2.isRedstoneConductor(this, $$0)) {
            return Math.max((int)$$3, (int)this.getDirectSignalTo($$0));
        }
        return $$3;
    }

    public boolean hasNeighborSignal(BlockPos $$0) {
        if (this.getSignal((BlockPos)$$0.below(), Direction.DOWN) > 0) {
            return true;
        }
        if (this.getSignal((BlockPos)$$0.above(), Direction.UP) > 0) {
            return true;
        }
        if (this.getSignal((BlockPos)$$0.north(), Direction.NORTH) > 0) {
            return true;
        }
        if (this.getSignal((BlockPos)$$0.south(), Direction.SOUTH) > 0) {
            return true;
        }
        if (this.getSignal((BlockPos)$$0.west(), Direction.WEST) > 0) {
            return true;
        }
        return this.getSignal((BlockPos)$$0.east(), Direction.EAST) > 0;
    }

    public int getBestNeighborSignal(BlockPos $$0) {
        int $$1 = 0;
        for (Direction $$2 : DIRECTIONS) {
            int $$3 = this.getSignal((BlockPos)$$0.relative($$2), $$2);
            if ($$3 >= 15) {
                return 15;
            }
            if ($$3 <= $$1) continue;
            $$1 = $$3;
        }
        return $$1;
    }

    public void disconnect() {
    }

    public long getGameTime() {
        return this.levelData.getGameTime();
    }

    public long getDayTime() {
        return this.levelData.getDayTime();
    }

    public boolean mayInteract(Player $$0, BlockPos $$1) {
        return true;
    }

    public void broadcastEntityEvent(Entity $$0, byte $$1) {
    }

    public void blockEvent(BlockPos $$0, Block $$1, int $$2, int $$3) {
        this.getBlockState($$0).triggerEvent(this, $$0, $$2, $$3);
    }

    @Override
    public LevelData getLevelData() {
        return this.levelData;
    }

    public GameRules getGameRules() {
        return this.levelData.getGameRules();
    }

    public float getThunderLevel(float $$0) {
        return Mth.lerp($$0, this.oThunderLevel, this.thunderLevel) * this.getRainLevel($$0);
    }

    public void setThunderLevel(float $$0) {
        float $$1;
        this.oThunderLevel = $$1 = Mth.clamp($$0, 0.0f, 1.0f);
        this.thunderLevel = $$1;
    }

    public float getRainLevel(float $$0) {
        return Mth.lerp($$0, this.oRainLevel, this.rainLevel);
    }

    public void setRainLevel(float $$0) {
        float $$1;
        this.oRainLevel = $$1 = Mth.clamp($$0, 0.0f, 1.0f);
        this.rainLevel = $$1;
    }

    public boolean isThundering() {
        if (!this.dimensionType().hasSkyLight() || this.dimensionType().hasCeiling()) {
            return false;
        }
        return (double)this.getThunderLevel(1.0f) > 0.9;
    }

    public boolean isRaining() {
        return (double)this.getRainLevel(1.0f) > 0.2;
    }

    public boolean isRainingAt(BlockPos $$0) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.canSeeSky($$0)) {
            return false;
        }
        if (this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$0).getY() > $$0.getY()) {
            return false;
        }
        Biome $$1 = (Biome)this.getBiome($$0).value();
        return $$1.getPrecipitation() == Biome.Precipitation.RAIN && $$1.warmEnoughToRain($$0);
    }

    public boolean isHumidAt(BlockPos $$0) {
        Biome $$1 = (Biome)this.getBiome($$0).value();
        return $$1.isHumid();
    }

    @Nullable
    public abstract MapItemSavedData getMapData(String var1);

    public abstract void setMapData(String var1, MapItemSavedData var2);

    public abstract int getFreeMapId();

    public void globalLevelEvent(int $$0, BlockPos $$1, int $$2) {
    }

    public CrashReportCategory fillReportDetails(CrashReport $$0) {
        CrashReportCategory $$1 = $$0.addCategory("Affected level", 1);
        $$1.setDetail("All players", () -> this.players().size() + " total; " + this.players());
        $$1.setDetail("Chunk stats", this.getChunkSource()::gatherStats);
        $$1.setDetail("Level dimension", () -> this.dimension().location().toString());
        try {
            this.levelData.fillCrashReportCategory($$1, this);
        }
        catch (Throwable $$2) {
            $$1.setDetailError("Level Data Unobtainable", $$2);
        }
        return $$1;
    }

    public abstract void destroyBlockProgress(int var1, BlockPos var2, int var3);

    public void createFireworks(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, @Nullable CompoundTag $$6) {
    }

    public abstract Scoreboard getScoreboard();

    public void updateNeighbourForOutputSignal(BlockPos $$0, Block $$1) {
        for (Direction $$2 : Direction.Plane.HORIZONTAL) {
            Vec3i $$3 = $$0.relative($$2);
            if (!this.hasChunkAt((BlockPos)$$3)) continue;
            BlockState $$4 = this.getBlockState((BlockPos)$$3);
            if ($$4.is(Blocks.COMPARATOR)) {
                this.neighborChanged($$4, (BlockPos)$$3, $$1, $$0, false);
                continue;
            }
            if (!$$4.isRedstoneConductor(this, (BlockPos)$$3) || !($$4 = this.getBlockState((BlockPos)($$3 = ((BlockPos)$$3).relative($$2)))).is(Blocks.COMPARATOR)) continue;
            this.neighborChanged($$4, (BlockPos)$$3, $$1, $$0, false);
        }
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos $$0) {
        long $$1 = 0L;
        float $$2 = 0.0f;
        if (this.hasChunkAt($$0)) {
            $$2 = this.getMoonBrightness();
            $$1 = this.getChunkAt($$0).getInhabitedTime();
        }
        return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), $$1, $$2);
    }

    @Override
    public int getSkyDarken() {
        return this.skyDarken;
    }

    public void setSkyFlashTime(int $$0) {
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public void sendPacketToServer(Packet<?> $$0) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Override
    public DimensionType dimensionType() {
        return this.dimensionTypeRegistration.value();
    }

    public ResourceKey<DimensionType> dimensionTypeId() {
        return this.dimensionTypeId;
    }

    public Holder<DimensionType> dimensionTypeRegistration() {
        return this.dimensionTypeRegistration;
    }

    public ResourceKey<Level> dimension() {
        return this.dimension;
    }

    @Override
    public RandomSource getRandom() {
        return this.random;
    }

    @Override
    public boolean isStateAtPosition(BlockPos $$0, Predicate<BlockState> $$1) {
        return $$1.test((Object)this.getBlockState($$0));
    }

    @Override
    public boolean isFluidAtPosition(BlockPos $$0, Predicate<FluidState> $$1) {
        return $$1.test((Object)this.getFluidState($$0));
    }

    public abstract RecipeManager getRecipeManager();

    public BlockPos getBlockRandomPos(int $$0, int $$1, int $$2, int $$3) {
        this.randValue = this.randValue * 3 + 1013904223;
        int $$4 = this.randValue >> 2;
        return new BlockPos($$0 + ($$4 & 0xF), $$1 + ($$4 >> 16 & $$3), $$2 + ($$4 >> 8 & 0xF));
    }

    public boolean noSave() {
        return false;
    }

    public ProfilerFiller getProfiler() {
        return (ProfilerFiller)this.profiler.get();
    }

    public Supplier<ProfilerFiller> getProfilerSupplier() {
        return this.profiler;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    public final boolean isDebug() {
        return this.isDebug;
    }

    protected abstract LevelEntityGetter<Entity> getEntities();

    @Override
    public long nextSubTickCount() {
        return this.subTickCount++;
    }

    private static /* synthetic */ void lambda$getEntities$1(Entity $$0, Predicate $$1, List $$2, Entity $$3) {
        if ($$3 != $$0 && $$1.test((Object)$$3)) {
            $$2.add((Object)$$3);
        }
        if ($$3 instanceof EnderDragon) {
            for (EnderDragonPart $$4 : ((EnderDragon)$$3).getSubEntities()) {
                if ($$3 == $$0 || !$$1.test((Object)$$4)) continue;
                $$2.add((Object)$$4);
            }
        }
    }

    public static enum ExplosionInteraction {
        NONE,
        BLOCK,
        MOB,
        TNT;

    }
}