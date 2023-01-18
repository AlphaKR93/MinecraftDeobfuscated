/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class LootTable {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, new LootPool[0], new LootItemFunction[0]);
    public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
    final LootContextParamSet paramSet;
    final LootPool[] pools;
    final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

    LootTable(LootContextParamSet $$0, LootPool[] $$1, LootItemFunction[] $$2) {
        this.paramSet = $$0;
        this.pools = $$1;
        this.functions = $$2;
        this.compositeFunction = LootItemFunctions.compose($$2);
    }

    public static Consumer<ItemStack> createStackSplitter(LootContext $$0, Consumer<ItemStack> $$1) {
        return $$2 -> {
            if (!$$2.isItemEnabled($$0.getLevel().enabledFeatures())) {
                return;
            }
            if ($$2.getCount() < $$2.getMaxStackSize()) {
                $$1.accept($$2);
            } else {
                ItemStack $$4;
                for (int $$3 = $$2.getCount(); $$3 > 0; $$3 -= $$4.getCount()) {
                    $$4 = $$2.copy();
                    $$4.setCount(Math.min((int)$$2.getMaxStackSize(), (int)$$3));
                    $$1.accept((Object)$$4);
                }
            }
        };
    }

    public void getRandomItemsRaw(LootContext $$0, Consumer<ItemStack> $$1) {
        if ($$0.addVisitedTable(this)) {
            Consumer<ItemStack> $$2 = LootItemFunction.decorate(this.compositeFunction, $$1, $$0);
            for (LootPool $$3 : this.pools) {
                $$3.addRandomItems($$2, $$0);
            }
            $$0.removeVisitedTable(this);
        } else {
            LOGGER.warn("Detected infinite loop in loot tables");
        }
    }

    public void getRandomItems(LootContext $$0, Consumer<ItemStack> $$1) {
        this.getRandomItemsRaw($$0, LootTable.createStackSplitter($$0, $$1));
    }

    public ObjectArrayList<ItemStack> getRandomItems(LootContext $$0) {
        ObjectArrayList $$1 = new ObjectArrayList();
        this.getRandomItems($$0, (Consumer<ItemStack>)((Consumer)arg_0 -> ((ObjectArrayList)$$1).add(arg_0)));
        return $$1;
    }

    public LootContextParamSet getParamSet() {
        return this.paramSet;
    }

    public void validate(ValidationContext $$0) {
        for (int $$1 = 0; $$1 < this.pools.length; ++$$1) {
            this.pools[$$1].validate($$0.forChild(".pools[" + $$1 + "]"));
        }
        for (int $$2 = 0; $$2 < this.functions.length; ++$$2) {
            this.functions[$$2].validate($$0.forChild(".functions[" + $$2 + "]"));
        }
    }

    public void fill(Container $$0, LootContext $$1) {
        ObjectArrayList<ItemStack> $$2 = this.getRandomItems($$1);
        RandomSource $$3 = $$1.getRandom();
        List<Integer> $$4 = this.getAvailableSlots($$0, $$3);
        this.shuffleAndSplitItems($$2, $$4.size(), $$3);
        for (ItemStack $$5 : $$2) {
            if ($$4.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if ($$5.isEmpty()) {
                $$0.setItem((Integer)$$4.remove($$4.size() - 1), ItemStack.EMPTY);
                continue;
            }
            $$0.setItem((Integer)$$4.remove($$4.size() - 1), $$5);
        }
    }

    private void shuffleAndSplitItems(ObjectArrayList<ItemStack> $$0, int $$1, RandomSource $$2) {
        ArrayList $$3 = Lists.newArrayList();
        ObjectListIterator $$4 = $$0.iterator();
        while ($$4.hasNext()) {
            ItemStack $$5 = (ItemStack)$$4.next();
            if ($$5.isEmpty()) {
                $$4.remove();
                continue;
            }
            if ($$5.getCount() <= 1) continue;
            $$3.add((Object)$$5);
            $$4.remove();
        }
        while ($$1 - $$0.size() - $$3.size() > 0 && !$$3.isEmpty()) {
            ItemStack $$6 = (ItemStack)$$3.remove(Mth.nextInt($$2, 0, $$3.size() - 1));
            int $$7 = Mth.nextInt($$2, 1, $$6.getCount() / 2);
            ItemStack $$8 = $$6.split($$7);
            if ($$6.getCount() > 1 && $$2.nextBoolean()) {
                $$3.add((Object)$$6);
            } else {
                $$0.add((Object)$$6);
            }
            if ($$8.getCount() > 1 && $$2.nextBoolean()) {
                $$3.add((Object)$$8);
                continue;
            }
            $$0.add((Object)$$8);
        }
        $$0.addAll((Collection)$$3);
        Util.shuffle($$0, $$2);
    }

    private List<Integer> getAvailableSlots(Container $$0, RandomSource $$1) {
        ObjectArrayList $$2 = new ObjectArrayList();
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            if (!$$0.getItem($$3).isEmpty()) continue;
            $$2.add((Object)$$3);
        }
        Util.shuffle($$2, $$1);
        return $$2;
    }

    public static Builder lootTable() {
        return new Builder();
    }

    public static class Builder
    implements FunctionUserBuilder<Builder> {
        private final List<LootPool> pools = Lists.newArrayList();
        private final List<LootItemFunction> functions = Lists.newArrayList();
        private LootContextParamSet paramSet = DEFAULT_PARAM_SET;

        public Builder withPool(LootPool.Builder $$0) {
            this.pools.add((Object)$$0.build());
            return this;
        }

        public Builder setParamSet(LootContextParamSet $$0) {
            this.paramSet = $$0;
            return this;
        }

        @Override
        public Builder apply(LootItemFunction.Builder $$0) {
            this.functions.add((Object)$$0.build());
            return this;
        }

        @Override
        public Builder unwrap() {
            return this;
        }

        public LootTable build() {
            return new LootTable(this.paramSet, (LootPool[])this.pools.toArray((Object[])new LootPool[0]), (LootItemFunction[])this.functions.toArray((Object[])new LootItemFunction[0]));
        }
    }

    public static class Serializer
    implements JsonDeserializer<LootTable>,
    JsonSerializer<LootTable> {
        public LootTable deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "loot table");
            LootPool[] $$4 = GsonHelper.getAsObject($$3, "pools", new LootPool[0], $$2, LootPool[].class);
            LootContextParamSet $$5 = null;
            if ($$3.has("type")) {
                String $$6 = GsonHelper.getAsString($$3, "type");
                $$5 = LootContextParamSets.get(new ResourceLocation($$6));
            }
            LootItemFunction[] $$7 = GsonHelper.getAsObject($$3, "functions", new LootItemFunction[0], $$2, LootItemFunction[].class);
            return new LootTable($$5 != null ? $$5 : LootContextParamSets.ALL_PARAMS, $$4, $$7);
        }

        public JsonElement serialize(LootTable $$0, Type $$1, JsonSerializationContext $$2) {
            JsonObject $$3 = new JsonObject();
            if ($$0.paramSet != DEFAULT_PARAM_SET) {
                ResourceLocation $$4 = LootContextParamSets.getKey($$0.paramSet);
                if ($$4 != null) {
                    $$3.addProperty("type", $$4.toString());
                } else {
                    LOGGER.warn("Failed to find id for param set {}", (Object)$$0.paramSet);
                }
            }
            if ($$0.pools.length > 0) {
                $$3.add("pools", $$2.serialize((Object)$$0.pools));
            }
            if (!ArrayUtils.isEmpty((Object[])$$0.functions)) {
                $$3.add("functions", $$2.serialize((Object)$$0.functions));
            }
            return $$3;
        }
    }
}