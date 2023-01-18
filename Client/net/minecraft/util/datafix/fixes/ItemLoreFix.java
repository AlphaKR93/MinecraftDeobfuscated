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
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  java.util.stream.Stream
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
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.util.datafix.fixes.References;

public class ItemLoreFix
extends DataFix {
    public ItemLoreFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    protected TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder $$12 = $$0.findField("tag");
        return this.fixTypeEverywhereTyped("Item Lore componentize", $$0, $$1 -> $$1.updateTyped($$12, $$02 -> $$02.update(DSL.remainderFinder(), $$0 -> $$0.update("display", $$02 -> $$02.update("Lore", $$0 -> (Dynamic)DataFixUtils.orElse((Optional)$$0.asStreamOpt().map(ItemLoreFix::fixLoreList).map(arg_0 -> ((Dynamic)$$0).createList(arg_0)).result(), (Object)$$0))))));
    }

    private static <T> Stream<Dynamic<T>> fixLoreList(Stream<Dynamic<T>> $$02) {
        return $$02.map($$0 -> (Dynamic)DataFixUtils.orElse((Optional)$$0.asString().map(ItemLoreFix::fixLoreEntry).map(arg_0 -> ((Dynamic)$$0).createString(arg_0)).result(), (Object)$$0));
    }

    private static String fixLoreEntry(String $$0) {
        return Component.Serializer.toJson(Component.literal($$0));
    }
}