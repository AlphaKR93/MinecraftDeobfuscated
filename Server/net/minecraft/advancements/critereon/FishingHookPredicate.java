/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

public class FishingHookPredicate
implements EntitySubPredicate {
    public static final FishingHookPredicate ANY = new FishingHookPredicate(false);
    private static final String IN_OPEN_WATER_KEY = "in_open_water";
    private final boolean inOpenWater;

    private FishingHookPredicate(boolean $$0) {
        this.inOpenWater = $$0;
    }

    public static FishingHookPredicate inOpenWater(boolean $$0) {
        return new FishingHookPredicate($$0);
    }

    public static FishingHookPredicate fromJson(JsonObject $$0) {
        JsonElement $$1 = $$0.get(IN_OPEN_WATER_KEY);
        if ($$1 != null) {
            return new FishingHookPredicate(GsonHelper.convertToBoolean($$1, IN_OPEN_WATER_KEY));
        }
        return ANY;
    }

    @Override
    public JsonObject serializeCustomData() {
        if (this == ANY) {
            return new JsonObject();
        }
        JsonObject $$0 = new JsonObject();
        $$0.add(IN_OPEN_WATER_KEY, (JsonElement)new JsonPrimitive(Boolean.valueOf((boolean)this.inOpenWater)));
        return $$0;
    }

    @Override
    public EntitySubPredicate.Type type() {
        return EntitySubPredicate.Types.FISHING_HOOK;
    }

    @Override
    public boolean matches(Entity $$0, ServerLevel $$1, @Nullable Vec3 $$2) {
        if (this == ANY) {
            return true;
        }
        if (!($$0 instanceof FishingHook)) {
            return false;
        }
        FishingHook $$3 = (FishingHook)$$0;
        return this.inOpenWater == $$3.isOpenWaterFishing();
    }
}