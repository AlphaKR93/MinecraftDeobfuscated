/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Set
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.blocks;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockInput
implements Predicate<BlockInWorld> {
    private final BlockState state;
    private final Set<Property<?>> properties;
    @Nullable
    private final CompoundTag tag;

    public BlockInput(BlockState $$0, Set<Property<?>> $$1, @Nullable CompoundTag $$2) {
        this.state = $$0;
        this.properties = $$1;
        this.tag = $$2;
    }

    public BlockState getState() {
        return this.state;
    }

    public Set<Property<?>> getDefinedProperties() {
        return this.properties;
    }

    public boolean test(BlockInWorld $$0) {
        BlockState $$1 = $$0.getState();
        if (!$$1.is(this.state.getBlock())) {
            return false;
        }
        for (Property $$2 : this.properties) {
            if ($$1.getValue($$2) == this.state.getValue($$2)) continue;
            return false;
        }
        if (this.tag != null) {
            BlockEntity $$3 = $$0.getEntity();
            return $$3 != null && NbtUtils.compareNbt(this.tag, $$3.saveWithFullMetadata(), true);
        }
        return true;
    }

    public boolean test(ServerLevel $$0, BlockPos $$1) {
        return this.test(new BlockInWorld($$0, $$1, false));
    }

    public boolean place(ServerLevel $$0, BlockPos $$1, int $$2) {
        BlockEntity $$4;
        BlockState $$3 = Block.updateFromNeighbourShapes(this.state, $$0, $$1);
        if ($$3.isAir()) {
            $$3 = this.state;
        }
        if (!$$0.setBlock($$1, $$3, $$2)) {
            return false;
        }
        if (this.tag != null && ($$4 = $$0.getBlockEntity($$1)) != null) {
            $$4.load(this.tag);
        }
        return true;
    }
}