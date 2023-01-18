/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Locale;
import net.minecraft.util.datafix.fixes.References;

public abstract class EntityRenameFix
extends DataFix {
    protected final String name;

    public EntityRenameFix(String $$0, Schema $$1, boolean $$2) {
        super($$1, $$2);
        this.name = $$0;
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType $$0 = this.getInputSchema().findChoiceType(References.ENTITY);
        TaggedChoice.TaggedChoiceType $$1 = this.getOutputSchema().findChoiceType(References.ENTITY);
        return this.fixTypeEverywhere(this.name, (Type)$$0, (Type)$$1, $$2 -> $$3 -> {
            String $$4 = (String)$$3.getFirst();
            Type $$5 = (Type)$$0.types().get((Object)$$4);
            Pair<String, Typed<?>> $$6 = this.fix($$4, this.getEntity($$3.getSecond(), (DynamicOps<?>)$$2, (Type)$$5));
            Type $$7 = (Type)$$1.types().get($$6.getFirst());
            if (!$$7.equals((Object)((Typed)$$6.getSecond()).getType(), true, true)) {
                throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"Dynamic type check failed: %s not equal to %s", (Object[])new Object[]{$$7, ((Typed)$$6.getSecond()).getType()}));
            }
            return Pair.of((Object)((String)$$6.getFirst()), (Object)((Typed)$$6.getSecond()).getValue());
        });
    }

    private <A> Typed<A> getEntity(Object $$0, DynamicOps<?> $$1, Type<A> $$2) {
        return new Typed($$2, $$1, $$0);
    }

    protected abstract Pair<String, Typed<?>> fix(String var1, Typed<?> var2);
}