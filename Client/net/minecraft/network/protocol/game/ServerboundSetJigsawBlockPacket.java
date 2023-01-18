/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class ServerboundSetJigsawBlockPacket
implements Packet<ServerGamePacketListener> {
    private final BlockPos pos;
    private final ResourceLocation name;
    private final ResourceLocation target;
    private final ResourceLocation pool;
    private final String finalState;
    private final JigsawBlockEntity.JointType joint;

    public ServerboundSetJigsawBlockPacket(BlockPos $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3, String $$4, JigsawBlockEntity.JointType $$5) {
        this.pos = $$0;
        this.name = $$1;
        this.target = $$2;
        this.pool = $$3;
        this.finalState = $$4;
        this.joint = $$5;
    }

    public ServerboundSetJigsawBlockPacket(FriendlyByteBuf $$0) {
        this.pos = $$0.readBlockPos();
        this.name = $$0.readResourceLocation();
        this.target = $$0.readResourceLocation();
        this.pool = $$0.readResourceLocation();
        this.finalState = $$0.readUtf();
        this.joint = (JigsawBlockEntity.JointType)JigsawBlockEntity.JointType.byName($$0.readUtf()).orElse((Object)JigsawBlockEntity.JointType.ALIGNED);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        $$0.writeResourceLocation(this.name);
        $$0.writeResourceLocation(this.target);
        $$0.writeResourceLocation(this.pool);
        $$0.writeUtf(this.finalState);
        $$0.writeUtf(this.joint.getSerializedName());
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSetJigsawBlock(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public ResourceLocation getTarget() {
        return this.target;
    }

    public ResourceLocation getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public JigsawBlockEntity.JointType getJoint() {
        return this.joint;
    }
}