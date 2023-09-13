package net.minecraft.network.chat;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Signer;

public class SignedMessageChain {

    @Nullable
    private MessageSignature previousSignature;

    public SignedMessageChain() {}

    private SignedMessageChain.c pack(Signer signer, MessageSigner messagesigner, ChatMessageContent chatmessagecontent, LastSeenMessages lastseenmessages) {
        MessageSignature messagesignature = pack(signer, messagesigner, this.previousSignature, chatmessagecontent, lastseenmessages);

        this.previousSignature = messagesignature;
        return new SignedMessageChain.c(messagesignature);
    }

    private static MessageSignature pack(Signer signer, MessageSigner messagesigner, @Nullable MessageSignature messagesignature, ChatMessageContent chatmessagecontent, LastSeenMessages lastseenmessages) {
        SignedMessageHeader signedmessageheader = new SignedMessageHeader(messagesignature, messagesigner.profileId());
        SignedMessageBody signedmessagebody = new SignedMessageBody(chatmessagecontent, messagesigner.timeStamp(), messagesigner.salt(), lastseenmessages);
        byte[] abyte = signedmessagebody.hash().asBytes();

        return new MessageSignature(signer.sign((signatureupdater_a) -> {
            signedmessageheader.updateSignature(signatureupdater_a, abyte);
        }));
    }

    private PlayerChatMessage unpack(SignedMessageChain.c signedmessagechain_c, MessageSigner messagesigner, ChatMessageContent chatmessagecontent, LastSeenMessages lastseenmessages) {
        PlayerChatMessage playerchatmessage = unpack(signedmessagechain_c, this.previousSignature, messagesigner, chatmessagecontent, lastseenmessages);

        this.previousSignature = signedmessagechain_c.signature;
        return playerchatmessage;
    }

    private static PlayerChatMessage unpack(SignedMessageChain.c signedmessagechain_c, @Nullable MessageSignature messagesignature, MessageSigner messagesigner, ChatMessageContent chatmessagecontent, LastSeenMessages lastseenmessages) {
        SignedMessageHeader signedmessageheader = new SignedMessageHeader(messagesignature, messagesigner.profileId());
        SignedMessageBody signedmessagebody = new SignedMessageBody(chatmessagecontent, messagesigner.timeStamp(), messagesigner.salt(), lastseenmessages);

        return new PlayerChatMessage(signedmessageheader, signedmessagechain_c.signature, signedmessagebody, Optional.empty(), FilterMask.PASS_THROUGH);
    }

    public SignedMessageChain.a decoder() {
        return this::unpack;
    }

    public SignedMessageChain.b encoder() {
        return this::pack;
    }

    public static record c(MessageSignature signature) {

    }

    @FunctionalInterface
    public interface a {

        SignedMessageChain.a UNSIGNED = (signedmessagechain_c, messagesigner, chatmessagecontent, lastseenmessages) -> {
            return PlayerChatMessage.unsigned(messagesigner, chatmessagecontent);
        };

        PlayerChatMessage unpack(SignedMessageChain.c signedmessagechain_c, MessageSigner messagesigner, ChatMessageContent chatmessagecontent, LastSeenMessages lastseenmessages);
    }

    @FunctionalInterface
    public interface b {

        SignedMessageChain.c pack(Signer signer, MessageSigner messagesigner, ChatMessageContent chatmessagecontent, LastSeenMessages lastseenmessages);
    }
}
