/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType$StringType
 *  java.lang.Enum
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class StringArgumentSerializer
implements ArgumentTypeInfo<StringArgumentType, Template> {
    @Override
    public void serializeToNetwork(Template $$0, FriendlyByteBuf $$1) {
        $$1.writeEnum((Enum<?>)$$0.type);
    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf $$0) {
        StringArgumentType.StringType $$1 = $$0.readEnum(StringArgumentType.StringType.class);
        return new Template($$1);
    }

    @Override
    public void serializeToJson(Template $$0, JsonObject $$1) {
        $$1.addProperty("type", switch ($$0.type) {
            default -> throw new IncompatibleClassChangeError();
            case StringArgumentType.StringType.SINGLE_WORD -> "word";
            case StringArgumentType.StringType.QUOTABLE_PHRASE -> "phrase";
            case StringArgumentType.StringType.GREEDY_PHRASE -> "greedy";
        });
    }

    @Override
    public Template unpack(StringArgumentType $$0) {
        return new Template($$0.getType());
    }

    public final class Template
    implements ArgumentTypeInfo.Template<StringArgumentType> {
        final StringArgumentType.StringType type;

        public Template(StringArgumentType.StringType $$1) {
            this.type = $$1;
        }

        @Override
        public StringArgumentType instantiate(CommandBuildContext $$0) {
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case StringArgumentType.StringType.SINGLE_WORD -> StringArgumentType.word();
                case StringArgumentType.StringType.QUOTABLE_PHRASE -> StringArgumentType.string();
                case StringArgumentType.StringType.GREEDY_PHRASE -> StringArgumentType.greedyString();
            };
        }

        @Override
        public ArgumentTypeInfo<StringArgumentType, ?> type() {
            return StringArgumentSerializer.this;
        }
    }
}