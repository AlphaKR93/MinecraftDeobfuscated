/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.MissingPaletteEntryException;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

public class GlobalPalette<T>
implements Palette<T> {
    private final IdMap<T> registry;

    public GlobalPalette(IdMap<T> $$0) {
        this.registry = $$0;
    }

    public static <A> Palette<A> create(int $$0, IdMap<A> $$1, PaletteResize<A> $$2, List<A> $$3) {
        return new GlobalPalette<A>($$1);
    }

    @Override
    public int idFor(T $$0) {
        int $$1 = this.registry.getId($$0);
        return $$1 == -1 ? 0 : $$1;
    }

    @Override
    public boolean maybeHas(Predicate<T> $$0) {
        return true;
    }

    @Override
    public T valueFor(int $$0) {
        T $$1 = this.registry.byId($$0);
        if ($$1 == null) {
            throw new MissingPaletteEntryException($$0);
        }
        return $$1;
    }

    @Override
    public void read(FriendlyByteBuf $$0) {
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
    }

    @Override
    public int getSerializedSize() {
        return FriendlyByteBuf.getVarIntSize(0);
    }

    @Override
    public int getSize() {
        return this.registry.size();
    }

    @Override
    public Palette<T> copy() {
        return this;
    }
}