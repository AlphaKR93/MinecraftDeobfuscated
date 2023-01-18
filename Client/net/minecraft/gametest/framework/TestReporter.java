/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.gametest.framework;

import net.minecraft.gametest.framework.GameTestInfo;

public interface TestReporter {
    public void onTestFailed(GameTestInfo var1);

    public void onTestSuccess(GameTestInfo var1);

    default public void finish() {
    }
}