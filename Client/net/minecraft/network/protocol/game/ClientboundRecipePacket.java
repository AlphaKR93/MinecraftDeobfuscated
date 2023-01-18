/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBookSettings;

public class ClientboundRecipePacket
implements Packet<ClientGamePacketListener> {
    private final State state;
    private final List<ResourceLocation> recipes;
    private final List<ResourceLocation> toHighlight;
    private final RecipeBookSettings bookSettings;

    public ClientboundRecipePacket(State $$0, Collection<ResourceLocation> $$1, Collection<ResourceLocation> $$2, RecipeBookSettings $$3) {
        this.state = $$0;
        this.recipes = ImmutableList.copyOf($$1);
        this.toHighlight = ImmutableList.copyOf($$2);
        this.bookSettings = $$3;
    }

    public ClientboundRecipePacket(FriendlyByteBuf $$0) {
        this.state = $$0.readEnum(State.class);
        this.bookSettings = RecipeBookSettings.read($$0);
        this.recipes = $$0.readList(FriendlyByteBuf::readResourceLocation);
        this.toHighlight = this.state == State.INIT ? $$0.readList(FriendlyByteBuf::readResourceLocation) : ImmutableList.of();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.state);
        this.bookSettings.write($$0);
        $$0.writeCollection(this.recipes, FriendlyByteBuf::writeResourceLocation);
        if (this.state == State.INIT) {
            $$0.writeCollection(this.toHighlight, FriendlyByteBuf::writeResourceLocation);
        }
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleAddOrRemoveRecipes(this);
    }

    public List<ResourceLocation> getRecipes() {
        return this.recipes;
    }

    public List<ResourceLocation> getHighlights() {
        return this.toHighlight;
    }

    public RecipeBookSettings getBookSettings() {
        return this.bookSettings;
    }

    public State getState() {
        return this.state;
    }

    public static enum State {
        INIT,
        ADD,
        REMOVE;

    }
}