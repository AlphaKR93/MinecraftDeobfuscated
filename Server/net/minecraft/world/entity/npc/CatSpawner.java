/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.npc;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.AABB;

public class CatSpawner
implements CustomSpawner {
    private static final int TICK_DELAY = 1200;
    private int nextTick;

    @Override
    public int tick(ServerLevel $$0, boolean $$1, boolean $$2) {
        if (!$$2 || !$$0.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return 0;
        }
        --this.nextTick;
        if (this.nextTick > 0) {
            return 0;
        }
        this.nextTick = 1200;
        ServerPlayer $$3 = $$0.getRandomPlayer();
        if ($$3 == null) {
            return 0;
        }
        RandomSource $$4 = $$0.random;
        int $$5 = (8 + $$4.nextInt(24)) * ($$4.nextBoolean() ? -1 : 1);
        int $$6 = (8 + $$4.nextInt(24)) * ($$4.nextBoolean() ? -1 : 1);
        BlockPos $$7 = $$3.blockPosition().offset($$5, 0, $$6);
        int $$8 = 10;
        if (!$$0.hasChunksAt($$7.getX() - 10, $$7.getZ() - 10, $$7.getX() + 10, $$7.getZ() + 10)) {
            return 0;
        }
        if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, $$0, $$7, EntityType.CAT)) {
            if ($$0.isCloseToVillage($$7, 2)) {
                return this.spawnInVillage($$0, $$7);
            }
            if ($$0.structureManager().getStructureWithPieceAt($$7, StructureTags.CATS_SPAWN_IN).isValid()) {
                return this.spawnInHut($$0, $$7);
            }
        }
        return 0;
    }

    private int spawnInVillage(ServerLevel $$02, BlockPos $$1) {
        List $$3;
        int $$2 = 48;
        if ($$02.getPoiManager().getCountInRange((Predicate<Holder<PoiType>>)((Predicate)$$0 -> $$0.is(PoiTypes.HOME)), $$1, 48, PoiManager.Occupancy.IS_OCCUPIED) > 4L && ($$3 = $$02.getEntitiesOfClass(Cat.class, new AABB($$1).inflate(48.0, 8.0, 48.0))).size() < 5) {
            return this.spawnCat($$1, $$02);
        }
        return 0;
    }

    private int spawnInHut(ServerLevel $$0, BlockPos $$1) {
        int $$2 = 16;
        List $$3 = $$0.getEntitiesOfClass(Cat.class, new AABB($$1).inflate(16.0, 8.0, 16.0));
        if ($$3.size() < 1) {
            return this.spawnCat($$1, $$0);
        }
        return 0;
    }

    private int spawnCat(BlockPos $$0, ServerLevel $$1) {
        Cat $$2 = EntityType.CAT.create($$1);
        if ($$2 == null) {
            return 0;
        }
        $$2.finalizeSpawn($$1, $$1.getCurrentDifficultyAt($$0), MobSpawnType.NATURAL, null, null);
        $$2.moveTo($$0, 0.0f, 0.0f);
        $$1.addFreshEntityWithPassengers($$2);
        return 1;
    }
}