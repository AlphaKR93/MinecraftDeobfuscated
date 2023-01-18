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
import net.minecraft.util.datafix.schemas.V100;

public class V703
extends Schema {
    public V703(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        Map $$1 = super.registerEntities($$0);
        $$1.remove((Object)"EntityHorse");
        $$0.register($$1, "Horse", () -> DSL.optionalFields((String)"ArmorItem", (TypeTemplate)References.ITEM_STACK.in($$0), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.register($$1, "Donkey", () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.register($$1, "Mule", () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)References.ITEM_STACK.in($$0)), (String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.register($$1, "ZombieHorse", () -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        $$0.register($$1, "SkeletonHorse", () -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)References.ITEM_STACK.in($$0), (TypeTemplate)V100.equipment($$0)));
        return $$1;
    }
}