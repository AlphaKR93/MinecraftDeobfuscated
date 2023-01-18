/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class PlayerEnderChestContainer
extends SimpleContainer {
    @Nullable
    private EnderChestBlockEntity activeChest;

    public PlayerEnderChestContainer() {
        super(27);
    }

    public void setActiveChest(EnderChestBlockEntity $$0) {
        this.activeChest = $$0;
    }

    public boolean isActiveChest(EnderChestBlockEntity $$0) {
        return this.activeChest == $$0;
    }

    @Override
    public void fromTag(ListTag $$0) {
        for (int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            this.setItem($$1, ItemStack.EMPTY);
        }
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            CompoundTag $$3 = $$0.getCompound($$2);
            int $$4 = $$3.getByte("Slot") & 0xFF;
            if ($$4 < 0 || $$4 >= this.getContainerSize()) continue;
            this.setItem($$4, ItemStack.of($$3));
        }
    }

    @Override
    public ListTag createTag() {
        ListTag $$0 = new ListTag();
        for (int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if ($$2.isEmpty()) continue;
            CompoundTag $$3 = new CompoundTag();
            $$3.putByte("Slot", (byte)$$1);
            $$2.save($$3);
            $$0.add($$3);
        }
        return $$0;
    }

    @Override
    public boolean stillValid(Player $$0) {
        if (this.activeChest != null && !this.activeChest.stillValid($$0)) {
            return false;
        }
        return super.stillValid($$0);
    }

    @Override
    public void startOpen(Player $$0) {
        if (this.activeChest != null) {
            this.activeChest.startOpen($$0);
        }
        super.startOpen($$0);
    }

    @Override
    public void stopOpen(Player $$0) {
        if (this.activeChest != null) {
            this.activeChest.stopOpen($$0);
        }
        super.stopOpen($$0);
        this.activeChest = null;
    }
}