/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityPaintingFieldsRenameFix
extends NamedEntityFix {
    public EntityPaintingFieldsRenameFix(Schema $$0) {
        super($$0, false, "EntityPaintingFieldsRenameFix", References.ENTITY, "minecraft:painting");
    }

    public Dynamic<?> fixTag(Dynamic<?> $$0) {
        return this.renameField(this.renameField($$0, "Motive", "variant"), "Facing", "facing");
    }

    private Dynamic<?> renameField(Dynamic<?> $$0, String $$1, String $$2) {
        Optional $$32 = $$0.get($$1).result();
        Optional $$4 = $$32.map($$3 -> $$0.remove($$1).set($$2, $$3));
        return (Dynamic)DataFixUtils.orElse((Optional)$$4, $$0);
    }

    @Override
    protected Typed<?> fix(Typed<?> $$0) {
        return $$0.update(DSL.remainderFinder(), this::fixTag);
    }
}