/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AdvancementRewards {
    public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
    private final int experience;
    private final ResourceLocation[] loot;
    private final ResourceLocation[] recipes;
    private final CommandFunction.CacheableFunction function;

    public AdvancementRewards(int $$0, ResourceLocation[] $$1, ResourceLocation[] $$2, CommandFunction.CacheableFunction $$3) {
        this.experience = $$0;
        this.loot = $$1;
        this.recipes = $$2;
        this.function = $$3;
    }

    public ResourceLocation[] getRecipes() {
        return this.recipes;
    }

    public void grant(ServerPlayer $$0) {
        $$0.giveExperiencePoints(this.experience);
        LootContext $$1 = new LootContext.Builder($$0.getLevel()).withParameter(LootContextParams.THIS_ENTITY, $$0).withParameter(LootContextParams.ORIGIN, $$0.position()).withRandom($$0.getRandom()).create(LootContextParamSets.ADVANCEMENT_REWARD);
        boolean $$22 = false;
        for (ResourceLocation $$3 : this.loot) {
            for (ItemStack $$4 : $$0.server.getLootTables().get($$3).getRandomItems($$1)) {
                if ($$0.addItem($$4)) {
                    $$0.level.playSound(null, $$0.getX(), $$0.getY(), $$0.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (($$0.getRandom().nextFloat() - $$0.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    $$22 = true;
                    continue;
                }
                ItemEntity $$5 = $$0.drop($$4, false);
                if ($$5 == null) continue;
                $$5.setNoPickUpDelay();
                $$5.setOwner($$0.getUUID());
            }
        }
        if ($$22) {
            $$0.containerMenu.broadcastChanges();
        }
        if (this.recipes.length > 0) {
            $$0.awardRecipesByKey(this.recipes);
        }
        MinecraftServer $$6 = $$0.server;
        this.function.get($$6.getFunctions()).ifPresent($$2 -> $$6.getFunctions().execute((CommandFunction)$$2, $$0.createCommandSourceStack().withSuppressedOutput().withPermission(2)));
    }

    public String toString() {
        return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString((Object[])this.loot) + ", recipes=" + Arrays.toString((Object[])this.recipes) + ", function=" + this.function + "}";
    }

    public JsonElement serializeToJson() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        if (this.experience != 0) {
            $$0.addProperty("experience", (Number)Integer.valueOf((int)this.experience));
        }
        if (this.loot.length > 0) {
            JsonArray $$1 = new JsonArray();
            for (ResourceLocation $$2 : this.loot) {
                $$1.add($$2.toString());
            }
            $$0.add("loot", (JsonElement)$$1);
        }
        if (this.recipes.length > 0) {
            JsonArray $$3 = new JsonArray();
            for (ResourceLocation $$4 : this.recipes) {
                $$3.add($$4.toString());
            }
            $$0.add("recipes", (JsonElement)$$3);
        }
        if (this.function.getId() != null) {
            $$0.addProperty("function", this.function.getId().toString());
        }
        return $$0;
    }

    public static AdvancementRewards deserialize(JsonObject $$0) throws JsonParseException {
        CommandFunction.CacheableFunction $$9;
        int $$1 = GsonHelper.getAsInt($$0, "experience", 0);
        JsonArray $$2 = GsonHelper.getAsJsonArray($$0, "loot", new JsonArray());
        ResourceLocation[] $$3 = new ResourceLocation[$$2.size()];
        for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
            $$3[$$4] = new ResourceLocation(GsonHelper.convertToString($$2.get($$4), "loot[" + $$4 + "]"));
        }
        JsonArray $$5 = GsonHelper.getAsJsonArray($$0, "recipes", new JsonArray());
        ResourceLocation[] $$6 = new ResourceLocation[$$5.size()];
        for (int $$7 = 0; $$7 < $$6.length; ++$$7) {
            $$6[$$7] = new ResourceLocation(GsonHelper.convertToString($$5.get($$7), "recipes[" + $$7 + "]"));
        }
        if ($$0.has("function")) {
            CommandFunction.CacheableFunction $$8 = new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString($$0, "function")));
        } else {
            $$9 = CommandFunction.CacheableFunction.NONE;
        }
        return new AdvancementRewards($$1, $$3, $$6, $$9);
    }

    public static class Builder {
        private int experience;
        private final List<ResourceLocation> loot = Lists.newArrayList();
        private final List<ResourceLocation> recipes = Lists.newArrayList();
        @Nullable
        private ResourceLocation function;

        public static Builder experience(int $$0) {
            return new Builder().addExperience($$0);
        }

        public Builder addExperience(int $$0) {
            this.experience += $$0;
            return this;
        }

        public static Builder loot(ResourceLocation $$0) {
            return new Builder().addLootTable($$0);
        }

        public Builder addLootTable(ResourceLocation $$0) {
            this.loot.add((Object)$$0);
            return this;
        }

        public static Builder recipe(ResourceLocation $$0) {
            return new Builder().addRecipe($$0);
        }

        public Builder addRecipe(ResourceLocation $$0) {
            this.recipes.add((Object)$$0);
            return this;
        }

        public static Builder function(ResourceLocation $$0) {
            return new Builder().runs($$0);
        }

        public Builder runs(ResourceLocation $$0) {
            this.function = $$0;
            return this;
        }

        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, (ResourceLocation[])this.loot.toArray((Object[])new ResourceLocation[0]), (ResourceLocation[])this.recipes.toArray((Object[])new ResourceLocation[0]), this.function == null ? CommandFunction.CacheableFunction.NONE : new CommandFunction.CacheableFunction(this.function));
        }
    }
}