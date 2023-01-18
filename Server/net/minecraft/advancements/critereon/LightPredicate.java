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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

public class LightPredicate {
    public static final LightPredicate ANY = new LightPredicate(MinMaxBounds.Ints.ANY);
    private final MinMaxBounds.Ints composite;

    LightPredicate(MinMaxBounds.Ints $$0) {
        this.composite = $$0;
    }

    public boolean matches(ServerLevel $$0, BlockPos $$1) {
        if (this == ANY) {
            return true;
        }
        if (!$$0.isLoaded($$1)) {
            return false;
        }
        return this.composite.matches($$0.getMaxLocalRawBrightness($$1));
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        $$0.add("light", this.composite.serializeToJson());
        return $$0;
    }

    public static LightPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "light");
        MinMaxBounds.Ints $$2 = MinMaxBounds.Ints.fromJson($$1.get("light"));
        return new LightPredicate($$2);
    }

    public static class Builder {
        private MinMaxBounds.Ints composite = MinMaxBounds.Ints.ANY;

        public static Builder light() {
            return new Builder();
        }

        public Builder setComposite(MinMaxBounds.Ints $$0) {
            this.composite = $$0;
            return this;
        }

        public LightPredicate build() {
            return new LightPredicate(this.composite);
        }
    }
}