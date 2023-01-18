/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 */
package net.minecraft.nbt.visitors;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.visitors.FieldSelector;

public record FieldTree(int depth, Map<String, TagType<?>> selectedFields, Map<String, FieldTree> fieldsToRecurse) {
    private FieldTree(int $$0) {
        this($$0, (Map<String, TagType<?>>)new HashMap(), (Map<String, FieldTree>)new HashMap());
    }

    public static FieldTree createRoot() {
        return new FieldTree(1);
    }

    public void addEntry(FieldSelector $$02) {
        if (this.depth <= $$02.path().size()) {
            ((FieldTree)((Object)this.fieldsToRecurse.computeIfAbsent((Object)((String)$$02.path().get(this.depth - 1)), $$0 -> new FieldTree(this.depth + 1)))).addEntry($$02);
        } else {
            this.selectedFields.put((Object)$$02.name(), $$02.type());
        }
    }

    public boolean isSelected(TagType<?> $$0, String $$1) {
        return $$0.equals(this.selectedFields().get((Object)$$1));
    }
}