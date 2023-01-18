/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
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
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;

public class FixedScoreboardNameProvider
implements ScoreboardNameProvider {
    final String name;

    FixedScoreboardNameProvider(String $$0) {
        this.name = $$0;
    }

    public static ScoreboardNameProvider forName(String $$0) {
        return new FixedScoreboardNameProvider($$0);
    }

    @Override
    public LootScoreProviderType getType() {
        return ScoreboardNameProviders.FIXED;
    }

    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public String getScoreboardName(LootContext $$0) {
        return this.name;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<FixedScoreboardNameProvider> {
        @Override
        public void serialize(JsonObject $$0, FixedScoreboardNameProvider $$1, JsonSerializationContext $$2) {
            $$0.addProperty("name", $$1.name);
        }

        @Override
        public FixedScoreboardNameProvider deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            String $$2 = GsonHelper.getAsString($$0, "name");
            return new FixedScoreboardNameProvider($$2);
        }
    }
}