/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndPodiumFeature
extends Feature<NoneFeatureConfiguration> {
    public static final int PODIUM_RADIUS = 4;
    public static final int PODIUM_PILLAR_HEIGHT = 4;
    public static final int RIM_RADIUS = 1;
    public static final float CORNER_ROUNDING = 0.5f;
    public static final BlockPos END_PODIUM_LOCATION = BlockPos.ZERO;
    private final boolean active;

    public EndPodiumFeature(boolean $$0) {
        super(NoneFeatureConfiguration.CODEC);
        this.active = $$0;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        for (BlockPos $$3 : BlockPos.betweenClosed(new BlockPos($$1.getX() - 4, $$1.getY() - 1, $$1.getZ() - 4), new BlockPos($$1.getX() + 4, $$1.getY() + 32, $$1.getZ() + 4))) {
            boolean $$4 = $$3.closerThan($$1, 2.5);
            if (!$$4 && !$$3.closerThan($$1, 3.5)) continue;
            if ($$3.getY() < $$1.getY()) {
                if ($$4) {
                    this.setBlock($$2, $$3, Blocks.BEDROCK.defaultBlockState());
                    continue;
                }
                if ($$3.getY() >= $$1.getY()) continue;
                this.setBlock($$2, $$3, Blocks.END_STONE.defaultBlockState());
                continue;
            }
            if ($$3.getY() > $$1.getY()) {
                this.setBlock($$2, $$3, Blocks.AIR.defaultBlockState());
                continue;
            }
            if (!$$4) {
                this.setBlock($$2, $$3, Blocks.BEDROCK.defaultBlockState());
                continue;
            }
            if (this.active) {
                this.setBlock($$2, new BlockPos($$3), Blocks.END_PORTAL.defaultBlockState());
                continue;
            }
            this.setBlock($$2, new BlockPos($$3), Blocks.AIR.defaultBlockState());
        }
        for (int $$5 = 0; $$5 < 4; ++$$5) {
            this.setBlock($$2, (BlockPos)$$1.above($$5), Blocks.BEDROCK.defaultBlockState());
        }
        Vec3i $$6 = $$1.above(2);
        for (Direction $$7 : Direction.Plane.HORIZONTAL) {
            this.setBlock($$2, (BlockPos)((BlockPos)$$6).relative($$7), (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, $$7));
        }
        return true;
    }
}