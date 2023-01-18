/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.lang.Boolean
 *  java.lang.Object
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.advancements.critereon.LightPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;

public class LocationPredicate {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, null, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    private final MinMaxBounds.Doubles x;
    private final MinMaxBounds.Doubles y;
    private final MinMaxBounds.Doubles z;
    @Nullable
    private final ResourceKey<Biome> biome;
    @Nullable
    private final ResourceKey<Structure> structure;
    @Nullable
    private final ResourceKey<Level> dimension;
    @Nullable
    private final Boolean smokey;
    private final LightPredicate light;
    private final BlockPredicate block;
    private final FluidPredicate fluid;

    public LocationPredicate(MinMaxBounds.Doubles $$0, MinMaxBounds.Doubles $$1, MinMaxBounds.Doubles $$2, @Nullable ResourceKey<Biome> $$3, @Nullable ResourceKey<Structure> $$4, @Nullable ResourceKey<Level> $$5, @Nullable Boolean $$6, LightPredicate $$7, BlockPredicate $$8, FluidPredicate $$9) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.biome = $$3;
        this.structure = $$4;
        this.dimension = $$5;
        this.smokey = $$6;
        this.light = $$7;
        this.block = $$8;
        this.fluid = $$9;
    }

    public static LocationPredicate inBiome(ResourceKey<Biome> $$0) {
        return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, $$0, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate inDimension(ResourceKey<Level> $$0) {
        return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, null, null, $$0, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate inStructure(ResourceKey<Structure> $$0) {
        return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, null, $$0, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public static LocationPredicate atYLocation(MinMaxBounds.Doubles $$0) {
        return new LocationPredicate(MinMaxBounds.Doubles.ANY, $$0, MinMaxBounds.Doubles.ANY, null, null, null, null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
    }

    public boolean matches(ServerLevel $$0, double $$1, double $$2, double $$3) {
        if (!this.x.matches($$1)) {
            return false;
        }
        if (!this.y.matches($$2)) {
            return false;
        }
        if (!this.z.matches($$3)) {
            return false;
        }
        if (this.dimension != null && this.dimension != $$0.dimension()) {
            return false;
        }
        BlockPos $$4 = new BlockPos($$1, $$2, $$3);
        boolean $$5 = $$0.isLoaded($$4);
        if (!(this.biome == null || $$5 && $$0.getBiome($$4).is(this.biome))) {
            return false;
        }
        if (!(this.structure == null || $$5 && $$0.structureManager().getStructureWithPieceAt($$4, this.structure).isValid())) {
            return false;
        }
        if (!(this.smokey == null || $$5 && this.smokey == CampfireBlock.isSmokeyPos($$0, $$4))) {
            return false;
        }
        if (!this.light.matches($$0, $$4)) {
            return false;
        }
        if (!this.block.matches($$0, $$4)) {
            return false;
        }
        return this.fluid.matches($$0, $$4);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        if (!(this.x.isAny() && this.y.isAny() && this.z.isAny())) {
            JsonObject $$12 = new JsonObject();
            $$12.add("x", this.x.serializeToJson());
            $$12.add("y", this.y.serializeToJson());
            $$12.add("z", this.z.serializeToJson());
            $$0.add("position", (JsonElement)$$12);
        }
        if (this.dimension != null) {
            Level.RESOURCE_KEY_CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, this.dimension).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.add("dimension", $$1));
        }
        if (this.structure != null) {
            $$0.addProperty("structure", this.structure.location().toString());
        }
        if (this.biome != null) {
            $$0.addProperty("biome", this.biome.location().toString());
        }
        if (this.smokey != null) {
            $$0.addProperty("smokey", this.smokey);
        }
        $$0.add("light", this.light.serializeToJson());
        $$0.add("block", this.block.serializeToJson());
        $$0.add("fluid", this.fluid.serializeToJson());
        return $$0;
    }

    public static LocationPredicate fromJson(@Nullable JsonElement $$02) {
        ResourceKey $$6;
        if ($$02 == null || $$02.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$02, "location");
        JsonObject $$2 = GsonHelper.getAsJsonObject($$1, "position", new JsonObject());
        MinMaxBounds.Doubles $$3 = MinMaxBounds.Doubles.fromJson($$2.get("x"));
        MinMaxBounds.Doubles $$4 = MinMaxBounds.Doubles.fromJson($$2.get("y"));
        MinMaxBounds.Doubles $$5 = MinMaxBounds.Doubles.fromJson($$2.get("z"));
        ResourceKey resourceKey = $$1.has("dimension") ? (ResourceKey)ResourceLocation.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)$$1.get("dimension")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).map($$0 -> ResourceKey.create(Registries.DIMENSION, $$0)).orElse(null) : ($$6 = null);
        ResourceKey $$7 = $$1.has("structure") ? (ResourceKey)ResourceLocation.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)$$1.get("structure")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).map($$0 -> ResourceKey.create(Registries.STRUCTURE, $$0)).orElse(null) : null;
        ResourceKey<Biome> $$8 = null;
        if ($$1.has("biome")) {
            ResourceLocation $$9 = new ResourceLocation(GsonHelper.getAsString($$1, "biome"));
            $$8 = ResourceKey.create(Registries.BIOME, $$9);
        }
        Boolean $$10 = $$1.has("smokey") ? Boolean.valueOf((boolean)$$1.get("smokey").getAsBoolean()) : null;
        LightPredicate $$11 = LightPredicate.fromJson($$1.get("light"));
        BlockPredicate $$12 = BlockPredicate.fromJson($$1.get("block"));
        FluidPredicate $$13 = FluidPredicate.fromJson($$1.get("fluid"));
        return new LocationPredicate($$3, $$4, $$5, $$8, $$7, $$6, $$10, $$11, $$12, $$13);
    }

    public static class Builder {
        private MinMaxBounds.Doubles x = MinMaxBounds.Doubles.ANY;
        private MinMaxBounds.Doubles y = MinMaxBounds.Doubles.ANY;
        private MinMaxBounds.Doubles z = MinMaxBounds.Doubles.ANY;
        @Nullable
        private ResourceKey<Biome> biome;
        @Nullable
        private ResourceKey<Structure> structure;
        @Nullable
        private ResourceKey<Level> dimension;
        @Nullable
        private Boolean smokey;
        private LightPredicate light = LightPredicate.ANY;
        private BlockPredicate block = BlockPredicate.ANY;
        private FluidPredicate fluid = FluidPredicate.ANY;

        public static Builder location() {
            return new Builder();
        }

        public Builder setX(MinMaxBounds.Doubles $$0) {
            this.x = $$0;
            return this;
        }

        public Builder setY(MinMaxBounds.Doubles $$0) {
            this.y = $$0;
            return this;
        }

        public Builder setZ(MinMaxBounds.Doubles $$0) {
            this.z = $$0;
            return this;
        }

        public Builder setBiome(@Nullable ResourceKey<Biome> $$0) {
            this.biome = $$0;
            return this;
        }

        public Builder setStructure(@Nullable ResourceKey<Structure> $$0) {
            this.structure = $$0;
            return this;
        }

        public Builder setDimension(@Nullable ResourceKey<Level> $$0) {
            this.dimension = $$0;
            return this;
        }

        public Builder setLight(LightPredicate $$0) {
            this.light = $$0;
            return this;
        }

        public Builder setBlock(BlockPredicate $$0) {
            this.block = $$0;
            return this;
        }

        public Builder setFluid(FluidPredicate $$0) {
            this.fluid = $$0;
            return this;
        }

        public Builder setSmokey(Boolean $$0) {
            this.smokey = $$0;
            return this;
        }

        public LocationPredicate build() {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.structure, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}