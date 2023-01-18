/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;

public interface WorldGenLevel
extends ServerLevelAccessor {
    public long getSeed();

    default public boolean ensureCanWrite(BlockPos $$0) {
        return true;
    }

    default public void setCurrentlyGenerating(@Nullable Supplier<String> $$0) {
    }
}