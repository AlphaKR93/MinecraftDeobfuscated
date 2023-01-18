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
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
    public static final DamageSourcePredicate ANY = Builder.damageType().build();
    @Nullable
    private final Boolean isProjectile;
    @Nullable
    private final Boolean isExplosion;
    @Nullable
    private final Boolean bypassesArmor;
    @Nullable
    private final Boolean bypassesInvulnerability;
    @Nullable
    private final Boolean bypassesMagic;
    @Nullable
    private final Boolean isFire;
    @Nullable
    private final Boolean isMagic;
    @Nullable
    private final Boolean isLightning;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;

    public DamageSourcePredicate(@Nullable Boolean $$0, @Nullable Boolean $$1, @Nullable Boolean $$2, @Nullable Boolean $$3, @Nullable Boolean $$4, @Nullable Boolean $$5, @Nullable Boolean $$6, @Nullable Boolean $$7, EntityPredicate $$8, EntityPredicate $$9) {
        this.isProjectile = $$0;
        this.isExplosion = $$1;
        this.bypassesArmor = $$2;
        this.bypassesInvulnerability = $$3;
        this.bypassesMagic = $$4;
        this.isFire = $$5;
        this.isMagic = $$6;
        this.isLightning = $$7;
        this.directEntity = $$8;
        this.sourceEntity = $$9;
    }

    public boolean matches(ServerPlayer $$0, DamageSource $$1) {
        return this.matches($$0.getLevel(), $$0.position(), $$1);
    }

    public boolean matches(ServerLevel $$0, Vec3 $$1, DamageSource $$2) {
        if (this == ANY) {
            return true;
        }
        if (this.isProjectile != null && this.isProjectile.booleanValue() != $$2.isProjectile()) {
            return false;
        }
        if (this.isExplosion != null && this.isExplosion.booleanValue() != $$2.isExplosion()) {
            return false;
        }
        if (this.bypassesArmor != null && this.bypassesArmor.booleanValue() != $$2.isBypassArmor()) {
            return false;
        }
        if (this.bypassesInvulnerability != null && this.bypassesInvulnerability.booleanValue() != $$2.isBypassInvul()) {
            return false;
        }
        if (this.bypassesMagic != null && this.bypassesMagic.booleanValue() != $$2.isBypassMagic()) {
            return false;
        }
        if (this.isFire != null && this.isFire.booleanValue() != $$2.isFire()) {
            return false;
        }
        if (this.isMagic != null && this.isMagic.booleanValue() != $$2.isMagic()) {
            return false;
        }
        if (this.isLightning != null && this.isLightning != ($$2 == DamageSource.LIGHTNING_BOLT)) {
            return false;
        }
        if (!this.directEntity.matches($$0, $$1, $$2.getDirectEntity())) {
            return false;
        }
        return this.sourceEntity.matches($$0, $$1, $$2.getEntity());
    }

    public static DamageSourcePredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "damage type");
        Boolean $$2 = DamageSourcePredicate.getOptionalBoolean($$1, "is_projectile");
        Boolean $$3 = DamageSourcePredicate.getOptionalBoolean($$1, "is_explosion");
        Boolean $$4 = DamageSourcePredicate.getOptionalBoolean($$1, "bypasses_armor");
        Boolean $$5 = DamageSourcePredicate.getOptionalBoolean($$1, "bypasses_invulnerability");
        Boolean $$6 = DamageSourcePredicate.getOptionalBoolean($$1, "bypasses_magic");
        Boolean $$7 = DamageSourcePredicate.getOptionalBoolean($$1, "is_fire");
        Boolean $$8 = DamageSourcePredicate.getOptionalBoolean($$1, "is_magic");
        Boolean $$9 = DamageSourcePredicate.getOptionalBoolean($$1, "is_lightning");
        EntityPredicate $$10 = EntityPredicate.fromJson($$1.get("direct_entity"));
        EntityPredicate $$11 = EntityPredicate.fromJson($$1.get("source_entity"));
        return new DamageSourcePredicate($$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11);
    }

    @Nullable
    private static Boolean getOptionalBoolean(JsonObject $$0, String $$1) {
        return $$0.has($$1) ? Boolean.valueOf((boolean)GsonHelper.getAsBoolean($$0, $$1)) : null;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        this.addOptionally($$0, "is_projectile", this.isProjectile);
        this.addOptionally($$0, "is_explosion", this.isExplosion);
        this.addOptionally($$0, "bypasses_armor", this.bypassesArmor);
        this.addOptionally($$0, "bypasses_invulnerability", this.bypassesInvulnerability);
        this.addOptionally($$0, "bypasses_magic", this.bypassesMagic);
        this.addOptionally($$0, "is_fire", this.isFire);
        this.addOptionally($$0, "is_magic", this.isMagic);
        this.addOptionally($$0, "is_lightning", this.isLightning);
        $$0.add("direct_entity", this.directEntity.serializeToJson());
        $$0.add("source_entity", this.sourceEntity.serializeToJson());
        return $$0;
    }

    private void addOptionally(JsonObject $$0, String $$1, @Nullable Boolean $$2) {
        if ($$2 != null) {
            $$0.addProperty($$1, $$2);
        }
    }

    public static class Builder {
        @Nullable
        private Boolean isProjectile;
        @Nullable
        private Boolean isExplosion;
        @Nullable
        private Boolean bypassesArmor;
        @Nullable
        private Boolean bypassesInvulnerability;
        @Nullable
        private Boolean bypassesMagic;
        @Nullable
        private Boolean isFire;
        @Nullable
        private Boolean isMagic;
        @Nullable
        private Boolean isLightning;
        private EntityPredicate directEntity = EntityPredicate.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;

        public static Builder damageType() {
            return new Builder();
        }

        public Builder isProjectile(Boolean $$0) {
            this.isProjectile = $$0;
            return this;
        }

        public Builder isExplosion(Boolean $$0) {
            this.isExplosion = $$0;
            return this;
        }

        public Builder bypassesArmor(Boolean $$0) {
            this.bypassesArmor = $$0;
            return this;
        }

        public Builder bypassesInvulnerability(Boolean $$0) {
            this.bypassesInvulnerability = $$0;
            return this;
        }

        public Builder bypassesMagic(Boolean $$0) {
            this.bypassesMagic = $$0;
            return this;
        }

        public Builder isFire(Boolean $$0) {
            this.isFire = $$0;
            return this;
        }

        public Builder isMagic(Boolean $$0) {
            this.isMagic = $$0;
            return this;
        }

        public Builder isLightning(Boolean $$0) {
            this.isLightning = $$0;
            return this;
        }

        public Builder direct(EntityPredicate $$0) {
            this.directEntity = $$0;
            return this;
        }

        public Builder direct(EntityPredicate.Builder $$0) {
            this.directEntity = $$0.build();
            return this;
        }

        public Builder source(EntityPredicate $$0) {
            this.sourceEntity = $$0;
            return this;
        }

        public Builder source(EntityPredicate.Builder $$0) {
            this.sourceEntity = $$0.build();
            return this;
        }

        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }
}