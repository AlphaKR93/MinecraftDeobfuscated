/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record ClientboundLoginPacket(int playerId, boolean hardcore, GameType gameType, @Nullable GameType previousGameType, Set<ResourceKey<Level>> levels, RegistryAccess.Frozen registryHolder, ResourceKey<DimensionType> dimensionType, ResourceKey<Level> dimension, long seed, int maxPlayers, int chunkRadius, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean isDebug, boolean isFlat, Optional<GlobalPos> lastDeathLocation) implements Packet<ClientGamePacketListener>
{
    public ClientboundLoginPacket(FriendlyByteBuf $$02) {
        this($$02.readInt(), $$02.readBoolean(), GameType.byId($$02.readByte()), GameType.byNullableId($$02.readByte()), (Set<ResourceKey<Level>>)((Set)$$02.readCollection(Sets::newHashSetWithExpectedSize, $$0 -> $$0.readResourceKey(Registries.DIMENSION))), $$02.readWithCodec(RegistrySynchronization.NETWORK_CODEC).freeze(), $$02.readResourceKey(Registries.DIMENSION_TYPE), $$02.readResourceKey(Registries.DIMENSION), $$02.readLong(), $$02.readVarInt(), $$02.readVarInt(), $$02.readVarInt(), $$02.readBoolean(), $$02.readBoolean(), $$02.readBoolean(), $$02.readBoolean(), $$02.readOptional(FriendlyByteBuf::readGlobalPos));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.playerId);
        $$0.writeBoolean(this.hardcore);
        $$0.writeByte(this.gameType.getId());
        $$0.writeByte(GameType.getNullableId(this.previousGameType));
        $$0.writeCollection(this.levels, FriendlyByteBuf::writeResourceKey);
        $$0.writeWithCodec(RegistrySynchronization.NETWORK_CODEC, this.registryHolder);
        $$0.writeResourceKey(this.dimensionType);
        $$0.writeResourceKey(this.dimension);
        $$0.writeLong(this.seed);
        $$0.writeVarInt(this.maxPlayers);
        $$0.writeVarInt(this.chunkRadius);
        $$0.writeVarInt(this.simulationDistance);
        $$0.writeBoolean(this.reducedDebugInfo);
        $$0.writeBoolean(this.showDeathScreen);
        $$0.writeBoolean(this.isDebug);
        $$0.writeBoolean(this.isFlat);
        $$0.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleLogin(this);
    }
}