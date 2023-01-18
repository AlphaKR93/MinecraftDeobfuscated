/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.phys.Vec3;

public class LocationCheck
implements LootItemCondition {
    final LocationPredicate predicate;
    final BlockPos offset;

    LocationCheck(LocationPredicate $$0, BlockPos $$1) {
        this.predicate = $$0;
        this.offset = $$1;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.LOCATION_CHECK;
    }

    public boolean test(LootContext $$0) {
        Vec3 $$1 = $$0.getParamOrNull(LootContextParams.ORIGIN);
        return $$1 != null && this.predicate.matches($$0.getLevel(), $$1.x() + (double)this.offset.getX(), $$1.y() + (double)this.offset.getY(), $$1.z() + (double)this.offset.getZ());
    }

    public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder $$0) {
        return () -> new LocationCheck($$0.build(), BlockPos.ZERO);
    }

    public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder $$0, BlockPos $$1) {
        return () -> new LocationCheck($$0.build(), $$1);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LocationCheck> {
        @Override
        public void serialize(JsonObject $$0, LocationCheck $$1, JsonSerializationContext $$2) {
            $$0.add("predicate", $$1.predicate.serializeToJson());
            if ($$1.offset.getX() != 0) {
                $$0.addProperty("offsetX", (Number)Integer.valueOf((int)$$1.offset.getX()));
            }
            if ($$1.offset.getY() != 0) {
                $$0.addProperty("offsetY", (Number)Integer.valueOf((int)$$1.offset.getY()));
            }
            if ($$1.offset.getZ() != 0) {
                $$0.addProperty("offsetZ", (Number)Integer.valueOf((int)$$1.offset.getZ()));
            }
        }

        @Override
        public LocationCheck deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            LocationPredicate $$2 = LocationPredicate.fromJson($$0.get("predicate"));
            int $$3 = GsonHelper.getAsInt($$0, "offsetX", 0);
            int $$4 = GsonHelper.getAsInt($$0, "offsetY", 0);
            int $$5 = GsonHelper.getAsInt($$0, "offsetZ", 0);
            return new LocationCheck($$2, new BlockPos($$3, $$4, $$5));
        }
    }
}