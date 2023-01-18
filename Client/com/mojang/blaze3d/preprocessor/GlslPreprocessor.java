/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  java.lang.CharSequence
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Locale
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.mojang.blaze3d.preprocessor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public abstract class GlslPreprocessor {
    private static final String C_COMMENT = "/\\*(?:[^*]|\\*+[^*/])*\\*+/";
    private static final String LINE_COMMENT = "//[^\\v]*";
    private static final Pattern REGEX_MOJ_IMPORT = Pattern.compile((String)"(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))");
    private static final Pattern REGEX_VERSION = Pattern.compile((String)"(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(\\d+))\\b");
    private static final Pattern REGEX_ENDS_WITH_WHITESPACE = Pattern.compile((String)"(?:^|\\v)(?:\\s|/\\*(?:[^*]|\\*+[^*/])*\\*+/|(//[^\\v]*))*\\z");

    public List<String> process(String $$0) {
        Context $$1 = new Context();
        List<String> $$2 = this.processImports($$0, $$1, "");
        $$2.set(0, (Object)this.setVersion((String)$$2.get(0), $$1.glslVersion));
        return $$2;
    }

    private List<String> processImports(String $$0, Context $$1, String $$2) {
        int $$3 = $$1.sourceId;
        int $$4 = 0;
        String $$5 = "";
        ArrayList $$6 = Lists.newArrayList();
        Matcher $$7 = REGEX_MOJ_IMPORT.matcher((CharSequence)$$0);
        while ($$7.find()) {
            boolean $$9;
            if (GlslPreprocessor.isDirectiveDisabled($$0, $$7, $$4)) continue;
            String $$8 = $$7.group(2);
            boolean bl = $$9 = $$8 != null;
            if (!$$9) {
                $$8 = $$7.group(3);
            }
            if ($$8 == null) continue;
            String $$10 = $$0.substring($$4, $$7.start(1));
            String $$11 = $$2 + $$8;
            String $$12 = this.applyImport($$9, $$11);
            if (!Strings.isNullOrEmpty((String)$$12)) {
                if (!StringUtil.endsWithNewLine($$12)) {
                    $$12 = $$12 + System.lineSeparator();
                }
                int $$13 = ++$$1.sourceId;
                List<String> $$14 = this.processImports($$12, $$1, $$9 ? FileUtil.getFullResourcePath($$11) : "");
                $$14.set(0, (Object)String.format((Locale)Locale.ROOT, (String)"#line %d %d\n%s", (Object[])new Object[]{0, $$13, this.processVersions((String)$$14.get(0), $$1)}));
                if (!StringUtils.isBlank((CharSequence)$$10)) {
                    $$6.add((Object)$$10);
                }
                $$6.addAll($$14);
            } else {
                String $$15 = $$9 ? String.format((Locale)Locale.ROOT, (String)"/*#moj_import \"%s\"*/", (Object[])new Object[]{$$8}) : String.format((Locale)Locale.ROOT, (String)"/*#moj_import <%s>*/", (Object[])new Object[]{$$8});
                $$6.add((Object)($$5 + $$10 + $$15));
            }
            int $$16 = StringUtil.lineCount($$0.substring(0, $$7.end(1)));
            $$5 = String.format((Locale)Locale.ROOT, (String)"#line %d %d", (Object[])new Object[]{$$16, $$3});
            $$4 = $$7.end(1);
        }
        String $$17 = $$0.substring($$4);
        if (!StringUtils.isBlank((CharSequence)$$17)) {
            $$6.add((Object)($$5 + $$17));
        }
        return $$6;
    }

    private String processVersions(String $$0, Context $$1) {
        Matcher $$2 = REGEX_VERSION.matcher((CharSequence)$$0);
        if ($$2.find() && GlslPreprocessor.isDirectiveEnabled($$0, $$2)) {
            $$1.glslVersion = Math.max((int)$$1.glslVersion, (int)Integer.parseInt((String)$$2.group(2)));
            return $$0.substring(0, $$2.start(1)) + "/*" + $$0.substring($$2.start(1), $$2.end(1)) + "*/" + $$0.substring($$2.end(1));
        }
        return $$0;
    }

    private String setVersion(String $$0, int $$1) {
        Matcher $$2 = REGEX_VERSION.matcher((CharSequence)$$0);
        if ($$2.find() && GlslPreprocessor.isDirectiveEnabled($$0, $$2)) {
            return $$0.substring(0, $$2.start(2)) + Math.max((int)$$1, (int)Integer.parseInt((String)$$2.group(2))) + $$0.substring($$2.end(2));
        }
        return $$0;
    }

    private static boolean isDirectiveEnabled(String $$0, Matcher $$1) {
        return !GlslPreprocessor.isDirectiveDisabled($$0, $$1, 0);
    }

    private static boolean isDirectiveDisabled(String $$0, Matcher $$1, int $$2) {
        int $$3 = $$1.start() - $$2;
        if ($$3 == 0) {
            return false;
        }
        Matcher $$4 = REGEX_ENDS_WITH_WHITESPACE.matcher((CharSequence)$$0.substring($$2, $$1.start()));
        if (!$$4.find()) {
            return true;
        }
        int $$5 = $$4.end(1);
        return $$5 == $$1.start();
    }

    @Nullable
    public abstract String applyImport(boolean var1, String var2);

    static final class Context {
        int glslVersion;
        int sourceId;

        Context() {
        }
    }
}