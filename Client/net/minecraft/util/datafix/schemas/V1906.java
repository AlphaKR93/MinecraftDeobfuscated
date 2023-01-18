/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Supplier
 */
package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1906
extends NamespacedSchema {
    public V1906(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
        Map $$12 = super.registerBlockEntities($$0);
        V1906.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:barrel");
        V1906.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:smoker");
        V1906.registerInventory($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:blast_furnace");
        $$0.register($$12, "minecraft:lectern", $$1 -> DSL.optionalFields((String)"Book", (TypeTemplate)References.ITEM_STACK.in($$0)));
        $$0.registerSimple($$12, "minecraft:bell");
        return $$12;
    }

    protected static void registerInventory(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0))));
    }
}