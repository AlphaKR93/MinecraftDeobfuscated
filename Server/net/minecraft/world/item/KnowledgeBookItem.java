/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Optional
 *  org.slf4j.Logger
 */
package net.minecraft.world.item;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class KnowledgeBookItem
extends Item {
    private static final String RECIPE_TAG = "Recipes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public KnowledgeBookItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        CompoundTag $$4 = $$3.getTag();
        if (!$$1.getAbilities().instabuild) {
            $$1.setItemInHand($$2, ItemStack.EMPTY);
        }
        if ($$4 == null || !$$4.contains(RECIPE_TAG, 9)) {
            LOGGER.error("Tag not valid: {}", (Object)$$4);
            return InteractionResultHolder.fail($$3);
        }
        if (!$$0.isClientSide) {
            ListTag $$5 = $$4.getList(RECIPE_TAG, 8);
            ArrayList $$6 = Lists.newArrayList();
            RecipeManager $$7 = $$0.getServer().getRecipeManager();
            for (int $$8 = 0; $$8 < $$5.size(); ++$$8) {
                String $$9 = $$5.getString($$8);
                Optional<? extends Recipe<?>> $$10 = $$7.byKey(new ResourceLocation($$9));
                if (!$$10.isPresent()) {
                    LOGGER.error("Invalid recipe: {}", (Object)$$9);
                    return InteractionResultHolder.fail($$3);
                }
                $$6.add((Object)((Recipe)$$10.get()));
            }
            $$1.awardRecipes((Collection<Recipe<?>>)$$6);
            $$1.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess($$3, $$0.isClientSide());
    }
}