/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;

public record ChatTypeDecoration(String translationKey, List<Parameter> parameters, Style style) {
    public static final Codec<ChatTypeDecoration> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.STRING.fieldOf("translation_key").forGetter(ChatTypeDecoration::translationKey), (App)Parameter.CODEC.listOf().fieldOf("parameters").forGetter(ChatTypeDecoration::parameters), (App)Style.FORMATTING_CODEC.optionalFieldOf("style", (Object)Style.EMPTY).forGetter(ChatTypeDecoration::style)).apply((Applicative)$$0, ChatTypeDecoration::new));

    public static ChatTypeDecoration withSender(String $$0) {
        return new ChatTypeDecoration($$0, (List<Parameter>)List.of((Object)Parameter.SENDER, (Object)Parameter.CONTENT), Style.EMPTY);
    }

    public static ChatTypeDecoration incomingDirectMessage(String $$0) {
        Style $$1 = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
        return new ChatTypeDecoration($$0, (List<Parameter>)List.of((Object)Parameter.SENDER, (Object)Parameter.CONTENT), $$1);
    }

    public static ChatTypeDecoration outgoingDirectMessage(String $$0) {
        Style $$1 = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
        return new ChatTypeDecoration($$0, (List<Parameter>)List.of((Object)Parameter.TARGET, (Object)Parameter.CONTENT), $$1);
    }

    public static ChatTypeDecoration teamMessage(String $$0) {
        return new ChatTypeDecoration($$0, (List<Parameter>)List.of((Object)Parameter.TARGET, (Object)Parameter.SENDER, (Object)Parameter.CONTENT), Style.EMPTY);
    }

    public Component decorate(Component $$0, ChatType.Bound $$1) {
        Object[] $$2 = this.resolveParameters($$0, $$1);
        return Component.translatable(this.translationKey, $$2).withStyle(this.style);
    }

    private Component[] resolveParameters(Component $$0, ChatType.Bound $$1) {
        Component[] $$2 = new Component[this.parameters.size()];
        for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
            Parameter $$4 = (Parameter)this.parameters.get($$3);
            $$2[$$3] = $$4.select($$0, $$1);
        }
        return $$2;
    }

    public static enum Parameter implements StringRepresentable
    {
        SENDER("sender", ($$0, $$1) -> $$1.name()),
        TARGET("target", ($$0, $$1) -> $$1.targetName()),
        CONTENT("content", ($$0, $$1) -> $$0);

        public static final Codec<Parameter> CODEC;
        private final String name;
        private final Selector selector;

        private Parameter(String $$0, Selector $$1) {
            this.name = $$0;
            this.selector = $$1;
        }

        public Component select(Component $$0, ChatType.Bound $$1) {
            Component $$2 = this.selector.select($$0, $$1);
            return (Component)Objects.requireNonNullElse((Object)$$2, (Object)CommonComponents.EMPTY);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Parameter::values));
        }

        public static interface Selector {
            @Nullable
            public Component select(Component var1, ChatType.Bound var2);
        }
    }
}