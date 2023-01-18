/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Boolean
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.UUID
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.monster;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class ZombieVillager
extends Zombie
implements VillagerDataHolder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
    private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
    private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
    private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
    private static final int SPECIAL_BLOCK_RADIUS = 4;
    private int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;
    @Nullable
    private Tag gossips;
    @Nullable
    private CompoundTag tradeOffers;
    private int villagerXp;

    public ZombieVillager(EntityType<? extends ZombieVillager> $$02, Level $$1) {
        super((EntityType<? extends Zombie>)$$02, $$1);
        BuiltInRegistries.VILLAGER_PROFESSION.getRandom(this.random).ifPresent($$0 -> this.setVillagerData(this.getVillagerData().setProfession((VillagerProfession)((Object)((Object)$$0.value())))));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERTING_ID, false);
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        VillagerData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.getVillagerData()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("VillagerData", (Tag)$$1));
        if (this.tradeOffers != null) {
            $$0.put("Offers", this.tradeOffers);
        }
        if (this.gossips != null) {
            $$0.put("Gossips", this.gossips);
        }
        $$0.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            $$0.putUUID("ConversionPlayer", this.conversionStarter);
        }
        $$0.putInt("Xp", this.villagerXp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        if ($$0.contains("VillagerData", 10)) {
            DataResult $$1 = VillagerData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$0.get("VillagerData")));
            $$1.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(this::setVillagerData);
        }
        if ($$0.contains("Offers", 10)) {
            this.tradeOffers = $$0.getCompound("Offers");
        }
        if ($$0.contains("Gossips", 9)) {
            this.gossips = $$0.getList("Gossips", 10);
        }
        if ($$0.contains("ConversionTime", 99) && $$0.getInt("ConversionTime") > -1) {
            this.startConverting($$0.hasUUID("ConversionPlayer") ? $$0.getUUID("ConversionPlayer") : null, $$0.getInt("ConversionTime"));
        }
        if ($$0.contains("Xp", 3)) {
            this.villagerXp = $$0.getInt("Xp");
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
            int $$0 = this.getConversionProgress();
            this.villagerConversionTime -= $$0;
            if (this.villagerConversionTime <= 0) {
                this.finishConversion((ServerLevel)this.level);
            }
        }
        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if ($$2.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!$$0.getAbilities().instabuild) {
                    $$2.shrink(1);
                }
                if (!this.level.isClientSide) {
                    this.startConverting($$0.getUUID(), this.random.nextInt(2401) + 3600);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract($$0, $$1);
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return !this.isConverting() && this.villagerXp == 0;
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID $$0, int $$1) {
        this.conversionStarter = $$0;
        this.villagerConversionTime = $$1;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, $$1, Math.min((int)(this.level.getDifficulty().getId() - 1), (int)0)));
        this.level.broadcastEntityEvent(this, (byte)16);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 16) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            return;
        }
        super.handleEntityEvent($$0);
    }

    private void finishConversion(ServerLevel $$0) {
        Player $$5;
        Villager $$1 = this.convertTo(EntityType.VILLAGER, false);
        for (EquipmentSlot $$2 : EquipmentSlot.values()) {
            ItemStack $$3 = this.getItemBySlot($$2);
            if ($$3.isEmpty()) continue;
            if (EnchantmentHelper.hasBindingCurse($$3)) {
                $$1.getSlot($$2.getIndex() + 300).set($$3);
                continue;
            }
            double $$4 = this.getEquipmentDropChance($$2);
            if (!($$4 > 1.0)) continue;
            this.spawnAtLocation($$3);
        }
        $$1.setVillagerData(this.getVillagerData());
        if (this.gossips != null) {
            $$1.setGossips(this.gossips);
        }
        if (this.tradeOffers != null) {
            $$1.setOffers(new MerchantOffers(this.tradeOffers));
        }
        $$1.setVillagerXp(this.villagerXp);
        $$1.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$1.blockPosition()), MobSpawnType.CONVERSION, null, null);
        $$1.refreshBrain($$0);
        if (this.conversionStarter != null && ($$5 = $$0.getPlayerByUUID(this.conversionStarter)) instanceof ServerPlayer) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)$$5, this, $$1);
            $$0.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, $$5, $$1);
        }
        $$1.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            $$0.levelEvent(null, 1027, this.blockPosition(), 0);
        }
    }

    private int getConversionProgress() {
        int $$0 = 1;
        if (this.random.nextFloat() < 0.01f) {
            int $$1 = 0;
            BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos();
            for (int $$3 = (int)this.getX() - 4; $$3 < (int)this.getX() + 4 && $$1 < 14; ++$$3) {
                for (int $$4 = (int)this.getY() - 4; $$4 < (int)this.getY() + 4 && $$1 < 14; ++$$4) {
                    for (int $$5 = (int)this.getZ() - 4; $$5 < (int)this.getZ() + 4 && $$1 < 14; ++$$5) {
                        BlockState $$6 = this.level.getBlockState($$2.set($$3, $$4, $$5));
                        if (!$$6.is(Blocks.IRON_BARS) && !($$6.getBlock() instanceof BedBlock)) continue;
                        if (this.random.nextFloat() < 0.3f) {
                            ++$$0;
                        }
                        ++$$1;
                    }
                }
            }
        }
        return $$0;
    }

    @Override
    public float getVoicePitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 2.0f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    public void setTradeOffers(CompoundTag $$0) {
        this.tradeOffers = $$0;
    }

    public void setGossips(Tag $$0) {
        this.gossips = $$0;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3, @Nullable CompoundTag $$4) {
        this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome($$0.getBiome(this.blockPosition()))));
        return super.finalizeSpawn($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void setVillagerData(VillagerData $$0) {
        VillagerData $$1 = this.getVillagerData();
        if ($$1.getProfession() != $$0.getProfession()) {
            this.tradeOffers = null;
        }
        this.entityData.set(DATA_VILLAGER_DATA, $$0);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int $$0) {
        this.villagerXp = $$0;
    }
}