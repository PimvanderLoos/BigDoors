package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class SignedMessageChain {

    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private SignedMessageLink nextLink;

    public SignedMessageChain(UUID uuid, UUID uuid1) {
        this.nextLink = SignedMessageLink.root(uuid, uuid1);
    }

    public SignedMessageChain.c encoder(Signer signer) {
        return (signedmessagebody) -> {
            SignedMessageLink signedmessagelink = this.advanceLink();

            return signedmessagelink == null ? null : new MessageSignature(signer.sign((signatureupdater_a) -> {
                PlayerChatMessage.updateSignature(signatureupdater_a, signedmessagelink, signedmessagebody);
            }));
        };
    }

    public SignedMessageChain.b decoder(ProfilePublicKey profilepublickey) {
        SignatureValidator signaturevalidator = profilepublickey.createSignatureValidator();

        return (messagesignature, signedmessagebody) -> {
            SignedMessageLink signedmessagelink = this.advanceLink();

            if (signedmessagelink == null) {
                throw new SignedMessageChain.a(IChatBaseComponent.translatable("chat.disabled.chain_broken"), false);
            } else if (profilepublickey.data().hasExpired()) {
                throw new SignedMessageChain.a(IChatBaseComponent.translatable("chat.disabled.expiredProfileKey"), false);
            } else {
                PlayerChatMessage playerchatmessage = new PlayerChatMessage(signedmessagelink, messagesignature, signedmessagebody, (IChatBaseComponent) null, FilterMask.PASS_THROUGH);

                if (!playerchatmessage.verify(signaturevalidator)) {
                    throw new SignedMessageChain.a(IChatBaseComponent.translatable("multiplayer.disconnect.unsigned_chat"), true);
                } else {
                    if (playerchatmessage.hasExpiredServer(Instant.now())) {
                        SignedMessageChain.LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", signedmessagebody.content());
                    }

                    return playerchatmessage;
                }
            }
        };
    }

    @Nullable
    private SignedMessageLink advanceLink() {
        SignedMessageLink signedmessagelink = this.nextLink;

        if (signedmessagelink != null) {
            this.nextLink = signedmessagelink.advance();
        }

        return signedmessagelink;
    }

    @FunctionalInterface
    public interface c {

        SignedMessageChain.c UNSIGNED = (signedmessagebody) -> {
            return null;
        };

        @Nullable
        MessageSignature pack(SignedMessageBody signedmessagebody);
    }

    @FunctionalInterface
    public interface b {

        SignedMessageChain.b REJECT_ALL = (messagesignature, signedmessagebody) -> {
            throw new SignedMessageChain.a(IChatBaseComponent.translatable("chat.disabled.missingProfileKey"), false);
        };

        static SignedMessageChain.b unsigned(UUID uuid) {
            return (messagesignature, signedmessagebody) -> {
                return PlayerChatMessage.unsigned(uuid, signedmessagebody.content());
            };
        }

        PlayerChatMessage unpack(@Nullable MessageSignature messagesignature, SignedMessageBody signedmessagebody) throws SignedMessageChain.a;
    }

    public static class a extends ThrowingComponent {

        private final boolean shouldDisconnect;

        public a(IChatBaseComponent ichatbasecomponent, boolean flag) {
            super(ichatbasecomponent);
            this.shouldDisconnect = flag;
        }

        public boolean shouldDisconnect() {
            return this.shouldDisconnect;
        }
    }
}
