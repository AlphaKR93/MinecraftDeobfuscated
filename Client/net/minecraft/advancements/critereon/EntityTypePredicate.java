/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

        @Override
        public boolean matches(EntityType<?> $$0) {
            return true;
        }

        @Override
        public JsonElement serializeToJson() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on((String)", ");

    public abstract boolean matches(EntityType<?> var1);

    public abstract JsonElement serializeToJson();

    public static EntityTypePredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        String $$1 = GsonHelper.convertToString($$0, "type");
        if ($$1.startsWith("#")) {
            ResourceLocation $$2 = new ResourceLocation($$1.substring(1));
            return new TagPredicate(TagKey.create(Registries.ENTITY_TYPE, $$2));
        }
        ResourceLocation $$3 = new ResourceLocation($$1);
        EntityType $$4 = (EntityType)BuiltInRegistries.ENTITY_TYPE.getOptional($$3).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + $$3 + "', valid types are: " + COMMA_JOINER.join((Iterable)BuiltInRegistries.ENTITY_TYPE.keySet())));
        return new TypePredicate($$4);
    }

    public static EntityTypePredicate of(EntityType<?> $$0) {
        return new TypePredicate($$0);
    }

    public static EntityTypePredicate of(TagKey<EntityType<?>> $$0) {
        return new TagPredicate($$0);
    }

    static class TagPredicate
    extends EntityTypePredicate {
        private final TagKey<EntityType<?>> tag;

        public TagPredicate(TagKey<EntityType<?>> $$0) {
            this.tag = $$0;
        }

        @Override
        public boolean matches(EntityType<?> $$0) {
            return $$0.is(this.tag);
        }

        @Override
        public JsonElement serializeToJson() {
            return new JsonPrimitive("#" + this.tag.location());
        }
    }

    static class TypePredicate
    extends EntityTypePredicate {
        private final EntityType<?> type;

        public TypePredicate(EntityType<?> $$0) {
            this.type = $$0;
        }

        @Override
        public boolean matches(EntityType<?> $$0) {
            return this.type == $$0;
        }

        @Override
        public JsonElement serializeToJson() {
            return new JsonPrimitive(BuiltInRegistries.ENTITY_TYPE.getKey(this.type).toString());
        }
    }
}