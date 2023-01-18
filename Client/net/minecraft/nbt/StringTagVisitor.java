/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  java.util.regex.Pattern
 */
package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagVisitor;

public class StringTagVisitor
implements TagVisitor {
    private static final Pattern SIMPLE_VALUE = Pattern.compile((String)"[A-Za-z0-9._+-]+");
    private final StringBuilder builder = new StringBuilder();

    public String visit(Tag $$0) {
        $$0.accept(this);
        return this.builder.toString();
    }

    @Override
    public void visitString(StringTag $$0) {
        this.builder.append(StringTag.quoteAndEscape($$0.getAsString()));
    }

    @Override
    public void visitByte(ByteTag $$0) {
        this.builder.append((Object)$$0.getAsNumber()).append('b');
    }

    @Override
    public void visitShort(ShortTag $$0) {
        this.builder.append((Object)$$0.getAsNumber()).append('s');
    }

    @Override
    public void visitInt(IntTag $$0) {
        this.builder.append((Object)$$0.getAsNumber());
    }

    @Override
    public void visitLong(LongTag $$0) {
        this.builder.append((Object)$$0.getAsNumber()).append('L');
    }

    @Override
    public void visitFloat(FloatTag $$0) {
        this.builder.append($$0.getAsFloat()).append('f');
    }

    @Override
    public void visitDouble(DoubleTag $$0) {
        this.builder.append($$0.getAsDouble()).append('d');
    }

    @Override
    public void visitByteArray(ByteArrayTag $$0) {
        this.builder.append("[B;");
        byte[] $$1 = $$0.getAsByteArray();
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$2 != 0) {
                this.builder.append(',');
            }
            this.builder.append((int)$$1[$$2]).append('B');
        }
        this.builder.append(']');
    }

    @Override
    public void visitIntArray(IntArrayTag $$0) {
        this.builder.append("[I;");
        int[] $$1 = $$0.getAsIntArray();
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$2 != 0) {
                this.builder.append(',');
            }
            this.builder.append($$1[$$2]);
        }
        this.builder.append(']');
    }

    @Override
    public void visitLongArray(LongArrayTag $$0) {
        this.builder.append("[L;");
        long[] $$1 = $$0.getAsLongArray();
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$2 != 0) {
                this.builder.append(',');
            }
            this.builder.append($$1[$$2]).append('L');
        }
        this.builder.append(']');
    }

    @Override
    public void visitList(ListTag $$0) {
        this.builder.append('[');
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            if ($$1 != 0) {
                this.builder.append(',');
            }
            this.builder.append(new StringTagVisitor().visit($$0.get($$1)));
        }
        this.builder.append(']');
    }

    @Override
    public void visitCompound(CompoundTag $$0) {
        this.builder.append('{');
        ArrayList $$1 = Lists.newArrayList($$0.getAllKeys());
        Collections.sort((List)$$1);
        for (String $$2 : $$1) {
            if (this.builder.length() != 1) {
                this.builder.append(',');
            }
            this.builder.append(StringTagVisitor.handleEscape($$2)).append(':').append(new StringTagVisitor().visit($$0.get($$2)));
        }
        this.builder.append('}');
    }

    protected static String handleEscape(String $$0) {
        if (SIMPLE_VALUE.matcher((CharSequence)$$0).matches()) {
            return $$0;
        }
        return StringTag.quoteAndEscape($$0);
    }

    @Override
    public void visitEnd(EndTag $$0) {
        this.builder.append("END");
    }
}