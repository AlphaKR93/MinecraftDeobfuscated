/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  org.slf4j.Logger
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestBatch;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class GameTestBatchRunner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockPos firstTestNorthWestCorner;
    final ServerLevel level;
    private final GameTestTicker testTicker;
    private final int testsPerRow;
    private final List<GameTestInfo> allTestInfos;
    private final List<Pair<GameTestBatch, Collection<GameTestInfo>>> batches;
    private final BlockPos.MutableBlockPos nextTestNorthWestCorner;

    public GameTestBatchRunner(Collection<GameTestBatch> $$02, BlockPos $$1, Rotation $$2, ServerLevel $$3, GameTestTicker $$4, int $$5) {
        this.nextTestNorthWestCorner = $$1.mutable();
        this.firstTestNorthWestCorner = $$1;
        this.level = $$3;
        this.testTicker = $$4;
        this.testsPerRow = $$5;
        this.batches = (List)$$02.stream().map($$22 -> {
            Collection $$3 = (Collection)$$22.getTestFunctions().stream().map($$2 -> new GameTestInfo((TestFunction)$$2, $$2, $$3)).collect(ImmutableList.toImmutableList());
            return Pair.of((Object)$$22, (Object)$$3);
        }).collect(ImmutableList.toImmutableList());
        this.allTestInfos = (List)this.batches.stream().flatMap($$0 -> ((Collection)$$0.getSecond()).stream()).collect(ImmutableList.toImmutableList());
    }

    public List<GameTestInfo> getTestInfos() {
        return this.allTestInfos;
    }

    public void start() {
        this.runBatch(0);
    }

    void runBatch(final int $$0) {
        if ($$0 >= this.batches.size()) {
            return;
        }
        Pair $$12 = (Pair)this.batches.get($$0);
        final GameTestBatch $$2 = (GameTestBatch)$$12.getFirst();
        Collection $$3 = (Collection)$$12.getSecond();
        Map<GameTestInfo, BlockPos> $$4 = this.createStructuresForBatch((Collection<GameTestInfo>)$$3);
        String $$5 = $$2.getName();
        LOGGER.info("Running test batch '{}' ({} tests)...", (Object)$$5, (Object)$$3.size());
        $$2.runBeforeBatchFunction(this.level);
        final MultipleTestTracker $$6 = new MultipleTestTracker();
        $$3.forEach($$6::addTestToTrack);
        $$6.addListener(new GameTestListener(){

            private void testCompleted() {
                if ($$6.isDone()) {
                    $$2.runAfterBatchFunction(GameTestBatchRunner.this.level);
                    GameTestBatchRunner.this.runBatch($$0 + 1);
                }
            }

            @Override
            public void testStructureLoaded(GameTestInfo $$02) {
            }

            @Override
            public void testPassed(GameTestInfo $$02) {
                this.testCompleted();
            }

            @Override
            public void testFailed(GameTestInfo $$02) {
                this.testCompleted();
            }
        });
        $$3.forEach($$1 -> {
            BlockPos $$2 = (BlockPos)$$4.get($$1);
            GameTestRunner.runTest($$1, $$2, this.testTicker);
        });
    }

    private Map<GameTestInfo, BlockPos> createStructuresForBatch(Collection<GameTestInfo> $$0) {
        HashMap $$1 = Maps.newHashMap();
        int $$2 = 0;
        AABB $$3 = new AABB(this.nextTestNorthWestCorner);
        for (GameTestInfo $$4 : $$0) {
            BlockPos $$5 = new BlockPos(this.nextTestNorthWestCorner);
            StructureBlockEntity $$6 = StructureUtils.spawnStructure($$4.getStructureName(), $$5, $$4.getRotation(), 2, this.level, true);
            AABB $$7 = StructureUtils.getStructureBounds($$6);
            $$4.setStructureBlockPos($$6.getBlockPos());
            $$1.put((Object)$$4, (Object)new BlockPos(this.nextTestNorthWestCorner));
            $$3 = $$3.minmax($$7);
            this.nextTestNorthWestCorner.move((int)$$7.getXsize() + 5, 0, 0);
            if ($$2++ % this.testsPerRow != this.testsPerRow - 1) continue;
            this.nextTestNorthWestCorner.move(0, 0, (int)$$3.getZsize() + 6);
            this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
            $$3 = new AABB(this.nextTestNorthWestCorner);
        }
        return $$1;
    }
}