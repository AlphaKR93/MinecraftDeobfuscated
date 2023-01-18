/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.narration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;

public class NarrationThunk<T> {
    private final T contents;
    private final BiConsumer<Consumer<String>, T> converter;
    public static final NarrationThunk<?> EMPTY = new NarrationThunk<Unit>(Unit.INSTANCE, ($$0, $$1) -> {});

    private NarrationThunk(T $$0, BiConsumer<Consumer<String>, T> $$1) {
        this.contents = $$0;
        this.converter = $$1;
    }

    public static NarrationThunk<?> from(String $$0) {
        return new NarrationThunk<String>($$0, Consumer::accept);
    }

    public static NarrationThunk<?> from(Component $$02) {
        return new NarrationThunk<Component>($$02, ($$0, $$1) -> $$0.accept((Object)$$1.getString()));
    }

    public static NarrationThunk<?> from(List<Component> $$0) {
        return new NarrationThunk<List<Component>>($$0, ($$1, $$2) -> $$0.stream().map(Component::getString).forEach($$1));
    }

    public void getText(Consumer<String> $$0) {
        this.converter.accept($$0, this.contents);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof NarrationThunk) {
            NarrationThunk $$1 = (NarrationThunk)$$0;
            return $$1.converter == this.converter && $$1.contents.equals(this.contents);
        }
        return false;
    }

    public int hashCode() {
        int $$0 = this.contents.hashCode();
        $$0 = 31 * $$0 + this.converter.hashCode();
        return $$0;
    }
}