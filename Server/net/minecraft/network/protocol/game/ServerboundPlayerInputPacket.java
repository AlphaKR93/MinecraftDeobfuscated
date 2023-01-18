/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundPlayerInputPacket
implements Packet<ServerGamePacketListener> {
    private static final int FLAG_JUMPING = 1;
    private static final int FLAG_SHIFT_KEY_DOWN = 2;
    private final float xxa;
    private final float zza;
    private final boolean isJumping;
    private final boolean isShiftKeyDown;

    public ServerboundPlayerInputPacket(float $$0, float $$1, boolean $$2, boolean $$3) {
        this.xxa = $$0;
        this.zza = $$1;
        this.isJumping = $$2;
        this.isShiftKeyDown = $$3;
    }

    public ServerboundPlayerInputPacket(FriendlyByteBuf $$0) {
        this.xxa = $$0.readFloat();
        this.zza = $$0.readFloat();
        byte $$1 = $$0.readByte();
        this.isJumping = ($$1 & 1) > 0;
        this.isShiftKeyDown = ($$1 & 2) > 0;
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeFloat(this.xxa);
        $$0.writeFloat(this.zza);
        byte $$1 = 0;
        if (this.isJumping) {
            $$1 = (byte)($$1 | 1);
        }
        if (this.isShiftKeyDown) {
            $$1 = (byte)($$1 | 2);
        }
        $$0.writeByte($$1);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePlayerInput(this);
    }

    public float getXxa() {
        return this.xxa;
    }

    public float getZza() {
        return this.zza;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public boolean isShiftKeyDown() {
        return this.isShiftKeyDown;
    }
}