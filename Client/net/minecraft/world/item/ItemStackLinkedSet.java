/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet
 *  java.lang.Object
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackLinkedSet {
    private static final Hash.Strategy<? super ItemStack> TYPE_AND_TAG = new Hash.Strategy<ItemStack>(){

        public int hashCode(@Nullable ItemStack $$0) {
            return ItemStackLinkedSet.hashStackAndTag($$0);
        }

        public boolean equals(@Nullable ItemStack $$0, @Nullable ItemStack $$1) {
            return $$0 == $$1 || $$0 != null && $$1 != null && $$0.isEmpty() == $$1.isEmpty() && ItemStack.isSameItemSameTags($$0, $$1);
        }
    };

    static int hashStackAndTag(@Nullable ItemStack $$0) {
        if ($$0 != null) {
            CompoundTag $$1 = $$0.getTag();
            int $$2 = 31 + $$0.getItem().hashCode();
            return 31 * $$2 + ($$1 == null ? 0 : $$1.hashCode());
        }
        return 0;
    }

    public static Set<ItemStack> createTypeAndTagSet() {
        return new ObjectLinkedOpenCustomHashSet(TYPE_AND_TAG);
    }
}