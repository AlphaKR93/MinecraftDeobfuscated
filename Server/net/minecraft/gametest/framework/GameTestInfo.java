/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.concurrent.TimeUnit
 *  javax.annotation.Nullable
 */
package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.gametest.framework.GameTestTimeoutException;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class GameTestInfo {
    private final TestFunction testFunction;
    @Nullable
    private BlockPos structureBlockPos;
    private final ServerLevel level;
    private final Collection<GameTestListener> listeners = Lists.newArrayList();
    private final int timeoutTicks;
    private final Collection<GameTestSequence> sequences = Lists.newCopyOnWriteArrayList();
    private final Object2LongMap<Runnable> runAtTickTimeMap = new Object2LongOpenHashMap();
    private long startTick;
    private long tickCount;
    private boolean started;
    private final Stopwatch timer = Stopwatch.createUnstarted();
    private boolean done;
    private final Rotation rotation;
    @Nullable
    private Throwable error;
    @Nullable
    private StructureBlockEntity structureBlockEntity;

    public GameTestInfo(TestFunction $$0, Rotation $$1, ServerLevel $$2) {
        this.testFunction = $$0;
        this.level = $$2;
        this.timeoutTicks = $$0.getMaxTicks();
        this.rotation = $$0.getRotation().getRotated($$1);
    }

    void setStructureBlockPos(BlockPos $$0) {
        this.structureBlockPos = $$0;
    }

    void startExecution() {
        this.startTick = this.level.getGameTime() + 1L + this.testFunction.getSetupTicks();
        this.timer.start();
    }

    public void tick() {
        if (this.isDone()) {
            return;
        }
        this.tickInternal();
        if (this.isDone()) {
            if (this.error != null) {
                this.listeners.forEach($$0 -> $$0.testFailed(this));
            } else {
                this.listeners.forEach($$0 -> $$0.testPassed(this));
            }
        }
    }

    private void tickInternal() {
        this.tickCount = this.level.getGameTime() - this.startTick;
        if (this.tickCount < 0L) {
            return;
        }
        if (this.tickCount == 0L) {
            this.startTest();
        }
        ObjectIterator $$02 = this.runAtTickTimeMap.object2LongEntrySet().iterator();
        while ($$02.hasNext()) {
            Object2LongMap.Entry $$1 = (Object2LongMap.Entry)$$02.next();
            if ($$1.getLongValue() > this.tickCount) continue;
            try {
                ((Runnable)$$1.getKey()).run();
            }
            catch (Exception $$2) {
                this.fail($$2);
            }
            $$02.remove();
        }
        if (this.tickCount > (long)this.timeoutTicks) {
            if (this.sequences.isEmpty()) {
                this.fail((Throwable)new GameTestTimeoutException("Didn't succeed or fail within " + this.testFunction.getMaxTicks() + " ticks"));
            } else {
                this.sequences.forEach($$0 -> $$0.tickAndFailIfNotComplete(this.tickCount));
                if (this.error == null) {
                    this.fail((Throwable)new GameTestTimeoutException("No sequences finished"));
                }
            }
        } else {
            this.sequences.forEach($$0 -> $$0.tickAndContinue(this.tickCount));
        }
    }

    private void startTest() {
        if (this.started) {
            throw new IllegalStateException("Test already started");
        }
        this.started = true;
        try {
            this.testFunction.run(new GameTestHelper(this));
        }
        catch (Exception $$0) {
            this.fail($$0);
        }
    }

    public void setRunAtTickTime(long $$0, Runnable $$1) {
        this.runAtTickTimeMap.put((Object)$$1, $$0);
    }

    public String getTestName() {
        return this.testFunction.getTestName();
    }

    public BlockPos getStructureBlockPos() {
        return this.structureBlockPos;
    }

    @Nullable
    public Vec3i getStructureSize() {
        StructureBlockEntity $$0 = this.getStructureBlockEntity();
        if ($$0 == null) {
            return null;
        }
        return $$0.getStructureSize();
    }

    @Nullable
    public AABB getStructureBounds() {
        StructureBlockEntity $$0 = this.getStructureBlockEntity();
        if ($$0 == null) {
            return null;
        }
        return StructureUtils.getStructureBounds($$0);
    }

    @Nullable
    private StructureBlockEntity getStructureBlockEntity() {
        return (StructureBlockEntity)this.level.getBlockEntity(this.structureBlockPos);
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public boolean hasSucceeded() {
        return this.done && this.error == null;
    }

    public boolean hasFailed() {
        return this.error != null;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public boolean isDone() {
        return this.done;
    }

    public long getRunTime() {
        return this.timer.elapsed(TimeUnit.MILLISECONDS);
    }

    private void finish() {
        if (!this.done) {
            this.done = true;
            this.timer.stop();
        }
    }

    public void succeed() {
        if (this.error == null) {
            this.finish();
        }
    }

    public void fail(Throwable $$0) {
        this.error = $$0;
        this.finish();
    }

    @Nullable
    public Throwable getError() {
        return this.error;
    }

    public String toString() {
        return this.getTestName();
    }

    public void addListener(GameTestListener $$0) {
        this.listeners.add((Object)$$0);
    }

    public void spawnStructure(BlockPos $$02, int $$1) {
        this.structureBlockEntity = StructureUtils.spawnStructure(this.getStructureName(), $$02, this.getRotation(), $$1, this.level, false);
        this.structureBlockPos = this.structureBlockEntity.getBlockPos();
        this.structureBlockEntity.setStructureName(this.getTestName());
        StructureUtils.addCommandBlockAndButtonToStartTest(this.structureBlockPos, new BlockPos(1, 0, -1), this.getRotation(), this.level);
        this.listeners.forEach($$0 -> $$0.testStructureLoaded(this));
    }

    public void clearStructure() {
        if (this.structureBlockEntity == null) {
            throw new IllegalStateException("Expected structure to be initialized, but it was null");
        }
        BoundingBox $$0 = StructureUtils.getStructureBoundingBox(this.structureBlockEntity);
        StructureUtils.clearSpaceForStructure($$0, this.structureBlockPos.getY(), this.level);
    }

    long getTick() {
        return this.tickCount;
    }

    GameTestSequence createSequence() {
        GameTestSequence $$0 = new GameTestSequence(this);
        this.sequences.add((Object)$$0);
        return $$0;
    }

    public boolean isRequired() {
        return this.testFunction.isRequired();
    }

    public boolean isOptional() {
        return !this.testFunction.isRequired();
    }

    public String getStructureName() {
        return this.testFunction.getStructureName();
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public TestFunction getTestFunction() {
        return this.testFunction;
    }

    public int getTimeoutTicks() {
        return this.timeoutTicks;
    }

    public boolean isFlaky() {
        return this.testFunction.isFlaky();
    }

    public int maxAttempts() {
        return this.testFunction.getMaxAttempts();
    }

    public int requiredSuccesses() {
        return this.testFunction.getRequiredSuccesses();
    }
}