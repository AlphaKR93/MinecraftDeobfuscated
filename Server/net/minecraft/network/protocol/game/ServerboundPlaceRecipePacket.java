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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundPlaceRecipePacket
implements Packet<ServerGamePacketListener> {
    private final int containerId;
    private final ResourceLocation recipe;
    private final boolean shiftDown;

    public ServerboundPlaceRecipePacket(int $$0, Recipe<?> $$1, boolean $$2) {
        this.containerId = $$0;
        this.recipe = $$1.getId();
        this.shiftDown = $$2;
    }

    public ServerboundPlaceRecipePacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readByte();
        this.recipe = $$0.readResourceLocation();
        this.shiftDown = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeResourceLocation(this.recipe);
        $$0.writeBoolean(this.shiftDown);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePlaceRecipe(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }

    public boolean isShiftDown() {
        return this.shiftDown;
    }
}