/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  org.slf4j.Logger
 */
package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;
import org.slf4j.Logger;

public class LogTestReporter
implements TestReporter {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onTestFailed(GameTestInfo $$0) {
        if ($$0.isRequired()) {
            LOGGER.error("{} failed! {}", (Object)$$0.getTestName(), (Object)Util.describeError($$0.getError()));
        } else {
            LOGGER.warn("(optional) {} failed. {}", (Object)$$0.getTestName(), (Object)Util.describeError($$0.getError()));
        }
    }

    @Override
    public void onTestSuccess(GameTestInfo $$0) {
    }
}