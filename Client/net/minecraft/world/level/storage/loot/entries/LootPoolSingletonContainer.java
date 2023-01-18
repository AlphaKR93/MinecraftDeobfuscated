/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.FunctionalInterface
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolSingletonContainer
extends LootPoolEntryContainer {
    public static final int DEFAULT_WEIGHT = 1;
    public static final int DEFAULT_QUALITY = 0;
    protected final int weight;
    protected final int quality;
    protected final LootItemFunction[] functions;
    final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    private final LootPoolEntry entry = new EntryBase(){

        @Override
        public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
            LootPoolSingletonContainer.this.createItemStack(LootItemFunction.decorate(LootPoolSingletonContainer.this.compositeFunction, $$0, $$1), $$1);
        }
    };

    protected LootPoolSingletonContainer(int $$0, int $$1, LootItemCondition[] $$2, LootItemFunction[] $$3) {
        super($$2);
        this.weight = $$0;
        this.quality = $$1;
        this.functions = $$3;
        this.compositeFunction = LootItemFunctions.compose($$3);
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        for (int $$1 = 0; $$1 < this.functions.length; ++$$1) {
            this.functions[$$1].validate($$0.forChild(".functions[" + $$1 + "]"));
        }
    }

    protected abstract void createItemStack(Consumer<ItemStack> var1, LootContext var2);

    @Override
    public boolean expand(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (this.canRun($$0)) {
            $$1.accept((Object)this.entry);
            return true;
        }
        return false;
    }

    public static Builder<?> simpleBuilder(EntryConstructor $$0) {
        return new DummyBuilder($$0);
    }

    static class DummyBuilder
    extends Builder<DummyBuilder> {
        private final EntryConstructor constructor;

        public DummyBuilder(EntryConstructor $$0) {
            this.constructor = $$0;
        }

        @Override
        protected DummyBuilder getThis() {
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }
    }

    @FunctionalInterface
    protected static interface EntryConstructor {
        public LootPoolSingletonContainer build(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4);
    }

    public static abstract class Serializer<T extends LootPoolSingletonContainer>
    extends LootPoolEntryContainer.Serializer<T> {
        @Override
        public void serialize(JsonObject $$0, T $$1, JsonSerializationContext $$2) {
            if (((LootPoolSingletonContainer)$$1).weight != 1) {
                $$0.addProperty("weight", (Number)Integer.valueOf((int)((LootPoolSingletonContainer)$$1).weight));
            }
            if (((LootPoolSingletonContainer)$$1).quality != 0) {
                $$0.addProperty("quality", (Number)Integer.valueOf((int)((LootPoolSingletonContainer)$$1).quality));
            }
            if (!ArrayUtils.isEmpty((Object[])((LootPoolSingletonContainer)$$1).functions)) {
                $$0.add("functions", $$2.serialize((Object)((LootPoolSingletonContainer)$$1).functions));
            }
        }

        @Override
        public final T deserializeCustom(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            int $$3 = GsonHelper.getAsInt($$0, "weight", 1);
            int $$4 = GsonHelper.getAsInt($$0, "quality", 0);
            LootItemFunction[] $$5 = GsonHelper.getAsObject($$0, "functions", new LootItemFunction[0], $$1, LootItemFunction[].class);
            return this.deserialize($$0, $$1, $$3, $$4, $$2, $$5);
        }

        protected abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6);
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootPoolEntryContainer.Builder<T>
    implements FunctionUserBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final List<LootItemFunction> functions = Lists.newArrayList();

        @Override
        public T apply(LootItemFunction.Builder $$0) {
            this.functions.add((Object)$$0.build());
            return (T)((Builder)this.getThis());
        }

        protected LootItemFunction[] getFunctions() {
            return (LootItemFunction[])this.functions.toArray((Object[])new LootItemFunction[0]);
        }

        public T setWeight(int $$0) {
            this.weight = $$0;
            return (T)((Builder)this.getThis());
        }

        public T setQuality(int $$0) {
            this.quality = $$0;
            return (T)((Builder)this.getThis());
        }

        @Override
        public /* synthetic */ FunctionUserBuilder unwrap() {
            return (FunctionUserBuilder)((Object)super.unwrap());
        }
    }

    protected abstract class EntryBase
    implements LootPoolEntry {
        protected EntryBase() {
        }

        @Override
        public int getWeight(float $$0) {
            return Math.max((int)Mth.floor((float)LootPoolSingletonContainer.this.weight + (float)LootPoolSingletonContainer.this.quality * $$0), (int)0);
        }
    }
}