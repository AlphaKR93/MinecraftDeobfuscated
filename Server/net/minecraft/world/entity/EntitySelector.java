/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  java.lang.Object
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Team;

public final class EntitySelector {
    public static final Predicate<Entity> ENTITY_STILL_ALIVE = Entity::isAlive;
    public static final Predicate<Entity> LIVING_ENTITY_STILL_ALIVE = $$0 -> $$0.isAlive() && $$0 instanceof LivingEntity;
    public static final Predicate<Entity> ENTITY_NOT_BEING_RIDDEN = $$0 -> $$0.isAlive() && !$$0.isVehicle() && !$$0.isPassenger();
    public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = $$0 -> $$0 instanceof Container && $$0.isAlive();
    public static final Predicate<Entity> NO_CREATIVE_OR_SPECTATOR = $$0 -> !($$0 instanceof Player) || !$$0.isSpectator() && !((Player)$$0).isCreative();
    public static final Predicate<Entity> NO_SPECTATORS = $$0 -> !$$0.isSpectator();
    public static final Predicate<Entity> CAN_BE_COLLIDED_WITH = NO_SPECTATORS.and(Entity::canBeCollidedWith);

    private EntitySelector() {
    }

    public static Predicate<Entity> withinDistance(double $$0, double $$1, double $$2, double $$3) {
        double $$42 = $$3 * $$3;
        return $$4 -> $$4 != null && $$4.distanceToSqr($$0, $$1, $$2) <= $$42;
    }

    public static Predicate<Entity> pushableBy(Entity $$0) {
        Team.CollisionRule $$2;
        Team $$1 = $$0.getTeam();
        Team.CollisionRule collisionRule = $$2 = $$1 == null ? Team.CollisionRule.ALWAYS : $$1.getCollisionRule();
        if ($$2 == Team.CollisionRule.NEVER) {
            return Predicates.alwaysFalse();
        }
        return NO_SPECTATORS.and($$3 -> {
            boolean $$6;
            Team.CollisionRule $$5;
            if (!$$3.isPushable()) {
                return false;
            }
            if (!(!$$0.level.isClientSide || $$3 instanceof Player && ((Player)$$3).isLocalPlayer())) {
                return false;
            }
            Team $$4 = $$3.getTeam();
            Team.CollisionRule collisionRule = $$5 = $$4 == null ? Team.CollisionRule.ALWAYS : $$4.getCollisionRule();
            if ($$5 == Team.CollisionRule.NEVER) {
                return false;
            }
            boolean bl = $$6 = $$1 != null && $$1.isAlliedTo($$4);
            if (($$2 == Team.CollisionRule.PUSH_OWN_TEAM || $$5 == Team.CollisionRule.PUSH_OWN_TEAM) && $$6) {
                return false;
            }
            return $$2 != Team.CollisionRule.PUSH_OTHER_TEAMS && $$5 != Team.CollisionRule.PUSH_OTHER_TEAMS || $$6;
        });
    }

    public static Predicate<Entity> notRiding(Entity $$0) {
        return $$1 -> {
            while ($$1.isPassenger()) {
                if (($$1 = $$1.getVehicle()) != $$0) continue;
                return false;
            }
            return true;
        };
    }

    public static class MobCanWearArmorEntitySelector
    implements Predicate<Entity> {
        private final ItemStack itemStack;

        public MobCanWearArmorEntitySelector(ItemStack $$0) {
            this.itemStack = $$0;
        }

        public boolean test(@Nullable Entity $$0) {
            if (!$$0.isAlive()) {
                return false;
            }
            if (!($$0 instanceof LivingEntity)) {
                return false;
            }
            LivingEntity $$1 = (LivingEntity)$$0;
            return $$1.canTakeItem(this.itemStack);
        }
    }
}