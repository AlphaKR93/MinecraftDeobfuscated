/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.security.SignatureException
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.TemporalAmount
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record PlayerChatMessage(SignedMessageLink link, @Nullable MessageSignature signature, SignedMessageBody signedBody, @Nullable Component unsignedContent, FilterMask filterMask) {
    public static final MapCodec<PlayerChatMessage> MAP_CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)SignedMessageLink.CODEC.fieldOf("link").forGetter(PlayerChatMessage::link), (App)MessageSignature.CODEC.optionalFieldOf("signature").forGetter($$0 -> Optional.ofNullable((Object)((Object)$$0.signature))), (App)SignedMessageBody.MAP_CODEC.forGetter(PlayerChatMessage::signedBody), (App)ExtraCodecs.COMPONENT.optionalFieldOf("unsigned_content").forGetter($$0 -> Optional.ofNullable((Object)$$0.unsignedContent)), (App)FilterMask.CODEC.optionalFieldOf("filter_mask", (Object)FilterMask.PASS_THROUGH).forGetter(PlayerChatMessage::filterMask)).apply((Applicative)$$02, ($$0, $$1, $$2, $$3, $$4) -> new PlayerChatMessage((SignedMessageLink)((Object)((Object)$$0)), (MessageSignature)((Object)((Object)((Object)$$1.orElse(null)))), (SignedMessageBody)((Object)((Object)$$2)), (Component)$$3.orElse(null), (FilterMask)$$4)));
    private static final UUID SYSTEM_SENDER = Util.NIL_UUID;
    public static final Duration MESSAGE_EXPIRES_AFTER_SERVER = Duration.ofMinutes((long)5L);
    public static final Duration MESSAGE_EXPIRES_AFTER_CLIENT = MESSAGE_EXPIRES_AFTER_SERVER.plus(Duration.ofMinutes((long)2L));

    public static PlayerChatMessage system(String $$0) {
        return PlayerChatMessage.unsigned(SYSTEM_SENDER, $$0);
    }

    public static PlayerChatMessage unsigned(UUID $$0, String $$1) {
        SignedMessageBody $$2 = SignedMessageBody.unsigned($$1);
        SignedMessageLink $$3 = SignedMessageLink.unsigned($$0);
        return new PlayerChatMessage($$3, null, $$2, null, FilterMask.PASS_THROUGH);
    }

    public PlayerChatMessage withUnsignedContent(Component $$0) {
        Component $$1 = !$$0.equals(Component.literal(this.signedContent())) ? $$0 : null;
        return new PlayerChatMessage(this.link, this.signature, this.signedBody, $$1, this.filterMask);
    }

    public PlayerChatMessage removeUnsignedContent() {
        if (this.unsignedContent != null) {
            return new PlayerChatMessage(this.link, this.signature, this.signedBody, null, this.filterMask);
        }
        return this;
    }

    public PlayerChatMessage filter(FilterMask $$0) {
        if (this.filterMask.equals($$0)) {
            return this;
        }
        return new PlayerChatMessage(this.link, this.signature, this.signedBody, this.unsignedContent, $$0);
    }

    public PlayerChatMessage filter(boolean $$0) {
        return this.filter($$0 ? this.filterMask : FilterMask.PASS_THROUGH);
    }

    public static void updateSignature(SignatureUpdater.Output $$0, SignedMessageLink $$1, SignedMessageBody $$2) throws SignatureException {
        $$0.update(Ints.toByteArray((int)1));
        $$1.updateSignature($$0);
        $$2.updateSignature($$0);
    }

    public boolean verify(SignatureValidator $$02) {
        return this.signature != null && this.signature.verify($$02, $$0 -> PlayerChatMessage.updateSignature($$0, this.link, this.signedBody));
    }

    public String signedContent() {
        return this.signedBody.content();
    }

    public Component decoratedContent() {
        return (Component)Objects.requireNonNullElseGet((Object)this.unsignedContent, () -> Component.literal(this.signedContent()));
    }

    public Instant timeStamp() {
        return this.signedBody.timeStamp();
    }

    public long salt() {
        return this.signedBody.salt();
    }

    public boolean hasExpiredServer(Instant $$0) {
        return $$0.isAfter(this.timeStamp().plus((TemporalAmount)MESSAGE_EXPIRES_AFTER_SERVER));
    }

    public boolean hasExpiredClient(Instant $$0) {
        return $$0.isAfter(this.timeStamp().plus((TemporalAmount)MESSAGE_EXPIRES_AFTER_CLIENT));
    }

    public UUID sender() {
        return this.link.sender();
    }

    public boolean isSystem() {
        return this.sender().equals((Object)SYSTEM_SENDER);
    }

    public boolean hasSignature() {
        return this.signature != null;
    }

    public boolean hasSignatureFrom(UUID $$0) {
        return this.hasSignature() && this.link.sender().equals((Object)$$0);
    }

    public boolean isFullyFiltered() {
        return this.filterMask.isFullyFiltered();
    }
}