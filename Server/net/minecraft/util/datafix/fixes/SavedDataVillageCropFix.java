/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.String
 *  java.util.stream.Stream
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;

public class SavedDataVillageCropFix
extends DataFix {
    public SavedDataVillageCropFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE), this::fixTag);
    }

    private <T> Dynamic<T> fixTag(Dynamic<T> $$0) {
        return $$0.update("Children", SavedDataVillageCropFix::updateChildren);
    }

    private static <T> Dynamic<T> updateChildren(Dynamic<T> $$0) {
        return (Dynamic)$$0.asStreamOpt().map(SavedDataVillageCropFix::updateChildren).map(arg_0 -> $$0.createList(arg_0)).result().orElse($$0);
    }

    private static Stream<? extends Dynamic<?>> updateChildren(Stream<? extends Dynamic<?>> $$02) {
        return $$02.map($$0 -> {
            String $$1 = $$0.get("id").asString("");
            if ("ViF".equals((Object)$$1)) {
                return SavedDataVillageCropFix.updateSingleField($$0);
            }
            if ("ViDF".equals((Object)$$1)) {
                return SavedDataVillageCropFix.updateDoubleField($$0);
            }
            return $$0;
        });
    }

    private static <T> Dynamic<T> updateSingleField(Dynamic<T> $$0) {
        $$0 = SavedDataVillageCropFix.updateCrop($$0, "CA");
        return SavedDataVillageCropFix.updateCrop($$0, "CB");
    }

    private static <T> Dynamic<T> updateDoubleField(Dynamic<T> $$0) {
        $$0 = SavedDataVillageCropFix.updateCrop($$0, "CA");
        $$0 = SavedDataVillageCropFix.updateCrop($$0, "CB");
        $$0 = SavedDataVillageCropFix.updateCrop($$0, "CC");
        return SavedDataVillageCropFix.updateCrop($$0, "CD");
    }

    private static <T> Dynamic<T> updateCrop(Dynamic<T> $$0, String $$1) {
        if ($$0.get($$1).asNumber().result().isPresent()) {
            return $$0.set($$1, BlockStateData.getTag($$0.get($$1).asInt(0) << 4));
        }
        return $$0;
    }
}