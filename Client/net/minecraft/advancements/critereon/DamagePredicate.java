/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  java.lang.Boolean
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;

public class DamagePredicate {
    public static final DamagePredicate ANY = Builder.damageInstance().build();
    private final MinMaxBounds.Doubles dealtDamage;
    private final MinMaxBounds.Doubles takenDamage;
    private final EntityPredicate sourceEntity;
    @Nullable
    private final Boolean blocked;
    private final DamageSourcePredicate type;

    public DamagePredicate() {
        this.dealtDamage = MinMaxBounds.Doubles.ANY;
        this.takenDamage = MinMaxBounds.Doubles.ANY;
        this.sourceEntity = EntityPredicate.ANY;
        this.blocked = null;
        this.type = DamageSourcePredicate.ANY;
    }

    public DamagePredicate(MinMaxBounds.Doubles $$0, MinMaxBounds.Doubles $$1, EntityPredicate $$2, @Nullable Boolean $$3, DamageSourcePredicate $$4) {
        this.dealtDamage = $$0;
        this.takenDamage = $$1;
        this.sourceEntity = $$2;
        this.blocked = $$3;
        this.type = $$4;
    }

    public boolean matches(ServerPlayer $$0, DamageSource $$1, float $$2, float $$3, boolean $$4) {
        if (this == ANY) {
            return true;
        }
        if (!this.dealtDamage.matches($$2)) {
            return false;
        }
        if (!this.takenDamage.matches($$3)) {
            return false;
        }
        if (!this.sourceEntity.matches($$0, $$1.getEntity())) {
            return false;
        }
        if (this.blocked != null && this.blocked != $$4) {
            return false;
        }
        return this.type.matches($$0, $$1);
    }

    public static DamagePredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "damage");
        MinMaxBounds.Doubles $$2 = MinMaxBounds.Doubles.fromJson($$1.get("dealt"));
        MinMaxBounds.Doubles $$3 = MinMaxBounds.Doubles.fromJson($$1.get("taken"));
        Boolean $$4 = $$1.has("blocked") ? Boolean.valueOf((boolean)GsonHelper.getAsBoolean($$1, "blocked")) : null;
        EntityPredicate $$5 = EntityPredicate.fromJson($$1.get("source_entity"));
        DamageSourcePredicate $$6 = DamageSourcePredicate.fromJson($$1.get("type"));
        return new DamagePredicate($$2, $$3, $$5, $$4, $$6);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        $$0.add("dealt", this.dealtDamage.serializeToJson());
        $$0.add("taken", this.takenDamage.serializeToJson());
        $$0.add("source_entity", this.sourceEntity.serializeToJson());
        $$0.add("type", this.type.serializeToJson());
        if (this.blocked != null) {
            $$0.addProperty("blocked", this.blocked);
        }
        return $$0;
    }

    public static class Builder {
        private MinMaxBounds.Doubles dealtDamage = MinMaxBounds.Doubles.ANY;
        private MinMaxBounds.Doubles takenDamage = MinMaxBounds.Doubles.ANY;
        private EntityPredicate sourceEntity = EntityPredicate.ANY;
        @Nullable
        private Boolean blocked;
        private DamageSourcePredicate type = DamageSourcePredicate.ANY;

        public static Builder damageInstance() {
            return new Builder();
        }

        public Builder dealtDamage(MinMaxBounds.Doubles $$0) {
            this.dealtDamage = $$0;
            return this;
        }

        public Builder takenDamage(MinMaxBounds.Doubles $$0) {
            this.takenDamage = $$0;
            return this;
        }

        public Builder sourceEntity(EntityPredicate $$0) {
            this.sourceEntity = $$0;
            return this;
        }

        public Builder blocked(Boolean $$0) {
            this.blocked = $$0;
            return this;
        }

        public Builder type(DamageSourcePredicate $$0) {
            this.type = $$0;
            return this;
        }

        public Builder type(DamageSourcePredicate.Builder $$0) {
            this.type = $$0.build();
            return this;
        }

        public DamagePredicate build() {
            return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
        }
    }
}