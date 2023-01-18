/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.escape.Escaper
 *  com.google.common.escape.Escapers
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  org.slf4j.Logger
 */
package net.minecraft.gametest.framework;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;
import org.slf4j.Logger;

public class TeamcityTestReporter
implements TestReporter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Escaper ESCAPER = Escapers.builder().addEscape('\'', "|'").addEscape('\n', "|n").addEscape('\r', "|r").addEscape('|', "||").addEscape('[', "|[").addEscape(']', "|]").build();

    @Override
    public void onTestFailed(GameTestInfo $$0) {
        String $$1 = ESCAPER.escape($$0.getTestName());
        String $$2 = ESCAPER.escape($$0.getError().getMessage());
        String $$3 = ESCAPER.escape(Util.describeError($$0.getError()));
        LOGGER.info("##teamcity[testStarted name='{}']", (Object)$$1);
        if ($$0.isRequired()) {
            LOGGER.info("##teamcity[testFailed name='{}' message='{}' details='{}']", new Object[]{$$1, $$2, $$3});
        } else {
            LOGGER.info("##teamcity[testIgnored name='{}' message='{}' details='{}']", new Object[]{$$1, $$2, $$3});
        }
        LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", (Object)$$1, (Object)$$0.getRunTime());
    }

    @Override
    public void onTestSuccess(GameTestInfo $$0) {
        String $$1 = ESCAPER.escape($$0.getTestName());
        LOGGER.info("##teamcity[testStarted name='{}']", (Object)$$1);
        LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", (Object)$$1, (Object)$$0.getRunTime());
    }
}