/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Supplier
 */
package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1451_5
extends NamespacedSchema {
    public V1451_5(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
        Map $$1 = super.registerBlockEntities($$0);
        $$1.remove((Object)"minecraft:flower_pot");
        $$1.remove((Object)"minecraft:noteblock");
        return $$1;
    }
}