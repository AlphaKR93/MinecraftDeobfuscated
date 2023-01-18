/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseContainerBlockEntity
extends BlockEntity
implements Container,
MenuProvider,
Nameable {
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private Component name;

    protected BaseContainerBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.lockKey = LockCode.fromTag($$0);
        if ($$0.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson($$0.getString("CustomName"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        this.lockKey.addToTag($$0);
        if (this.name != null) {
            $$0.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }

    public void setCustomName(Component $$0) {
        this.name = $$0;
    }

    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    protected abstract Component getDefaultName();

    public boolean canOpen(Player $$0) {
        return BaseContainerBlockEntity.canUnlock($$0, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(Player $$0, LockCode $$1, Component $$2) {
        if ($$0.isSpectator() || $$1.unlocksWith($$0.getMainHandItem())) {
            return true;
        }
        $$0.displayClientMessage(Component.translatable("container.isLocked", $$2), true);
        $$0.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0f, 1.0f);
        return false;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        if (this.canOpen($$2)) {
            return this.createMenu($$0, $$1);
        }
        return null;
    }

    protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);
}