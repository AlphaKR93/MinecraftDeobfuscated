/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  java.io.PrintWriter
 *  java.io.StringWriter
 *  java.io.Writer
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.server.packs.PackResources;
import org.slf4j.Logger;

public class ResourceLoadStateTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private ReloadState reloadState;
    private int reloadCount;

    public void startReload(ReloadReason $$0, List<PackResources> $$1) {
        ++this.reloadCount;
        if (this.reloadState != null && !this.reloadState.finished) {
            LOGGER.warn("Reload already ongoing, replacing");
        }
        this.reloadState = new ReloadState($$0, (List<String>)((List)$$1.stream().map(PackResources::packId).collect(ImmutableList.toImmutableList())));
    }

    public void startRecovery(Throwable $$0) {
        if (this.reloadState == null) {
            LOGGER.warn("Trying to signal reload recovery, but nothing was started");
            this.reloadState = new ReloadState(ReloadReason.UNKNOWN, (List<String>)ImmutableList.of());
        }
        this.reloadState.recoveryReloadInfo = new RecoveryInfo($$0);
    }

    public void finishReload() {
        if (this.reloadState == null) {
            LOGGER.warn("Trying to finish reload, but nothing was started");
        } else {
            this.reloadState.finished = true;
        }
    }

    public void fillCrashReport(CrashReport $$0) {
        CrashReportCategory $$1 = $$0.addCategory("Last reload");
        $$1.setDetail("Reload number", this.reloadCount);
        if (this.reloadState != null) {
            this.reloadState.fillCrashInfo($$1);
        }
    }

    static class ReloadState {
        private final ReloadReason reloadReason;
        private final List<String> packs;
        @Nullable
        RecoveryInfo recoveryReloadInfo;
        boolean finished;

        ReloadState(ReloadReason $$0, List<String> $$1) {
            this.reloadReason = $$0;
            this.packs = $$1;
        }

        public void fillCrashInfo(CrashReportCategory $$0) {
            $$0.setDetail("Reload reason", this.reloadReason.name);
            $$0.setDetail("Finished", this.finished ? "Yes" : "No");
            $$0.setDetail("Packs", () -> String.join((CharSequence)", ", this.packs));
            if (this.recoveryReloadInfo != null) {
                this.recoveryReloadInfo.fillCrashInfo($$0);
            }
        }
    }

    public static enum ReloadReason {
        INITIAL("initial"),
        MANUAL("manual"),
        UNKNOWN("unknown");

        final String name;

        private ReloadReason(String $$0) {
            this.name = $$0;
        }
    }

    static class RecoveryInfo {
        private final Throwable error;

        RecoveryInfo(Throwable $$0) {
            this.error = $$0;
        }

        public void fillCrashInfo(CrashReportCategory $$0) {
            $$0.setDetail("Recovery", "Yes");
            $$0.setDetail("Recovery reason", () -> {
                StringWriter $$0 = new StringWriter();
                this.error.printStackTrace(new PrintWriter((Writer)$$0));
                return $$0.toString();
            });
        }
    }
}