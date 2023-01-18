/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityVariantPredicate<V> {
    private static final String VARIANT_KEY = "variant";
    final Codec<V> variantCodec;
    final Function<Entity, Optional<V>> getter;
    final EntitySubPredicate.Type type;

    public static <V> EntityVariantPredicate<V> create(Registry<V> $$0, Function<Entity, Optional<V>> $$1) {
        return new EntityVariantPredicate<V>($$0.byNameCodec(), $$1);
    }

    public static <V> EntityVariantPredicate<V> create(Codec<V> $$0, Function<Entity, Optional<V>> $$1) {
        return new EntityVariantPredicate<V>($$0, $$1);
    }

    private EntityVariantPredicate(Codec<V> $$0, Function<Entity, Optional<V>> $$12) {
        this.variantCodec = $$0;
        this.getter = $$12;
        this.type = $$1 -> {
            JsonElement $$2 = $$1.get(VARIANT_KEY);
            if ($$2 == null) {
                throw new JsonParseException("Missing variant field");
            }
            Object $$3 = ((Pair)Util.getOrThrow($$0.decode(new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)$$2)), JsonParseException::new)).getFirst();
            return this.createPredicate($$3);
        };
    }

    public EntitySubPredicate.Type type() {
        return this.type;
    }

    public EntitySubPredicate createPredicate(final V $$0) {
        return new EntitySubPredicate(){

            @Override
            public boolean matches(Entity $$02, ServerLevel $$12, @Nullable Vec3 $$2) {
                return ((Optional)EntityVariantPredicate.this.getter.apply((Object)$$02)).filter($$1 -> $$1.equals($$0)).isPresent();
            }

            @Override
            public JsonObject serializeCustomData() {
                JsonObject $$02 = new JsonObject();
                $$02.add(EntityVariantPredicate.VARIANT_KEY, (JsonElement)Util.getOrThrow(EntityVariantPredicate.this.variantCodec.encodeStart((DynamicOps)JsonOps.INSTANCE, $$0), $$1 -> new JsonParseException("Can't serialize variant " + $$0 + ", message " + $$1)));
                return $$02;
            }

            @Override
            public EntitySubPredicate.Type type() {
                return EntityVariantPredicate.this.type;
            }
        };
    }
}