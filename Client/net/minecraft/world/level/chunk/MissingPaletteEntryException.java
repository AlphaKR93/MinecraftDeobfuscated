/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.RuntimeException
 */
package net.minecraft.world.level.chunk;

public class MissingPaletteEntryException
extends RuntimeException {
    public MissingPaletteEntryException(int $$0) {
        super("Missing Palette entry for index " + $$0 + ".");
    }
}