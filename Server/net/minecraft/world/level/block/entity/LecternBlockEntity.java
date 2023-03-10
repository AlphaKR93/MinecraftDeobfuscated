/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LecternBlockEntity
extends BlockEntity
implements Clearable,
MenuProvider {
    public static final int DATA_PAGE = 0;
    public static final int NUM_DATA = 1;
    public static final int SLOT_BOOK = 0;
    public static final int NUM_SLOTS = 1;
    private final Container bookAccess = new Container(){

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return LecternBlockEntity.this.book.isEmpty();
        }

        @Override
        public ItemStack getItem(int $$0) {
            return $$0 == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int $$0, int $$1) {
            if ($$0 == 0) {
                ItemStack $$2 = LecternBlockEntity.this.book.split($$1);
                if (LecternBlockEntity.this.book.isEmpty()) {
                    LecternBlockEntity.this.onBookItemRemove();
                }
                return $$2;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int $$0) {
            if ($$0 == 0) {
                ItemStack $$1 = LecternBlockEntity.this.book;
                LecternBlockEntity.this.book = ItemStack.EMPTY;
                LecternBlockEntity.this.onBookItemRemove();
                return $$1;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int $$0, ItemStack $$1) {
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            LecternBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player $$0) {
            if (LecternBlockEntity.this.level.getBlockEntity(LecternBlockEntity.this.worldPosition) != LecternBlockEntity.this) {
                return false;
            }
            if ($$0.distanceToSqr((double)LecternBlockEntity.this.worldPosition.getX() + 0.5, (double)LecternBlockEntity.this.worldPosition.getY() + 0.5, (double)LecternBlockEntity.this.worldPosition.getZ() + 0.5) > 64.0) {
                return false;
            }
            return LecternBlockEntity.this.hasBook();
        }

        @Override
        public boolean canPlaceItem(int $$0, ItemStack $$1) {
            return false;
        }

        @Override
        public void clearContent() {
        }
    };
    private final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int $$0) {
            return $$0 == 0 ? LecternBlockEntity.this.page : 0;
        }

        @Override
        public void set(int $$0, int $$1) {
            if ($$0 == 0) {
                LecternBlockEntity.this.setPage($$1);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    ItemStack book = ItemStack.EMPTY;
    int page;
    private int pageCount;

    public LecternBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.LECTERN, $$0, $$1);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return this.book.is(Items.WRITABLE_BOOK) || this.book.is(Items.WRITTEN_BOOK);
    }

    public void setBook(ItemStack $$0) {
        this.setBook($$0, null);
    }

    void onBookItemRemove() {
        this.page = 0;
        this.pageCount = 0;
        LecternBlock.resetBookState(this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
    }

    public void setBook(ItemStack $$0, @Nullable Player $$1) {
        this.book = this.resolveBook($$0, $$1);
        this.page = 0;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.setChanged();
    }

    void setPage(int $$0) {
        int $$1 = Mth.clamp($$0, 0, this.pageCount - 1);
        if ($$1 != this.page) {
            this.page = $$1;
            this.setChanged();
            LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public int getPage() {
        return this.page;
    }

    public int getRedstoneSignal() {
        float $$0 = this.pageCount > 1 ? (float)this.getPage() / ((float)this.pageCount - 1.0f) : 1.0f;
        return Mth.floor($$0 * 14.0f) + (this.hasBook() ? 1 : 0);
    }

    private ItemStack resolveBook(ItemStack $$0, @Nullable Player $$1) {
        if (this.level instanceof ServerLevel && $$0.is(Items.WRITTEN_BOOK)) {
            WrittenBookItem.resolveBookComponents($$0, this.createCommandSourceStack($$1), $$1);
        }
        return $$0;
    }

    private CommandSourceStack createCommandSourceStack(@Nullable Player $$0) {
        Component $$4;
        String $$3;
        if ($$0 == null) {
            String $$1 = "Lectern";
            MutableComponent $$2 = Component.literal("Lectern");
        } else {
            $$3 = $$0.getName().getString();
            $$4 = $$0.getDisplayName();
        }
        Vec3 $$5 = Vec3.atCenterOf(this.worldPosition);
        return new CommandSourceStack(CommandSource.NULL, $$5, Vec2.ZERO, (ServerLevel)this.level, 2, $$3, $$4, this.level.getServer(), $$0);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.book = $$0.contains("Book", 10) ? this.resolveBook(ItemStack.of($$0.getCompound("Book")), null) : ItemStack.EMPTY;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.page = Mth.clamp($$0.getInt("Page"), 0, this.pageCount - 1);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        if (!this.getBook().isEmpty()) {
            $$0.put("Book", this.getBook().save(new CompoundTag()));
            $$0.putInt("Page", this.page);
        }
    }

    @Override
    public void clearContent() {
        this.setBook(ItemStack.EMPTY);
    }

    @Override
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        return new LecternMenu($$0, this.bookAccess, this.dataAccess);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.lectern");
    }
}