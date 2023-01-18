/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.util.BitSet
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacketData {
    private final BitSet skyYMask;
    private final BitSet blockYMask;
    private final BitSet emptySkyYMask;
    private final BitSet emptyBlockYMask;
    private final List<byte[]> skyUpdates;
    private final List<byte[]> blockUpdates;
    private final boolean trustEdges;

    public ClientboundLightUpdatePacketData(ChunkPos $$0, LevelLightEngine $$1, @Nullable BitSet $$2, @Nullable BitSet $$3, boolean $$4) {
        this.trustEdges = $$4;
        this.skyYMask = new BitSet();
        this.blockYMask = new BitSet();
        this.emptySkyYMask = new BitSet();
        this.emptyBlockYMask = new BitSet();
        this.skyUpdates = Lists.newArrayList();
        this.blockUpdates = Lists.newArrayList();
        for (int $$5 = 0; $$5 < $$1.getLightSectionCount(); ++$$5) {
            if ($$2 == null || $$2.get($$5)) {
                this.prepareSectionData($$0, $$1, LightLayer.SKY, $$5, this.skyYMask, this.emptySkyYMask, this.skyUpdates);
            }
            if ($$3 != null && !$$3.get($$5)) continue;
            this.prepareSectionData($$0, $$1, LightLayer.BLOCK, $$5, this.blockYMask, this.emptyBlockYMask, this.blockUpdates);
        }
    }

    public ClientboundLightUpdatePacketData(FriendlyByteBuf $$02, int $$1, int $$2) {
        this.trustEdges = $$02.readBoolean();
        this.skyYMask = $$02.readBitSet();
        this.blockYMask = $$02.readBitSet();
        this.emptySkyYMask = $$02.readBitSet();
        this.emptyBlockYMask = $$02.readBitSet();
        this.skyUpdates = $$02.readList($$0 -> $$0.readByteArray(2048));
        this.blockUpdates = $$02.readList($$0 -> $$0.readByteArray(2048));
    }

    public void write(FriendlyByteBuf $$0) {
        $$0.writeBoolean(this.trustEdges);
        $$0.writeBitSet(this.skyYMask);
        $$0.writeBitSet(this.blockYMask);
        $$0.writeBitSet(this.emptySkyYMask);
        $$0.writeBitSet(this.emptyBlockYMask);
        $$0.writeCollection(this.skyUpdates, FriendlyByteBuf::writeByteArray);
        $$0.writeCollection(this.blockUpdates, FriendlyByteBuf::writeByteArray);
    }

    private void prepareSectionData(ChunkPos $$0, LevelLightEngine $$1, LightLayer $$2, int $$3, BitSet $$4, BitSet $$5, List<byte[]> $$6) {
        DataLayer $$7 = $$1.getLayerListener($$2).getDataLayerData(SectionPos.of($$0, $$1.getMinLightSection() + $$3));
        if ($$7 != null) {
            if ($$7.isEmpty()) {
                $$5.set($$3);
            } else {
                $$4.set($$3);
                $$6.add((Object)((byte[])$$7.getData().clone()));
            }
        }
    }

    public BitSet getSkyYMask() {
        return this.skyYMask;
    }

    public BitSet getEmptySkyYMask() {
        return this.emptySkyYMask;
    }

    public List<byte[]> getSkyUpdates() {
        return this.skyUpdates;
    }

    public BitSet getBlockYMask() {
        return this.blockYMask;
    }

    public BitSet getEmptyBlockYMask() {
        return this.emptyBlockYMask;
    }

    public List<byte[]> getBlockUpdates() {
        return this.blockUpdates;
    }

    public boolean getTrustEdges() {
        return this.trustEdges;
    }
}