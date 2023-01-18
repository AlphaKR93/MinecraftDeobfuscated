/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.CharSequence
 *  java.lang.Character
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Array
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
    private static final Joiner COMMA_JOINED = Joiner.on((String)",");
    private final List<String[]> pattern = Lists.newArrayList();
    private final Map<Character, Predicate<BlockInWorld>> lookup = Maps.newHashMap();
    private int height;
    private int width;

    private BlockPatternBuilder() {
        this.lookup.put((Object)Character.valueOf((char)' '), $$0 -> true);
    }

    public BlockPatternBuilder aisle(String ... $$0) {
        if (ArrayUtils.isEmpty((Object[])$$0) || StringUtils.isEmpty((CharSequence)$$0[0])) {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
        if (this.pattern.isEmpty()) {
            this.height = $$0.length;
            this.width = $$0[0].length();
        }
        if ($$0.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + $$0.length + ")");
        }
        for (String $$1 : $$0) {
            if ($$1.length() != this.width) {
                throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + $$1.length() + ")");
            }
            for (char $$2 : $$1.toCharArray()) {
                if (this.lookup.containsKey((Object)Character.valueOf((char)$$2))) continue;
                this.lookup.put((Object)Character.valueOf((char)$$2), null);
            }
        }
        this.pattern.add((Object)$$0);
        return this;
    }

    public static BlockPatternBuilder start() {
        return new BlockPatternBuilder();
    }

    public BlockPatternBuilder where(char $$0, Predicate<BlockInWorld> $$1) {
        this.lookup.put((Object)Character.valueOf((char)$$0), $$1);
        return this;
    }

    public BlockPattern build() {
        return new BlockPattern(this.createPattern());
    }

    private Predicate<BlockInWorld>[][][] createPattern() {
        this.ensureAllCharactersMatched();
        Predicate[][][] $$0 = (Predicate[][][])Array.newInstance(Predicate.class, (int[])new int[]{this.pattern.size(), this.height, this.width});
        for (int $$1 = 0; $$1 < this.pattern.size(); ++$$1) {
            for (int $$2 = 0; $$2 < this.height; ++$$2) {
                for (int $$3 = 0; $$3 < this.width; ++$$3) {
                    $$0[$$1][$$2][$$3] = (Predicate)this.lookup.get((Object)Character.valueOf((char)((String[])this.pattern.get($$1))[$$2].charAt($$3)));
                }
            }
        }
        return $$0;
    }

    private void ensureAllCharactersMatched() {
        ArrayList $$0 = Lists.newArrayList();
        for (Map.Entry $$1 : this.lookup.entrySet()) {
            if ($$1.getValue() != null) continue;
            $$0.add((Object)((Character)$$1.getKey()));
        }
        if (!$$0.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join((Iterable)$$0) + " are missing");
        }
    }
}