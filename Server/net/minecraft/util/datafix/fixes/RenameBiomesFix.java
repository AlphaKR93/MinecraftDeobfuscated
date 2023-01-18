/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Objects
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RenameBiomesFix
extends DataFix {
    private final String name;
    private final Map<String, String> biomes;

    public RenameBiomesFix(Schema $$0, boolean $$1, String $$2, Map<String, String> $$3) {
        super($$0, $$1);
        this.biomes = $$3;
        this.name = $$2;
    }

    protected TypeRewriteRule makeRule() {
        Type $$02 = DSL.named((String)References.BIOME.typeName(), NamespacedSchema.namespacedString());
        if (!Objects.equals((Object)$$02, (Object)this.getInputSchema().getType(References.BIOME))) {
            throw new IllegalStateException("Biome type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, $$02, $$0 -> $$02 -> $$02.mapSecond($$0 -> (String)this.biomes.getOrDefault($$0, $$0)));
    }
}