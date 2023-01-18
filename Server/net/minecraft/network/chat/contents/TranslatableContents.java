/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat.contents;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableFormatException;
import net.minecraft.world.entity.Entity;

public class TranslatableContents
implements ComponentContents {
    private static final Object[] NO_ARGS = new Object[0];
    private static final FormattedText TEXT_PERCENT = FormattedText.of("%");
    private static final FormattedText TEXT_NULL = FormattedText.of("null");
    private final String key;
    private final Object[] args;
    @Nullable
    private Language decomposedWith;
    private List<FormattedText> decomposedParts = ImmutableList.of();
    private static final Pattern FORMAT_PATTERN = Pattern.compile((String)"%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public TranslatableContents(String $$0) {
        this.key = $$0;
        this.args = NO_ARGS;
    }

    public TranslatableContents(String $$0, Object ... $$1) {
        this.key = $$0;
        this.args = $$1;
    }

    private void decompose() {
        Language $$0 = Language.getInstance();
        if ($$0 == this.decomposedWith) {
            return;
        }
        this.decomposedWith = $$0;
        String $$1 = $$0.getOrDefault(this.key);
        try {
            ImmutableList.Builder $$2 = ImmutableList.builder();
            this.decomposeTemplate($$1, (Consumer<FormattedText>)((Consumer)arg_0 -> ((ImmutableList.Builder)$$2).add(arg_0)));
            this.decomposedParts = $$2.build();
        }
        catch (TranslatableFormatException $$3) {
            this.decomposedParts = ImmutableList.of((Object)FormattedText.of($$1));
        }
    }

    private void decomposeTemplate(String $$0, Consumer<FormattedText> $$1) {
        Matcher $$2 = FORMAT_PATTERN.matcher((CharSequence)$$0);
        try {
            int $$3 = 0;
            int $$4 = 0;
            while ($$2.find($$4)) {
                int $$5 = $$2.start();
                int $$6 = $$2.end();
                if ($$5 > $$4) {
                    String $$7 = $$0.substring($$4, $$5);
                    if ($$7.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }
                    $$1.accept((Object)FormattedText.of($$7));
                }
                String $$8 = $$2.group(2);
                String $$9 = $$0.substring($$5, $$6);
                if ("%".equals((Object)$$8) && "%%".equals((Object)$$9)) {
                    $$1.accept((Object)TEXT_PERCENT);
                } else if ("s".equals((Object)$$8)) {
                    String $$10 = $$2.group(1);
                    int $$11 = $$10 != null ? Integer.parseInt((String)$$10) - 1 : $$3++;
                    $$1.accept((Object)this.getArgument($$11));
                } else {
                    throw new TranslatableFormatException(this, "Unsupported format: '" + $$9 + "'");
                }
                $$4 = $$6;
            }
            if ($$4 < $$0.length()) {
                String $$12 = $$0.substring($$4);
                if ($$12.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }
                $$1.accept((Object)FormattedText.of($$12));
            }
        }
        catch (IllegalArgumentException $$13) {
            throw new TranslatableFormatException(this, $$13);
        }
    }

    private FormattedText getArgument(int $$0) {
        if ($$0 < 0) {
            throw new TranslatableFormatException(this, $$0);
        }
        if ($$0 >= this.args.length) {
            return Component.EMPTY;
        }
        Object $$1 = this.args[$$0];
        if ($$1 instanceof Component) {
            return (Component)$$1;
        }
        return $$1 == null ? TEXT_NULL : FormattedText.of($$1.toString());
    }

    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> $$0, Style $$1) {
        this.decompose();
        for (FormattedText $$2 : this.decomposedParts) {
            Optional<T> $$3 = $$2.visit($$0, $$1);
            if (!$$3.isPresent()) continue;
            return $$3;
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> $$0) {
        this.decompose();
        for (FormattedText $$1 : this.decomposedParts) {
            Optional<T> $$2 = $$1.visit($$0);
            if (!$$2.isPresent()) continue;
            return $$2;
        }
        return Optional.empty();
    }

    @Override
    public MutableComponent resolve(@Nullable CommandSourceStack $$0, @Nullable Entity $$1, int $$2) throws CommandSyntaxException {
        Object[] $$3 = new Object[this.args.length];
        for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
            Object $$5 = this.args[$$4];
            $$3[$$4] = $$5 instanceof Component ? ComponentUtils.updateForEntity($$0, (Component)$$5, $$1, $$2) : $$5;
        }
        return MutableComponent.create(new TranslatableContents(this.key, $$3));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof TranslatableContents)) return false;
        TranslatableContents $$1 = (TranslatableContents)$$0;
        if (!this.key.equals((Object)$$1.key)) return false;
        if (!Arrays.equals((Object[])this.args, (Object[])$$1.args)) return false;
        return true;
    }

    public int hashCode() {
        int $$0 = this.key.hashCode();
        $$0 = 31 * $$0 + Arrays.hashCode((Object[])this.args);
        return $$0;
    }

    public String toString() {
        return "translation{key='" + this.key + "', args=" + Arrays.toString((Object[])this.args) + "}";
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }
}