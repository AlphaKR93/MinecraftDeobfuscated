/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.IntFunction
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Parrot
extends ShoulderRidingEntity
implements VariantHolder<Variant>,
FlyingAnimal {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Parrot.class, EntityDataSerializers.INT);
    private static final Predicate<Mob> NOT_PARROT_PREDICATE = new Predicate<Mob>(){

        public boolean test(@Nullable Mob $$0) {
            return $$0 != null && MOB_SOUND_MAP.containsKey($$0.getType());
        }
    };
    private static final Item POISONOUS_FOOD = Items.COOKIE;
    private static final Set<Item> TAME_FOOD = Sets.newHashSet((Object[])new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    static final Map<EntityType<?>, SoundEvent> MOB_SOUND_MAP = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put(EntityType.BLAZE, (Object)SoundEvents.PARROT_IMITATE_BLAZE);
        $$0.put(EntityType.CAVE_SPIDER, (Object)SoundEvents.PARROT_IMITATE_SPIDER);
        $$0.put(EntityType.CREEPER, (Object)SoundEvents.PARROT_IMITATE_CREEPER);
        $$0.put(EntityType.DROWNED, (Object)SoundEvents.PARROT_IMITATE_DROWNED);
        $$0.put(EntityType.ELDER_GUARDIAN, (Object)SoundEvents.PARROT_IMITATE_ELDER_GUARDIAN);
        $$0.put(EntityType.ENDER_DRAGON, (Object)SoundEvents.PARROT_IMITATE_ENDER_DRAGON);
        $$0.put(EntityType.ENDERMITE, (Object)SoundEvents.PARROT_IMITATE_ENDERMITE);
        $$0.put(EntityType.EVOKER, (Object)SoundEvents.PARROT_IMITATE_EVOKER);
        $$0.put(EntityType.GHAST, (Object)SoundEvents.PARROT_IMITATE_GHAST);
        $$0.put(EntityType.GUARDIAN, (Object)SoundEvents.PARROT_IMITATE_GUARDIAN);
        $$0.put(EntityType.HOGLIN, (Object)SoundEvents.PARROT_IMITATE_HOGLIN);
        $$0.put(EntityType.HUSK, (Object)SoundEvents.PARROT_IMITATE_HUSK);
        $$0.put(EntityType.ILLUSIONER, (Object)SoundEvents.PARROT_IMITATE_ILLUSIONER);
        $$0.put(EntityType.MAGMA_CUBE, (Object)SoundEvents.PARROT_IMITATE_MAGMA_CUBE);
        $$0.put(EntityType.PHANTOM, (Object)SoundEvents.PARROT_IMITATE_PHANTOM);
        $$0.put(EntityType.PIGLIN, (Object)SoundEvents.PARROT_IMITATE_PIGLIN);
        $$0.put(EntityType.PIGLIN_BRUTE, (Object)SoundEvents.PARROT_IMITATE_PIGLIN_BRUTE);
        $$0.put(EntityType.PILLAGER, (Object)SoundEvents.PARROT_IMITATE_PILLAGER);
        $$0.put(EntityType.RAVAGER, (Object)SoundEvents.PARROT_IMITATE_RAVAGER);
        $$0.put(EntityType.SHULKER, (Object)SoundEvents.PARROT_IMITATE_SHULKER);
        $$0.put(EntityType.SILVERFISH, (Object)SoundEvents.PARROT_IMITATE_SILVERFISH);
        $$0.put(EntityType.SKELETON, (Object)SoundEvents.PARROT_IMITATE_SKELETON);
        $$0.put(EntityType.SLIME, (Object)SoundEvents.PARROT_IMITATE_SLIME);
        $$0.put(EntityType.SPIDER, (Object)SoundEvents.PARROT_IMITATE_SPIDER);
        $$0.put(EntityType.STRAY, (Object)SoundEvents.PARROT_IMITATE_STRAY);
        $$0.put(EntityType.VEX, (Object)SoundEvents.PARROT_IMITATE_VEX);
        $$0.put(EntityType.VINDICATOR, (Object)SoundEvents.PARROT_IMITATE_VINDICATOR);
        $$0.put(EntityType.WARDEN, (Object)SoundEvents.PARROT_IMITATE_WARDEN);
        $$0.put(EntityType.WITCH, (Object)SoundEvents.PARROT_IMITATE_WITCH);
        $$0.put(EntityType.WITHER, (Object)SoundEvents.PARROT_IMITATE_WITHER);
        $$0.put(EntityType.WITHER_SKELETON, (Object)SoundEvents.PARROT_IMITATE_WITHER_SKELETON);
        $$0.put(EntityType.ZOGLIN, (Object)SoundEvents.PARROT_IMITATE_ZOGLIN);
        $$0.put(EntityType.ZOMBIE, (Object)SoundEvents.PARROT_IMITATE_ZOMBIE);
        $$0.put(EntityType.ZOMBIE_VILLAGER, (Object)SoundEvents.PARROT_IMITATE_ZOMBIE_VILLAGER);
    });
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0f;
    private float nextFlap = 1.0f;
    private boolean partyParrot;
    @Nullable
    private BlockPos jukebox;

    public Parrot(EntityType<? extends Parrot> $$0, Level $$1) {
        super((EntityType<? extends ShoulderRidingEntity>)$$0, $$1);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0f);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.setVariant(Util.getRandom(Variant.values(), $$0.getRandom()));
        if ($$3 == null) {
            $$3 = new AgeableMob.AgeableMobGroupData(false);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 5.0f, 1.0f, true));
        this.goalSelector.addGoal(2, new ParrotWanderGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0, 3.0f, 7.0f));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.FLYING_SPEED, 0.4f).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        FlyingPathNavigation $$1 = new FlyingPathNavigation(this, $$0);
        $$1.setCanOpenDoors(false);
        $$1.setCanFloat(true);
        $$1.setCanPassDoors(true);
        return $$1;
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.6f;
    }

    @Override
    public void aiStep() {
        if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46) || !this.level.getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.partyParrot = false;
            this.jukebox = null;
        }
        if (this.level.random.nextInt(400) == 0) {
            Parrot.imitateNearbyMobs(this.level, this);
        }
        super.aiStep();
        this.calculateFlapping();
    }

    @Override
    public void setRecordPlayingNearby(BlockPos $$0, boolean $$1) {
        this.jukebox = $$0;
        this.partyParrot = $$1;
    }

    public boolean isPartyParrot() {
        return this.partyParrot;
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (float)(this.onGround || this.isPassenger() ? -1 : 4) * 0.3f;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0f, 1.0f);
        if (!this.onGround && this.flapping < 1.0f) {
            this.flapping = 1.0f;
        }
        this.flapping *= 0.9f;
        Vec3 $$0 = this.getDeltaMovement();
        if (!this.onGround && $$0.y < 0.0) {
            this.setDeltaMovement($$0.multiply(1.0, 0.6, 1.0));
        }
        this.flap += this.flapping * 2.0f;
    }

    public static boolean imitateNearbyMobs(Level $$0, Entity $$1) {
        Mob $$3;
        if (!$$1.isAlive() || $$1.isSilent() || $$0.random.nextInt(2) != 0) {
            return false;
        }
        List $$2 = $$0.getEntitiesOfClass(Mob.class, $$1.getBoundingBox().inflate(20.0), NOT_PARROT_PREDICATE);
        if (!$$2.isEmpty() && !($$3 = (Mob)$$2.get($$0.random.nextInt($$2.size()))).isSilent()) {
            SoundEvent $$4 = Parrot.getImitatedSound($$3.getType());
            $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), $$4, $$1.getSoundSource(), 0.7f, Parrot.getPitch($$0.random));
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (!this.isTame() && TAME_FOOD.contains((Object)$$2.getItem())) {
            if (!$$0.getAbilities().instabuild) {
                $$2.shrink(1);
            }
            if (!this.isSilent()) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
            if (!this.level.isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.tame($$0);
                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if ($$2.is(POISONOUS_FOOD)) {
            if (!$$0.getAbilities().instabuild) {
                $$2.shrink(1);
            }
            this.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
            if ($$0.isCreative() || !this.isInvulnerable()) {
                this.hurt(DamageSource.playerAttack($$0), Float.MAX_VALUE);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (!this.isFlying() && this.isTame() && this.isOwnedBy($$0)) {
            if (!this.level.isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract($$0, $$1);
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return false;
    }

    public static boolean checkParrotSpawnRules(EntityType<Parrot> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        return $$1.getBlockState((BlockPos)$$3.below()).is(BlockTags.PARROTS_SPAWNABLE_ON) && Parrot.isBrightEnoughToSpawn($$1, $$3);
    }

    @Override
    public boolean causeFallDamage(float $$0, float $$1, DamageSource $$2) {
        return false;
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
    }

    @Override
    public boolean canMate(Animal $$0) {
        return false;
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return null;
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        return $$0.hurt(DamageSource.mobAttack(this), 3.0f);
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound() {
        return Parrot.getAmbient(this.level, this.level.random);
    }

    public static SoundEvent getAmbient(Level $$0, RandomSource $$1) {
        if ($$0.getDifficulty() != Difficulty.PEACEFUL && $$1.nextInt(1000) == 0) {
            ArrayList $$2 = Lists.newArrayList((Iterable)MOB_SOUND_MAP.keySet());
            return Parrot.getImitatedSound((EntityType)$$2.get($$1.nextInt($$2.size())));
        }
        return SoundEvents.PARROT_AMBIENT;
    }

    private static SoundEvent getImitatedSound(EntityType<?> $$0) {
        return (SoundEvent)MOB_SOUND_MAP.getOrDefault($$0, (Object)SoundEvents.PARROT_AMBIENT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.PARROT_STEP, 0.15f, 1.0f);
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.playSound(SoundEvents.PARROT_FLY, 0.15f, 1.0f);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0f;
    }

    @Override
    public float getVoicePitch() {
        return Parrot.getPitch(this.random);
    }

    public static float getPitch(RandomSource $$0) {
        return ($$0.nextFloat() - $$0.nextFloat()) * 0.2f + 1.0f;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void doPush(Entity $$0) {
        if ($$0 instanceof Player) {
            return;
        }
        super.doPush($$0);
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (!this.level.isClientSide) {
            this.setOrderedToSit(false);
        }
        return super.hurt($$0, $$1);
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT_ID));
    }

    @Override
    public void setVariant(Variant $$0) {
        this.entityData.set(DATA_VARIANT_ID, $$0.id);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("Variant", this.getVariant().id);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setVariant(Variant.byId($$0.getInt("Variant")));
    }

    @Override
    public boolean isFlying() {
        return !this.onGround;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.5f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    public static enum Variant implements StringRepresentable
    {
        RED_BLUE(0, "red_blue"),
        BLUE(1, "blue"),
        GREEN(2, "green"),
        YELLOW_BLUE(3, "yellow_blue"),
        GRAY(4, "gray");

        public static final Codec<Variant> CODEC;
        private static final IntFunction<Variant> BY_ID;
        final int id;
        private final String name;

        private Variant(int $$0, String $$1) {
            this.id = $$0;
            this.name = $$1;
        }

        public int getId() {
            return this.id;
        }

        public static Variant byId(int $$0) {
            return (Variant)BY_ID.apply($$0);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Variant::values));
            BY_ID = ByIdMap.continuous(Variant::getId, Variant.values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        }
    }

    static class ParrotWanderGoal
    extends WaterAvoidingRandomFlyingGoal {
        public ParrotWanderGoal(PathfinderMob $$0, double $$1) {
            super($$0, $$1);
        }

        @Override
        @Nullable
        protected Vec3 getPosition() {
            Vec3 $$0 = null;
            if (this.mob.isInWater()) {
                $$0 = LandRandomPos.getPos(this.mob, 15, 15);
            }
            if (this.mob.getRandom().nextFloat() >= this.probability) {
                $$0 = this.getTreePos();
            }
            return $$0 == null ? super.getPosition() : $$0;
        }

        @Nullable
        private Vec3 getTreePos() {
            BlockPos $$0 = this.mob.blockPosition();
            BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos();
            Iterable<BlockPos> $$3 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0), Mth.floor(this.mob.getY() - 6.0), Mth.floor(this.mob.getZ() - 3.0), Mth.floor(this.mob.getX() + 3.0), Mth.floor(this.mob.getY() + 6.0), Mth.floor(this.mob.getZ() + 3.0));
            for (BlockPos $$4 : $$3) {
                BlockState $$5;
                boolean $$6;
                if ($$0.equals($$4) || !($$6 = ($$5 = this.mob.level.getBlockState($$2.setWithOffset((Vec3i)$$4, Direction.DOWN))).getBlock() instanceof LeavesBlock || $$5.is(BlockTags.LOGS)) || !this.mob.level.isEmptyBlock($$4) || !this.mob.level.isEmptyBlock($$1.setWithOffset((Vec3i)$$4, Direction.UP))) continue;
                return Vec3.atBottomCenterOf($$4);
            }
            return null;
        }
    }
}