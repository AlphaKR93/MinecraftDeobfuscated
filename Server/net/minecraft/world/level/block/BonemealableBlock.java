/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface BonemealableBlock {
    public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3, boolean var4);

    public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4);

    public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4);
}