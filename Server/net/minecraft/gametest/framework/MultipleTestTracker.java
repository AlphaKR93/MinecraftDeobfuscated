/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuffer
 *  java.util.Collection
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;

public class MultipleTestTracker {
    private static final char NOT_STARTED_TEST_CHAR = ' ';
    private static final char ONGOING_TEST_CHAR = '_';
    private static final char SUCCESSFUL_TEST_CHAR = '+';
    private static final char FAILED_OPTIONAL_TEST_CHAR = 'x';
    private static final char FAILED_REQUIRED_TEST_CHAR = 'X';
    private final Collection<GameTestInfo> tests = Lists.newArrayList();
    @Nullable
    private final Collection<GameTestListener> listeners = Lists.newArrayList();

    public MultipleTestTracker() {
    }

    public MultipleTestTracker(Collection<GameTestInfo> $$0) {
        this.tests.addAll($$0);
    }

    public void addTestToTrack(GameTestInfo $$0) {
        this.tests.add((Object)$$0);
        this.listeners.forEach($$0::addListener);
    }

    public void addListener(GameTestListener $$0) {
        this.listeners.add((Object)$$0);
        this.tests.forEach($$1 -> $$1.addListener($$0));
    }

    public void addFailureListener(final Consumer<GameTestInfo> $$0) {
        this.addListener(new GameTestListener(){

            @Override
            public void testStructureLoaded(GameTestInfo $$02) {
            }

            @Override
            public void testPassed(GameTestInfo $$02) {
            }

            @Override
            public void testFailed(GameTestInfo $$02) {
                $$0.accept((Object)$$02);
            }
        });
    }

    public int getFailedRequiredCount() {
        return (int)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isRequired).count();
    }

    public int getFailedOptionalCount() {
        return (int)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isOptional).count();
    }

    public int getDoneCount() {
        return (int)this.tests.stream().filter(GameTestInfo::isDone).count();
    }

    public boolean hasFailedRequired() {
        return this.getFailedRequiredCount() > 0;
    }

    public boolean hasFailedOptional() {
        return this.getFailedOptionalCount() > 0;
    }

    public Collection<GameTestInfo> getFailedRequired() {
        return (Collection)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isRequired).collect(Collectors.toList());
    }

    public Collection<GameTestInfo> getFailedOptional() {
        return (Collection)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isOptional).collect(Collectors.toList());
    }

    public int getTotalCount() {
        return this.tests.size();
    }

    public boolean isDone() {
        return this.getDoneCount() == this.getTotalCount();
    }

    public String getProgressBar() {
        StringBuffer $$0 = new StringBuffer();
        $$0.append('[');
        this.tests.forEach($$1 -> {
            if (!$$1.hasStarted()) {
                $$0.append(' ');
            } else if ($$1.hasSucceeded()) {
                $$0.append('+');
            } else if ($$1.hasFailed()) {
                $$0.append($$1.isRequired() ? (char)'X' : (char)'x');
            } else {
                $$0.append('_');
            }
        });
        $$0.append(']');
        return $$0.toString();
    }

    public String toString() {
        return this.getProgressBar();
    }
}