/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.exception;

import org.slf4j.Logger;

public class RealmsDefaultUncaughtExceptionHandler
implements Thread.UncaughtExceptionHandler {
    private final Logger logger;

    public RealmsDefaultUncaughtExceptionHandler(Logger $$0) {
        this.logger = $$0;
    }

    public void uncaughtException(Thread $$0, Throwable $$1) {
        this.logger.error("Caught previously unhandled exception", $$1);
    }
}