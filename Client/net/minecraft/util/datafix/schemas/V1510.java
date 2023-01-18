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

public class V1510
extends NamespacedSchema {
    public V1510(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
        Map $$1 = super.registerEntities($$0);
        $$1.put((Object)"minecraft:command_block_minecart", (Object)((Supplier)$$1.remove((Object)"minecraft:commandblock_minecart")));
        $$1.put((Object)"minecraft:end_crystal", (Object)((Supplier)$$1.remove((Object)"minecraft:ender_crystal")));
        $$1.put((Object)"minecraft:snow_golem", (Object)((Supplier)$$1.remove((Object)"minecraft:snowman")));
        $$1.put((Object)"minecraft:evoker", (Object)((Supplier)$$1.remove((Object)"minecraft:evocation_illager")));
        $$1.put((Object)"minecraft:evoker_fangs", (Object)((Supplier)$$1.remove((Object)"minecraft:evocation_fangs")));
        $$1.put((Object)"minecraft:illusioner", (Object)((Supplier)$$1.remove((Object)"minecraft:illusion_illager")));
        $$1.put((Object)"minecraft:vindicator", (Object)((Supplier)$$1.remove((Object)"minecraft:vindication_illager")));
        $$1.put((Object)"minecraft:iron_golem", (Object)((Supplier)$$1.remove((Object)"minecraft:villager_golem")));
        $$1.put((Object)"minecraft:experience_orb", (Object)((Supplier)$$1.remove((Object)"minecraft:xp_orb")));
        $$1.put((Object)"minecraft:experience_bottle", (Object)((Supplier)$$1.remove((Object)"minecraft:xp_bottle")));
        $$1.put((Object)"minecraft:eye_of_ender", (Object)((Supplier)$$1.remove((Object)"minecraft:eye_of_ender_signal")));
        $$1.put((Object)"minecraft:firework_rocket", (Object)((Supplier)$$1.remove((Object)"minecraft:fireworks_rocket")));
        return $$1;
    }
}