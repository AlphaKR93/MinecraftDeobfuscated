/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class ModelResourceLocation
extends ResourceLocation {
    @VisibleForTesting
    static final char VARIANT_SEPARATOR = '#';
    private final String variant;

    private ModelResourceLocation(String $$0, String $$1, String $$2, @Nullable ResourceLocation.Dummy $$3) {
        super($$0, $$1, $$3);
        this.variant = $$2;
    }

    public ModelResourceLocation(String $$0, String $$1, String $$2) {
        super($$0, $$1);
        this.variant = ModelResourceLocation.lowercaseVariant($$2);
    }

    public ModelResourceLocation(ResourceLocation $$0, String $$1) {
        this($$0.getNamespace(), $$0.getPath(), ModelResourceLocation.lowercaseVariant($$1), null);
    }

    public static ModelResourceLocation vanilla(String $$0, String $$1) {
        return new ModelResourceLocation("minecraft", $$0, $$1);
    }

    private static String lowercaseVariant(String $$0) {
        return $$0.toLowerCase(Locale.ROOT);
    }

    public String getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof ModelResourceLocation && super.equals($$0)) {
            ModelResourceLocation $$1 = (ModelResourceLocation)$$0;
            return this.variant.equals((Object)$$1.variant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.variant.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + "#" + this.variant;
    }
}