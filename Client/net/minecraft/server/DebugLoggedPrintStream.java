/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.OutputStream
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.StackTraceElement
 *  java.lang.String
 *  java.lang.Thread
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import net.minecraft.server.LoggedPrintStream;
import org.slf4j.Logger;

public class DebugLoggedPrintStream
extends LoggedPrintStream {
    private static final Logger LOGGER = LogUtils.getLogger();

    public DebugLoggedPrintStream(String $$0, OutputStream $$1) {
        super($$0, $$1);
    }

    @Override
    protected void logLine(String $$0) {
        StackTraceElement[] $$1 = Thread.currentThread().getStackTrace();
        StackTraceElement $$2 = $$1[Math.min((int)3, (int)$$1.length)];
        LOGGER.info("[{}]@.({}:{}): {}", new Object[]{this.name, $$2.getFileName(), $$2.getLineNumber(), $$0});
    }
}