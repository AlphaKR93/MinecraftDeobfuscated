/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.material;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public record MaterialRuleList(List<NoiseChunk.BlockStateFiller> materialRuleList) implements NoiseChunk.BlockStateFiller
{
    @Override
    @Nullable
    public BlockState calculate(DensityFunction.FunctionContext $$0) {
        for (NoiseChunk.BlockStateFiller $$1 : this.materialRuleList) {
            BlockState $$2 = $$1.calculate($$0);
            if ($$2 == null) continue;
            return $$2;
        }
        return null;
    }
}