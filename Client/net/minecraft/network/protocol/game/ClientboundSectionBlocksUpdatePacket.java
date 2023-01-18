/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.ShortIterator
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Short
 *  java.util.function.BiConsumer
 */
package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class ClientboundSectionBlocksUpdatePacket
implements Packet<ClientGamePacketListener> {
    private static final int POS_IN_SECTION_BITS = 12;
    private final SectionPos sectionPos;
    private final short[] positions;
    private final BlockState[] states;
    private final boolean suppressLightUpdates;

    public ClientboundSectionBlocksUpdatePacket(SectionPos $$0, ShortSet $$1, LevelChunkSection $$2, boolean $$3) {
        this.sectionPos = $$0;
        this.suppressLightUpdates = $$3;
        int $$4 = $$1.size();
        this.positions = new short[$$4];
        this.states = new BlockState[$$4];
        int $$5 = 0;
        ShortIterator shortIterator = $$1.iterator();
        while (shortIterator.hasNext()) {
            short $$6;
            this.positions[$$5] = $$6 = ((Short)shortIterator.next()).shortValue();
            this.states[$$5] = $$2.getBlockState(SectionPos.sectionRelativeX($$6), SectionPos.sectionRelativeY($$6), SectionPos.sectionRelativeZ($$6));
            ++$$5;
        }
    }

    public ClientboundSectionBlocksUpdatePacket(FriendlyByteBuf $$0) {
        this.sectionPos = SectionPos.of($$0.readLong());
        this.suppressLightUpdates = $$0.readBoolean();
        int $$1 = $$0.readVarInt();
        this.positions = new short[$$1];
        this.states = new BlockState[$$1];
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            long $$3 = $$0.readVarLong();
            this.positions[$$2] = (short)($$3 & 0xFFFL);
            this.states[$$2] = Block.BLOCK_STATE_REGISTRY.byId((int)($$3 >>> 12));
        }
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeLong(this.sectionPos.asLong());
        $$0.writeBoolean(this.suppressLightUpdates);
        $$0.writeVarInt(this.positions.length);
        for (int $$1 = 0; $$1 < this.positions.length; ++$$1) {
            $$0.writeVarLong((long)Block.getId(this.states[$$1]) << 12 | (long)this.positions[$$1]);
        }
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleChunkBlocksUpdate(this);
    }

    public void runUpdates(BiConsumer<BlockPos, BlockState> $$0) {
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        for (int $$2 = 0; $$2 < this.positions.length; ++$$2) {
            short $$3 = this.positions[$$2];
            $$1.set(this.sectionPos.relativeToBlockX($$3), this.sectionPos.relativeToBlockY($$3), this.sectionPos.relativeToBlockZ($$3));
            $$0.accept((Object)$$1, (Object)this.states[$$2]);
        }
    }

    public boolean shouldSuppressLightUpdates() {
        return this.suppressLightUpdates;
    }
}