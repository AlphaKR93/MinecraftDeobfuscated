/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

public class ClientboundAwardStatsPacket
implements Packet<ClientGamePacketListener> {
    private final Object2IntMap<Stat<?>> stats;

    public ClientboundAwardStatsPacket(Object2IntMap<Stat<?>> $$0) {
        this.stats = $$0;
    }

    public ClientboundAwardStatsPacket(FriendlyByteBuf $$0) {
        this.stats = (Object2IntMap)$$0.readMap(Object2IntOpenHashMap::new, $$1 -> {
            StatType<?> $$2 = $$1.readById(BuiltInRegistries.STAT_TYPE);
            return ClientboundAwardStatsPacket.readStatCap($$0, $$2);
        }, FriendlyByteBuf::readVarInt);
    }

    private static <T> Stat<T> readStatCap(FriendlyByteBuf $$0, StatType<T> $$1) {
        return $$1.get($$0.readById($$1.getRegistry()));
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleAwardStats(this);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeMap(this.stats, ClientboundAwardStatsPacket::writeStatCap, FriendlyByteBuf::writeVarInt);
    }

    private static <T> void writeStatCap(FriendlyByteBuf $$0, Stat<T> $$1) {
        $$0.writeId(BuiltInRegistries.STAT_TYPE, $$1.getType());
        $$0.writeId($$1.getType().getRegistry(), $$1.getValue());
    }

    public Map<Stat<?>, Integer> getStats() {
        return this.stats;
    }
}