/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.exceptions.MinecraftClientException
 *  com.mojang.authlib.exceptions.MinecraftClientException$ErrorType
 *  com.mojang.authlib.exceptions.MinecraftClientHttpException
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest
 *  com.mojang.datafixers.util.Unit
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.concurrent.Executor
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.datafixers.util.Unit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ThrowingComponent;

public interface AbuseReportSender {
    public static AbuseReportSender create(ReportEnvironment $$0, UserApiService $$1) {
        return new Services($$0, $$1);
    }

    public CompletableFuture<Unit> send(UUID var1, AbuseReport var2);

    public boolean isEnabled();

    default public AbuseReportLimits reportLimits() {
        return AbuseReportLimits.DEFAULTS;
    }

    public record Services(ReportEnvironment environment, UserApiService userApiService) implements AbuseReportSender
    {
        private static final Component SERVICE_UNAVAILABLE_TEXT = Component.translatable("gui.abuseReport.send.service_unavailable");
        private static final Component HTTP_ERROR_TEXT = Component.translatable("gui.abuseReport.send.http_error");
        private static final Component JSON_ERROR_TEXT = Component.translatable("gui.abuseReport.send.json_error");

        @Override
        public CompletableFuture<Unit> send(UUID $$0, AbuseReport $$1) {
            return CompletableFuture.supplyAsync(() -> {
                AbuseReportRequest $$2 = new AbuseReportRequest(1, $$0, $$1, this.environment.clientInfo(), this.environment.thirdPartyServerInfo(), this.environment.realmInfo());
                try {
                    this.userApiService.reportAbuse($$2);
                    return Unit.INSTANCE;
                }
                catch (MinecraftClientHttpException $$3) {
                    Component $$4 = this.getHttpErrorDescription($$3);
                    throw new CompletionException((Throwable)new SendException($$4, $$3));
                }
                catch (MinecraftClientException $$5) {
                    Component $$6 = this.getErrorDescription($$5);
                    throw new CompletionException((Throwable)new SendException($$6, $$5));
                }
            }, (Executor)Util.ioPool());
        }

        @Override
        public boolean isEnabled() {
            return this.userApiService.canSendReports();
        }

        private Component getHttpErrorDescription(MinecraftClientHttpException $$0) {
            return Component.translatable("gui.abuseReport.send.error_message", $$0.getMessage());
        }

        private Component getErrorDescription(MinecraftClientException $$0) {
            return switch ($$0.getType()) {
                default -> throw new IncompatibleClassChangeError();
                case MinecraftClientException.ErrorType.SERVICE_UNAVAILABLE -> SERVICE_UNAVAILABLE_TEXT;
                case MinecraftClientException.ErrorType.HTTP_ERROR -> HTTP_ERROR_TEXT;
                case MinecraftClientException.ErrorType.JSON_ERROR -> JSON_ERROR_TEXT;
            };
        }

        @Override
        public AbuseReportLimits reportLimits() {
            return this.userApiService.getAbuseReportLimits();
        }
    }

    public static class SendException
    extends ThrowingComponent {
        public SendException(Component $$0, Throwable $$1) {
            super($$0, $$1);
        }
    }
}