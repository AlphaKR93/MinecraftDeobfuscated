/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BoatItem
extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final Boat.Type type;
    private final boolean hasChest;

    public BoatItem(boolean $$0, Boat.Type $$1, Item.Properties $$2) {
        super($$2);
        this.hasChest = $$0;
        this.type = $$1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        BlockHitResult $$4 = BoatItem.getPlayerPOVHitResult($$0, $$1, ClipContext.Fluid.ANY);
        if (((HitResult)$$4).getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass($$3);
        }
        Vec3 $$5 = $$1.getViewVector(1.0f);
        double $$6 = 5.0;
        List<Entity> $$7 = $$0.getEntities($$1, $$1.getBoundingBox().expandTowards($$5.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
        if (!$$7.isEmpty()) {
            Vec3 $$8 = $$1.getEyePosition();
            for (Entity $$9 : $$7) {
                AABB $$10 = $$9.getBoundingBox().inflate($$9.getPickRadius());
                if (!$$10.contains($$8)) continue;
                return InteractionResultHolder.pass($$3);
            }
        }
        if (((HitResult)$$4).getType() == HitResult.Type.BLOCK) {
            Boat $$11 = this.getBoat($$0, $$4);
            $$11.setVariant(this.type);
            $$11.setYRot($$1.getYRot());
            if (!$$0.noCollision($$11, $$11.getBoundingBox())) {
                return InteractionResultHolder.fail($$3);
            }
            if (!$$0.isClientSide) {
                $$0.addFreshEntity($$11);
                $$0.gameEvent($$1, GameEvent.ENTITY_PLACE, $$4.getLocation());
                if (!$$1.getAbilities().instabuild) {
                    $$3.shrink(1);
                }
            }
            $$1.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess($$3, $$0.isClientSide());
        }
        return InteractionResultHolder.pass($$3);
    }

    private Boat getBoat(Level $$0, HitResult $$1) {
        if (this.hasChest) {
            return new ChestBoat($$0, $$1.getLocation().x, $$1.getLocation().y, $$1.getLocation().z);
        }
        return new Boat($$0, $$1.getLocation().x, $$1.getLocation().y, $$1.getLocation().z);
    }
}