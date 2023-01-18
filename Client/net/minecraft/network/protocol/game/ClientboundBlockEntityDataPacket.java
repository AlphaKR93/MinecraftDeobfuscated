/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientboundBlockEntityDataPacket
implements Packet<ClientGamePacketListener> {
    private final BlockPos pos;
    private final BlockEntityType<?> type;
    @Nullable
    private final CompoundTag tag;

    public static ClientboundBlockEntityDataPacket create(BlockEntity $$0, Function<BlockEntity, CompoundTag> $$1) {
        return new ClientboundBlockEntityDataPacket($$0.getBlockPos(), $$0.getType(), (CompoundTag)$$1.apply((Object)$$0));
    }

    public static ClientboundBlockEntityDataPacket create(BlockEntity $$0) {
        return ClientboundBlockEntityDataPacket.create($$0, (Function<BlockEntity, CompoundTag>)((Function)BlockEntity::getUpdateTag));
    }

    private ClientboundBlockEntityDataPacket(BlockPos $$0, BlockEntityType<?> $$1, CompoundTag $$2) {
        this.pos = $$0;
        this.type = $$1;
        this.tag = $$2.isEmpty() ? null : $$2;
    }

    public ClientboundBlockEntityDataPacket(FriendlyByteBuf $$0) {
        this.pos = $$0.readBlockPos();
        this.type = $$0.readById(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        this.tag = $$0.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        $$0.writeId(BuiltInRegistries.BLOCK_ENTITY_TYPE, this.type);
        $$0.writeNbt(this.tag);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleBlockEntityData(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }
}