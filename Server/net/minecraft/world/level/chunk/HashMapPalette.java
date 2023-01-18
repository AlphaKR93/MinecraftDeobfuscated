/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.chunk.MissingPaletteEntryException;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

public class HashMapPalette<T>
implements Palette<T> {
    private final IdMap<T> registry;
    private final CrudeIncrementalIntIdentityHashBiMap<T> values;
    private final PaletteResize<T> resizeHandler;
    private final int bits;

    public HashMapPalette(IdMap<T> $$0, int $$1, PaletteResize<T> $$2, List<T> $$3) {
        this($$0, $$1, $$2);
        $$3.forEach(this.values::add);
    }

    public HashMapPalette(IdMap<T> $$0, int $$1, PaletteResize<T> $$2) {
        this($$0, $$1, $$2, CrudeIncrementalIntIdentityHashBiMap.create(1 << $$1));
    }

    private HashMapPalette(IdMap<T> $$0, int $$1, PaletteResize<T> $$2, CrudeIncrementalIntIdentityHashBiMap<T> $$3) {
        this.registry = $$0;
        this.bits = $$1;
        this.resizeHandler = $$2;
        this.values = $$3;
    }

    public static <A> Palette<A> create(int $$0, IdMap<A> $$1, PaletteResize<A> $$2, List<A> $$3) {
        return new HashMapPalette<A>($$1, $$0, $$2, $$3);
    }

    @Override
    public int idFor(T $$0) {
        int $$1 = this.values.getId($$0);
        if ($$1 == -1 && ($$1 = this.values.add($$0)) >= 1 << this.bits) {
            $$1 = this.resizeHandler.onResize(this.bits + 1, $$0);
        }
        return $$1;
    }

    @Override
    public boolean maybeHas(Predicate<T> $$0) {
        for (int $$1 = 0; $$1 < this.getSize(); ++$$1) {
            if (!$$0.test(this.values.byId($$1))) continue;
            return true;
        }
        return false;
    }

    @Override
    public T valueFor(int $$0) {
        T $$1 = this.values.byId($$0);
        if ($$1 == null) {
            throw new MissingPaletteEntryException($$0);
        }
        return $$1;
    }

    @Override
    public void read(FriendlyByteBuf $$0) {
        this.values.clear();
        int $$1 = $$0.readVarInt();
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            this.values.add(this.registry.byIdOrThrow($$0.readVarInt()));
        }
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        int $$1 = this.getSize();
        $$0.writeVarInt($$1);
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            $$0.writeVarInt(this.registry.getId(this.values.byId($$2)));
        }
    }

    @Override
    public int getSerializedSize() {
        int $$0 = FriendlyByteBuf.getVarIntSize(this.getSize());
        for (int $$1 = 0; $$1 < this.getSize(); ++$$1) {
            $$0 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values.byId($$1)));
        }
        return $$0;
    }

    public List<T> getEntries() {
        ArrayList $$0 = new ArrayList();
        this.values.iterator().forEachRemaining(arg_0 -> ((ArrayList)$$0).add(arg_0));
        return $$0;
    }

    @Override
    public int getSize() {
        return this.values.size();
    }

    @Override
    public Palette<T> copy() {
        return new HashMapPalette<T>(this.registry, this.bits, this.resizeHandler, this.values.copy());
    }
}