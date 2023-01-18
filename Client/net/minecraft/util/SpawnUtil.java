/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.util;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnUtil {
    public static <T extends Mob> Optional<T> trySpawnMob(EntityType<T> $$0, MobSpawnType $$1, ServerLevel $$2, BlockPos $$3, int $$4, int $$5, int $$6, Strategy $$7) {
        BlockPos.MutableBlockPos $$8 = $$3.mutable();
        for (int $$9 = 0; $$9 < $$4; ++$$9) {
            Mob $$12;
            int $$10 = Mth.randomBetweenInclusive($$2.random, -$$5, $$5);
            int $$11 = Mth.randomBetweenInclusive($$2.random, -$$5, $$5);
            $$8.setWithOffset($$3, $$10, $$6, $$11);
            if (!$$2.getWorldBorder().isWithinBounds($$8) || !SpawnUtil.moveToPossibleSpawnPosition($$2, $$6, $$8, $$7) || ($$12 = (Mob)$$0.create($$2, null, null, $$8, $$1, false, false)) == null) continue;
            if ($$12.checkSpawnRules($$2, $$1) && $$12.checkSpawnObstruction($$2)) {
                $$2.addFreshEntityWithPassengers($$12);
                return Optional.of((Object)$$12);
            }
            $$12.discard();
        }
        return Optional.empty();
    }

    private static boolean moveToPossibleSpawnPosition(ServerLevel $$0, int $$1, BlockPos.MutableBlockPos $$2, Strategy $$3) {
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos().set($$2);
        BlockState $$5 = $$0.getBlockState($$4);
        for (int $$6 = $$1; $$6 >= -$$1; --$$6) {
            $$2.move(Direction.DOWN);
            $$4.setWithOffset((Vec3i)$$2, Direction.UP);
            BlockState $$7 = $$0.getBlockState($$2);
            if ($$3.canSpawnOn($$0, $$2, $$7, $$4, $$5)) {
                $$2.move(Direction.UP);
                return true;
            }
            $$5 = $$7;
        }
        return false;
    }

    public static interface Strategy {
        public static final Strategy LEGACY_IRON_GOLEM = ($$0, $$1, $$2, $$3, $$4) -> ($$4.isAir() || $$4.getMaterial().isLiquid()) && $$2.getMaterial().isSolidBlocking();
        public static final Strategy ON_TOP_OF_COLLIDER = ($$0, $$1, $$2, $$3, $$4) -> $$4.getCollisionShape($$0, $$3).isEmpty() && Block.isFaceFull($$2.getCollisionShape($$0, $$1), Direction.UP);

        public boolean canSpawnOn(ServerLevel var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5);
    }
}