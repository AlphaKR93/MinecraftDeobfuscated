/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public record ServerboundClientInformationPacket(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand, boolean textFilteringEnabled, boolean allowsListing) implements Packet<ServerGamePacketListener>
{
    public static final int MAX_LANGUAGE_LENGTH = 16;

    public ServerboundClientInformationPacket(FriendlyByteBuf $$0) {
        this($$0.readUtf(16), $$0.readByte(), $$0.readEnum(ChatVisiblity.class), $$0.readBoolean(), $$0.readUnsignedByte(), $$0.readEnum(HumanoidArm.class), $$0.readBoolean(), $$0.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.language);
        $$0.writeByte(this.viewDistance);
        $$0.writeEnum(this.chatVisibility);
        $$0.writeBoolean(this.chatColors);
        $$0.writeByte(this.modelCustomisation);
        $$0.writeEnum(this.mainHand);
        $$0.writeBoolean(this.textFilteringEnabled);
        $$0.writeBoolean(this.allowsListing);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleClientInformation(this);
    }
}