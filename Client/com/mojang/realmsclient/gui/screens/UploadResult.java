/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.gui.screens;

import javax.annotation.Nullable;

public class UploadResult {
    public final int statusCode;
    @Nullable
    public final String errorMessage;

    UploadResult(int $$0, String $$1) {
        this.statusCode = $$0;
        this.errorMessage = $$1;
    }

    public static class Builder {
        private int statusCode = -1;
        private String errorMessage;

        public Builder withStatusCode(int $$0) {
            this.statusCode = $$0;
            return this;
        }

        public Builder withErrorMessage(@Nullable String $$0) {
            this.errorMessage = $$0;
            return this;
        }

        public UploadResult build() {
            return new UploadResult(this.statusCode, this.errorMessage);
        }
    }
}