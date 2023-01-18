/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class GiveGiftToHero
extends Behavior<Villager> {
    private static final int THROW_GIFT_AT_DISTANCE = 5;
    private static final int MIN_TIME_BETWEEN_GIFTS = 600;
    private static final int MAX_TIME_BETWEEN_GIFTS = 6600;
    private static final int TIME_TO_DELAY_FOR_HEAD_TO_FINISH_TURNING = 20;
    private static final Map<VillagerProfession, ResourceLocation> GIFTS = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)VillagerProfession.ARMORER, (Object)BuiltInLootTables.ARMORER_GIFT);
        $$0.put((Object)VillagerProfession.BUTCHER, (Object)BuiltInLootTables.BUTCHER_GIFT);
        $$0.put((Object)VillagerProfession.CARTOGRAPHER, (Object)BuiltInLootTables.CARTOGRAPHER_GIFT);
        $$0.put((Object)VillagerProfession.CLERIC, (Object)BuiltInLootTables.CLERIC_GIFT);
        $$0.put((Object)VillagerProfession.FARMER, (Object)BuiltInLootTables.FARMER_GIFT);
        $$0.put((Object)VillagerProfession.FISHERMAN, (Object)BuiltInLootTables.FISHERMAN_GIFT);
        $$0.put((Object)VillagerProfession.FLETCHER, (Object)BuiltInLootTables.FLETCHER_GIFT);
        $$0.put((Object)VillagerProfession.LEATHERWORKER, (Object)BuiltInLootTables.LEATHERWORKER_GIFT);
        $$0.put((Object)VillagerProfession.LIBRARIAN, (Object)BuiltInLootTables.LIBRARIAN_GIFT);
        $$0.put((Object)VillagerProfession.MASON, (Object)BuiltInLootTables.MASON_GIFT);
        $$0.put((Object)VillagerProfession.SHEPHERD, (Object)BuiltInLootTables.SHEPHERD_GIFT);
        $$0.put((Object)VillagerProfession.TOOLSMITH, (Object)BuiltInLootTables.TOOLSMITH_GIFT);
        $$0.put((Object)VillagerProfession.WEAPONSMITH, (Object)BuiltInLootTables.WEAPONSMITH_GIFT);
    });
    private static final float SPEED_MODIFIER = 0.5f;
    private int timeUntilNextGift = 600;
    private boolean giftGivenDuringThisRun;
    private long timeSinceStart;

    public GiveGiftToHero(int $$0) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.NEAREST_VISIBLE_PLAYER, (Object)((Object)MemoryStatus.VALUE_PRESENT)), $$0);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Villager $$1) {
        if (!this.isHeroVisible($$1)) {
            return false;
        }
        if (this.timeUntilNextGift > 0) {
            --this.timeUntilNextGift;
            return false;
        }
        return true;
    }

    @Override
    protected void start(ServerLevel $$0, Villager $$1, long $$2) {
        this.giftGivenDuringThisRun = false;
        this.timeSinceStart = $$2;
        Player $$3 = (Player)this.getNearestTargetableHero($$1).get();
        $$1.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, $$3);
        BehaviorUtils.lookAtEntity($$1, $$3);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Villager $$1, long $$2) {
        return this.isHeroVisible($$1) && !this.giftGivenDuringThisRun;
    }

    @Override
    protected void tick(ServerLevel $$0, Villager $$1, long $$2) {
        Player $$3 = (Player)this.getNearestTargetableHero($$1).get();
        BehaviorUtils.lookAtEntity($$1, $$3);
        if (this.isWithinThrowingDistance($$1, $$3)) {
            if ($$2 - this.timeSinceStart > 20L) {
                this.throwGift($$1, $$3);
                this.giftGivenDuringThisRun = true;
            }
        } else {
            BehaviorUtils.setWalkAndLookTargetMemories((LivingEntity)$$1, $$3, 0.5f, 5);
        }
    }

    @Override
    protected void start(ServerLevel $$0, Villager $$1, long $$2) {
        this.timeUntilNextGift = GiveGiftToHero.calculateTimeUntilNextGift($$0);
        $$1.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        $$1.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        $$1.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    private void throwGift(Villager $$0, LivingEntity $$1) {
        List<ItemStack> $$2 = this.getItemToThrow($$0);
        for (ItemStack $$3 : $$2) {
            BehaviorUtils.throwItem($$0, $$3, $$1.position());
        }
    }

    private List<ItemStack> getItemToThrow(Villager $$0) {
        if ($$0.isBaby()) {
            return ImmutableList.of((Object)new ItemStack(Items.POPPY));
        }
        VillagerProfession $$1 = $$0.getVillagerData().getProfession();
        if (GIFTS.containsKey((Object)$$1)) {
            LootTable $$2 = $$0.level.getServer().getLootTables().get((ResourceLocation)GIFTS.get((Object)$$1));
            LootContext.Builder $$3 = new LootContext.Builder((ServerLevel)$$0.level).withParameter(LootContextParams.ORIGIN, $$0.position()).withParameter(LootContextParams.THIS_ENTITY, $$0).withRandom($$0.getRandom());
            return $$2.getRandomItems($$3.create(LootContextParamSets.GIFT));
        }
        return ImmutableList.of((Object)new ItemStack(Items.WHEAT_SEEDS));
    }

    private boolean isHeroVisible(Villager $$0) {
        return this.getNearestTargetableHero($$0).isPresent();
    }

    private Optional<Player> getNearestTargetableHero(Villager $$0) {
        return $$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
    }

    private boolean isHero(Player $$0) {
        return $$0.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
    }

    private boolean isWithinThrowingDistance(Villager $$0, Player $$1) {
        BlockPos $$2 = $$1.blockPosition();
        BlockPos $$3 = $$0.blockPosition();
        return $$3.closerThan($$2, 5.0);
    }

    private static int calculateTimeUntilNextGift(ServerLevel $$0) {
        return 600 + $$0.random.nextInt(6001);
    }
}