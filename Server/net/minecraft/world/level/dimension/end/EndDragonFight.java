/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ContiguousSet
 *  com.google.common.collect.DiscreteDomain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Range
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  java.lang.Comparable
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.List
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EndDragonFight {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TICKS_BEFORE_DRAGON_RESPAWN = 1200;
    private static final int TIME_BETWEEN_CRYSTAL_SCANS = 100;
    private static final int TIME_BETWEEN_PLAYER_SCANS = 20;
    private static final int ARENA_SIZE_CHUNKS = 8;
    public static final int ARENA_TICKET_LEVEL = 9;
    private static final int GATEWAY_COUNT = 20;
    private static final int GATEWAY_DISTANCE = 96;
    public static final int DRAGON_SPAWN_Y = 128;
    private static final Predicate<Entity> VALID_PLAYER = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(0.0, 128.0, 0.0, 192.0));
    private final ServerBossEvent dragonEvent = (ServerBossEvent)new ServerBossEvent(Component.translatable("entity.minecraft.ender_dragon"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS).setPlayBossMusic(true).setCreateWorldFog(true);
    private final ServerLevel level;
    private final ObjectArrayList<Integer> gateways = new ObjectArrayList();
    private final BlockPattern exitPortalPattern;
    private int ticksSinceDragonSeen;
    private int crystalsAlive;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    @Nullable
    private UUID dragonUUID;
    private boolean needsStateScanning = true;
    @Nullable
    private BlockPos portalLocation;
    @Nullable
    private DragonRespawnAnimation respawnStage;
    private int respawnTime;
    @Nullable
    private List<EndCrystal> respawnCrystals;

    public EndDragonFight(ServerLevel $$0, long $$1, CompoundTag $$2) {
        this.level = $$0;
        if ($$2.contains("NeedsStateScanning")) {
            this.needsStateScanning = $$2.getBoolean("NeedsStateScanning");
        }
        if ($$2.contains("DragonKilled", 99)) {
            if ($$2.hasUUID("Dragon")) {
                this.dragonUUID = $$2.getUUID("Dragon");
            }
            this.dragonKilled = $$2.getBoolean("DragonKilled");
            this.previouslyKilled = $$2.getBoolean("PreviouslyKilled");
            if ($$2.getBoolean("IsRespawning")) {
                this.respawnStage = DragonRespawnAnimation.START;
            }
            if ($$2.contains("ExitPortalLocation", 10)) {
                this.portalLocation = NbtUtils.readBlockPos($$2.getCompound("ExitPortalLocation"));
            }
        } else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }
        if ($$2.contains("Gateways", 9)) {
            ListTag $$3 = $$2.getList("Gateways", 3);
            for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                this.gateways.add((Object)$$3.getInt($$4));
            }
        } else {
            this.gateways.addAll((Collection)ContiguousSet.create((Range)Range.closedOpen((Comparable)Integer.valueOf((int)0), (Comparable)Integer.valueOf((int)20)), (DiscreteDomain)DiscreteDomain.integers()));
            Util.shuffle(this.gateways, RandomSource.create($$1));
        }
        this.exitPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK))).build();
    }

    public CompoundTag saveData() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putBoolean("NeedsStateScanning", this.needsStateScanning);
        if (this.dragonUUID != null) {
            $$0.putUUID("Dragon", this.dragonUUID);
        }
        $$0.putBoolean("DragonKilled", this.dragonKilled);
        $$0.putBoolean("PreviouslyKilled", this.previouslyKilled);
        if (this.portalLocation != null) {
            $$0.put("ExitPortalLocation", NbtUtils.writeBlockPos(this.portalLocation));
        }
        ListTag $$1 = new ListTag();
        ObjectListIterator objectListIterator = this.gateways.iterator();
        while (objectListIterator.hasNext()) {
            int $$2 = (Integer)objectListIterator.next();
            $$1.add(IntTag.valueOf($$2));
        }
        $$0.put("Gateways", $$1);
        return $$0;
    }

    public void tick() {
        this.dragonEvent.setVisible(!this.dragonKilled);
        if (++this.ticksSinceLastPlayerScan >= 20) {
            this.updatePlayers();
            this.ticksSinceLastPlayerScan = 0;
        }
        if (!this.dragonEvent.getPlayers().isEmpty()) {
            this.level.getChunkSource().addRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
            boolean $$0 = this.isArenaLoaded();
            if (this.needsStateScanning && $$0) {
                this.scanState();
                this.needsStateScanning = false;
            }
            if (this.respawnStage != null) {
                if (this.respawnCrystals == null && $$0) {
                    this.respawnStage = null;
                    this.tryRespawn();
                }
                this.respawnStage.tick(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
            }
            if (!this.dragonKilled) {
                if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && $$0) {
                    this.findOrCreateDragon();
                    this.ticksSinceDragonSeen = 0;
                }
                if (++this.ticksSinceCrystalsScanned >= 100 && $$0) {
                    this.updateCrystalCount();
                    this.ticksSinceCrystalsScanned = 0;
                }
            }
        } else {
            this.level.getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
        }
    }

    private void scanState() {
        LOGGER.info("Scanning for legacy world dragon fight...");
        boolean $$0 = this.hasActiveExitPortal();
        if ($$0) {
            LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        } else {
            LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.previouslyKilled = false;
            if (this.findExitPortal() == null) {
                this.spawnExitPortal(false);
            }
        }
        List<? extends EnderDragon> $$1 = this.level.getDragons();
        if ($$1.isEmpty()) {
            this.dragonKilled = true;
        } else {
            EnderDragon $$2 = (EnderDragon)$$1.get(0);
            this.dragonUUID = $$2.getUUID();
            LOGGER.info("Found that there's a dragon still alive ({})", (Object)$$2);
            this.dragonKilled = false;
            if (!$$0) {
                LOGGER.info("But we didn't have a portal, let's remove it.");
                $$2.discard();
                this.dragonUUID = null;
            }
        }
        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }
    }

    private void findOrCreateDragon() {
        List<? extends EnderDragon> $$0 = this.level.getDragons();
        if ($$0.isEmpty()) {
            LOGGER.debug("Haven't seen the dragon, respawning it");
            this.createNewDragon();
        } else {
            LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUUID = ((EnderDragon)$$0.get(0)).getUUID();
        }
    }

    protected void setRespawnStage(DragonRespawnAnimation $$0) {
        if (this.respawnStage == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        }
        this.respawnTime = 0;
        if ($$0 == DragonRespawnAnimation.END) {
            this.respawnStage = null;
            this.dragonKilled = false;
            EnderDragon $$1 = this.createNewDragon();
            if ($$1 != null) {
                for (ServerPlayer $$2 : this.dragonEvent.getPlayers()) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger($$2, $$1);
                }
            }
        } else {
            this.respawnStage = $$0;
        }
    }

    private boolean hasActiveExitPortal() {
        for (int $$0 = -8; $$0 <= 8; ++$$0) {
            for (int $$1 = -8; $$1 <= 8; ++$$1) {
                LevelChunk $$2 = this.level.getChunk($$0, $$1);
                for (BlockEntity $$3 : $$2.getBlockEntities().values()) {
                    if (!($$3 instanceof TheEndPortalBlockEntity)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private BlockPattern.BlockPatternMatch findExitPortal() {
        int $$6;
        for (int $$0 = -8; $$0 <= 8; ++$$0) {
            for (int $$1 = -8; $$1 <= 8; ++$$1) {
                LevelChunk $$2 = this.level.getChunk($$0, $$1);
                for (BlockEntity $$3 : $$2.getBlockEntities().values()) {
                    BlockPattern.BlockPatternMatch $$4;
                    if (!($$3 instanceof TheEndPortalBlockEntity) || ($$4 = this.exitPortalPattern.find(this.level, $$3.getBlockPos())) == null) continue;
                    BlockPos $$5 = $$4.getBlock(3, 3, 3).getPos();
                    if (this.portalLocation == null) {
                        this.portalLocation = $$5;
                    }
                    return $$4;
                }
            }
        }
        for (int $$7 = $$6 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION).getY(); $$7 >= this.level.getMinBuildHeight(); --$$7) {
            BlockPattern.BlockPatternMatch $$8 = this.exitPortalPattern.find(this.level, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), $$7, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
            if ($$8 == null) continue;
            if (this.portalLocation == null) {
                this.portalLocation = $$8.getBlock(3, 3, 3).getPos();
            }
            return $$8;
        }
        return null;
    }

    private boolean isArenaLoaded() {
        for (int $$0 = -8; $$0 <= 8; ++$$0) {
            for (int $$1 = 8; $$1 <= 8; ++$$1) {
                ChunkAccess $$2 = this.level.getChunk($$0, $$1, ChunkStatus.FULL, false);
                if (!($$2 instanceof LevelChunk)) {
                    return false;
                }
                ChunkHolder.FullChunkStatus $$3 = ((LevelChunk)$$2).getFullStatus();
                if ($$3.isOrAfter(ChunkHolder.FullChunkStatus.TICKING)) continue;
                return false;
            }
        }
        return true;
    }

    private void updatePlayers() {
        HashSet $$0 = Sets.newHashSet();
        for (ServerPlayer $$1 : this.level.getPlayers(VALID_PLAYER)) {
            this.dragonEvent.addPlayer($$1);
            $$0.add((Object)$$1);
        }
        HashSet $$2 = Sets.newHashSet(this.dragonEvent.getPlayers());
        $$2.removeAll((Collection)$$0);
        for (ServerPlayer $$3 : $$2) {
            this.dragonEvent.removePlayer($$3);
        }
    }

    private void updateCrystalCount() {
        this.ticksSinceCrystalsScanned = 0;
        this.crystalsAlive = 0;
        for (SpikeFeature.EndSpike $$0 : SpikeFeature.getSpikesForLevel(this.level)) {
            this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, $$0.getTopBoundingBox()).size();
        }
        LOGGER.debug("Found {} end crystals still alive", (Object)this.crystalsAlive);
    }

    public void setDragonKilled(EnderDragon $$0) {
        if ($$0.getUUID().equals((Object)this.dragonUUID)) {
            this.dragonEvent.setProgress(0.0f);
            this.dragonEvent.setVisible(false);
            this.spawnExitPortal(true);
            this.spawnNewGateway();
            if (!this.previouslyKilled) {
                this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState());
            }
            this.previouslyKilled = true;
            this.dragonKilled = true;
        }
    }

    private void spawnNewGateway() {
        if (this.gateways.isEmpty()) {
            return;
        }
        int $$0 = (Integer)this.gateways.remove(this.gateways.size() - 1);
        int $$1 = Mth.floor(96.0 * Math.cos((double)(2.0 * (-Math.PI + 0.15707963267948966 * (double)$$0))));
        int $$2 = Mth.floor(96.0 * Math.sin((double)(2.0 * (-Math.PI + 0.15707963267948966 * (double)$$0))));
        this.spawnNewGateway(new BlockPos($$1, 75, $$2));
    }

    private void spawnNewGateway(BlockPos $$02) {
        this.level.levelEvent(3000, $$02, 0);
        this.level.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap($$0 -> $$0.getHolder(EndFeatures.END_GATEWAY_DELAYED)).ifPresent($$1 -> ((ConfiguredFeature)((Object)((Object)$$1.value()))).place(this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), $$02));
    }

    private void spawnExitPortal(boolean $$0) {
        EndPodiumFeature $$1 = new EndPodiumFeature($$0);
        if (this.portalLocation == null) {
            this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).below();
            while (this.level.getBlockState(this.portalLocation).is(Blocks.BEDROCK) && this.portalLocation.getY() > this.level.getSeaLevel()) {
                this.portalLocation = this.portalLocation.below();
            }
        }
        $$1.place(FeatureConfiguration.NONE, this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), this.portalLocation);
    }

    @Nullable
    private EnderDragon createNewDragon() {
        this.level.getChunkAt(new BlockPos(0, 128, 0));
        EnderDragon $$0 = EntityType.ENDER_DRAGON.create(this.level);
        if ($$0 != null) {
            $$0.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            $$0.moveTo(0.0, 128.0, 0.0, this.level.random.nextFloat() * 360.0f, 0.0f);
            this.level.addFreshEntity($$0);
            this.dragonUUID = $$0.getUUID();
        }
        return $$0;
    }

    public void updateDragon(EnderDragon $$0) {
        if ($$0.getUUID().equals((Object)this.dragonUUID)) {
            this.dragonEvent.setProgress($$0.getHealth() / $$0.getMaxHealth());
            this.ticksSinceDragonSeen = 0;
            if ($$0.hasCustomName()) {
                this.dragonEvent.setName($$0.getDisplayName());
            }
        }
    }

    public int getCrystalsAlive() {
        return this.crystalsAlive;
    }

    public void onCrystalDestroyed(EndCrystal $$0, DamageSource $$1) {
        if (this.respawnStage != null && this.respawnCrystals.contains((Object)$$0)) {
            LOGGER.debug("Aborting respawn sequence");
            this.respawnStage = null;
            this.respawnTime = 0;
            this.resetSpikeCrystals();
            this.spawnExitPortal(true);
        } else {
            this.updateCrystalCount();
            Entity $$2 = this.level.getEntity(this.dragonUUID);
            if ($$2 instanceof EnderDragon) {
                ((EnderDragon)$$2).onCrystalDestroyed($$0, $$0.blockPosition(), $$1);
            }
        }
    }

    public boolean hasPreviouslyKilledDragon() {
        return this.previouslyKilled;
    }

    public void tryRespawn() {
        if (this.dragonKilled && this.respawnStage == null) {
            BlockPos $$0 = this.portalLocation;
            if ($$0 == null) {
                LOGGER.debug("Tried to respawn, but need to find the portal first.");
                BlockPattern.BlockPatternMatch $$1 = this.findExitPortal();
                if ($$1 == null) {
                    LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.spawnExitPortal(true);
                } else {
                    LOGGER.debug("Found the exit portal & saved its location for next time.");
                }
                $$0 = this.portalLocation;
            }
            ArrayList $$2 = Lists.newArrayList();
            Vec3i $$3 = $$0.above(1);
            for (Direction $$4 : Direction.Plane.HORIZONTAL) {
                List $$5 = this.level.getEntitiesOfClass(EndCrystal.class, new AABB(((BlockPos)$$3).relative($$4, 2)));
                if ($$5.isEmpty()) {
                    return;
                }
                $$2.addAll((Collection)$$5);
            }
            LOGGER.debug("Found all crystals, respawning dragon.");
            this.respawnDragon((List<EndCrystal>)$$2);
        }
    }

    private void respawnDragon(List<EndCrystal> $$0) {
        if (this.dragonKilled && this.respawnStage == null) {
            BlockPattern.BlockPatternMatch $$1 = this.findExitPortal();
            while ($$1 != null) {
                for (int $$2 = 0; $$2 < this.exitPortalPattern.getWidth(); ++$$2) {
                    for (int $$3 = 0; $$3 < this.exitPortalPattern.getHeight(); ++$$3) {
                        for (int $$4 = 0; $$4 < this.exitPortalPattern.getDepth(); ++$$4) {
                            BlockInWorld $$5 = $$1.getBlock($$2, $$3, $$4);
                            if (!$$5.getState().is(Blocks.BEDROCK) && !$$5.getState().is(Blocks.END_PORTAL)) continue;
                            this.level.setBlockAndUpdate($$5.getPos(), Blocks.END_STONE.defaultBlockState());
                        }
                    }
                }
                $$1 = this.findExitPortal();
            }
            this.respawnStage = DragonRespawnAnimation.START;
            this.respawnTime = 0;
            this.spawnExitPortal(false);
            this.respawnCrystals = $$0;
        }
    }

    public void resetSpikeCrystals() {
        for (SpikeFeature.EndSpike $$0 : SpikeFeature.getSpikesForLevel(this.level)) {
            List $$1 = this.level.getEntitiesOfClass(EndCrystal.class, $$0.getTopBoundingBox());
            for (EndCrystal $$2 : $$1) {
                $$2.setInvulnerable(false);
                $$2.setBeamTarget(null);
            }
        }
    }
}