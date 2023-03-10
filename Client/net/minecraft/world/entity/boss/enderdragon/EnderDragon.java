/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Double
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.boss.enderdragon;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class EnderDragon
extends Mob
implements Enemy {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(EnderDragon.class, EntityDataSerializers.INT);
    private static final TargetingConditions CRYSTAL_DESTROY_TARGETING = TargetingConditions.forCombat().range(64.0);
    private static final int GROWL_INTERVAL_MIN = 200;
    private static final int GROWL_INTERVAL_MAX = 400;
    private static final float SITTING_ALLOWED_DAMAGE_PERCENTAGE = 0.25f;
    private static final String DRAGON_DEATH_TIME_KEY = "DragonDeathTime";
    private static final String DRAGON_PHASE_KEY = "DragonPhase";
    public final double[][] positions = new double[64][3];
    public int posPointer = -1;
    private final EnderDragonPart[] subEntities;
    public final EnderDragonPart head;
    private final EnderDragonPart neck;
    private final EnderDragonPart body;
    private final EnderDragonPart tail1;
    private final EnderDragonPart tail2;
    private final EnderDragonPart tail3;
    private final EnderDragonPart wing1;
    private final EnderDragonPart wing2;
    public float oFlapTime;
    public float flapTime;
    public boolean inWall;
    public int dragonDeathTime;
    public float yRotA;
    @Nullable
    public EndCrystal nearestCrystal;
    @Nullable
    private final EndDragonFight dragonFight;
    private final EnderDragonPhaseManager phaseManager;
    private int growlTime = 100;
    private float sittingDamageReceived;
    private final Node[] nodes = new Node[24];
    private final int[] nodeAdjacency = new int[24];
    private final BinaryHeap openSet = new BinaryHeap();

    public EnderDragon(EntityType<? extends EnderDragon> $$0, Level $$1) {
        super((EntityType<? extends Mob>)EntityType.ENDER_DRAGON, $$1);
        this.head = new EnderDragonPart(this, "head", 1.0f, 1.0f);
        this.neck = new EnderDragonPart(this, "neck", 3.0f, 3.0f);
        this.body = new EnderDragonPart(this, "body", 5.0f, 3.0f);
        this.tail1 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.tail2 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.tail3 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.wing1 = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.wing2 = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.subEntities = new EnderDragonPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
        this.setHealth(this.getMaxHealth());
        this.noPhysics = true;
        this.noCulling = true;
        this.dragonFight = $$1 instanceof ServerLevel ? ((ServerLevel)$$1).dragonFight() : null;
        this.phaseManager = new EnderDragonPhaseManager(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 200.0);
    }

    @Override
    public boolean isFlapping() {
        float $$0 = Mth.cos(this.flapTime * ((float)Math.PI * 2));
        float $$1 = Mth.cos(this.oFlapTime * ((float)Math.PI * 2));
        return $$1 <= -0.3f && $$0 >= -0.3f;
    }

    @Override
    public void onFlap() {
        if (this.level.isClientSide && !this.isSilent()) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0f, 0.8f + this.random.nextFloat() * 0.3f, false);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_PHASE, EnderDragonPhase.HOVERING.getId());
    }

    public double[] getLatencyPos(int $$0, float $$1) {
        if (this.isDeadOrDying()) {
            $$1 = 0.0f;
        }
        $$1 = 1.0f - $$1;
        int $$2 = this.posPointer - $$0 & 0x3F;
        int $$3 = this.posPointer - $$0 - 1 & 0x3F;
        double[] $$4 = new double[3];
        double $$5 = this.positions[$$2][0];
        double $$6 = Mth.wrapDegrees(this.positions[$$3][0] - $$5);
        $$4[0] = $$5 + $$6 * (double)$$1;
        $$5 = this.positions[$$2][1];
        $$6 = this.positions[$$3][1] - $$5;
        $$4[1] = $$5 + $$6 * (double)$$1;
        $$4[2] = Mth.lerp((double)$$1, this.positions[$$2][2], this.positions[$$3][2]);
        return $$4;
    }

    @Override
    public void aiStep() {
        this.processFlappingMovement();
        if (this.level.isClientSide) {
            this.setHealth(this.getHealth());
            if (!this.isSilent() && !this.phaseManager.getCurrentPhase().isSitting() && --this.growlTime < 0) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 2.5f, 0.8f + this.random.nextFloat() * 0.3f, false);
                this.growlTime = 200 + this.random.nextInt(200);
            }
        }
        this.oFlapTime = this.flapTime;
        if (this.isDeadOrDying()) {
            float $$0 = (this.random.nextFloat() - 0.5f) * 8.0f;
            float $$1 = (this.random.nextFloat() - 0.5f) * 4.0f;
            float $$2 = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)$$0, this.getY() + 2.0 + (double)$$1, this.getZ() + (double)$$2, 0.0, 0.0, 0.0);
            return;
        }
        this.checkCrystals();
        Vec3 $$3 = this.getDeltaMovement();
        float $$4 = 0.2f / ((float)$$3.horizontalDistance() * 10.0f + 1.0f);
        this.flapTime = this.phaseManager.getCurrentPhase().isSitting() ? (this.flapTime += 0.1f) : (this.inWall ? (this.flapTime += $$4 * 0.5f) : (this.flapTime += ($$4 *= (float)Math.pow((double)2.0, (double)$$3.y))));
        this.setYRot(Mth.wrapDegrees(this.getYRot()));
        if (this.isNoAi()) {
            this.flapTime = 0.5f;
            return;
        }
        if (this.posPointer < 0) {
            for (int $$5 = 0; $$5 < this.positions.length; ++$$5) {
                this.positions[$$5][0] = this.getYRot();
                this.positions[$$5][1] = this.getY();
            }
        }
        if (++this.posPointer == this.positions.length) {
            this.posPointer = 0;
        }
        this.positions[this.posPointer][0] = this.getYRot();
        this.positions[this.posPointer][1] = this.getY();
        if (this.level.isClientSide) {
            if (this.lerpSteps > 0) {
                double $$6 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
                double $$7 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
                double $$8 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
                double $$9 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
                this.setYRot(this.getYRot() + (float)$$9 / (float)this.lerpSteps);
                this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
                --this.lerpSteps;
                this.setPos($$6, $$7, $$8);
                this.setRot(this.getYRot(), this.getXRot());
            }
            this.phaseManager.getCurrentPhase().doClientTick();
        } else {
            Vec3 $$11;
            DragonPhaseInstance $$10 = this.phaseManager.getCurrentPhase();
            $$10.doServerTick();
            if (this.phaseManager.getCurrentPhase() != $$10) {
                $$10 = this.phaseManager.getCurrentPhase();
                $$10.doServerTick();
            }
            if (($$11 = $$10.getFlyTargetLocation()) != null) {
                double $$12 = $$11.x - this.getX();
                double $$13 = $$11.y - this.getY();
                double $$14 = $$11.z - this.getZ();
                double $$15 = $$12 * $$12 + $$13 * $$13 + $$14 * $$14;
                float $$16 = $$10.getFlySpeed();
                double $$17 = Math.sqrt((double)($$12 * $$12 + $$14 * $$14));
                if ($$17 > 0.0) {
                    $$13 = Mth.clamp($$13 / $$17, (double)(-$$16), (double)$$16);
                }
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, $$13 * 0.01, 0.0));
                this.setYRot(Mth.wrapDegrees(this.getYRot()));
                Vec3 $$18 = $$11.subtract(this.getX(), this.getY(), this.getZ()).normalize();
                Vec3 $$19 = new Vec3(Mth.sin(this.getYRot() * ((float)Math.PI / 180)), this.getDeltaMovement().y, -Mth.cos(this.getYRot() * ((float)Math.PI / 180))).normalize();
                float $$20 = Math.max((float)(((float)$$19.dot($$18) + 0.5f) / 1.5f), (float)0.0f);
                if (Math.abs((double)$$12) > (double)1.0E-5f || Math.abs((double)$$14) > (double)1.0E-5f) {
                    float $$21 = Mth.clamp(Mth.wrapDegrees(180.0f - (float)Mth.atan2($$12, $$14) * 57.295776f - this.getYRot()), -50.0f, 50.0f);
                    this.yRotA *= 0.8f;
                    this.yRotA += $$21 * $$10.getTurnSpeed();
                    this.setYRot(this.getYRot() + this.yRotA * 0.1f);
                }
                float $$22 = (float)(2.0 / ($$15 + 1.0));
                float $$23 = 0.06f;
                this.moveRelative(0.06f * ($$20 * $$22 + (1.0f - $$22)), new Vec3(0.0, 0.0, -1.0));
                if (this.inWall) {
                    this.move(MoverType.SELF, this.getDeltaMovement().scale(0.8f));
                } else {
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }
                Vec3 $$24 = this.getDeltaMovement().normalize();
                double $$25 = 0.8 + 0.15 * ($$24.dot($$19) + 1.0) / 2.0;
                this.setDeltaMovement(this.getDeltaMovement().multiply($$25, 0.91f, $$25));
            }
        }
        this.yBodyRot = this.getYRot();
        Vec3[] $$26 = new Vec3[this.subEntities.length];
        for (int $$27 = 0; $$27 < this.subEntities.length; ++$$27) {
            $$26[$$27] = new Vec3(this.subEntities[$$27].getX(), this.subEntities[$$27].getY(), this.subEntities[$$27].getZ());
        }
        float $$28 = (float)(this.getLatencyPos(5, 1.0f)[1] - this.getLatencyPos(10, 1.0f)[1]) * 10.0f * ((float)Math.PI / 180);
        float $$29 = Mth.cos($$28);
        float $$30 = Mth.sin($$28);
        float $$31 = this.getYRot() * ((float)Math.PI / 180);
        float $$32 = Mth.sin($$31);
        float $$33 = Mth.cos($$31);
        this.tickPart(this.body, $$32 * 0.5f, 0.0, -$$33 * 0.5f);
        this.tickPart(this.wing1, $$33 * 4.5f, 2.0, $$32 * 4.5f);
        this.tickPart(this.wing2, $$33 * -4.5f, 2.0, $$32 * -4.5f);
        if (!this.level.isClientSide && this.hurtTime == 0) {
            this.knockBack(this.level.getEntities(this, this.wing1.getBoundingBox().inflate(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
            this.knockBack(this.level.getEntities(this, this.wing2.getBoundingBox().inflate(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
            this.hurt(this.level.getEntities(this, this.head.getBoundingBox().inflate(1.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
            this.hurt(this.level.getEntities(this, this.neck.getBoundingBox().inflate(1.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
        }
        float $$34 = Mth.sin(this.getYRot() * ((float)Math.PI / 180) - this.yRotA * 0.01f);
        float $$35 = Mth.cos(this.getYRot() * ((float)Math.PI / 180) - this.yRotA * 0.01f);
        float $$36 = this.getHeadYOffset();
        this.tickPart(this.head, $$34 * 6.5f * $$29, $$36 + $$30 * 6.5f, -$$35 * 6.5f * $$29);
        this.tickPart(this.neck, $$34 * 5.5f * $$29, $$36 + $$30 * 5.5f, -$$35 * 5.5f * $$29);
        double[] $$37 = this.getLatencyPos(5, 1.0f);
        for (int $$38 = 0; $$38 < 3; ++$$38) {
            EnderDragonPart $$39 = null;
            if ($$38 == 0) {
                $$39 = this.tail1;
            }
            if ($$38 == 1) {
                $$39 = this.tail2;
            }
            if ($$38 == 2) {
                $$39 = this.tail3;
            }
            double[] $$40 = this.getLatencyPos(12 + $$38 * 2, 1.0f);
            float $$41 = this.getYRot() * ((float)Math.PI / 180) + this.rotWrap($$40[0] - $$37[0]) * ((float)Math.PI / 180);
            float $$42 = Mth.sin($$41);
            float $$43 = Mth.cos($$41);
            float $$44 = 1.5f;
            float $$45 = (float)($$38 + 1) * 2.0f;
            this.tickPart($$39, -($$32 * 1.5f + $$42 * $$45) * $$29, $$40[1] - $$37[1] - (double)(($$45 + 1.5f) * $$30) + 1.5, ($$33 * 1.5f + $$43 * $$45) * $$29);
        }
        if (!this.level.isClientSide) {
            this.inWall = this.checkWalls(this.head.getBoundingBox()) | this.checkWalls(this.neck.getBoundingBox()) | this.checkWalls(this.body.getBoundingBox());
            if (this.dragonFight != null) {
                this.dragonFight.updateDragon(this);
            }
        }
        for (int $$46 = 0; $$46 < this.subEntities.length; ++$$46) {
            this.subEntities[$$46].xo = $$26[$$46].x;
            this.subEntities[$$46].yo = $$26[$$46].y;
            this.subEntities[$$46].zo = $$26[$$46].z;
            this.subEntities[$$46].xOld = $$26[$$46].x;
            this.subEntities[$$46].yOld = $$26[$$46].y;
            this.subEntities[$$46].zOld = $$26[$$46].z;
        }
    }

    private void tickPart(EnderDragonPart $$0, double $$1, double $$2, double $$3) {
        $$0.setPos(this.getX() + $$1, this.getY() + $$2, this.getZ() + $$3);
    }

    private float getHeadYOffset() {
        if (this.phaseManager.getCurrentPhase().isSitting()) {
            return -1.0f;
        }
        double[] $$0 = this.getLatencyPos(5, 1.0f);
        double[] $$1 = this.getLatencyPos(0, 1.0f);
        return (float)($$0[1] - $$1[1]);
    }

    private void checkCrystals() {
        if (this.nearestCrystal != null) {
            if (this.nearestCrystal.isRemoved()) {
                this.nearestCrystal = null;
            } else if (this.tickCount % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0f);
            }
        }
        if (this.random.nextInt(10) == 0) {
            List $$0 = this.level.getEntitiesOfClass(EndCrystal.class, this.getBoundingBox().inflate(32.0));
            EndCrystal $$1 = null;
            double $$2 = Double.MAX_VALUE;
            for (EndCrystal $$3 : $$0) {
                double $$4 = $$3.distanceToSqr(this);
                if (!($$4 < $$2)) continue;
                $$2 = $$4;
                $$1 = $$3;
            }
            this.nearestCrystal = $$1;
        }
    }

    private void knockBack(List<Entity> $$0) {
        double $$1 = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
        double $$2 = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;
        for (Entity $$3 : $$0) {
            if (!($$3 instanceof LivingEntity)) continue;
            double $$4 = $$3.getX() - $$1;
            double $$5 = $$3.getZ() - $$2;
            double $$6 = Math.max((double)($$4 * $$4 + $$5 * $$5), (double)0.1);
            $$3.push($$4 / $$6 * 4.0, 0.2f, $$5 / $$6 * 4.0);
            if (this.phaseManager.getCurrentPhase().isSitting() || ((LivingEntity)$$3).getLastHurtByMobTimestamp() >= $$3.tickCount - 2) continue;
            $$3.hurt(DamageSource.mobAttack(this), 5.0f);
            this.doEnchantDamageEffects(this, $$3);
        }
    }

    private void hurt(List<Entity> $$0) {
        for (Entity $$1 : $$0) {
            if (!($$1 instanceof LivingEntity)) continue;
            $$1.hurt(DamageSource.mobAttack(this), 10.0f);
            this.doEnchantDamageEffects(this, $$1);
        }
    }

    private float rotWrap(double $$0) {
        return (float)Mth.wrapDegrees($$0);
    }

    private boolean checkWalls(AABB $$0) {
        int $$1 = Mth.floor($$0.minX);
        int $$2 = Mth.floor($$0.minY);
        int $$3 = Mth.floor($$0.minZ);
        int $$4 = Mth.floor($$0.maxX);
        int $$5 = Mth.floor($$0.maxY);
        int $$6 = Mth.floor($$0.maxZ);
        boolean $$7 = false;
        boolean $$8 = false;
        for (int $$9 = $$1; $$9 <= $$4; ++$$9) {
            for (int $$10 = $$2; $$10 <= $$5; ++$$10) {
                for (int $$11 = $$3; $$11 <= $$6; ++$$11) {
                    BlockPos $$12 = new BlockPos($$9, $$10, $$11);
                    BlockState $$13 = this.level.getBlockState($$12);
                    if ($$13.isAir() || $$13.is(BlockTags.DRAGON_TRANSPARENT)) continue;
                    if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || $$13.is(BlockTags.DRAGON_IMMUNE)) {
                        $$7 = true;
                        continue;
                    }
                    $$8 = this.level.removeBlock($$12, false) || $$8;
                }
            }
        }
        if ($$8) {
            BlockPos $$14 = new BlockPos($$1 + this.random.nextInt($$4 - $$1 + 1), $$2 + this.random.nextInt($$5 - $$2 + 1), $$3 + this.random.nextInt($$6 - $$3 + 1));
            this.level.levelEvent(2008, $$14, 0);
        }
        return $$7;
    }

    public boolean hurt(EnderDragonPart $$0, DamageSource $$1, float $$2) {
        if (this.phaseManager.getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
            return false;
        }
        $$2 = this.phaseManager.getCurrentPhase().onHurt($$1, $$2);
        if ($$0 != this.head) {
            $$2 = $$2 / 4.0f + Math.min((float)$$2, (float)1.0f);
        }
        if ($$2 < 0.01f) {
            return false;
        }
        if ($$1.getEntity() instanceof Player || $$1.isExplosion()) {
            float $$3 = this.getHealth();
            this.reallyHurt($$1, $$2);
            if (this.isDeadOrDying() && !this.phaseManager.getCurrentPhase().isSitting()) {
                this.setHealth(1.0f);
                this.phaseManager.setPhase(EnderDragonPhase.DYING);
            }
            if (this.phaseManager.getCurrentPhase().isSitting()) {
                this.sittingDamageReceived = this.sittingDamageReceived + $$3 - this.getHealth();
                if (this.sittingDamageReceived > 0.25f * this.getMaxHealth()) {
                    this.sittingDamageReceived = 0.0f;
                    this.phaseManager.setPhase(EnderDragonPhase.TAKEOFF);
                }
            }
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (!this.level.isClientSide) {
            return this.hurt(this.body, $$0, $$1);
        }
        return false;
    }

    protected boolean reallyHurt(DamageSource $$0, float $$1) {
        return super.hurt($$0, $$1);
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
        if (this.dragonFight != null) {
            this.dragonFight.updateDragon(this);
            this.dragonFight.setDragonKilled(this);
        }
    }

    @Override
    protected void tickDeath() {
        if (this.dragonFight != null) {
            this.dragonFight.updateDragon(this);
        }
        ++this.dragonDeathTime;
        if (this.dragonDeathTime >= 180 && this.dragonDeathTime <= 200) {
            float $$0 = (this.random.nextFloat() - 0.5f) * 8.0f;
            float $$1 = (this.random.nextFloat() - 0.5f) * 4.0f;
            float $$2 = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)$$0, this.getY() + 2.0 + (double)$$1, this.getZ() + (double)$$2, 0.0, 0.0, 0.0);
        }
        boolean $$3 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        int $$4 = 500;
        if (this.dragonFight != null && !this.dragonFight.hasPreviouslyKilledDragon()) {
            $$4 = 12000;
        }
        if (this.level instanceof ServerLevel) {
            if (this.dragonDeathTime > 150 && this.dragonDeathTime % 5 == 0 && $$3) {
                ExperienceOrb.award((ServerLevel)this.level, this.position(), Mth.floor((float)$$4 * 0.08f));
            }
            if (this.dragonDeathTime == 1 && !this.isSilent()) {
                this.level.globalLevelEvent(1028, this.blockPosition(), 0);
            }
        }
        this.move(MoverType.SELF, new Vec3(0.0, 0.1f, 0.0));
        this.setYRot(this.getYRot() + 20.0f);
        this.yBodyRot = this.getYRot();
        if (this.dragonDeathTime == 200 && this.level instanceof ServerLevel) {
            if ($$3) {
                ExperienceOrb.award((ServerLevel)this.level, this.position(), Mth.floor((float)$$4 * 0.2f));
            }
            if (this.dragonFight != null) {
                this.dragonFight.setDragonKilled(this);
            }
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    public int findClosestNode() {
        if (this.nodes[0] == null) {
            for (int $$0 = 0; $$0 < 24; ++$$0) {
                int $$8;
                int $$7;
                int $$1 = 5;
                int $$2 = $$0;
                if ($$0 < 12) {
                    int $$3 = Mth.floor(60.0f * Mth.cos(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)$$2)));
                    int $$4 = Mth.floor(60.0f * Mth.sin(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)$$2)));
                } else if ($$0 < 20) {
                    int $$5 = Mth.floor(40.0f * Mth.cos(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)($$2 -= 12))));
                    int $$6 = Mth.floor(40.0f * Mth.sin(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)$$2)));
                    $$1 += 10;
                } else {
                    $$7 = Mth.floor(20.0f * Mth.cos(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)($$2 -= 20))));
                    $$8 = Mth.floor(20.0f * Mth.sin(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)$$2)));
                }
                int $$9 = Math.max((int)(this.level.getSeaLevel() + 10), (int)(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos($$7, 0, $$8)).getY() + $$1));
                this.nodes[$$0] = new Node($$7, $$9, $$8);
            }
            this.nodeAdjacency[0] = 6146;
            this.nodeAdjacency[1] = 8197;
            this.nodeAdjacency[2] = 8202;
            this.nodeAdjacency[3] = 16404;
            this.nodeAdjacency[4] = 32808;
            this.nodeAdjacency[5] = 32848;
            this.nodeAdjacency[6] = 65696;
            this.nodeAdjacency[7] = 131392;
            this.nodeAdjacency[8] = 131712;
            this.nodeAdjacency[9] = 263424;
            this.nodeAdjacency[10] = 526848;
            this.nodeAdjacency[11] = 525313;
            this.nodeAdjacency[12] = 1581057;
            this.nodeAdjacency[13] = 3166214;
            this.nodeAdjacency[14] = 2138120;
            this.nodeAdjacency[15] = 6373424;
            this.nodeAdjacency[16] = 4358208;
            this.nodeAdjacency[17] = 12910976;
            this.nodeAdjacency[18] = 9044480;
            this.nodeAdjacency[19] = 9706496;
            this.nodeAdjacency[20] = 15216640;
            this.nodeAdjacency[21] = 0xD0E000;
            this.nodeAdjacency[22] = 11763712;
            this.nodeAdjacency[23] = 0x7E0000;
        }
        return this.findClosestNode(this.getX(), this.getY(), this.getZ());
    }

    public int findClosestNode(double $$0, double $$1, double $$2) {
        float $$3 = 10000.0f;
        int $$4 = 0;
        Node $$5 = new Node(Mth.floor($$0), Mth.floor($$1), Mth.floor($$2));
        int $$6 = 0;
        if (this.dragonFight == null || this.dragonFight.getCrystalsAlive() == 0) {
            $$6 = 12;
        }
        for (int $$7 = $$6; $$7 < 24; ++$$7) {
            float $$8;
            if (this.nodes[$$7] == null || !(($$8 = this.nodes[$$7].distanceToSqr($$5)) < $$3)) continue;
            $$3 = $$8;
            $$4 = $$7;
        }
        return $$4;
    }

    @Nullable
    public Path findPath(int $$0, int $$1, @Nullable Node $$2) {
        for (int $$3 = 0; $$3 < 24; ++$$3) {
            Node $$4 = this.nodes[$$3];
            $$4.closed = false;
            $$4.f = 0.0f;
            $$4.g = 0.0f;
            $$4.h = 0.0f;
            $$4.cameFrom = null;
            $$4.heapIdx = -1;
        }
        Node $$5 = this.nodes[$$0];
        Node $$6 = this.nodes[$$1];
        $$5.g = 0.0f;
        $$5.f = $$5.h = $$5.distanceTo($$6);
        this.openSet.clear();
        this.openSet.insert($$5);
        Node $$7 = $$5;
        int $$8 = 0;
        if (this.dragonFight == null || this.dragonFight.getCrystalsAlive() == 0) {
            $$8 = 12;
        }
        while (!this.openSet.isEmpty()) {
            Node $$9 = this.openSet.pop();
            if ($$9.equals($$6)) {
                if ($$2 != null) {
                    $$2.cameFrom = $$6;
                    $$6 = $$2;
                }
                return this.reconstructPath($$5, $$6);
            }
            if ($$9.distanceTo($$6) < $$7.distanceTo($$6)) {
                $$7 = $$9;
            }
            $$9.closed = true;
            int $$10 = 0;
            for (int $$11 = 0; $$11 < 24; ++$$11) {
                if (this.nodes[$$11] != $$9) continue;
                $$10 = $$11;
                break;
            }
            for (int $$12 = $$8; $$12 < 24; ++$$12) {
                if ((this.nodeAdjacency[$$10] & 1 << $$12) <= 0) continue;
                Node $$13 = this.nodes[$$12];
                if ($$13.closed) continue;
                float $$14 = $$9.g + $$9.distanceTo($$13);
                if ($$13.inOpenSet() && !($$14 < $$13.g)) continue;
                $$13.cameFrom = $$9;
                $$13.g = $$14;
                $$13.h = $$13.distanceTo($$6);
                if ($$13.inOpenSet()) {
                    this.openSet.changeCost($$13, $$13.g + $$13.h);
                    continue;
                }
                $$13.f = $$13.g + $$13.h;
                this.openSet.insert($$13);
            }
        }
        if ($$7 == $$5) {
            return null;
        }
        LOGGER.debug("Failed to find path from {} to {}", (Object)$$0, (Object)$$1);
        if ($$2 != null) {
            $$2.cameFrom = $$7;
            $$7 = $$2;
        }
        return this.reconstructPath($$5, $$7);
    }

    private Path reconstructPath(Node $$0, Node $$1) {
        ArrayList $$2 = Lists.newArrayList();
        Node $$3 = $$1;
        $$2.add(0, (Object)$$3);
        while ($$3.cameFrom != null) {
            $$3 = $$3.cameFrom;
            $$2.add(0, (Object)$$3);
        }
        return new Path((List<Node>)$$2, new BlockPos($$1.x, $$1.y, $$1.z), true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt(DRAGON_PHASE_KEY, this.phaseManager.getCurrentPhase().getPhase().getId());
        $$0.putInt(DRAGON_DEATH_TIME_KEY, this.dragonDeathTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains(DRAGON_PHASE_KEY)) {
            this.phaseManager.setPhase(EnderDragonPhase.getById($$0.getInt(DRAGON_PHASE_KEY)));
        }
        if ($$0.contains(DRAGON_DEATH_TIME_KEY)) {
            this.dragonDeathTime = $$0.getInt(DRAGON_DEATH_TIME_KEY);
        }
    }

    @Override
    public void checkDespawn() {
    }

    public EnderDragonPart[] getSubEntities() {
        return this.subEntities;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    public float getHeadPartYOffset(int $$0, double[] $$1, double[] $$2) {
        double $$10;
        DragonPhaseInstance $$3 = this.phaseManager.getCurrentPhase();
        EnderDragonPhase<? extends DragonPhaseInstance> $$4 = $$3.getPhase();
        if ($$4 == EnderDragonPhase.LANDING || $$4 == EnderDragonPhase.TAKEOFF) {
            BlockPos $$5 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            double $$6 = Math.max((double)(Math.sqrt((double)$$5.distToCenterSqr(this.position())) / 4.0), (double)1.0);
            double $$7 = (double)$$0 / $$6;
        } else if ($$3.isSitting()) {
            double $$8 = $$0;
        } else if ($$0 == 6) {
            double $$9 = 0.0;
        } else {
            $$10 = $$2[1] - $$1[1];
        }
        return (float)$$10;
    }

    public Vec3 getHeadLookVector(float $$0) {
        Vec3 $$12;
        DragonPhaseInstance $$1 = this.phaseManager.getCurrentPhase();
        EnderDragonPhase<? extends DragonPhaseInstance> $$2 = $$1.getPhase();
        if ($$2 == EnderDragonPhase.LANDING || $$2 == EnderDragonPhase.TAKEOFF) {
            BlockPos $$3 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            float $$4 = Math.max((float)((float)Math.sqrt((double)$$3.distToCenterSqr(this.position())) / 4.0f), (float)1.0f);
            float $$5 = 6.0f / $$4;
            float $$6 = this.getXRot();
            float $$7 = 1.5f;
            this.setXRot(-$$5 * 1.5f * 5.0f);
            Vec3 $$8 = this.getViewVector($$0);
            this.setXRot($$6);
        } else if ($$1.isSitting()) {
            float $$9 = this.getXRot();
            float $$10 = 1.5f;
            this.setXRot(-45.0f);
            Vec3 $$11 = this.getViewVector($$0);
            this.setXRot($$9);
        } else {
            $$12 = this.getViewVector($$0);
        }
        return $$12;
    }

    public void onCrystalDestroyed(EndCrystal $$0, BlockPos $$1, DamageSource $$2) {
        Player $$4;
        if ($$2.getEntity() instanceof Player) {
            Player $$3 = (Player)$$2.getEntity();
        } else {
            $$4 = this.level.getNearestPlayer(CRYSTAL_DESTROY_TARGETING, $$1.getX(), $$1.getY(), $$1.getZ());
        }
        if ($$0 == this.nearestCrystal) {
            this.hurt(this.head, DamageSource.explosion($$0, $$4), 10.0f);
        }
        this.phaseManager.getCurrentPhase().onCrystalDestroyed($$0, $$1, $$2, $$4);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (DATA_PHASE.equals($$0) && this.level.isClientSide) {
            this.phaseManager.setPhase(EnderDragonPhase.getById(this.getEntityData().get(DATA_PHASE)));
        }
        super.onSyncedDataUpdated($$0);
    }

    public EnderDragonPhaseManager getPhaseManager() {
        return this.phaseManager;
    }

    @Nullable
    public EndDragonFight getDragonFight() {
        return this.dragonFight;
    }

    @Override
    public boolean addEffect(MobEffectInstance $$0, @Nullable Entity $$1) {
        return false;
    }

    @Override
    protected boolean canRide(Entity $$0) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        EnderDragonPart[] $$1 = this.getSubEntities();
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            $$1[$$2].setId($$2 + $$0.getId());
        }
    }

    @Override
    public boolean canAttack(LivingEntity $$0) {
        return $$0.canBeSeenAsEnemy();
    }
}