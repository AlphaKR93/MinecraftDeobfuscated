/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.function.Supplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BecomePassiveIfMemoryPresent {
    public static BehaviorControl<LivingEntity> create(MemoryModuleType<?> $$0, int $$1) {
        return BehaviorBuilder.create($$22 -> $$22.group($$22.registered(MemoryModuleType.ATTACK_TARGET), $$22.absent(MemoryModuleType.PACIFIED), $$22.present($$0)).apply((Applicative)$$22, $$22.point((Supplier<String>)((Supplier)() -> "[BecomePassive if " + $$0 + " present]"), ($$1, $$2, $$32) -> ($$3, $$4, $$5) -> {
            $$2.setWithExpiry(true, $$1);
            $$1.erase();
            return true;
        })));
    }
}