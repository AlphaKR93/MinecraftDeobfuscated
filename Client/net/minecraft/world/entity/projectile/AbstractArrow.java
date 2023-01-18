/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  java.lang.Byte
 *  java.lang.Double
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractArrow
extends Projectile {
    private static final double ARROW_BASE_DAMAGE = 2.0;
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    private static final int FLAG_CRIT = 1;
    private static final int FLAG_NOPHYSICS = 2;
    private static final int FLAG_CROSSBOW = 4;
    @Nullable
    private BlockState lastState;
    protected boolean inGround;
    protected int inGroundTime;
    public Pickup pickup = Pickup.DISALLOWED;
    public int shakeTime;
    private int life;
    private double baseDamage = 2.0;
    private int knockback;
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private List<Entity> piercedAndKilledEntities;

    protected AbstractArrow(EntityType<? extends AbstractArrow> $$0, Level $$1) {
        super((EntityType<? extends Projectile>)$$0, $$1);
    }

    protected AbstractArrow(EntityType<? extends AbstractArrow> $$0, double $$1, double $$2, double $$3, Level $$4) {
        this($$0, $$4);
        this.setPos($$1, $$2, $$3);
    }

    protected AbstractArrow(EntityType<? extends AbstractArrow> $$0, LivingEntity $$1, Level $$2) {
        this($$0, $$1.getX(), $$1.getEyeY() - (double)0.1f, $$1.getZ(), $$2);
        this.setOwner($$1);
        if ($$1 instanceof Player) {
            this.pickup = Pickup.ALLOWED;
        }
    }

    public void setSoundEvent(SoundEvent $$0) {
        this.soundEvent = $$0;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN((double)$$1)) {
            $$1 = 1.0;
        }
        return $$0 < ($$1 *= 64.0 * AbstractArrow.getViewScale()) * $$1;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_FLAGS, (byte)0);
        this.entityData.define(PIERCE_LEVEL, (byte)0);
    }

    @Override
    public void shoot(double $$0, double $$1, double $$2, float $$3, float $$4) {
        super.shoot($$0, $$1, $$2, $$3, $$4);
        this.life = 0;
    }

    @Override
    public void lerpTo(double $$0, double $$1, double $$2, float $$3, float $$4, int $$5, boolean $$6) {
        this.setPos($$0, $$1, $$2);
        this.setRot($$3, $$4);
    }

    @Override
    public void lerpMotion(double $$0, double $$1, double $$2) {
        super.lerpMotion($$0, $$1, $$2);
        this.life = 0;
    }

    @Override
    public void tick() {
        Vec3 $$9;
        VoxelShape $$5;
        BlockPos $$3;
        BlockState $$4;
        super.tick();
        boolean $$0 = this.isNoPhysics();
        Vec3 $$1 = this.getDeltaMovement();
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            double $$2 = $$1.horizontalDistance();
            this.setYRot((float)(Mth.atan2($$1.x, $$1.z) * 57.2957763671875));
            this.setXRot((float)(Mth.atan2($$1.y, $$2) * 57.2957763671875));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        if (!(($$4 = this.level.getBlockState($$3 = this.blockPosition())).isAir() || $$0 || ($$5 = $$4.getCollisionShape(this.level, $$3)).isEmpty())) {
            Vec3 $$6 = this.position();
            for (AABB $$7 : $$5.toAabbs()) {
                if (!$$7.move($$3).contains($$6)) continue;
                this.inGround = true;
                break;
            }
        }
        if (this.shakeTime > 0) {
            --this.shakeTime;
        }
        if (this.isInWaterOrRain() || $$4.is(Blocks.POWDER_SNOW)) {
            this.clearFire();
        }
        if (this.inGround && !$$0) {
            if (this.lastState != $$4 && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level.isClientSide) {
                this.tickDespawn();
            }
            ++this.inGroundTime;
            return;
        }
        this.inGroundTime = 0;
        Vec3 $$8 = this.position();
        HitResult $$10 = this.level.clip(new ClipContext($$8, $$9 = $$8.add($$1), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if ($$10.getType() != HitResult.Type.MISS) {
            $$9 = $$10.getLocation();
        }
        while (!this.isRemoved()) {
            EntityHitResult $$11 = this.findHitEntity($$8, $$9);
            if ($$11 != null) {
                $$10 = $$11;
            }
            if ($$10 != null && $$10.getType() == HitResult.Type.ENTITY) {
                Entity $$12 = ((EntityHitResult)$$10).getEntity();
                Entity $$13 = this.getOwner();
                if ($$12 instanceof Player && $$13 instanceof Player && !((Player)$$13).canHarmPlayer((Player)$$12)) {
                    $$10 = null;
                    $$11 = null;
                }
            }
            if ($$10 != null && !$$0) {
                this.onHit($$10);
                this.hasImpulse = true;
            }
            if ($$11 == null || this.getPierceLevel() <= 0) break;
            $$10 = null;
        }
        $$1 = this.getDeltaMovement();
        double $$14 = $$1.x;
        double $$15 = $$1.y;
        double $$16 = $$1.z;
        if (this.isCritArrow()) {
            for (int $$17 = 0; $$17 < 4; ++$$17) {
                this.level.addParticle(ParticleTypes.CRIT, this.getX() + $$14 * (double)$$17 / 4.0, this.getY() + $$15 * (double)$$17 / 4.0, this.getZ() + $$16 * (double)$$17 / 4.0, -$$14, -$$15 + 0.2, -$$16);
            }
        }
        double $$18 = this.getX() + $$14;
        double $$19 = this.getY() + $$15;
        double $$20 = this.getZ() + $$16;
        double $$21 = $$1.horizontalDistance();
        if ($$0) {
            this.setYRot((float)(Mth.atan2(-$$14, -$$16) * 57.2957763671875));
        } else {
            this.setYRot((float)(Mth.atan2($$14, $$16) * 57.2957763671875));
        }
        this.setXRot((float)(Mth.atan2($$15, $$21) * 57.2957763671875));
        this.setXRot(AbstractArrow.lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(AbstractArrow.lerpRotation(this.yRotO, this.getYRot()));
        float $$22 = 0.99f;
        float $$23 = 0.05f;
        if (this.isInWater()) {
            for (int $$24 = 0; $$24 < 4; ++$$24) {
                float $$25 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, $$18 - $$14 * 0.25, $$19 - $$15 * 0.25, $$20 - $$16 * 0.25, $$14, $$15, $$16);
            }
            $$22 = this.getWaterInertia();
        }
        this.setDeltaMovement($$1.scale($$22));
        if (!this.isNoGravity() && !$$0) {
            Vec3 $$26 = this.getDeltaMovement();
            this.setDeltaMovement($$26.x, $$26.y - (double)0.05f, $$26.z);
        }
        this.setPos($$18, $$19, $$20);
        this.checkInsideBlocks();
    }

    private boolean shouldFall() {
        return this.inGround && this.level.noCollision(new AABB(this.position(), this.position()).inflate(0.06));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 $$0 = this.getDeltaMovement();
        this.setDeltaMovement($$0.multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
        this.life = 0;
    }

    @Override
    public void move(MoverType $$0, Vec3 $$1) {
        super.move($$0, $$1);
        if ($$0 != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 1200) {
            this.discard();
        }
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }
        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        DamageSource $$7;
        Entity $$5;
        super.onHitEntity($$0);
        Entity $$1 = $$0.getEntity();
        float $$2 = (float)this.getDeltaMovement().length();
        int $$3 = Mth.ceil(Mth.clamp((double)$$2 * this.baseDamage, 0.0, 2.147483647E9));
        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }
            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity((int)5);
            }
            if (this.piercingIgnoreEntityIds.size() < this.getPierceLevel() + 1) {
                this.piercingIgnoreEntityIds.add($$1.getId());
            } else {
                this.discard();
                return;
            }
        }
        if (this.isCritArrow()) {
            long $$4 = this.random.nextInt($$3 / 2 + 2);
            $$3 = (int)Math.min((long)($$4 + (long)$$3), (long)Integer.MAX_VALUE);
        }
        if (($$5 = this.getOwner()) == null) {
            DamageSource $$6 = DamageSource.arrow(this, this);
        } else {
            $$7 = DamageSource.arrow(this, $$5);
            if ($$5 instanceof LivingEntity) {
                ((LivingEntity)$$5).setLastHurtMob($$1);
            }
        }
        boolean $$8 = $$1.getType() == EntityType.ENDERMAN;
        int $$9 = $$1.getRemainingFireTicks();
        if (this.isOnFire() && !$$8) {
            $$1.setSecondsOnFire(5);
        }
        if ($$1.hurt($$7, $$3)) {
            if ($$8) {
                return;
            }
            if ($$1 instanceof LivingEntity) {
                LivingEntity $$10 = (LivingEntity)$$1;
                if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
                    $$10.setArrowCount($$10.getArrowCount() + 1);
                }
                if (this.knockback > 0) {
                    double $$11 = Math.max((double)0.0, (double)(1.0 - $$10.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                    Vec3 $$12 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale((double)this.knockback * 0.6 * $$11);
                    if ($$12.lengthSqr() > 0.0) {
                        $$10.push($$12.x, 0.1, $$12.z);
                    }
                }
                if (!this.level.isClientSide && $$5 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects($$10, $$5);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)$$5, $$10);
                }
                this.doPostHurtEffects($$10);
                if ($$5 != null && $$10 != $$5 && $$10 instanceof Player && $$5 instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer)$$5).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0f));
                }
                if (!$$1.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add((Object)$$10);
                }
                if (!this.level.isClientSide && $$5 instanceof ServerPlayer) {
                    ServerPlayer $$13 = (ServerPlayer)$$5;
                    if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger($$13, (Collection<Entity>)this.piercedAndKilledEntities);
                    } else if (!$$1.isAlive() && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger($$13, (Collection<Entity>)Arrays.asList((Object[])new Entity[]{$$1}));
                    }
                }
            }
            this.playSound(this.soundEvent, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            $$1.setRemainingFireTicks($$9);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
            this.setYRot(this.getYRot() + 180.0f);
            this.yRotO += 180.0f;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
                if (this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1f);
                }
                this.discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult $$0) {
        this.lastState = this.level.getBlockState($$0.getBlockPos());
        super.onHitBlock($$0);
        Vec3 $$1 = $$0.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement($$1);
        Vec3 $$2 = $$1.normalize().scale(0.05f);
        this.setPosRaw(this.getX() - $$2.x, this.getY() - $$2.y, this.getZ() - $$2.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte)0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.resetPiercedEntities();
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    protected void doPostHurtEffects(LivingEntity $$0) {
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 $$0, Vec3 $$1) {
        return ProjectileUtil.getEntityHitResult(this.level, this, $$0, $$1, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), (Predicate<Entity>)((Predicate)this::canHitEntity));
    }

    @Override
    protected boolean canHitEntity(Entity $$0) {
        return super.canHitEntity($$0) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains($$0.getId()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putShort("life", (short)this.life);
        if (this.lastState != null) {
            $$0.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
        }
        $$0.putByte("shake", (byte)this.shakeTime);
        $$0.putBoolean("inGround", this.inGround);
        $$0.putByte("pickup", (byte)this.pickup.ordinal());
        $$0.putDouble("damage", this.baseDamage);
        $$0.putBoolean("crit", this.isCritArrow());
        $$0.putByte("PierceLevel", this.getPierceLevel());
        $$0.putString("SoundEvent", BuiltInRegistries.SOUND_EVENT.getKey(this.soundEvent).toString());
        $$0.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.life = $$0.getShort("life");
        if ($$0.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), $$0.getCompound("inBlockState"));
        }
        this.shakeTime = $$0.getByte("shake") & 0xFF;
        this.inGround = $$0.getBoolean("inGround");
        if ($$0.contains("damage", 99)) {
            this.baseDamage = $$0.getDouble("damage");
        }
        this.pickup = Pickup.byOrdinal($$0.getByte("pickup"));
        this.setCritArrow($$0.getBoolean("crit"));
        this.setPierceLevel($$0.getByte("PierceLevel"));
        if ($$0.contains("SoundEvent", 8)) {
            this.soundEvent = (SoundEvent)BuiltInRegistries.SOUND_EVENT.getOptional(new ResourceLocation($$0.getString("SoundEvent"))).orElse((Object)this.getDefaultHitGroundSoundEvent());
        }
        this.setShotFromCrossbow($$0.getBoolean("ShotFromCrossbow"));
    }

    @Override
    public void setOwner(@Nullable Entity $$0) {
        super.setOwner($$0);
        if ($$0 instanceof Player) {
            this.pickup = ((Player)$$0).getAbilities().instabuild ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        }
    }

    @Override
    public void playerTouch(Player $$0) {
        if (this.level.isClientSide || !this.inGround && !this.isNoPhysics() || this.shakeTime > 0) {
            return;
        }
        if (this.tryPickup($$0)) {
            $$0.take(this, 1);
            this.discard();
        }
    }

    protected boolean tryPickup(Player $$0) {
        switch (this.pickup) {
            case ALLOWED: {
                return $$0.getInventory().add(this.getPickupItem());
            }
            case CREATIVE_ONLY: {
                return $$0.getAbilities().instabuild;
            }
        }
        return false;
    }

    protected abstract ItemStack getPickupItem();

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public void setBaseDamage(double $$0) {
        this.baseDamage = $$0;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    public void setKnockback(int $$0) {
        this.knockback = $$0;
    }

    public int getKnockback() {
        return this.knockback;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected float getEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 0.13f;
    }

    public void setCritArrow(boolean $$0) {
        this.setFlag(1, $$0);
    }

    public void setPierceLevel(byte $$0) {
        this.entityData.set(PIERCE_LEVEL, $$0);
    }

    private void setFlag(int $$0, boolean $$1) {
        byte $$2 = this.entityData.get(ID_FLAGS);
        if ($$1) {
            this.entityData.set(ID_FLAGS, (byte)($$2 | $$0));
        } else {
            this.entityData.set(ID_FLAGS, (byte)($$2 & ~$$0));
        }
    }

    public boolean isCritArrow() {
        byte $$0 = this.entityData.get(ID_FLAGS);
        return ($$0 & 1) != 0;
    }

    public boolean shotFromCrossbow() {
        byte $$0 = this.entityData.get(ID_FLAGS);
        return ($$0 & 4) != 0;
    }

    public byte getPierceLevel() {
        return this.entityData.get(PIERCE_LEVEL);
    }

    public void setEnchantmentEffectsFromEntity(LivingEntity $$0, float $$1) {
        int $$2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, $$0);
        int $$3 = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, $$0);
        this.setBaseDamage((double)($$1 * 2.0f) + this.random.triangle((double)this.level.getDifficulty().getId() * 0.11, 0.57425));
        if ($$2 > 0) {
            this.setBaseDamage(this.getBaseDamage() + (double)$$2 * 0.5 + 0.5);
        }
        if ($$3 > 0) {
            this.setKnockback($$3);
        }
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, $$0) > 0) {
            this.setSecondsOnFire(100);
        }
    }

    protected float getWaterInertia() {
        return 0.6f;
    }

    public void setNoPhysics(boolean $$0) {
        this.noPhysics = $$0;
        this.setFlag(2, $$0);
    }

    public boolean isNoPhysics() {
        if (!this.level.isClientSide) {
            return this.noPhysics;
        }
        return (this.entityData.get(ID_FLAGS) & 2) != 0;
    }

    public void setShotFromCrossbow(boolean $$0) {
        this.setFlag(4, $$0);
    }

    public static enum Pickup {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;


        public static Pickup byOrdinal(int $$0) {
            if ($$0 < 0 || $$0 > Pickup.values().length) {
                $$0 = 0;
            }
            return Pickup.values()[$$0];
        }
    }
}