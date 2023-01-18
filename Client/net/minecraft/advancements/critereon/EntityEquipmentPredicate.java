/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;

public class EntityEquipmentPredicate {
    public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    public static final EntityEquipmentPredicate CAPTAIN = new EntityEquipmentPredicate(ItemPredicate.Builder.item().of(Items.WHITE_BANNER).hasNbt(Raid.getLeaderBannerInstance().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    private final ItemPredicate head;
    private final ItemPredicate chest;
    private final ItemPredicate legs;
    private final ItemPredicate feet;
    private final ItemPredicate mainhand;
    private final ItemPredicate offhand;

    public EntityEquipmentPredicate(ItemPredicate $$0, ItemPredicate $$1, ItemPredicate $$2, ItemPredicate $$3, ItemPredicate $$4, ItemPredicate $$5) {
        this.head = $$0;
        this.chest = $$1;
        this.legs = $$2;
        this.feet = $$3;
        this.mainhand = $$4;
        this.offhand = $$5;
    }

    public boolean matches(@Nullable Entity $$0) {
        if (this == ANY) {
            return true;
        }
        if (!($$0 instanceof LivingEntity)) {
            return false;
        }
        LivingEntity $$1 = (LivingEntity)$$0;
        if (!this.head.matches($$1.getItemBySlot(EquipmentSlot.HEAD))) {
            return false;
        }
        if (!this.chest.matches($$1.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
        }
        if (!this.legs.matches($$1.getItemBySlot(EquipmentSlot.LEGS))) {
            return false;
        }
        if (!this.feet.matches($$1.getItemBySlot(EquipmentSlot.FEET))) {
            return false;
        }
        if (!this.mainhand.matches($$1.getItemBySlot(EquipmentSlot.MAINHAND))) {
            return false;
        }
        return this.offhand.matches($$1.getItemBySlot(EquipmentSlot.OFFHAND));
    }

    public static EntityEquipmentPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "equipment");
        ItemPredicate $$2 = ItemPredicate.fromJson($$1.get("head"));
        ItemPredicate $$3 = ItemPredicate.fromJson($$1.get("chest"));
        ItemPredicate $$4 = ItemPredicate.fromJson($$1.get("legs"));
        ItemPredicate $$5 = ItemPredicate.fromJson($$1.get("feet"));
        ItemPredicate $$6 = ItemPredicate.fromJson($$1.get("mainhand"));
        ItemPredicate $$7 = ItemPredicate.fromJson($$1.get("offhand"));
        return new EntityEquipmentPredicate($$2, $$3, $$4, $$5, $$6, $$7);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        $$0.add("head", this.head.serializeToJson());
        $$0.add("chest", this.chest.serializeToJson());
        $$0.add("legs", this.legs.serializeToJson());
        $$0.add("feet", this.feet.serializeToJson());
        $$0.add("mainhand", this.mainhand.serializeToJson());
        $$0.add("offhand", this.offhand.serializeToJson());
        return $$0;
    }

    public static class Builder {
        private ItemPredicate head = ItemPredicate.ANY;
        private ItemPredicate chest = ItemPredicate.ANY;
        private ItemPredicate legs = ItemPredicate.ANY;
        private ItemPredicate feet = ItemPredicate.ANY;
        private ItemPredicate mainhand = ItemPredicate.ANY;
        private ItemPredicate offhand = ItemPredicate.ANY;

        public static Builder equipment() {
            return new Builder();
        }

        public Builder head(ItemPredicate $$0) {
            this.head = $$0;
            return this;
        }

        public Builder chest(ItemPredicate $$0) {
            this.chest = $$0;
            return this;
        }

        public Builder legs(ItemPredicate $$0) {
            this.legs = $$0;
            return this;
        }

        public Builder feet(ItemPredicate $$0) {
            this.feet = $$0;
            return this;
        }

        public Builder mainhand(ItemPredicate $$0) {
            this.mainhand = $$0;
            return this;
        }

        public Builder offhand(ItemPredicate $$0) {
            this.offhand = $$0;
            return this;
        }

        public EntityEquipmentPredicate build() {
            return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
        }
    }
}