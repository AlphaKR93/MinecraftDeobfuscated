/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.entity.ai.attributes;

public class Attribute {
    public static final int MAX_NAME_LENGTH = 64;
    private final double defaultValue;
    private boolean syncable;
    private final String descriptionId;

    protected Attribute(String $$0, double $$1) {
        this.defaultValue = $$1;
        this.descriptionId = $$0;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isClientSyncable() {
        return this.syncable;
    }

    public Attribute setSyncable(boolean $$0) {
        this.syncable = $$0;
        return this;
    }

    public double sanitizeValue(double $$0) {
        return $$0;
    }

    public String getDescriptionId() {
        return this.descriptionId;
    }
}