/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Predicate
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;
import net.minecraft.util.datafix.fixes.ItemStackTagFix;

public class FilteredBooksFix
extends ItemStackTagFix {
    public FilteredBooksFix(Schema $$02) {
        super($$02, "Remove filtered text from books", (Predicate<String>)((Predicate)$$0 -> $$0.equals((Object)"minecraft:writable_book") || $$0.equals((Object)"minecraft:written_book")));
    }

    @Override
    protected <T> Dynamic<T> fixItemStackTag(Dynamic<T> $$0) {
        return $$0.remove("filtered_title").remove("filtered_pages");
    }
}