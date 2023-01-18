/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonPrimitive
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 */
package net.minecraft.data.models.blockstates;

import com.google.gson.JsonPrimitive;
import net.minecraft.data.models.blockstates.VariantProperty;
import net.minecraft.resources.ResourceLocation;

public class VariantProperties {
    public static final VariantProperty<Rotation> X_ROT = new VariantProperty("x", $$0 -> new JsonPrimitive((Number)Integer.valueOf((int)$$0.value)));
    public static final VariantProperty<Rotation> Y_ROT = new VariantProperty("y", $$0 -> new JsonPrimitive((Number)Integer.valueOf((int)$$0.value)));
    public static final VariantProperty<ResourceLocation> MODEL = new VariantProperty("model", $$0 -> new JsonPrimitive($$0.toString()));
    public static final VariantProperty<Boolean> UV_LOCK = new VariantProperty("uvlock", JsonPrimitive::new);
    public static final VariantProperty<Integer> WEIGHT = new VariantProperty("weight", JsonPrimitive::new);

    public static enum Rotation {
        R0(0),
        R90(90),
        R180(180),
        R270(270);

        final int value;

        private Rotation(int $$0) {
            this.value = $$0;
        }
    }
}