/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Pair
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.TreeMap
 *  java.util.stream.Stream
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Selector;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class MultiVariantGenerator
implements BlockStateGenerator {
    private final Block block;
    private final List<Variant> baseVariants;
    private final Set<Property<?>> seenProperties = Sets.newHashSet();
    private final List<PropertyDispatch> declaredPropertySets = Lists.newArrayList();

    private MultiVariantGenerator(Block $$0, List<Variant> $$1) {
        this.block = $$0;
        this.baseVariants = $$1;
    }

    public MultiVariantGenerator with(PropertyDispatch $$02) {
        $$02.getDefinedProperties().forEach($$0 -> {
            if (this.block.getStateDefinition().getProperty($$0.getName()) != $$0) {
                throw new IllegalStateException("Property " + $$0 + " is not defined for block " + this.block);
            }
            if (!this.seenProperties.add($$0)) {
                throw new IllegalStateException("Values of property " + $$0 + " already defined for block " + this.block);
            }
        });
        this.declaredPropertySets.add((Object)$$02);
        return this;
    }

    public JsonElement get() {
        Stream $$0 = Stream.of((Object)Pair.of((Object)Selector.empty(), this.baseVariants));
        for (PropertyDispatch $$1 : this.declaredPropertySets) {
            Map<Selector, List<Variant>> $$2 = $$1.getEntries();
            $$0 = $$0.flatMap($$12 -> $$2.entrySet().stream().map($$1 -> {
                Selector $$2 = ((Selector)$$12.getFirst()).extend((Selector)$$1.getKey());
                List<Variant> $$3 = MultiVariantGenerator.mergeVariants((List<Variant>)((List)$$12.getSecond()), (List<Variant>)((List)$$1.getValue()));
                return Pair.of((Object)$$2, $$3);
            }));
        }
        TreeMap $$3 = new TreeMap();
        $$0.forEach(arg_0 -> MultiVariantGenerator.lambda$get$3((Map)$$3, arg_0));
        JsonObject $$4 = new JsonObject();
        $$4.add("variants", (JsonElement)Util.make(new JsonObject(), arg_0 -> MultiVariantGenerator.lambda$get$4((Map)$$3, arg_0)));
        return $$4;
    }

    private static List<Variant> mergeVariants(List<Variant> $$0, List<Variant> $$1) {
        ImmutableList.Builder $$2 = ImmutableList.builder();
        $$0.forEach($$22 -> $$1.forEach($$2 -> $$2.add((Object)Variant.merge($$22, $$2))));
        return $$2.build();
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultiVariantGenerator multiVariant(Block $$0) {
        return new MultiVariantGenerator($$0, (List<Variant>)ImmutableList.of((Object)Variant.variant()));
    }

    public static MultiVariantGenerator multiVariant(Block $$0, Variant $$1) {
        return new MultiVariantGenerator($$0, (List<Variant>)ImmutableList.of((Object)$$1));
    }

    public static MultiVariantGenerator multiVariant(Block $$0, Variant ... $$1) {
        return new MultiVariantGenerator($$0, (List<Variant>)ImmutableList.copyOf((Object[])$$1));
    }

    private static /* synthetic */ void lambda$get$4(Map $$0, JsonObject $$1) {
        $$0.forEach((arg_0, arg_1) -> ((JsonObject)$$1).add(arg_0, arg_1));
    }

    private static /* synthetic */ void lambda$get$3(Map $$0, Pair $$1) {
        $$0.put((Object)((Selector)$$1.getFirst()).getKey(), (Object)Variant.convertList((List<Variant>)((List)$$1.getSecond())));
    }
}