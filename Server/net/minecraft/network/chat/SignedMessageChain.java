/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.time.Instant
 *  java.util.UUID
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class SignedMessageChain {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private SignedMessageLink nextLink;

    public SignedMessageChain(UUID $$0, UUID $$1) {
        this.nextLink = SignedMessageLink.root($$0, $$1);
    }

    public Encoder encoder(Signer $$0) {
        return $$1 -> {
            SignedMessageLink $$22 = this.advanceLink();
            if ($$22 == null) {
                return null;
            }
            return new MessageSignature($$0.sign($$2 -> PlayerChatMessage.updateSignature($$2, $$22, $$1)));
        };
    }

    public Decoder decoder(ProfilePublicKey $$0) {
        SignatureValidator $$1 = $$0.createSignatureValidator();
        return ($$2, $$3) -> {
            SignedMessageLink $$4 = this.advanceLink();
            if ($$4 == null) {
                throw new DecodeException((Component)Component.translatable("chat.disabled.chain_broken"), false);
            }
            if ($$0.data().hasExpired()) {
                throw new DecodeException((Component)Component.translatable("chat.disabled.expiredProfileKey"), false);
            }
            PlayerChatMessage $$5 = new PlayerChatMessage($$4, $$2, $$3, null, FilterMask.PASS_THROUGH);
            if (!$$5.verify($$1)) {
                throw new DecodeException((Component)Component.translatable("multiplayer.disconnect.unsigned_chat"), true);
            }
            if ($$5.hasExpiredServer(Instant.now())) {
                LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", (Object)$$3.content());
            }
            return $$5;
        };
    }

    @Nullable
    private SignedMessageLink advanceLink() {
        SignedMessageLink $$0 = this.nextLink;
        if ($$0 != null) {
            this.nextLink = $$0.advance();
        }
        return $$0;
    }

    @FunctionalInterface
    public static interface Encoder {
        public static final Encoder UNSIGNED = $$0 -> null;

        @Nullable
        public MessageSignature pack(SignedMessageBody var1);
    }

    @FunctionalInterface
    public static interface Decoder {
        public static final Decoder REJECT_ALL = ($$0, $$1) -> {
            throw new DecodeException((Component)Component.translatable("chat.disabled.missingProfileKey"), false);
        };

        public static Decoder unsigned(UUID $$0) {
            return ($$1, $$2) -> PlayerChatMessage.unsigned($$0, $$2.content());
        }

        public PlayerChatMessage unpack(@Nullable MessageSignature var1, SignedMessageBody var2) throws DecodeException;
    }

    public static class DecodeException
    extends ThrowingComponent {
        private final boolean shouldDisconnect;

        public DecodeException(Component $$0, boolean $$1) {
            super($$0);
            this.shouldDisconnect = $$1;
        }

        public boolean shouldDisconnect() {
            return this.shouldDisconnect;
        }
    }
}