/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Explosion {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
    private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
    private final boolean fire;
    private final BlockInteraction blockInteraction;
    private final RandomSource random = RandomSource.create();
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity source;
    private final float radius;
    private final DamageSource damageSource;
    private final ExplosionDamageCalculator damageCalculator;
    private final ObjectArrayList<BlockPos> toBlow = new ObjectArrayList();
    private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();

    public Explosion(Level $$0, @Nullable Entity $$1, double $$2, double $$3, double $$4, float $$5, List<BlockPos> $$6) {
        this($$0, $$1, $$2, $$3, $$4, $$5, false, BlockInteraction.DESTROY_WITH_DECAY, $$6);
    }

    public Explosion(Level $$0, @Nullable Entity $$1, double $$2, double $$3, double $$4, float $$5, boolean $$6, BlockInteraction $$7, List<BlockPos> $$8) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        this.toBlow.addAll($$8);
    }

    public Explosion(Level $$0, @Nullable Entity $$1, double $$2, double $$3, double $$4, float $$5, boolean $$6, BlockInteraction $$7) {
        this($$0, $$1, null, null, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public Explosion(Level $$0, @Nullable Entity $$1, @Nullable DamageSource $$2, @Nullable ExplosionDamageCalculator $$3, double $$4, double $$5, double $$6, float $$7, boolean $$8, BlockInteraction $$9) {
        this.level = $$0;
        this.source = $$1;
        this.radius = $$7;
        this.x = $$4;
        this.y = $$5;
        this.z = $$6;
        this.fire = $$8;
        this.blockInteraction = $$9;
        this.damageSource = $$2 == null ? DamageSource.explosion(this) : $$2;
        this.damageCalculator = $$3 == null ? this.makeDamageCalculator($$1) : $$3;
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity $$0) {
        return $$0 == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator($$0);
    }

    public static float getSeenPercent(Vec3 $$0, Entity $$1) {
        AABB $$2 = $$1.getBoundingBox();
        double $$3 = 1.0 / (($$2.maxX - $$2.minX) * 2.0 + 1.0);
        double $$4 = 1.0 / (($$2.maxY - $$2.minY) * 2.0 + 1.0);
        double $$5 = 1.0 / (($$2.maxZ - $$2.minZ) * 2.0 + 1.0);
        double $$6 = (1.0 - Math.floor((double)(1.0 / $$3)) * $$3) / 2.0;
        double $$7 = (1.0 - Math.floor((double)(1.0 / $$5)) * $$5) / 2.0;
        if ($$3 < 0.0 || $$4 < 0.0 || $$5 < 0.0) {
            return 0.0f;
        }
        int $$8 = 0;
        int $$9 = 0;
        for (double $$10 = 0.0; $$10 <= 1.0; $$10 += $$3) {
            for (double $$11 = 0.0; $$11 <= 1.0; $$11 += $$4) {
                for (double $$12 = 0.0; $$12 <= 1.0; $$12 += $$5) {
                    double $$15;
                    double $$14;
                    double $$13 = Mth.lerp($$10, $$2.minX, $$2.maxX);
                    Vec3 $$16 = new Vec3($$13 + $$6, $$14 = Mth.lerp($$11, $$2.minY, $$2.maxY), ($$15 = Mth.lerp($$12, $$2.minZ, $$2.maxZ)) + $$7);
                    if ($$1.level.clip(new ClipContext($$16, $$0, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, $$1)).getType() == HitResult.Type.MISS) {
                        ++$$8;
                    }
                    ++$$9;
                }
            }
        }
        return (float)$$8 / (float)$$9;
    }

    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        HashSet $$0 = Sets.newHashSet();
        int $$1 = 16;
        for (int $$2 = 0; $$2 < 16; ++$$2) {
            for (int $$3 = 0; $$3 < 16; ++$$3) {
                block2: for (int $$4 = 0; $$4 < 16; ++$$4) {
                    if ($$2 != 0 && $$2 != 15 && $$3 != 0 && $$3 != 15 && $$4 != 0 && $$4 != 15) continue;
                    double $$5 = (float)$$2 / 15.0f * 2.0f - 1.0f;
                    double $$6 = (float)$$3 / 15.0f * 2.0f - 1.0f;
                    double $$7 = (float)$$4 / 15.0f * 2.0f - 1.0f;
                    double $$8 = Math.sqrt((double)($$5 * $$5 + $$6 * $$6 + $$7 * $$7));
                    $$5 /= $$8;
                    $$6 /= $$8;
                    $$7 /= $$8;
                    double $$10 = this.x;
                    double $$11 = this.y;
                    double $$12 = this.z;
                    float $$13 = 0.3f;
                    for (float $$9 = this.radius * (0.7f + this.level.random.nextFloat() * 0.6f); $$9 > 0.0f; $$9 -= 0.22500001f) {
                        BlockPos $$14 = new BlockPos($$10, $$11, $$12);
                        BlockState $$15 = this.level.getBlockState($$14);
                        FluidState $$16 = this.level.getFluidState($$14);
                        if (!this.level.isInWorldBounds($$14)) continue block2;
                        Optional<Float> $$17 = this.damageCalculator.getBlockExplosionResistance(this, this.level, $$14, $$15, $$16);
                        if ($$17.isPresent()) {
                            $$9 -= (((Float)$$17.get()).floatValue() + 0.3f) * 0.3f;
                        }
                        if ($$9 > 0.0f && this.damageCalculator.shouldBlockExplode(this, this.level, $$14, $$15, $$9)) {
                            $$0.add((Object)$$14);
                        }
                        $$10 += $$5 * (double)0.3f;
                        $$11 += $$6 * (double)0.3f;
                        $$12 += $$7 * (double)0.3f;
                    }
                }
            }
        }
        this.toBlow.addAll((Collection)$$0);
        float $$18 = this.radius * 2.0f;
        int $$19 = Mth.floor(this.x - (double)$$18 - 1.0);
        int $$20 = Mth.floor(this.x + (double)$$18 + 1.0);
        int $$21 = Mth.floor(this.y - (double)$$18 - 1.0);
        int $$22 = Mth.floor(this.y + (double)$$18 + 1.0);
        int $$23 = Mth.floor(this.z - (double)$$18 - 1.0);
        int $$24 = Mth.floor(this.z + (double)$$18 + 1.0);
        List $$25 = this.level.getEntities(this.source, new AABB($$19, $$21, $$23, $$20, $$22, $$24));
        Vec3 $$26 = new Vec3(this.x, this.y, this.z);
        for (int $$27 = 0; $$27 < $$25.size(); ++$$27) {
            Player $$37;
            double $$32;
            double $$31;
            double $$30;
            double $$33;
            double $$29;
            Entity $$28 = (Entity)$$25.get($$27);
            if ($$28.ignoreExplosion() || !(($$29 = Math.sqrt((double)$$28.distanceToSqr($$26)) / (double)$$18) <= 1.0) || ($$33 = Math.sqrt((double)(($$30 = $$28.getX() - this.x) * $$30 + ($$31 = ($$28 instanceof PrimedTnt ? $$28.getY() : $$28.getEyeY()) - this.y) * $$31 + ($$32 = $$28.getZ() - this.z) * $$32))) == 0.0) continue;
            $$30 /= $$33;
            $$31 /= $$33;
            $$32 /= $$33;
            double $$34 = Explosion.getSeenPercent($$26, $$28);
            double $$35 = (1.0 - $$29) * $$34;
            $$28.hurt(this.getDamageSource(), (int)(($$35 * $$35 + $$35) / 2.0 * 7.0 * (double)$$18 + 1.0));
            double $$36 = $$35;
            if ($$28 instanceof LivingEntity) {
                $$36 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)$$28, $$35);
            }
            $$28.setDeltaMovement($$28.getDeltaMovement().add($$30 * $$36, $$31 * $$36, $$32 * $$36));
            if (!($$28 instanceof Player) || ($$37 = (Player)$$28).isSpectator() || $$37.isCreative() && $$37.getAbilities().flying) continue;
            this.hitPlayers.put((Object)$$37, (Object)new Vec3($$30 * $$35, $$31 * $$35, $$32 * $$35));
        }
    }

    public void finalizeExplosion(boolean $$0) {
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f, false);
        }
        boolean $$1 = this.interactsWithBlocks();
        if ($$0) {
            if (this.radius < 2.0f || !$$1) {
                this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            } else {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            }
        }
        if ($$1) {
            ObjectArrayList $$22 = new ObjectArrayList();
            boolean $$3 = this.getIndirectSourceEntity() instanceof Player;
            Util.shuffle(this.toBlow, this.level.random);
            for (BlockPos $$4 : this.toBlow) {
                Level level;
                BlockState $$5 = this.level.getBlockState($$4);
                Block $$6 = $$5.getBlock();
                if ($$5.isAir()) continue;
                BlockPos $$7 = $$4.immutable();
                this.level.getProfiler().push("explosion_blocks");
                if ($$6.dropFromExplosion(this) && (level = this.level) instanceof ServerLevel) {
                    ServerLevel $$8 = (ServerLevel)level;
                    BlockEntity $$9 = $$5.hasBlockEntity() ? this.level.getBlockEntity($$4) : null;
                    LootContext.Builder $$10 = new LootContext.Builder($$8).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf($$4)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, $$9).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
                    if (this.blockInteraction == BlockInteraction.DESTROY_WITH_DECAY) {
                        $$10.withParameter(LootContextParams.EXPLOSION_RADIUS, Float.valueOf((float)this.radius));
                    }
                    $$5.spawnAfterBreak($$8, $$4, ItemStack.EMPTY, $$3);
                    $$5.getDrops($$10).forEach($$2 -> Explosion.addBlockDrops((ObjectArrayList<Pair<ItemStack, BlockPos>>)$$22, $$2, $$7));
                }
                this.level.setBlock($$4, Blocks.AIR.defaultBlockState(), 3);
                $$6.wasExploded(this.level, $$4, this);
                this.level.getProfiler().pop();
            }
            for (Pair $$11 : $$22) {
                Block.popResource(this.level, (BlockPos)$$11.getSecond(), (ItemStack)$$11.getFirst());
            }
        }
        if (this.fire) {
            for (BlockPos $$12 : this.toBlow) {
                if (this.random.nextInt(3) != 0 || !this.level.getBlockState($$12).isAir() || !this.level.getBlockState((BlockPos)$$12.below()).isSolidRender(this.level, (BlockPos)$$12.below())) continue;
                this.level.setBlockAndUpdate($$12, BaseFireBlock.getState(this.level, $$12));
            }
        }
    }

    public boolean interactsWithBlocks() {
        return this.blockInteraction != BlockInteraction.KEEP;
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> $$0, ItemStack $$1, BlockPos $$2) {
        int $$3 = $$0.size();
        for (int $$4 = 0; $$4 < $$3; ++$$4) {
            Pair $$5 = (Pair)$$0.get($$4);
            ItemStack $$6 = (ItemStack)$$5.getFirst();
            if (!ItemEntity.areMergable($$6, $$1)) continue;
            ItemStack $$7 = ItemEntity.merge($$6, $$1, 16);
            $$0.set($$4, (Object)Pair.of((Object)$$7, (Object)((BlockPos)$$5.getSecond())));
            if (!$$1.isEmpty()) continue;
            return;
        }
        $$0.add((Object)Pair.of((Object)$$1, (Object)$$2));
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers;
    }

    @Nullable
    public LivingEntity getIndirectSourceEntity() {
        Projectile $$2;
        Entity $$3;
        if (this.source == null) {
            return null;
        }
        Entity entity = this.source;
        if (entity instanceof PrimedTnt) {
            PrimedTnt $$0 = (PrimedTnt)entity;
            return $$0.getOwner();
        }
        entity = this.source;
        if (entity instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)entity;
            return $$1;
        }
        entity = this.source;
        if (entity instanceof Projectile && ($$3 = ($$2 = (Projectile)entity).getOwner()) instanceof LivingEntity) {
            LivingEntity $$4 = (LivingEntity)$$3;
            return $$4;
        }
        return null;
    }

    @Nullable
    public Entity getDirectSourceEntity() {
        return this.source;
    }

    public void clearToBlow() {
        this.toBlow.clear();
    }

    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }

    public static enum BlockInteraction {
        KEEP,
        DESTROY,
        DESTROY_WITH_DECAY;

    }
}