/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.BiPredicate
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class LongJumpToRandomPos<E extends Mob>
extends Behavior<E> {
    protected static final int FIND_JUMP_TRIES = 20;
    private static final int PREPARE_JUMP_DURATION = 40;
    protected static final int MIN_PATHFIND_DISTANCE_TO_VALID_JUMP = 8;
    private static final int TIME_OUT_DURATION = 200;
    private static final List<Integer> ALLOWED_ANGLES = Lists.newArrayList((Object[])new Integer[]{65, 70, 75, 80});
    private final UniformInt timeBetweenLongJumps;
    protected final int maxLongJumpHeight;
    protected final int maxLongJumpWidth;
    protected final float maxJumpVelocity;
    protected List<PossibleJump> jumpCandidates = Lists.newArrayList();
    protected Optional<Vec3> initialPosition = Optional.empty();
    @Nullable
    protected Vec3 chosenJump;
    protected int findJumpTries;
    protected long prepareJumpStart;
    private final Function<E, SoundEvent> getJumpSound;
    private final BiPredicate<E, BlockPos> acceptableLandingSpot;

    public LongJumpToRandomPos(UniformInt $$0, int $$1, int $$2, float $$3, Function<E, SoundEvent> $$4) {
        this($$0, $$1, $$2, $$3, $$4, LongJumpToRandomPos::defaultAcceptableLandingSpot);
    }

    public static <E extends Mob> boolean defaultAcceptableLandingSpot(E $$0, BlockPos $$1) {
        Level $$2 = $$0.level;
        Vec3i $$3 = $$1.below();
        return $$2.getBlockState((BlockPos)$$3).isSolidRender($$2, (BlockPos)$$3) && $$0.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic($$2, $$1.mutable())) == 0.0f;
    }

    public LongJumpToRandomPos(UniformInt $$0, int $$1, int $$2, float $$3, Function<E, SoundEvent> $$4, BiPredicate<E, BlockPos> $$5) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.LONG_JUMP_MID_JUMP, (Object)((Object)MemoryStatus.VALUE_ABSENT)), 200);
        this.timeBetweenLongJumps = $$0;
        this.maxLongJumpHeight = $$1;
        this.maxLongJumpWidth = $$2;
        this.maxJumpVelocity = $$3;
        this.getJumpSound = $$4;
        this.acceptableLandingSpot = $$5;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Mob $$1) {
        boolean $$2;
        boolean bl = $$2 = $$1.isOnGround() && !$$1.isInWater() && !$$1.isInLava() && !$$0.getBlockState($$1.blockPosition()).is(Blocks.HONEY_BLOCK);
        if (!$$2) {
            $$1.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample($$0.random) / 2);
        }
        return $$2;
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Mob $$1, long $$2) {
        boolean $$3;
        boolean bl = $$3 = this.initialPosition.isPresent() && ((Vec3)this.initialPosition.get()).equals($$1.position()) && this.findJumpTries > 0 && !$$1.isInWaterOrBubble() && (this.chosenJump != null || !this.jumpCandidates.isEmpty());
        if (!$$3 && $$1.getBrain().getMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            $$1.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample($$0.random) / 2);
            $$1.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        }
        return $$3;
    }

    @Override
    protected void start(ServerLevel $$0, E $$12, long $$2) {
        this.chosenJump = null;
        this.findJumpTries = 20;
        this.initialPosition = Optional.of((Object)((Entity)$$12).position());
        BlockPos $$3 = ((Entity)$$12).blockPosition();
        int $$4 = $$3.getX();
        int $$5 = $$3.getY();
        int $$6 = $$3.getZ();
        this.jumpCandidates = (List)BlockPos.betweenClosedStream($$4 - this.maxLongJumpWidth, $$5 - this.maxLongJumpHeight, $$6 - this.maxLongJumpWidth, $$4 + this.maxLongJumpWidth, $$5 + this.maxLongJumpHeight, $$6 + this.maxLongJumpWidth).filter($$1 -> !$$1.equals($$3)).map($$1 -> new PossibleJump($$1.immutable(), Mth.ceil($$3.distSqr((Vec3i)$$1)))).collect(Collectors.toCollection(Lists::newArrayList));
    }

    @Override
    protected void tick(ServerLevel $$0, E $$1, long $$2) {
        if (this.chosenJump != null) {
            if ($$2 - this.prepareJumpStart >= 40L) {
                ((Entity)$$1).setYRot(((Mob)$$1).yBodyRot);
                ((LivingEntity)$$1).setDiscardFriction(true);
                double $$3 = this.chosenJump.length();
                double $$4 = $$3 + ((LivingEntity)$$1).getJumpBoostPower();
                ((Entity)$$1).setDeltaMovement(this.chosenJump.scale($$4 / $$3));
                ((LivingEntity)$$1).getBrain().setMemory(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
                $$0.playSound(null, (Entity)$$1, (SoundEvent)this.getJumpSound.apply($$1), SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
        } else {
            --this.findJumpTries;
            this.start($$0, $$1, $$2);
        }
    }

    @Override
    protected void start(ServerLevel $$0, E $$1, long $$2) {
        while (!this.jumpCandidates.isEmpty()) {
            Vec3 $$6;
            Vec3 $$7;
            PossibleJump $$4;
            BlockPos $$5;
            Optional<PossibleJump> $$3 = this.getJumpCandidate($$0);
            if ($$3.isEmpty() || !this.isAcceptableLandingPosition($$0, $$1, $$5 = ($$4 = (PossibleJump)$$3.get()).getJumpTarget()) || ($$7 = this.calculateOptimalJumpVector((Mob)$$1, $$6 = Vec3.atCenterOf($$5))) == null) continue;
            ((LivingEntity)$$1).getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker($$5));
            PathNavigation $$8 = ((Mob)$$1).getNavigation();
            Path $$9 = $$8.createPath($$5, 0, 8);
            if ($$9 != null && $$9.canReach()) continue;
            this.chosenJump = $$7;
            this.prepareJumpStart = $$2;
            return;
        }
    }

    protected Optional<PossibleJump> getJumpCandidate(ServerLevel $$0) {
        Optional<PossibleJump> $$1 = WeightedRandom.getRandomItem($$0.random, this.jumpCandidates);
        $$1.ifPresent(arg_0 -> this.jumpCandidates.remove(arg_0));
        return $$1;
    }

    private boolean isAcceptableLandingPosition(ServerLevel $$0, E $$1, BlockPos $$2) {
        BlockPos $$3 = ((Entity)$$1).blockPosition();
        int $$4 = $$3.getX();
        int $$5 = $$3.getZ();
        if ($$4 == $$2.getX() && $$5 == $$2.getZ()) {
            return false;
        }
        return this.acceptableLandingSpot.test($$1, (Object)$$2);
    }

    @Nullable
    protected Vec3 calculateOptimalJumpVector(Mob $$0, Vec3 $$1) {
        ArrayList $$2 = Lists.newArrayList(ALLOWED_ANGLES);
        Collections.shuffle((List)$$2);
        Iterator iterator = $$2.iterator();
        while (iterator.hasNext()) {
            int $$3 = (Integer)iterator.next();
            Vec3 $$4 = this.calculateJumpVectorForAngle($$0, $$1, $$3);
            if ($$4 == null) continue;
            return $$4;
        }
        return null;
    }

    @Nullable
    private Vec3 calculateJumpVectorForAngle(Mob $$0, Vec3 $$1, int $$2) {
        Vec3 $$3 = $$0.position();
        Vec3 $$4 = new Vec3($$1.x - $$3.x, 0.0, $$1.z - $$3.z).normalize().scale(0.5);
        $$1 = $$1.subtract($$4);
        Vec3 $$5 = $$1.subtract($$3);
        float $$6 = (float)$$2 * (float)Math.PI / 180.0f;
        double $$7 = Math.atan2((double)$$5.z, (double)$$5.x);
        double $$8 = $$5.subtract(0.0, $$5.y, 0.0).lengthSqr();
        double $$9 = Math.sqrt((double)$$8);
        double $$10 = $$5.y;
        double $$11 = Math.sin((double)(2.0f * $$6));
        double $$12 = 0.08;
        double $$13 = Math.pow((double)Math.cos((double)$$6), (double)2.0);
        double $$14 = Math.sin((double)$$6);
        double $$15 = Math.cos((double)$$6);
        double $$16 = Math.sin((double)$$7);
        double $$17 = Math.cos((double)$$7);
        double $$18 = $$8 * 0.08 / ($$9 * $$11 - 2.0 * $$10 * $$13);
        if ($$18 < 0.0) {
            return null;
        }
        double $$19 = Math.sqrt((double)$$18);
        if ($$19 > (double)this.maxJumpVelocity) {
            return null;
        }
        double $$20 = $$19 * $$15;
        double $$21 = $$19 * $$14;
        int $$22 = Mth.ceil($$9 / $$20) * 2;
        double $$23 = 0.0;
        Vec3 $$24 = null;
        EntityDimensions $$25 = $$0.getDimensions(Pose.LONG_JUMPING);
        for (int $$26 = 0; $$26 < $$22 - 1; ++$$26) {
            double $$27 = $$14 / $$15 * ($$23 += $$9 / (double)$$22) - Math.pow((double)$$23, (double)2.0) * 0.08 / (2.0 * $$18 * Math.pow((double)$$15, (double)2.0));
            double $$28 = $$23 * $$17;
            double $$29 = $$23 * $$16;
            Vec3 $$30 = new Vec3($$3.x + $$28, $$3.y + $$27, $$3.z + $$29);
            if ($$24 != null && !this.isClearTransition($$0, $$25, $$24, $$30)) {
                return null;
            }
            $$24 = $$30;
        }
        return new Vec3($$20 * $$17, $$21, $$20 * $$16).scale(0.95f);
    }

    private boolean isClearTransition(Mob $$0, EntityDimensions $$1, Vec3 $$2, Vec3 $$3) {
        Vec3 $$4 = $$3.subtract($$2);
        double $$5 = Math.min((float)$$1.width, (float)$$1.height);
        int $$6 = Mth.ceil($$4.length() / $$5);
        Vec3 $$7 = $$4.normalize();
        Vec3 $$8 = $$2;
        for (int $$9 = 0; $$9 < $$6; ++$$9) {
            Vec3 vec3 = $$8 = $$9 == $$6 - 1 ? $$3 : $$8.add($$7.scale($$5 * (double)0.9f));
            if ($$0.level.noCollision($$0, $$1.makeBoundingBox($$8))) continue;
            return false;
        }
        return true;
    }

    public static class PossibleJump
    extends WeightedEntry.IntrusiveBase {
        private final BlockPos jumpTarget;

        public PossibleJump(BlockPos $$0, int $$1) {
            super($$1);
            this.jumpTarget = $$0;
        }

        public BlockPos getJumpTarget() {
            return this.jumpTarget;
        }
    }
}