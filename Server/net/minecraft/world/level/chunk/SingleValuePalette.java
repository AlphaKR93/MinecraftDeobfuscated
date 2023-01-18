/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;
import org.apache.commons.lang3.Validate;

public class SingleValuePalette<T>
implements Palette<T> {
    private final IdMap<T> registry;
    @Nullable
    private T value;
    private final PaletteResize<T> resizeHandler;

    public SingleValuePalette(IdMap<T> $$0, PaletteResize<T> $$1, List<T> $$2) {
        this.registry = $$0;
        this.resizeHandler = $$1;
        if ($$2.size() > 0) {
            Validate.isTrue(($$2.size() <= 1 ? 1 : 0) != 0, (String)"Can't initialize SingleValuePalette with %d values.", (long)$$2.size());
            this.value = $$2.get(0);
        }
    }

    public static <A> Palette<A> create(int $$0, IdMap<A> $$1, PaletteResize<A> $$2, List<A> $$3) {
        return new SingleValuePalette<A>($$1, $$2, $$3);
    }

    @Override
    public int idFor(T $$0) {
        if (this.value == null || this.value == $$0) {
            this.value = $$0;
            return 0;
        }
        return this.resizeHandler.onResize(1, $$0);
    }

    @Override
    public boolean maybeHas(Predicate<T> $$0) {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        }
        return $$0.test(this.value);
    }

    @Override
    public T valueFor(int $$0) {
        if (this.value == null || $$0 != 0) {
            throw new IllegalStateException("Missing Palette entry for id " + $$0 + ".");
        }
        return this.value;
    }

    @Override
    public void read(FriendlyByteBuf $$0) {
        this.value = this.registry.byIdOrThrow($$0.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        }
        $$0.writeVarInt(this.registry.getId(this.value));
    }

    @Override
    public int getSerializedSize() {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        }
        return FriendlyByteBuf.getVarIntSize(this.registry.getId(this.value));
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public Palette<T> copy() {
        if (this.value == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        }
        return this;
    }
}