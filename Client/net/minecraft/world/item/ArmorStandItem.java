/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStandItem
extends Item {
    public ArmorStandItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$02) {
        Direction $$1 = $$02.getClickedFace();
        if ($$1 == Direction.DOWN) {
            return InteractionResult.FAIL;
        }
        Level $$2 = $$02.getLevel();
        BlockPlaceContext $$3 = new BlockPlaceContext($$02);
        BlockPos $$4 = $$3.getClickedPos();
        ItemStack $$5 = $$02.getItemInHand();
        Vec3 $$6 = Vec3.atBottomCenterOf($$4);
        AABB $$7 = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox($$6.x(), $$6.y(), $$6.z());
        if (!$$2.noCollision(null, $$7) || !$$2.getEntities(null, $$7).isEmpty()) {
            return InteractionResult.FAIL;
        }
        if ($$2 instanceof ServerLevel) {
            ServerLevel $$8 = (ServerLevel)$$2;
            Consumer $$9 = EntityType.appendCustomEntityStackConfig($$0 -> {}, $$8, $$5, $$02.getPlayer());
            ArmorStand $$10 = EntityType.ARMOR_STAND.create($$8, $$5.getTag(), $$9, $$4, MobSpawnType.SPAWN_EGG, true, true);
            if ($$10 == null) {
                return InteractionResult.FAIL;
            }
            float $$11 = (float)Mth.floor((Mth.wrapDegrees($$02.getRotation() - 180.0f) + 22.5f) / 45.0f) * 45.0f;
            $$10.moveTo($$10.getX(), $$10.getY(), $$10.getZ(), $$11, 0.0f);
            this.randomizePose($$10, $$2.random);
            $$8.addFreshEntityWithPassengers($$10);
            $$2.playSound(null, $$10.getX(), $$10.getY(), $$10.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75f, 0.8f);
            $$10.gameEvent(GameEvent.ENTITY_PLACE, $$02.getPlayer());
        }
        $$5.shrink(1);
        return InteractionResult.sidedSuccess($$2.isClientSide);
    }

    private void randomizePose(ArmorStand $$0, RandomSource $$1) {
        Rotations $$2 = $$0.getHeadPose();
        float $$3 = $$1.nextFloat() * 5.0f;
        float $$4 = $$1.nextFloat() * 20.0f - 10.0f;
        Rotations $$5 = new Rotations($$2.getX() + $$3, $$2.getY() + $$4, $$2.getZ());
        $$0.setHeadPose($$5);
        $$2 = $$0.getBodyPose();
        $$3 = $$1.nextFloat() * 10.0f - 5.0f;
        $$5 = new Rotations($$2.getX(), $$2.getY() + $$3, $$2.getZ());
        $$0.setBodyPose($$5);
    }
}