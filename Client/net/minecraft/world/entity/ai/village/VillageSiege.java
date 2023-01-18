/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.ai.village;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class VillageSiege
implements CustomSpawner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean hasSetupSiege;
    private State siegeState = State.SIEGE_DONE;
    private int zombiesToSpawn;
    private int nextSpawnTime;
    private int spawnX;
    private int spawnY;
    private int spawnZ;

    @Override
    public int tick(ServerLevel $$0, boolean $$1, boolean $$2) {
        if ($$0.isDay() || !$$1) {
            this.siegeState = State.SIEGE_DONE;
            this.hasSetupSiege = false;
            return 0;
        }
        float $$3 = $$0.getTimeOfDay(0.0f);
        if ((double)$$3 == 0.5) {
            State state = this.siegeState = $$0.random.nextInt(10) == 0 ? State.SIEGE_TONIGHT : State.SIEGE_DONE;
        }
        if (this.siegeState == State.SIEGE_DONE) {
            return 0;
        }
        if (!this.hasSetupSiege) {
            if (this.tryToSetupSiege($$0)) {
                this.hasSetupSiege = true;
            } else {
                return 0;
            }
        }
        if (this.nextSpawnTime > 0) {
            --this.nextSpawnTime;
            return 0;
        }
        this.nextSpawnTime = 2;
        if (this.zombiesToSpawn > 0) {
            this.trySpawn($$0);
            --this.zombiesToSpawn;
        } else {
            this.siegeState = State.SIEGE_DONE;
        }
        return 1;
    }

    private boolean tryToSetupSiege(ServerLevel $$0) {
        for (Player $$1 : $$0.players()) {
            BlockPos $$2;
            if ($$1.isSpectator() || !$$0.isVillage($$2 = $$1.blockPosition()) || $$0.getBiome($$2).is(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) continue;
            for (int $$3 = 0; $$3 < 10; ++$$3) {
                float $$4 = $$0.random.nextFloat() * ((float)Math.PI * 2);
                this.spawnX = $$2.getX() + Mth.floor(Mth.cos($$4) * 32.0f);
                this.spawnY = $$2.getY();
                this.spawnZ = $$2.getZ() + Mth.floor(Mth.sin($$4) * 32.0f);
                if (this.findRandomSpawnPos($$0, new BlockPos(this.spawnX, this.spawnY, this.spawnZ)) == null) continue;
                this.nextSpawnTime = 0;
                this.zombiesToSpawn = 20;
                break;
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    private void trySpawn(ServerLevel $$0) {
        void $$4;
        Vec3 $$1 = this.findRandomSpawnPos($$0, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
        if ($$1 == null) {
            return;
        }
        try {
            Zombie $$2 = new Zombie($$0);
            $$2.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$2.blockPosition()), MobSpawnType.EVENT, null, null);
        }
        catch (Exception $$3) {
            LOGGER.warn("Failed to create zombie for village siege at {}", (Object)$$1, (Object)$$3);
            return;
        }
        $$4.moveTo($$1.x, $$1.y, $$1.z, $$0.random.nextFloat() * 360.0f, 0.0f);
        $$0.addFreshEntityWithPassengers((Entity)$$4);
    }

    @Nullable
    private Vec3 findRandomSpawnPos(ServerLevel $$0, BlockPos $$1) {
        for (int $$2 = 0; $$2 < 10; ++$$2) {
            int $$4;
            int $$5;
            int $$3 = $$1.getX() + $$0.random.nextInt(16) - 8;
            BlockPos $$6 = new BlockPos($$3, $$5 = $$0.getHeight(Heightmap.Types.WORLD_SURFACE, $$3, $$4 = $$1.getZ() + $$0.random.nextInt(16) - 8), $$4);
            if (!$$0.isVillage($$6) || !Monster.checkMonsterSpawnRules(EntityType.ZOMBIE, $$0, MobSpawnType.EVENT, $$6, $$0.random)) continue;
            return Vec3.atBottomCenterOf($$6);
        }
        return null;
    }

    static enum State {
        SIEGE_CAN_ACTIVATE,
        SIEGE_TONIGHT,
        SIEGE_DONE;

    }
}