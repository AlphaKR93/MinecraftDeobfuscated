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
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class DistancePredicate {
    public static final DistancePredicate ANY = new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
    private final MinMaxBounds.Doubles x;
    private final MinMaxBounds.Doubles y;
    private final MinMaxBounds.Doubles z;
    private final MinMaxBounds.Doubles horizontal;
    private final MinMaxBounds.Doubles absolute;

    public DistancePredicate(MinMaxBounds.Doubles $$0, MinMaxBounds.Doubles $$1, MinMaxBounds.Doubles $$2, MinMaxBounds.Doubles $$3, MinMaxBounds.Doubles $$4) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.horizontal = $$3;
        this.absolute = $$4;
    }

    public static DistancePredicate horizontal(MinMaxBounds.Doubles $$0) {
        return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, $$0, MinMaxBounds.Doubles.ANY);
    }

    public static DistancePredicate vertical(MinMaxBounds.Doubles $$0) {
        return new DistancePredicate(MinMaxBounds.Doubles.ANY, $$0, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
    }

    public static DistancePredicate absolute(MinMaxBounds.Doubles $$0) {
        return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, $$0);
    }

    public boolean matches(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        float $$6 = (float)($$0 - $$3);
        float $$7 = (float)($$1 - $$4);
        float $$8 = (float)($$2 - $$5);
        if (!(this.x.matches(Mth.abs($$6)) && this.y.matches(Mth.abs($$7)) && this.z.matches(Mth.abs($$8)))) {
            return false;
        }
        if (!this.horizontal.matchesSqr($$6 * $$6 + $$8 * $$8)) {
            return false;
        }
        return this.absolute.matchesSqr($$6 * $$6 + $$7 * $$7 + $$8 * $$8);
    }

    public static DistancePredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "distance");
        MinMaxBounds.Doubles $$2 = MinMaxBounds.Doubles.fromJson($$1.get("x"));
        MinMaxBounds.Doubles $$3 = MinMaxBounds.Doubles.fromJson($$1.get("y"));
        MinMaxBounds.Doubles $$4 = MinMaxBounds.Doubles.fromJson($$1.get("z"));
        MinMaxBounds.Doubles $$5 = MinMaxBounds.Doubles.fromJson($$1.get("horizontal"));
        MinMaxBounds.Doubles $$6 = MinMaxBounds.Doubles.fromJson($$1.get("absolute"));
        return new DistancePredicate($$2, $$3, $$4, $$5, $$6);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        $$0.add("x", this.x.serializeToJson());
        $$0.add("y", this.y.serializeToJson());
        $$0.add("z", this.z.serializeToJson());
        $$0.add("horizontal", this.horizontal.serializeToJson());
        $$0.add("absolute", this.absolute.serializeToJson());
        return $$0;
    }
}