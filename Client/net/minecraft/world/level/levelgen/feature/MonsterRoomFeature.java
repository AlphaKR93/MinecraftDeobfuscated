/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.slf4j.Logger;

public class MonsterRoomFeature
extends Feature<NoneFeatureConfiguration> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityType<?>[] MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    public MonsterRoomFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        Predicate<BlockState> $$1 = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
        BlockPos $$2 = $$0.origin();
        RandomSource $$3 = $$0.random();
        WorldGenLevel $$4 = $$0.level();
        int $$5 = 3;
        int $$6 = $$3.nextInt(2) + 2;
        int $$7 = -$$6 - 1;
        int $$8 = $$6 + 1;
        int $$9 = -1;
        int $$10 = 4;
        int $$11 = $$3.nextInt(2) + 2;
        int $$12 = -$$11 - 1;
        int $$13 = $$11 + 1;
        int $$14 = 0;
        for (int $$15 = $$7; $$15 <= $$8; ++$$15) {
            for (int $$16 = -1; $$16 <= 4; ++$$16) {
                for (int $$17 = $$12; $$17 <= $$13; ++$$17) {
                    BlockPos $$18 = $$2.offset($$15, $$16, $$17);
                    Material $$19 = $$4.getBlockState($$18).getMaterial();
                    boolean $$20 = $$19.isSolid();
                    if ($$16 == -1 && !$$20) {
                        return false;
                    }
                    if ($$16 == 4 && !$$20) {
                        return false;
                    }
                    if ($$15 != $$7 && $$15 != $$8 && $$17 != $$12 && $$17 != $$13 || $$16 != 0 || !$$4.isEmptyBlock($$18) || !$$4.isEmptyBlock((BlockPos)$$18.above())) continue;
                    ++$$14;
                }
            }
        }
        if ($$14 < 1 || $$14 > 5) {
            return false;
        }
        for (int $$21 = $$7; $$21 <= $$8; ++$$21) {
            for (int $$22 = 3; $$22 >= -1; --$$22) {
                for (int $$23 = $$12; $$23 <= $$13; ++$$23) {
                    BlockPos $$24 = $$2.offset($$21, $$22, $$23);
                    BlockState $$25 = $$4.getBlockState($$24);
                    if ($$21 == $$7 || $$22 == -1 || $$23 == $$12 || $$21 == $$8 || $$22 == 4 || $$23 == $$13) {
                        if ($$24.getY() >= $$4.getMinBuildHeight() && !$$4.getBlockState((BlockPos)$$24.below()).getMaterial().isSolid()) {
                            $$4.setBlock($$24, AIR, 2);
                            continue;
                        }
                        if (!$$25.getMaterial().isSolid() || $$25.is(Blocks.CHEST)) continue;
                        if ($$22 == -1 && $$3.nextInt(4) != 0) {
                            this.safeSetBlock($$4, $$24, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), $$1);
                            continue;
                        }
                        this.safeSetBlock($$4, $$24, Blocks.COBBLESTONE.defaultBlockState(), $$1);
                        continue;
                    }
                    if ($$25.is(Blocks.CHEST) || $$25.is(Blocks.SPAWNER)) continue;
                    this.safeSetBlock($$4, $$24, AIR, $$1);
                }
            }
        }
        block6: for (int $$26 = 0; $$26 < 2; ++$$26) {
            for (int $$27 = 0; $$27 < 3; ++$$27) {
                int $$30;
                int $$29;
                int $$28 = $$2.getX() + $$3.nextInt($$6 * 2 + 1) - $$6;
                BlockPos $$31 = new BlockPos($$28, $$29 = $$2.getY(), $$30 = $$2.getZ() + $$3.nextInt($$11 * 2 + 1) - $$11);
                if (!$$4.isEmptyBlock($$31)) continue;
                int $$32 = 0;
                for (Direction $$33 : Direction.Plane.HORIZONTAL) {
                    if (!$$4.getBlockState((BlockPos)$$31.relative($$33)).getMaterial().isSolid()) continue;
                    ++$$32;
                }
                if ($$32 != 1) continue;
                this.safeSetBlock($$4, $$31, StructurePiece.reorient($$4, $$31, Blocks.CHEST.defaultBlockState()), $$1);
                RandomizableContainerBlockEntity.setLootTable($$4, $$3, $$31, BuiltInLootTables.SIMPLE_DUNGEON);
                continue block6;
            }
        }
        this.safeSetBlock($$4, $$2, Blocks.SPAWNER.defaultBlockState(), $$1);
        BlockEntity $$34 = $$4.getBlockEntity($$2);
        if ($$34 instanceof SpawnerBlockEntity) {
            SpawnerBlockEntity $$35 = (SpawnerBlockEntity)$$34;
            $$35.setEntityId(this.randomEntityId($$3), $$3);
        } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", new Object[]{$$2.getX(), $$2.getY(), $$2.getZ()});
        }
        return true;
    }

    private EntityType<?> randomEntityId(RandomSource $$0) {
        return Util.getRandom(MOBS, $$0);
    }
}