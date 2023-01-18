/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ValidateNearbyPoi {
    private static final int MAX_DISTANCE = 16;

    public static BehaviorControl<LivingEntity> create(Predicate<Holder<PoiType>> $$0, MemoryModuleType<GlobalPos> $$1) {
        return BehaviorBuilder.create($$22 -> $$22.group($$22.present($$1)).apply((Applicative)$$22, $$2 -> ($$3, $$4, $$5) -> {
            GlobalPos $$6 = (GlobalPos)$$22.get($$2);
            BlockPos $$7 = $$6.pos();
            if ($$3.dimension() != $$6.dimension() || !$$7.closerToCenterThan($$4.position(), 16.0)) {
                return false;
            }
            ServerLevel $$8 = $$3.getServer().getLevel($$6.dimension());
            if ($$8 == null || !$$8.getPoiManager().exists($$7, $$0)) {
                $$2.erase();
            } else if (ValidateNearbyPoi.bedIsOccupied($$8, $$7, $$4)) {
                $$2.erase();
                $$3.getPoiManager().release($$7);
                DebugPackets.sendPoiTicketCountPacket($$3, $$7);
            }
            return true;
        }));
    }

    private static boolean bedIsOccupied(ServerLevel $$0, BlockPos $$1, LivingEntity $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        return $$3.is(BlockTags.BEDS) && $$3.getValue(BedBlock.OCCUPIED) != false && !$$2.isSleeping();
    }
}