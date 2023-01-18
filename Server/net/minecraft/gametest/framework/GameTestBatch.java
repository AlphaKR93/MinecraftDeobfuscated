/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;

public class GameTestBatch {
    public static final String DEFAULT_BATCH_NAME = "defaultBatch";
    private final String name;
    private final Collection<TestFunction> testFunctions;
    @Nullable
    private final Consumer<ServerLevel> beforeBatchFunction;
    @Nullable
    private final Consumer<ServerLevel> afterBatchFunction;

    public GameTestBatch(String $$0, Collection<TestFunction> $$1, @Nullable Consumer<ServerLevel> $$2, @Nullable Consumer<ServerLevel> $$3) {
        if ($$1.isEmpty()) {
            throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
        }
        this.name = $$0;
        this.testFunctions = $$1;
        this.beforeBatchFunction = $$2;
        this.afterBatchFunction = $$3;
    }

    public String getName() {
        return this.name;
    }

    public Collection<TestFunction> getTestFunctions() {
        return this.testFunctions;
    }

    public void runBeforeBatchFunction(ServerLevel $$0) {
        if (this.beforeBatchFunction != null) {
            this.beforeBatchFunction.accept((Object)$$0);
        }
    }

    public void runAfterBatchFunction(ServerLevel $$0) {
        if (this.afterBatchFunction != null) {
            this.afterBatchFunction.accept((Object)$$0);
        }
    }
}