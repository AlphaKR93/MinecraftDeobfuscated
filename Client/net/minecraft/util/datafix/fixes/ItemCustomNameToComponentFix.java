/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.util.datafix.fixes.References;

public class ItemCustomNameToComponentFix
extends DataFix {
    public ItemCustomNameToComponentFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    private Dynamic<?> fixTag(Dynamic<?> $$0) {
        Optional $$1 = $$0.get("display").result();
        if ($$1.isPresent()) {
            Dynamic $$2 = (Dynamic)$$1.get();
            Optional $$3 = $$2.get("Name").asString().result();
            if ($$3.isPresent()) {
                $$2 = $$2.set("Name", $$2.createString(Component.Serializer.toJson(Component.literal((String)$$3.get()))));
            } else {
                Optional $$4 = $$2.get("LocName").asString().result();
                if ($$4.isPresent()) {
                    $$2 = $$2.set("Name", $$2.createString(Component.Serializer.toJson(Component.translatable((String)$$4.get()))));
                    $$2 = $$2.remove("LocName");
                }
            }
            return $$0.set("display", $$2);
        }
        return $$0;
    }

    public TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder $$12 = $$0.findField("tag");
        return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", $$0, $$1 -> $$1.updateTyped($$12, $$0 -> $$0.update(DSL.remainderFinder(), this::fixTag)));
    }
}