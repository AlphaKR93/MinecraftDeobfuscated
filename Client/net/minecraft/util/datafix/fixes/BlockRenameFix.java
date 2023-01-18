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
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.function.Function
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class BlockRenameFix
extends DataFix {
    private final String name;

    public BlockRenameFix(Schema $$0, String $$1) {
        super($$0, false);
        this.name = $$1;
    }

    public TypeRewriteRule makeRule() {
        Type $$1;
        Type $$0 = this.getInputSchema().getType(References.BLOCK_NAME);
        if (!Objects.equals((Object)$$0, (Object)($$1 = DSL.named((String)References.BLOCK_NAME.typeName(), NamespacedSchema.namespacedString())))) {
            throw new IllegalStateException("block type is not what was expected.");
        }
        TypeRewriteRule $$2 = this.fixTypeEverywhere(this.name + " for block", $$1, $$02 -> $$0 -> $$0.mapSecond(this::fixBlock));
        TypeRewriteRule $$3 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), $$02 -> $$02.update(DSL.remainderFinder(), $$0 -> {
            Optional $$1 = $$0.get("Name").asString().result();
            if ($$1.isPresent()) {
                return $$0.set("Name", $$0.createString(this.fixBlock((String)$$1.get())));
            }
            return $$0;
        }));
        return TypeRewriteRule.seq((TypeRewriteRule)$$2, (TypeRewriteRule)$$3);
    }

    protected abstract String fixBlock(String var1);

    public static DataFix create(Schema $$0, String $$1, final Function<String, String> $$2) {
        return new BlockRenameFix($$0, $$1){

            @Override
            protected String fixBlock(String $$0) {
                return (String)$$2.apply((Object)$$0);
            }
        };
    }
}