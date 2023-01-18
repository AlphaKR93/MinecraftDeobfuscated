/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.security.SignatureException
 */
package net.minecraft.util;

import java.security.SignatureException;

@FunctionalInterface
public interface SignatureUpdater {
    public void update(Output var1) throws SignatureException;

    @FunctionalInterface
    public static interface Output {
        public void update(byte[] var1) throws SignatureException;
    }
}