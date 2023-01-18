/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.time.Instant
 *  java.util.Optional
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;

public enum ChatTrustLevel implements StringRepresentable
{
    SECURE("secure"),
    MODIFIED("modified"),
    NOT_SECURE("not_secure");

    public static final Codec<ChatTrustLevel> CODEC;
    private final String serializedName;

    private ChatTrustLevel(String $$0) {
        this.serializedName = $$0;
    }

    public static ChatTrustLevel evaluate(PlayerChatMessage $$0, Component $$1, Instant $$2) {
        if (!$$0.hasSignature() || $$0.hasExpiredClient($$2)) {
            return NOT_SECURE;
        }
        if (ChatTrustLevel.isModified($$0, $$1)) {
            return MODIFIED;
        }
        return SECURE;
    }

    private static boolean isModified(PlayerChatMessage $$0, Component $$1) {
        if (!$$1.getString().contains((CharSequence)$$0.signedContent())) {
            return true;
        }
        Component $$2 = $$0.unsignedContent();
        if ($$2 == null) {
            return false;
        }
        return ChatTrustLevel.containsModifiedStyle($$2);
    }

    private static boolean containsModifiedStyle(Component $$02) {
        return (Boolean)$$02.visit(($$0, $$1) -> {
            if (ChatTrustLevel.isModifiedStyle($$0)) {
                return Optional.of((Object)true);
            }
            return Optional.empty();
        }, Style.EMPTY).orElse((Object)false);
    }

    private static boolean isModifiedStyle(Style $$0) {
        return !$$0.getFont().equals(Style.DEFAULT_FONT);
    }

    public boolean isNotSecure() {
        return this == NOT_SECURE;
    }

    @Nullable
    public GuiMessageTag createTag(PlayerChatMessage $$0) {
        return switch (this) {
            case MODIFIED -> GuiMessageTag.chatModified($$0.signedContent());
            case NOT_SECURE -> GuiMessageTag.chatNotSecure();
            default -> null;
        };
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)ChatTrustLevel::values));
    }
}