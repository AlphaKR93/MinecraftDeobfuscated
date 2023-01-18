/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.Map;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityIdFix
extends DataFix {
    private static final Map<String, String> ID_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"Airportal", (Object)"minecraft:end_portal");
        $$0.put((Object)"Banner", (Object)"minecraft:banner");
        $$0.put((Object)"Beacon", (Object)"minecraft:beacon");
        $$0.put((Object)"Cauldron", (Object)"minecraft:brewing_stand");
        $$0.put((Object)"Chest", (Object)"minecraft:chest");
        $$0.put((Object)"Comparator", (Object)"minecraft:comparator");
        $$0.put((Object)"Control", (Object)"minecraft:command_block");
        $$0.put((Object)"DLDetector", (Object)"minecraft:daylight_detector");
        $$0.put((Object)"Dropper", (Object)"minecraft:dropper");
        $$0.put((Object)"EnchantTable", (Object)"minecraft:enchanting_table");
        $$0.put((Object)"EndGateway", (Object)"minecraft:end_gateway");
        $$0.put((Object)"EnderChest", (Object)"minecraft:ender_chest");
        $$0.put((Object)"FlowerPot", (Object)"minecraft:flower_pot");
        $$0.put((Object)"Furnace", (Object)"minecraft:furnace");
        $$0.put((Object)"Hopper", (Object)"minecraft:hopper");
        $$0.put((Object)"MobSpawner", (Object)"minecraft:mob_spawner");
        $$0.put((Object)"Music", (Object)"minecraft:noteblock");
        $$0.put((Object)"Piston", (Object)"minecraft:piston");
        $$0.put((Object)"RecordPlayer", (Object)"minecraft:jukebox");
        $$0.put((Object)"Sign", (Object)"minecraft:sign");
        $$0.put((Object)"Skull", (Object)"minecraft:skull");
        $$0.put((Object)"Structure", (Object)"minecraft:structure_block");
        $$0.put((Object)"Trap", (Object)"minecraft:dispenser");
    });

    public BlockEntityIdFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        Type $$02 = this.getInputSchema().getType(References.ITEM_STACK);
        Type $$1 = this.getOutputSchema().getType(References.ITEM_STACK);
        TaggedChoice.TaggedChoiceType $$2 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
        TaggedChoice.TaggedChoiceType $$3 = this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY);
        return TypeRewriteRule.seq((TypeRewriteRule)this.convertUnchecked("item stack block entity name hook converter", $$02, $$1), (TypeRewriteRule)this.fixTypeEverywhere("BlockEntityIdFix", (Type)$$2, (Type)$$3, $$0 -> $$02 -> $$02.mapFirst($$0 -> (String)ID_MAP.getOrDefault($$0, $$0))));
    }
}