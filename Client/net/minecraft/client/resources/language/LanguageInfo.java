/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.Language
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 */
package net.minecraft.client.resources.language;

import com.mojang.bridge.game.Language;
import java.util.Locale;

public class LanguageInfo
implements Language,
Comparable<LanguageInfo> {
    private final String code;
    private final String region;
    private final String name;
    private final boolean bidirectional;

    public LanguageInfo(String $$0, String $$1, String $$2, boolean $$3) {
        this.code = $$0;
        this.region = $$1;
        this.name = $$2;
        this.bidirectional = $$3;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getRegion() {
        return this.region;
    }

    public boolean isBidirectional() {
        return this.bidirectional;
    }

    public String toString() {
        return String.format((Locale)Locale.ROOT, (String)"%s (%s)", (Object[])new Object[]{this.name, this.region});
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof LanguageInfo)) {
            return false;
        }
        return this.code.equals((Object)((LanguageInfo)$$0).code);
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    public int compareTo(LanguageInfo $$0) {
        return this.code.compareTo($$0.code);
    }
}