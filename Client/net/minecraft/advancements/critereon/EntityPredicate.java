/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

public class EntityPredicate {
    public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, EntitySubPredicate.ANY, null);
    private final EntityTypePredicate entityType;
    private final DistancePredicate distanceToPlayer;
    private final LocationPredicate location;
    private final LocationPredicate steppingOnLocation;
    private final MobEffectsPredicate effects;
    private final NbtPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final EntitySubPredicate subPredicate;
    private final EntityPredicate vehicle;
    private final EntityPredicate passenger;
    private final EntityPredicate targetedEntity;
    @Nullable
    private final String team;

    private EntityPredicate(EntityTypePredicate $$0, DistancePredicate $$1, LocationPredicate $$2, LocationPredicate $$3, MobEffectsPredicate $$4, NbtPredicate $$5, EntityFlagsPredicate $$6, EntityEquipmentPredicate $$7, EntitySubPredicate $$8, @Nullable String $$9) {
        this.entityType = $$0;
        this.distanceToPlayer = $$1;
        this.location = $$2;
        this.steppingOnLocation = $$3;
        this.effects = $$4;
        this.nbt = $$5;
        this.flags = $$6;
        this.equipment = $$7;
        this.subPredicate = $$8;
        this.passenger = this;
        this.vehicle = this;
        this.targetedEntity = this;
        this.team = $$9;
    }

    EntityPredicate(EntityTypePredicate $$0, DistancePredicate $$1, LocationPredicate $$2, LocationPredicate $$3, MobEffectsPredicate $$4, NbtPredicate $$5, EntityFlagsPredicate $$6, EntityEquipmentPredicate $$7, EntitySubPredicate $$8, EntityPredicate $$9, EntityPredicate $$10, EntityPredicate $$11, @Nullable String $$12) {
        this.entityType = $$0;
        this.distanceToPlayer = $$1;
        this.location = $$2;
        this.steppingOnLocation = $$3;
        this.effects = $$4;
        this.nbt = $$5;
        this.flags = $$6;
        this.equipment = $$7;
        this.subPredicate = $$8;
        this.vehicle = $$9;
        this.passenger = $$10;
        this.targetedEntity = $$11;
        this.team = $$12;
    }

    public boolean matches(ServerPlayer $$0, @Nullable Entity $$1) {
        return this.matches($$0.getLevel(), $$0.position(), $$1);
    }

    public boolean matches(ServerLevel $$0, @Nullable Vec3 $$1, @Nullable Entity $$22) {
        Team $$4;
        Vec3 $$3;
        if (this == ANY) {
            return true;
        }
        if ($$22 == null) {
            return false;
        }
        if (!this.entityType.matches($$22.getType())) {
            return false;
        }
        if ($$1 == null ? this.distanceToPlayer != DistancePredicate.ANY : !this.distanceToPlayer.matches($$1.x, $$1.y, $$1.z, $$22.getX(), $$22.getY(), $$22.getZ())) {
            return false;
        }
        if (!this.location.matches($$0, $$22.getX(), $$22.getY(), $$22.getZ())) {
            return false;
        }
        if (this.steppingOnLocation != LocationPredicate.ANY && !this.steppingOnLocation.matches($$0, ($$3 = Vec3.atCenterOf($$22.getOnPosLegacy())).x(), $$3.y(), $$3.z())) {
            return false;
        }
        if (!this.effects.matches($$22)) {
            return false;
        }
        if (!this.nbt.matches($$22)) {
            return false;
        }
        if (!this.flags.matches($$22)) {
            return false;
        }
        if (!this.equipment.matches($$22)) {
            return false;
        }
        if (!this.subPredicate.matches($$22, $$0, $$1)) {
            return false;
        }
        if (!this.vehicle.matches($$0, $$1, $$22.getVehicle())) {
            return false;
        }
        if (this.passenger != ANY && $$22.getPassengers().stream().noneMatch($$2 -> this.passenger.matches($$0, $$1, (Entity)$$2))) {
            return false;
        }
        if (!this.targetedEntity.matches($$0, $$1, $$22 instanceof Mob ? ((Mob)$$22).getTarget() : null)) {
            return false;
        }
        return this.team == null || ($$4 = $$22.getTeam()) != null && this.team.equals((Object)$$4.getName());
    }

    public static EntityPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "entity");
        EntityTypePredicate $$2 = EntityTypePredicate.fromJson($$1.get("type"));
        DistancePredicate $$3 = DistancePredicate.fromJson($$1.get("distance"));
        LocationPredicate $$4 = LocationPredicate.fromJson($$1.get("location"));
        LocationPredicate $$5 = LocationPredicate.fromJson($$1.get("stepping_on"));
        MobEffectsPredicate $$6 = MobEffectsPredicate.fromJson($$1.get("effects"));
        NbtPredicate $$7 = NbtPredicate.fromJson($$1.get("nbt"));
        EntityFlagsPredicate $$8 = EntityFlagsPredicate.fromJson($$1.get("flags"));
        EntityEquipmentPredicate $$9 = EntityEquipmentPredicate.fromJson($$1.get("equipment"));
        EntitySubPredicate $$10 = EntitySubPredicate.fromJson($$1.get("type_specific"));
        EntityPredicate $$11 = EntityPredicate.fromJson($$1.get("vehicle"));
        EntityPredicate $$12 = EntityPredicate.fromJson($$1.get("passenger"));
        EntityPredicate $$13 = EntityPredicate.fromJson($$1.get("targeted_entity"));
        String $$14 = GsonHelper.getAsString($$1, "team", null);
        return new Builder().entityType($$2).distance($$3).located($$4).steppingOn($$5).effects($$6).nbt($$7).flags($$8).equipment($$9).subPredicate($$10).team($$14).vehicle($$11).passenger($$12).targetedEntity($$13).build();
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        $$0.add("type", this.entityType.serializeToJson());
        $$0.add("distance", this.distanceToPlayer.serializeToJson());
        $$0.add("location", this.location.serializeToJson());
        $$0.add("stepping_on", this.steppingOnLocation.serializeToJson());
        $$0.add("effects", this.effects.serializeToJson());
        $$0.add("nbt", this.nbt.serializeToJson());
        $$0.add("flags", this.flags.serializeToJson());
        $$0.add("equipment", this.equipment.serializeToJson());
        $$0.add("type_specific", this.subPredicate.serialize());
        $$0.add("vehicle", this.vehicle.serializeToJson());
        $$0.add("passenger", this.passenger.serializeToJson());
        $$0.add("targeted_entity", this.targetedEntity.serializeToJson());
        $$0.addProperty("team", this.team);
        return $$0;
    }

    public static LootContext createContext(ServerPlayer $$0, Entity $$1) {
        return new LootContext.Builder($$0.getLevel()).withParameter(LootContextParams.THIS_ENTITY, $$1).withParameter(LootContextParams.ORIGIN, $$0.position()).withRandom($$0.getRandom()).create(LootContextParamSets.ADVANCEMENT_ENTITY);
    }

    public static class Builder {
        private EntityTypePredicate entityType = EntityTypePredicate.ANY;
        private DistancePredicate distanceToPlayer = DistancePredicate.ANY;
        private LocationPredicate location = LocationPredicate.ANY;
        private LocationPredicate steppingOnLocation = LocationPredicate.ANY;
        private MobEffectsPredicate effects = MobEffectsPredicate.ANY;
        private NbtPredicate nbt = NbtPredicate.ANY;
        private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
        private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
        private EntitySubPredicate subPredicate = EntitySubPredicate.ANY;
        private EntityPredicate vehicle = ANY;
        private EntityPredicate passenger = ANY;
        private EntityPredicate targetedEntity = ANY;
        @Nullable
        private String team;

        public static Builder entity() {
            return new Builder();
        }

        public Builder of(EntityType<?> $$0) {
            this.entityType = EntityTypePredicate.of($$0);
            return this;
        }

        public Builder of(TagKey<EntityType<?>> $$0) {
            this.entityType = EntityTypePredicate.of($$0);
            return this;
        }

        public Builder entityType(EntityTypePredicate $$0) {
            this.entityType = $$0;
            return this;
        }

        public Builder distance(DistancePredicate $$0) {
            this.distanceToPlayer = $$0;
            return this;
        }

        public Builder located(LocationPredicate $$0) {
            this.location = $$0;
            return this;
        }

        public Builder steppingOn(LocationPredicate $$0) {
            this.steppingOnLocation = $$0;
            return this;
        }

        public Builder effects(MobEffectsPredicate $$0) {
            this.effects = $$0;
            return this;
        }

        public Builder nbt(NbtPredicate $$0) {
            this.nbt = $$0;
            return this;
        }

        public Builder flags(EntityFlagsPredicate $$0) {
            this.flags = $$0;
            return this;
        }

        public Builder equipment(EntityEquipmentPredicate $$0) {
            this.equipment = $$0;
            return this;
        }

        public Builder subPredicate(EntitySubPredicate $$0) {
            this.subPredicate = $$0;
            return this;
        }

        public Builder vehicle(EntityPredicate $$0) {
            this.vehicle = $$0;
            return this;
        }

        public Builder passenger(EntityPredicate $$0) {
            this.passenger = $$0;
            return this;
        }

        public Builder targetedEntity(EntityPredicate $$0) {
            this.targetedEntity = $$0;
            return this;
        }

        public Builder team(@Nullable String $$0) {
            this.team = $$0;
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.steppingOnLocation, this.effects, this.nbt, this.flags, this.equipment, this.subPredicate, this.vehicle, this.passenger, this.targetedEntity, this.team);
        }
    }

    public static class Composite {
        public static final Composite ANY = new Composite(new LootItemCondition[0]);
        private final LootItemCondition[] conditions;
        private final Predicate<LootContext> compositePredicates;

        private Composite(LootItemCondition[] $$0) {
            this.conditions = $$0;
            this.compositePredicates = LootItemConditions.andConditions($$0);
        }

        public static Composite create(LootItemCondition ... $$0) {
            return new Composite($$0);
        }

        public static Composite fromJson(JsonObject $$0, String $$1, DeserializationContext $$2) {
            JsonElement $$3 = $$0.get($$1);
            return Composite.fromElement($$1, $$2, $$3);
        }

        public static Composite[] fromJsonArray(JsonObject $$0, String $$1, DeserializationContext $$2) {
            JsonElement $$3 = $$0.get($$1);
            if ($$3 == null || $$3.isJsonNull()) {
                return new Composite[0];
            }
            JsonArray $$4 = GsonHelper.convertToJsonArray($$3, $$1);
            Composite[] $$5 = new Composite[$$4.size()];
            for (int $$6 = 0; $$6 < $$4.size(); ++$$6) {
                $$5[$$6] = Composite.fromElement($$1 + "[" + $$6 + "]", $$2, $$4.get($$6));
            }
            return $$5;
        }

        private static Composite fromElement(String $$0, DeserializationContext $$1, @Nullable JsonElement $$2) {
            if ($$2 != null && $$2.isJsonArray()) {
                LootItemCondition[] $$3 = $$1.deserializeConditions($$2.getAsJsonArray(), $$1.getAdvancementId() + "/" + $$0, LootContextParamSets.ADVANCEMENT_ENTITY);
                return new Composite($$3);
            }
            EntityPredicate $$4 = EntityPredicate.fromJson($$2);
            return Composite.wrap($$4);
        }

        public static Composite wrap(EntityPredicate $$0) {
            if ($$0 == ANY) {
                return ANY;
            }
            LootItemCondition $$1 = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, $$0).build();
            return new Composite(new LootItemCondition[]{$$1});
        }

        public boolean matches(LootContext $$0) {
            return this.compositePredicates.test((Object)$$0);
        }

        public JsonElement toJson(SerializationContext $$0) {
            if (this.conditions.length == 0) {
                return JsonNull.INSTANCE;
            }
            return $$0.serializeConditions(this.conditions);
        }

        public static JsonElement toJson(Composite[] $$0, SerializationContext $$1) {
            if ($$0.length == 0) {
                return JsonNull.INSTANCE;
            }
            JsonArray $$2 = new JsonArray();
            for (Composite $$3 : $$0) {
                $$2.add($$3.toJson($$1));
            }
            return $$2;
        }
    }
}