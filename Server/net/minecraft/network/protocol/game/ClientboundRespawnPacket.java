/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket
implements Packet<ClientGamePacketListener> {
    public static final byte KEEP_ATTRIBUTES = 1;
    public static final byte KEEP_ENTITY_DATA = 2;
    public static final byte KEEP_ALL_DATA = 3;
    private final ResourceKey<DimensionType> dimensionType;
    private final ResourceKey<Level> dimension;
    private final long seed;
    private final GameType playerGameType;
    @Nullable
    private final GameType previousPlayerGameType;
    private final boolean isDebug;
    private final boolean isFlat;
    private final byte dataToKeep;
    private final Optional<GlobalPos> lastDeathLocation;

    public ClientboundRespawnPacket(ResourceKey<DimensionType> $$0, ResourceKey<Level> $$1, long $$2, GameType $$3, @Nullable GameType $$4, boolean $$5, boolean $$6, byte $$7, Optional<GlobalPos> $$8) {
        this.dimensionType = $$0;
        this.dimension = $$1;
        this.seed = $$2;
        this.playerGameType = $$3;
        this.previousPlayerGameType = $$4;
        this.isDebug = $$5;
        this.isFlat = $$6;
        this.dataToKeep = $$7;
        this.lastDeathLocation = $$8;
    }

    public ClientboundRespawnPacket(FriendlyByteBuf $$0) {
        this.dimensionType = $$0.readResourceKey(Registries.DIMENSION_TYPE);
        this.dimension = $$0.readResourceKey(Registries.DIMENSION);
        this.seed = $$0.readLong();
        this.playerGameType = GameType.byId($$0.readUnsignedByte());
        this.previousPlayerGameType = GameType.byNullableId($$0.readByte());
        this.isDebug = $$0.readBoolean();
        this.isFlat = $$0.readBoolean();
        this.dataToKeep = $$0.readByte();
        this.lastDeathLocation = $$0.readOptional(FriendlyByteBuf::readGlobalPos);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeResourceKey(this.dimensionType);
        $$0.writeResourceKey(this.dimension);
        $$0.writeLong(this.seed);
        $$0.writeByte(this.playerGameType.getId());
        $$0.writeByte(GameType.getNullableId(this.previousPlayerGameType));
        $$0.writeBoolean(this.isDebug);
        $$0.writeBoolean(this.isFlat);
        $$0.writeByte(this.dataToKeep);
        $$0.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleRespawn(this);
    }

    public ResourceKey<DimensionType> getDimensionType() {
        return this.dimensionType;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public long getSeed() {
        return this.seed;
    }

    public GameType getPlayerGameType() {
        return this.playerGameType;
    }

    @Nullable
    public GameType getPreviousPlayerGameType() {
        return this.previousPlayerGameType;
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public boolean isFlat() {
        return this.isFlat;
    }

    public boolean shouldKeep(byte $$0) {
        return (this.dataToKeep & $$0) != 0;
    }

    public Optional<GlobalPos> getLastDeathLocation() {
        return this.lastDeathLocation;
    }
}