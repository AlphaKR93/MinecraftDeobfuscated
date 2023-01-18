/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Iterator
 */
package net.minecraft.recipebook;

import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

public interface PlaceRecipe<T> {
    default public void placeRecipe(int $$0, int $$1, int $$2, Recipe<?> $$3, Iterator<T> $$4, int $$5) {
        int $$6 = $$0;
        int $$7 = $$1;
        if ($$3 instanceof ShapedRecipe) {
            ShapedRecipe $$8 = (ShapedRecipe)$$3;
            $$6 = $$8.getWidth();
            $$7 = $$8.getHeight();
        }
        int $$9 = 0;
        block0: for (int $$10 = 0; $$10 < $$1; ++$$10) {
            if ($$9 == $$2) {
                ++$$9;
            }
            boolean $$11 = (float)$$7 < (float)$$1 / 2.0f;
            int $$12 = Mth.floor((float)$$1 / 2.0f - (float)$$7 / 2.0f);
            if ($$11 && $$12 > $$10) {
                $$9 += $$0;
                ++$$10;
            }
            for (int $$13 = 0; $$13 < $$0; ++$$13) {
                boolean $$15;
                if (!$$4.hasNext()) {
                    return;
                }
                $$11 = (float)$$6 < (float)$$0 / 2.0f;
                $$12 = Mth.floor((float)$$0 / 2.0f - (float)$$6 / 2.0f);
                int $$14 = $$6;
                boolean bl = $$15 = $$13 < $$6;
                if ($$11) {
                    $$14 = $$12 + $$6;
                    boolean bl2 = $$15 = $$12 <= $$13 && $$13 < $$12 + $$6;
                }
                if ($$15) {
                    this.addItemToSlot($$4, $$9, $$5, $$10, $$13);
                } else if ($$14 == $$13) {
                    $$9 += $$0 - $$13;
                    continue block0;
                }
                ++$$9;
            }
        }
    }

    public void addItemToSlot(Iterator<T> var1, int var2, int var3, int var4, int var5);
}