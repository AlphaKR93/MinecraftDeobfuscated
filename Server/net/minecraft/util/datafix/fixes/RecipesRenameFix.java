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
 *  java.util.Objects
 *  java.util.function.Function
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RecipesRenameFix
extends DataFix {
    private final String name;
    private final Function<String, String> renamer;

    public RecipesRenameFix(Schema $$0, boolean $$1, String $$2, Function<String, String> $$3) {
        super($$0, $$1);
        this.name = $$2;
        this.renamer = $$3;
    }

    protected TypeRewriteRule makeRule() {
        Type $$0 = DSL.named((String)References.RECIPE.typeName(), NamespacedSchema.namespacedString());
        if (!Objects.equals((Object)$$0, (Object)this.getInputSchema().getType(References.RECIPE))) {
            throw new IllegalStateException("Recipe type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, $$0, $$02 -> $$0 -> $$0.mapSecond(this.renamer));
    }
}