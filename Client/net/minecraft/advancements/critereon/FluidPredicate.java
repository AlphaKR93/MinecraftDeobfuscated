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
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidPredicate {
    public static final FluidPredicate ANY = new FluidPredicate(null, null, StatePropertiesPredicate.ANY);
    @Nullable
    private final TagKey<Fluid> tag;
    @Nullable
    private final Fluid fluid;
    private final StatePropertiesPredicate properties;

    public FluidPredicate(@Nullable TagKey<Fluid> $$0, @Nullable Fluid $$1, StatePropertiesPredicate $$2) {
        this.tag = $$0;
        this.fluid = $$1;
        this.properties = $$2;
    }

    public boolean matches(ServerLevel $$0, BlockPos $$1) {
        if (this == ANY) {
            return true;
        }
        if (!$$0.isLoaded($$1)) {
            return false;
        }
        FluidState $$2 = $$0.getFluidState($$1);
        if (this.tag != null && !$$2.is(this.tag)) {
            return false;
        }
        if (this.fluid != null && !$$2.is(this.fluid)) {
            return false;
        }
        return this.properties.matches($$2);
    }

    public static FluidPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "fluid");
        Fluid $$2 = null;
        if ($$1.has("fluid")) {
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$1, "fluid"));
            $$2 = BuiltInRegistries.FLUID.get($$3);
        }
        TagKey<Fluid> $$4 = null;
        if ($$1.has("tag")) {
            ResourceLocation $$5 = new ResourceLocation(GsonHelper.getAsString($$1, "tag"));
            $$4 = TagKey.create(Registries.FLUID, $$5);
        }
        StatePropertiesPredicate $$6 = StatePropertiesPredicate.fromJson($$1.get("state"));
        return new FluidPredicate($$4, $$2, $$6);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        if (this.fluid != null) {
            $$0.addProperty("fluid", BuiltInRegistries.FLUID.getKey(this.fluid).toString());
        }
        if (this.tag != null) {
            $$0.addProperty("tag", this.tag.location().toString());
        }
        $$0.add("state", this.properties.serializeToJson());
        return $$0;
    }

    public static class Builder {
        @Nullable
        private Fluid fluid;
        @Nullable
        private TagKey<Fluid> fluids;
        private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;

        private Builder() {
        }

        public static Builder fluid() {
            return new Builder();
        }

        public Builder of(Fluid $$0) {
            this.fluid = $$0;
            return this;
        }

        public Builder of(TagKey<Fluid> $$0) {
            this.fluids = $$0;
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate $$0) {
            this.properties = $$0;
            return this;
        }

        public FluidPredicate build() {
            return new FluidPredicate(this.fluids, this.fluid, this.properties);
        }
    }
}