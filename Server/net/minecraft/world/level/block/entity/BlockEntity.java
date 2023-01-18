/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public abstract class BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityType<?> type;
    @Nullable
    protected Level level;
    protected final BlockPos worldPosition;
    protected boolean remove;
    private BlockState blockState;

    public BlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        this.type = $$0;
        this.worldPosition = $$1.immutable();
        this.blockState = $$2;
    }

    public static BlockPos getPosFromTag(CompoundTag $$0) {
        return new BlockPos($$0.getInt("x"), $$0.getInt("y"), $$0.getInt("z"));
    }

    @Nullable
    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level $$0) {
        this.level = $$0;
    }

    public boolean hasLevel() {
        return this.level != null;
    }

    public void load(CompoundTag $$0) {
    }

    protected void saveAdditional(CompoundTag $$0) {
    }

    public final CompoundTag saveWithFullMetadata() {
        CompoundTag $$0 = this.saveWithoutMetadata();
        this.saveMetadata($$0);
        return $$0;
    }

    public final CompoundTag saveWithId() {
        CompoundTag $$0 = this.saveWithoutMetadata();
        this.saveId($$0);
        return $$0;
    }

    public final CompoundTag saveWithoutMetadata() {
        CompoundTag $$0 = new CompoundTag();
        this.saveAdditional($$0);
        return $$0;
    }

    private void saveId(CompoundTag $$0) {
        ResourceLocation $$1 = BlockEntityType.getKey(this.getType());
        if ($$1 == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        $$0.putString("id", $$1.toString());
    }

    public static void addEntityType(CompoundTag $$0, BlockEntityType<?> $$1) {
        $$0.putString("id", BlockEntityType.getKey($$1).toString());
    }

    public void saveToItem(ItemStack $$0) {
        BlockItem.setBlockEntityData($$0, this.getType(), this.saveWithoutMetadata());
    }

    private void saveMetadata(CompoundTag $$0) {
        this.saveId($$0);
        $$0.putInt("x", this.worldPosition.getX());
        $$0.putInt("y", this.worldPosition.getY());
        $$0.putInt("z", this.worldPosition.getZ());
    }

    @Nullable
    public static BlockEntity loadStatic(BlockPos $$0, BlockState $$1, CompoundTag $$22) {
        String $$32 = $$22.getString("id");
        ResourceLocation $$4 = ResourceLocation.tryParse($$32);
        if ($$4 == null) {
            LOGGER.error("Block entity has invalid type: {}", (Object)$$32);
            return null;
        }
        return (BlockEntity)BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional($$4).map($$3 -> {
            try {
                return $$3.create($$0, $$1);
            }
            catch (Throwable $$4) {
                LOGGER.error("Failed to create block entity {}", (Object)$$32, (Object)$$4);
                return null;
            }
        }).map($$2 -> {
            try {
                $$2.load($$22);
                return $$2;
            }
            catch (Throwable $$3) {
                LOGGER.error("Failed to load data for block entity {}", (Object)$$32, (Object)$$3);
                return null;
            }
        }).orElseGet(() -> {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object)$$32);
            return null;
        });
    }

    public void setChanged() {
        if (this.level != null) {
            BlockEntity.setChanged(this.level, this.worldPosition, this.blockState);
        }
    }

    protected static void setChanged(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.blockEntityChanged($$1);
        if (!$$2.isAir()) {
            $$0.updateNeighbourForOutputSignal($$1, $$2.getBlock());
        }
    }

    public BlockPos getBlockPos() {
        return this.worldPosition;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return null;
    }

    public CompoundTag getUpdateTag() {
        return new CompoundTag();
    }

    public boolean isRemoved() {
        return this.remove;
    }

    public void setRemoved() {
        this.remove = true;
    }

    public void clearRemoved() {
        this.remove = false;
    }

    public boolean triggerEvent(int $$0, int $$1) {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory $$0) {
        $$0.setDetail("Name", () -> BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName());
        if (this.level == null) {
            return;
        }
        CrashReportCategory.populateBlockDetails($$0, this.level, this.worldPosition, this.getBlockState());
        CrashReportCategory.populateBlockDetails($$0, this.level, this.worldPosition, this.level.getBlockState(this.worldPosition));
    }

    public boolean onlyOpCanSetNbt() {
        return false;
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    @Deprecated
    public void setBlockState(BlockState $$0) {
        this.blockState = $$0;
    }
}