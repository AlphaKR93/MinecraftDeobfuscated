/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;

public class RealmsServiceException
extends Exception {
    public final int httpResultCode;
    public final String rawResponse;
    @Nullable
    public final RealmsError realmsError;

    public RealmsServiceException(int $$0, String $$1, RealmsError $$2) {
        super($$1);
        this.httpResultCode = $$0;
        this.rawResponse = $$1;
        this.realmsError = $$2;
    }

    public RealmsServiceException(int $$0, String $$1) {
        super($$1);
        this.httpResultCode = $$0;
        this.rawResponse = $$1;
        this.realmsError = null;
    }

    public String toString() {
        if (this.realmsError != null) {
            String $$0 = "mco.errorMessage." + this.realmsError.getErrorCode();
            String $$1 = I18n.exists($$0) ? I18n.get($$0, new Object[0]) : this.realmsError.getErrorMessage();
            return String.format((Locale)Locale.ROOT, (String)"Realms service error (%d/%d) %s", (Object[])new Object[]{this.httpResultCode, this.realmsError.getErrorCode(), $$1});
        }
        return String.format((Locale)Locale.ROOT, (String)"Realms service error (%d) %s", (Object[])new Object[]{this.httpResultCode, this.rawResponse});
    }

    public int realmsErrorCodeOrDefault(int $$0) {
        return this.realmsError != null ? this.realmsError.getErrorCode() : $$0;
    }
}