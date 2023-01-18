/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.commons.lang3.tuple.Pair;

public class MushroomCow
extends Cow
implements Shearable,
VariantHolder<MushroomType> {
    private static final EntityDataAccessor<String> DATA_TYPE = SynchedEntityData.defineId(MushroomCow.class, EntityDataSerializers.STRING);
    private static final int MUTATE_CHANCE = 1024;
    @Nullable
    private MobEffect effect;
    private int effectDuration;
    @Nullable
    private UUID lastLightningBoltUUID;

    public MushroomCow(EntityType<? extends MushroomCow> $$0, Level $$1) {
        super((EntityType<? extends Cow>)$$0, $$1);
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if ($$1.getBlockState((BlockPos)$$0.below()).is(Blocks.MYCELIUM)) {
            return 10.0f;
        }
        return $$1.getPathfindingCostFromLightLevels($$0);
    }

    public static boolean checkMushroomSpawnRules(EntityType<MushroomCow> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        return $$1.getBlockState((BlockPos)$$3.below()).is(BlockTags.MOOSHROOMS_SPAWNABLE_ON) && MushroomCow.isBrightEnoughToSpawn($$1, $$3);
    }

    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
        UUID $$2 = $$1.getUUID();
        if (!$$2.equals((Object)this.lastLightningBoltUUID)) {
            this.setVariant(this.getVariant() == MushroomType.RED ? MushroomType.BROWN : MushroomType.RED);
            this.lastLightningBoltUUID = $$2;
            this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0f, 1.0f);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE, MushroomType.RED.type);
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$12) {
        ItemStack $$2 = $$0.getItemInHand($$12);
        if ($$2.is(Items.BOWL) && !this.isBaby()) {
            SoundEvent $$8;
            ItemStack $$5;
            boolean $$3 = false;
            if (this.effect != null) {
                $$3 = true;
                ItemStack $$4 = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.saveMobEffect($$4, this.effect, this.effectDuration);
                this.effect = null;
                this.effectDuration = 0;
            } else {
                $$5 = new ItemStack(Items.MUSHROOM_STEW);
            }
            ItemStack $$6 = ItemUtils.createFilledResult($$2, $$0, $$5, false);
            $$0.setItemInHand($$12, $$6);
            if ($$3) {
                SoundEvent $$7 = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
            } else {
                $$8 = SoundEvents.MOOSHROOM_MILK;
            }
            this.playSound($$8, 1.0f, 1.0f);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if ($$2.is(Items.SHEARS) && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, (Entity)((Object)$$0));
            if (!this.level.isClientSide) {
                $$2.hurtAndBreak(1, $$0, $$1 -> $$1.broadcastBreakEvent($$12));
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (this.getVariant() == MushroomType.BROWN && $$2.is(ItemTags.SMALL_FLOWERS)) {
            if (this.effect != null) {
                for (int $$9 = 0; $$9 < 2; ++$$9) {
                    this.level.addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextDouble() / 2.0, this.getY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }
            } else {
                Optional<Pair<MobEffect, Integer>> $$10 = this.getEffectFromItemStack($$2);
                if (!$$10.isPresent()) {
                    return InteractionResult.PASS;
                }
                Pair $$11 = (Pair)$$10.get();
                if (!$$0.getAbilities().instabuild) {
                    $$2.shrink(1);
                }
                for (int $$122 = 0; $$122 < 4; ++$$122) {
                    this.level.addParticle(ParticleTypes.EFFECT, this.getX() + this.random.nextDouble() / 2.0, this.getY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }
                this.effect = (MobEffect)$$11.getLeft();
                this.effectDuration = (Integer)$$11.getRight();
                this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract($$0, $$12);
    }

    @Override
    public void shear(SoundSource $$0) {
        Cow $$1;
        this.level.playSound(null, (Entity)((Object)this), SoundEvents.MOOSHROOM_SHEAR, $$0, 1.0f, 1.0f);
        if (!this.level.isClientSide() && ($$1 = EntityType.COW.create(this.level)) != null) {
            ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            this.discard();
            $$1.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            $$1.setHealth(this.getHealth());
            $$1.yBodyRot = this.yBodyRot;
            if (this.hasCustomName()) {
                $$1.setCustomName(this.getCustomName());
                $$1.setCustomNameVisible(this.isCustomNameVisible());
            }
            if (this.isPersistenceRequired()) {
                $$1.setPersistenceRequired();
            }
            $$1.setInvulnerable(this.isInvulnerable());
            this.level.addFreshEntity((Entity)((Object)$$1));
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(1.0), this.getZ(), new ItemStack(this.getVariant().blockState.getBlock())));
            }
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putString("Type", this.getVariant().getSerializedName());
        if (this.effect != null) {
            $$0.putInt("EffectId", MobEffect.getId(this.effect));
            $$0.putInt("EffectDuration", this.effectDuration);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.setVariant(MushroomType.byType($$0.getString("Type")));
        if ($$0.contains("EffectId", 1)) {
            this.effect = MobEffect.byId($$0.getInt("EffectId"));
        }
        if ($$0.contains("EffectDuration", 3)) {
            this.effectDuration = $$0.getInt("EffectDuration");
        }
    }

    private Optional<Pair<MobEffect, Integer>> getEffectFromItemStack(ItemStack $$0) {
        SuspiciousEffectHolder $$1 = SuspiciousEffectHolder.tryGet($$0.getItem());
        if ($$1 != null) {
            return Optional.of((Object)Pair.of((Object)$$1.getSuspiciousEffect(), (Object)$$1.getEffectDuration()));
        }
        return Optional.empty();
    }

    @Override
    public void setVariant(MushroomType $$0) {
        this.entityData.set(DATA_TYPE, $$0.type);
    }

    @Override
    public MushroomType getVariant() {
        return MushroomType.byType(this.entityData.get(DATA_TYPE));
    }

    @Override
    @Nullable
    public MushroomCow getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        MushroomCow $$2 = EntityType.MOOSHROOM.create($$0);
        if ($$2 != null) {
            $$2.setVariant(this.getOffspringType((MushroomCow)$$1));
        }
        return $$2;
    }

    private MushroomType getOffspringType(MushroomCow $$0) {
        MushroomType $$4;
        MushroomType $$2;
        MushroomType $$1 = this.getVariant();
        if ($$1 == ($$2 = $$0.getVariant()) && this.random.nextInt(1024) == 0) {
            MushroomType $$3 = $$1 == MushroomType.BROWN ? MushroomType.RED : MushroomType.BROWN;
        } else {
            $$4 = this.random.nextBoolean() ? $$1 : $$2;
        }
        return $$4;
    }

    public static enum MushroomType implements StringRepresentable
    {
        RED("red", Blocks.RED_MUSHROOM.defaultBlockState()),
        BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());

        public static final StringRepresentable.EnumCodec<MushroomType> CODEC;
        final String type;
        final BlockState blockState;

        private MushroomType(String $$0, BlockState $$1) {
            this.type = $$0;
            this.blockState = $$1;
        }

        public BlockState getBlockState() {
            return this.blockState;
        }

        @Override
        public String getSerializedName() {
            return this.type;
        }

        static MushroomType byType(String $$0) {
            return CODEC.byName($$0, RED);
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)MushroomType::values));
        }
    }
}