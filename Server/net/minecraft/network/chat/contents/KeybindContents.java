/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat.contents;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.KeybindResolver;

public class KeybindContents
implements ComponentContents {
    private final String name;
    @Nullable
    private Supplier<Component> nameResolver;

    public KeybindContents(String $$0) {
        this.name = $$0;
    }

    private Component getNestedComponent() {
        if (this.nameResolver == null) {
            this.nameResolver = (Supplier)KeybindResolver.keyResolver.apply((Object)this.name);
        }
        return (Component)this.nameResolver.get();
    }

    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> $$0) {
        return this.getNestedComponent().visit($$0);
    }

    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> $$0, Style $$1) {
        return this.getNestedComponent().visit($$0, $$1);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof KeybindContents)) return false;
        KeybindContents $$1 = (KeybindContents)$$0;
        if (!this.name.equals((Object)$$1.name)) return false;
        return true;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return "keybind{" + this.name + "}";
    }

    public String getName() {
        return this.name;
    }
}