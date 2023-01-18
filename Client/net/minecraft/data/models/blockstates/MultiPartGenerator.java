/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPartGenerator
implements BlockStateGenerator {
    private final Block block;
    private final List<Entry> parts = Lists.newArrayList();

    private MultiPartGenerator(Block $$0) {
        this.block = $$0;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultiPartGenerator multiPart(Block $$0) {
        return new MultiPartGenerator($$0);
    }

    public MultiPartGenerator with(List<Variant> $$0) {
        this.parts.add((Object)new Entry($$0));
        return this;
    }

    public MultiPartGenerator with(Variant $$0) {
        return this.with((List<Variant>)ImmutableList.of((Object)$$0));
    }

    public MultiPartGenerator with(Condition $$0, List<Variant> $$1) {
        this.parts.add((Object)new ConditionalEntry($$0, $$1));
        return this;
    }

    public MultiPartGenerator with(Condition $$0, Variant ... $$1) {
        return this.with($$0, (List<Variant>)ImmutableList.copyOf((Object[])$$1));
    }

    public MultiPartGenerator with(Condition $$0, Variant $$1) {
        return this.with($$0, (List<Variant>)ImmutableList.of((Object)$$1));
    }

    public JsonElement get() {
        StateDefinition<Block, BlockState> $$0 = this.block.getStateDefinition();
        this.parts.forEach($$1 -> $$1.validate($$0));
        JsonArray $$12 = new JsonArray();
        this.parts.stream().map(Entry::get).forEach(arg_0 -> ((JsonArray)$$12).add(arg_0));
        JsonObject $$2 = new JsonObject();
        $$2.add("multipart", (JsonElement)$$12);
        return $$2;
    }

    static class Entry
    implements Supplier<JsonElement> {
        private final List<Variant> variants;

        Entry(List<Variant> $$0) {
            this.variants = $$0;
        }

        public void validate(StateDefinition<?, ?> $$0) {
        }

        public void decorate(JsonObject $$0) {
        }

        public JsonElement get() {
            JsonObject $$0 = new JsonObject();
            this.decorate($$0);
            $$0.add("apply", Variant.convertList(this.variants));
            return $$0;
        }
    }

    static class ConditionalEntry
    extends Entry {
        private final Condition condition;

        ConditionalEntry(Condition $$0, List<Variant> $$1) {
            super($$1);
            this.condition = $$0;
        }

        @Override
        public void validate(StateDefinition<?, ?> $$0) {
            this.condition.validate($$0);
        }

        @Override
        public void decorate(JsonObject $$0) {
            $$0.add("when", (JsonElement)this.condition.get());
        }
    }
}