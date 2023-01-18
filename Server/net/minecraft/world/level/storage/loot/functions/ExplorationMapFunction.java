/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.logging.LogUtils
 *  java.lang.Boolean
 *  java.lang.Byte
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  java.util.Set
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ExplorationMapFunction
extends LootItemConditionalFunction {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final TagKey<Structure> DEFAULT_DESTINATION = StructureTags.ON_TREASURE_MAPS;
    public static final String DEFAULT_DECORATION_NAME = "mansion";
    public static final MapDecoration.Type DEFAULT_DECORATION = MapDecoration.Type.MANSION;
    public static final byte DEFAULT_ZOOM = 2;
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING = true;
    final TagKey<Structure> destination;
    final MapDecoration.Type mapDecoration;
    final byte zoom;
    final int searchRadius;
    final boolean skipKnownStructures;

    ExplorationMapFunction(LootItemCondition[] $$0, TagKey<Structure> $$1, MapDecoration.Type $$2, byte $$3, int $$4, boolean $$5) {
        super($$0);
        this.destination = $$1;
        this.mapDecoration = $$2;
        this.zoom = $$3;
        this.searchRadius = $$4;
        this.skipKnownStructures = $$5;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.EXPLORATION_MAP;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        ServerLevel $$3;
        BlockPos $$4;
        if (!$$0.is(Items.MAP)) {
            return $$0;
        }
        Vec3 $$2 = $$1.getParamOrNull(LootContextParams.ORIGIN);
        if ($$2 != null && ($$4 = ($$3 = $$1.getLevel()).findNearestMapStructure(this.destination, new BlockPos($$2), this.searchRadius, this.skipKnownStructures)) != null) {
            ItemStack $$5 = MapItem.create($$3, $$4.getX(), $$4.getZ(), this.zoom, true, true);
            MapItem.renderBiomePreviewMap($$3, $$5);
            MapItemSavedData.addTargetDecoration($$5, $$4, "+", this.mapDecoration);
            return $$5;
        }
        return $$0;
    }

    public static Builder makeExplorationMap() {
        return new Builder();
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private TagKey<Structure> destination = DEFAULT_DESTINATION;
        private MapDecoration.Type mapDecoration = DEFAULT_DECORATION;
        private byte zoom = (byte)2;
        private int searchRadius = 50;
        private boolean skipKnownStructures = true;

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder setDestination(TagKey<Structure> $$0) {
            this.destination = $$0;
            return this;
        }

        public Builder setMapDecoration(MapDecoration.Type $$0) {
            this.mapDecoration = $$0;
            return this;
        }

        public Builder setZoom(byte $$0) {
            this.zoom = $$0;
            return this;
        }

        public Builder setSearchRadius(int $$0) {
            this.searchRadius = $$0;
            return this;
        }

        public Builder setSkipKnownStructures(boolean $$0) {
            this.skipKnownStructures = $$0;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new ExplorationMapFunction(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<ExplorationMapFunction> {
        @Override
        public void serialize(JsonObject $$0, ExplorationMapFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            if (!$$1.destination.equals(DEFAULT_DESTINATION)) {
                $$0.addProperty("destination", $$1.destination.location().toString());
            }
            if ($$1.mapDecoration != DEFAULT_DECORATION) {
                $$0.add("decoration", $$2.serialize((Object)$$1.mapDecoration.toString().toLowerCase(Locale.ROOT)));
            }
            if ($$1.zoom != 2) {
                $$0.addProperty("zoom", (Number)Byte.valueOf((byte)$$1.zoom));
            }
            if ($$1.searchRadius != 50) {
                $$0.addProperty("search_radius", (Number)Integer.valueOf((int)$$1.searchRadius));
            }
            if (!$$1.skipKnownStructures) {
                $$0.addProperty("skip_existing_chunks", Boolean.valueOf((boolean)$$1.skipKnownStructures));
            }
        }

        @Override
        public ExplorationMapFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            TagKey<Structure> $$3 = Serializer.readStructure($$0);
            String $$4 = $$0.has("decoration") ? GsonHelper.getAsString($$0, "decoration") : ExplorationMapFunction.DEFAULT_DECORATION_NAME;
            MapDecoration.Type $$5 = DEFAULT_DECORATION;
            try {
                $$5 = MapDecoration.Type.valueOf($$4.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException $$6) {
                LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to {}", (Object)$$4, (Object)DEFAULT_DECORATION);
            }
            byte $$7 = GsonHelper.getAsByte($$0, "zoom", (byte)2);
            int $$8 = GsonHelper.getAsInt($$0, "search_radius", 50);
            boolean $$9 = GsonHelper.getAsBoolean($$0, "skip_existing_chunks", true);
            return new ExplorationMapFunction($$2, $$3, $$5, $$7, $$8, $$9);
        }

        private static TagKey<Structure> readStructure(JsonObject $$0) {
            if ($$0.has("destination")) {
                String $$1 = GsonHelper.getAsString($$0, "destination");
                return TagKey.create(Registries.STRUCTURE, new ResourceLocation($$1));
            }
            return DEFAULT_DESTINATION;
        }
    }
}