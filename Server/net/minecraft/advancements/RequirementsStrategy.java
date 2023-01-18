/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 */
package net.minecraft.advancements;

import java.util.Collection;

public interface RequirementsStrategy {
    public static final RequirementsStrategy AND = $$0 -> {
        String[][] $$1 = new String[$$0.size()][];
        int $$2 = 0;
        for (String $$3 : $$0) {
            $$1[$$2++] = new String[]{$$3};
        }
        return $$1;
    };
    public static final RequirementsStrategy OR = $$0 -> new String[][]{(String[])$$0.toArray((Object[])new String[0])};

    public String[][] createRequirements(Collection<String> var1);
}