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

public class ServerboundRecipeBookSeenRecipePacket
implements Packet<ServerGamePacketListener> {
    private final ResourceLocation recipe;

    public ServerboundRecipeBookSeenRecipePacket(Recipe<?> $$0) {
        this.recipe = $$0.getId();
    }

    public ServerboundRecipeBookSeenRecipePacket(FriendlyByteBuf $$0) {
        this.recipe = $$0.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeResourceLocation(this.recipe);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleRecipeBookSeenRecipePacket(this);
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }
}