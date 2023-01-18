/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;

public class EndGatewayFeature
extends Feature<EndGatewayConfiguration> {
    public EndGatewayFeature(Codec<EndGatewayConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<EndGatewayConfiguration> $$0) {
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        EndGatewayConfiguration $$32 = $$0.config();
        for (BlockPos $$4 : BlockPos.betweenClosed($$1.offset(-1, -2, -1), $$1.offset(1, 2, 1))) {
            boolean $$8;
            boolean $$5 = $$4.getX() == $$1.getX();
            boolean $$6 = $$4.getY() == $$1.getY();
            boolean $$7 = $$4.getZ() == $$1.getZ();
            boolean bl = $$8 = Math.abs((int)($$4.getY() - $$1.getY())) == 2;
            if ($$5 && $$6 && $$7) {
                BlockPos $$9 = $$4.immutable();
                this.setBlock($$2, $$9, Blocks.END_GATEWAY.defaultBlockState());
                $$32.getExit().ifPresent($$3 -> {
                    BlockEntity $$4 = $$2.getBlockEntity($$9);
                    if ($$4 instanceof TheEndGatewayBlockEntity) {
                        TheEndGatewayBlockEntity $$5 = (TheEndGatewayBlockEntity)$$4;
                        $$5.setExitPosition((BlockPos)$$3, $$32.isExitExact());
                        $$4.setChanged();
                    }
                });
                continue;
            }
            if ($$6) {
                this.setBlock($$2, $$4, Blocks.AIR.defaultBlockState());
                continue;
            }
            if ($$8 && $$5 && $$7) {
                this.setBlock($$2, $$4, Blocks.BEDROCK.defaultBlockState());
                continue;
            }
            if (!$$5 && !$$7 || $$8) {
                this.setBlock($$2, $$4, Blocks.AIR.defaultBlockState());
                continue;
            }
            this.setBlock($$2, $$4, Blocks.BEDROCK.defaultBlockState());
        }
        return true;
    }
}