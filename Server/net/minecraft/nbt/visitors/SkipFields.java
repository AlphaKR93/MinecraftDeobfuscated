/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayDeque
 *  java.util.Deque
 */
package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.visitors.CollectToTag;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.FieldTree;

public class SkipFields
extends CollectToTag {
    private final Deque<FieldTree> stack = new ArrayDeque();

    public SkipFields(FieldSelector ... $$0) {
        FieldTree $$1 = FieldTree.createRoot();
        for (FieldSelector $$2 : $$0) {
            $$1.addEntry($$2);
        }
        this.stack.push((Object)$$1);
    }

    @Override
    public StreamTagVisitor.EntryResult visitEntry(TagType<?> $$0, String $$1) {
        FieldTree $$3;
        FieldTree $$2 = (FieldTree)((Object)this.stack.element());
        if ($$2.isSelected($$0, $$1)) {
            return StreamTagVisitor.EntryResult.SKIP;
        }
        if ($$0 == CompoundTag.TYPE && ($$3 = (FieldTree)((Object)$$2.fieldsToRecurse().get((Object)$$1))) != null) {
            this.stack.push((Object)$$3);
        }
        return super.visitEntry($$0, $$1);
    }

    @Override
    public StreamTagVisitor.ValueResult visitContainerEnd() {
        if (this.depth() == ((FieldTree)((Object)this.stack.element())).depth()) {
            this.stack.pop();
        }
        return super.visitContainerEnd();
    }
}