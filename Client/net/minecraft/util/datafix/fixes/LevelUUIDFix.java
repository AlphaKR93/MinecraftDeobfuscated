/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  org.slf4j.Logger
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.References;
import org.slf4j.Logger;

public class LevelUUIDFix
extends AbstractUUIDFix {
    private static final Logger LOGGER = LogUtils.getLogger();

    public LevelUUIDFix(Schema $$0) {
        super($$0, References.LEVEL);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelUUIDFix", this.getInputSchema().getType(this.typeReference), $$0 -> $$0.updateTyped(DSL.remainderFinder(), $$02 -> $$02.update(DSL.remainderFinder(), $$0 -> {
            $$0 = this.updateCustomBossEvents((Dynamic<?>)$$0);
            $$0 = this.updateDragonFight((Dynamic<?>)$$0);
            $$0 = this.updateWanderingTrader((Dynamic<?>)$$0);
            return $$0;
        })));
    }

    private Dynamic<?> updateWanderingTrader(Dynamic<?> $$0) {
        return (Dynamic)LevelUUIDFix.replaceUUIDString($$0, "WanderingTraderId", "WanderingTraderId").orElse($$0);
    }

    private Dynamic<?> updateDragonFight(Dynamic<?> $$0) {
        return $$0.update("DimensionData", $$02 -> $$02.updateMapValues($$0 -> $$0.mapSecond($$02 -> $$02.update("DragonFight", $$0 -> (Dynamic)LevelUUIDFix.replaceUUIDLeastMost($$0, "DragonUUID", "Dragon").orElse($$0)))));
    }

    private Dynamic<?> updateCustomBossEvents(Dynamic<?> $$02) {
        return $$02.update("CustomBossEvents", $$0 -> $$0.updateMapValues($$02 -> $$02.mapSecond($$0 -> $$0.update("Players", $$1 -> $$0.createList($$1.asStream().map($$0 -> (Dynamic)LevelUUIDFix.createUUIDFromML($$0).orElseGet(() -> {
            LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
            return $$0;
        })))))));
    }
}