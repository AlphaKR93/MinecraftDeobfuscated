/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;

public class ContextNbtProvider
implements NbtProvider {
    private static final String BLOCK_ENTITY_ID = "block_entity";
    private static final Getter BLOCK_ENTITY_PROVIDER = new Getter(){

        @Override
        public Tag get(LootContext $$0) {
            BlockEntity $$1 = $$0.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            return $$1 != null ? $$1.saveWithFullMetadata() : null;
        }

        @Override
        public String getId() {
            return ContextNbtProvider.BLOCK_ENTITY_ID;
        }

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
        }
    };
    public static final ContextNbtProvider BLOCK_ENTITY = new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
    final Getter getter;

    private static Getter forEntity(final LootContext.EntityTarget $$0) {
        return new Getter(){

            @Override
            @Nullable
            public Tag get(LootContext $$02) {
                Entity $$1 = $$02.getParamOrNull($$0.getParam());
                return $$1 != null ? NbtPredicate.getEntityTagToCompare($$1) : null;
            }

            @Override
            public String getId() {
                return $$0.name();
            }

            @Override
            public Set<LootContextParam<?>> getReferencedContextParams() {
                return ImmutableSet.of($$0.getParam());
            }
        };
    }

    private ContextNbtProvider(Getter $$0) {
        this.getter = $$0;
    }

    @Override
    public LootNbtProviderType getType() {
        return NbtProviders.CONTEXT;
    }

    @Override
    @Nullable
    public Tag get(LootContext $$0) {
        return this.getter.get($$0);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.getter.getReferencedContextParams();
    }

    public static NbtProvider forContextEntity(LootContext.EntityTarget $$0) {
        return new ContextNbtProvider(ContextNbtProvider.forEntity($$0));
    }

    static ContextNbtProvider createFromContext(String $$0) {
        if ($$0.equals((Object)BLOCK_ENTITY_ID)) {
            return new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
        }
        LootContext.EntityTarget $$1 = LootContext.EntityTarget.getByName($$0);
        return new ContextNbtProvider(ContextNbtProvider.forEntity($$1));
    }

    static interface Getter {
        @Nullable
        public Tag get(LootContext var1);

        public String getId();

        public Set<LootContextParam<?>> getReferencedContextParams();
    }

    public static class InlineSerializer
    implements GsonAdapterFactory.InlineSerializer<ContextNbtProvider> {
        @Override
        public JsonElement serialize(ContextNbtProvider $$0, JsonSerializationContext $$1) {
            return new JsonPrimitive($$0.getter.getId());
        }

        @Override
        public ContextNbtProvider deserialize(JsonElement $$0, JsonDeserializationContext $$1) {
            String $$2 = $$0.getAsString();
            return ContextNbtProvider.createFromContext($$2);
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<ContextNbtProvider> {
        @Override
        public void serialize(JsonObject $$0, ContextNbtProvider $$1, JsonSerializationContext $$2) {
            $$0.addProperty("target", $$1.getter.getId());
        }

        @Override
        public ContextNbtProvider deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            String $$2 = GsonHelper.getAsString($$0, "target");
            return ContextNbtProvider.createFromContext($$2);
        }
    }
}