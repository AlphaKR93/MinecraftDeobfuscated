/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ServerboundSeenAdvancementsPacket
implements Packet<ServerGamePacketListener> {
    private final Action action;
    @Nullable
    private final ResourceLocation tab;

    public ServerboundSeenAdvancementsPacket(Action $$0, @Nullable ResourceLocation $$1) {
        this.action = $$0;
        this.tab = $$1;
    }

    public static ServerboundSeenAdvancementsPacket openedTab(Advancement $$0) {
        return new ServerboundSeenAdvancementsPacket(Action.OPENED_TAB, $$0.getId());
    }

    public static ServerboundSeenAdvancementsPacket closedScreen() {
        return new ServerboundSeenAdvancementsPacket(Action.CLOSED_SCREEN, null);
    }

    public ServerboundSeenAdvancementsPacket(FriendlyByteBuf $$0) {
        this.action = $$0.readEnum(Action.class);
        this.tab = this.action == Action.OPENED_TAB ? $$0.readResourceLocation() : null;
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.action);
        if (this.action == Action.OPENED_TAB) {
            $$0.writeResourceLocation(this.tab);
        }
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSeenAdvancements(this);
    }

    public Action getAction() {
        return this.action;
    }

    @Nullable
    public ResourceLocation getTab() {
        return this.tab;
    }

    public static enum Action {
        OPENED_TAB,
        CLOSED_SCREEN;

    }
}