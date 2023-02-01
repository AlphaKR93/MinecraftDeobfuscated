/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlayerPredicate
implements EntitySubPredicate {
    public static final int LOOKING_AT_RANGE = 100;
    private final MinMaxBounds.Ints level;
    @Nullable
    private final GameType gameType;
    private final Map<Stat<?>, MinMaxBounds.Ints> stats;
    private final Object2BooleanMap<ResourceLocation> recipes;
    private final Map<ResourceLocation, AdvancementPredicate> advancements;
    private final EntityPredicate lookingAt;

    private static AdvancementPredicate advancementPredicateFromJson(JsonElement $$0) {
        if ($$0.isJsonPrimitive()) {
            boolean $$1 = $$0.getAsBoolean();
            return new AdvancementDonePredicate($$1);
        }
        Object2BooleanOpenHashMap $$2 = new Object2BooleanOpenHashMap();
        JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "criterion data");
        $$3.entrySet().forEach(arg_0 -> PlayerPredicate.lambda$advancementPredicateFromJson$0((Object2BooleanMap)$$2, arg_0));
        return new AdvancementCriterionsPredicate((Object2BooleanMap<String>)$$2);
    }

    PlayerPredicate(MinMaxBounds.Ints $$0, @Nullable GameType $$1, Map<Stat<?>, MinMaxBounds.Ints> $$2, Object2BooleanMap<ResourceLocation> $$3, Map<ResourceLocation, AdvancementPredicate> $$4, EntityPredicate $$5) {
        this.level = $$0;
        this.gameType = $$1;
        this.stats = $$2;
        this.recipes = $$3;
        this.advancements = $$4;
        this.lookingAt = $$5;
    }

    @Override
    public boolean matches(Entity $$02, ServerLevel $$1, @Nullable Vec3 $$2) {
        if (!($$02 instanceof ServerPlayer)) {
            return false;
        }
        ServerPlayer $$3 = (ServerPlayer)$$02;
        if (!this.level.matches($$3.experienceLevel)) {
            return false;
        }
        if (this.gameType != null && this.gameType != $$3.gameMode.getGameModeForPlayer()) {
            return false;
        }
        ServerStatsCounter $$4 = $$3.getStats();
        for (Map.Entry $$5 : this.stats.entrySet()) {
            int $$6 = $$4.getValue((Stat)$$5.getKey());
            if (((MinMaxBounds.Ints)$$5.getValue()).matches($$6)) continue;
            return false;
        }
        ServerRecipeBook $$7 = $$3.getRecipeBook();
        for (Object2BooleanMap.Entry $$8 : this.recipes.object2BooleanEntrySet()) {
            if ($$7.contains((ResourceLocation)$$8.getKey()) == $$8.getBooleanValue()) continue;
            return false;
        }
        if (!this.advancements.isEmpty()) {
            PlayerAdvancements $$9 = $$3.getAdvancements();
            ServerAdvancementManager $$10 = $$3.getServer().getAdvancements();
            for (Map.Entry $$11 : this.advancements.entrySet()) {
                Advancement $$12 = $$10.getAdvancement((ResourceLocation)$$11.getKey());
                if ($$12 != null && ((AdvancementPredicate)$$11.getValue()).test($$9.getOrStartProgress($$12))) continue;
                return false;
            }
        }
        if (this.lookingAt != EntityPredicate.ANY) {
            Vec3 $$13 = $$3.getEyePosition();
            Vec3 $$14 = $$3.getViewVector(1.0f);
            Vec3 $$15 = $$13.add($$14.x * 100.0, $$14.y * 100.0, $$14.z * 100.0);
            EntityHitResult $$16 = ProjectileUtil.getEntityHitResult($$3.level, $$3, $$13, $$15, new AABB($$13, $$15).inflate(1.0), (Predicate<Entity>)((Predicate)$$0 -> !$$0.isSpectator()), 0.0f);
            if ($$16 == null || $$16.getType() != HitResult.Type.ENTITY) {
                return false;
            }
            Entity $$17 = $$16.getEntity();
            if (!this.lookingAt.matches($$3, $$17) || !$$3.hasLineOfSight($$17)) {
                return false;
            }
        }
        return true;
    }

    public static PlayerPredicate fromJson(JsonObject $$0) {
        MinMaxBounds.Ints $$1 = MinMaxBounds.Ints.fromJson($$0.get("level"));
        String $$2 = GsonHelper.getAsString($$0, "gamemode", "");
        GameType $$3 = GameType.byName($$2, null);
        HashMap $$4 = Maps.newHashMap();
        JsonArray $$5 = GsonHelper.getAsJsonArray($$0, "stats", null);
        if ($$5 != null) {
            for (JsonElement $$6 : $$5) {
                JsonObject $$7 = GsonHelper.convertToJsonObject($$6, "stats entry");
                ResourceLocation $$8 = new ResourceLocation(GsonHelper.getAsString($$7, "type"));
                StatType<?> $$9 = BuiltInRegistries.STAT_TYPE.get($$8);
                if ($$9 == null) {
                    throw new JsonParseException("Invalid stat type: " + $$8);
                }
                ResourceLocation $$10 = new ResourceLocation(GsonHelper.getAsString($$7, "stat"));
                Stat<?> $$11 = PlayerPredicate.getStat($$9, $$10);
                MinMaxBounds.Ints $$12 = MinMaxBounds.Ints.fromJson($$7.get("value"));
                $$4.put($$11, (Object)$$12);
            }
        }
        Object2BooleanOpenHashMap $$13 = new Object2BooleanOpenHashMap();
        JsonObject $$14 = GsonHelper.getAsJsonObject($$0, "recipes", new JsonObject());
        for (Map.Entry $$15 : $$14.entrySet()) {
            ResourceLocation $$16 = new ResourceLocation((String)$$15.getKey());
            boolean $$17 = GsonHelper.convertToBoolean((JsonElement)$$15.getValue(), "recipe present");
            $$13.put((Object)$$16, $$17);
        }
        HashMap $$18 = Maps.newHashMap();
        JsonObject $$19 = GsonHelper.getAsJsonObject($$0, "advancements", new JsonObject());
        for (Map.Entry $$20 : $$19.entrySet()) {
            ResourceLocation $$21 = new ResourceLocation((String)$$20.getKey());
            AdvancementPredicate $$22 = PlayerPredicate.advancementPredicateFromJson((JsonElement)$$20.getValue());
            $$18.put((Object)$$21, (Object)$$22);
        }
        EntityPredicate $$23 = EntityPredicate.fromJson($$0.get("looking_at"));
        return new PlayerPredicate($$1, $$3, (Map<Stat<?>, MinMaxBounds.Ints>)$$4, (Object2BooleanMap<ResourceLocation>)$$13, (Map<ResourceLocation, AdvancementPredicate>)$$18, $$23);
    }

    private static <T> Stat<T> getStat(StatType<T> $$0, ResourceLocation $$1) {
        Registry<T> $$2 = $$0.getRegistry();
        T $$3 = $$2.get($$1);
        if ($$3 == null) {
            throw new JsonParseException("Unknown object " + $$1 + " for stat type " + BuiltInRegistries.STAT_TYPE.getKey($$0));
        }
        return $$0.get($$3);
    }

    private static <T> ResourceLocation getStatValueId(Stat<T> $$0) {
        return $$0.getType().getRegistry().getKey($$0.getValue());
    }

    @Override
    public JsonObject serializeCustomData() {
        JsonObject $$0 = new JsonObject();
        $$0.add("level", this.level.serializeToJson());
        if (this.gameType != null) {
            $$0.addProperty("gamemode", this.gameType.getName());
        }
        if (!this.stats.isEmpty()) {
            JsonArray $$12 = new JsonArray();
            this.stats.forEach(($$1, $$2) -> {
                JsonObject $$3 = new JsonObject();
                $$3.addProperty("type", BuiltInRegistries.STAT_TYPE.getKey($$1.getType()).toString());
                $$3.addProperty("stat", PlayerPredicate.getStatValueId($$1).toString());
                $$3.add("value", $$2.serializeToJson());
                $$12.add((JsonElement)$$3);
            });
            $$0.add("stats", (JsonElement)$$12);
        }
        if (!this.recipes.isEmpty()) {
            JsonObject $$22 = new JsonObject();
            this.recipes.forEach(($$1, $$2) -> $$22.addProperty($$1.toString(), $$2));
            $$0.add("recipes", (JsonElement)$$22);
        }
        if (!this.advancements.isEmpty()) {
            JsonObject $$3 = new JsonObject();
            this.advancements.forEach(($$1, $$2) -> $$3.add($$1.toString(), $$2.toJson()));
            $$0.add("advancements", (JsonElement)$$3);
        }
        $$0.add("looking_at", this.lookingAt.serializeToJson());
        return $$0;
    }

    @Override
    public EntitySubPredicate.Type type() {
        return EntitySubPredicate.Types.PLAYER;
    }

    private static /* synthetic */ void lambda$advancementPredicateFromJson$0(Object2BooleanMap $$0, Map.Entry $$1) {
        boolean $$2 = GsonHelper.convertToBoolean((JsonElement)$$1.getValue(), "criterion test");
        $$0.put((Object)((String)$$1.getKey()), $$2);
    }

    static class AdvancementDonePredicate
    implements AdvancementPredicate {
        private final boolean state;

        public AdvancementDonePredicate(boolean $$0) {
            this.state = $$0;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Boolean.valueOf((boolean)this.state));
        }

        public boolean test(AdvancementProgress $$0) {
            return $$0.isDone() == this.state;
        }
    }

    static class AdvancementCriterionsPredicate
    implements AdvancementPredicate {
        private final Object2BooleanMap<String> criterions;

        public AdvancementCriterionsPredicate(Object2BooleanMap<String> $$0) {
            this.criterions = $$0;
        }

        @Override
        public JsonElement toJson() {
            JsonObject $$0 = new JsonObject();
            this.criterions.forEach((arg_0, arg_1) -> ((JsonObject)$$0).addProperty(arg_0, arg_1));
            return $$0;
        }

        public boolean test(AdvancementProgress $$0) {
            for (Object2BooleanMap.Entry $$1 : this.criterions.object2BooleanEntrySet()) {
                CriterionProgress $$2 = $$0.getCriterion((String)$$1.getKey());
                if ($$2 != null && $$2.isDone() == $$1.getBooleanValue()) continue;
                return false;
            }
            return true;
        }
    }

    static interface AdvancementPredicate
    extends Predicate<AdvancementProgress> {
        public JsonElement toJson();
    }

    public static class Builder {
        private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
        @Nullable
        private GameType gameType;
        private final Map<Stat<?>, MinMaxBounds.Ints> stats = Maps.newHashMap();
        private final Object2BooleanMap<ResourceLocation> recipes = new Object2BooleanOpenHashMap();
        private final Map<ResourceLocation, AdvancementPredicate> advancements = Maps.newHashMap();
        private EntityPredicate lookingAt = EntityPredicate.ANY;

        public static Builder player() {
            return new Builder();
        }

        public Builder setLevel(MinMaxBounds.Ints $$0) {
            this.level = $$0;
            return this;
        }

        public Builder addStat(Stat<?> $$0, MinMaxBounds.Ints $$1) {
            this.stats.put($$0, (Object)$$1);
            return this;
        }

        public Builder addRecipe(ResourceLocation $$0, boolean $$1) {
            this.recipes.put((Object)$$0, $$1);
            return this;
        }

        public Builder setGameType(GameType $$0) {
            this.gameType = $$0;
            return this;
        }

        public Builder setLookingAt(EntityPredicate $$0) {
            this.lookingAt = $$0;
            return this;
        }

        public Builder checkAdvancementDone(ResourceLocation $$0, boolean $$1) {
            this.advancements.put((Object)$$0, (Object)new AdvancementDonePredicate($$1));
            return this;
        }

        public Builder checkAdvancementCriterions(ResourceLocation $$0, Map<String, Boolean> $$1) {
            this.advancements.put((Object)$$0, (Object)new AdvancementCriterionsPredicate((Object2BooleanMap<String>)new Object2BooleanOpenHashMap($$1)));
            return this;
        }

        public PlayerPredicate build() {
            return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements, this.lookingAt);
        }
    }
}