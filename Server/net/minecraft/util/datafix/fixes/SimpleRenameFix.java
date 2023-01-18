/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Objects
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SimpleRenameFix
extends DataFix {
    private final String fixerName;
    private final Map<String, String> nameMapping;
    private final DSL.TypeReference typeReference;

    public SimpleRenameFix(Schema $$0, DSL.TypeReference $$1, Map<String, String> $$2) {
        this($$0, $$1, $$1.typeName() + "-renames at version: " + $$0.getVersionKey(), $$2);
    }

    public SimpleRenameFix(Schema $$0, DSL.TypeReference $$1, String $$2, Map<String, String> $$3) {
        super($$0, false);
        this.nameMapping = $$3;
        this.fixerName = $$2;
        this.typeReference = $$1;
    }

    protected TypeRewriteRule makeRule() {
        Type $$02 = DSL.named((String)this.typeReference.typeName(), NamespacedSchema.namespacedString());
        if (!Objects.equals((Object)$$02, (Object)this.getInputSchema().getType(this.typeReference))) {
            throw new IllegalStateException("\"" + this.typeReference.typeName() + "\" type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.fixerName, $$02, $$0 -> $$02 -> $$02.mapSecond($$0 -> (String)this.nameMapping.getOrDefault($$0, $$0)));
    }
}