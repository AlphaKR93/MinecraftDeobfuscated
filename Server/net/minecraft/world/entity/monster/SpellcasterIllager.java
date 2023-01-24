/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.IntFunction
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;

public abstract class SpellcasterIllager
extends AbstractIllager {
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
    protected int spellCastingTickCount;
    private IllagerSpell currentSpell = IllagerSpell.NONE;

    protected SpellcasterIllager(EntityType<? extends SpellcasterIllager> $$0, Level $$1) {
        super((EntityType<? extends AbstractIllager>)$$0, $$1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.spellCastingTickCount = $$0.getInt("SpellTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("SpellTicks", this.spellCastingTickCount);
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        }
        if (this.isCelebrating()) {
            return AbstractIllager.IllagerArmPose.CELEBRATING;
        }
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
        }
        return this.spellCastingTickCount > 0;
    }

    public void setIsCastingSpell(IllagerSpell $$0) {
        this.currentSpell = $$0;
        this.entityData.set(DATA_SPELL_CASTING_ID, (byte)$$0.id);
    }

    protected IllagerSpell getCurrentSpell() {
        if (!this.level.isClientSide) {
            return this.currentSpell;
        }
        return IllagerSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID).byteValue());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && this.isCastingSpell()) {
            IllagerSpell $$0 = this.getCurrentSpell();
            double $$1 = $$0.spellColor[0];
            double $$2 = $$0.spellColor[1];
            double $$3 = $$0.spellColor[2];
            float $$4 = this.yBodyRot * ((float)Math.PI / 180) + Mth.cos((float)this.tickCount * 0.6662f) * 0.25f;
            float $$5 = Mth.cos($$4);
            float $$6 = Mth.sin($$4);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)$$5 * 0.6, this.getY() + 1.8, this.getZ() + (double)$$6 * 0.6, $$1, $$2, $$3);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)$$5 * 0.6, this.getY() + 1.8, this.getZ() - (double)$$6 * 0.6, $$1, $$2, $$3);
        }
    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    protected static enum IllagerSpell {
        NONE(0, 0.0, 0.0, 0.0),
        SUMMON_VEX(1, 0.7, 0.7, 0.8),
        FANGS(2, 0.4, 0.3, 0.35),
        WOLOLO(3, 0.7, 0.5, 0.2),
        DISAPPEAR(4, 0.3, 0.3, 0.8),
        BLINDNESS(5, 0.1, 0.1, 0.2);

        private static final IntFunction<IllagerSpell> BY_ID;
        final int id;
        final double[] spellColor;

        private IllagerSpell(int $$0, double $$1, double $$2, double $$3) {
            this.id = $$0;
            this.spellColor = new double[]{$$1, $$2, $$3};
        }

        public static IllagerSpell byId(int $$0) {
            return (IllagerSpell)((Object)BY_ID.apply($$0));
        }

        static {
            BY_ID = ByIdMap.continuous($$0 -> $$0.id, IllagerSpell.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        }
    }

    protected abstract class SpellcasterUseSpellGoal
    extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        protected SpellcasterUseSpellGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = SpellcasterIllager.this.getTarget();
            if ($$0 == null || !$$0.isAlive()) {
                return false;
            }
            if (SpellcasterIllager.this.isCastingSpell()) {
                return false;
            }
            return SpellcasterIllager.this.tickCount >= this.nextAttackTickCount;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity $$0 = SpellcasterIllager.this.getTarget();
            return $$0 != null && $$0.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
            SoundEvent $$0 = this.getSpellPrepareSound();
            if ($$0 != null) {
                SpellcasterIllager.this.playSound($$0, 1.0f, 1.0f);
            }
            SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
        }

        @Override
        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                SpellcasterIllager.this.playSound(SpellcasterIllager.this.getCastingSoundEvent(), 1.0f, 1.0f);
            }
        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract IllagerSpell getSpell();
    }

    protected class SpellcasterCastingSpellGoal
    extends Goal {
        public SpellcasterCastingSpellGoal() {
            this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return SpellcasterIllager.this.getSpellCastingTime() > 0;
        }

        @Override
        public void start() {
            super.start();
            SpellcasterIllager.this.navigation.stop();
        }

        @Override
        public void stop() {
            super.stop();
            SpellcasterIllager.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public void tick() {
            if (SpellcasterIllager.this.getTarget() != null) {
                SpellcasterIllager.this.getLookControl().setLookAt(SpellcasterIllager.this.getTarget(), SpellcasterIllager.this.getMaxHeadYRot(), SpellcasterIllager.this.getMaxHeadXRot());
            }
        }
    }
}