/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 */
package net.minecraft.world.entity.monster.piglin;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Items;

public class StopHoldingItemIfNoLongerAdmiring {
    public static BehaviorControl<Piglin> create() {
        return BehaviorBuilder.create($$0 -> $$0.group($$0.absent(MemoryModuleType.ADMIRING_ITEM)).apply((Applicative)$$0, $$02 -> ($$0, $$1, $$2) -> {
            if ($$1.getOffhandItem().isEmpty() || $$1.getOffhandItem().is(Items.SHIELD)) {
                return false;
            }
            PiglinAi.stopHoldingOffHandItem($$1, true);
            return true;
        }));
    }
}