/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.CharSequence
 *  java.lang.Object
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;

public class OptionsProgrammerArtFix
extends DataFix {
    public OptionsProgrammerArtFix(Schema $$0) {
        super($$0, false);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsProgrammerArtFix", this.getInputSchema().getType(References.OPTIONS), $$02 -> $$02.update(DSL.remainderFinder(), $$0 -> $$0.update("resourcePacks", this::fixList).update("incompatibleResourcePacks", this::fixList)));
    }

    private <T> Dynamic<T> fixList(Dynamic<T> $$0) {
        return (Dynamic)$$0.asString().result().map($$1 -> $$0.createString($$1.replace((CharSequence)"\"programer_art\"", (CharSequence)"\"programmer_art\""))).orElse($$0);
    }
}