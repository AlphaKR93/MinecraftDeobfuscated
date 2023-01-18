/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public class TextRenderingUtils {
    private TextRenderingUtils() {
    }

    @VisibleForTesting
    protected static List<String> lineBreak(String $$0) {
        return Arrays.asList((Object[])$$0.split("\\n"));
    }

    public static List<Line> decompose(String $$0, LineSegment ... $$1) {
        return TextRenderingUtils.decompose($$0, (List<LineSegment>)Arrays.asList((Object[])$$1));
    }

    private static List<Line> decompose(String $$0, List<LineSegment> $$1) {
        List<String> $$2 = TextRenderingUtils.lineBreak($$0);
        return TextRenderingUtils.insertLinks($$2, $$1);
    }

    private static List<Line> insertLinks(List<String> $$0, List<LineSegment> $$1) {
        int $$2 = 0;
        ArrayList $$3 = Lists.newArrayList();
        for (String $$4 : $$0) {
            ArrayList $$5 = Lists.newArrayList();
            List<String> $$6 = TextRenderingUtils.split($$4, "%link");
            for (String $$7 : $$6) {
                if ("%link".equals((Object)$$7)) {
                    $$5.add((Object)((LineSegment)$$1.get($$2++)));
                    continue;
                }
                $$5.add((Object)LineSegment.text($$7));
            }
            $$3.add((Object)new Line((List<LineSegment>)$$5));
        }
        return $$3;
    }

    public static List<String> split(String $$0, String $$1) {
        int $$4;
        if ($$1.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be the empty string");
        }
        ArrayList $$2 = Lists.newArrayList();
        int $$3 = 0;
        while (($$4 = $$0.indexOf($$1, $$3)) != -1) {
            if ($$4 > $$3) {
                $$2.add((Object)$$0.substring($$3, $$4));
            }
            $$2.add((Object)$$1);
            $$3 = $$4 + $$1.length();
        }
        if ($$3 < $$0.length()) {
            $$2.add((Object)$$0.substring($$3));
        }
        return $$2;
    }

    public static class LineSegment {
        private final String fullText;
        @Nullable
        private final String linkTitle;
        @Nullable
        private final String linkUrl;

        private LineSegment(String $$0) {
            this.fullText = $$0;
            this.linkTitle = null;
            this.linkUrl = null;
        }

        private LineSegment(String $$0, @Nullable String $$1, @Nullable String $$2) {
            this.fullText = $$0;
            this.linkTitle = $$1;
            this.linkUrl = $$2;
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            LineSegment $$1 = (LineSegment)$$0;
            return Objects.equals((Object)this.fullText, (Object)$$1.fullText) && Objects.equals((Object)this.linkTitle, (Object)$$1.linkTitle) && Objects.equals((Object)this.linkUrl, (Object)$$1.linkUrl);
        }

        public int hashCode() {
            return Objects.hash((Object[])new Object[]{this.fullText, this.linkTitle, this.linkUrl});
        }

        public String toString() {
            return "Segment{fullText='" + this.fullText + "', linkTitle='" + this.linkTitle + "', linkUrl='" + this.linkUrl + "'}";
        }

        public String renderedText() {
            return this.isLink() ? this.linkTitle : this.fullText;
        }

        public boolean isLink() {
            return this.linkTitle != null;
        }

        public String getLinkUrl() {
            if (!this.isLink()) {
                throw new IllegalStateException("Not a link: " + this);
            }
            return this.linkUrl;
        }

        public static LineSegment link(String $$0, String $$1) {
            return new LineSegment(null, $$0, $$1);
        }

        @VisibleForTesting
        protected static LineSegment text(String $$0) {
            return new LineSegment($$0);
        }
    }

    public static class Line {
        public final List<LineSegment> segments;

        Line(LineSegment ... $$0) {
            this((List<LineSegment>)Arrays.asList((Object[])$$0));
        }

        Line(List<LineSegment> $$0) {
            this.segments = $$0;
        }

        public String toString() {
            return "Line{segments=" + this.segments + "}";
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            Line $$1 = (Line)$$0;
            return Objects.equals(this.segments, $$1.segments);
        }

        public int hashCode() {
            return Objects.hash((Object[])new Object[]{this.segments});
        }
    }
}