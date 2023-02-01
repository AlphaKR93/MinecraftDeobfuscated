/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Deque
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.BooleanSupplier
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  java.util.function.ToIntFunction
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.slf4j.Logger;

public class ClientLevel
extends Level {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final double FLUID_PARTICLE_SPAWN_OFFSET = 0.05;
    private static final int NORMAL_LIGHT_UPDATES_PER_FRAME = 10;
    private static final int LIGHT_UPDATE_QUEUE_SIZE_THRESHOLD = 1000;
    final EntityTickList tickingEntities = new EntityTickList();
    private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager<Entity>(Entity.class, new EntityCallbacks());
    private final ClientPacketListener connection;
    private final LevelRenderer levelRenderer;
    private final ClientLevelData clientLevelData;
    private final DimensionSpecialEffects effects;
    private final Minecraft minecraft = Minecraft.getInstance();
    final List<AbstractClientPlayer> players = Lists.newArrayList();
    private Scoreboard scoreboard = new Scoreboard();
    private final Map<String, MapItemSavedData> mapData = Maps.newHashMap();
    private static final long CLOUD_COLOR = 0xFFFFFFL;
    private int skyFlashTime;
    private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = Util.make(new Object2ObjectArrayMap(3), $$02 -> {
        $$02.put((Object)BiomeColors.GRASS_COLOR_RESOLVER, (Object)new BlockTintCache((ToIntFunction<BlockPos>)((ToIntFunction)$$0 -> this.calculateBlockTint((BlockPos)$$0, BiomeColors.GRASS_COLOR_RESOLVER))));
        $$02.put((Object)BiomeColors.FOLIAGE_COLOR_RESOLVER, (Object)new BlockTintCache((ToIntFunction<BlockPos>)((ToIntFunction)$$0 -> this.calculateBlockTint((BlockPos)$$0, BiomeColors.FOLIAGE_COLOR_RESOLVER))));
        $$02.put((Object)BiomeColors.WATER_COLOR_RESOLVER, (Object)new BlockTintCache((ToIntFunction<BlockPos>)((ToIntFunction)$$0 -> this.calculateBlockTint((BlockPos)$$0, BiomeColors.WATER_COLOR_RESOLVER))));
    });
    private final ClientChunkCache chunkSource;
    private final Deque<Runnable> lightUpdateQueue = Queues.newArrayDeque();
    private int serverSimulationDistance;
    private final BlockStatePredictionHandler blockStatePredictionHandler = new BlockStatePredictionHandler();
    private static final Set<Item> MARKER_PARTICLE_ITEMS = Set.of((Object)Items.BARRIER, (Object)Items.LIGHT);

    public void handleBlockChangedAck(int $$0) {
        this.blockStatePredictionHandler.endPredictionsUpTo($$0, this);
    }

    public void setServerVerifiedBlockState(BlockPos $$0, BlockState $$1, int $$2) {
        if (!this.blockStatePredictionHandler.updateKnownServerState($$0, $$1)) {
            super.setBlock($$0, $$1, $$2, 512);
        }
    }

    public void syncBlockState(BlockPos $$0, BlockState $$1, Vec3 $$2) {
        BlockState $$3 = this.getBlockState($$0);
        if ($$3 != $$1) {
            this.setBlock($$0, $$1, 19);
            LocalPlayer $$4 = this.minecraft.player;
            if (this == $$4.level && $$4.isColliding($$0, $$1)) {
                $$4.absMoveTo($$2.x, $$2.y, $$2.z);
            }
        }
    }

    BlockStatePredictionHandler getBlockStatePredictionHandler() {
        return this.blockStatePredictionHandler;
    }

    @Override
    public boolean setBlock(BlockPos $$0, BlockState $$1, int $$2, int $$3) {
        if (this.blockStatePredictionHandler.isPredicting()) {
            BlockState $$4 = this.getBlockState($$0);
            boolean $$5 = super.setBlock($$0, $$1, $$2, $$3);
            if ($$5) {
                this.blockStatePredictionHandler.retainKnownServerState($$0, $$4, this.minecraft.player);
            }
            return $$5;
        }
        return super.setBlock($$0, $$1, $$2, $$3);
    }

    public ClientLevel(ClientPacketListener $$0, ClientLevelData $$1, ResourceKey<Level> $$2, Holder<DimensionType> $$3, int $$4, int $$5, Supplier<ProfilerFiller> $$6, LevelRenderer $$7, boolean $$8, long $$9) {
        super($$1, $$2, $$3, $$6, true, $$8, $$9, 1000000);
        this.connection = $$0;
        this.chunkSource = new ClientChunkCache(this, $$4);
        this.clientLevelData = $$1;
        this.levelRenderer = $$7;
        this.effects = DimensionSpecialEffects.forType($$3.value());
        this.setDefaultSpawnPos(new BlockPos(8, 64, 8), 0.0f);
        this.serverSimulationDistance = $$5;
        this.updateSkyBrightness();
        this.prepareWeather();
    }

    public void queueLightUpdate(Runnable $$0) {
        this.lightUpdateQueue.add((Object)$$0);
    }

    public void pollLightUpdates() {
        Runnable $$3;
        int $$0 = this.lightUpdateQueue.size();
        int $$1 = $$0 < 1000 ? Math.max((int)10, (int)($$0 / 10)) : $$0;
        for (int $$2 = 0; $$2 < $$1 && ($$3 = (Runnable)this.lightUpdateQueue.poll()) != null; ++$$2) {
            $$3.run();
        }
    }

    public boolean isLightUpdateQueueEmpty() {
        return this.lightUpdateQueue.isEmpty();
    }

    public DimensionSpecialEffects effects() {
        return this.effects;
    }

    public void tick(BooleanSupplier $$0) {
        this.getWorldBorder().tick();
        this.tickTime();
        this.getProfiler().push("blocks");
        this.chunkSource.tick($$0, true);
        this.getProfiler().pop();
    }

    private void tickTime() {
        this.setGameTime(this.levelData.getGameTime() + 1L);
        if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
        }
    }

    public void setGameTime(long $$0) {
        this.clientLevelData.setGameTime($$0);
    }

    public void setDayTime(long $$0) {
        if ($$0 < 0L) {
            $$0 = -$$0;
            this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, null);
        } else {
            this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, null);
        }
        this.clientLevelData.setDayTime($$0);
    }

    public Iterable<Entity> entitiesForRendering() {
        return this.getEntities().getAll();
    }

    public void tickEntities() {
        ProfilerFiller $$02 = this.getProfiler();
        $$02.push("entities");
        this.tickingEntities.forEach((Consumer<Entity>)((Consumer)$$0 -> {
            if ($$0.isRemoved() || $$0.isPassenger()) {
                return;
            }
            this.guardEntityTick(this::tickNonPassenger, $$0);
        }));
        $$02.pop();
        this.tickBlockEntities();
    }

    @Override
    public boolean shouldTickDeath(Entity $$0) {
        return $$0.chunkPosition().getChessboardDistance(this.minecraft.player.chunkPosition()) <= this.serverSimulationDistance;
    }

    public void tickNonPassenger(Entity $$0) {
        $$0.setOldPosAndRot();
        ++$$0.tickCount;
        this.getProfiler().push((Supplier<String>)((Supplier)() -> BuiltInRegistries.ENTITY_TYPE.getKey($$0.getType()).toString()));
        $$0.tick();
        this.getProfiler().pop();
        for (Entity $$1 : $$0.getPassengers()) {
            this.tickPassenger($$0, $$1);
        }
    }

    private void tickPassenger(Entity $$0, Entity $$1) {
        if ($$1.isRemoved() || $$1.getVehicle() != $$0) {
            $$1.stopRiding();
            return;
        }
        if (!($$1 instanceof Player) && !this.tickingEntities.contains($$1)) {
            return;
        }
        $$1.setOldPosAndRot();
        ++$$1.tickCount;
        $$1.rideTick();
        for (Entity $$2 : $$1.getPassengers()) {
            this.tickPassenger($$1, $$2);
        }
    }

    public void unload(LevelChunk $$0) {
        $$0.clearAllBlockEntities();
        this.chunkSource.getLightEngine().enableLightSources($$0.getPos(), false);
        this.entityStorage.stopTicking($$0.getPos());
    }

    public void onChunkLoaded(ChunkPos $$0) {
        this.tintCaches.forEach(($$1, $$2) -> $$2.invalidateForChunk($$0.x, $$0.z));
        this.entityStorage.startTicking($$0);
    }

    public void clearTintCaches() {
        this.tintCaches.forEach(($$0, $$1) -> $$1.invalidateAll());
    }

    @Override
    public boolean hasChunk(int $$0, int $$1) {
        return true;
    }

    public int getEntityCount() {
        return this.entityStorage.count();
    }

    public void addPlayer(int $$0, AbstractClientPlayer $$1) {
        this.addEntity($$0, $$1);
    }

    public void putNonPlayerEntity(int $$0, Entity $$1) {
        this.addEntity($$0, $$1);
    }

    private void addEntity(int $$0, Entity $$1) {
        this.removeEntity($$0, Entity.RemovalReason.DISCARDED);
        this.entityStorage.addEntity($$1);
    }

    public void removeEntity(int $$0, Entity.RemovalReason $$1) {
        Entity $$2 = this.getEntities().get($$0);
        if ($$2 != null) {
            $$2.setRemoved($$1);
            $$2.onClientRemoval();
        }
    }

    @Override
    @Nullable
    public Entity getEntity(int $$0) {
        return this.getEntities().get($$0);
    }

    @Override
    public void disconnect() {
        this.connection.getConnection().disconnect(Component.translatable("multiplayer.status.quitting"));
    }

    public void animateTick(int $$0, int $$1, int $$2) {
        int $$3 = 32;
        RandomSource $$4 = RandomSource.create();
        Block $$5 = this.getMarkerParticleTarget();
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        for (int $$7 = 0; $$7 < 667; ++$$7) {
            this.doAnimateTick($$0, $$1, $$2, 16, $$4, $$5, $$6);
            this.doAnimateTick($$0, $$1, $$2, 32, $$4, $$5, $$6);
        }
    }

    @Nullable
    private Block getMarkerParticleTarget() {
        ItemStack $$0;
        Item $$1;
        if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE && MARKER_PARTICLE_ITEMS.contains((Object)($$1 = ($$0 = this.minecraft.player.getMainHandItem()).getItem())) && $$1 instanceof BlockItem) {
            BlockItem $$2 = (BlockItem)$$1;
            return $$2.getBlock();
        }
        return null;
    }

    public void doAnimateTick(int $$0, int $$12, int $$2, int $$3, RandomSource $$4, @Nullable Block $$5, BlockPos.MutableBlockPos $$6) {
        int $$7 = $$0 + this.random.nextInt($$3) - this.random.nextInt($$3);
        int $$8 = $$12 + this.random.nextInt($$3) - this.random.nextInt($$3);
        int $$9 = $$2 + this.random.nextInt($$3) - this.random.nextInt($$3);
        $$6.set($$7, $$8, $$9);
        BlockState $$10 = this.getBlockState($$6);
        $$10.getBlock().animateTick($$10, this, $$6, $$4);
        FluidState $$11 = this.getFluidState($$6);
        if (!$$11.isEmpty()) {
            $$11.animateTick(this, $$6, $$4);
            ParticleOptions $$122 = $$11.getDripParticle();
            if ($$122 != null && this.random.nextInt(10) == 0) {
                boolean $$13 = $$10.isFaceSturdy(this, $$6, Direction.DOWN);
                Vec3i $$14 = $$6.below();
                this.trySpawnDripParticles((BlockPos)$$14, this.getBlockState((BlockPos)$$14), $$122, $$13);
            }
        }
        if ($$5 == $$10.getBlock()) {
            this.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, $$10), (double)$$7 + 0.5, (double)$$8 + 0.5, (double)$$9 + 0.5, 0.0, 0.0, 0.0);
        }
        if (!$$10.isCollisionShapeFullBlock(this, $$6)) {
            ((Biome)this.getBiome($$6).value()).getAmbientParticle().ifPresent($$1 -> {
                if ($$1.canSpawn(this.random)) {
                    this.addParticle($$1.getOptions(), (double)$$6.getX() + this.random.nextDouble(), (double)$$6.getY() + this.random.nextDouble(), (double)$$6.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
                }
            });
        }
    }

    private void trySpawnDripParticles(BlockPos $$0, BlockState $$1, ParticleOptions $$2, boolean $$3) {
        if (!$$1.getFluidState().isEmpty()) {
            return;
        }
        VoxelShape $$4 = $$1.getCollisionShape(this, $$0);
        double $$5 = $$4.max(Direction.Axis.Y);
        if ($$5 < 1.0) {
            if ($$3) {
                this.spawnFluidParticle($$0.getX(), $$0.getX() + 1, $$0.getZ(), $$0.getZ() + 1, (double)($$0.getY() + 1) - 0.05, $$2);
            }
        } else if (!$$1.is(BlockTags.IMPERMEABLE)) {
            double $$6 = $$4.min(Direction.Axis.Y);
            if ($$6 > 0.0) {
                this.spawnParticle($$0, $$2, $$4, (double)$$0.getY() + $$6 - 0.05);
            } else {
                Vec3i $$7 = $$0.below();
                BlockState $$8 = this.getBlockState((BlockPos)$$7);
                VoxelShape $$9 = $$8.getCollisionShape(this, (BlockPos)$$7);
                double $$10 = $$9.max(Direction.Axis.Y);
                if ($$10 < 1.0 && $$8.getFluidState().isEmpty()) {
                    this.spawnParticle($$0, $$2, $$4, (double)$$0.getY() - 0.05);
                }
            }
        }
    }

    private void spawnParticle(BlockPos $$0, ParticleOptions $$1, VoxelShape $$2, double $$3) {
        this.spawnFluidParticle((double)$$0.getX() + $$2.min(Direction.Axis.X), (double)$$0.getX() + $$2.max(Direction.Axis.X), (double)$$0.getZ() + $$2.min(Direction.Axis.Z), (double)$$0.getZ() + $$2.max(Direction.Axis.Z), $$3, $$1);
    }

    private void spawnFluidParticle(double $$0, double $$1, double $$2, double $$3, double $$4, ParticleOptions $$5) {
        this.addParticle($$5, Mth.lerp(this.random.nextDouble(), $$0, $$1), $$4, Mth.lerp(this.random.nextDouble(), $$2, $$3), 0.0, 0.0, 0.0);
    }

    @Override
    public CrashReportCategory fillReportDetails(CrashReport $$0) {
        CrashReportCategory $$1 = super.fillReportDetails($$0);
        $$1.setDetail("Server brand", () -> this.minecraft.player.getServerBrand());
        $$1.setDetail("Server type", () -> this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        return $$1;
    }

    @Override
    public void playSeededSound(@Nullable Player $$0, double $$1, double $$2, double $$3, Holder<SoundEvent> $$4, SoundSource $$5, float $$6, float $$7, long $$8) {
        if ($$0 == this.minecraft.player) {
            this.playSound($$1, $$2, $$3, $$4.value(), $$5, $$6, $$7, false, $$8);
        }
    }

    @Override
    public void playSeededSound(@Nullable Player $$0, Entity $$1, Holder<SoundEvent> $$2, SoundSource $$3, float $$4, float $$5, long $$6) {
        if ($$0 == this.minecraft.player) {
            this.minecraft.getSoundManager().play(new EntityBoundSoundInstance($$2.value(), $$3, $$4, $$5, $$1, $$6));
        }
    }

    @Override
    public void playLocalSound(double $$0, double $$1, double $$2, SoundEvent $$3, SoundSource $$4, float $$5, float $$6, boolean $$7) {
        this.playSound($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.random.nextLong());
    }

    private void playSound(double $$0, double $$1, double $$2, SoundEvent $$3, SoundSource $$4, float $$5, float $$6, boolean $$7, long $$8) {
        double $$9 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr($$0, $$1, $$2);
        SimpleSoundInstance $$10 = new SimpleSoundInstance($$3, $$4, $$5, $$6, RandomSource.create($$8), $$0, $$1, $$2);
        if ($$7 && $$9 > 100.0) {
            double $$11 = Math.sqrt((double)$$9) / 40.0;
            this.minecraft.getSoundManager().playDelayed($$10, (int)($$11 * 20.0));
        } else {
            this.minecraft.getSoundManager().play($$10);
        }
    }

    @Override
    public void createFireworks(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, @Nullable CompoundTag $$6) {
        this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, $$0, $$1, $$2, $$3, $$4, $$5, this.minecraft.particleEngine, $$6));
    }

    @Override
    public void sendPacketToServer(Packet<?> $$0) {
        this.connection.send($$0);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.connection.getRecipeManager();
    }

    public void setScoreboard(Scoreboard $$0) {
        this.scoreboard = $$0;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public ClientChunkCache getChunkSource() {
        return this.chunkSource;
    }

    @Override
    @Nullable
    public MapItemSavedData getMapData(String $$0) {
        return (MapItemSavedData)this.mapData.get((Object)$$0);
    }

    public void overrideMapData(String $$0, MapItemSavedData $$1) {
        this.mapData.put((Object)$$0, (Object)$$1);
    }

    @Override
    public void setMapData(String $$0, MapItemSavedData $$1) {
    }

    @Override
    public int getFreeMapId() {
        return 0;
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.connection.registryAccess();
    }

    @Override
    public void sendBlockUpdated(BlockPos $$0, BlockState $$1, BlockState $$2, int $$3) {
        this.levelRenderer.blockChanged(this, $$0, $$1, $$2, $$3);
    }

    @Override
    public void setBlocksDirty(BlockPos $$0, BlockState $$1, BlockState $$2) {
        this.levelRenderer.setBlockDirty($$0, $$1, $$2);
    }

    public void setSectionDirtyWithNeighbors(int $$0, int $$1, int $$2) {
        this.levelRenderer.setSectionDirtyWithNeighbors($$0, $$1, $$2);
    }

    public void setLightReady(int $$0, int $$1) {
        LevelChunk $$2 = this.chunkSource.getChunk($$0, $$1, false);
        if ($$2 != null) {
            $$2.setClientLightReady(true);
        }
    }

    @Override
    public void destroyBlockProgress(int $$0, BlockPos $$1, int $$2) {
        this.levelRenderer.destroyBlockProgress($$0, $$1, $$2);
    }

    @Override
    public void globalLevelEvent(int $$0, BlockPos $$1, int $$2) {
        this.levelRenderer.globalLevelEvent($$0, $$1, $$2);
    }

    @Override
    public void levelEvent(@Nullable Player $$0, int $$1, BlockPos $$2, int $$3) {
        try {
            this.levelRenderer.levelEvent($$1, $$2, $$3);
        }
        catch (Throwable $$4) {
            CrashReport $$5 = CrashReport.forThrowable($$4, "Playing level event");
            CrashReportCategory $$6 = $$5.addCategory("Level event being played");
            $$6.setDetail("Block coordinates", CrashReportCategory.formatLocation(this, $$2));
            $$6.setDetail("Event source", $$0);
            $$6.setDetail("Event type", $$1);
            $$6.setDetail("Event data", $$3);
            throw new ReportedException($$5);
        }
    }

    @Override
    public void addParticle(ParticleOptions $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        this.levelRenderer.addParticle($$0, $$0.getType().getOverrideLimiter(), $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Override
    public void addParticle(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        this.levelRenderer.addParticle($$0, $$0.getType().getOverrideLimiter() || $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        this.levelRenderer.addParticle($$0, false, true, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        this.levelRenderer.addParticle($$0, $$0.getType().getOverrideLimiter() || $$1, true, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public List<AbstractClientPlayer> players() {
        return this.players;
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int $$0, int $$1, int $$2) {
        return this.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
    }

    public float getSkyDarken(float $$0) {
        float $$1 = this.getTimeOfDay($$0);
        float $$2 = 1.0f - (Mth.cos($$1 * ((float)Math.PI * 2)) * 2.0f + 0.2f);
        $$2 = Mth.clamp($$2, 0.0f, 1.0f);
        $$2 = 1.0f - $$2;
        $$2 *= 1.0f - this.getRainLevel($$0) * 5.0f / 16.0f;
        return ($$2 *= 1.0f - this.getThunderLevel($$0) * 5.0f / 16.0f) * 0.8f + 0.2f;
    }

    public Vec3 getSkyColor(Vec3 $$0, float $$12) {
        float $$13;
        float $$22 = this.getTimeOfDay($$12);
        Vec3 $$32 = $$0.subtract(2.0, 2.0, 2.0).scale(0.25);
        BiomeManager $$4 = this.getBiomeManager();
        Vec3 $$5 = CubicSampler.gaussianSampleVec3($$32, ($$1, $$2, $$3) -> Vec3.fromRGB24($$4.getNoiseBiomeAtQuart($$1, $$2, $$3).value().getSkyColor()));
        float $$6 = Mth.cos($$22 * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        $$6 = Mth.clamp($$6, 0.0f, 1.0f);
        float $$7 = (float)$$5.x * $$6;
        float $$8 = (float)$$5.y * $$6;
        float $$9 = (float)$$5.z * $$6;
        float $$10 = this.getRainLevel($$12);
        if ($$10 > 0.0f) {
            float $$11 = ($$7 * 0.3f + $$8 * 0.59f + $$9 * 0.11f) * 0.6f;
            float $$122 = 1.0f - $$10 * 0.75f;
            $$7 = $$7 * $$122 + $$11 * (1.0f - $$122);
            $$8 = $$8 * $$122 + $$11 * (1.0f - $$122);
            $$9 = $$9 * $$122 + $$11 * (1.0f - $$122);
        }
        if (($$13 = this.getThunderLevel($$12)) > 0.0f) {
            float $$14 = ($$7 * 0.3f + $$8 * 0.59f + $$9 * 0.11f) * 0.2f;
            float $$15 = 1.0f - $$13 * 0.75f;
            $$7 = $$7 * $$15 + $$14 * (1.0f - $$15);
            $$8 = $$8 * $$15 + $$14 * (1.0f - $$15);
            $$9 = $$9 * $$15 + $$14 * (1.0f - $$15);
        }
        if (!this.minecraft.options.hideLightningFlash().get().booleanValue() && this.skyFlashTime > 0) {
            float $$16 = (float)this.skyFlashTime - $$12;
            if ($$16 > 1.0f) {
                $$16 = 1.0f;
            }
            $$7 = $$7 * (1.0f - ($$16 *= 0.45f)) + 0.8f * $$16;
            $$8 = $$8 * (1.0f - $$16) + 0.8f * $$16;
            $$9 = $$9 * (1.0f - $$16) + 1.0f * $$16;
        }
        return new Vec3($$7, $$8, $$9);
    }

    public Vec3 getCloudColor(float $$0) {
        float $$1 = this.getTimeOfDay($$0);
        float $$2 = Mth.cos($$1 * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        $$2 = Mth.clamp($$2, 0.0f, 1.0f);
        float $$3 = 1.0f;
        float $$4 = 1.0f;
        float $$5 = 1.0f;
        float $$6 = this.getRainLevel($$0);
        if ($$6 > 0.0f) {
            float $$7 = ($$3 * 0.3f + $$4 * 0.59f + $$5 * 0.11f) * 0.6f;
            float $$8 = 1.0f - $$6 * 0.95f;
            $$3 = $$3 * $$8 + $$7 * (1.0f - $$8);
            $$4 = $$4 * $$8 + $$7 * (1.0f - $$8);
            $$5 = $$5 * $$8 + $$7 * (1.0f - $$8);
        }
        $$3 *= $$2 * 0.9f + 0.1f;
        $$4 *= $$2 * 0.9f + 0.1f;
        $$5 *= $$2 * 0.85f + 0.15f;
        float $$9 = this.getThunderLevel($$0);
        if ($$9 > 0.0f) {
            float $$10 = ($$3 * 0.3f + $$4 * 0.59f + $$5 * 0.11f) * 0.2f;
            float $$11 = 1.0f - $$9 * 0.95f;
            $$3 = $$3 * $$11 + $$10 * (1.0f - $$11);
            $$4 = $$4 * $$11 + $$10 * (1.0f - $$11);
            $$5 = $$5 * $$11 + $$10 * (1.0f - $$11);
        }
        return new Vec3($$3, $$4, $$5);
    }

    public float getStarBrightness(float $$0) {
        float $$1 = this.getTimeOfDay($$0);
        float $$2 = 1.0f - (Mth.cos($$1 * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        $$2 = Mth.clamp($$2, 0.0f, 1.0f);
        return $$2 * $$2 * 0.5f;
    }

    public int getSkyFlashTime() {
        return this.skyFlashTime;
    }

    @Override
    public void setSkyFlashTime(int $$0) {
        this.skyFlashTime = $$0;
    }

    @Override
    public float getShade(Direction $$0, boolean $$1) {
        boolean $$2 = this.effects().constantAmbientLight();
        if (!$$1) {
            return $$2 ? 0.9f : 1.0f;
        }
        switch ($$0) {
            case DOWN: {
                return $$2 ? 0.9f : 0.5f;
            }
            case UP: {
                return $$2 ? 0.9f : 1.0f;
            }
            case NORTH: 
            case SOUTH: {
                return 0.8f;
            }
            case WEST: 
            case EAST: {
                return 0.6f;
            }
        }
        return 1.0f;
    }

    @Override
    public int getBlockTint(BlockPos $$0, ColorResolver $$1) {
        BlockTintCache $$2 = (BlockTintCache)this.tintCaches.get((Object)$$1);
        return $$2.getColor($$0);
    }

    public int calculateBlockTint(BlockPos $$0, ColorResolver $$1) {
        int $$2 = Minecraft.getInstance().options.biomeBlendRadius().get();
        if ($$2 == 0) {
            return $$1.getColor((Biome)this.getBiome($$0).value(), $$0.getX(), $$0.getZ());
        }
        int $$3 = ($$2 * 2 + 1) * ($$2 * 2 + 1);
        int $$4 = 0;
        int $$5 = 0;
        int $$6 = 0;
        Cursor3D $$7 = new Cursor3D($$0.getX() - $$2, $$0.getY(), $$0.getZ() - $$2, $$0.getX() + $$2, $$0.getY(), $$0.getZ() + $$2);
        BlockPos.MutableBlockPos $$8 = new BlockPos.MutableBlockPos();
        while ($$7.advance()) {
            $$8.set($$7.nextX(), $$7.nextY(), $$7.nextZ());
            int $$9 = $$1.getColor((Biome)this.getBiome($$8).value(), $$8.getX(), $$8.getZ());
            $$4 += ($$9 & 0xFF0000) >> 16;
            $$5 += ($$9 & 0xFF00) >> 8;
            $$6 += $$9 & 0xFF;
        }
        return ($$4 / $$3 & 0xFF) << 16 | ($$5 / $$3 & 0xFF) << 8 | $$6 / $$3 & 0xFF;
    }

    public void setDefaultSpawnPos(BlockPos $$0, float $$1) {
        this.levelData.setSpawn($$0, $$1);
    }

    public String toString() {
        return "ClientLevel";
    }

    @Override
    public ClientLevelData getLevelData() {
        return this.clientLevelData;
    }

    @Override
    public void gameEvent(GameEvent $$0, Vec3 $$1, GameEvent.Context $$2) {
    }

    protected Map<String, MapItemSavedData> getAllMapData() {
        return ImmutableMap.copyOf(this.mapData);
    }

    protected void addMapData(Map<String, MapItemSavedData> $$0) {
        this.mapData.putAll($$0);
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return this.entityStorage.getEntityGetter();
    }

    @Override
    public String gatherChunkSourceStats() {
        return "Chunks[C] W: " + this.chunkSource.gatherStats() + " E: " + this.entityStorage.gatherStats();
    }

    @Override
    public void addDestroyBlockEffect(BlockPos $$0, BlockState $$1) {
        this.minecraft.particleEngine.destroy($$0, $$1);
    }

    public void setServerSimulationDistance(int $$0) {
        this.serverSimulationDistance = $$0;
    }

    public int getServerSimulationDistance() {
        return this.serverSimulationDistance;
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.connection.enabledFeatures();
    }

    final class EntityCallbacks
    implements LevelCallback<Entity> {
        EntityCallbacks() {
        }

        @Override
        public void onSectionChange(Entity $$0) {
        }

        @Override
        public void onTrackingEnd(Entity $$0) {
        }

        @Override
        public void onTrackingStart(Entity $$0) {
            ClientLevel.this.tickingEntities.add($$0);
        }

        @Override
        public void onTickingEnd(Entity $$0) {
            ClientLevel.this.tickingEntities.remove($$0);
        }

        @Override
        public void onTrackingStart(Entity $$0) {
            if ($$0 instanceof AbstractClientPlayer) {
                ClientLevel.this.players.add((Object)((AbstractClientPlayer)$$0));
            }
        }

        @Override
        public void onTrackingEnd(Entity $$0) {
            $$0.unRide();
            ClientLevel.this.players.remove((Object)$$0);
        }

        @Override
        public void onSectionChange(Entity $$0) {
        }
    }

    public static class ClientLevelData
    implements WritableLevelData {
        private final boolean hardcore;
        private final GameRules gameRules;
        private final boolean isFlat;
        private int xSpawn;
        private int ySpawn;
        private int zSpawn;
        private float spawnAngle;
        private long gameTime;
        private long dayTime;
        private boolean raining;
        private Difficulty difficulty;
        private boolean difficultyLocked;

        public ClientLevelData(Difficulty $$0, boolean $$1, boolean $$2) {
            this.difficulty = $$0;
            this.hardcore = $$1;
            this.isFlat = $$2;
            this.gameRules = new GameRules();
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

        public void setGameTime(long $$0) {
            this.gameTime = $$0;
        }

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
        public boolean isThundering() {
            return false;
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
        public boolean isHardcore() {
            return this.hardcore;
        }

        @Override
        public GameRules getGameRules() {
            return this.gameRules;
        }

        @Override
        public Difficulty getDifficulty() {
            return this.difficulty;
        }

        @Override
        public boolean isDifficultyLocked() {
            return this.difficultyLocked;
        }

        @Override
        public void fillCrashReportCategory(CrashReportCategory $$0, LevelHeightAccessor $$1) {
            WritableLevelData.super.fillCrashReportCategory($$0, $$1);
        }

        public void setDifficulty(Difficulty $$0) {
            this.difficulty = $$0;
        }

        public void setDifficultyLocked(boolean $$0) {
            this.difficultyLocked = $$0;
        }

        public double getHorizonHeight(LevelHeightAccessor $$0) {
            if (this.isFlat) {
                return $$0.getMinBuildHeight();
            }
            return 63.0;
        }

        public float getClearColorScale() {
            if (this.isFlat) {
                return 1.0f;
            }
            return 0.03125f;
        }
    }
}