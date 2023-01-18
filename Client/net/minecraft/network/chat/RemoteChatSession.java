/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Object
 *  java.time.Duration
 *  java.util.UUID
 */
package net.minecraft.network.chat;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.SignedMessageValidator;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {
    public SignedMessageValidator createMessageValidator() {
        return new SignedMessageValidator.KeyBased(this.profilePublicKey.createSignatureValidator());
    }

    public SignedMessageChain.Decoder createMessageDecoder(UUID $$0) {
        return new SignedMessageChain($$0, this.sessionId).decoder(this.profilePublicKey);
    }

    public Data asData() {
        return new Data(this.sessionId, this.profilePublicKey.data());
    }

    public record Data(UUID sessionId, ProfilePublicKey.Data profilePublicKey) {
        public static Data read(FriendlyByteBuf $$0) {
            return new Data($$0.readUUID(), new ProfilePublicKey.Data($$0));
        }

        public static void write(FriendlyByteBuf $$0, Data $$1) {
            $$0.writeUUID($$1.sessionId);
            $$1.profilePublicKey.write($$0);
        }

        public RemoteChatSession validate(GameProfile $$0, SignatureValidator $$1, Duration $$2) throws ProfilePublicKey.ValidationException {
            return new RemoteChatSession(this.sessionId, ProfilePublicKey.createValidated($$1, $$0.getId(), this.profilePublicKey, $$2));
        }
    }
}