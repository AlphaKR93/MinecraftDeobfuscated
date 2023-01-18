/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Consumer
 *  org.slf4j.Logger
 */
package net.minecraft.stats;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

public class ServerRecipeBook
extends RecipeBook {
    public static final String RECIPE_BOOK_TAG = "recipeBook";
    private static final Logger LOGGER = LogUtils.getLogger();

    public int addRecipes(Collection<Recipe<?>> $$0, ServerPlayer $$1) {
        ArrayList $$2 = Lists.newArrayList();
        int $$3 = 0;
        for (Recipe $$4 : $$0) {
            ResourceLocation $$5 = $$4.getId();
            if (this.known.contains((Object)$$5) || $$4.isSpecial()) continue;
            this.add($$5);
            this.addHighlight($$5);
            $$2.add((Object)$$5);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger($$1, $$4);
            ++$$3;
        }
        this.sendRecipes(ClientboundRecipePacket.State.ADD, $$1, (List<ResourceLocation>)$$2);
        return $$3;
    }

    public int removeRecipes(Collection<Recipe<?>> $$0, ServerPlayer $$1) {
        ArrayList $$2 = Lists.newArrayList();
        int $$3 = 0;
        for (Recipe $$4 : $$0) {
            ResourceLocation $$5 = $$4.getId();
            if (!this.known.contains((Object)$$5)) continue;
            this.remove($$5);
            $$2.add((Object)$$5);
            ++$$3;
        }
        this.sendRecipes(ClientboundRecipePacket.State.REMOVE, $$1, (List<ResourceLocation>)$$2);
        return $$3;
    }

    private void sendRecipes(ClientboundRecipePacket.State $$0, ServerPlayer $$1, List<ResourceLocation> $$2) {
        $$1.connection.send(new ClientboundRecipePacket($$0, (Collection<ResourceLocation>)$$2, (Collection<ResourceLocation>)Collections.emptyList(), this.getBookSettings()));
    }

    public CompoundTag toNbt() {
        CompoundTag $$0 = new CompoundTag();
        this.getBookSettings().write($$0);
        ListTag $$1 = new ListTag();
        for (ResourceLocation $$2 : this.known) {
            $$1.add(StringTag.valueOf($$2.toString()));
        }
        $$0.put("recipes", $$1);
        ListTag $$3 = new ListTag();
        for (ResourceLocation $$4 : this.highlight) {
            $$3.add(StringTag.valueOf($$4.toString()));
        }
        $$0.put("toBeDisplayed", $$3);
        return $$0;
    }

    public void fromNbt(CompoundTag $$0, RecipeManager $$1) {
        this.setBookSettings(RecipeBookSettings.read($$0));
        ListTag $$2 = $$0.getList("recipes", 8);
        this.loadRecipes($$2, this::add, $$1);
        ListTag $$3 = $$0.getList("toBeDisplayed", 8);
        this.loadRecipes($$3, this::addHighlight, $$1);
    }

    private void loadRecipes(ListTag $$0, Consumer<Recipe<?>> $$1, RecipeManager $$2) {
        for (int $$3 = 0; $$3 < $$0.size(); ++$$3) {
            String $$4 = $$0.getString($$3);
            try {
                ResourceLocation $$5 = new ResourceLocation($$4);
                Optional<? extends Recipe<?>> $$6 = $$2.byKey($$5);
                if (!$$6.isPresent()) {
                    LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)$$5);
                    continue;
                }
                $$1.accept((Object)((Recipe)$$6.get()));
                continue;
            }
            catch (ResourceLocationException $$7) {
                LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)$$4);
            }
        }
    }

    public void sendInitialRecipeBook(ServerPlayer $$0) {
        $$0.connection.send(new ClientboundRecipePacket(ClientboundRecipePacket.State.INIT, (Collection<ResourceLocation>)this.known, (Collection<ResourceLocation>)this.highlight, this.getBookSettings()));
    }
}