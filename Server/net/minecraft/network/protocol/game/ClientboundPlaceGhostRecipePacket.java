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
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ClientboundPlaceGhostRecipePacket
implements Packet<ClientGamePacketListener> {
    private final int containerId;
    private final ResourceLocation recipe;

    public ClientboundPlaceGhostRecipePacket(int $$0, Recipe<?> $$1) {
        this.containerId = $$0;
        this.recipe = $$1.getId();
    }

    public ClientboundPlaceGhostRecipePacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readByte();
        this.recipe = $$0.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeResourceLocation(this.recipe);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlaceRecipe(this);
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }

    public int getContainerId() {
        return this.containerId;
    }
}