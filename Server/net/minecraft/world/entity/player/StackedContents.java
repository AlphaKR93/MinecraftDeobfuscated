/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntAVLTreeSet
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.BitSet
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class StackedContents {
    private static final int EMPTY = 0;
    public final Int2IntMap contents = new Int2IntOpenHashMap();

    public void accountSimpleStack(ItemStack $$0) {
        if (!($$0.isDamaged() || $$0.isEnchanted() || $$0.hasCustomHoverName())) {
            this.accountStack($$0);
        }
    }

    public void accountStack(ItemStack $$0) {
        this.accountStack($$0, 64);
    }

    public void accountStack(ItemStack $$0, int $$1) {
        if (!$$0.isEmpty()) {
            int $$2 = StackedContents.getStackingIndex($$0);
            int $$3 = Math.min((int)$$1, (int)$$0.getCount());
            this.put($$2, $$3);
        }
    }

    public static int getStackingIndex(ItemStack $$0) {
        return BuiltInRegistries.ITEM.getId($$0.getItem());
    }

    boolean has(int $$0) {
        return this.contents.get($$0) > 0;
    }

    int take(int $$0, int $$1) {
        int $$2 = this.contents.get($$0);
        if ($$2 >= $$1) {
            this.contents.put($$0, $$2 - $$1);
            return $$0;
        }
        return 0;
    }

    void put(int $$0, int $$1) {
        this.contents.put($$0, this.contents.get($$0) + $$1);
    }

    public boolean canCraft(Recipe<?> $$0, @Nullable IntList $$1) {
        return this.canCraft($$0, $$1, 1);
    }

    public boolean canCraft(Recipe<?> $$0, @Nullable IntList $$1, int $$2) {
        return new RecipePicker($$0).tryPick($$2, $$1);
    }

    public int getBiggestCraftableStack(Recipe<?> $$0, @Nullable IntList $$1) {
        return this.getBiggestCraftableStack($$0, Integer.MAX_VALUE, $$1);
    }

    public int getBiggestCraftableStack(Recipe<?> $$0, int $$1, @Nullable IntList $$2) {
        return new RecipePicker($$0).tryPickAll($$1, $$2);
    }

    public static ItemStack fromStackingIndex(int $$0) {
        if ($$0 == 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Item.byId($$0));
    }

    public void clear() {
        this.contents.clear();
    }

    class RecipePicker {
        private final Recipe<?> recipe;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final int ingredientCount;
        private final int[] items;
        private final int itemCount;
        private final BitSet data;
        private final IntList path = new IntArrayList();

        public RecipePicker(Recipe<?> $$0) {
            this.recipe = $$0;
            this.ingredients.addAll($$0.getIngredients());
            this.ingredients.removeIf(Ingredient::isEmpty);
            this.ingredientCount = this.ingredients.size();
            this.items = this.getUniqueAvailableIngredientItems();
            this.itemCount = this.items.length;
            this.data = new BitSet(this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);
            for (int $$1 = 0; $$1 < this.ingredients.size(); ++$$1) {
                IntList $$2 = ((Ingredient)this.ingredients.get($$1)).getStackingIds();
                for (int $$3 = 0; $$3 < this.itemCount; ++$$3) {
                    if (!$$2.contains(this.items[$$3])) continue;
                    this.data.set(this.getIndex(true, $$3, $$1));
                }
            }
        }

        public boolean tryPick(int $$0, @Nullable IntList $$1) {
            boolean $$6;
            if ($$0 <= 0) {
                return true;
            }
            int $$2 = 0;
            while (this.dfs($$0)) {
                StackedContents.this.take(this.items[this.path.getInt(0)], $$0);
                int $$3 = this.path.size() - 1;
                this.setSatisfied(this.path.getInt($$3));
                for (int $$4 = 0; $$4 < $$3; ++$$4) {
                    this.toggleResidual(($$4 & 1) == 0, this.path.get($$4), this.path.get($$4 + 1));
                }
                this.path.clear();
                this.data.clear(0, this.ingredientCount + this.itemCount);
                ++$$2;
            }
            boolean $$5 = $$2 == this.ingredientCount;
            boolean bl = $$6 = $$5 && $$1 != null;
            if ($$6) {
                $$1.clear();
            }
            this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
            int $$7 = 0;
            NonNullList<Ingredient> $$8 = this.recipe.getIngredients();
            for (int $$9 = 0; $$9 < $$8.size(); ++$$9) {
                if ($$6 && ((Ingredient)$$8.get($$9)).isEmpty()) {
                    $$1.add(0);
                    continue;
                }
                for (int $$10 = 0; $$10 < this.itemCount; ++$$10) {
                    if (!this.hasResidual(false, $$7, $$10)) continue;
                    this.toggleResidual(true, $$10, $$7);
                    StackedContents.this.put(this.items[$$10], $$0);
                    if (!$$6) continue;
                    $$1.add(this.items[$$10]);
                }
                ++$$7;
            }
            return $$5;
        }

        private int[] getUniqueAvailableIngredientItems() {
            IntAVLTreeSet $$0 = new IntAVLTreeSet();
            for (Ingredient $$1 : this.ingredients) {
                $$0.addAll((IntCollection)$$1.getStackingIds());
            }
            IntIterator $$2 = $$0.iterator();
            while ($$2.hasNext()) {
                if (StackedContents.this.has($$2.nextInt())) continue;
                $$2.remove();
            }
            return $$0.toIntArray();
        }

        private boolean dfs(int $$0) {
            int $$1 = this.itemCount;
            for (int $$2 = 0; $$2 < $$1; ++$$2) {
                if (StackedContents.this.contents.get(this.items[$$2]) < $$0) continue;
                this.visit(false, $$2);
                while (!this.path.isEmpty()) {
                    int $$8;
                    int $$3 = this.path.size();
                    boolean $$4 = ($$3 & 1) == 1;
                    int $$5 = this.path.getInt($$3 - 1);
                    if (!$$4 && !this.isSatisfied($$5)) break;
                    int $$6 = $$4 ? this.ingredientCount : $$1;
                    for (int $$7 = 0; $$7 < $$6; ++$$7) {
                        if (this.hasVisited($$4, $$7) || !this.hasConnection($$4, $$5, $$7) || !this.hasResidual($$4, $$5, $$7)) continue;
                        this.visit($$4, $$7);
                        break;
                    }
                    if (($$8 = this.path.size()) != $$3) continue;
                    this.path.removeInt($$8 - 1);
                }
                if (this.path.isEmpty()) continue;
                return true;
            }
            return false;
        }

        private boolean isSatisfied(int $$0) {
            return this.data.get(this.getSatisfiedIndex($$0));
        }

        private void setSatisfied(int $$0) {
            this.data.set(this.getSatisfiedIndex($$0));
        }

        private int getSatisfiedIndex(int $$0) {
            return this.ingredientCount + this.itemCount + $$0;
        }

        private boolean hasConnection(boolean $$0, int $$1, int $$2) {
            return this.data.get(this.getIndex($$0, $$1, $$2));
        }

        private boolean hasResidual(boolean $$0, int $$1, int $$2) {
            return $$0 != this.data.get(1 + this.getIndex($$0, $$1, $$2));
        }

        private void toggleResidual(boolean $$0, int $$1, int $$2) {
            this.data.flip(1 + this.getIndex($$0, $$1, $$2));
        }

        private int getIndex(boolean $$0, int $$1, int $$2) {
            int $$3 = $$0 ? $$1 * this.ingredientCount + $$2 : $$2 * this.ingredientCount + $$1;
            return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * $$3;
        }

        private void visit(boolean $$0, int $$1) {
            this.data.set(this.getVisitedIndex($$0, $$1));
            this.path.add($$1);
        }

        private boolean hasVisited(boolean $$0, int $$1) {
            return this.data.get(this.getVisitedIndex($$0, $$1));
        }

        private int getVisitedIndex(boolean $$0, int $$1) {
            return ($$0 ? 0 : this.ingredientCount) + $$1;
        }

        public int tryPickAll(int $$0, @Nullable IntList $$1) {
            int $$4;
            int $$2 = 0;
            int $$3 = Math.min((int)$$0, (int)this.getMinIngredientCount()) + 1;
            while (true) {
                if (this.tryPick($$4 = ($$2 + $$3) / 2, null)) {
                    if ($$3 - $$2 <= 1) break;
                    $$2 = $$4;
                    continue;
                }
                $$3 = $$4;
            }
            if ($$4 > 0) {
                this.tryPick($$4, $$1);
            }
            return $$4;
        }

        private int getMinIngredientCount() {
            int $$0 = Integer.MAX_VALUE;
            for (Ingredient $$1 : this.ingredients) {
                int $$2 = 0;
                IntListIterator intListIterator = $$1.getStackingIds().iterator();
                while (intListIterator.hasNext()) {
                    int $$3 = (Integer)intListIterator.next();
                    $$2 = Math.max((int)$$2, (int)StackedContents.this.contents.get($$3));
                }
                if ($$0 <= 0) continue;
                $$0 = Math.min((int)$$0, (int)$$2);
            }
            return $$0;
        }
    }
}