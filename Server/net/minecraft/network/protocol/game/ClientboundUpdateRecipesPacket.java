/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ClientboundUpdateRecipesPacket
implements Packet<ClientGamePacketListener> {
    private final List<Recipe<?>> recipes;

    public ClientboundUpdateRecipesPacket(Collection<Recipe<?>> $$0) {
        this.recipes = Lists.newArrayList($$0);
    }

    public ClientboundUpdateRecipesPacket(FriendlyByteBuf $$0) {
        this.recipes = $$0.readList(ClientboundUpdateRecipesPacket::fromNetwork);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeCollection(this.recipes, ClientboundUpdateRecipesPacket::toNetwork);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleUpdateRecipes(this);
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public static Recipe<?> fromNetwork(FriendlyByteBuf $$0) {
        ResourceLocation $$1 = $$0.readResourceLocation();
        ResourceLocation $$2 = $$0.readResourceLocation();
        return ((RecipeSerializer)BuiltInRegistries.RECIPE_SERIALIZER.getOptional($$1).orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + $$1))).fromNetwork($$2, $$0);
    }

    public static <T extends Recipe<?>> void toNetwork(FriendlyByteBuf $$0, T $$1) {
        $$0.writeResourceLocation(BuiltInRegistries.RECIPE_SERIALIZER.getKey($$1.getSerializer()));
        $$0.writeResourceLocation($$1.getId());
        $$1.getSerializer().toNetwork($$0, $$1);
    }
}