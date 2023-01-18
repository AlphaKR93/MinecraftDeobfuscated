/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Supplier
 */
package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1451_6
extends NamespacedSchema {
    public static final String SPECIAL_OBJECTIVE_MARKER = "_special";
    protected static final Hook.HookFunction UNPACK_OBJECTIVE_ID = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> $$02, T $$12) {
            Dynamic $$2 = new Dynamic($$02, $$12);
            return (T)((Dynamic)DataFixUtils.orElse((Optional)$$2.get("CriteriaName").asString().get().left().map($$0 -> {
                int $$1 = $$0.indexOf(58);
                if ($$1 < 0) {
                    return Pair.of((Object)V1451_6.SPECIAL_OBJECTIVE_MARKER, (Object)$$0);
                }
                try {
                    ResourceLocation $$2 = ResourceLocation.of($$0.substring(0, $$1), '.');
                    ResourceLocation $$3 = ResourceLocation.of($$0.substring($$1 + 1), '.');
                    return Pair.of((Object)$$2.toString(), (Object)$$3.toString());
                }
                catch (Exception $$4) {
                    return Pair.of((Object)V1451_6.SPECIAL_OBJECTIVE_MARKER, (Object)$$0);
                }
            }).map($$1 -> $$2.set("CriteriaType", $$2.createMap((Map)ImmutableMap.of((Object)$$2.createString("type"), (Object)$$2.createString((String)$$1.getFirst()), (Object)$$2.createString("id"), (Object)$$2.createString((String)$$1.getSecond()))))), (Object)$$2)).getValue();
        }
    };
    protected static final Hook.HookFunction REPACK_OBJECTIVE_ID = new Hook.HookFunction(){

        private String packWithDot(String $$0) {
            ResourceLocation $$1 = ResourceLocation.tryParse($$0);
            return $$1 != null ? $$1.getNamespace() + "." + $$1.getPath() : $$0;
        }

        public <T> T apply(DynamicOps<T> $$0, T $$12) {
            Dynamic $$2 = new Dynamic($$0, $$12);
            Optional $$3 = $$2.get("CriteriaType").get().get().left().flatMap($$1 -> {
                Optional $$2 = $$1.get("type").asString().get().left();
                Optional $$3 = $$1.get("id").asString().get().left();
                if ($$2.isPresent() && $$3.isPresent()) {
                    String $$4 = (String)$$2.get();
                    if ($$4.equals((Object)V1451_6.SPECIAL_OBJECTIVE_MARKER)) {
                        return Optional.of((Object)$$2.createString((String)$$3.get()));
                    }
                    return Optional.of((Object)$$1.createString(this.packWithDot($$4) + ":" + this.packWithDot((String)$$3.get())));
                }
                return Optional.empty();
            });
            return (T)((Dynamic)DataFixUtils.orElse((Optional)$$3.map($$1 -> $$2.set("CriteriaName", $$1).remove("CriteriaType")), (Object)$$2)).getValue();
        }
    };

    public V1451_6(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
        super.registerTypes($$0, $$1, $$2);
        Supplier $$3 = () -> DSL.compoundList((TypeTemplate)References.ITEM_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType()));
        $$0.registerType(false, References.STATS, () -> V1451_6.lambda$registerTypes$1($$0, (Supplier)$$3));
        Map<String, Supplier<TypeTemplate>> $$4 = V1451_6.createCriterionTypes($$0);
        $$0.registerType(false, References.OBJECTIVE, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"CriteriaType", (TypeTemplate)DSL.taggedChoiceLazy((String)"type", (Type)DSL.string(), (Map)$$4)), (Hook.HookFunction)UNPACK_OBJECTIVE_ID, (Hook.HookFunction)REPACK_OBJECTIVE_ID));
    }

    protected static Map<String, Supplier<TypeTemplate>> createCriterionTypes(Schema $$0) {
        Supplier $$1 = () -> DSL.optionalFields((String)"id", (TypeTemplate)References.ITEM_NAME.in($$0));
        Supplier $$2 = () -> DSL.optionalFields((String)"id", (TypeTemplate)References.BLOCK_NAME.in($$0));
        Supplier $$3 = () -> DSL.optionalFields((String)"id", (TypeTemplate)References.ENTITY_NAME.in($$0));
        HashMap $$4 = Maps.newHashMap();
        $$4.put((Object)"minecraft:mined", (Object)$$2);
        $$4.put((Object)"minecraft:crafted", (Object)$$1);
        $$4.put((Object)"minecraft:used", (Object)$$1);
        $$4.put((Object)"minecraft:broken", (Object)$$1);
        $$4.put((Object)"minecraft:picked_up", (Object)$$1);
        $$4.put((Object)"minecraft:dropped", (Object)$$1);
        $$4.put((Object)"minecraft:killed", (Object)$$3);
        $$4.put((Object)"minecraft:killed_by", (Object)$$3);
        $$4.put((Object)"minecraft:custom", () -> DSL.optionalFields((String)"id", (TypeTemplate)DSL.constType(V1451_6.namespacedString())));
        $$4.put((Object)SPECIAL_OBJECTIVE_MARKER, () -> DSL.optionalFields((String)"id", (TypeTemplate)DSL.constType((Type)DSL.string())));
        return $$4;
    }

    private static /* synthetic */ TypeTemplate lambda$registerTypes$1(Schema $$0, Supplier $$1) {
        return DSL.optionalFields((String)"stats", (TypeTemplate)DSL.optionalFields((String)"minecraft:mined", (TypeTemplate)DSL.compoundList((TypeTemplate)References.BLOCK_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:crafted", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:used", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:broken", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:picked_up", (TypeTemplate)((TypeTemplate)$$1.get()), (TypeTemplate)DSL.optionalFields((String)"minecraft:dropped", (TypeTemplate)((TypeTemplate)$$1.get()), (String)"minecraft:killed", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:killed_by", (TypeTemplate)DSL.compoundList((TypeTemplate)References.ENTITY_NAME.in($$0), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:custom", (TypeTemplate)DSL.compoundList((TypeTemplate)DSL.constType(V1451_6.namespacedString()), (TypeTemplate)DSL.constType((Type)DSL.intType())))));
    }
}