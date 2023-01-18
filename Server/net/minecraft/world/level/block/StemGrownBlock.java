/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.block;

import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class StemGrownBlock
extends Block {
    public StemGrownBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    public abstract StemBlock getStem();

    public abstract AttachedStemBlock getAttachedStem();
}