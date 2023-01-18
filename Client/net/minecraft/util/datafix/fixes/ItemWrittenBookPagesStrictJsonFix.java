/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.util.Optional
 *  org.apache.commons.lang3.StringUtils
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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix
extends DataFix {
    public ItemWrittenBookPagesStrictJsonFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public Dynamic<?> fixTag(Dynamic<?> $$0) {
        return $$0.update("pages", $$1 -> (Dynamic)DataFixUtils.orElse((Optional)$$1.asStreamOpt().map($$02 -> $$02.map($$0 -> {
            if (!$$0.asString().result().isPresent()) {
                return $$0;
            }
            Object $$1 = $$0.asString("");
            Component $$2 = null;
            if ("null".equals($$1) || StringUtils.isEmpty((CharSequence)$$1)) {
                $$2 = CommonComponents.EMPTY;
            } else if ($$1.charAt(0) == '\"' && $$1.charAt($$1.length() - 1) == '\"' || $$1.charAt(0) == '{' && $$1.charAt($$1.length() - 1) == '}') {
                try {
                    $$2 = GsonHelper.fromNullableJson(BlockEntitySignTextStrictJsonFix.GSON, $$1, Component.class, true);
                    if ($$2 == null) {
                        $$2 = CommonComponents.EMPTY;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if ($$2 == null) {
                    try {
                        $$2 = Component.Serializer.fromJson($$1);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if ($$2 == null) {
                    try {
                        $$2 = Component.Serializer.fromJsonLenient($$1);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if ($$2 == null) {
                    $$2 = Component.literal($$1);
                }
            } else {
                $$2 = Component.literal($$1);
            }
            return $$0.createString(Component.Serializer.toJson($$2));
        })).map(arg_0 -> ((Dynamic)$$0).createList(arg_0)).result(), (Object)$$0.emptyList()));
    }

    public TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder $$12 = $$0.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", $$0, $$1 -> $$1.updateTyped($$12, $$0 -> $$0.update(DSL.remainderFinder(), this::fixTag)));
    }
}