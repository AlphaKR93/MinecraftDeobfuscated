/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Splitter
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.List
 *  java.util.Locale
 *  java.util.Optional
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class KeyValueCondition
implements Condition {
    private static final Splitter PIPE_SPLITTER = Splitter.on((char)'|').omitEmptyStrings();
    private final String key;
    private final String value;

    public KeyValueCondition(String $$0, String $$1) {
        this.key = $$0;
        this.value = $$1;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> $$0) {
        Predicate $$7;
        List $$4;
        boolean $$3;
        Property<?> $$1 = $$0.getProperty(this.key);
        if ($$1 == null) {
            throw new RuntimeException(String.format((Locale)Locale.ROOT, (String)"Unknown property '%s' on '%s'", (Object[])new Object[]{this.key, $$0.getOwner()}));
        }
        String $$22 = this.value;
        boolean bl = $$3 = !$$22.isEmpty() && $$22.charAt(0) == '!';
        if ($$3) {
            $$22 = $$22.substring(1);
        }
        if (($$4 = PIPE_SPLITTER.splitToList((CharSequence)$$22)).isEmpty()) {
            throw new RuntimeException(String.format((Locale)Locale.ROOT, (String)"Empty value '%s' for property '%s' on '%s'", (Object[])new Object[]{this.value, this.key, $$0.getOwner()}));
        }
        if ($$4.size() == 1) {
            Predicate<BlockState> $$5 = this.getBlockStatePredicate($$0, $$1, $$22);
        } else {
            List $$6 = (List)$$4.stream().map($$2 -> this.getBlockStatePredicate($$0, $$1, (String)$$2)).collect(Collectors.toList());
            $$7 = $$12 -> $$6.stream().anyMatch($$1 -> $$1.test($$12));
        }
        return $$3 ? $$7.negate() : $$7;
    }

    private Predicate<BlockState> getBlockStatePredicate(StateDefinition<Block, BlockState> $$0, Property<?> $$1, String $$22) {
        Optional<?> $$3 = $$1.getValue($$22);
        if (!$$3.isPresent()) {
            throw new RuntimeException(String.format((Locale)Locale.ROOT, (String)"Unknown value '%s' for property '%s' on '%s' in '%s'", (Object[])new Object[]{$$22, this.key, $$0.getOwner(), this.value}));
        }
        return $$2 -> $$2.getValue($$1).equals($$3.get());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("key", (Object)this.key).add("value", (Object)this.value).toString();
    }
}