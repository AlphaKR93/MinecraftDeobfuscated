/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.network.syncher;

import java.util.Optional;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;

public interface EntityDataSerializer<T> {
    public void write(FriendlyByteBuf var1, T var2);

    public T read(FriendlyByteBuf var1);

    default public EntityDataAccessor<T> createAccessor(int $$0) {
        return new EntityDataAccessor($$0, this);
    }

    public T copy(T var1);

    public static <T> EntityDataSerializer<T> simple(final FriendlyByteBuf.Writer<T> $$0, final FriendlyByteBuf.Reader<T> $$1) {
        return new ForValueType<T>(){

            @Override
            public void write(FriendlyByteBuf $$02, T $$12) {
                $$0.accept((Object)$$02, $$12);
            }

            @Override
            public T read(FriendlyByteBuf $$02) {
                return $$1.apply((Object)$$02);
            }
        };
    }

    public static <T> EntityDataSerializer<Optional<T>> optional(FriendlyByteBuf.Writer<T> $$0, FriendlyByteBuf.Reader<T> $$1) {
        return EntityDataSerializer.simple($$0.asOptional(), $$1.asOptional());
    }

    public static <T extends Enum<T>> EntityDataSerializer<T> simpleEnum(Class<T> $$0) {
        return EntityDataSerializer.simple(FriendlyByteBuf::writeEnum, $$1 -> $$1.readEnum($$0));
    }

    public static <T> EntityDataSerializer<T> simpleId(IdMap<T> $$0) {
        return EntityDataSerializer.simple(($$1, $$2) -> $$1.writeId($$0, $$2), $$1 -> $$1.readById($$0));
    }

    public static interface ForValueType<T>
    extends EntityDataSerializer<T> {
        @Override
        default public T copy(T $$0) {
            return $$0;
        }
    }
}