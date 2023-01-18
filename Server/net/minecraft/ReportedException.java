/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 */
package net.minecraft;

import net.minecraft.CrashReport;

public class ReportedException
extends RuntimeException {
    private final CrashReport report;

    public ReportedException(CrashReport $$0) {
        this.report = $$0;
    }

    public CrashReport getReport() {
        return this.report;
    }

    public Throwable getCause() {
        return this.report.getException();
    }

    public String getMessage() {
        return this.report.getTitle();
    }
}