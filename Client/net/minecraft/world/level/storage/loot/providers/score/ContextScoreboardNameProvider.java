/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;

public class ContextScoreboardNameProvider
implements ScoreboardNameProvider {
    final LootContext.EntityTarget target;

    ContextScoreboardNameProvider(LootContext.EntityTarget $$0) {
        this.target = $$0;
    }

    public static ScoreboardNameProvider forTarget(LootContext.EntityTarget $$0) {
        return new ContextScoreboardNameProvider($$0);
    }

    @Override
    public LootScoreProviderType getType() {
        return ScoreboardNameProviders.CONTEXT;
    }

    @Override
    @Nullable
    public String getScoreboardName(LootContext $$0) {
        Entity $$1 = $$0.getParamOrNull(this.target.getParam());
        return $$1 != null ? $$1.getScoreboardName() : null;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.target.getParam());
    }

    public static class InlineSerializer
    implements GsonAdapterFactory.InlineSerializer<ContextScoreboardNameProvider> {
        @Override
        public JsonElement serialize(ContextScoreboardNameProvider $$0, JsonSerializationContext $$1) {
            return $$1.serialize((Object)$$0.target);
        }

        @Override
        public ContextScoreboardNameProvider deserialize(JsonElement $$0, JsonDeserializationContext $$1) {
            LootContext.EntityTarget $$2 = (LootContext.EntityTarget)((Object)$$1.deserialize($$0, LootContext.EntityTarget.class));
            return new ContextScoreboardNameProvider($$2);
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<ContextScoreboardNameProvider> {
        @Override
        public void serialize(JsonObject $$0, ContextScoreboardNameProvider $$1, JsonSerializationContext $$2) {
            $$0.addProperty("target", $$1.target.name());
        }

        @Override
        public ContextScoreboardNameProvider deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            LootContext.EntityTarget $$2 = GsonHelper.getAsObject($$0, "target", $$1, LootContext.EntityTarget.class);
            return new ContextScoreboardNameProvider($$2);
        }
    }
}