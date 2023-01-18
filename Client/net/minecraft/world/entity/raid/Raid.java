/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Raid {
    private static final int SECTION_RADIUS_FOR_FINDING_NEW_VILLAGE_CENTER = 2;
    private static final int ATTEMPT_RAID_FARTHEST = 0;
    private static final int ATTEMPT_RAID_CLOSE = 1;
    private static final int ATTEMPT_RAID_INSIDE = 2;
    private static final int VILLAGE_SEARCH_RADIUS = 32;
    private static final int RAID_TIMEOUT_TICKS = 48000;
    private static final int NUM_SPAWN_ATTEMPTS = 3;
    private static final String OMINOUS_BANNER_PATTERN_NAME = "block.minecraft.ominous_banner";
    private static final String RAIDERS_REMAINING = "event.minecraft.raid.raiders_remaining";
    public static final int VILLAGE_RADIUS_BUFFER = 16;
    private static final int POST_RAID_TICK_LIMIT = 40;
    private static final int DEFAULT_PRE_RAID_TICKS = 300;
    public static final int MAX_NO_ACTION_TIME = 2400;
    public static final int MAX_CELEBRATION_TICKS = 600;
    private static final int OUTSIDE_RAID_BOUNDS_TIMEOUT = 30;
    public static final int TICKS_PER_DAY = 24000;
    public static final int DEFAULT_MAX_BAD_OMEN_LEVEL = 5;
    private static final int LOW_MOB_THRESHOLD = 2;
    private static final Component RAID_NAME_COMPONENT = Component.translatable("event.minecraft.raid");
    private static final Component VICTORY = Component.translatable("event.minecraft.raid.victory");
    private static final Component DEFEAT = Component.translatable("event.minecraft.raid.defeat");
    private static final Component RAID_BAR_VICTORY_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
    private static final Component RAID_BAR_DEFEAT_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);
    private static final int HERO_OF_THE_VILLAGE_DURATION = 48000;
    public static final int VALID_RAID_RADIUS_SQR = 9216;
    public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
    private final Map<Integer, Raider> groupToLeaderMap = Maps.newHashMap();
    private final Map<Integer, Set<Raider>> groupRaiderMap = Maps.newHashMap();
    private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
    private long ticksActive;
    private BlockPos center;
    private final ServerLevel level;
    private boolean started;
    private final int id;
    private float totalHealth;
    private int badOmenLevel;
    private boolean active;
    private int groupsSpawned;
    private final ServerBossEvent raidEvent = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private int postRaidTicks;
    private int raidCooldownTicks;
    private final RandomSource random = RandomSource.create();
    private final int numGroups;
    private RaidStatus status;
    private int celebrationTicks;
    private Optional<BlockPos> waveSpawnPos = Optional.empty();

    public Raid(int $$0, ServerLevel $$1, BlockPos $$2) {
        this.id = $$0;
        this.level = $$1;
        this.active = true;
        this.raidCooldownTicks = 300;
        this.raidEvent.setProgress(0.0f);
        this.center = $$2;
        this.numGroups = this.getNumGroups($$1.getDifficulty());
        this.status = RaidStatus.ONGOING;
    }

    public Raid(ServerLevel $$0, CompoundTag $$1) {
        this.level = $$0;
        this.id = $$1.getInt("Id");
        this.started = $$1.getBoolean("Started");
        this.active = $$1.getBoolean("Active");
        this.ticksActive = $$1.getLong("TicksActive");
        this.badOmenLevel = $$1.getInt("BadOmenLevel");
        this.groupsSpawned = $$1.getInt("GroupsSpawned");
        this.raidCooldownTicks = $$1.getInt("PreRaidTicks");
        this.postRaidTicks = $$1.getInt("PostRaidTicks");
        this.totalHealth = $$1.getFloat("TotalHealth");
        this.center = new BlockPos($$1.getInt("CX"), $$1.getInt("CY"), $$1.getInt("CZ"));
        this.numGroups = $$1.getInt("NumGroups");
        this.status = RaidStatus.getByName($$1.getString("Status"));
        this.heroesOfTheVillage.clear();
        if ($$1.contains("HeroesOfTheVillage", 9)) {
            ListTag $$2 = $$1.getList("HeroesOfTheVillage", 11);
            for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                this.heroesOfTheVillage.add((Object)NbtUtils.loadUUID($$2.get($$3)));
            }
        }
    }

    public boolean isOver() {
        return this.isVictory() || this.isLoss();
    }

    public boolean isBetweenWaves() {
        return this.hasFirstWaveSpawned() && this.getTotalRaidersAlive() == 0 && this.raidCooldownTicks > 0;
    }

    public boolean hasFirstWaveSpawned() {
        return this.groupsSpawned > 0;
    }

    public boolean isStopped() {
        return this.status == RaidStatus.STOPPED;
    }

    public boolean isVictory() {
        return this.status == RaidStatus.VICTORY;
    }

    public boolean isLoss() {
        return this.status == RaidStatus.LOSS;
    }

    public float getTotalHealth() {
        return this.totalHealth;
    }

    public Set<Raider> getAllRaiders() {
        HashSet $$0 = Sets.newHashSet();
        for (Set $$1 : this.groupRaiderMap.values()) {
            $$0.addAll((Collection)$$1);
        }
        return $$0;
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean isStarted() {
        return this.started;
    }

    public int getGroupsSpawned() {
        return this.groupsSpawned;
    }

    private Predicate<ServerPlayer> validPlayer() {
        return $$0 -> {
            BlockPos $$1 = $$0.blockPosition();
            return $$0.isAlive() && this.level.getRaidAt($$1) == this;
        };
    }

    private void updatePlayers() {
        HashSet $$0 = Sets.newHashSet(this.raidEvent.getPlayers());
        List<ServerPlayer> $$1 = this.level.getPlayers(this.validPlayer());
        for (ServerPlayer $$2 : $$1) {
            if ($$0.contains((Object)$$2)) continue;
            this.raidEvent.addPlayer($$2);
        }
        for (ServerPlayer $$3 : $$0) {
            if ($$1.contains((Object)$$3)) continue;
            this.raidEvent.removePlayer($$3);
        }
    }

    public int getMaxBadOmenLevel() {
        return 5;
    }

    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }

    public void setBadOmenLevel(int $$0) {
        this.badOmenLevel = $$0;
    }

    public void absorbBadOmen(Player $$0) {
        if ($$0.hasEffect(MobEffects.BAD_OMEN)) {
            this.badOmenLevel += $$0.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;
            this.badOmenLevel = Mth.clamp(this.badOmenLevel, 0, this.getMaxBadOmenLevel());
        }
        $$0.removeEffect(MobEffects.BAD_OMEN);
    }

    public void stop() {
        this.active = false;
        this.raidEvent.removeAllPlayers();
        this.status = RaidStatus.STOPPED;
    }

    public void tick() {
        if (this.isStopped()) {
            return;
        }
        if (this.status == RaidStatus.ONGOING) {
            boolean $$0 = this.active;
            this.active = this.level.hasChunkAt(this.center);
            if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                this.stop();
                return;
            }
            if ($$0 != this.active) {
                this.raidEvent.setVisible(this.active);
            }
            if (!this.active) {
                return;
            }
            if (!this.level.isVillage(this.center)) {
                this.moveRaidCenterToNearbyVillageSection();
            }
            if (!this.level.isVillage(this.center)) {
                if (this.groupsSpawned > 0) {
                    this.status = RaidStatus.LOSS;
                } else {
                    this.stop();
                }
            }
            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
                this.stop();
                return;
            }
            int $$1 = this.getTotalRaidersAlive();
            if ($$1 == 0 && this.hasMoreWaves()) {
                if (this.raidCooldownTicks > 0) {
                    boolean $$3;
                    boolean $$2 = this.waveSpawnPos.isPresent();
                    boolean bl = $$3 = !$$2 && this.raidCooldownTicks % 5 == 0;
                    if ($$2 && !this.level.isPositionEntityTicking((BlockPos)this.waveSpawnPos.get())) {
                        $$3 = true;
                    }
                    if ($$3) {
                        int $$4 = 0;
                        if (this.raidCooldownTicks < 100) {
                            $$4 = 1;
                        } else if (this.raidCooldownTicks < 40) {
                            $$4 = 2;
                        }
                        this.waveSpawnPos = this.getValidSpawnPos($$4);
                    }
                    if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                        this.updatePlayers();
                    }
                    --this.raidCooldownTicks;
                    this.raidEvent.setProgress(Mth.clamp((float)(300 - this.raidCooldownTicks) / 300.0f, 0.0f, 1.0f));
                } else if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                    this.raidCooldownTicks = 300;
                    this.raidEvent.setName(RAID_NAME_COMPONENT);
                    return;
                }
            }
            if (this.ticksActive % 20L == 0L) {
                this.updatePlayers();
                this.updateRaiders();
                if ($$1 > 0) {
                    if ($$1 <= 2) {
                        this.raidEvent.setName(RAID_NAME_COMPONENT.copy().append(" - ").append(Component.translatable(RAIDERS_REMAINING, $$1)));
                    } else {
                        this.raidEvent.setName(RAID_NAME_COMPONENT);
                    }
                } else {
                    this.raidEvent.setName(RAID_NAME_COMPONENT);
                }
            }
            boolean $$5 = false;
            int $$6 = 0;
            while (this.shouldSpawnGroup()) {
                BlockPos $$7;
                BlockPos blockPos = $$7 = this.waveSpawnPos.isPresent() ? (BlockPos)this.waveSpawnPos.get() : this.findRandomSpawnPos($$6, 20);
                if ($$7 != null) {
                    this.started = true;
                    this.spawnGroup($$7);
                    if (!$$5) {
                        this.playSound($$7);
                        $$5 = true;
                    }
                } else {
                    ++$$6;
                }
                if ($$6 <= 3) continue;
                this.stop();
                break;
            }
            if (this.isStarted() && !this.hasMoreWaves() && $$1 == 0) {
                if (this.postRaidTicks < 40) {
                    ++this.postRaidTicks;
                } else {
                    this.status = RaidStatus.VICTORY;
                    for (UUID $$8 : this.heroesOfTheVillage) {
                        Entity $$9 = this.level.getEntity($$8);
                        if (!($$9 instanceof LivingEntity) || $$9.isSpectator()) continue;
                        LivingEntity $$10 = (LivingEntity)$$9;
                        $$10.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                        if (!($$10 instanceof ServerPlayer)) continue;
                        ServerPlayer $$11 = (ServerPlayer)$$10;
                        $$11.awardStat(Stats.RAID_WIN);
                        CriteriaTriggers.RAID_WIN.trigger($$11);
                    }
                }
            }
            this.setDirty();
        } else if (this.isOver()) {
            ++this.celebrationTicks;
            if (this.celebrationTicks >= 600) {
                this.stop();
                return;
            }
            if (this.celebrationTicks % 20 == 0) {
                this.updatePlayers();
                this.raidEvent.setVisible(true);
                if (this.isVictory()) {
                    this.raidEvent.setProgress(0.0f);
                    this.raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
                } else {
                    this.raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
                }
            }
        }
    }

    private void moveRaidCenterToNearbyVillageSection() {
        Stream<SectionPos> $$02 = SectionPos.cube(SectionPos.of(this.center), 2);
        $$02.filter(this.level::isVillage).map(SectionPos::center).min(Comparator.comparingDouble($$0 -> $$0.distSqr(this.center))).ifPresent(this::setCenter);
    }

    private Optional<BlockPos> getValidSpawnPos(int $$0) {
        for (int $$1 = 0; $$1 < 3; ++$$1) {
            BlockPos $$2 = this.findRandomSpawnPos($$0, 1);
            if ($$2 == null) continue;
            return Optional.of((Object)$$2);
        }
        return Optional.empty();
    }

    private boolean hasMoreWaves() {
        if (this.hasBonusWave()) {
            return !this.hasSpawnedBonusWave();
        }
        return !this.isFinalWave();
    }

    private boolean isFinalWave() {
        return this.getGroupsSpawned() == this.numGroups;
    }

    private boolean hasBonusWave() {
        return this.badOmenLevel > 1;
    }

    private boolean hasSpawnedBonusWave() {
        return this.getGroupsSpawned() > this.numGroups;
    }

    private boolean shouldSpawnBonusGroup() {
        return this.isFinalWave() && this.getTotalRaidersAlive() == 0 && this.hasBonusWave();
    }

    private void updateRaiders() {
        Iterator $$0 = this.groupRaiderMap.values().iterator();
        HashSet $$1 = Sets.newHashSet();
        while ($$0.hasNext()) {
            Set $$2 = (Set)$$0.next();
            for (Raider $$3 : $$2) {
                BlockPos $$4 = $$3.blockPosition();
                if ($$3.isRemoved() || $$3.level.dimension() != this.level.dimension() || this.center.distSqr($$4) >= 12544.0) {
                    $$1.add((Object)$$3);
                    continue;
                }
                if ($$3.tickCount <= 600) continue;
                if (this.level.getEntity($$3.getUUID()) == null) {
                    $$1.add((Object)$$3);
                }
                if (!this.level.isVillage($$4) && $$3.getNoActionTime() > 2400) {
                    $$3.setTicksOutsideRaid($$3.getTicksOutsideRaid() + 1);
                }
                if ($$3.getTicksOutsideRaid() < 30) continue;
                $$1.add((Object)$$3);
            }
        }
        for (Raider $$5 : $$1) {
            this.removeFromRaid($$5, true);
        }
    }

    private void playSound(BlockPos $$0) {
        float $$1 = 13.0f;
        int $$2 = 64;
        Collection<ServerPlayer> $$3 = this.raidEvent.getPlayers();
        long $$4 = this.random.nextLong();
        for (ServerPlayer $$5 : this.level.players()) {
            Vec3 $$6 = $$5.position();
            Vec3 $$7 = Vec3.atCenterOf($$0);
            double $$8 = Math.sqrt((double)(($$7.x - $$6.x) * ($$7.x - $$6.x) + ($$7.z - $$6.z) * ($$7.z - $$6.z)));
            double $$9 = $$6.x + 13.0 / $$8 * ($$7.x - $$6.x);
            double $$10 = $$6.z + 13.0 / $$8 * ($$7.z - $$6.z);
            if (!($$8 <= 64.0) && !$$3.contains((Object)$$5)) continue;
            $$5.connection.send(new ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.NEUTRAL, $$9, $$5.getY(), $$10, 64.0f, 1.0f, $$4));
        }
    }

    private void spawnGroup(BlockPos $$0) {
        boolean $$1 = false;
        int $$2 = this.groupsSpawned + 1;
        this.totalHealth = 0.0f;
        DifficultyInstance $$3 = this.level.getCurrentDifficultyAt($$0);
        boolean $$4 = this.shouldSpawnBonusGroup();
        for (RaiderType $$5 : RaiderType.VALUES) {
            Raider $$9;
            int $$6 = this.getDefaultNumSpawns($$5, $$2, $$4) + this.getPotentialBonusSpawns($$5, this.random, $$2, $$3, $$4);
            int $$7 = 0;
            for (int $$8 = 0; $$8 < $$6 && ($$9 = $$5.entityType.create(this.level)) != null; ++$$8) {
                if (!$$1 && $$9.canBeLeader()) {
                    $$9.setPatrolLeader(true);
                    this.setLeader($$2, $$9);
                    $$1 = true;
                }
                this.joinRaid($$2, $$9, $$0, false);
                if ($$5.entityType != EntityType.RAVAGER) continue;
                Raider $$10 = null;
                if ($$2 == this.getNumGroups(Difficulty.NORMAL)) {
                    $$10 = EntityType.PILLAGER.create(this.level);
                } else if ($$2 >= this.getNumGroups(Difficulty.HARD)) {
                    $$10 = $$7 == 0 ? (Raider)EntityType.EVOKER.create(this.level) : (Raider)EntityType.VINDICATOR.create(this.level);
                }
                ++$$7;
                if ($$10 == null) continue;
                this.joinRaid($$2, $$10, $$0, false);
                $$10.moveTo($$0, 0.0f, 0.0f);
                $$10.startRiding($$9);
            }
        }
        this.waveSpawnPos = Optional.empty();
        ++this.groupsSpawned;
        this.updateBossbar();
        this.setDirty();
    }

    public void joinRaid(int $$0, Raider $$1, @Nullable BlockPos $$2, boolean $$3) {
        boolean $$4 = this.addWaveMob($$0, $$1);
        if ($$4) {
            $$1.setCurrentRaid(this);
            $$1.setWave($$0);
            $$1.setCanJoinRaid(true);
            $$1.setTicksOutsideRaid(0);
            if (!$$3 && $$2 != null) {
                $$1.setPos((double)$$2.getX() + 0.5, (double)$$2.getY() + 1.0, (double)$$2.getZ() + 0.5);
                $$1.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt($$2), MobSpawnType.EVENT, null, null);
                $$1.applyRaidBuffs($$0, false);
                $$1.setOnGround(true);
                this.level.addFreshEntityWithPassengers($$1);
            }
        }
    }

    public void updateBossbar() {
        this.raidEvent.setProgress(Mth.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0f, 1.0f));
    }

    public float getHealthOfLivingRaiders() {
        float $$0 = 0.0f;
        for (Set $$1 : this.groupRaiderMap.values()) {
            for (Raider $$2 : $$1) {
                $$0 += $$2.getHealth();
            }
        }
        return $$0;
    }

    private boolean shouldSpawnGroup() {
        return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalRaidersAlive() == 0;
    }

    public int getTotalRaidersAlive() {
        return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
    }

    public void removeFromRaid(Raider $$0, boolean $$1) {
        boolean $$3;
        Set $$2 = (Set)this.groupRaiderMap.get((Object)$$0.getWave());
        if ($$2 != null && ($$3 = $$2.remove((Object)$$0))) {
            if ($$1) {
                this.totalHealth -= $$0.getHealth();
            }
            $$0.setCurrentRaid(null);
            this.updateBossbar();
            this.setDirty();
        }
    }

    private void setDirty() {
        this.level.getRaids().setDirty();
    }

    public static ItemStack getLeaderBannerInstance() {
        ItemStack $$0 = new ItemStack(Items.WHITE_BANNER);
        CompoundTag $$1 = new CompoundTag();
        ListTag $$2 = new BannerPattern.Builder().addPattern(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.CYAN).addPattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.STRIPE_CENTER, DyeColor.GRAY).addPattern(BannerPatterns.BORDER, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.STRIPE_MIDDLE, DyeColor.BLACK).addPattern(BannerPatterns.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).addPattern(BannerPatterns.BORDER, DyeColor.BLACK).toListTag();
        $$1.put("Patterns", $$2);
        BlockItem.setBlockEntityData($$0, BlockEntityType.BANNER, $$1);
        $$0.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
        $$0.setHoverName(Component.translatable(OMINOUS_BANNER_PATTERN_NAME).withStyle(ChatFormatting.GOLD));
        return $$0;
    }

    @Nullable
    public Raider getLeader(int $$0) {
        return (Raider)this.groupToLeaderMap.get((Object)$$0);
    }

    @Nullable
    private BlockPos findRandomSpawnPos(int $$0, int $$1) {
        int $$2 = $$0 == 0 ? 2 : 2 - $$0;
        BlockPos.MutableBlockPos $$3 = new BlockPos.MutableBlockPos();
        for (int $$4 = 0; $$4 < $$1; ++$$4) {
            float $$5 = this.level.random.nextFloat() * ((float)Math.PI * 2);
            int $$6 = this.center.getX() + Mth.floor(Mth.cos($$5) * 32.0f * (float)$$2) + this.level.random.nextInt(5);
            int $$7 = this.center.getZ() + Mth.floor(Mth.sin($$5) * 32.0f * (float)$$2) + this.level.random.nextInt(5);
            int $$8 = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, $$6, $$7);
            $$3.set($$6, $$8, $$7);
            if (this.level.isVillage($$3) && $$0 < 2) continue;
            int $$9 = 10;
            if (!this.level.hasChunksAt($$3.getX() - 10, $$3.getZ() - 10, $$3.getX() + 10, $$3.getZ() + 10) || !this.level.isPositionEntityTicking($$3) || !NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, $$3, EntityType.RAVAGER) && (!this.level.getBlockState((BlockPos)$$3.below()).is(Blocks.SNOW) || !this.level.getBlockState($$3).isAir())) continue;
            return $$3;
        }
        return null;
    }

    private boolean addWaveMob(int $$0, Raider $$1) {
        return this.addWaveMob($$0, $$1, true);
    }

    public boolean addWaveMob(int $$02, Raider $$1, boolean $$2) {
        this.groupRaiderMap.computeIfAbsent((Object)$$02, $$0 -> Sets.newHashSet());
        Set $$3 = (Set)this.groupRaiderMap.get((Object)$$02);
        Raider $$4 = null;
        for (Raider $$5 : $$3) {
            if (!$$5.getUUID().equals((Object)$$1.getUUID())) continue;
            $$4 = $$5;
            break;
        }
        if ($$4 != null) {
            $$3.remove($$4);
            $$3.add((Object)$$1);
        }
        $$3.add((Object)$$1);
        if ($$2) {
            this.totalHealth += $$1.getHealth();
        }
        this.updateBossbar();
        this.setDirty();
        return true;
    }

    public void setLeader(int $$0, Raider $$1) {
        this.groupToLeaderMap.put((Object)$$0, (Object)$$1);
        $$1.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
        $$1.setDropChance(EquipmentSlot.HEAD, 2.0f);
    }

    public void removeLeader(int $$0) {
        this.groupToLeaderMap.remove((Object)$$0);
    }

    public BlockPos getCenter() {
        return this.center;
    }

    private void setCenter(BlockPos $$0) {
        this.center = $$0;
    }

    public int getId() {
        return this.id;
    }

    private int getDefaultNumSpawns(RaiderType $$0, int $$1, boolean $$2) {
        return $$2 ? $$0.spawnsPerWaveBeforeBonus[this.numGroups] : $$0.spawnsPerWaveBeforeBonus[$$1];
    }

    /*
     * WARNING - void declaration
     */
    private int getPotentialBonusSpawns(RaiderType $$0, RandomSource $$1, int $$2, DifficultyInstance $$3, boolean $$4) {
        void $$13;
        Difficulty $$5 = $$3.getDifficulty();
        boolean $$6 = $$5 == Difficulty.EASY;
        boolean $$7 = $$5 == Difficulty.NORMAL;
        switch ($$0) {
            case WITCH: {
                if (!$$6 && $$2 > 2 && $$2 != 4) {
                    boolean $$8 = true;
                    break;
                }
                return 0;
            }
            case PILLAGER: 
            case VINDICATOR: {
                if ($$6) {
                    int $$9 = $$1.nextInt(2);
                    break;
                }
                if ($$7) {
                    boolean $$10 = true;
                    break;
                }
                int $$11 = 2;
                break;
            }
            case RAVAGER: {
                boolean $$12 = !$$6 && $$4;
                break;
            }
            default: {
                return 0;
            }
        }
        return $$13 > 0 ? $$1.nextInt((int)($$13 + true)) : 0;
    }

    public boolean isActive() {
        return this.active;
    }

    public CompoundTag save(CompoundTag $$0) {
        $$0.putInt("Id", this.id);
        $$0.putBoolean("Started", this.started);
        $$0.putBoolean("Active", this.active);
        $$0.putLong("TicksActive", this.ticksActive);
        $$0.putInt("BadOmenLevel", this.badOmenLevel);
        $$0.putInt("GroupsSpawned", this.groupsSpawned);
        $$0.putInt("PreRaidTicks", this.raidCooldownTicks);
        $$0.putInt("PostRaidTicks", this.postRaidTicks);
        $$0.putFloat("TotalHealth", this.totalHealth);
        $$0.putInt("NumGroups", this.numGroups);
        $$0.putString("Status", this.status.getName());
        $$0.putInt("CX", this.center.getX());
        $$0.putInt("CY", this.center.getY());
        $$0.putInt("CZ", this.center.getZ());
        ListTag $$1 = new ListTag();
        for (UUID $$2 : this.heroesOfTheVillage) {
            $$1.add(NbtUtils.createUUID($$2));
        }
        $$0.put("HeroesOfTheVillage", $$1);
        return $$0;
    }

    public int getNumGroups(Difficulty $$0) {
        switch ($$0) {
            case EASY: {
                return 3;
            }
            case NORMAL: {
                return 5;
            }
            case HARD: {
                return 7;
            }
        }
        return 0;
    }

    public float getEnchantOdds() {
        int $$0 = this.getBadOmenLevel();
        if ($$0 == 2) {
            return 0.1f;
        }
        if ($$0 == 3) {
            return 0.25f;
        }
        if ($$0 == 4) {
            return 0.5f;
        }
        if ($$0 == 5) {
            return 0.75f;
        }
        return 0.0f;
    }

    public void addHeroOfTheVillage(Entity $$0) {
        this.heroesOfTheVillage.add((Object)$$0.getUUID());
    }

    static enum RaidStatus {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final RaidStatus[] VALUES;

        static RaidStatus getByName(String $$0) {
            for (RaidStatus $$1 : VALUES) {
                if (!$$0.equalsIgnoreCase($$1.name())) continue;
                return $$1;
            }
            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        static {
            VALUES = RaidStatus.values();
        }
    }

    static enum RaiderType {
        VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
        EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
        PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
        WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
        RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

        static final RaiderType[] VALUES;
        final EntityType<? extends Raider> entityType;
        final int[] spawnsPerWaveBeforeBonus;

        private RaiderType(EntityType<? extends Raider> $$0, int[] $$1) {
            this.entityType = $$0;
            this.spawnsPerWaveBeforeBonus = $$1;
        }

        static {
            VALUES = RaiderType.values();
        }
    }
}