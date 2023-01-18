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
import net.minecraft.util.datafix.schemas.V100;

public class V1470
extends NamespacedSchema {
    public V1470(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    protected static void registerMob(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, String $$2) {
        $$0.register($$1, $$2, () -> V100.equipment($$0));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        Map $$12 = super.registerEntities($$0);
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:turtle");
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:cod_mob");
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:tropical_fish");
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:salmon_mob");
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:puffer_fish");
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:phantom");
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:dolphin");
        V1470.registerMob($$0, (Map<String, Supplier<TypeTemplate>>)$$12, "minecraft:drowned");
        $$0.register($$12, "minecraft:trident", $$1 -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)References.BLOCK_STATE.in($$0)));
        return $$12;
    }
}