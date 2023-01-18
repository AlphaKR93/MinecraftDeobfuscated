/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class SolidBucketItem
extends BlockItem
implements DispensibleContainerItem {
    private final SoundEvent placeSound;

    public SolidBucketItem(Block $$0, SoundEvent $$1, Item.Properties $$2) {
        super($$0, $$2);
        this.placeSound = $$1;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        InteractionResult $$1 = super.useOn($$0);
        Player $$2 = $$0.getPlayer();
        if ($$1.consumesAction() && $$2 != null && !$$2.isCreative()) {
            InteractionHand $$3 = $$0.getHand();
            $$2.setItemInHand($$3, Items.BUCKET.getDefaultInstance());
        }
        return $$1;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState $$0) {
        return this.placeSound;
    }

    @Override
    public boolean emptyContents(@Nullable Player $$0, Level $$1, BlockPos $$2, @Nullable BlockHitResult $$3) {
        if ($$1.isInWorldBounds($$2) && $$1.isEmptyBlock($$2)) {
            if (!$$1.isClientSide) {
                $$1.setBlock($$2, this.getBlock().defaultBlockState(), 3);
            }
            $$1.gameEvent($$0, GameEvent.FLUID_PLACE, $$2);
            $$1.playSound($$0, $$2, this.placeSound, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}