/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityPaintingMotiveFix
extends NamedEntityFix {
    private static final Map<String, String> MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"donkeykong", (Object)"donkey_kong");
        $$0.put((Object)"burningskull", (Object)"burning_skull");
        $$0.put((Object)"skullandroses", (Object)"skull_and_roses");
    });

    public EntityPaintingMotiveFix(Schema $$0, boolean $$1) {
        super($$0, $$1, "EntityPaintingMotiveFix", References.ENTITY, "minecraft:painting");
    }

    public Dynamic<?> fixTag(Dynamic<?> $$0) {
        Optional $$1 = $$0.get("Motive").asString().result();
        if ($$1.isPresent()) {
            String $$2 = ((String)$$1.get()).toLowerCase(Locale.ROOT);
            return $$0.set("Motive", $$0.createString(new ResourceLocation((String)MAP.getOrDefault((Object)$$2, (Object)$$2)).toString()));
        }
        return $$0;
    }

    @Override
    protected Typed<?> fix(Typed<?> $$0) {
        return $$0.update(DSL.remainderFinder(), this::fixTag);
    }
}