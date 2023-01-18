/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.effect;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class MobEffectUtil {
    public static String formatDuration(MobEffectInstance $$0, float $$1) {
        int $$2 = Mth.floor((float)$$0.getDuration() * $$1);
        return StringUtil.formatTickDuration($$2);
    }

    public static boolean hasDigSpeed(LivingEntity $$0) {
        return $$0.hasEffect(MobEffects.DIG_SPEED) || $$0.hasEffect(MobEffects.CONDUIT_POWER);
    }

    public static int getDigSpeedAmplification(LivingEntity $$0) {
        int $$1 = 0;
        int $$2 = 0;
        if ($$0.hasEffect(MobEffects.DIG_SPEED)) {
            $$1 = $$0.getEffect(MobEffects.DIG_SPEED).getAmplifier();
        }
        if ($$0.hasEffect(MobEffects.CONDUIT_POWER)) {
            $$2 = $$0.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
        }
        return Math.max((int)$$1, (int)$$2);
    }

    public static boolean hasWaterBreathing(LivingEntity $$0) {
        return $$0.hasEffect(MobEffects.WATER_BREATHING) || $$0.hasEffect(MobEffects.CONDUIT_POWER);
    }

    public static List<ServerPlayer> addEffectToPlayersAround(ServerLevel $$0, @Nullable Entity $$1, Vec3 $$22, double $$3, MobEffectInstance $$4, int $$5) {
        MobEffect $$62 = $$4.getEffect();
        List<ServerPlayer> $$7 = $$0.getPlayers((Predicate<? super ServerPlayer>)((Predicate)$$6 -> !(!$$6.gameMode.isSurvival() || $$1 != null && $$1.isAlliedTo((Entity)$$6) || !$$22.closerThan($$6.position(), $$3) || $$6.hasEffect($$62) && $$6.getEffect($$62).getAmplifier() >= $$4.getAmplifier() && $$6.getEffect($$62).getDuration() >= $$5)));
        $$7.forEach($$2 -> $$2.addEffect(new MobEffectInstance($$4), $$1));
        return $$7;
    }
}