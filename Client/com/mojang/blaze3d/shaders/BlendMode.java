/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import javax.annotation.Nullable;

public class BlendMode {
    @Nullable
    private static BlendMode lastApplied;
    private final int srcColorFactor;
    private final int srcAlphaFactor;
    private final int dstColorFactor;
    private final int dstAlphaFactor;
    private final int blendFunc;
    private final boolean separateBlend;
    private final boolean opaque;

    private BlendMode(boolean $$0, boolean $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        this.separateBlend = $$0;
        this.srcColorFactor = $$2;
        this.dstColorFactor = $$3;
        this.srcAlphaFactor = $$4;
        this.dstAlphaFactor = $$5;
        this.opaque = $$1;
        this.blendFunc = $$6;
    }

    public BlendMode() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public BlendMode(int $$0, int $$1, int $$2) {
        this(false, false, $$0, $$1, $$0, $$1, $$2);
    }

    public BlendMode(int $$0, int $$1, int $$2, int $$3, int $$4) {
        this(true, false, $$0, $$1, $$2, $$3, $$4);
    }

    public void apply() {
        if (this.equals(lastApplied)) {
            return;
        }
        if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
            lastApplied = this;
            if (this.opaque) {
                RenderSystem.disableBlend();
                return;
            }
            RenderSystem.enableBlend();
        }
        RenderSystem.blendEquation(this.blendFunc);
        if (this.separateBlend) {
            RenderSystem.blendFuncSeparate(this.srcColorFactor, this.dstColorFactor, this.srcAlphaFactor, this.dstAlphaFactor);
        } else {
            RenderSystem.blendFunc(this.srcColorFactor, this.dstColorFactor);
        }
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof BlendMode)) {
            return false;
        }
        BlendMode $$1 = (BlendMode)$$0;
        if (this.blendFunc != $$1.blendFunc) {
            return false;
        }
        if (this.dstAlphaFactor != $$1.dstAlphaFactor) {
            return false;
        }
        if (this.dstColorFactor != $$1.dstColorFactor) {
            return false;
        }
        if (this.opaque != $$1.opaque) {
            return false;
        }
        if (this.separateBlend != $$1.separateBlend) {
            return false;
        }
        if (this.srcAlphaFactor != $$1.srcAlphaFactor) {
            return false;
        }
        return this.srcColorFactor == $$1.srcColorFactor;
    }

    public int hashCode() {
        int $$0 = this.srcColorFactor;
        $$0 = 31 * $$0 + this.srcAlphaFactor;
        $$0 = 31 * $$0 + this.dstColorFactor;
        $$0 = 31 * $$0 + this.dstAlphaFactor;
        $$0 = 31 * $$0 + this.blendFunc;
        $$0 = 31 * $$0 + (this.separateBlend ? 1 : 0);
        $$0 = 31 * $$0 + (this.opaque ? 1 : 0);
        return $$0;
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public static int stringToBlendFunc(String $$0) {
        String $$1 = $$0.trim().toLowerCase(Locale.ROOT);
        if ("add".equals((Object)$$1)) {
            return 32774;
        }
        if ("subtract".equals((Object)$$1)) {
            return 32778;
        }
        if ("reversesubtract".equals((Object)$$1)) {
            return 32779;
        }
        if ("reverse_subtract".equals((Object)$$1)) {
            return 32779;
        }
        if ("min".equals((Object)$$1)) {
            return 32775;
        }
        if ("max".equals((Object)$$1)) {
            return 32776;
        }
        return 32774;
    }

    public static int stringToBlendFactor(String $$0) {
        String $$1 = $$0.trim().toLowerCase(Locale.ROOT);
        $$1 = $$1.replaceAll("_", "");
        $$1 = $$1.replaceAll("one", "1");
        $$1 = $$1.replaceAll("zero", "0");
        if ("0".equals((Object)($$1 = $$1.replaceAll("minus", "-")))) {
            return 0;
        }
        if ("1".equals((Object)$$1)) {
            return 1;
        }
        if ("srccolor".equals((Object)$$1)) {
            return 768;
        }
        if ("1-srccolor".equals((Object)$$1)) {
            return 769;
        }
        if ("dstcolor".equals((Object)$$1)) {
            return 774;
        }
        if ("1-dstcolor".equals((Object)$$1)) {
            return 775;
        }
        if ("srcalpha".equals((Object)$$1)) {
            return 770;
        }
        if ("1-srcalpha".equals((Object)$$1)) {
            return 771;
        }
        if ("dstalpha".equals((Object)$$1)) {
            return 772;
        }
        if ("1-dstalpha".equals((Object)$$1)) {
            return 773;
        }
        return -1;
    }
}