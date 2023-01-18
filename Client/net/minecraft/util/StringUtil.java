/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile((String)"(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern LINE_PATTERN = Pattern.compile((String)"\\r\\n|\\v");
    private static final Pattern LINE_END_PATTERN = Pattern.compile((String)"(?:\\r\\n|\\v)$");

    public static String formatTickDuration(int $$0) {
        int $$1 = $$0 / 20;
        int $$2 = $$1 / 60;
        $$1 %= 60;
        int $$3 = $$2 / 60;
        $$2 %= 60;
        if ($$3 > 0) {
            return String.format((Locale)Locale.ROOT, (String)"%02d:%02d:%02d", (Object[])new Object[]{$$3, $$2, $$1});
        }
        return String.format((Locale)Locale.ROOT, (String)"%02d:%02d", (Object[])new Object[]{$$2, $$1});
    }

    public static String stripColor(String $$0) {
        return STRIP_COLOR_PATTERN.matcher((CharSequence)$$0).replaceAll("");
    }

    public static boolean isNullOrEmpty(@Nullable String $$0) {
        return StringUtils.isEmpty((CharSequence)$$0);
    }

    public static String truncateStringIfNecessary(String $$0, int $$1, boolean $$2) {
        if ($$0.length() <= $$1) {
            return $$0;
        }
        if ($$2 && $$1 > 3) {
            return $$0.substring(0, $$1 - 3) + "...";
        }
        return $$0.substring(0, $$1);
    }

    public static int lineCount(String $$0) {
        if ($$0.isEmpty()) {
            return 0;
        }
        Matcher $$1 = LINE_PATTERN.matcher((CharSequence)$$0);
        int $$2 = 1;
        while ($$1.find()) {
            ++$$2;
        }
        return $$2;
    }

    public static boolean endsWithNewLine(String $$0) {
        return LINE_END_PATTERN.matcher((CharSequence)$$0).find();
    }

    public static String trimChatMessage(String $$0) {
        return StringUtil.truncateStringIfNecessary($$0, 256, false);
    }
}