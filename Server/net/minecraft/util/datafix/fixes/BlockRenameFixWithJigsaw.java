/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Function
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.References;

public abstract class BlockRenameFixWithJigsaw
extends BlockRenameFix {
    private final String name;

    public BlockRenameFixWithJigsaw(Schema $$0, String $$1) {
        super($$0, $$1);
        this.name = $$1;
    }

    @Override
    public TypeRewriteRule makeRule() {
        DSL.TypeReference $$0 = References.BLOCK_ENTITY;
        String $$1 = "minecraft:jigsaw";
        OpticFinder $$22 = DSL.namedChoice((String)"minecraft:jigsaw", (Type)this.getInputSchema().getChoiceType($$0, "minecraft:jigsaw"));
        TypeRewriteRule $$3 = this.fixTypeEverywhereTyped(this.name + " for jigsaw state", this.getInputSchema().getType($$0), this.getOutputSchema().getType($$0), $$2 -> $$2.updateTyped($$22, this.getOutputSchema().getChoiceType($$0, "minecraft:jigsaw"), $$02 -> $$02.update(DSL.remainderFinder(), $$0 -> $$0.update("final_state", $$1 -> (Dynamic)DataFixUtils.orElse((Optional)$$1.asString().result().map($$0 -> {
            Object $$1 = $$0.indexOf(91);
            Object $$2 = $$0.indexOf(123);
            int $$3 = $$0.length();
            if ($$1 > 0) {
                $$3 = Math.min((int)$$3, (int)$$1);
            }
            if ($$2 > 0) {
                $$3 = Math.min((int)$$3, (int)$$2);
            }
            String $$4 = $$0.substring(0, $$3);
            String $$5 = this.fixBlock($$4);
            return $$5 + $$0.substring($$3);
        }).map(arg_0 -> ((Dynamic)$$0).createString(arg_0)), (Object)$$1)))));
        return TypeRewriteRule.seq((TypeRewriteRule)super.makeRule(), (TypeRewriteRule)$$3);
    }

    public static DataFix create(Schema $$0, String $$1, final Function<String, String> $$2) {
        return new BlockRenameFixWithJigsaw($$0, $$1){

            @Override
            protected String fixBlock(String $$0) {
                return (String)$$2.apply((Object)$$0);
            }
        };
    }
}