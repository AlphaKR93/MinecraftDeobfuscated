/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  java.lang.Boolean
 *  java.lang.Comparable
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class MobEffectInstance
implements Comparable<MobEffectInstance> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int INFINITE_DURATION = -1;
    private final MobEffect effect;
    private int duration;
    private int amplifier;
    private boolean ambient;
    private boolean visible;
    private boolean showIcon;
    @Nullable
    private MobEffectInstance hiddenEffect;
    private final Optional<FactorData> factorData;

    public MobEffectInstance(MobEffect $$0) {
        this($$0, 0, 0);
    }

    public MobEffectInstance(MobEffect $$0, int $$1) {
        this($$0, $$1, 0);
    }

    public MobEffectInstance(MobEffect $$0, int $$1, int $$2) {
        this($$0, $$1, $$2, false, true);
    }

    public MobEffectInstance(MobEffect $$0, int $$1, int $$2, boolean $$3, boolean $$4) {
        this($$0, $$1, $$2, $$3, $$4, $$4);
    }

    public MobEffectInstance(MobEffect $$0, int $$1, int $$2, boolean $$3, boolean $$4, boolean $$5) {
        this($$0, $$1, $$2, $$3, $$4, $$5, null, $$0.createFactorData());
    }

    public MobEffectInstance(MobEffect $$0, int $$1, int $$2, boolean $$3, boolean $$4, boolean $$5, @Nullable MobEffectInstance $$6, Optional<FactorData> $$7) {
        this.effect = $$0;
        this.duration = $$1;
        this.amplifier = $$2;
        this.ambient = $$3;
        this.visible = $$4;
        this.showIcon = $$5;
        this.hiddenEffect = $$6;
        this.factorData = $$7;
    }

    public MobEffectInstance(MobEffectInstance $$0) {
        this.effect = $$0.effect;
        this.factorData = this.effect.createFactorData();
        this.setDetailsFrom($$0);
    }

    public Optional<FactorData> getFactorData() {
        return this.factorData;
    }

    void setDetailsFrom(MobEffectInstance $$0) {
        this.duration = $$0.duration;
        this.amplifier = $$0.amplifier;
        this.ambient = $$0.ambient;
        this.visible = $$0.visible;
        this.showIcon = $$0.showIcon;
    }

    public boolean update(MobEffectInstance $$0) {
        if (this.effect != $$0.effect) {
            LOGGER.warn("This method should only be called for matching effects!");
        }
        int $$1 = this.duration;
        boolean $$2 = false;
        if ($$0.amplifier > this.amplifier) {
            if ($$0.isShorterDurationThan(this)) {
                MobEffectInstance $$3 = this.hiddenEffect;
                this.hiddenEffect = new MobEffectInstance(this);
                this.hiddenEffect.hiddenEffect = $$3;
            }
            this.amplifier = $$0.amplifier;
            this.duration = $$0.duration;
            $$2 = true;
        } else if (this.isShorterDurationThan($$0)) {
            if ($$0.amplifier == this.amplifier) {
                this.duration = $$0.duration;
                $$2 = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new MobEffectInstance($$0);
            } else {
                this.hiddenEffect.update($$0);
            }
        }
        if (!$$0.ambient && this.ambient || $$2) {
            this.ambient = $$0.ambient;
            $$2 = true;
        }
        if ($$0.visible != this.visible) {
            this.visible = $$0.visible;
            $$2 = true;
        }
        if ($$0.showIcon != this.showIcon) {
            this.showIcon = $$0.showIcon;
            $$2 = true;
        }
        return $$2;
    }

    private boolean isShorterDurationThan(MobEffectInstance $$0) {
        return !this.isInfiniteDuration() && (this.duration < $$0.duration || $$0.isInfiniteDuration());
    }

    public boolean isInfiniteDuration() {
        return this.duration == -1;
    }

    public boolean endsWithin(int $$0) {
        return !this.isInfiniteDuration() && this.duration <= $$0;
    }

    public int mapDuration(Int2IntFunction $$0) {
        if (this.isInfiniteDuration()) {
            return -1;
        }
        return $$0.applyAsInt(this.duration);
    }

    public MobEffect getEffect() {
        return this.effect;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean showIcon() {
        return this.showIcon;
    }

    public boolean tick(LivingEntity $$02, Runnable $$1) {
        if (this.hasRemainingDuration()) {
            int $$2;
            int n = $$2 = this.isInfiniteDuration() ? $$02.tickCount : this.duration;
            if (this.effect.isDurationEffectTick($$2, this.amplifier)) {
                this.applyEffect($$02);
            }
            this.tickDownDuration();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.setDetailsFrom(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                $$1.run();
            }
        }
        this.factorData.ifPresent($$0 -> $$0.tick(this));
        return this.hasRemainingDuration();
    }

    private boolean hasRemainingDuration() {
        return this.isInfiniteDuration() || this.duration > 0;
    }

    private int tickDownDuration() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.tickDownDuration();
        }
        this.duration = this.mapDuration($$0 -> $$0 - 1);
        return this.duration;
    }

    public void applyEffect(LivingEntity $$0) {
        if (this.hasRemainingDuration()) {
            this.effect.applyEffectTick($$0, this.amplifier);
        }
    }

    public String getDescriptionId() {
        return this.effect.getDescriptionId();
    }

    public String toString() {
        String $$1;
        if (this.amplifier > 0) {
            String $$0 = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.describeDuration();
        } else {
            $$1 = this.getDescriptionId() + ", Duration: " + this.describeDuration();
        }
        if (!this.visible) {
            $$1 = $$1 + ", Particles: false";
        }
        if (!this.showIcon) {
            $$1 = $$1 + ", Show Icon: false";
        }
        return $$1;
    }

    private String describeDuration() {
        if (this.isInfiniteDuration()) {
            return "infinite";
        }
        return Integer.toString((int)this.duration);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof MobEffectInstance) {
            MobEffectInstance $$1 = (MobEffectInstance)$$0;
            return this.duration == $$1.duration && this.amplifier == $$1.amplifier && this.ambient == $$1.ambient && this.effect.equals($$1.effect);
        }
        return false;
    }

    public int hashCode() {
        int $$0 = this.effect.hashCode();
        $$0 = 31 * $$0 + this.duration;
        $$0 = 31 * $$0 + this.amplifier;
        $$0 = 31 * $$0 + (this.ambient ? 1 : 0);
        return $$0;
    }

    public CompoundTag save(CompoundTag $$0) {
        $$0.putInt("Id", MobEffect.getId(this.getEffect()));
        this.writeDetailsTo($$0);
        return $$0;
    }

    private void writeDetailsTo(CompoundTag $$0) {
        $$0.putByte("Amplifier", (byte)this.getAmplifier());
        $$0.putInt("Duration", this.getDuration());
        $$0.putBoolean("Ambient", this.isAmbient());
        $$0.putBoolean("ShowParticles", this.isVisible());
        $$0.putBoolean("ShowIcon", this.showIcon());
        if (this.hiddenEffect != null) {
            CompoundTag $$1 = new CompoundTag();
            this.hiddenEffect.save($$1);
            $$0.put("HiddenEffect", $$1);
        }
        this.factorData.ifPresent($$12 -> FactorData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, $$12).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("FactorCalculationData", (Tag)$$1)));
    }

    @Nullable
    public static MobEffectInstance load(CompoundTag $$0) {
        int $$1 = $$0.getInt("Id");
        MobEffect $$2 = MobEffect.byId($$1);
        if ($$2 == null) {
            return null;
        }
        return MobEffectInstance.loadSpecifiedEffect($$2, $$0);
    }

    private static MobEffectInstance loadSpecifiedEffect(MobEffect $$0, CompoundTag $$1) {
        Optional $$9;
        byte $$2 = $$1.getByte("Amplifier");
        int $$3 = $$1.getInt("Duration");
        boolean $$4 = $$1.getBoolean("Ambient");
        boolean $$5 = true;
        if ($$1.contains("ShowParticles", 1)) {
            $$5 = $$1.getBoolean("ShowParticles");
        }
        boolean $$6 = $$5;
        if ($$1.contains("ShowIcon", 1)) {
            $$6 = $$1.getBoolean("ShowIcon");
        }
        MobEffectInstance $$7 = null;
        if ($$1.contains("HiddenEffect", 10)) {
            $$7 = MobEffectInstance.loadSpecifiedEffect($$0, $$1.getCompound("HiddenEffect"));
        }
        if ($$1.contains("FactorCalculationData", 10)) {
            Optional $$8 = FactorData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$1.getCompound("FactorCalculationData"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0));
        } else {
            $$9 = Optional.empty();
        }
        return new MobEffectInstance($$0, $$3, Math.max((int)$$2, (int)0), $$4, $$5, $$6, $$7, (Optional<FactorData>)$$9);
    }

    public int compareTo(MobEffectInstance $$0) {
        int $$1 = 32147;
        if (this.getDuration() > 32147 && $$0.getDuration() > 32147 || this.isAmbient() && $$0.isAmbient()) {
            return ComparisonChain.start().compare(Boolean.valueOf((boolean)this.isAmbient()), Boolean.valueOf((boolean)$$0.isAmbient())).compare(this.getEffect().getColor(), $$0.getEffect().getColor()).result();
        }
        return ComparisonChain.start().compareFalseFirst(this.isAmbient(), $$0.isAmbient()).compareFalseFirst(this.isInfiniteDuration(), $$0.isInfiniteDuration()).compare(this.getDuration(), $$0.getDuration()).compare(this.getEffect().getColor(), $$0.getEffect().getColor()).result();
    }

    public static class FactorData {
        public static final Codec<FactorData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("padding_duration").forGetter($$0 -> $$0.paddingDuration), (App)Codec.FLOAT.fieldOf("factor_start").orElse((Object)Float.valueOf((float)0.0f)).forGetter($$0 -> Float.valueOf((float)$$0.factorStart)), (App)Codec.FLOAT.fieldOf("factor_target").orElse((Object)Float.valueOf((float)1.0f)).forGetter($$0 -> Float.valueOf((float)$$0.factorTarget)), (App)Codec.FLOAT.fieldOf("factor_current").orElse((Object)Float.valueOf((float)0.0f)).forGetter($$0 -> Float.valueOf((float)$$0.factorCurrent)), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_active").orElse((Object)0).forGetter($$0 -> $$0.ticksActive), (App)Codec.FLOAT.fieldOf("factor_previous_frame").orElse((Object)Float.valueOf((float)0.0f)).forGetter($$0 -> Float.valueOf((float)$$0.factorPreviousFrame)), (App)Codec.BOOL.fieldOf("had_effect_last_tick").orElse((Object)false).forGetter($$0 -> $$0.hadEffectLastTick)).apply((Applicative)$$02, FactorData::new));
        private final int paddingDuration;
        private float factorStart;
        private float factorTarget;
        private float factorCurrent;
        private int ticksActive;
        private float factorPreviousFrame;
        private boolean hadEffectLastTick;

        public FactorData(int $$0, float $$1, float $$2, float $$3, int $$4, float $$5, boolean $$6) {
            this.paddingDuration = $$0;
            this.factorStart = $$1;
            this.factorTarget = $$2;
            this.factorCurrent = $$3;
            this.ticksActive = $$4;
            this.factorPreviousFrame = $$5;
            this.hadEffectLastTick = $$6;
        }

        public FactorData(int $$0) {
            this($$0, 0.0f, 1.0f, 0.0f, 0, 0.0f, false);
        }

        public void tick(MobEffectInstance $$0) {
            this.factorPreviousFrame = this.factorCurrent;
            boolean $$1 = !$$0.endsWithin(this.paddingDuration);
            ++this.ticksActive;
            if (this.hadEffectLastTick != $$1) {
                this.hadEffectLastTick = $$1;
                this.ticksActive = 0;
                this.factorStart = this.factorCurrent;
                this.factorTarget = $$1 ? 1.0f : 0.0f;
            }
            float $$2 = Mth.clamp((float)this.ticksActive / (float)this.paddingDuration, 0.0f, 1.0f);
            this.factorCurrent = Mth.lerp($$2, this.factorStart, this.factorTarget);
        }

        public float getFactor(LivingEntity $$0, float $$1) {
            if ($$0.isRemoved()) {
                this.factorPreviousFrame = this.factorCurrent;
            }
            return Mth.lerp($$1, this.factorPreviousFrame, this.factorCurrent);
        }
    }
}