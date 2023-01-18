/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityShulkerBoxColorFix
extends NamedEntityFix {
    public BlockEntityShulkerBoxColorFix(Schema $$0, boolean $$1) {
        super($$0, $$1, "BlockEntityShulkerBoxColorFix", References.BLOCK_ENTITY, "minecraft:shulker_box");
    }

    @Override
    protected Typed<?> fix(Typed<?> $$02) {
        return $$02.update(DSL.remainderFinder(), $$0 -> $$0.remove("Color"));
    }
}