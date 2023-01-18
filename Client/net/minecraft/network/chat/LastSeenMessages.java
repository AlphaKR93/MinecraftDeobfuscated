/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.security.SignatureException
 *  java.util.ArrayList
 *  java.util.BitSet
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.util.SignatureUpdater;

public record LastSeenMessages(List<MessageSignature> entries) {
    public static final Codec<LastSeenMessages> CODEC = MessageSignature.CODEC.listOf().xmap(LastSeenMessages::new, LastSeenMessages::entries);
    public static LastSeenMessages EMPTY = new LastSeenMessages((List<MessageSignature>)List.of());
    public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 20;

    public void updateSignature(SignatureUpdater.Output $$0) throws SignatureException {
        $$0.update(Ints.toByteArray((int)this.entries.size()));
        for (MessageSignature $$1 : this.entries) {
            $$0.update($$1.bytes());
        }
    }

    public Packed pack(MessageSignatureCache $$0) {
        return new Packed((List<MessageSignature.Packed>)this.entries.stream().map($$1 -> $$1.pack($$0)).toList());
    }

    public record Packed(List<MessageSignature.Packed> entries) {
        public static final Packed EMPTY = new Packed((List<MessageSignature.Packed>)List.of());

        public Packed(FriendlyByteBuf $$0) {
            this((List<MessageSignature.Packed>)((List)$$0.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 20), MessageSignature.Packed::read)));
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeCollection(this.entries, MessageSignature.Packed::write);
        }

        public Optional<LastSeenMessages> unpack(MessageSignatureCache $$0) {
            ArrayList $$1 = new ArrayList(this.entries.size());
            for (MessageSignature.Packed $$2 : this.entries) {
                Optional<MessageSignature> $$3 = $$2.unpack($$0);
                if ($$3.isEmpty()) {
                    return Optional.empty();
                }
                $$1.add((Object)((MessageSignature)((Object)$$3.get())));
            }
            return Optional.of((Object)((Object)new LastSeenMessages((List<MessageSignature>)$$1)));
        }
    }

    public record Update(int offset, BitSet acknowledged) {
        public Update(FriendlyByteBuf $$0) {
            this($$0.readVarInt(), $$0.readFixedBitSet(20));
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeVarInt(this.offset);
            $$0.writeFixedBitSet(this.acknowledged, 20);
        }
    }
}