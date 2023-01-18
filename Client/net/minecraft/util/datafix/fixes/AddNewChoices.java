/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.Locale;

public class AddNewChoices
extends DataFix {
    private final String name;
    private final DSL.TypeReference type;

    public AddNewChoices(Schema $$0, String $$1, DSL.TypeReference $$2) {
        super($$0, true);
        this.name = $$1;
        this.type = $$2;
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType $$0 = this.getInputSchema().findChoiceType(this.type);
        TaggedChoice.TaggedChoiceType $$1 = this.getOutputSchema().findChoiceType(this.type);
        return this.cap(this.name, $$0, $$1);
    }

    protected final <K> TypeRewriteRule cap(String $$0, TaggedChoice.TaggedChoiceType<K> $$1, TaggedChoice.TaggedChoiceType<?> $$2) {
        if ($$1.getKeyType() != $$2.getKeyType()) {
            throw new IllegalStateException("Could not inject: key type is not the same");
        }
        TaggedChoice.TaggedChoiceType<?> $$3 = $$2;
        return this.fixTypeEverywhere($$0, (Type)$$1, (Type)$$3, $$12 -> $$1 -> {
            if (!$$3.hasType($$1.getFirst())) {
                throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"Unknown type %s in %s ", (Object[])new Object[]{$$1.getFirst(), this.type}));
            }
            return $$1;
        });
    }
}