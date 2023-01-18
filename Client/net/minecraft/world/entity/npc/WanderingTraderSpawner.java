/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.npc;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;

public class WanderingTraderSpawner
implements CustomSpawner {
    private static final int DEFAULT_TICK_DELAY = 1200;
    public static final int DEFAULT_SPAWN_DELAY = 24000;
    private static final int MIN_SPAWN_CHANCE = 25;
    private static final int MAX_SPAWN_CHANCE = 75;
    private static final int SPAWN_CHANCE_INCREASE = 25;
    private static final int SPAWN_ONE_IN_X_CHANCE = 10;
    private static final int NUMBER_OF_SPAWN_ATTEMPTS = 10;
    private final RandomSource random = RandomSource.create();
    private final ServerLevelData serverLevelData;
    private int tickDelay;
    private int spawnDelay;
    private int spawnChance;

    public WanderingTraderSpawner(ServerLevelData $$0) {
        this.serverLevelData = $$0;
        this.tickDelay = 1200;
        this.spawnDelay = $$0.getWanderingTraderSpawnDelay();
        this.spawnChance = $$0.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            $$0.setWanderingTraderSpawnDelay(this.spawnDelay);
            this.spawnChance = 25;
            $$0.setWanderingTraderSpawnChance(this.spawnChance);
        }
    }

    @Override
    public int tick(ServerLevel $$0, boolean $$1, boolean $$2) {
        if (!$$0.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
            return 0;
        }
        if (--this.tickDelay > 0) {
            return 0;
        }
        this.tickDelay = 1200;
        this.spawnDelay -= 1200;
        this.serverLevelData.setWanderingTraderSpawnDelay(this.spawnDelay);
        if (this.spawnDelay > 0) {
            return 0;
        }
        this.spawnDelay = 24000;
        if (!$$0.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return 0;
        }
        int $$3 = this.spawnChance;
        this.spawnChance = Mth.clamp(this.spawnChance + 25, 25, 75);
        this.serverLevelData.setWanderingTraderSpawnChance(this.spawnChance);
        if (this.random.nextInt(100) > $$3) {
            return 0;
        }
        if (this.spawn($$0)) {
            this.spawnChance = 25;
            return 1;
        }
        return 0;
    }

    private boolean spawn(ServerLevel $$02) {
        ServerPlayer $$1 = $$02.getRandomPlayer();
        if ($$1 == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        BlockPos $$2 = $$1.blockPosition();
        int $$3 = 48;
        PoiManager $$4 = $$02.getPoiManager();
        Optional<BlockPos> $$5 = $$4.find((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypes.MEETING)), (Predicate<BlockPos>)((Predicate)$$0 -> true), $$2, 48, PoiManager.Occupancy.ANY);
        BlockPos $$6 = (BlockPos)$$5.orElse((Object)$$2);
        BlockPos $$7 = this.findSpawnPositionNear($$02, $$6, 48);
        if ($$7 != null && this.hasEnoughSpace($$02, $$7)) {
            if ($$02.getBiome($$7).is(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                return false;
            }
            WanderingTrader $$8 = EntityType.WANDERING_TRADER.spawn($$02, $$7, MobSpawnType.EVENT);
            if ($$8 != null) {
                for (int $$9 = 0; $$9 < 2; ++$$9) {
                    this.tryToSpawnLlamaFor($$02, $$8, 4);
                }
                this.serverLevelData.setWanderingTraderId($$8.getUUID());
                $$8.setDespawnDelay(48000);
                $$8.setWanderTarget($$6);
                $$8.restrictTo($$6, 16);
                return true;
            }
        }
        return false;
    }

    private void tryToSpawnLlamaFor(ServerLevel $$0, WanderingTrader $$1, int $$2) {
        BlockPos $$3 = this.findSpawnPositionNear($$0, $$1.blockPosition(), $$2);
        if ($$3 == null) {
            return;
        }
        TraderLlama $$4 = EntityType.TRADER_LLAMA.spawn($$0, $$3, MobSpawnType.EVENT);
        if ($$4 == null) {
            return;
        }
        $$4.setLeashedTo($$1, true);
    }

    @Nullable
    private BlockPos findSpawnPositionNear(LevelReader $$0, BlockPos $$1, int $$2) {
        BlockPos $$3 = null;
        for (int $$4 = 0; $$4 < 10; ++$$4) {
            int $$6;
            int $$7;
            int $$5 = $$1.getX() + this.random.nextInt($$2 * 2) - $$2;
            BlockPos $$8 = new BlockPos($$5, $$7 = $$0.getHeight(Heightmap.Types.WORLD_SURFACE, $$5, $$6 = $$1.getZ() + this.random.nextInt($$2 * 2) - $$2), $$6);
            if (!NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, $$0, $$8, EntityType.WANDERING_TRADER)) continue;
            $$3 = $$8;
            break;
        }
        return $$3;
    }

    private boolean hasEnoughSpace(BlockGetter $$0, BlockPos $$1) {
        for (BlockPos $$2 : BlockPos.betweenClosed($$1, $$1.offset(1, 2, 1))) {
            if ($$0.getBlockState($$2).getCollisionShape($$0, $$2).isEmpty()) continue;
            return false;
        }
        return true;
    }
}