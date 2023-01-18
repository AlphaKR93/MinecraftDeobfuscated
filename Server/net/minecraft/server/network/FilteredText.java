/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.server.network;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FilterMask;

public record FilteredText(String raw, FilterMask mask) {
    public static final FilteredText EMPTY = FilteredText.passThrough("");

    public static FilteredText passThrough(String $$0) {
        return new FilteredText($$0, FilterMask.PASS_THROUGH);
    }

    public static FilteredText fullyFiltered(String $$0) {
        return new FilteredText($$0, FilterMask.FULLY_FILTERED);
    }

    @Nullable
    public String filtered() {
        return this.mask.apply(this.raw);
    }

    public String filteredOrEmpty() {
        return (String)Objects.requireNonNullElse((Object)this.filtered(), (Object)"");
    }

    public boolean isFiltered() {
        return !this.mask.isEmpty();
    }
}