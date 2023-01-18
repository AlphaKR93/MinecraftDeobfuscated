/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentPredicate {
    public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
    public static final EnchantmentPredicate[] NONE = new EnchantmentPredicate[0];
    @Nullable
    private final Enchantment enchantment;
    private final MinMaxBounds.Ints level;

    public EnchantmentPredicate() {
        this.enchantment = null;
        this.level = MinMaxBounds.Ints.ANY;
    }

    public EnchantmentPredicate(@Nullable Enchantment $$0, MinMaxBounds.Ints $$1) {
        this.enchantment = $$0;
        this.level = $$1;
    }

    public boolean containedIn(Map<Enchantment, Integer> $$0) {
        if (this.enchantment != null) {
            if (!$$0.containsKey((Object)this.enchantment)) {
                return false;
            }
            int $$1 = (Integer)$$0.get((Object)this.enchantment);
            if (this.level != MinMaxBounds.Ints.ANY && !this.level.matches($$1)) {
                return false;
            }
        } else if (this.level != MinMaxBounds.Ints.ANY) {
            for (Integer $$2 : $$0.values()) {
                if (!this.level.matches($$2)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        if (this.enchantment != null) {
            $$0.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this.enchantment).toString());
        }
        $$0.add("levels", this.level.serializeToJson());
        return $$0;
    }

    public static EnchantmentPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "enchantment");
        Enchantment $$2 = null;
        if ($$1.has("enchantment")) {
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$1, "enchantment"));
            $$2 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional($$3).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + $$3 + "'"));
        }
        MinMaxBounds.Ints $$4 = MinMaxBounds.Ints.fromJson($$1.get("levels"));
        return new EnchantmentPredicate($$2, $$4);
    }

    public static EnchantmentPredicate[] fromJsonArray(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return NONE;
        }
        JsonArray $$1 = GsonHelper.convertToJsonArray($$0, "enchantments");
        EnchantmentPredicate[] $$2 = new EnchantmentPredicate[$$1.size()];
        for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
            $$2[$$3] = EnchantmentPredicate.fromJson($$1.get($$3));
        }
        return $$2;
    }
}