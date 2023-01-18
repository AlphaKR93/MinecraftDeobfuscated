/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuffer
 *  java.lang.Throwable
 *  java.util.Arrays
 *  net.minecraft.server.level.ServerLevel
 *  org.apache.commons.lang3.exception.ExceptionUtils
 */
package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.ExhaustedAttemptsException;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener
implements GameTestListener {
    private final GameTestInfo originalTestInfo;
    private final GameTestTicker testTicker;
    private final BlockPos structurePos;
    int attempts;
    int successes;

    public ReportGameListener(GameTestInfo $$0, GameTestTicker $$1, BlockPos $$2) {
        this.originalTestInfo = $$0;
        this.testTicker = $$1;
        this.structurePos = $$2;
        this.attempts = 0;
        this.successes = 0;
    }

    @Override
    public void testStructureLoaded(GameTestInfo $$0) {
        ReportGameListener.spawnBeacon(this.originalTestInfo, Blocks.LIGHT_GRAY_STAINED_GLASS);
        ++this.attempts;
    }

    @Override
    public void testPassed(GameTestInfo $$0) {
        ++this.successes;
        if (!$$0.isFlaky()) {
            ReportGameListener.reportPassed($$0, $$0.getTestName() + " passed! (" + $$0.getRunTime() + "ms)");
            return;
        }
        if (this.successes >= $$0.requiredSuccesses()) {
            ReportGameListener.reportPassed($$0, $$0 + " passed " + this.successes + " times of " + this.attempts + " attempts.");
        } else {
            ReportGameListener.say(this.originalTestInfo.getLevel(), ChatFormatting.GREEN, "Flaky test " + this.originalTestInfo + " succeeded, attempt: " + this.attempts + " successes: " + this.successes);
            this.rerunTest();
        }
    }

    @Override
    public void testFailed(GameTestInfo $$0) {
        if (!$$0.isFlaky()) {
            ReportGameListener.reportFailure($$0, $$0.getError());
            return;
        }
        TestFunction $$1 = this.originalTestInfo.getTestFunction();
        String $$2 = "Flaky test " + this.originalTestInfo + " failed, attempt: " + this.attempts + "/" + $$1.getMaxAttempts();
        if ($$1.getRequiredSuccesses() > 1) {
            $$2 = $$2 + ", successes: " + this.successes + " (" + $$1.getRequiredSuccesses() + " required)";
        }
        ReportGameListener.say(this.originalTestInfo.getLevel(), ChatFormatting.YELLOW, $$2);
        if ($$0.maxAttempts() - this.attempts + this.successes >= $$0.requiredSuccesses()) {
            this.rerunTest();
        } else {
            ReportGameListener.reportFailure($$0, new ExhaustedAttemptsException(this.attempts, this.successes, $$0));
        }
    }

    public static void reportPassed(GameTestInfo $$0, String $$1) {
        ReportGameListener.spawnBeacon($$0, Blocks.LIME_STAINED_GLASS);
        ReportGameListener.visualizePassedTest($$0, $$1);
    }

    private static void visualizePassedTest(GameTestInfo $$0, String $$1) {
        ReportGameListener.say($$0.getLevel(), ChatFormatting.GREEN, $$1);
        GlobalTestReporter.onTestSuccess($$0);
    }

    protected static void reportFailure(GameTestInfo $$0, Throwable $$1) {
        ReportGameListener.spawnBeacon($$0, $$0.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
        ReportGameListener.spawnLectern($$0, Util.describeError($$1));
        ReportGameListener.visualizeFailedTest($$0, $$1);
    }

    protected static void visualizeFailedTest(GameTestInfo $$0, Throwable $$1) {
        String $$2 = $$1.getMessage() + ($$1.getCause() == null ? "" : " cause: " + Util.describeError($$1.getCause()));
        String $$3 = ($$0.isRequired() ? "" : "(optional) ") + $$0.getTestName() + " failed! " + $$2;
        ReportGameListener.say($$0.getLevel(), $$0.isRequired() ? ChatFormatting.RED : ChatFormatting.YELLOW, $$3);
        Throwable $$4 = (Throwable)MoreObjects.firstNonNull((Object)ExceptionUtils.getRootCause((Throwable)$$1), (Object)$$1);
        if ($$4 instanceof GameTestAssertPosException) {
            GameTestAssertPosException $$5 = (GameTestAssertPosException)$$4;
            ReportGameListener.showRedBox($$0.getLevel(), $$5.getAbsolutePos(), $$5.getMessageToShowAtBlock());
        }
        GlobalTestReporter.onTestFailed($$0);
    }

    private void rerunTest() {
        this.originalTestInfo.clearStructure();
        GameTestInfo $$0 = new GameTestInfo(this.originalTestInfo.getTestFunction(), this.originalTestInfo.getRotation(), this.originalTestInfo.getLevel());
        $$0.startExecution();
        this.testTicker.add($$0);
        $$0.addListener(this);
        $$0.spawnStructure(this.structurePos, 2);
    }

    protected static void spawnBeacon(GameTestInfo $$0, Block $$1) {
        ServerLevel $$2 = $$0.getLevel();
        BlockPos $$3 = $$0.getStructureBlockPos();
        BlockPos $$4 = new BlockPos(-1, -1, -1);
        BlockPos $$5 = StructureTemplate.transform((BlockPos)$$3.offset($$4), Mirror.NONE, $$0.getRotation(), $$3);
        $$2.setBlockAndUpdate($$5, Blocks.BEACON.defaultBlockState().rotate($$0.getRotation()));
        BlockPos $$6 = $$5.offset(0, 1, 0);
        $$2.setBlockAndUpdate($$6, $$1.defaultBlockState());
        for (int $$7 = -1; $$7 <= 1; ++$$7) {
            for (int $$8 = -1; $$8 <= 1; ++$$8) {
                BlockPos $$9 = $$5.offset($$7, -1, $$8);
                $$2.setBlockAndUpdate($$9, Blocks.IRON_BLOCK.defaultBlockState());
            }
        }
    }

    private static void spawnLectern(GameTestInfo $$0, String $$1) {
        ServerLevel $$2 = $$0.getLevel();
        BlockPos $$3 = $$0.getStructureBlockPos();
        BlockPos $$4 = new BlockPos(-1, 1, -1);
        BlockPos $$5 = StructureTemplate.transform((BlockPos)$$3.offset($$4), Mirror.NONE, $$0.getRotation(), $$3);
        $$2.setBlockAndUpdate($$5, Blocks.LECTERN.defaultBlockState().rotate($$0.getRotation()));
        BlockState $$6 = $$2.getBlockState($$5);
        ItemStack $$7 = ReportGameListener.createBook($$0.getTestName(), $$0.isRequired(), $$1);
        LecternBlock.tryPlaceBook(null, (Level)$$2, $$5, $$6, $$7);
    }

    private static ItemStack createBook(String $$0, boolean $$12, String $$2) {
        ItemStack $$3 = new ItemStack(Items.WRITABLE_BOOK);
        ListTag $$4 = new ListTag();
        StringBuffer $$5 = new StringBuffer();
        Arrays.stream((Object[])$$0.split("\\.")).forEach($$1 -> $$5.append($$1).append('\n'));
        if (!$$12) {
            $$5.append("(optional)\n");
        }
        $$5.append("-------------------\n");
        $$4.add(StringTag.valueOf($$5 + $$2));
        $$3.addTagElement("pages", $$4);
        return $$3;
    }

    protected static void say(ServerLevel $$02, ChatFormatting $$1, String $$22) {
        $$02.getPlayers($$0 -> true).forEach($$2 -> $$2.sendSystemMessage(Component.literal($$22).withStyle($$1)));
    }

    private static void showRedBox(ServerLevel $$0, BlockPos $$1, String $$2) {
        DebugPackets.sendGameTestAddMarker($$0, $$1, $$2, -2130771968, Integer.MAX_VALUE);
    }
}