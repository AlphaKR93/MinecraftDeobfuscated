/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import javax.annotation.Nullable;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.SignatureValidator;

@FunctionalInterface
public interface SignedMessageValidator {
    public static final SignedMessageValidator ACCEPT_UNSIGNED = $$0 -> !$$0.hasSignature();
    public static final SignedMessageValidator REJECT_ALL = $$0 -> false;

    public boolean updateAndValidate(PlayerChatMessage var1);

    public static class KeyBased
    implements SignedMessageValidator {
        private final SignatureValidator validator;
        @Nullable
        private PlayerChatMessage lastMessage;
        private boolean isChainValid = true;

        public KeyBased(SignatureValidator $$0) {
            this.validator = $$0;
        }

        private boolean validateChain(PlayerChatMessage $$0) {
            if ($$0.equals((Object)this.lastMessage)) {
                return true;
            }
            return this.lastMessage == null || $$0.link().isDescendantOf(this.lastMessage.link());
        }

        @Override
        public boolean updateAndValidate(PlayerChatMessage $$0) {
            boolean bl = this.isChainValid = this.isChainValid && $$0.verify(this.validator) && this.validateChain($$0);
            if (!this.isChainValid) {
                return false;
            }
            this.lastMessage = $$0;
            return true;
        }
    }
}