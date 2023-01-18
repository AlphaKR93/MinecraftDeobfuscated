/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Streams
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Map
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestBatch;
import net.minecraft.gametest.framework.GameTestBatchRunner;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.ReportGameListener;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class GameTestRunner {
    private static final int MAX_TESTS_PER_BATCH = 100;
    public static final int PADDING_AROUND_EACH_STRUCTURE = 2;
    public static final int SPACE_BETWEEN_COLUMNS = 5;
    public static final int SPACE_BETWEEN_ROWS = 6;
    public static final int DEFAULT_TESTS_PER_ROW = 8;

    public static void runTest(GameTestInfo $$0, BlockPos $$1, GameTestTicker $$2) {
        $$0.startExecution();
        $$2.add($$0);
        $$0.addListener(new ReportGameListener($$0, $$2, $$1));
        $$0.spawnStructure($$1, 2);
    }

    public static Collection<GameTestInfo> runTestBatches(Collection<GameTestBatch> $$0, BlockPos $$1, Rotation $$2, ServerLevel $$3, GameTestTicker $$4, int $$5) {
        GameTestBatchRunner $$6 = new GameTestBatchRunner($$0, $$1, $$2, $$3, $$4, $$5);
        $$6.start();
        return $$6.getTestInfos();
    }

    public static Collection<GameTestInfo> runTests(Collection<TestFunction> $$0, BlockPos $$1, Rotation $$2, ServerLevel $$3, GameTestTicker $$4, int $$5) {
        return GameTestRunner.runTestBatches(GameTestRunner.groupTestsIntoBatches($$0), $$1, $$2, $$3, $$4, $$5);
    }

    public static Collection<GameTestBatch> groupTestsIntoBatches(Collection<TestFunction> $$02) {
        Map $$1 = (Map)$$02.stream().collect(Collectors.groupingBy(TestFunction::getBatchName));
        return (Collection)$$1.entrySet().stream().flatMap($$0 -> {
            String $$1 = (String)$$0.getKey();
            Consumer<ServerLevel> $$2 = GameTestRegistry.getBeforeBatchFunction($$1);
            Consumer<ServerLevel> $$3 = GameTestRegistry.getAfterBatchFunction($$1);
            MutableInt $$42 = new MutableInt();
            Collection $$5 = (Collection)$$0.getValue();
            return Streams.stream((Iterable)Iterables.partition((Iterable)$$5, (int)100)).map($$4 -> new GameTestBatch($$1 + ":" + $$42.incrementAndGet(), (Collection<TestFunction>)ImmutableList.copyOf((Collection)$$4), $$2, $$3));
        }).collect(ImmutableList.toImmutableList());
    }

    public static void clearAllTests(ServerLevel $$0, BlockPos $$12, GameTestTicker $$2, int $$3) {
        $$2.clear();
        BlockPos $$4 = $$12.offset(-$$3, 0, -$$3);
        BlockPos $$5 = $$12.offset($$3, 0, $$3);
        BlockPos.betweenClosedStream($$4, $$5).filter($$1 -> $$0.getBlockState((BlockPos)$$1).is(Blocks.STRUCTURE_BLOCK)).forEach($$1 -> {
            StructureBlockEntity $$2 = (StructureBlockEntity)$$0.getBlockEntity((BlockPos)$$1);
            BlockPos $$3 = $$2.getBlockPos();
            BoundingBox $$4 = StructureUtils.getStructureBoundingBox($$2);
            StructureUtils.clearSpaceForStructure($$4, $$3.getY(), $$0);
        });
    }

    public static void clearMarkers(ServerLevel $$0) {
        DebugPackets.sendGameTestClearPacket($$0);
    }
}