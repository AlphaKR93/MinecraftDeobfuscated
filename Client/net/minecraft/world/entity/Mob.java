/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Byte
 *  java.lang.Float
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class Mob
extends LivingEntity {
    private static final EntityDataAccessor<Byte> DATA_MOB_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
    private static final int MOB_FLAG_NO_AI = 1;
    private static final int MOB_FLAG_LEFTHANDED = 2;
    private static final int MOB_FLAG_AGGRESSIVE = 4;
    protected static final int PICKUP_REACH = 1;
    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 0, 1);
    public static final float MAX_WEARING_ARMOR_CHANCE = 0.15f;
    public static final float MAX_PICKUP_LOOT_CHANCE = 0.55f;
    public static final float MAX_ENCHANTED_ARMOR_CHANCE = 0.5f;
    public static final float MAX_ENCHANTED_WEAPON_CHANCE = 0.25f;
    public static final String LEASH_TAG = "Leash";
    public static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085f;
    public static final int PRESERVE_ITEM_DROP_CHANCE = 2;
    public static final int UPDATE_GOAL_SELECTOR_EVERY_N_TICKS = 2;
    public int ambientSoundTime;
    protected int xpReward;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyRotationControl bodyRotationControl;
    protected PathNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    @Nullable
    private LivingEntity target;
    private final Sensing sensing;
    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    protected final float[] handDropChances = new float[2];
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    protected final float[] armorDropChances = new float[4];
    private boolean canPickUpLoot;
    private boolean persistenceRequired;
    private final Map<BlockPathTypes, Float> pathfindingMalus = Maps.newEnumMap(BlockPathTypes.class);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity leashHolder;
    private int delayedLeashHolderId;
    @Nullable
    private CompoundTag leashInfoTag;
    private BlockPos restrictCenter = BlockPos.ZERO;
    private float restrictRadius = -1.0f;

    protected Mob(EntityType<? extends Mob> $$0, Level $$1) {
        super((EntityType<? extends LivingEntity>)$$0, $$1);
        this.goalSelector = new GoalSelector($$1.getProfilerSupplier());
        this.targetSelector = new GoalSelector($$1.getProfilerSupplier());
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyRotationControl = this.createBodyControl();
        this.navigation = this.createNavigation($$1);
        this.sensing = new Sensing(this);
        Arrays.fill((float[])this.armorDropChances, (float)0.085f);
        Arrays.fill((float[])this.handDropChances, (float)0.085f);
        if ($$1 != null && !$$1.isClientSide) {
            this.registerGoals();
        }
    }

    protected void registerGoals() {
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0).add(Attributes.ATTACK_KNOCKBACK);
    }

    protected PathNavigation createNavigation(Level $$0) {
        return new GroundPathNavigation(this, $$0);
    }

    protected boolean shouldPassengersInheritMalus() {
        return false;
    }

    public float getPathfindingMalus(BlockPathTypes $$0) {
        Mob $$2;
        if (this.getVehicle() instanceof Mob && ((Mob)this.getVehicle()).shouldPassengersInheritMalus()) {
            Mob $$1 = (Mob)this.getVehicle();
        } else {
            $$2 = this;
        }
        Float $$3 = (Float)$$2.pathfindingMalus.get((Object)$$0);
        return $$3 == null ? $$0.getMalus() : $$3.floatValue();
    }

    public void setPathfindingMalus(BlockPathTypes $$0, float $$1) {
        this.pathfindingMalus.put((Object)$$0, (Object)Float.valueOf((float)$$1));
    }

    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this);
    }

    public LookControl getLookControl() {
        return this.lookControl;
    }

    public MoveControl getMoveControl() {
        if (this.isPassenger() && this.getVehicle() instanceof Mob) {
            Mob $$0 = (Mob)this.getVehicle();
            return $$0.getMoveControl();
        }
        return this.moveControl;
    }

    public JumpControl getJumpControl() {
        return this.jumpControl;
    }

    public PathNavigation getNavigation() {
        if (this.isPassenger() && this.getVehicle() instanceof Mob) {
            Mob $$0 = (Mob)this.getVehicle();
            return $$0.getNavigation();
        }
        return this.navigation;
    }

    public Sensing getSensing() {
        return this.sensing;
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable LivingEntity $$0) {
        this.target = $$0;
    }

    @Override
    public boolean canAttackType(EntityType<?> $$0) {
        return $$0 != EntityType.GHAST;
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem $$0) {
        return false;
    }

    public void ate() {
        this.gameEvent(GameEvent.EAT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_MOB_FLAGS_ID, (byte)0);
    }

    public int getAmbientSoundInterval() {
        return 80;
    }

    public void playAmbientSound() {
        SoundEvent $$0 = this.getAmbientSound();
        if ($$0 != null) {
            this.playSound($$0, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.level.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
            this.resetAmbientSoundTime();
            this.playAmbientSound();
        }
        this.level.getProfiler().pop();
    }

    @Override
    protected void playHurtSound(DamageSource $$0) {
        this.resetAmbientSoundTime();
        super.playHurtSound($$0);
    }

    private void resetAmbientSoundTime() {
        this.ambientSoundTime = -this.getAmbientSoundInterval();
    }

    @Override
    public int getExperienceReward() {
        if (this.xpReward > 0) {
            int $$0 = this.xpReward;
            for (int $$1 = 0; $$1 < this.armorItems.size(); ++$$1) {
                if (this.armorItems.get($$1).isEmpty() || !(this.armorDropChances[$$1] <= 1.0f)) continue;
                $$0 += 1 + this.random.nextInt(3);
            }
            for (int $$2 = 0; $$2 < this.handItems.size(); ++$$2) {
                if (this.handItems.get($$2).isEmpty() || !(this.handDropChances[$$2] <= 1.0f)) continue;
                $$0 += 1 + this.random.nextInt(3);
            }
            return $$0;
        }
        return this.xpReward;
    }

    public void spawnAnim() {
        if (this.level.isClientSide) {
            for (int $$0 = 0; $$0 < 20; ++$$0) {
                double $$1 = this.random.nextGaussian() * 0.02;
                double $$2 = this.random.nextGaussian() * 0.02;
                double $$3 = this.random.nextGaussian() * 0.02;
                double $$4 = 10.0;
                this.level.addParticle(ParticleTypes.POOF, this.getX(1.0) - $$1 * 10.0, this.getRandomY() - $$2 * 10.0, this.getRandomZ(1.0) - $$3 * 10.0, $$1, $$2, $$3);
            }
        } else {
            this.level.broadcastEntityEvent(this, (byte)20);
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 20) {
            this.spawnAnim();
        } else {
            super.handleEntityEvent($$0);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.tickLeash();
            if (this.tickCount % 5 == 0) {
                this.updateControlFlags();
            }
        }
    }

    protected void updateControlFlags() {
        boolean $$0 = !(this.getControllingPassenger() instanceof Mob);
        boolean $$1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, $$0);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, $$0 && $$1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, $$0);
    }

    @Override
    protected float tickHeadTurn(float $$0, float $$1) {
        this.bodyRotationControl.clientTick();
        return $$1;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        ItemStack $$2;
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        $$0.putBoolean("PersistenceRequired", this.persistenceRequired);
        ListTag $$1 = new ListTag();
        Iterator iterator = this.armorItems.iterator();
        while (iterator.hasNext()) {
            $$2 = (ItemStack)iterator.next();
            CompoundTag $$3 = new CompoundTag();
            if (!$$2.isEmpty()) {
                $$2.save($$3);
            }
            $$1.add($$3);
        }
        $$0.put("ArmorItems", $$1);
        ListTag $$4 = new ListTag();
        $$2 = this.handItems.iterator();
        while ($$2.hasNext()) {
            ItemStack $$5 = (ItemStack)$$2.next();
            CompoundTag $$6 = new CompoundTag();
            if (!$$5.isEmpty()) {
                $$5.save($$6);
            }
            $$4.add($$6);
        }
        $$0.put("HandItems", $$4);
        ListTag $$7 = new ListTag();
        for (Object $$8 : (ItemStack)this.armorDropChances) {
            $$7.add(FloatTag.valueOf((float)$$8));
        }
        $$0.put("ArmorDropChances", $$7);
        ListTag $$9 = new ListTag();
        for (float $$10 : this.handDropChances) {
            $$9.add(FloatTag.valueOf($$10));
        }
        $$0.put("HandDropChances", $$9);
        if (this.leashHolder != null) {
            CompoundTag $$11 = new CompoundTag();
            if (this.leashHolder instanceof LivingEntity) {
                UUID $$12 = this.leashHolder.getUUID();
                $$11.putUUID("UUID", $$12);
            } else if (this.leashHolder instanceof HangingEntity) {
                BlockPos $$13 = ((HangingEntity)this.leashHolder).getPos();
                $$11.putInt("X", $$13.getX());
                $$11.putInt("Y", $$13.getY());
                $$11.putInt("Z", $$13.getZ());
            }
            $$0.put(LEASH_TAG, $$11);
        } else if (this.leashInfoTag != null) {
            $$0.put(LEASH_TAG, this.leashInfoTag.copy());
        }
        $$0.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            $$0.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                $$0.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isNoAi()) {
            $$0.putBoolean("NoAI", this.isNoAi());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot($$0.getBoolean("CanPickUpLoot"));
        }
        this.persistenceRequired = $$0.getBoolean("PersistenceRequired");
        if ($$0.contains("ArmorItems", 9)) {
            ListTag $$1 = $$0.getList("ArmorItems", 10);
            for (int $$2 = 0; $$2 < this.armorItems.size(); ++$$2) {
                this.armorItems.set($$2, ItemStack.of($$1.getCompound($$2)));
            }
        }
        if ($$0.contains("HandItems", 9)) {
            ListTag $$3 = $$0.getList("HandItems", 10);
            for (int $$4 = 0; $$4 < this.handItems.size(); ++$$4) {
                this.handItems.set($$4, ItemStack.of($$3.getCompound($$4)));
            }
        }
        if ($$0.contains("ArmorDropChances", 9)) {
            ListTag $$5 = $$0.getList("ArmorDropChances", 5);
            for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
                this.armorDropChances[$$6] = $$5.getFloat($$6);
            }
        }
        if ($$0.contains("HandDropChances", 9)) {
            ListTag $$7 = $$0.getList("HandDropChances", 5);
            for (int $$8 = 0; $$8 < $$7.size(); ++$$8) {
                this.handDropChances[$$8] = $$7.getFloat($$8);
            }
        }
        if ($$0.contains(LEASH_TAG, 10)) {
            this.leashInfoTag = $$0.getCompound(LEASH_TAG);
        }
        this.setLeftHanded($$0.getBoolean("LeftHanded"));
        if ($$0.contains("DeathLootTable", 8)) {
            this.lootTable = new ResourceLocation($$0.getString("DeathLootTable"));
            this.lootTableSeed = $$0.getLong("DeathLootTableSeed");
        }
        this.setNoAi($$0.getBoolean("NoAI"));
    }

    @Override
    protected void dropFromLootTable(DamageSource $$0, boolean $$1) {
        super.dropFromLootTable($$0, $$1);
        this.lootTable = null;
    }

    @Override
    protected LootContext.Builder createLootContext(boolean $$0, DamageSource $$1) {
        return super.createLootContext($$0, $$1).withOptionalRandomSeed(this.lootTableSeed, this.random);
    }

    @Override
    public final ResourceLocation getLootTable() {
        return this.lootTable == null ? this.getDefaultLootTable() : this.lootTable;
    }

    protected ResourceLocation getDefaultLootTable() {
        return super.getLootTable();
    }

    public void setZza(float $$0) {
        this.zza = $$0;
    }

    public void setYya(float $$0) {
        this.yya = $$0;
    }

    public void setXxa(float $$0) {
        this.xxa = $$0;
    }

    @Override
    public void setSpeed(float $$0) {
        super.setSpeed($$0);
        this.setZza($$0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.level.getProfiler().push("looting");
        if (!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            Vec3i $$0 = this.getPickupReach();
            List $$1 = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate($$0.getX(), $$0.getY(), $$0.getZ()));
            for (ItemEntity $$2 : $$1) {
                if ($$2.isRemoved() || $$2.getItem().isEmpty() || $$2.hasPickUpDelay() || !this.wantsToPickUp($$2.getItem())) continue;
                this.pickUpItem($$2);
            }
        }
        this.level.getProfiler().pop();
    }

    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    protected void pickUpItem(ItemEntity $$0) {
        ItemStack $$1 = $$0.getItem();
        ItemStack $$2 = this.equipItemIfPossible($$1.copy());
        if (!$$2.isEmpty()) {
            this.onItemPickup($$0);
            this.take($$0, $$2.getCount());
            $$1.shrink($$2.getCount());
            if ($$1.isEmpty()) {
                $$0.discard();
            }
        }
    }

    public ItemStack equipItemIfPossible(ItemStack $$0) {
        EquipmentSlot $$1 = this.getEquipmentSlotForItemStack($$0);
        ItemStack $$2 = this.getItemBySlot($$1);
        boolean $$3 = this.canReplaceCurrentItem($$0, $$2);
        if ($$3 && this.canHoldItem($$0)) {
            double $$4 = this.getEquipmentDropChance($$1);
            if (!$$2.isEmpty() && (double)Math.max((float)(this.random.nextFloat() - 0.1f), (float)0.0f) < $$4) {
                this.spawnAtLocation($$2);
            }
            if ($$1.isArmor() && $$0.getCount() > 1) {
                ItemStack $$5 = $$0.copyWithCount(1);
                this.setItemSlotAndDropWhenKilled($$1, $$5);
                return $$5;
            }
            this.setItemSlotAndDropWhenKilled($$1, $$0);
            return $$0;
        }
        return ItemStack.EMPTY;
    }

    private EquipmentSlot getEquipmentSlotForItemStack(ItemStack $$0) {
        EquipmentSlot $$1 = Mob.getEquipmentSlotForItem($$0);
        boolean $$2 = this.getItemBySlot($$1).isEmpty();
        return $$1.isArmor() && !$$2 ? EquipmentSlot.MAINHAND : $$1;
    }

    protected void setItemSlotAndDropWhenKilled(EquipmentSlot $$0, ItemStack $$1) {
        this.setItemSlot($$0, $$1);
        this.setGuaranteedDrop($$0);
        this.persistenceRequired = true;
    }

    public void setGuaranteedDrop(EquipmentSlot $$0) {
        switch ($$0.getType()) {
            case HAND: {
                this.handDropChances[$$0.getIndex()] = 2.0f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[$$0.getIndex()] = 2.0f;
            }
        }
    }

    protected boolean canReplaceCurrentItem(ItemStack $$0, ItemStack $$1) {
        if ($$1.isEmpty()) {
            return true;
        }
        if ($$0.getItem() instanceof SwordItem) {
            if (!($$1.getItem() instanceof SwordItem)) {
                return true;
            }
            SwordItem $$2 = (SwordItem)$$0.getItem();
            SwordItem $$3 = (SwordItem)$$1.getItem();
            if ($$2.getDamage() != $$3.getDamage()) {
                return $$2.getDamage() > $$3.getDamage();
            }
            return this.canReplaceEqualItem($$0, $$1);
        }
        if ($$0.getItem() instanceof BowItem && $$1.getItem() instanceof BowItem) {
            return this.canReplaceEqualItem($$0, $$1);
        }
        if ($$0.getItem() instanceof CrossbowItem && $$1.getItem() instanceof CrossbowItem) {
            return this.canReplaceEqualItem($$0, $$1);
        }
        if ($$0.getItem() instanceof ArmorItem) {
            if (EnchantmentHelper.hasBindingCurse($$1)) {
                return false;
            }
            if (!($$1.getItem() instanceof ArmorItem)) {
                return true;
            }
            ArmorItem $$4 = (ArmorItem)$$0.getItem();
            ArmorItem $$5 = (ArmorItem)$$1.getItem();
            if ($$4.getDefense() != $$5.getDefense()) {
                return $$4.getDefense() > $$5.getDefense();
            }
            if ($$4.getToughness() != $$5.getToughness()) {
                return $$4.getToughness() > $$5.getToughness();
            }
            return this.canReplaceEqualItem($$0, $$1);
        }
        if ($$0.getItem() instanceof DiggerItem) {
            if ($$1.getItem() instanceof BlockItem) {
                return true;
            }
            if ($$1.getItem() instanceof DiggerItem) {
                DiggerItem $$6 = (DiggerItem)$$0.getItem();
                DiggerItem $$7 = (DiggerItem)$$1.getItem();
                if ($$6.getAttackDamage() != $$7.getAttackDamage()) {
                    return $$6.getAttackDamage() > $$7.getAttackDamage();
                }
                return this.canReplaceEqualItem($$0, $$1);
            }
        }
        return false;
    }

    public boolean canReplaceEqualItem(ItemStack $$02, ItemStack $$1) {
        if ($$02.getDamageValue() < $$1.getDamageValue() || $$02.hasTag() && !$$1.hasTag()) {
            return true;
        }
        if ($$02.hasTag() && $$1.hasTag()) {
            return $$02.getTag().getAllKeys().stream().anyMatch($$0 -> !$$0.equals((Object)"Damage")) && !$$1.getTag().getAllKeys().stream().anyMatch($$0 -> !$$0.equals((Object)"Damage"));
        }
        return false;
    }

    public boolean canHoldItem(ItemStack $$0) {
        return true;
    }

    public boolean wantsToPickUp(ItemStack $$0) {
        return this.canHoldItem($$0);
    }

    public boolean removeWhenFarAway(double $$0) {
        return true;
    }

    public boolean requiresCustomPersistence() {
        return this.isPassenger();
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
            return;
        }
        if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
            this.noActionTime = 0;
            return;
        }
        Player $$0 = this.level.getNearestPlayer(this, -1.0);
        if ($$0 != null) {
            int $$2;
            int $$3;
            double $$1 = $$0.distanceToSqr(this);
            if ($$1 > (double)($$3 = ($$2 = this.getType().getCategory().getDespawnDistance()) * $$2) && this.removeWhenFarAway($$1)) {
                this.discard();
            }
            int $$4 = this.getType().getCategory().getNoDespawnDistance();
            int $$5 = $$4 * $$4;
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && $$1 > (double)$$5 && this.removeWhenFarAway($$1)) {
                this.discard();
            } else if ($$1 < (double)$$5) {
                this.noActionTime = 0;
            }
        }
    }

    @Override
    protected final void serverAiStep() {
        ++this.noActionTime;
        this.level.getProfiler().push("sensing");
        this.sensing.tick();
        this.level.getProfiler().pop();
        int $$0 = this.level.getServer().getTickCount() + this.getId();
        if ($$0 % 2 == 0 || this.tickCount <= 1) {
            this.level.getProfiler().push("targetSelector");
            this.targetSelector.tick();
            this.level.getProfiler().pop();
            this.level.getProfiler().push("goalSelector");
            this.goalSelector.tick();
            this.level.getProfiler().pop();
        } else {
            this.level.getProfiler().push("targetSelector");
            this.targetSelector.tickRunningGoals(false);
            this.level.getProfiler().pop();
            this.level.getProfiler().push("goalSelector");
            this.goalSelector.tickRunningGoals(false);
            this.level.getProfiler().pop();
        }
        this.level.getProfiler().push("navigation");
        this.navigation.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("mob tick");
        this.customServerAiStep();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("controls");
        this.level.getProfiler().push("move");
        this.moveControl.tick();
        this.level.getProfiler().popPush("look");
        this.lookControl.tick();
        this.level.getProfiler().popPush("jump");
        this.jumpControl.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().pop();
        this.sendDebugPackets();
    }

    protected void sendDebugPackets() {
        DebugPackets.sendGoalSelector(this.level, this, this.goalSelector);
    }

    protected void customServerAiStep() {
    }

    public int getMaxHeadXRot() {
        return 40;
    }

    public int getMaxHeadYRot() {
        return 75;
    }

    public int getHeadRotSpeed() {
        return 10;
    }

    public void lookAt(Entity $$0, float $$1, float $$2) {
        double $$7;
        double $$3 = $$0.getX() - this.getX();
        double $$4 = $$0.getZ() - this.getZ();
        if ($$0 instanceof LivingEntity) {
            LivingEntity $$5 = (LivingEntity)$$0;
            double $$6 = $$5.getEyeY() - this.getEyeY();
        } else {
            $$7 = ($$0.getBoundingBox().minY + $$0.getBoundingBox().maxY) / 2.0 - this.getEyeY();
        }
        double $$8 = Math.sqrt((double)($$3 * $$3 + $$4 * $$4));
        float $$9 = (float)(Mth.atan2($$4, $$3) * 57.2957763671875) - 90.0f;
        float $$10 = (float)(-(Mth.atan2($$7, $$8) * 57.2957763671875));
        this.setXRot(this.rotlerp(this.getXRot(), $$10, $$2));
        this.setYRot(this.rotlerp(this.getYRot(), $$9, $$1));
    }

    private float rotlerp(float $$0, float $$1, float $$2) {
        float $$3 = Mth.wrapDegrees($$1 - $$0);
        if ($$3 > $$2) {
            $$3 = $$2;
        }
        if ($$3 < -$$2) {
            $$3 = -$$2;
        }
        return $$0 + $$3;
    }

    public static boolean checkMobSpawnRules(EntityType<? extends Mob> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        Vec3i $$5 = $$3.below();
        return $$2 == MobSpawnType.SPAWNER || $$1.getBlockState((BlockPos)$$5).isValidSpawn($$1, (BlockPos)$$5, $$0);
    }

    public boolean checkSpawnRules(LevelAccessor $$0, MobSpawnType $$1) {
        return true;
    }

    public boolean checkSpawnObstruction(LevelReader $$0) {
        return !$$0.containsAnyLiquid(this.getBoundingBox()) && $$0.isUnobstructed(this);
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public boolean isMaxGroupSizeReached(int $$0) {
        return false;
    }

    @Override
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        int $$0 = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
        if (($$0 -= (3 - this.level.getDifficulty().getId()) * 4) < 0) {
            $$0 = 0;
        }
        return $$0 + 3;
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot $$0) {
        switch ($$0.getType()) {
            case HAND: {
                return this.handItems.get($$0.getIndex());
            }
            case ARMOR: {
                return this.armorItems.get($$0.getIndex());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot $$0, ItemStack $$1) {
        this.verifyEquippedItem($$1);
        switch ($$0.getType()) {
            case HAND: {
                this.onEquipItem($$0, this.handItems.set($$0.getIndex(), $$1), $$1);
                break;
            }
            case ARMOR: {
                this.onEquipItem($$0, this.armorItems.set($$0.getIndex(), $$1), $$1);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource $$0, int $$1, boolean $$2) {
        super.dropCustomDeathLoot($$0, $$1, $$2);
        for (EquipmentSlot $$3 : EquipmentSlot.values()) {
            boolean $$6;
            ItemStack $$4 = this.getItemBySlot($$3);
            float $$5 = this.getEquipmentDropChance($$3);
            boolean bl = $$6 = $$5 > 1.0f;
            if ($$4.isEmpty() || EnchantmentHelper.hasVanishingCurse($$4) || !$$2 && !$$6 || !(Math.max((float)(this.random.nextFloat() - (float)$$1 * 0.01f), (float)0.0f) < $$5)) continue;
            if (!$$6 && $$4.isDamageableItem()) {
                $$4.setDamageValue($$4.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max((int)($$4.getMaxDamage() - 3), (int)1))));
            }
            this.spawnAtLocation($$4);
            this.setItemSlot($$3, ItemStack.EMPTY);
        }
    }

    protected float getEquipmentDropChance(EquipmentSlot $$0) {
        float $$3;
        switch ($$0.getType()) {
            case HAND: {
                float $$1 = this.handDropChances[$$0.getIndex()];
                break;
            }
            case ARMOR: {
                float $$2 = this.armorDropChances[$$0.getIndex()];
                break;
            }
            default: {
                $$3 = 0.0f;
            }
        }
        return $$3;
    }

    protected void populateDefaultEquipmentSlots(RandomSource $$0, DifficultyInstance $$1) {
        if ($$0.nextFloat() < 0.15f * $$1.getSpecialMultiplier()) {
            float $$3;
            int $$2 = $$0.nextInt(2);
            float f = $$3 = this.level.getDifficulty() == Difficulty.HARD ? 0.1f : 0.25f;
            if ($$0.nextFloat() < 0.095f) {
                ++$$2;
            }
            if ($$0.nextFloat() < 0.095f) {
                ++$$2;
            }
            if ($$0.nextFloat() < 0.095f) {
                ++$$2;
            }
            boolean $$4 = true;
            for (EquipmentSlot $$5 : EquipmentSlot.values()) {
                Item $$7;
                if ($$5.getType() != EquipmentSlot.Type.ARMOR) continue;
                ItemStack $$6 = this.getItemBySlot($$5);
                if (!$$4 && $$0.nextFloat() < $$3) break;
                $$4 = false;
                if (!$$6.isEmpty() || ($$7 = Mob.getEquipmentForSlot($$5, $$2)) == null) continue;
                this.setItemSlot($$5, new ItemStack($$7));
            }
        }
    }

    @Nullable
    public static Item getEquipmentForSlot(EquipmentSlot $$0, int $$1) {
        switch ($$0) {
            case HEAD: {
                if ($$1 == 0) {
                    return Items.LEATHER_HELMET;
                }
                if ($$1 == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if ($$1 == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if ($$1 == 3) {
                    return Items.IRON_HELMET;
                }
                if ($$1 == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if ($$1 == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if ($$1 == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if ($$1 == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if ($$1 == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if ($$1 == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if ($$1 == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if ($$1 == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if ($$1 == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if ($$1 == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if ($$1 == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if ($$1 == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if ($$1 == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if ($$1 == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if ($$1 == 3) {
                    return Items.IRON_BOOTS;
                }
                if ($$1 != 4) break;
                return Items.DIAMOND_BOOTS;
            }
        }
        return null;
    }

    protected void populateDefaultEquipmentEnchantments(RandomSource $$0, DifficultyInstance $$1) {
        float $$2 = $$1.getSpecialMultiplier();
        this.enchantSpawnedWeapon($$0, $$2);
        for (EquipmentSlot $$3 : EquipmentSlot.values()) {
            if ($$3.getType() != EquipmentSlot.Type.ARMOR) continue;
            this.enchantSpawnedArmor($$0, $$2, $$3);
        }
    }

    protected void enchantSpawnedWeapon(RandomSource $$0, float $$1) {
        if (!this.getMainHandItem().isEmpty() && $$0.nextFloat() < 0.25f * $$1) {
            this.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem($$0, this.getMainHandItem(), (int)(5.0f + $$1 * (float)$$0.nextInt(18)), false));
        }
    }

    protected void enchantSpawnedArmor(RandomSource $$0, float $$1, EquipmentSlot $$2) {
        ItemStack $$3 = this.getItemBySlot($$2);
        if (!$$3.isEmpty() && $$0.nextFloat() < 0.5f * $$1) {
            this.setItemSlot($$2, EnchantmentHelper.enchantItem($$0, $$3, (int)(5.0f + $$1 * (float)$$0.nextInt(18)), false));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        RandomSource $$5 = $$0.getRandom();
        this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random spawn bonus", $$5.triangle(0.0, 0.11485000000000001), AttributeModifier.Operation.MULTIPLY_BASE));
        if ($$5.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return $$3;
    }

    public void setPersistenceRequired() {
        this.persistenceRequired = true;
    }

    public void setDropChance(EquipmentSlot $$0, float $$1) {
        switch ($$0.getType()) {
            case HAND: {
                this.handDropChances[$$0.getIndex()] = $$1;
                break;
            }
            case ARMOR: {
                this.armorDropChances[$$0.getIndex()] = $$1;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean $$0) {
        this.canPickUpLoot = $$0;
    }

    @Override
    public boolean canTakeItem(ItemStack $$0) {
        EquipmentSlot $$1 = Mob.getEquipmentSlotForItem($$0);
        return this.getItemBySlot($$1).isEmpty() && this.canPickUpLoot();
    }

    public boolean isPersistenceRequired() {
        return this.persistenceRequired;
    }

    @Override
    public final InteractionResult interact(Player $$0, InteractionHand $$1) {
        if (!this.isAlive()) {
            return InteractionResult.PASS;
        }
        if (this.getLeashHolder() == $$0) {
            this.dropLeash(true, !$$0.getAbilities().instabuild);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        InteractionResult $$2 = this.checkAndHandleImportantInteractions($$0, $$1);
        if ($$2.consumesAction()) {
            return $$2;
        }
        $$2 = this.mobInteract($$0, $$1);
        if ($$2.consumesAction()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            return $$2;
        }
        return super.interact($$0, $$1);
    }

    private InteractionResult checkAndHandleImportantInteractions(Player $$0, InteractionHand $$12) {
        InteractionResult $$3;
        ItemStack $$2 = $$0.getItemInHand($$12);
        if ($$2.is(Items.LEAD) && this.canBeLeashed($$0)) {
            this.setLeashedTo($$0, true);
            $$2.shrink(1);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if ($$2.is(Items.NAME_TAG) && ($$3 = $$2.interactLivingEntity($$0, this, $$12)).consumesAction()) {
            return $$3;
        }
        if ($$2.getItem() instanceof SpawnEggItem) {
            if (this.level instanceof ServerLevel) {
                SpawnEggItem $$4 = (SpawnEggItem)$$2.getItem();
                Optional<Mob> $$5 = $$4.spawnOffspringFromSpawnEgg($$0, this, this.getType(), (ServerLevel)this.level, this.position(), $$2);
                $$5.ifPresent($$1 -> this.onOffspringSpawnedFromEgg($$0, (Mob)$$1));
                return $$5.isPresent() ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    protected void onOffspringSpawnedFromEgg(Player $$0, Mob $$1) {
    }

    protected InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        return InteractionResult.PASS;
    }

    public boolean isWithinRestriction() {
        return this.isWithinRestriction(this.blockPosition());
    }

    public boolean isWithinRestriction(BlockPos $$0) {
        if (this.restrictRadius == -1.0f) {
            return true;
        }
        return this.restrictCenter.distSqr($$0) < (double)(this.restrictRadius * this.restrictRadius);
    }

    public void restrictTo(BlockPos $$0, int $$1) {
        this.restrictCenter = $$0;
        this.restrictRadius = $$1;
    }

    public BlockPos getRestrictCenter() {
        return this.restrictCenter;
    }

    public float getRestrictRadius() {
        return this.restrictRadius;
    }

    public void clearRestriction() {
        this.restrictRadius = -1.0f;
    }

    public boolean hasRestriction() {
        return this.restrictRadius != -1.0f;
    }

    @Nullable
    public <T extends Mob> T convertTo(EntityType<T> $$0, boolean $$1) {
        if (this.isRemoved()) {
            return null;
        }
        Mob $$2 = (Mob)$$0.create(this.level);
        if ($$2 == null) {
            return null;
        }
        $$2.copyPosition(this);
        $$2.setBaby(this.isBaby());
        $$2.setNoAi(this.isNoAi());
        if (this.hasCustomName()) {
            $$2.setCustomName(this.getCustomName());
            $$2.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isPersistenceRequired()) {
            $$2.setPersistenceRequired();
        }
        $$2.setInvulnerable(this.isInvulnerable());
        if ($$1) {
            $$2.setCanPickUpLoot(this.canPickUpLoot());
            for (EquipmentSlot $$3 : EquipmentSlot.values()) {
                ItemStack $$4 = this.getItemBySlot($$3);
                if ($$4.isEmpty()) continue;
                $$2.setItemSlot($$3, $$4.copy());
                $$2.setDropChance($$3, this.getEquipmentDropChance($$3));
                $$4.setCount(0);
            }
        }
        this.level.addFreshEntity($$2);
        if (this.isPassenger()) {
            Entity $$5 = this.getVehicle();
            this.stopRiding();
            $$2.startRiding($$5, true);
        }
        this.discard();
        return (T)$$2;
    }

    protected void tickLeash() {
        if (this.leashInfoTag != null) {
            this.restoreLeashFromSave();
        }
        if (this.leashHolder == null) {
            return;
        }
        if (!this.isAlive() || !this.leashHolder.isAlive()) {
            this.dropLeash(true, true);
        }
    }

    public void dropLeash(boolean $$0, boolean $$1) {
        if (this.leashHolder != null) {
            this.leashHolder = null;
            this.leashInfoTag = null;
            if (!this.level.isClientSide && $$1) {
                this.spawnAtLocation(Items.LEAD);
            }
            if (!this.level.isClientSide && $$0 && this.level instanceof ServerLevel) {
                ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
            }
        }
    }

    public boolean canBeLeashed(Player $$0) {
        return !this.isLeashed() && !(this instanceof Enemy);
    }

    public boolean isLeashed() {
        return this.leashHolder != null;
    }

    @Nullable
    public Entity getLeashHolder() {
        if (this.leashHolder == null && this.delayedLeashHolderId != 0 && this.level.isClientSide) {
            this.leashHolder = this.level.getEntity(this.delayedLeashHolderId);
        }
        return this.leashHolder;
    }

    public void setLeashedTo(Entity $$0, boolean $$1) {
        this.leashHolder = $$0;
        this.leashInfoTag = null;
        if (!this.level.isClientSide && $$1 && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, this.leashHolder));
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
    }

    public void setDelayedLeashHolderId(int $$0) {
        this.delayedLeashHolderId = $$0;
        this.dropLeash(false, false);
    }

    @Override
    public boolean startRiding(Entity $$0, boolean $$1) {
        boolean $$2 = super.startRiding($$0, $$1);
        if ($$2 && this.isLeashed()) {
            this.dropLeash(true, true);
        }
        return $$2;
    }

    private void restoreLeashFromSave() {
        if (this.leashInfoTag != null && this.level instanceof ServerLevel) {
            if (this.leashInfoTag.hasUUID("UUID")) {
                UUID $$0 = this.leashInfoTag.getUUID("UUID");
                Entity $$1 = ((ServerLevel)this.level).getEntity($$0);
                if ($$1 != null) {
                    this.setLeashedTo($$1, true);
                    return;
                }
            } else if (this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
                BlockPos $$2 = NbtUtils.readBlockPos(this.leashInfoTag);
                this.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(this.level, $$2), true);
                return;
            }
            if (this.tickCount > 100) {
                this.spawnAtLocation(Items.LEAD);
                this.leashInfoTag = null;
            }
        }
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.hasControllingPassenger() && super.isControlledByLocalInstance();
    }

    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && !this.isNoAi();
    }

    public void setNoAi(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_MOB_FLAGS_ID);
        this.entityData.set(DATA_MOB_FLAGS_ID, $$0 ? (byte)($$1 | 1) : (byte)($$1 & 0xFFFFFFFE));
    }

    public void setLeftHanded(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_MOB_FLAGS_ID);
        this.entityData.set(DATA_MOB_FLAGS_ID, $$0 ? (byte)($$1 | 2) : (byte)($$1 & 0xFFFFFFFD));
    }

    public void setAggressive(boolean $$0) {
        byte $$1 = this.entityData.get(DATA_MOB_FLAGS_ID);
        this.entityData.set(DATA_MOB_FLAGS_ID, $$0 ? (byte)($$1 | 4) : (byte)($$1 & 0xFFFFFFFB));
    }

    public boolean isNoAi() {
        return (this.entityData.get(DATA_MOB_FLAGS_ID) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return (this.entityData.get(DATA_MOB_FLAGS_ID) & 2) != 0;
    }

    public boolean isAggressive() {
        return (this.entityData.get(DATA_MOB_FLAGS_ID) & 4) != 0;
    }

    public void setBaby(boolean $$0) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return this.isLeftHanded() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    public double getMeleeAttackRangeSqr(LivingEntity $$0) {
        return this.getBbWidth() * 2.0f * (this.getBbWidth() * 2.0f) + $$0.getBbWidth();
    }

    public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity $$0) {
        return Math.max((double)this.distanceToSqr($$0.getMeleeAttackReferencePosition()), (double)this.distanceToSqr($$0.position()));
    }

    public boolean isWithinMeleeAttackRange(LivingEntity $$0) {
        double $$1 = this.getPerceivedTargetDistanceSquareForMeleeAttack($$0);
        return $$1 <= this.getMeleeAttackRangeSqr($$0);
    }

    @Override
    public boolean doHurtTarget(Entity $$0) {
        boolean $$4;
        int $$3;
        float $$1 = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float $$2 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if ($$0 instanceof LivingEntity) {
            $$1 += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)$$0).getMobType());
            $$2 += (float)EnchantmentHelper.getKnockbackBonus(this);
        }
        if (($$3 = EnchantmentHelper.getFireAspect(this)) > 0) {
            $$0.setSecondsOnFire($$3 * 4);
        }
        if ($$4 = $$0.hurt(DamageSource.mobAttack(this), $$1)) {
            if ($$2 > 0.0f && $$0 instanceof LivingEntity) {
                ((LivingEntity)$$0).knockback($$2 * 0.5f, Mth.sin(this.getYRot() * ((float)Math.PI / 180)), -Mth.cos(this.getYRot() * ((float)Math.PI / 180)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }
            if ($$0 instanceof Player) {
                Player $$5 = (Player)$$0;
                this.maybeDisableShield($$5, this.getMainHandItem(), $$5.isUsingItem() ? $$5.getUseItem() : ItemStack.EMPTY);
            }
            this.doEnchantDamageEffects(this, $$0);
            this.setLastHurtMob($$0);
        }
        return $$4;
    }

    private void maybeDisableShield(Player $$0, ItemStack $$1, ItemStack $$2) {
        if (!$$1.isEmpty() && !$$2.isEmpty() && $$1.getItem() instanceof AxeItem && $$2.is(Items.SHIELD)) {
            float $$3 = 0.25f + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05f;
            if (this.random.nextFloat() < $$3) {
                $$0.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent($$0, (byte)30);
            }
        }
    }

    protected boolean isSunBurnTick() {
        if (this.level.isDay() && !this.level.isClientSide) {
            boolean $$2;
            float $$0 = this.getLightLevelDependentMagicValue();
            BlockPos $$1 = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
            boolean bl = $$2 = this.isInWaterRainOrBubble() || this.isInPowderSnow || this.wasInPowderSnow;
            if ($$0 > 0.5f && this.random.nextFloat() * 30.0f < ($$0 - 0.4f) * 2.0f && !$$2 && this.level.canSeeSky($$1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void jumpInLiquid(TagKey<Fluid> $$0) {
        if (this.getNavigation().canFloat()) {
            super.jumpInLiquid($$0);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.3, 0.0));
        }
    }

    public void removeFreeWill() {
        this.removeAllGoals((Predicate<Goal>)((Predicate)$$0 -> true));
        this.getBrain().removeAllBehaviors();
    }

    public void removeAllGoals(Predicate<Goal> $$0) {
        this.goalSelector.removeAllGoals($$0);
    }

    @Override
    protected void removeAfterChangingDimensions() {
        super.removeAfterChangingDimensions();
        this.dropLeash(true, false);
        this.getAllSlots().forEach($$0 -> $$0.setCount(0));
    }

    @Override
    @Nullable
    public ItemStack getPickResult() {
        SpawnEggItem $$0 = SpawnEggItem.byId(this.getType());
        if ($$0 == null) {
            return null;
        }
        return new ItemStack($$0);
    }
}