/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 */
package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {
    private final List<SlotDefinition> slots;
    private final SlotDefinition resultSlot;

    ItemCombinerMenuSlotDefinition(List<SlotDefinition> $$0, SlotDefinition $$1) {
        if ($$0.isEmpty() || $$1.equals((Object)SlotDefinition.EMPTY)) {
            throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
        }
        this.slots = $$0;
        this.resultSlot = $$1;
    }

    public static Builder create() {
        return new Builder();
    }

    public boolean hasSlot(int $$0) {
        return this.slots.size() >= $$0;
    }

    public SlotDefinition getSlot(int $$0) {
        return (SlotDefinition)((Object)this.slots.get($$0));
    }

    public SlotDefinition getResultSlot() {
        return this.resultSlot;
    }

    public List<SlotDefinition> getSlots() {
        return this.slots;
    }

    public int getNumOfInputSlots() {
        return this.slots.size();
    }

    public int getResultSlotIndex() {
        return this.getNumOfInputSlots();
    }

    public List<Integer> getInputSlotIndexes() {
        return (List)this.slots.stream().map(SlotDefinition::slotIndex).collect(Collectors.toList());
    }

    public record SlotDefinition(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {
        static final SlotDefinition EMPTY = new SlotDefinition(0, 0, 0, (Predicate<ItemStack>)((Predicate)$$0 -> true));
    }

    public static class Builder {
        private final List<SlotDefinition> slots = new ArrayList();
        private SlotDefinition resultSlot = SlotDefinition.EMPTY;

        public Builder withSlot(int $$0, int $$1, int $$2, Predicate<ItemStack> $$3) {
            this.slots.add((Object)new SlotDefinition($$0, $$1, $$2, $$3));
            return this;
        }

        public Builder withResultSlot(int $$02, int $$1, int $$2) {
            this.resultSlot = new SlotDefinition($$02, $$1, $$2, (Predicate<ItemStack>)((Predicate)$$0 -> false));
            return this;
        }

        public ItemCombinerMenuSlotDefinition build() {
            return new ItemCombinerMenuSlotDefinition(this.slots, this.resultSlot);
        }
    }
}