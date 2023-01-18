/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EntityFlagsPredicate {
    public static final EntityFlagsPredicate ANY = new Builder().build();
    @Nullable
    private final Boolean isOnFire;
    @Nullable
    private final Boolean isCrouching;
    @Nullable
    private final Boolean isSprinting;
    @Nullable
    private final Boolean isSwimming;
    @Nullable
    private final Boolean isBaby;

    public EntityFlagsPredicate(@Nullable Boolean $$0, @Nullable Boolean $$1, @Nullable Boolean $$2, @Nullable Boolean $$3, @Nullable Boolean $$4) {
        this.isOnFire = $$0;
        this.isCrouching = $$1;
        this.isSprinting = $$2;
        this.isSwimming = $$3;
        this.isBaby = $$4;
    }

    public boolean matches(Entity $$0) {
        if (this.isOnFire != null && $$0.isOnFire() != this.isOnFire.booleanValue()) {
            return false;
        }
        if (this.isCrouching != null && $$0.isCrouching() != this.isCrouching.booleanValue()) {
            return false;
        }
        if (this.isSprinting != null && $$0.isSprinting() != this.isSprinting.booleanValue()) {
            return false;
        }
        if (this.isSwimming != null && $$0.isSwimming() != this.isSwimming.booleanValue()) {
            return false;
        }
        return this.isBaby == null || !($$0 instanceof LivingEntity) || ((LivingEntity)$$0).isBaby() == this.isBaby.booleanValue();
    }

    @Nullable
    private static Boolean getOptionalBoolean(JsonObject $$0, String $$1) {
        return $$0.has($$1) ? Boolean.valueOf((boolean)GsonHelper.getAsBoolean($$0, $$1)) : null;
    }

    public static EntityFlagsPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "entity flags");
        Boolean $$2 = EntityFlagsPredicate.getOptionalBoolean($$1, "is_on_fire");
        Boolean $$3 = EntityFlagsPredicate.getOptionalBoolean($$1, "is_sneaking");
        Boolean $$4 = EntityFlagsPredicate.getOptionalBoolean($$1, "is_sprinting");
        Boolean $$5 = EntityFlagsPredicate.getOptionalBoolean($$1, "is_swimming");
        Boolean $$6 = EntityFlagsPredicate.getOptionalBoolean($$1, "is_baby");
        return new EntityFlagsPredicate($$2, $$3, $$4, $$5, $$6);
    }

    private void addOptionalBoolean(JsonObject $$0, String $$1, @Nullable Boolean $$2) {
        if ($$2 != null) {
            $$0.addProperty($$1, $$2);
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        this.addOptionalBoolean($$0, "is_on_fire", this.isOnFire);
        this.addOptionalBoolean($$0, "is_sneaking", this.isCrouching);
        this.addOptionalBoolean($$0, "is_sprinting", this.isSprinting);
        this.addOptionalBoolean($$0, "is_swimming", this.isSwimming);
        this.addOptionalBoolean($$0, "is_baby", this.isBaby);
        return $$0;
    }

    public static class Builder {
        @Nullable
        private Boolean isOnFire;
        @Nullable
        private Boolean isCrouching;
        @Nullable
        private Boolean isSprinting;
        @Nullable
        private Boolean isSwimming;
        @Nullable
        private Boolean isBaby;

        public static Builder flags() {
            return new Builder();
        }

        public Builder setOnFire(@Nullable Boolean $$0) {
            this.isOnFire = $$0;
            return this;
        }

        public Builder setCrouching(@Nullable Boolean $$0) {
            this.isCrouching = $$0;
            return this;
        }

        public Builder setSprinting(@Nullable Boolean $$0) {
            this.isSprinting = $$0;
            return this;
        }

        public Builder setSwimming(@Nullable Boolean $$0) {
            this.isSwimming = $$0;
            return this;
        }

        public Builder setIsBaby(@Nullable Boolean $$0) {
            this.isBaby = $$0;
            return this;
        }

        public EntityFlagsPredicate build() {
            return new EntityFlagsPredicate(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}