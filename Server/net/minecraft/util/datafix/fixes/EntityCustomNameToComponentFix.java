/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  java.util.Optional
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityCustomNameToComponentFix
extends DataFix {
    public EntityCustomNameToComponentFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        OpticFinder $$0 = DSL.fieldFinder((String)"id", NamespacedSchema.namespacedString());
        return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", this.getInputSchema().getType(References.ENTITY), $$1 -> $$1.update(DSL.remainderFinder(), $$2 -> {
            Optional $$3 = $$1.getOptional($$0);
            if ($$3.isPresent() && Objects.equals((Object)$$3.get(), (Object)"minecraft:commandblock_minecart")) {
                return $$2;
            }
            return EntityCustomNameToComponentFix.fixTagCustomName($$2);
        }));
    }

    public static Dynamic<?> fixTagCustomName(Dynamic<?> $$0) {
        String $$1 = $$0.get("CustomName").asString("");
        if ($$1.isEmpty()) {
            return $$0.remove("CustomName");
        }
        return $$0.set("CustomName", $$0.createString(Component.Serializer.toJson(Component.literal($$1))));
    }
}