/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.blockentity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BrightnessCombiner<S extends BlockEntity>
implements DoubleBlockCombiner.Combiner<S, Int2IntFunction> {
    @Override
    public Int2IntFunction acceptDouble(S $$0, S $$1) {
        return $$2 -> {
            int $$3 = LevelRenderer.getLightColor($$0.getLevel(), $$0.getBlockPos());
            int $$4 = LevelRenderer.getLightColor($$1.getLevel(), $$1.getBlockPos());
            int $$5 = LightTexture.block($$3);
            int $$6 = LightTexture.block($$4);
            int $$7 = LightTexture.sky($$3);
            int $$8 = LightTexture.sky($$4);
            return LightTexture.pack(Math.max((int)$$5, (int)$$6), Math.max((int)$$7, (int)$$8));
        };
    }

    @Override
    public Int2IntFunction acceptSingle(S $$02) {
        return $$0 -> $$0;
    }

    @Override
    public Int2IntFunction acceptNone() {
        return $$0 -> $$0;
    }
}