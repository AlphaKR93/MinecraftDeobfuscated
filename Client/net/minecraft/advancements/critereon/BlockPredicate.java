/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate {
    public static final BlockPredicate ANY = new BlockPredicate(null, null, StatePropertiesPredicate.ANY, NbtPredicate.ANY);
    @Nullable
    private final TagKey<Block> tag;
    @Nullable
    private final Set<Block> blocks;
    private final StatePropertiesPredicate properties;
    private final NbtPredicate nbt;

    public BlockPredicate(@Nullable TagKey<Block> $$0, @Nullable Set<Block> $$1, StatePropertiesPredicate $$2, NbtPredicate $$3) {
        this.tag = $$0;
        this.blocks = $$1;
        this.properties = $$2;
        this.nbt = $$3;
    }

    public boolean matches(ServerLevel $$0, BlockPos $$1) {
        BlockEntity $$3;
        if (this == ANY) {
            return true;
        }
        if (!$$0.isLoaded($$1)) {
            return false;
        }
        BlockState $$2 = $$0.getBlockState($$1);
        if (this.tag != null && !$$2.is(this.tag)) {
            return false;
        }
        if (this.blocks != null && !this.blocks.contains((Object)$$2.getBlock())) {
            return false;
        }
        if (!this.properties.matches($$2)) {
            return false;
        }
        return this.nbt == NbtPredicate.ANY || ($$3 = $$0.getBlockEntity($$1)) != null && this.nbt.matches($$3.saveWithFullMetadata());
    }

    public static BlockPredicate fromJson(@Nullable JsonElement $$0) {
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "block");
        NbtPredicate $$2 = NbtPredicate.fromJson($$1.get("nbt"));
        ImmutableSet $$3 = null;
        JsonArray $$4 = GsonHelper.getAsJsonArray($$1, "blocks", null);
        if ($$4 != null) {
            ImmutableSet.Builder $$5 = ImmutableSet.builder();
            for (JsonElement $$6 : $$4) {
                ResourceLocation $$7 = new ResourceLocation(GsonHelper.convertToString($$6, "block"));
                $$5.add((Object)((Block)BuiltInRegistries.BLOCK.getOptional($$7).orElseThrow(() -> new JsonSyntaxException("Unknown block id '" + $$7 + "'"))));
            }
            $$3 = $$5.build();
        }
        TagKey<Block> $$8 = null;
        if ($$1.has("tag")) {
            ResourceLocation $$9 = new ResourceLocation(GsonHelper.getAsString($$1, "tag"));
            $$8 = TagKey.create(Registries.BLOCK, $$9);
        }
        StatePropertiesPredicate $$10 = StatePropertiesPredicate.fromJson($$1.get("state"));
        return new BlockPredicate($$8, (Set<Block>)$$3, $$10, $$2);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject $$0 = new JsonObject();
        if (this.blocks != null) {
            JsonArray $$1 = new JsonArray();
            for (Block $$2 : this.blocks) {
                $$1.add(BuiltInRegistries.BLOCK.getKey($$2).toString());
            }
            $$0.add("blocks", (JsonElement)$$1);
        }
        if (this.tag != null) {
            $$0.addProperty("tag", this.tag.location().toString());
        }
        $$0.add("nbt", this.nbt.serializeToJson());
        $$0.add("state", this.properties.serializeToJson());
        return $$0;
    }

    public static class Builder {
        @Nullable
        private Set<Block> blocks;
        @Nullable
        private TagKey<Block> tag;
        private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {
        }

        public static Builder block() {
            return new Builder();
        }

        public Builder of(Block ... $$0) {
            this.blocks = ImmutableSet.copyOf((Object[])$$0);
            return this;
        }

        public Builder of(Iterable<Block> $$0) {
            this.blocks = ImmutableSet.copyOf($$0);
            return this;
        }

        public Builder of(TagKey<Block> $$0) {
            this.tag = $$0;
            return this;
        }

        public Builder hasNbt(CompoundTag $$0) {
            this.nbt = new NbtPredicate($$0);
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate $$0) {
            this.properties = $$0;
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(this.tag, this.blocks, this.properties, this.nbt);
        }
    }
}