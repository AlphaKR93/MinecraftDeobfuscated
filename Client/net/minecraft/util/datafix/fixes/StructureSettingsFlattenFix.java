/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.References;

public class StructureSettingsFlattenFix
extends DataFix {
    public StructureSettingsFlattenFix(Schema $$0) {
        super($$0, false);
    }

    protected TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
        OpticFinder $$1 = $$0.findField("dimensions");
        return this.fixTypeEverywhereTyped("StructureSettingsFlatten", $$0, $$12 -> $$12.updateTyped($$1, $$1 -> {
            Dynamic $$2 = (Dynamic)$$1.write().result().orElseThrow();
            Dynamic $$3 = $$2.updateMapValues(StructureSettingsFlattenFix::fixDimension);
            return (Typed)((Pair)$$1.type().readTyped($$3).result().orElseThrow()).getFirst();
        }));
    }

    private static Pair<Dynamic<?>, Dynamic<?>> fixDimension(Pair<Dynamic<?>, Dynamic<?>> $$0) {
        Dynamic $$1 = (Dynamic)$$0.getSecond();
        return Pair.of((Object)((Dynamic)$$0.getFirst()), (Object)$$1.update("generator", $$02 -> $$02.update("settings", $$0 -> $$0.update("structures", StructureSettingsFlattenFix::fixStructures))));
    }

    private static Dynamic<?> fixStructures(Dynamic<?> $$0) {
        Dynamic $$1 = $$0.get("structures").orElseEmptyMap().updateMapValues($$12 -> $$12.mapSecond($$1 -> $$1.set("type", $$0.createString("minecraft:random_spread"))));
        return (Dynamic)DataFixUtils.orElse((Optional)$$0.get("stronghold").result().map($$2 -> $$1.set("minecraft:stronghold", $$2.set("type", $$0.createString("minecraft:concentric_rings")))), (Object)$$1);
    }
}