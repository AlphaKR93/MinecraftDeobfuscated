/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;

public class CopyNbtFunction
extends LootItemConditionalFunction {
    final NbtProvider source;
    final List<CopyOperation> operations;

    CopyNbtFunction(LootItemCondition[] $$0, NbtProvider $$1, List<CopyOperation> $$2) {
        super($$0);
        this.source = $$1;
        this.operations = ImmutableList.copyOf($$2);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_NBT;
    }

    static NbtPathArgument.NbtPath compileNbtPath(String $$0) {
        try {
            return new NbtPathArgument().parse(new StringReader($$0));
        }
        catch (CommandSyntaxException $$1) {
            throw new IllegalArgumentException("Failed to parse path " + $$0, (Throwable)$$1);
        }
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.source.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Tag $$22 = this.source.get($$1);
        if ($$22 != null) {
            this.operations.forEach($$2 -> $$2.apply((Supplier<Tag>)((Supplier)$$0::getOrCreateTag), $$22));
        }
        return $$0;
    }

    public static Builder copyData(NbtProvider $$0) {
        return new Builder($$0);
    }

    public static Builder copyData(LootContext.EntityTarget $$0) {
        return new Builder(ContextNbtProvider.forContextEntity($$0));
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final NbtProvider source;
        private final List<CopyOperation> ops = Lists.newArrayList();

        Builder(NbtProvider $$0) {
            this.source = $$0;
        }

        public Builder copy(String $$0, String $$1, MergeStrategy $$2) {
            this.ops.add((Object)new CopyOperation($$0, $$1, $$2));
            return this;
        }

        public Builder copy(String $$0, String $$1) {
            return this.copy($$0, $$1, MergeStrategy.REPLACE);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyNbtFunction(this.getConditions(), this.source, this.ops);
        }
    }

    static class CopyOperation {
        private final String sourcePathText;
        private final NbtPathArgument.NbtPath sourcePath;
        private final String targetPathText;
        private final NbtPathArgument.NbtPath targetPath;
        private final MergeStrategy op;

        CopyOperation(String $$0, String $$1, MergeStrategy $$2) {
            this.sourcePathText = $$0;
            this.sourcePath = CopyNbtFunction.compileNbtPath($$0);
            this.targetPathText = $$1;
            this.targetPath = CopyNbtFunction.compileNbtPath($$1);
            this.op = $$2;
        }

        public void apply(Supplier<Tag> $$0, Tag $$1) {
            try {
                List<Tag> $$2 = this.sourcePath.get($$1);
                if (!$$2.isEmpty()) {
                    this.op.merge((Tag)$$0.get(), this.targetPath, $$2);
                }
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }

        public JsonObject toJson() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("source", this.sourcePathText);
            $$0.addProperty("target", this.targetPathText);
            $$0.addProperty("op", this.op.name);
            return $$0;
        }

        public static CopyOperation fromJson(JsonObject $$0) {
            String $$1 = GsonHelper.getAsString($$0, "source");
            String $$2 = GsonHelper.getAsString($$0, "target");
            MergeStrategy $$3 = MergeStrategy.getByName(GsonHelper.getAsString($$0, "op"));
            return new CopyOperation($$1, $$2, $$3);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<CopyNbtFunction> {
        @Override
        public void serialize(JsonObject $$0, CopyNbtFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.add("source", $$2.serialize((Object)$$1.source));
            JsonArray $$3 = new JsonArray();
            $$1.operations.stream().map(CopyOperation::toJson).forEach(arg_0 -> ((JsonArray)$$3).add(arg_0));
            $$0.add("ops", (JsonElement)$$3);
        }

        @Override
        public CopyNbtFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            NbtProvider $$3 = GsonHelper.getAsObject($$0, "source", $$1, NbtProvider.class);
            ArrayList $$4 = Lists.newArrayList();
            JsonArray $$5 = GsonHelper.getAsJsonArray($$0, "ops");
            for (JsonElement $$6 : $$5) {
                JsonObject $$7 = GsonHelper.convertToJsonObject($$6, "op");
                $$4.add((Object)CopyOperation.fromJson($$7));
            }
            return new CopyNbtFunction($$2, $$3, (List<CopyOperation>)$$4);
        }
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum MergeStrategy {
        REPLACE("replace"){

            @Override
            public void merge(Tag $$0, NbtPathArgument.NbtPath $$1, List<Tag> $$2) throws CommandSyntaxException {
                $$1.set($$0, (Tag)Iterables.getLast($$2));
            }
        }
        ,
        APPEND("append"){

            @Override
            public void merge(Tag $$0, NbtPathArgument.NbtPath $$1, List<Tag> $$2) throws CommandSyntaxException {
                List<Tag> $$3 = $$1.getOrCreate($$0, (Supplier<Tag>)((Supplier)ListTag::new));
                $$3.forEach($$12 -> {
                    if ($$12 instanceof ListTag) {
                        $$2.forEach($$1 -> ((ListTag)$$12).add($$1.copy()));
                    }
                });
            }
        }
        ,
        MERGE("merge"){

            @Override
            public void merge(Tag $$0, NbtPathArgument.NbtPath $$1, List<Tag> $$2) throws CommandSyntaxException {
                List<Tag> $$3 = $$1.getOrCreate($$0, (Supplier<Tag>)((Supplier)CompoundTag::new));
                $$3.forEach($$12 -> {
                    if ($$12 instanceof CompoundTag) {
                        $$2.forEach($$1 -> {
                            if ($$1 instanceof CompoundTag) {
                                ((CompoundTag)$$12).merge((CompoundTag)$$1);
                            }
                        });
                    }
                });
            }
        };

        final String name;

        public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        MergeStrategy(String $$0) {
            this.name = $$0;
        }

        public static MergeStrategy getByName(String $$0) {
            for (MergeStrategy $$1 : MergeStrategy.values()) {
                if (!$$1.name.equals((Object)$$0)) continue;
                return $$1;
            }
            throw new IllegalArgumentException("Invalid merge strategy" + $$0);
        }
    }
}