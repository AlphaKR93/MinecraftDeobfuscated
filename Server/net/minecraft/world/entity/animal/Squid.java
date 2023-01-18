/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class Squid
extends WaterAnimal {
    public float xBodyRot;
    public float xBodyRotO;
    public float zBodyRot;
    public float zBodyRotO;
    public float tentacleMovement;
    public float oldTentacleMovement;
    public float tentacleAngle;
    public float oldTentacleAngle;
    private float speed;
    private float tentacleSpeed;
    private float rotateSpeed;
    private float tx;
    private float ty;
    private float tz;

    public Squid(EntityType<? extends Squid> $$0, Level $$1) {
        super((EntityType<? extends WaterAnimal>)$$0, $$1);
        this.random.setSeed(this.getId());
        this.tentacleSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SquidRandomMovementGoal(this));
        this.goalSelector.addGoal(1, new SquidFleeGoal());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.5f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SQUID_DEATH;
    }

    protected SoundEvent getSquirtSound() {
        return SoundEvents.SQUID_SQUIRT;
    }

    @Override
    public boolean canBeLeashed(Player $$0) {
        return !this.isLeashed();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.xBodyRotO = this.xBodyRot;
        this.zBodyRotO = this.zBodyRot;
        this.oldTentacleMovement = this.tentacleMovement;
        this.oldTentacleAngle = this.tentacleAngle;
        this.tentacleMovement += this.tentacleSpeed;
        if ((double)this.tentacleMovement > Math.PI * 2) {
            if (this.level.isClientSide) {
                this.tentacleMovement = (float)Math.PI * 2;
            } else {
                this.tentacleMovement -= (float)Math.PI * 2;
                if (this.random.nextInt(10) == 0) {
                    this.tentacleSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
                }
                this.level.broadcastEntityEvent(this, (byte)19);
            }
        }
        if (this.isInWaterOrBubble()) {
            if (this.tentacleMovement < (float)Math.PI) {
                float $$0 = this.tentacleMovement / (float)Math.PI;
                this.tentacleAngle = Mth.sin($$0 * $$0 * (float)Math.PI) * (float)Math.PI * 0.25f;
                if ((double)$$0 > 0.75) {
                    this.speed = 1.0f;
                    this.rotateSpeed = 1.0f;
                } else {
                    this.rotateSpeed *= 0.8f;
                }
            } else {
                this.tentacleAngle = 0.0f;
                this.speed *= 0.9f;
                this.rotateSpeed *= 0.99f;
            }
            if (!this.level.isClientSide) {
                this.setDeltaMovement(this.tx * this.speed, this.ty * this.speed, this.tz * this.speed);
            }
            Vec3 $$1 = this.getDeltaMovement();
            double $$2 = $$1.horizontalDistance();
            this.yBodyRot += (-((float)Mth.atan2($$1.x, $$1.z)) * 57.295776f - this.yBodyRot) * 0.1f;
            this.setYRot(this.yBodyRot);
            this.zBodyRot += (float)Math.PI * this.rotateSpeed * 1.5f;
            this.xBodyRot += (-((float)Mth.atan2($$2, $$1.y)) * 57.295776f - this.xBodyRot) * 0.1f;
        } else {
            this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * (float)Math.PI * 0.25f;
            if (!this.level.isClientSide) {
                double $$3 = this.getDeltaMovement().y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    $$3 = 0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
                } else if (!this.isNoGravity()) {
                    $$3 -= 0.08;
                }
                this.setDeltaMovement(0.0, $$3 * (double)0.98f, 0.0);
            }
            this.xBodyRot += (-90.0f - this.xBodyRot) * 0.02f;
        }
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (super.hurt($$0, $$1) && this.getLastHurtByMob() != null) {
            if (!this.level.isClientSide) {
                this.spawnInk();
            }
            return true;
        }
        return false;
    }

    private Vec3 rotateVector(Vec3 $$0) {
        Vec3 $$1 = $$0.xRot(this.xBodyRotO * ((float)Math.PI / 180));
        $$1 = $$1.yRot(-this.yBodyRotO * ((float)Math.PI / 180));
        return $$1;
    }

    private void spawnInk() {
        this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
        Vec3 $$0 = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());
        for (int $$1 = 0; $$1 < 30; ++$$1) {
            Vec3 $$2 = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
            Vec3 $$3 = $$2.scale(0.3 + (double)(this.random.nextFloat() * 2.0f));
            ((ServerLevel)this.level).sendParticles(this.getInkParticle(), $$0.x, $$0.y + 0.5, $$0.z, 0, $$3.x, $$3.y, $$3.z, 0.1f);
        }
    }

    protected ParticleOptions getInkParticle() {
        return ParticleTypes.SQUID_INK;
    }

    @Override
    public void travel(Vec3 $$0) {
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 19) {
            this.tentacleMovement = 0.0f;
        } else {
            super.handleEntityEvent($$0);
        }
    }

    public void setMovementVector(float $$0, float $$1, float $$2) {
        this.tx = $$0;
        this.ty = $$1;
        this.tz = $$2;
    }

    public boolean hasMovementVector() {
        return this.tx != 0.0f || this.ty != 0.0f || this.tz != 0.0f;
    }

    class SquidRandomMovementGoal
    extends Goal {
        private final Squid squid;

        public SquidRandomMovementGoal(Squid $$0) {
            this.squid = $$0;
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            int $$0 = this.squid.getNoActionTime();
            if ($$0 > 100) {
                this.squid.setMovementVector(0.0f, 0.0f, 0.0f);
            } else if (this.squid.getRandom().nextInt(SquidRandomMovementGoal.reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
                float $$1 = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2);
                float $$2 = Mth.cos($$1) * 0.2f;
                float $$3 = -0.1f + this.squid.getRandom().nextFloat() * 0.2f;
                float $$4 = Mth.sin($$1) * 0.2f;
                this.squid.setMovementVector($$2, $$3, $$4);
            }
        }
    }

    class SquidFleeGoal
    extends Goal {
        private static final float SQUID_FLEE_SPEED = 3.0f;
        private static final float SQUID_FLEE_MIN_DISTANCE = 5.0f;
        private static final float SQUID_FLEE_MAX_DISTANCE = 10.0f;
        private int fleeTicks;

        SquidFleeGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = Squid.this.getLastHurtByMob();
            if (Squid.this.isInWater() && $$0 != null) {
                return Squid.this.distanceToSqr($$0) < 100.0;
            }
            return false;
        }

        @Override
        public void start() {
            this.fleeTicks = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.fleeTicks;
            LivingEntity $$0 = Squid.this.getLastHurtByMob();
            if ($$0 == null) {
                return;
            }
            Vec3 $$1 = new Vec3(Squid.this.getX() - $$0.getX(), Squid.this.getY() - $$0.getY(), Squid.this.getZ() - $$0.getZ());
            BlockState $$2 = Squid.this.level.getBlockState(new BlockPos(Squid.this.getX() + $$1.x, Squid.this.getY() + $$1.y, Squid.this.getZ() + $$1.z));
            FluidState $$3 = Squid.this.level.getFluidState(new BlockPos(Squid.this.getX() + $$1.x, Squid.this.getY() + $$1.y, Squid.this.getZ() + $$1.z));
            if ($$3.is(FluidTags.WATER) || $$2.isAir()) {
                double $$4 = $$1.length();
                if ($$4 > 0.0) {
                    $$1.normalize();
                    double $$5 = 3.0;
                    if ($$4 > 5.0) {
                        $$5 -= ($$4 - 5.0) / 5.0;
                    }
                    if ($$5 > 0.0) {
                        $$1 = $$1.scale($$5);
                    }
                }
                if ($$2.isAir()) {
                    $$1 = $$1.subtract(0.0, $$1.y, 0.0);
                }
                Squid.this.setMovementVector((float)$$1.x / 20.0f, (float)$$1.y / 20.0f, (float)$$1.z / 20.0f);
            }
            if (this.fleeTicks % 10 == 5) {
                Squid.this.level.addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }
}