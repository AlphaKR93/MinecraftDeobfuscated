/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Predicate
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.datafix.fixes.ItemStackTagFix;

public class OminousBannerRenameFix
extends ItemStackTagFix {
    public OminousBannerRenameFix(Schema $$02) {
        super($$02, "OminousBannerRenameFix", (Predicate<String>)((Predicate)$$0 -> $$0.equals((Object)"minecraft:white_banner")));
    }

    @Override
    protected <T> Dynamic<T> fixItemStackTag(Dynamic<T> $$0) {
        Optional $$1 = $$0.get("display").result();
        if ($$1.isPresent()) {
            Dynamic $$2 = (Dynamic)$$1.get();
            Optional $$3 = $$2.get("Name").asString().result();
            if ($$3.isPresent()) {
                String $$4 = (String)$$3.get();
                $$4 = $$4.replace((CharSequence)"\"translate\":\"block.minecraft.illager_banner\"", (CharSequence)"\"translate\":\"block.minecraft.ominous_banner\"");
                $$2 = $$2.set("Name", $$2.createString($$4));
            }
            return $$0.set("display", $$2);
        }
        return $$0;
    }
}