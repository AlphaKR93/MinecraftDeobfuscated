/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Set
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetAttributesFunction
extends LootItemConditionalFunction {
    final List<Modifier> modifiers;

    SetAttributesFunction(LootItemCondition[] $$0, List<Modifier> $$1) {
        super($$0);
        this.modifiers = ImmutableList.copyOf($$1);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_ATTRIBUTES;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set)this.modifiers.stream().flatMap($$0 -> $$0.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        RandomSource $$2 = $$1.getRandom();
        for (Modifier $$3 : this.modifiers) {
            UUID $$4 = $$3.id;
            if ($$4 == null) {
                $$4 = UUID.randomUUID();
            }
            EquipmentSlot $$5 = Util.getRandom($$3.slots, $$2);
            $$0.addAttributeModifier($$3.attribute, new AttributeModifier($$4, $$3.name, (double)$$3.amount.getFloat($$1), $$3.operation), $$5);
        }
        return $$0;
    }

    public static ModifierBuilder modifier(String $$0, Attribute $$1, AttributeModifier.Operation $$2, NumberProvider $$3) {
        return new ModifierBuilder($$0, $$1, $$2, $$3);
    }

    public static Builder setAttributes() {
        return new Builder();
    }

    static class Modifier {
        final String name;
        final Attribute attribute;
        final AttributeModifier.Operation operation;
        final NumberProvider amount;
        @Nullable
        final UUID id;
        final EquipmentSlot[] slots;

        Modifier(String $$0, Attribute $$1, AttributeModifier.Operation $$2, NumberProvider $$3, EquipmentSlot[] $$4, @Nullable UUID $$5) {
            this.name = $$0;
            this.attribute = $$1;
            this.operation = $$2;
            this.amount = $$3;
            this.id = $$5;
            this.slots = $$4;
        }

        public JsonObject serialize(JsonSerializationContext $$0) {
            JsonObject $$1 = new JsonObject();
            $$1.addProperty("name", this.name);
            $$1.addProperty("attribute", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
            $$1.addProperty("operation", Modifier.operationToString(this.operation));
            $$1.add("amount", $$0.serialize((Object)this.amount));
            if (this.id != null) {
                $$1.addProperty("id", this.id.toString());
            }
            if (this.slots.length == 1) {
                $$1.addProperty("slot", this.slots[0].getName());
            } else {
                JsonArray $$2 = new JsonArray();
                for (EquipmentSlot $$3 : this.slots) {
                    $$2.add((JsonElement)new JsonPrimitive($$3.getName()));
                }
                $$1.add("slot", (JsonElement)$$2);
            }
            return $$1;
        }

        /*
         * WARNING - void declaration
         */
        public static Modifier deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            void $$13;
            String $$2 = GsonHelper.getAsString($$0, "name");
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$0, "attribute"));
            Attribute $$4 = BuiltInRegistries.ATTRIBUTE.get($$3);
            if ($$4 == null) {
                throw new JsonSyntaxException("Unknown attribute: " + $$3);
            }
            AttributeModifier.Operation $$5 = Modifier.operationFromString(GsonHelper.getAsString($$0, "operation"));
            NumberProvider $$6 = GsonHelper.getAsObject($$0, "amount", $$1, NumberProvider.class);
            UUID $$7 = null;
            if (GsonHelper.isStringValue($$0, "slot")) {
                EquipmentSlot[] $$8 = new EquipmentSlot[]{EquipmentSlot.byName(GsonHelper.getAsString($$0, "slot"))};
            } else if (GsonHelper.isArrayNode($$0, "slot")) {
                JsonArray $$9 = GsonHelper.getAsJsonArray($$0, "slot");
                EquipmentSlot[] $$10 = new EquipmentSlot[$$9.size()];
                int $$11 = 0;
                for (JsonElement $$12 : $$9) {
                    $$10[$$11++] = EquipmentSlot.byName(GsonHelper.convertToString($$12, "slot"));
                }
                if ($$10.length == 0) {
                    throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                }
            } else {
                throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }
            if ($$0.has("id")) {
                String $$14 = GsonHelper.getAsString($$0, "id");
                try {
                    $$7 = UUID.fromString((String)$$14);
                }
                catch (IllegalArgumentException $$15) {
                    throw new JsonSyntaxException("Invalid attribute modifier id '" + $$14 + "' (must be UUID format, with dashes)");
                }
            }
            return new Modifier($$2, $$4, $$5, $$6, (EquipmentSlot[])$$13, $$7);
        }

        private static String operationToString(AttributeModifier.Operation $$0) {
            switch ($$0) {
                case ADDITION: {
                    return "addition";
                }
                case MULTIPLY_BASE: {
                    return "multiply_base";
                }
                case MULTIPLY_TOTAL: {
                    return "multiply_total";
                }
            }
            throw new IllegalArgumentException("Unknown operation " + $$0);
        }

        /*
         * Exception decompiling
         */
        private static AttributeModifier.Operation operationFromString(String $$0) {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$TooOptimisticMatchException
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.getString(SwitchStringRewriter.java:404)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.access$600(SwitchStringRewriter.java:53)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$SwitchStringMatchResultCollector.collectMatches(SwitchStringRewriter.java:368)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:24)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.KleeneN.match(KleeneN.java:24)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.MatchSequence.match(MatchSequence.java:26)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:23)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewriteComplex(SwitchStringRewriter.java:201)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewrite(SwitchStringRewriter.java:73)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:881)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at cuchaz.enigma.source.cfr.CfrSource.ensureDecompiled(CfrSource.java:81)
             *     at cuchaz.enigma.source.cfr.CfrSource.asString(CfrSource.java:50)
             *     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
             *     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
             *     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
             *     at java.base/java.util.AbstractList$RandomAccessSpliterator.forEachRemaining(AbstractList.java:722)
             *     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
             *     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
             *     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:754)
             *     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
             *     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1311)
             *     at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1840)
             *     at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1806)
             *     at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
             */
            throw new IllegalStateException("Decompilation failed");
        }
    }

    public static class ModifierBuilder {
        private final String name;
        private final Attribute attribute;
        private final AttributeModifier.Operation operation;
        private final NumberProvider amount;
        @Nullable
        private UUID id;
        private final Set<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

        public ModifierBuilder(String $$0, Attribute $$1, AttributeModifier.Operation $$2, NumberProvider $$3) {
            this.name = $$0;
            this.attribute = $$1;
            this.operation = $$2;
            this.amount = $$3;
        }

        public ModifierBuilder forSlot(EquipmentSlot $$0) {
            this.slots.add((Object)$$0);
            return this;
        }

        public ModifierBuilder withUuid(UUID $$0) {
            this.id = $$0;
            return this;
        }

        public Modifier build() {
            return new Modifier(this.name, this.attribute, this.operation, this.amount, (EquipmentSlot[])this.slots.toArray((Object[])new EquipmentSlot[0]), this.id);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final List<Modifier> modifiers = Lists.newArrayList();

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withModifier(ModifierBuilder $$0) {
            this.modifiers.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetAttributesFunction(this.getConditions(), this.modifiers);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetAttributesFunction> {
        @Override
        public void serialize(JsonObject $$0, SetAttributesFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            JsonArray $$3 = new JsonArray();
            for (Modifier $$4 : $$1.modifiers) {
                $$3.add((JsonElement)$$4.serialize($$2));
            }
            $$0.add("modifiers", (JsonElement)$$3);
        }

        @Override
        public SetAttributesFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            JsonArray $$3 = GsonHelper.getAsJsonArray($$0, "modifiers");
            ArrayList $$4 = Lists.newArrayListWithExpectedSize((int)$$3.size());
            for (JsonElement $$5 : $$3) {
                $$4.add((Object)Modifier.deserialize(GsonHelper.convertToJsonObject($$5, "modifier"), $$1));
            }
            if ($$4.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            }
            return new SetAttributesFunction($$2, (List<Modifier>)$$4);
        }
    }
}