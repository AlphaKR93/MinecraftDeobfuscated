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
import net.minecraft.world.inventory.RecipeBookType;

public class ServerboundRecipeBookChangeSettingsPacket
implements Packet<ServerGamePacketListener> {
    private final RecipeBookType bookType;
    private final boolean isOpen;
    private final boolean isFiltering;

    public ServerboundRecipeBookChangeSettingsPacket(RecipeBookType $$0, boolean $$1, boolean $$2) {
        this.bookType = $$0;
        this.isOpen = $$1;
        this.isFiltering = $$2;
    }

    public ServerboundRecipeBookChangeSettingsPacket(FriendlyByteBuf $$0) {
        this.bookType = $$0.readEnum(RecipeBookType.class);
        this.isOpen = $$0.readBoolean();
        this.isFiltering = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.bookType);
        $$0.writeBoolean(this.isOpen);
        $$0.writeBoolean(this.isFiltering);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleRecipeBookChangeSettingsPacket(this);
    }

    public RecipeBookType getBookType() {
        return this.bookType;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean isFiltering() {
        return this.isFiltering;
    }
}