/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.effect.MobEffect;

public class ServerboundSetBeaconPacket
implements Packet<ServerGamePacketListener> {
    private final Optional<MobEffect> primary;
    private final Optional<MobEffect> secondary;

    public ServerboundSetBeaconPacket(Optional<MobEffect> $$0, Optional<MobEffect> $$1) {
        this.primary = $$0;
        this.secondary = $$1;
    }

    public ServerboundSetBeaconPacket(FriendlyByteBuf $$02) {
        this.primary = $$02.readOptional($$0 -> $$0.readById(BuiltInRegistries.MOB_EFFECT));
        this.secondary = $$02.readOptional($$0 -> $$0.readById(BuiltInRegistries.MOB_EFFECT));
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeOptional(this.primary, ($$0, $$1) -> $$0.writeId(BuiltInRegistries.MOB_EFFECT, $$1));
        $$02.writeOptional(this.secondary, ($$0, $$1) -> $$0.writeId(BuiltInRegistries.MOB_EFFECT, $$1));
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSetBeaconPacket(this);
    }

    public Optional<MobEffect> getPrimary() {
        return this.primary;
    }

    public Optional<MobEffect> getSecondary() {
        return this.secondary;
    }
}