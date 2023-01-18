/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Consumer
 */
package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Rotation;

public class TestFunction {
    private final String batchName;
    private final String testName;
    private final String structureName;
    private final boolean required;
    private final int maxAttempts;
    private final int requiredSuccesses;
    private final Consumer<GameTestHelper> function;
    private final int maxTicks;
    private final long setupTicks;
    private final Rotation rotation;

    public TestFunction(String $$0, String $$1, String $$2, int $$3, long $$4, boolean $$5, Consumer<GameTestHelper> $$6) {
        this($$0, $$1, $$2, Rotation.NONE, $$3, $$4, $$5, 1, 1, $$6);
    }

    public TestFunction(String $$0, String $$1, String $$2, Rotation $$3, int $$4, long $$5, boolean $$6, Consumer<GameTestHelper> $$7) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, 1, 1, $$7);
    }

    public TestFunction(String $$0, String $$1, String $$2, Rotation $$3, int $$4, long $$5, boolean $$6, int $$7, int $$8, Consumer<GameTestHelper> $$9) {
        this.batchName = $$0;
        this.testName = $$1;
        this.structureName = $$2;
        this.rotation = $$3;
        this.maxTicks = $$4;
        this.required = $$6;
        this.requiredSuccesses = $$7;
        this.maxAttempts = $$8;
        this.function = $$9;
        this.setupTicks = $$5;
    }

    public void run(GameTestHelper $$0) {
        this.function.accept((Object)$$0);
    }

    public String getTestName() {
        return this.testName;
    }

    public String getStructureName() {
        return this.structureName;
    }

    public String toString() {
        return this.testName;
    }

    public int getMaxTicks() {
        return this.maxTicks;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public long getSetupTicks() {
        return this.setupTicks;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public boolean isFlaky() {
        return this.maxAttempts > 1;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }

    public int getRequiredSuccesses() {
        return this.requiredSuccesses;
    }
}