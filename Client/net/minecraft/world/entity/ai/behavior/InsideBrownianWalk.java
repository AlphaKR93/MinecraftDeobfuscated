/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 *  java.util.Collections
 *  java.util.List
 *  java.util.stream.Collectors
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InsideBrownianWalk {
    public static BehaviorControl<PathfinderMob> create(float $$0) {
        return BehaviorBuilder.create($$12 -> $$12.group($$12.absent(MemoryModuleType.WALK_TARGET)).apply((Applicative)$$12, $$1 -> ($$22, $$3, $$4) -> {
            if ($$22.canSeeSky($$3.blockPosition())) {
                return false;
            }
            BlockPos $$5 = $$3.blockPosition();
            List $$6 = (List)BlockPos.betweenClosedStream($$5.offset(-1, -1, -1), $$5.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
            Collections.shuffle((List)$$6);
            $$6.stream().filter($$1 -> !$$22.canSeeSky((BlockPos)$$1)).filter($$2 -> $$22.loadedAndEntityCanStandOn((BlockPos)$$2, $$3)).filter($$2 -> $$22.noCollision($$3)).findFirst().ifPresent($$2 -> $$1.set(new WalkTarget((BlockPos)$$2, $$0, 0)));
            return true;
        }));
    }
}