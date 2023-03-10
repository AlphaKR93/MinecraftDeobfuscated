/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.DataFixUtils
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Optional
 *  java.util.function.Function
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
    public static final String DEFAULT_SEPARATOR_TEXT = ", ";
    public static final Component DEFAULT_SEPARATOR = Component.literal(", ").withStyle(ChatFormatting.GRAY);
    public static final Component DEFAULT_NO_STYLE_SEPARATOR = Component.literal(", ");

    public static MutableComponent mergeStyles(MutableComponent $$0, Style $$1) {
        if ($$1.isEmpty()) {
            return $$0;
        }
        Style $$2 = $$0.getStyle();
        if ($$2.isEmpty()) {
            return $$0.setStyle($$1);
        }
        if ($$2.equals($$1)) {
            return $$0;
        }
        return $$0.setStyle($$2.applyTo($$1));
    }

    public static Optional<MutableComponent> updateForEntity(@Nullable CommandSourceStack $$0, Optional<Component> $$1, @Nullable Entity $$2, int $$3) throws CommandSyntaxException {
        return $$1.isPresent() ? Optional.of((Object)ComponentUtils.updateForEntity($$0, (Component)$$1.get(), $$2, $$3)) : Optional.empty();
    }

    public static MutableComponent updateForEntity(@Nullable CommandSourceStack $$0, Component $$1, @Nullable Entity $$2, int $$3) throws CommandSyntaxException {
        if ($$3 > 100) {
            return $$1.copy();
        }
        MutableComponent $$4 = $$1.getContents().resolve($$0, $$2, $$3 + 1);
        for (Component $$5 : $$1.getSiblings()) {
            $$4.append(ComponentUtils.updateForEntity($$0, $$5, $$2, $$3 + 1));
        }
        return $$4.withStyle(ComponentUtils.resolveStyle($$0, $$1.getStyle(), $$2, $$3));
    }

    private static Style resolveStyle(@Nullable CommandSourceStack $$0, Style $$1, @Nullable Entity $$2, int $$3) throws CommandSyntaxException {
        Component $$5;
        HoverEvent $$4 = $$1.getHoverEvent();
        if ($$4 != null && ($$5 = $$4.getValue(HoverEvent.Action.SHOW_TEXT)) != null) {
            HoverEvent $$6 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentUtils.updateForEntity($$0, $$5, $$2, $$3 + 1));
            return $$1.withHoverEvent($$6);
        }
        return $$1;
    }

    public static Component getDisplayName(GameProfile $$0) {
        if ($$0.getName() != null) {
            return Component.literal($$0.getName());
        }
        if ($$0.getId() != null) {
            return Component.literal($$0.getId().toString());
        }
        return Component.literal("(unknown)");
    }

    public static Component formatList(Collection<String> $$02) {
        return ComponentUtils.formatAndSortList($$02, $$0 -> Component.literal($$0).withStyle(ChatFormatting.GREEN));
    }

    public static <T extends Comparable<T>> Component formatAndSortList(Collection<T> $$0, Function<T, Component> $$1) {
        if ($$0.isEmpty()) {
            return CommonComponents.EMPTY;
        }
        if ($$0.size() == 1) {
            return (Component)$$1.apply((Object)((Comparable)$$0.iterator().next()));
        }
        ArrayList $$2 = Lists.newArrayList($$0);
        $$2.sort(Comparable::compareTo);
        return ComponentUtils.formatList($$2, $$1);
    }

    public static <T> Component formatList(Collection<? extends T> $$0, Function<T, Component> $$1) {
        return ComponentUtils.formatList($$0, DEFAULT_SEPARATOR, $$1);
    }

    public static <T> MutableComponent formatList(Collection<? extends T> $$0, Optional<? extends Component> $$1, Function<T, Component> $$2) {
        return ComponentUtils.formatList($$0, (Component)DataFixUtils.orElse($$1, (Object)DEFAULT_SEPARATOR), $$2);
    }

    public static Component formatList(Collection<? extends Component> $$0, Component $$1) {
        return ComponentUtils.formatList($$0, $$1, Function.identity());
    }

    public static <T> MutableComponent formatList(Collection<? extends T> $$0, Component $$1, Function<T, Component> $$2) {
        if ($$0.isEmpty()) {
            return Component.empty();
        }
        if ($$0.size() == 1) {
            return ((Component)$$2.apply($$0.iterator().next())).copy();
        }
        MutableComponent $$3 = Component.empty();
        boolean $$4 = true;
        for (Object $$5 : $$0) {
            if (!$$4) {
                $$3.append($$1);
            }
            $$3.append((Component)$$2.apply($$5));
            $$4 = false;
        }
        return $$3;
    }

    public static MutableComponent wrapInSquareBrackets(Component $$0) {
        return Component.translatable("chat.square_brackets", $$0);
    }

    public static Component fromMessage(Message $$0) {
        if ($$0 instanceof Component) {
            Component $$1 = (Component)$$0;
            return $$1;
        }
        return Component.literal($$0.getString());
    }

    public static boolean isTranslationResolvable(@Nullable Component $$0) {
        ComponentContents componentContents;
        if ($$0 != null && (componentContents = $$0.getContents()) instanceof TranslatableContents) {
            TranslatableContents $$1 = (TranslatableContents)componentContents;
            String $$2 = $$1.getKey();
            String $$3 = $$1.getFallback();
            return $$3 != null || Language.getInstance().has($$2);
        }
        return true;
    }

    public static MutableComponent copyOnClickText(String $$0) {
        return ComponentUtils.wrapInSquareBrackets(Component.literal($$0).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, $$0)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click"))).withInsertion($$0))));
    }
}