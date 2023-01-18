/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.Collections
 *  java.util.List
 */
package net.minecraft.util.profiling;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ResultField;

public class EmptyProfileResults
implements ProfileResults {
    public static final EmptyProfileResults EMPTY = new EmptyProfileResults();

    private EmptyProfileResults() {
    }

    @Override
    public List<ResultField> getTimes(String $$0) {
        return Collections.emptyList();
    }

    @Override
    public boolean saveResults(Path $$0) {
        return false;
    }

    @Override
    public long getStartTimeNano() {
        return 0L;
    }

    @Override
    public int getStartTimeTicks() {
        return 0;
    }

    @Override
    public long getEndTimeNano() {
        return 0L;
    }

    @Override
    public int getEndTimeTicks() {
        return 0;
    }

    @Override
    public String getProfilerResults() {
        return "";
    }
}