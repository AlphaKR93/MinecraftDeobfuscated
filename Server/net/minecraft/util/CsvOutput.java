/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.io.IOException
 *  java.io.Writer
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CsvOutput {
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String FIELD_SEPARATOR = ",";
    private final Writer output;
    private final int columnCount;

    CsvOutput(Writer $$0, List<String> $$1) throws IOException {
        this.output = $$0;
        this.columnCount = $$1.size();
        this.writeLine($$1.stream());
    }

    public static Builder builder() {
        return new Builder();
    }

    public void writeRow(Object ... $$0) throws IOException {
        if ($$0.length != this.columnCount) {
            throw new IllegalArgumentException("Invalid number of columns, expected " + this.columnCount + ", but got " + $$0.length);
        }
        this.writeLine(Stream.of((Object[])$$0));
    }

    private void writeLine(Stream<?> $$0) throws IOException {
        this.output.write((String)$$0.map(CsvOutput::getStringValue).collect(Collectors.joining((CharSequence)FIELD_SEPARATOR)) + LINE_SEPARATOR);
    }

    private static String getStringValue(@Nullable Object $$0) {
        return StringEscapeUtils.escapeCsv((String)($$0 != null ? $$0.toString() : "[null]"));
    }

    public static class Builder {
        private final List<String> headers = Lists.newArrayList();

        public Builder addColumn(String $$0) {
            this.headers.add((Object)$$0);
            return this;
        }

        public CsvOutput build(Writer $$0) throws IOException {
            return new CsvOutput($$0, this.headers);
        }
    }
}