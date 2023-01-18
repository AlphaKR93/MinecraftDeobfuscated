/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;

public interface PositionSourceType<T extends PositionSource> {
    public static final PositionSourceType<BlockPositionSource> BLOCK = PositionSourceType.register("block", new BlockPositionSource.Type());
    public static final PositionSourceType<EntityPositionSource> ENTITY = PositionSourceType.register("entity", new EntityPositionSource.Type());

    public T read(FriendlyByteBuf var1);

    public void write(FriendlyByteBuf var1, T var2);

    public Codec<T> codec();

    public static <S extends PositionSourceType<T>, T extends PositionSource> S register(String $$0, S $$1) {
        return (S)Registry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, $$0, $$1);
    }

    public static PositionSource fromNetwork(FriendlyByteBuf $$0) {
        ResourceLocation $$1 = $$0.readResourceLocation();
        return ((PositionSourceType)BuiltInRegistries.POSITION_SOURCE_TYPE.getOptional($$1).orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + $$1))).read($$0);
    }

    public static <T extends PositionSource> void toNetwork(T $$0, FriendlyByteBuf $$1) {
        $$1.writeResourceLocation(BuiltInRegistries.POSITION_SOURCE_TYPE.getKey($$0.getType()));
        $$0.getType().write($$1, $$0);
    }
}