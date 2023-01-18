/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class NbtContents
implements ComponentContents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final boolean interpreting;
    private final Optional<Component> separator;
    private final String nbtPathPattern;
    private final DataSource dataSource;
    @Nullable
    protected final NbtPathArgument.NbtPath compiledNbtPath;

    public NbtContents(String $$0, boolean $$1, Optional<Component> $$2, DataSource $$3) {
        this($$0, NbtContents.compileNbtPath($$0), $$1, $$2, $$3);
    }

    private NbtContents(String $$0, @Nullable NbtPathArgument.NbtPath $$1, boolean $$2, Optional<Component> $$3, DataSource $$4) {
        this.nbtPathPattern = $$0;
        this.compiledNbtPath = $$1;
        this.interpreting = $$2;
        this.separator = $$3;
        this.dataSource = $$4;
    }

    @Nullable
    private static NbtPathArgument.NbtPath compileNbtPath(String $$0) {
        try {
            return new NbtPathArgument().parse(new StringReader($$0));
        }
        catch (CommandSyntaxException $$1) {
            return null;
        }
    }

    public String getNbtPath() {
        return this.nbtPathPattern;
    }

    public boolean isInterpreting() {
        return this.interpreting;
    }

    public Optional<Component> getSeparator() {
        return this.separator;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof NbtContents)) return false;
        NbtContents $$1 = (NbtContents)$$0;
        if (!this.dataSource.equals($$1.dataSource)) return false;
        if (!this.separator.equals($$1.separator)) return false;
        if (this.interpreting != $$1.interpreting) return false;
        if (!this.nbtPathPattern.equals((Object)$$1.nbtPathPattern)) return false;
        return true;
    }

    public int hashCode() {
        int $$0 = this.interpreting ? 1 : 0;
        $$0 = 31 * $$0 + this.separator.hashCode();
        $$0 = 31 * $$0 + this.nbtPathPattern.hashCode();
        $$0 = 31 * $$0 + this.dataSource.hashCode();
        return $$0;
    }

    public String toString() {
        return "nbt{" + this.dataSource + ", interpreting=" + this.interpreting + ", separator=" + this.separator + "}";
    }

    @Override
    public MutableComponent resolve(@Nullable CommandSourceStack $$02, @Nullable Entity $$13, int $$22) throws CommandSyntaxException {
        if ($$02 == null || this.compiledNbtPath == null) {
            return Component.empty();
        }
        Stream $$32 = this.dataSource.getData($$02).flatMap($$0 -> {
            try {
                return this.compiledNbtPath.get((Tag)$$0).stream();
            }
            catch (CommandSyntaxException $$1) {
                return Stream.empty();
            }
        }).map(Tag::getAsString);
        if (this.interpreting) {
            Component $$4 = (Component)DataFixUtils.orElse(ComponentUtils.updateForEntity($$02, this.separator, $$13, $$22), (Object)ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR);
            return (MutableComponent)$$32.flatMap($$3 -> {
                try {
                    MutableComponent $$4 = Component.Serializer.fromJson($$3);
                    return Stream.of((Object)ComponentUtils.updateForEntity($$02, $$4, $$13, $$22));
                }
                catch (Exception $$5) {
                    LOGGER.warn("Failed to parse component: {}", $$3, (Object)$$5);
                    return Stream.of((Object[])new MutableComponent[0]);
                }
            }).reduce(($$1, $$2) -> $$1.append($$4).append((Component)$$2)).orElseGet(Component::empty);
        }
        return (MutableComponent)ComponentUtils.updateForEntity($$02, this.separator, $$13, $$22).map($$12 -> (MutableComponent)$$32.map(Component::literal).reduce(($$1, $$2) -> $$1.append((Component)$$12).append((Component)$$2)).orElseGet(Component::empty)).orElseGet(() -> Component.literal((String)$$32.collect(Collectors.joining((CharSequence)", "))));
    }
}