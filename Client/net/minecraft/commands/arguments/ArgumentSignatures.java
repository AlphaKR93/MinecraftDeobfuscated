/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignableCommand;

public record ArgumentSignatures(List<Entry> entries) {
    public static final ArgumentSignatures EMPTY = new ArgumentSignatures((List<Entry>)List.of());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatures(FriendlyByteBuf $$0) {
        this((List<Entry>)((List)$$0.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 8), Entry::new)));
    }

    @Nullable
    public MessageSignature get(String $$0) {
        for (Entry $$1 : this.entries) {
            if (!$$1.name.equals((Object)$$0)) continue;
            return $$1.signature;
        }
        return null;
    }

    public void write(FriendlyByteBuf $$02) {
        $$02.writeCollection(this.entries, ($$0, $$1) -> $$1.write((FriendlyByteBuf)((Object)$$0)));
    }

    public static ArgumentSignatures signCommand(SignableCommand<?> $$0, Signer $$12) {
        List $$2 = $$0.arguments().stream().map($$1 -> {
            MessageSignature $$2 = $$12.sign($$1.value());
            if ($$2 != null) {
                return new Entry($$1.name(), $$2);
            }
            return null;
        }).filter(Objects::nonNull).toList();
        return new ArgumentSignatures((List<Entry>)$$2);
    }

    public record Entry(String name, MessageSignature signature) {
        public Entry(FriendlyByteBuf $$0) {
            this($$0.readUtf(16), MessageSignature.read($$0));
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeUtf(this.name, 16);
            MessageSignature.write($$0, this.signature);
        }
    }

    @FunctionalInterface
    public static interface Signer {
        @Nullable
        public MessageSignature sign(String var1);
    }
}