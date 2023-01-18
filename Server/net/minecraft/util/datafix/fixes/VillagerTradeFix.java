/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.util.Pair
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.function.Function
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class VillagerTradeFix
extends NamedEntityFix {
    public VillagerTradeFix(Schema $$0, boolean $$1) {
        super($$0, $$1, "Villager trade fix", References.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> fix(Typed<?> $$0) {
        OpticFinder $$12 = $$0.getType().findField("Offers");
        OpticFinder $$2 = $$12.type().findField("Recipes");
        Type $$3 = $$2.type();
        if (!($$3 instanceof List.ListType)) {
            throw new IllegalStateException("Recipes are expected to be a list.");
        }
        List.ListType $$4 = (List.ListType)$$3;
        Type $$5 = $$4.getElement();
        OpticFinder $$62 = DSL.typeFinder((Type)$$5);
        OpticFinder $$7 = $$5.findField("buy");
        OpticFinder $$8 = $$5.findField("buyB");
        OpticFinder $$9 = $$5.findField("sell");
        OpticFinder $$10 = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        Function $$11 = $$1 -> this.updateItemStack((OpticFinder<Pair<String, String>>)$$10, (Typed<?>)$$1);
        return $$0.updateTyped($$12, $$6 -> $$6.updateTyped($$2, $$5 -> $$5.updateTyped($$62, $$4 -> $$4.updateTyped($$7, $$11).updateTyped($$8, $$11).updateTyped($$9, $$11))));
    }

    private Typed<?> updateItemStack(OpticFinder<Pair<String, String>> $$0, Typed<?> $$1) {
        return $$1.update($$0, $$02 -> $$02.mapSecond($$0 -> Objects.equals((Object)$$0, (Object)"minecraft:carved_pumpkin") ? "minecraft:pumpkin" : $$0));
    }
}