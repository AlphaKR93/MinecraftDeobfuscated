/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.BiPredicate
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.npc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class Villager
extends AbstractVillager
implements ReputationEventHandler,
VillagerDataHolder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.VILLAGER_DATA);
    public static final int BREEDING_FOOD_THRESHOLD = 12;
    public static final Map<Item, Integer> FOOD_POINTS = ImmutableMap.of((Object)Items.BREAD, (Object)4, (Object)Items.POTATO, (Object)1, (Object)Items.CARROT, (Object)1, (Object)Items.BEETROOT, (Object)1);
    private static final int TRADES_PER_LEVEL = 2;
    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of((Object)Items.BREAD, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT, (Object[])new Item[]{Items.BEETROOT_SEEDS});
    private static final int MAX_GOSSIP_TOPICS = 10;
    private static final int GOSSIP_COOLDOWN = 1200;
    private static final int GOSSIP_DECAY_INTERVAL = 24000;
    private static final int REPUTATION_CHANGE_PER_EVENT = 25;
    private static final int HOW_FAR_AWAY_TO_TALK_TO_OTHER_VILLAGERS_ABOUT_GOLEMS = 10;
    private static final int HOW_MANY_VILLAGERS_NEED_TO_AGREE_TO_SPAWN_A_GOLEM = 5;
    private static final long TIME_SINCE_SLEEPING_FOR_GOLEM_SPAWNING = 24000L;
    @VisibleForTesting
    public static final float SPEED_MODIFIER = 0.5f;
    private int updateMerchantTimer;
    private boolean increaseProfessionLevelOnUpdate;
    @Nullable
    private Player lastTradedPlayer;
    private boolean chasing;
    private int foodLevel;
    private final GossipContainer gossips = new GossipContainer();
    private long lastGossipTime;
    private long lastGossipDecayTime;
    private int villagerXp;
    private long lastRestockGameTime;
    private int numberOfRestocksToday;
    private long lastRestockCheckDayTime;
    private boolean assignProfessionWhenSpawned;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, (Object[])new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY});
    private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<Villager, Holder<PoiType>>> POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, ($$0, $$1) -> $$1.is(PoiTypes.HOME), MemoryModuleType.JOB_SITE, ($$0, $$1) -> $$0.getVillagerData().getProfession().heldJobSite().test($$1), MemoryModuleType.POTENTIAL_JOB_SITE, ($$0, $$1) -> VillagerProfession.ALL_ACQUIRABLE_JOBS.test($$1), MemoryModuleType.MEETING_POINT, ($$0, $$1) -> $$1.is(PoiTypes.MEETING));

    public Villager(EntityType<? extends Villager> $$0, Level $$1) {
        this($$0, $$1, VillagerType.PLAINS);
    }

    public Villager(EntityType<? extends Villager> $$0, Level $$1, VillagerType $$2) {
        super((EntityType<? extends AbstractVillager>)$$0, $$1);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().setType($$2).setProfession(VillagerProfession.NONE));
    }

    public Brain<Villager> getBrain() {
        return super.getBrain();
    }

    protected Brain.Provider<Villager> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        Brain<Villager> $$1 = this.brainProvider().makeBrain($$0);
        this.registerBrainGoals($$1);
        return $$1;
    }

    public void refreshBrain(ServerLevel $$0) {
        Brain<Villager> $$1 = this.getBrain();
        $$1.stopAll($$0, this);
        this.brain = $$1.copyWithoutBehaviors();
        this.registerBrainGoals(this.getBrain());
    }

    private void registerBrainGoals(Brain<Villager> $$0) {
        VillagerProfession $$1 = this.getVillagerData().getProfession();
        if (this.isBaby()) {
            $$0.setSchedule(Schedule.VILLAGER_BABY);
            $$0.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5f));
        } else {
            $$0.setSchedule(Schedule.VILLAGER_DEFAULT);
            $$0.addActivityWithConditions(Activity.WORK, (ImmutableList<Pair<Integer, BehaviorControl<Villager>>>)VillagerGoalPackages.getWorkPackage($$1, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT))));
        }
        $$0.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage($$1, 0.5f));
        $$0.addActivityWithConditions(Activity.MEET, (ImmutableList<Pair<Integer, BehaviorControl<Villager>>>)VillagerGoalPackages.getMeetPackage($$1, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryStatus.VALUE_PRESENT))));
        $$0.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage($$1, 0.5f));
        $$0.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage($$1, 0.5f));
        $$0.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage($$1, 0.5f));
        $$0.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage($$1, 0.5f));
        $$0.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage($$1, 0.5f));
        $$0.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage($$1, 0.5f));
        $$0.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        $$0.setDefaultActivity(Activity.IDLE);
        $$0.setActiveActivityIfPossible(Activity.IDLE);
        $$0.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.level instanceof ServerLevel) {
            this.refreshBrain((ServerLevel)this.level);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 48.0);
    }

    public boolean assignProfessionWhenSpawned() {
        return this.assignProfessionWhenSpawned;
    }

    @Override
    protected void customServerAiStep() {
        Raid $$0;
        this.level.getProfiler().push("villagerBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        if (this.assignProfessionWhenSpawned) {
            this.assignProfessionWhenSpawned = false;
        }
        if (!this.isTrading() && this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
            if (this.updateMerchantTimer <= 0) {
                if (this.increaseProfessionLevelOnUpdate) {
                    this.increaseMerchantCareer();
                    this.increaseProfessionLevelOnUpdate = false;
                }
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastTradedPlayer != null && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).onReputationEvent(ReputationEventType.TRADE, this.lastTradedPlayer, this);
            this.level.broadcastEntityEvent(this, (byte)14);
            this.lastTradedPlayer = null;
        }
        if (!this.isNoAi() && this.random.nextInt(100) == 0 && ($$0 = ((ServerLevel)this.level).getRaidAt(this.blockPosition())) != null && $$0.isActive() && !$$0.isOver()) {
            this.level.broadcastEntityEvent(this, (byte)42);
        }
        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
            this.stopTrading();
        }
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getUnhappyCounter() > 0) {
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }
        this.maybeDecayGossip();
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (!$$2.is(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.setUnhappy();
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            boolean $$3 = this.getOffers().isEmpty();
            if ($$1 == InteractionHand.MAIN_HAND) {
                if ($$3 && !this.level.isClientSide) {
                    this.setUnhappy();
                }
                $$0.awardStat(Stats.TALKED_TO_VILLAGER);
            }
            if ($$3) {
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            if (!this.level.isClientSide && !this.offers.isEmpty()) {
                this.startTrading($$0);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract($$0, $$1);
    }

    private void setUnhappy() {
        this.setUnhappyCounter(40);
        if (!this.level.isClientSide()) {
            this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private void startTrading(Player $$0) {
        this.updateSpecialPrices($$0);
        this.setTradingPlayer($$0);
        this.openTradingScreen($$0, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setTradingPlayer(@Nullable Player $$0) {
        boolean $$1 = this.getTradingPlayer() != null && $$0 == null;
        super.setTradingPlayer($$0);
        if ($$1) {
            this.stopTrading();
        }
    }

    @Override
    protected void stopTrading() {
        super.stopTrading();
        this.resetSpecialPrices();
    }

    private void resetSpecialPrices() {
        Iterator iterator = this.getOffers().iterator();
        while (iterator.hasNext()) {
            MerchantOffer $$0 = (MerchantOffer)iterator.next();
            $$0.resetSpecialPriceDiff();
        }
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    @Override
    public boolean isClientSide() {
        return this.getLevel().isClientSide;
    }

    public void restock() {
        this.updateDemand();
        Iterator iterator = this.getOffers().iterator();
        while (iterator.hasNext()) {
            MerchantOffer $$0 = (MerchantOffer)iterator.next();
            $$0.resetUses();
        }
        this.lastRestockGameTime = this.level.getGameTime();
        ++this.numberOfRestocksToday;
    }

    private boolean needsToRestock() {
        Iterator iterator = this.getOffers().iterator();
        while (iterator.hasNext()) {
            MerchantOffer $$0 = (MerchantOffer)iterator.next();
            if (!$$0.needsRestock()) continue;
            return true;
        }
        return false;
    }

    private boolean allowedToRestock() {
        return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
    }

    public boolean shouldRestock() {
        long $$0 = this.lastRestockGameTime + 12000L;
        long $$1 = this.level.getGameTime();
        boolean $$2 = $$1 > $$0;
        long $$3 = this.level.getDayTime();
        if (this.lastRestockCheckDayTime > 0L) {
            long $$5 = $$3 / 24000L;
            long $$4 = this.lastRestockCheckDayTime / 24000L;
            $$2 |= $$5 > $$4;
        }
        this.lastRestockCheckDayTime = $$3;
        if ($$2) {
            this.lastRestockGameTime = $$1;
            this.resetNumberOfRestocks();
        }
        return this.allowedToRestock() && this.needsToRestock();
    }

    private void catchUpDemand() {
        int $$0 = 2 - this.numberOfRestocksToday;
        if ($$0 > 0) {
            Iterator iterator = this.getOffers().iterator();
            while (iterator.hasNext()) {
                MerchantOffer $$1 = (MerchantOffer)iterator.next();
                $$1.resetUses();
            }
        }
        for (int $$2 = 0; $$2 < $$0; ++$$2) {
            this.updateDemand();
        }
    }

    private void updateDemand() {
        Iterator iterator = this.getOffers().iterator();
        while (iterator.hasNext()) {
            MerchantOffer $$0 = (MerchantOffer)iterator.next();
            $$0.updateDemand();
        }
    }

    private void updateSpecialPrices(Player $$0) {
        int $$1 = this.getPlayerReputation($$0);
        if ($$1 != 0) {
            Iterator iterator = this.getOffers().iterator();
            while (iterator.hasNext()) {
                MerchantOffer $$2 = (MerchantOffer)iterator.next();
                $$2.addToSpecialPriceDiff(-Mth.floor((float)$$1 * $$2.getPriceMultiplier()));
            }
        }
        if ($$0.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffectInstance $$3 = $$0.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int $$4 = $$3.getAmplifier();
            Iterator iterator = this.getOffers().iterator();
            while (iterator.hasNext()) {
                MerchantOffer $$5 = (MerchantOffer)iterator.next();
                double $$6 = 0.3 + 0.0625 * (double)$$4;
                int $$7 = (int)Math.floor((double)($$6 * (double)$$5.getBaseCostA().getCount()));
                $$5.addToSpecialPriceDiff(-Math.max((int)$$7, (int)1));
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        VillagerData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.getVillagerData()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("VillagerData", (Tag)$$1));
        $$0.putByte("FoodLevel", (byte)this.foodLevel);
        $$0.put("Gossips", this.gossips.store(NbtOps.INSTANCE));
        $$0.putInt("Xp", this.villagerXp);
        $$0.putLong("LastRestock", this.lastRestockGameTime);
        $$0.putLong("LastGossipDecay", this.lastGossipDecayTime);
        $$0.putInt("RestocksToday", this.numberOfRestocksToday);
        if (this.assignProfessionWhenSpawned) {
            $$0.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("VillagerData", 10)) {
            DataResult $$1 = VillagerData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$0.get("VillagerData")));
            $$1.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(this::setVillagerData);
        }
        if ($$0.contains("Offers", 10)) {
            this.offers = new MerchantOffers($$0.getCompound("Offers"));
        }
        if ($$0.contains("FoodLevel", 1)) {
            this.foodLevel = $$0.getByte("FoodLevel");
        }
        ListTag $$2 = $$0.getList("Gossips", 10);
        this.gossips.update(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$2));
        if ($$0.contains("Xp", 3)) {
            this.villagerXp = $$0.getInt("Xp");
        }
        this.lastRestockGameTime = $$0.getLong("LastRestock");
        this.lastGossipDecayTime = $$0.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        if (this.level instanceof ServerLevel) {
            this.refreshBrain((ServerLevel)this.level);
        }
        this.numberOfRestocksToday = $$0.getInt("RestocksToday");
        if ($$0.contains("AssignProfessionWhenSpawned")) {
            this.assignProfessionWhenSpawned = $$0.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.isTrading()) {
            return SoundEvents.VILLAGER_TRADE;
        }
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    public void playWorkSound() {
        SoundEvent $$0 = this.getVillagerData().getProfession().workSound();
        if ($$0 != null) {
            this.playSound($$0, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public void setVillagerData(VillagerData $$0) {
        VillagerData $$1 = this.getVillagerData();
        if ($$1.getProfession() != $$0.getProfession()) {
            this.offers = null;
        }
        this.entityData.set(DATA_VILLAGER_DATA, $$0);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer $$0) {
        int $$1 = 3 + this.random.nextInt(4);
        this.villagerXp += $$0.getXp();
        this.lastTradedPlayer = this.getTradingPlayer();
        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            $$1 += 5;
        }
        if ($$0.shouldRewardExp()) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY() + 0.5, this.getZ(), $$1));
        }
    }

    public void setChasing(boolean $$0) {
        this.chasing = $$0;
    }

    public boolean isChasing() {
        return this.chasing;
    }

    @Override
    public void setLastHurtByMob(@Nullable LivingEntity $$0) {
        if ($$0 != null && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).onReputationEvent(ReputationEventType.VILLAGER_HURT, $$0, this);
            if (this.isAlive() && $$0 instanceof Player) {
                this.level.broadcastEntityEvent(this, (byte)13);
            }
        }
        super.setLastHurtByMob($$0);
    }

    @Override
    public void die(DamageSource $$0) {
        LOGGER.info("Villager {} died, message: '{}'", (Object)this, (Object)$$0.getLocalizedDeathMessage(this).getString());
        Entity $$1 = $$0.getEntity();
        if ($$1 != null) {
            this.tellWitnessesThatIWasMurdered($$1);
        }
        this.releaseAllPois();
        super.die($$0);
    }

    private void releaseAllPois() {
        this.releasePoi(MemoryModuleType.HOME);
        this.releasePoi(MemoryModuleType.JOB_SITE);
        this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releasePoi(MemoryModuleType.MEETING_POINT);
    }

    /*
     * WARNING - void declaration
     */
    private void tellWitnessesThatIWasMurdered(Entity $$0) {
        void $$2;
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel $$1 = (ServerLevel)level;
        Optional<NearestVisibleLivingEntities> $$3 = this.brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if ($$3.isEmpty()) {
            return;
        }
        ((NearestVisibleLivingEntities)$$3.get()).findAll((Predicate<LivingEntity>)((Predicate)arg_0 -> ReputationEventHandler.class.isInstance(arg_0))).forEach(arg_0 -> Villager.lambda$tellWitnessesThatIWasMurdered$5((ServerLevel)$$2, $$0, arg_0));
    }

    public void releasePoi(MemoryModuleType<GlobalPos> $$0) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        MinecraftServer $$1 = ((ServerLevel)this.level).getServer();
        this.brain.getMemory($$0).ifPresent($$2 -> {
            ServerLevel $$3 = $$1.getLevel($$2.dimension());
            if ($$3 == null) {
                return;
            }
            PoiManager $$4 = $$3.getPoiManager();
            Optional<Holder<PoiType>> $$5 = $$4.getType($$2.pos());
            BiPredicate $$6 = (BiPredicate)POI_MEMORIES.get((Object)$$0);
            if ($$5.isPresent() && $$6.test((Object)this, (Object)((Holder)$$5.get()))) {
                $$4.release($$2.pos());
                DebugPackets.sendPoiTicketCountPacket($$3, $$2.pos());
            }
        });
    }

    @Override
    public boolean canBreed() {
        return this.foodLevel + this.countFoodPointsInInventory() >= 12 && !this.isSleeping() && this.getAge() == 0;
    }

    private boolean hungry() {
        return this.foodLevel < 12;
    }

    private void eatUntilFull() {
        if (!this.hungry() || this.countFoodPointsInInventory() == 0) {
            return;
        }
        for (int $$0 = 0; $$0 < this.getInventory().getContainerSize(); ++$$0) {
            int $$3;
            Integer $$2;
            ItemStack $$1 = this.getInventory().getItem($$0);
            if ($$1.isEmpty() || ($$2 = (Integer)FOOD_POINTS.get((Object)$$1.getItem())) == null) continue;
            for (int $$4 = $$3 = $$1.getCount(); $$4 > 0; --$$4) {
                this.foodLevel += $$2.intValue();
                this.getInventory().removeItem($$0, 1);
                if (this.hungry()) continue;
                return;
            }
        }
    }

    public int getPlayerReputation(Player $$02) {
        return this.gossips.getReputation($$02.getUUID(), (Predicate<GossipType>)((Predicate)$$0 -> true));
    }

    private void digestFood(int $$0) {
        this.foodLevel -= $$0;
    }

    public void eatAndDigestFood() {
        this.eatUntilFull();
        this.digestFood(12);
    }

    public void setOffers(MerchantOffers $$0) {
        this.offers = $$0;
    }

    private boolean shouldIncreaseLevel() {
        int $$0 = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp($$0) && this.villagerXp >= VillagerData.getMaxXpPerLevel($$0);
    }

    private void increaseMerchantCareer() {
        this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
        this.updateTrades();
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getType().getDescriptionId() + "." + BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getPath());
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 12) {
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        } else if ($$0 == 13) {
            this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
        } else if ($$0 == 14) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        } else if ($$0 == 42) {
            this.addParticlesAroundSelf(ParticleTypes.SPLASH);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        if ($$2 == MobSpawnType.BREEDING) {
            this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
        }
        if ($$2 == MobSpawnType.COMMAND || $$2 == MobSpawnType.SPAWN_EGG || $$2 == MobSpawnType.SPAWNER || $$2 == MobSpawnType.DISPENSER) {
            this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome($$0.getBiome(this.blockPosition()))));
        }
        if ($$2 == MobSpawnType.STRUCTURE) {
            this.assignProfessionWhenSpawned = true;
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    @Nullable
    public Villager getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        VillagerType $$5;
        double $$2 = this.random.nextDouble();
        if ($$2 < 0.5) {
            VillagerType $$3 = VillagerType.byBiome($$0.getBiome(this.blockPosition()));
        } else if ($$2 < 0.75) {
            VillagerType $$4 = this.getVillagerData().getType();
        } else {
            $$5 = ((Villager)$$1).getVillagerData().getType();
        }
        Villager $$6 = new Villager(EntityType.VILLAGER, $$0, $$5);
        $$6.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$6.blockPosition()), MobSpawnType.BREEDING, null, null);
        return $$6;
    }

    @Override
    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
        if ($$0.getDifficulty() != Difficulty.PEACEFUL) {
            LOGGER.info("Villager {} was struck by lightning {}.", (Object)this, (Object)$$1);
            Witch $$2 = EntityType.WITCH.create($$0);
            if ($$2 != null) {
                $$2.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                $$2.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$2.blockPosition()), MobSpawnType.CONVERSION, null, null);
                $$2.setNoAi(this.isNoAi());
                if (this.hasCustomName()) {
                    $$2.setCustomName(this.getCustomName());
                    $$2.setCustomNameVisible(this.isCustomNameVisible());
                }
                $$2.setPersistenceRequired();
                $$0.addFreshEntityWithPassengers($$2);
                this.releaseAllPois();
                this.discard();
            } else {
                super.thunderHit($$0, $$1);
            }
        } else {
            super.thunderHit($$0, $$1);
        }
    }

    @Override
    protected void pickUpItem(ItemEntity $$0) {
        InventoryCarrier.pickUpItem(this, this, $$0);
    }

    @Override
    public boolean wantsToPickUp(ItemStack $$0) {
        Item $$1 = $$0.getItem();
        return (WANTED_ITEMS.contains((Object)$$1) || this.getVillagerData().getProfession().requestedItems().contains((Object)$$1)) && this.getInventory().canAddItem($$0);
    }

    public boolean hasExcessFood() {
        return this.countFoodPointsInInventory() >= 24;
    }

    public boolean wantsMoreFood() {
        return this.countFoodPointsInInventory() < 12;
    }

    private int countFoodPointsInInventory() {
        SimpleContainer $$0 = this.getInventory();
        return FOOD_POINTS.entrySet().stream().mapToInt($$1 -> $$0.countItem((Item)$$1.getKey()) * (Integer)$$1.getValue()).sum();
    }

    public boolean hasFarmSeeds() {
        return this.getInventory().hasAnyOf((Set)ImmutableSet.of((Object)Items.WHEAT_SEEDS, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.BEETROOT_SEEDS));
    }

    @Override
    protected void updateTrades() {
        VillagerData $$0 = this.getVillagerData();
        Int2ObjectMap $$1 = (Int2ObjectMap)VillagerTrades.TRADES.get((Object)$$0.getProfession());
        if ($$1 == null || $$1.isEmpty()) {
            return;
        }
        VillagerTrades.ItemListing[] $$2 = (VillagerTrades.ItemListing[])$$1.get($$0.getLevel());
        if ($$2 == null) {
            return;
        }
        MerchantOffers $$3 = this.getOffers();
        this.addOffersFromItemListings($$3, $$2, 2);
    }

    public void gossip(ServerLevel $$0, Villager $$1, long $$2) {
        if ($$2 >= this.lastGossipTime && $$2 < this.lastGossipTime + 1200L || $$2 >= $$1.lastGossipTime && $$2 < $$1.lastGossipTime + 1200L) {
            return;
        }
        this.gossips.transferFrom($$1.gossips, this.random, 10);
        this.lastGossipTime = $$2;
        $$1.lastGossipTime = $$2;
        this.spawnGolemIfNeeded($$0, $$2, 5);
    }

    private void maybeDecayGossip() {
        long $$0 = this.level.getGameTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = $$0;
            return;
        }
        if ($$0 < this.lastGossipDecayTime + 24000L) {
            return;
        }
        this.gossips.decay();
        this.lastGossipDecayTime = $$0;
    }

    public void spawnGolemIfNeeded(ServerLevel $$0, long $$12, int $$2) {
        if (!this.wantsToSpawnGolem($$12)) {
            return;
        }
        AABB $$3 = this.getBoundingBox().inflate(10.0, 10.0, 10.0);
        List $$4 = $$0.getEntitiesOfClass(Villager.class, $$3);
        List $$5 = (List)$$4.stream().filter($$1 -> $$1.wantsToSpawnGolem($$12)).limit(5L).collect(Collectors.toList());
        if ($$5.size() < $$2) {
            return;
        }
        if (!SpawnUtil.trySpawnMob(EntityType.IRON_GOLEM, MobSpawnType.MOB_SUMMONED, $$0, this.blockPosition(), 10, 8, 6, SpawnUtil.Strategy.LEGACY_IRON_GOLEM).isPresent()) {
            return;
        }
        $$4.forEach(GolemSensor::golemDetected);
    }

    public boolean wantsToSpawnGolem(long $$0) {
        if (!this.golemSpawnConditionsMet(this.level.getGameTime())) {
            return false;
        }
        return !this.brain.hasMemoryValue(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }

    @Override
    public void onReputationEventFrom(ReputationEventType $$0, Entity $$1) {
        if ($$0 == ReputationEventType.ZOMBIE_VILLAGER_CURED) {
            this.gossips.add($$1.getUUID(), GossipType.MAJOR_POSITIVE, 20);
            this.gossips.add($$1.getUUID(), GossipType.MINOR_POSITIVE, 25);
        } else if ($$0 == ReputationEventType.TRADE) {
            this.gossips.add($$1.getUUID(), GossipType.TRADING, 2);
        } else if ($$0 == ReputationEventType.VILLAGER_HURT) {
            this.gossips.add($$1.getUUID(), GossipType.MINOR_NEGATIVE, 25);
        } else if ($$0 == ReputationEventType.VILLAGER_KILLED) {
            this.gossips.add($$1.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
        }
    }

    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int $$0) {
        this.villagerXp = $$0;
    }

    private void resetNumberOfRestocks() {
        this.catchUpDemand();
        this.numberOfRestocksToday = 0;
    }

    public GossipContainer getGossips() {
        return this.gossips;
    }

    public void setGossips(Tag $$0) {
        this.gossips.update(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$0));
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void startSleeping(BlockPos $$0) {
        super.startSleeping($$0);
        this.brain.setMemory(MemoryModuleType.LAST_SLEPT, this.level.getGameTime());
        this.brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void stopSleeping() {
        super.stopSleeping();
        this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.level.getGameTime());
    }

    private boolean golemSpawnConditionsMet(long $$0) {
        Optional<Long> $$1 = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
        if ($$1.isPresent()) {
            return $$0 - (Long)$$1.get() < 24000L;
        }
        return false;
    }

    private static /* synthetic */ void lambda$tellWitnessesThatIWasMurdered$5(ServerLevel $$0, Entity $$1, LivingEntity $$2) {
        $$0.onReputationEvent(ReputationEventType.VILLAGER_KILLED, $$1, (ReputationEventHandler)((Object)$$2));
    }
}