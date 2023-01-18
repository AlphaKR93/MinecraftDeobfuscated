/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.io.Reader
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.System
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.nio.file.attribute.FileAttribute
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;

public class TestCommand {
    private static final int DEFAULT_CLEAR_RADIUS = 200;
    private static final int MAX_CLEAR_RADIUS = 1024;
    private static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
    private static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
    private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
    private static final int SHOW_POS_DURATION_MS = 10000;
    private static final int DEFAULT_X_SIZE = 5;
    private static final int DEFAULT_Y_SIZE = 5;
    private static final int DEFAULT_Z_SIZE = 5;

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("test").then(Commands.literal("runthis").executes($$0 -> TestCommand.runNearbyTest((CommandSourceStack)$$0.getSource())))).then(Commands.literal("runthese").executes($$0 -> TestCommand.runAllNearbyTests((CommandSourceStack)$$0.getSource())))).then(((LiteralArgumentBuilder)Commands.literal("runfailed").executes($$0 -> TestCommand.runLastFailedTests((CommandSourceStack)$$0.getSource(), false, 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("onlyRequiredTests", BoolArgumentType.bool()).executes($$0 -> TestCommand.runLastFailedTests((CommandSourceStack)$$0.getSource(), BoolArgumentType.getBool((CommandContext)$$0, (String)"onlyRequiredTests"), 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes($$0 -> TestCommand.runLastFailedTests((CommandSourceStack)$$0.getSource(), BoolArgumentType.getBool((CommandContext)$$0, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"rotationSteps"), 8))).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes($$0 -> TestCommand.runLastFailedTests((CommandSourceStack)$$0.getSource(), BoolArgumentType.getBool((CommandContext)$$0, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"testsPerRow")))))))).then(Commands.literal("run").then(((RequiredArgumentBuilder)Commands.argument("testName", TestFunctionArgument.testFunctionArgument()).executes($$0 -> TestCommand.runTest((CommandSourceStack)$$0.getSource(), TestFunctionArgument.getTestFunction((CommandContext<CommandSourceStack>)$$0, "testName"), 0))).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes($$0 -> TestCommand.runTest((CommandSourceStack)$$0.getSource(), TestFunctionArgument.getTestFunction((CommandContext<CommandSourceStack>)$$0, "testName"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"rotationSteps"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("runall").executes($$0 -> TestCommand.runAllTests((CommandSourceStack)$$0.getSource(), 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("testClassName", TestClassNameArgument.testClassName()).executes($$0 -> TestCommand.runAllTestsInClass((CommandSourceStack)$$0.getSource(), TestClassNameArgument.getTestClassName((CommandContext<CommandSourceStack>)$$0, "testClassName"), 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes($$0 -> TestCommand.runAllTestsInClass((CommandSourceStack)$$0.getSource(), TestClassNameArgument.getTestClassName((CommandContext<CommandSourceStack>)$$0, "testClassName"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"rotationSteps"), 8))).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes($$0 -> TestCommand.runAllTestsInClass((CommandSourceStack)$$0.getSource(), TestClassNameArgument.getTestClassName((CommandContext<CommandSourceStack>)$$0, "testClassName"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"testsPerRow"))))))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes($$0 -> TestCommand.runAllTests((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"rotationSteps"), 8))).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes($$0 -> TestCommand.runAllTests((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"testsPerRow"))))))).then(Commands.literal("export").then(Commands.argument("testName", StringArgumentType.word()).executes($$0 -> TestCommand.exportTestStructure((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"testName")))))).then(Commands.literal("exportthis").executes($$0 -> TestCommand.exportNearestTestStructure((CommandSourceStack)$$0.getSource())))).then(Commands.literal("import").then(Commands.argument("testName", StringArgumentType.word()).executes($$0 -> TestCommand.importTestStructure((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"testName")))))).then(((LiteralArgumentBuilder)Commands.literal("pos").executes($$0 -> TestCommand.showPos((CommandSourceStack)$$0.getSource(), "pos"))).then(Commands.argument("var", StringArgumentType.word()).executes($$0 -> TestCommand.showPos((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"var")))))).then(Commands.literal("create").then(((RequiredArgumentBuilder)Commands.argument("testName", StringArgumentType.word()).executes($$0 -> TestCommand.createNewStructure((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"testName"), 5, 5, 5))).then(((RequiredArgumentBuilder)Commands.argument("width", IntegerArgumentType.integer()).executes($$0 -> TestCommand.createNewStructure((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"width"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"width"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"width")))).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes($$0 -> TestCommand.createNewStructure((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"width"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"height"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"depth"))))))))).then(((LiteralArgumentBuilder)Commands.literal("clearall").executes($$0 -> TestCommand.clearAllTests((CommandSourceStack)$$0.getSource(), 200))).then(Commands.argument("radius", IntegerArgumentType.integer()).executes($$0 -> TestCommand.clearAllTests((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"radius"))))));
    }

    private static int createNewStructure(CommandSourceStack $$0, String $$1, int $$2, int $$3, int $$4) {
        if ($$2 > 48 || $$3 > 48 || $$4 > 48) {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
        ServerLevel $$5 = $$0.getLevel();
        BlockPos $$6 = new BlockPos($$0.getPosition());
        BlockPos $$7 = new BlockPos($$6.getX(), $$0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, $$6).getY(), $$6.getZ() + 3);
        StructureUtils.createNewEmptyStructureBlock($$1.toLowerCase(), $$7, new Vec3i($$2, $$3, $$4), Rotation.NONE, $$5);
        for (int $$8 = 0; $$8 < $$2; ++$$8) {
            for (int $$9 = 0; $$9 < $$4; ++$$9) {
                BlockPos $$10 = new BlockPos($$7.getX() + $$8, $$7.getY() + 1, $$7.getZ() + $$9);
                Block $$11 = Blocks.POLISHED_ANDESITE;
                BlockInput $$12 = new BlockInput($$11.defaultBlockState(), Collections.emptySet(), null);
                $$12.place($$5, $$10, 2);
            }
        }
        StructureUtils.addCommandBlockAndButtonToStartTest($$7, new BlockPos(1, 0, -1), Rotation.NONE, $$5);
        return 0;
    }

    private static int showPos(CommandSourceStack $$0, String $$1) throws CommandSyntaxException {
        ServerLevel $$4;
        BlockHitResult $$2 = (BlockHitResult)$$0.getPlayerOrException().pick(10.0, 1.0f, false);
        BlockPos $$3 = $$2.getBlockPos();
        Optional<BlockPos> $$5 = StructureUtils.findStructureBlockContainingPos($$3, 15, $$4 = $$0.getLevel());
        if (!$$5.isPresent()) {
            $$5 = StructureUtils.findStructureBlockContainingPos($$3, 200, $$4);
        }
        if (!$$5.isPresent()) {
            $$0.sendFailure(Component.literal("Can't find a structure block that contains the targeted pos " + $$3));
            return 0;
        }
        StructureBlockEntity $$6 = (StructureBlockEntity)$$4.getBlockEntity((BlockPos)$$5.get());
        Vec3i $$7 = $$3.subtract((Vec3i)$$5.get());
        String $$8 = $$7.getX() + ", " + $$7.getY() + ", " + $$7.getZ();
        String $$9 = $$6.getStructurePath();
        MutableComponent $$10 = Component.literal($$8).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard"))).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + $$1 + " = new BlockPos(" + $$8 + ");")));
        $$0.sendSuccess(Component.literal("Position relative to " + $$9 + ": ").append($$10), false);
        DebugPackets.sendGameTestAddMarker($$4, new BlockPos($$3), $$8, -2147418368, 10000);
        return 1;
    }

    private static int runNearbyTest(CommandSourceStack $$0) {
        ServerLevel $$2;
        BlockPos $$1 = new BlockPos($$0.getPosition());
        BlockPos $$3 = StructureUtils.findNearestStructureBlock($$1, 15, $$2 = $$0.getLevel());
        if ($$3 == null) {
            TestCommand.say($$2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        }
        GameTestRunner.clearMarkers($$2);
        TestCommand.runTest($$2, $$3, null);
        return 1;
    }

    private static int runAllNearbyTests(CommandSourceStack $$0) {
        ServerLevel $$22;
        BlockPos $$1 = new BlockPos($$0.getPosition());
        Collection<BlockPos> $$3 = StructureUtils.findStructureBlocks($$1, 200, $$22 = $$0.getLevel());
        if ($$3.isEmpty()) {
            TestCommand.say($$22, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
            return 1;
        }
        GameTestRunner.clearMarkers($$22);
        TestCommand.say($$0, "Running " + $$3.size() + " tests...");
        MultipleTestTracker $$4 = new MultipleTestTracker();
        $$3.forEach($$2 -> TestCommand.runTest($$22, $$2, $$4));
        return 1;
    }

    private static void runTest(ServerLevel $$0, BlockPos $$1, @Nullable MultipleTestTracker $$2) {
        StructureBlockEntity $$3 = (StructureBlockEntity)$$0.getBlockEntity($$1);
        String $$4 = $$3.getStructurePath();
        TestFunction $$5 = GameTestRegistry.getTestFunction($$4);
        GameTestInfo $$6 = new GameTestInfo($$5, $$3.getRotation(), $$0);
        if ($$2 != null) {
            $$2.addTestToTrack($$6);
            $$6.addListener(new TestSummaryDisplayer($$0, $$2));
        }
        TestCommand.runTestPreparation($$5, $$0);
        AABB $$7 = StructureUtils.getStructureBounds($$3);
        BlockPos $$8 = new BlockPos($$7.minX, $$7.minY, $$7.minZ);
        GameTestRunner.runTest($$6, $$8, GameTestTicker.SINGLETON);
    }

    static void showTestSummaryIfAllDone(ServerLevel $$0, MultipleTestTracker $$1) {
        if ($$1.isDone()) {
            TestCommand.say($$0, "GameTest done! " + $$1.getTotalCount() + " tests were run", ChatFormatting.WHITE);
            if ($$1.hasFailedRequired()) {
                TestCommand.say($$0, $$1.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
            } else {
                TestCommand.say($$0, "All required tests passed :)", ChatFormatting.GREEN);
            }
            if ($$1.hasFailedOptional()) {
                TestCommand.say($$0, $$1.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
            }
        }
    }

    private static int clearAllTests(CommandSourceStack $$0, int $$1) {
        ServerLevel $$2 = $$0.getLevel();
        GameTestRunner.clearMarkers($$2);
        BlockPos $$3 = new BlockPos($$0.getPosition().x, (double)$$0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos($$0.getPosition())).getY(), $$0.getPosition().z);
        GameTestRunner.clearAllTests($$2, $$3, GameTestTicker.SINGLETON, Mth.clamp($$1, 0, 1024));
        return 1;
    }

    private static int runTest(CommandSourceStack $$0, TestFunction $$1, int $$2) {
        ServerLevel $$3 = $$0.getLevel();
        BlockPos $$4 = new BlockPos($$0.getPosition());
        int $$5 = $$0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, $$4).getY();
        BlockPos $$6 = new BlockPos($$4.getX(), $$5, $$4.getZ() + 3);
        GameTestRunner.clearMarkers($$3);
        TestCommand.runTestPreparation($$1, $$3);
        Rotation $$7 = StructureUtils.getRotationForRotationSteps($$2);
        GameTestInfo $$8 = new GameTestInfo($$1, $$7, $$3);
        GameTestRunner.runTest($$8, $$6, GameTestTicker.SINGLETON);
        return 1;
    }

    private static void runTestPreparation(TestFunction $$0, ServerLevel $$1) {
        Consumer<ServerLevel> $$2 = GameTestRegistry.getBeforeBatchFunction($$0.getBatchName());
        if ($$2 != null) {
            $$2.accept((Object)$$1);
        }
    }

    private static int runAllTests(CommandSourceStack $$0, int $$1, int $$2) {
        GameTestRunner.clearMarkers($$0.getLevel());
        Collection<TestFunction> $$3 = GameTestRegistry.getAllTestFunctions();
        TestCommand.say($$0, "Running all " + $$3.size() + " tests...");
        GameTestRegistry.forgetFailedTests();
        TestCommand.runTests($$0, $$3, $$1, $$2);
        return 1;
    }

    private static int runAllTestsInClass(CommandSourceStack $$0, String $$1, int $$2, int $$3) {
        Collection<TestFunction> $$4 = GameTestRegistry.getTestFunctionsForClassName($$1);
        GameTestRunner.clearMarkers($$0.getLevel());
        TestCommand.say($$0, "Running " + $$4.size() + " tests from " + $$1 + "...");
        GameTestRegistry.forgetFailedTests();
        TestCommand.runTests($$0, $$4, $$2, $$3);
        return 1;
    }

    private static int runLastFailedTests(CommandSourceStack $$0, boolean $$1, int $$2, int $$3) {
        Collection<TestFunction> $$5;
        if ($$1) {
            Collection $$4 = (Collection)GameTestRegistry.getLastFailedTests().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
        } else {
            $$5 = GameTestRegistry.getLastFailedTests();
        }
        if ($$5.isEmpty()) {
            TestCommand.say($$0, "No failed tests to rerun");
            return 0;
        }
        GameTestRunner.clearMarkers($$0.getLevel());
        TestCommand.say($$0, "Rerunning " + $$5.size() + " failed tests (" + ($$1 ? "only required tests" : "including optional tests") + ")");
        TestCommand.runTests($$0, $$5, $$2, $$3);
        return 1;
    }

    private static void runTests(CommandSourceStack $$02, Collection<TestFunction> $$1, int $$2, int $$3) {
        BlockPos $$4 = new BlockPos($$02.getPosition());
        BlockPos $$5 = new BlockPos($$4.getX(), $$02.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, $$4).getY(), $$4.getZ() + 3);
        ServerLevel $$6 = $$02.getLevel();
        Rotation $$7 = StructureUtils.getRotationForRotationSteps($$2);
        Collection<GameTestInfo> $$8 = GameTestRunner.runTests($$1, $$5, $$7, $$6, GameTestTicker.SINGLETON, $$3);
        MultipleTestTracker $$9 = new MultipleTestTracker($$8);
        $$9.addListener(new TestSummaryDisplayer($$6, $$9));
        $$9.addFailureListener((Consumer<GameTestInfo>)((Consumer)$$0 -> GameTestRegistry.rememberFailedTest($$0.getTestFunction())));
    }

    private static void say(CommandSourceStack $$0, String $$1) {
        $$0.sendSuccess(Component.literal($$1), false);
    }

    private static int exportNearestTestStructure(CommandSourceStack $$0) {
        ServerLevel $$2;
        BlockPos $$1 = new BlockPos($$0.getPosition());
        BlockPos $$3 = StructureUtils.findNearestStructureBlock($$1, 15, $$2 = $$0.getLevel());
        if ($$3 == null) {
            TestCommand.say($$2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        }
        StructureBlockEntity $$4 = (StructureBlockEntity)$$2.getBlockEntity($$3);
        String $$5 = $$4.getStructurePath();
        return TestCommand.exportTestStructure($$0, $$5);
    }

    private static int exportTestStructure(CommandSourceStack $$0, String $$1) {
        Path $$2 = Paths.get((String)StructureUtils.testStructuresDir, (String[])new String[0]);
        ResourceLocation $$3 = new ResourceLocation("minecraft", $$1);
        Path $$4 = $$0.getLevel().getStructureManager().getPathToGeneratedStructure($$3, ".nbt");
        Path $$5 = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, $$4, $$1, $$2);
        if ($$5 == null) {
            TestCommand.say($$0, "Failed to export " + $$4);
            return 1;
        }
        try {
            Files.createDirectories((Path)$$5.getParent(), (FileAttribute[])new FileAttribute[0]);
        }
        catch (IOException $$6) {
            TestCommand.say($$0, "Could not create folder " + $$5.getParent());
            $$6.printStackTrace();
            return 1;
        }
        TestCommand.say($$0, "Exported " + $$1 + " to " + $$5.toAbsolutePath());
        return 0;
    }

    private static int importTestStructure(CommandSourceStack $$0, String $$1) {
        Path $$2 = Paths.get((String)StructureUtils.testStructuresDir, (String[])new String[]{$$1 + ".snbt"});
        ResourceLocation $$3 = new ResourceLocation("minecraft", $$1);
        Path $$4 = $$0.getLevel().getStructureManager().getPathToGeneratedStructure($$3, ".nbt");
        try {
            BufferedReader $$5 = Files.newBufferedReader((Path)$$2);
            String $$6 = IOUtils.toString((Reader)$$5);
            Files.createDirectories((Path)$$4.getParent(), (FileAttribute[])new FileAttribute[0]);
            try (OutputStream $$7 = Files.newOutputStream((Path)$$4, (OpenOption[])new OpenOption[0]);){
                NbtIo.writeCompressed(NbtUtils.snbtToStructure($$6), $$7);
            }
            TestCommand.say($$0, "Imported to " + $$4.toAbsolutePath());
            return 0;
        }
        catch (CommandSyntaxException | IOException $$8) {
            System.err.println("Failed to load structure " + $$1);
            $$8.printStackTrace();
            return 1;
        }
    }

    private static void say(ServerLevel $$02, String $$1, ChatFormatting $$22) {
        $$02.getPlayers((Predicate<? super ServerPlayer>)((Predicate)$$0 -> true)).forEach($$2 -> $$2.sendSystemMessage(Component.literal($$22 + $$1)));
    }

    static class TestSummaryDisplayer
    implements GameTestListener {
        private final ServerLevel level;
        private final MultipleTestTracker tracker;

        public TestSummaryDisplayer(ServerLevel $$0, MultipleTestTracker $$1) {
            this.level = $$0;
            this.tracker = $$1;
        }

        @Override
        public void testStructureLoaded(GameTestInfo $$0) {
        }

        @Override
        public void testPassed(GameTestInfo $$0) {
            TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }

        @Override
        public void testFailed(GameTestInfo $$0) {
            TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }
    }
}