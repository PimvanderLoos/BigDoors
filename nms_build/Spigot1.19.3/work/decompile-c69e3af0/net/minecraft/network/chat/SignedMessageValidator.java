package net.minecraft.network.chat;

import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;

@FunctionalInterface
public interface SignedMessageValidator {

    SignedMessageValidator ACCEPT_UNSIGNED = (playerchatmessage) -> {
        return !playerchatmessage.hasSignature();
    };
    SignedMessageValidator REJECT_ALL = (playerchatmessage) -> {
        return false;
    };

    boolean updateAndValidate(PlayerChatMessage playerchatmessage);

    public static class a implements SignedMessageValidator {

        private final SignatureValidator validator;
        @Nullable
        private PlayerChatMessage lastMessage;
        private boolean isChainValid = true;

        public a(SignatureValidator signaturevalidator) {
            this.validator = signaturevalidator;
        }

        private boolean validateChain(PlayerChatMessage playerchatmessage) {
            return playerchatmessage.equals(this.lastMessage) ? true : this.lastMessage == null || playerchatmessage.link().isDescendantOf(this.lastMessage.link());
        }

        @Override
        public boolean updateAndValidate(PlayerChatMessage playerchatmessage) {
            this.isChainValid = this.isChainValid && playerchatmessage.verify(this.validator) && this.validateChain(playerchatmessage);
            if (!this.isChainValid) {
                return false;
            } else {
                this.lastMessage = playerchatmessage;
                return true;
            }
        }
    }
}
