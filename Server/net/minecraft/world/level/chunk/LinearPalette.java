/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.MissingPaletteEntryException;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;
import org.apache.commons.lang3.Validate;

public class LinearPalette<T>
implements Palette<T> {
    private final IdMap<T> registry;
    private final T[] values;
    private final PaletteResize<T> resizeHandler;
    private final int bits;
    private int size;

    private LinearPalette(IdMap<T> $$0, int $$1, PaletteResize<T> $$2, List<T> $$3) {
        this.registry = $$0;
        this.values = new Object[1 << $$1];
        this.bits = $$1;
        this.resizeHandler = $$2;
        Validate.isTrue(($$3.size() <= this.values.length ? 1 : 0) != 0, (String)"Can't initialize LinearPalette of size %d with %d entries", (Object[])new Object[]{this.values.length, $$3.size()});
        for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            this.values[$$4] = $$3.get($$4);
        }
        this.size = $$3.size();
    }

    private LinearPalette(IdMap<T> $$0, T[] $$1, PaletteResize<T> $$2, int $$3, int $$4) {
        this.registry = $$0;
        this.values = $$1;
        this.resizeHandler = $$2;
        this.bits = $$3;
        this.size = $$4;
    }

    public static <A> Palette<A> create(int $$0, IdMap<A> $$1, PaletteResize<A> $$2, List<A> $$3) {
        return new LinearPalette<A>($$1, $$0, $$2, $$3);
    }

    @Override
    public int idFor(T $$0) {
        int $$2;
        for (int $$1 = 0; $$1 < this.size; ++$$1) {
            if (this.values[$$1] != $$0) continue;
            return $$1;
        }
        if (($$2 = this.size++) < this.values.length) {
            this.values[$$2] = $$0;
            return $$2;
        }
        return this.resizeHandler.onResize(this.bits + 1, $$0);
    }

    @Override
    public boolean maybeHas(Predicate<T> $$0) {
        for (int $$1 = 0; $$1 < this.size; ++$$1) {
            if (!$$0.test(this.values[$$1])) continue;
            return true;
        }
        return false;
    }

    @Override
    public T valueFor(int $$0) {
        if ($$0 >= 0 && $$0 < this.size) {
            return this.values[$$0];
        }
        throw new MissingPaletteEntryException($$0);
    }

    @Override
    public void read(FriendlyByteBuf $$0) {
        this.size = $$0.readVarInt();
        for (int $$1 = 0; $$1 < this.size; ++$$1) {
            this.values[$$1] = this.registry.byIdOrThrow($$0.readVarInt());
        }
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.size);
        for (int $$1 = 0; $$1 < this.size; ++$$1) {
            $$0.writeVarInt(this.registry.getId(this.values[$$1]));
        }
    }

    @Override
    public int getSerializedSize() {
        int $$0 = FriendlyByteBuf.getVarIntSize(this.getSize());
        for (int $$1 = 0; $$1 < this.getSize(); ++$$1) {
            $$0 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values[$$1]));
        }
        return $$0;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public Palette<T> copy() {
        return new LinearPalette<Object>(this.registry, (Object[])this.values.clone(), this.resizeHandler, this.bits, this.size);
    }
}