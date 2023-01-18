/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Supplier
 *  org.apache.commons.lang3.ObjectUtils
 */
package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModCheck(Confidence confidence, String description) {
    public static ModCheck identify(String $$0, Supplier<String> $$1, String $$2, Class<?> $$3) {
        String $$4 = (String)$$1.get();
        if (!$$0.equals((Object)$$4)) {
            return new ModCheck(Confidence.DEFINITELY, $$2 + " brand changed to '" + $$4 + "'");
        }
        if ($$3.getSigners() == null) {
            return new ModCheck(Confidence.VERY_LIKELY, $$2 + " jar signature invalidated");
        }
        return new ModCheck(Confidence.PROBABLY_NOT, $$2 + " jar signature and brand is untouched");
    }

    public boolean shouldReportAsModified() {
        return this.confidence.shouldReportAsModified;
    }

    public ModCheck merge(ModCheck $$0) {
        return new ModCheck((Confidence)ObjectUtils.max((Comparable[])new Confidence[]{this.confidence, $$0.confidence}), this.description + "; " + $$0.description);
    }

    public String fullDescription() {
        return this.confidence.description + " " + this.description;
    }

    public static enum Confidence {
        PROBABLY_NOT("Probably not.", false),
        VERY_LIKELY("Very likely;", true),
        DEFINITELY("Definitely;", true);

        final String description;
        final boolean shouldReportAsModified;

        private Confidence(String $$0, boolean $$1) {
            this.description = $$0;
            this.shouldReportAsModified = $$1;
        }
    }
}