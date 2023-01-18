/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Map
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BiFunction
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument
implements ArgumentType<Anchor> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"eyes", "feet"});
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.anchor.invalid", $$0));

    public static Anchor getAnchor(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Anchor)((Object)$$0.getArgument($$1, Anchor.class));
    }

    public static EntityAnchorArgument anchor() {
        return new EntityAnchorArgument();
    }

    public Anchor parse(StringReader $$0) throws CommandSyntaxException {
        int $$1 = $$0.getCursor();
        String $$2 = $$0.readUnquotedString();
        Anchor $$3 = Anchor.getByName($$2);
        if ($$3 == null) {
            $$0.setCursor($$1);
            throw ERROR_INVALID.createWithContext((ImmutableStringReader)$$0, (Object)$$2);
        }
        return $$3;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        return SharedSuggestionProvider.suggest((Iterable<String>)Anchor.BY_NAME.keySet(), $$1);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static enum Anchor {
        FEET("feet", (BiFunction<Vec3, Entity, Vec3>)((BiFunction)($$0, $$1) -> $$0)),
        EYES("eyes", (BiFunction<Vec3, Entity, Vec3>)((BiFunction)($$0, $$1) -> new Vec3($$0.x, $$0.y + (double)$$1.getEyeHeight(), $$0.z)));

        static final Map<String, Anchor> BY_NAME;
        private final String name;
        private final BiFunction<Vec3, Entity, Vec3> transform;

        private Anchor(String $$0, BiFunction<Vec3, Entity, Vec3> $$1) {
            this.name = $$0;
            this.transform = $$1;
        }

        @Nullable
        public static Anchor getByName(String $$0) {
            return (Anchor)((Object)BY_NAME.get((Object)$$0));
        }

        public Vec3 apply(Entity $$0) {
            return (Vec3)this.transform.apply((Object)$$0.position(), (Object)$$0);
        }

        public Vec3 apply(CommandSourceStack $$0) {
            Entity $$1 = $$0.getEntity();
            if ($$1 == null) {
                return $$0.getPosition();
            }
            return (Vec3)this.transform.apply((Object)$$0.getPosition(), (Object)$$1);
        }

        static {
            BY_NAME = (Map)Util.make(Maps.newHashMap(), $$0 -> {
                for (Anchor $$1 : Anchor.values()) {
                    $$0.put((Object)$$1.name, (Object)$$1);
                }
            });
        }
    }
}