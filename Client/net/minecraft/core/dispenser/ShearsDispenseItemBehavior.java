/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.core.dispenser;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior
extends OptionalDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
        ServerLevel $$2 = $$0.getLevel();
        if (!$$2.isClientSide()) {
            Vec3i $$3 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
            this.setSuccess(ShearsDispenseItemBehavior.tryShearBeehive($$2, (BlockPos)$$3) || ShearsDispenseItemBehavior.tryShearLivingEntity($$2, (BlockPos)$$3));
            if (this.isSuccess() && $$1.hurt(1, $$2.getRandom(), null)) {
                $$1.setCount(0);
            }
        }
        return $$1;
    }

    private static boolean tryShearBeehive(ServerLevel $$02, BlockPos $$1) {
        int $$3;
        BlockState $$2 = $$02.getBlockState($$1);
        if ($$2.is(BlockTags.BEEHIVES, (Predicate<BlockBehaviour.BlockStateBase>)((Predicate)$$0 -> $$0.hasProperty(BeehiveBlock.HONEY_LEVEL) && $$0.getBlock() instanceof BeehiveBlock)) && ($$3 = $$2.getValue(BeehiveBlock.HONEY_LEVEL).intValue()) >= 5) {
            $$02.playSound(null, $$1, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
            BeehiveBlock.dropHoneycomb($$02, $$1);
            ((BeehiveBlock)$$2.getBlock()).releaseBeesAndResetHoneyLevel($$02, $$2, $$1, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            $$02.gameEvent(null, GameEvent.SHEAR, $$1);
            return true;
        }
        return false;
    }

    private static boolean tryShearLivingEntity(ServerLevel $$0, BlockPos $$1) {
        List $$2 = $$0.getEntitiesOfClass(LivingEntity.class, new AABB($$1), EntitySelector.NO_SPECTATORS);
        for (LivingEntity $$3 : $$2) {
            Shearable $$4;
            if (!($$3 instanceof Shearable) || !($$4 = (Shearable)((Object)$$3)).readyForShearing()) continue;
            $$4.shear(SoundSource.BLOCKS);
            $$0.gameEvent(null, GameEvent.SHEAR, $$1);
            return true;
        }
        return false;
    }
}