/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.System
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class CampfireBlockEntity
extends BlockEntity
implements Clearable {
    private static final int BURN_COOL_SPEED = 2;
    private static final int NUM_SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[4];
    private final int[] cookingTime = new int[4];
    private final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public CampfireBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.CAMPFIRE, $$0, $$1);
    }

    public static void cookTick(Level $$0, BlockPos $$1, BlockState $$22, CampfireBlockEntity $$3) {
        boolean $$4 = false;
        for (int $$5 = 0; $$5 < $$3.items.size(); ++$$5) {
            SimpleContainer $$7;
            ItemStack $$8;
            ItemStack $$6 = $$3.items.get($$5);
            if ($$6.isEmpty()) continue;
            $$4 = true;
            int n = $$5;
            $$3.cookingProgress[n] = $$3.cookingProgress[n] + 1;
            if ($$3.cookingProgress[$$5] < $$3.cookingTime[$$5] || !($$8 = (ItemStack)$$3.quickCheck.getRecipeFor($$7 = new SimpleContainer($$6), $$0).map($$2 -> $$2.assemble($$7, $$0.registryAccess())).orElse((Object)$$6)).isItemEnabled($$0.enabledFeatures())) continue;
            Containers.dropItemStack($$0, $$1.getX(), $$1.getY(), $$1.getZ(), $$8);
            $$3.items.set($$5, ItemStack.EMPTY);
            $$0.sendBlockUpdated($$1, $$22, $$22, 3);
            $$0.gameEvent(GameEvent.BLOCK_CHANGE, $$1, GameEvent.Context.of($$22));
        }
        if ($$4) {
            CampfireBlockEntity.setChanged($$0, $$1, $$22);
        }
    }

    public static void cooldownTick(Level $$0, BlockPos $$1, BlockState $$2, CampfireBlockEntity $$3) {
        boolean $$4 = false;
        for (int $$5 = 0; $$5 < $$3.items.size(); ++$$5) {
            if ($$3.cookingProgress[$$5] <= 0) continue;
            $$4 = true;
            $$3.cookingProgress[$$5] = Mth.clamp($$3.cookingProgress[$$5] - 2, 0, $$3.cookingTime[$$5]);
        }
        if ($$4) {
            CampfireBlockEntity.setChanged($$0, $$1, $$2);
        }
    }

    public static void particleTick(Level $$0, BlockPos $$1, BlockState $$2, CampfireBlockEntity $$3) {
        RandomSource $$4 = $$0.random;
        if ($$4.nextFloat() < 0.11f) {
            for (int $$5 = 0; $$5 < $$4.nextInt(2) + 2; ++$$5) {
                CampfireBlock.makeParticles($$0, $$1, $$2.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }
        int $$6 = $$2.getValue(CampfireBlock.FACING).get2DDataValue();
        for (int $$7 = 0; $$7 < $$3.items.size(); ++$$7) {
            if ($$3.items.get($$7).isEmpty() || !($$4.nextFloat() < 0.2f)) continue;
            Direction $$8 = Direction.from2DDataValue(Math.floorMod((int)($$7 + $$6), (int)4));
            float $$9 = 0.3125f;
            double $$10 = (double)$$1.getX() + 0.5 - (double)((float)$$8.getStepX() * 0.3125f) + (double)((float)$$8.getClockWise().getStepX() * 0.3125f);
            double $$11 = (double)$$1.getY() + 0.5;
            double $$12 = (double)$$1.getZ() + 0.5 - (double)((float)$$8.getStepZ() * 0.3125f) + (double)((float)$$8.getClockWise().getStepZ() * 0.3125f);
            for (int $$13 = 0; $$13 < 4; ++$$13) {
                $$0.addParticle(ParticleTypes.SMOKE, $$10, $$11, $$12, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.items.clear();
        ContainerHelper.loadAllItems($$0, this.items);
        if ($$0.contains("CookingTimes", 11)) {
            int[] $$1 = $$0.getIntArray("CookingTimes");
            System.arraycopy((Object)$$1, (int)0, (Object)this.cookingProgress, (int)0, (int)Math.min((int)this.cookingTime.length, (int)$$1.length));
        }
        if ($$0.contains("CookingTotalTimes", 11)) {
            int[] $$2 = $$0.getIntArray("CookingTotalTimes");
            System.arraycopy((Object)$$2, (int)0, (Object)this.cookingTime, (int)0, (int)Math.min((int)this.cookingTime.length, (int)$$2.length));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        ContainerHelper.saveAllItems($$0, this.items, true);
        $$0.putIntArray("CookingTimes", this.cookingProgress);
        $$0.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag $$0 = new CompoundTag();
        ContainerHelper.saveAllItems($$0, this.items, true);
        return $$0;
    }

    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack $$0) {
        if (this.items.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return this.quickCheck.getRecipeFor(new SimpleContainer($$0), this.level);
    }

    public boolean placeFood(@Nullable Entity $$0, ItemStack $$1, int $$2) {
        for (int $$3 = 0; $$3 < this.items.size(); ++$$3) {
            ItemStack $$4 = this.items.get($$3);
            if (!$$4.isEmpty()) continue;
            this.cookingTime[$$3] = $$2;
            this.cookingProgress[$$3] = 0;
            this.items.set($$3, $$1.split(1));
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of($$0, this.getBlockState()));
            this.markUpdated();
            return true;
        }
        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            this.markUpdated();
        }
    }
}