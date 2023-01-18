/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Float
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.scores.Objective;

public class ScoreboardValue
implements NumberProvider {
    final ScoreboardNameProvider target;
    final String score;
    final float scale;

    ScoreboardValue(ScoreboardNameProvider $$0, String $$1, float $$2) {
        this.target = $$0;
        this.score = $$1;
        this.scale = $$2;
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.SCORE;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.target.getReferencedContextParams();
    }

    public static ScoreboardValue fromScoreboard(LootContext.EntityTarget $$0, String $$1) {
        return ScoreboardValue.fromScoreboard($$0, $$1, 1.0f);
    }

    public static ScoreboardValue fromScoreboard(LootContext.EntityTarget $$0, String $$1, float $$2) {
        return new ScoreboardValue(ContextScoreboardNameProvider.forTarget($$0), $$1, $$2);
    }

    @Override
    public float getFloat(LootContext $$0) {
        String $$1 = this.target.getScoreboardName($$0);
        if ($$1 == null) {
            return 0.0f;
        }
        ServerScoreboard $$2 = $$0.getLevel().getScoreboard();
        Objective $$3 = $$2.getObjective(this.score);
        if ($$3 == null) {
            return 0.0f;
        }
        if (!$$2.hasPlayerScore($$1, $$3)) {
            return 0.0f;
        }
        return (float)$$2.getOrCreatePlayerScore($$1, $$3).getScore() * this.scale;
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<ScoreboardValue> {
        @Override
        public ScoreboardValue deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            String $$2 = GsonHelper.getAsString($$0, "score");
            float $$3 = GsonHelper.getAsFloat($$0, "scale", 1.0f);
            ScoreboardNameProvider $$4 = GsonHelper.getAsObject($$0, "target", $$1, ScoreboardNameProvider.class);
            return new ScoreboardValue($$4, $$2, $$3);
        }

        @Override
        public void serialize(JsonObject $$0, ScoreboardValue $$1, JsonSerializationContext $$2) {
            $$0.addProperty("score", $$1.score);
            $$0.add("target", $$2.serialize((Object)$$1.target));
            $$0.addProperty("scale", (Number)Float.valueOf((float)$$1.scale));
        }
    }
}