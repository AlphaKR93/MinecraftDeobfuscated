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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

public class MeleeAttack {
    public static OneShot<Mob> create(int $$0) {
        return BehaviorBuilder.create($$1 -> $$1.group($$1.registered(MemoryModuleType.LOOK_TARGET), $$1.present(MemoryModuleType.ATTACK_TARGET), $$1.absent(MemoryModuleType.ATTACK_COOLING_DOWN), $$1.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply((Applicative)$$1, ($$2, $$3, $$4, $$5) -> ($$6, $$7, $$8) -> {
            LivingEntity $$9 = (LivingEntity)$$1.get($$3);
            if (!MeleeAttack.isHoldingUsableProjectileWeapon($$7) && $$7.isWithinMeleeAttackRange($$9) && ((NearestVisibleLivingEntities)$$1.get($$5)).contains($$9)) {
                $$2.set(new EntityTracker($$9, true));
                $$7.swing(InteractionHand.MAIN_HAND);
                $$7.doHurtTarget($$9);
                $$4.setWithExpiry(true, $$0);
                return true;
            }
            return false;
        }));
    }

    private static boolean isHoldingUsableProjectileWeapon(Mob $$0) {
        return $$0.isHolding((Predicate<ItemStack>)((Predicate)$$1 -> {
            Item $$2 = $$1.getItem();
            return $$2 instanceof ProjectileWeaponItem && $$0.canFireProjectileWeapon((ProjectileWeaponItem)$$2);
        }));
    }
}