/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 */
package net.minecraft.tags;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

public class TagBuilder {
    private final List<TagEntry> entries = new ArrayList();

    public static TagBuilder create() {
        return new TagBuilder();
    }

    public List<TagEntry> build() {
        return List.copyOf(this.entries);
    }

    public TagBuilder add(TagEntry $$0) {
        this.entries.add((Object)$$0);
        return this;
    }

    public TagBuilder addElement(ResourceLocation $$0) {
        return this.add(TagEntry.element($$0));
    }

    public TagBuilder addOptionalElement(ResourceLocation $$0) {
        return this.add(TagEntry.optionalElement($$0));
    }

    public TagBuilder addTag(ResourceLocation $$0) {
        return this.add(TagEntry.tag($$0));
    }

    public TagBuilder addOptionalTag(ResourceLocation $$0) {
        return this.add(TagEntry.optionalTag($$0));
    }
}