package net.minecraft.network.chat;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record PlayerChatMessage(SignedMessageHeader signedHeader, MessageSignature headerSignature, SignedMessageBody signedBody, Optional<IChatBaseComponent> unsignedContent, FilterMask filterMask) {

    public static final Duration MESSAGE_EXPIRES_AFTER_SERVER = Duration.ofMinutes(5L);
    public static final Duration MESSAGE_EXPIRES_AFTER_CLIENT = PlayerChatMessage.MESSAGE_EXPIRES_AFTER_SERVER.plus(Duration.ofMinutes(2L));

    public PlayerChatMessage(PacketDataSerializer packetdataserializer) {
        this(new SignedMessageHeader(packetdataserializer), new MessageSignature(packetdataserializer), new SignedMessageBody(packetdataserializer), packetdataserializer.readOptional(PacketDataSerializer::readComponent), FilterMask.read(packetdataserializer));
    }

    public static PlayerChatMessage system(ChatMessageContent chatmessagecontent) {
        return unsigned(MessageSigner.system(), chatmessagecontent);
    }

    public static PlayerChatMessage unsigned(MessageSigner messagesigner, ChatMessageContent chatmessagecontent) {
        SignedMessageBody signedmessagebody = new SignedMessageBody(chatmessagecontent, messagesigner.timeStamp(), messagesigner.salt(), LastSeenMessages.EMPTY);
        SignedMessageHeader signedmessageheader = new SignedMessageHeader((MessageSignature) null, messagesigner.profileId());

        return new PlayerChatMessage(signedmessageheader, MessageSignature.EMPTY, signedmessagebody, Optional.empty(), FilterMask.PASS_THROUGH);
    }

    public void write(PacketDataSerializer packetdataserializer) {
        this.signedHeader.write(packetdataserializer);
        this.headerSignature.write(packetdataserializer);
        this.signedBody.write(packetdataserializer);
        packetdataserializer.writeOptional(this.unsignedContent, PacketDataSerializer::writeComponent);
        FilterMask.write(packetdataserializer, this.filterMask);
    }

    public PlayerChatMessage withUnsignedContent(IChatBaseComponent ichatbasecomponent) {
        Optional<IChatBaseComponent> optional = !this.signedContent().decorated().equals(ichatbasecomponent) ? Optional.of(ichatbasecomponent) : Optional.empty();

        return new PlayerChatMessage(this.signedHeader, this.headerSignature, this.signedBody, optional, this.filterMask);
    }

    public PlayerChatMessage removeUnsignedContent() {
        return this.unsignedContent.isPresent() ? new PlayerChatMessage(this.signedHeader, this.headerSignature, this.signedBody, Optional.empty(), this.filterMask) : this;
    }

    public PlayerChatMessage filter(FilterMask filtermask) {
        return this.filterMask.equals(filtermask) ? this : new PlayerChatMessage(this.signedHeader, this.headerSignature, this.signedBody, this.unsignedContent, filtermask);
    }

    public PlayerChatMessage filter(boolean flag) {
        return this.filter(flag ? this.filterMask : FilterMask.PASS_THROUGH);
    }

    public boolean verify(SignatureValidator signaturevalidator) {
        return this.headerSignature.verify(signaturevalidator, this.signedHeader, this.signedBody);
    }

    public boolean verify(ProfilePublicKey profilepublickey) {
        SignatureValidator signaturevalidator = profilepublickey.createSignatureValidator();

        return this.verify(signaturevalidator);
    }

    public boolean verify(ChatSender chatsender) {
        ProfilePublicKey profilepublickey = chatsender.profilePublicKey();

        return profilepublickey != null && this.verify(profilepublickey);
    }

    public ChatMessageContent signedContent() {
        return this.signedBody.content();
    }

    public IChatBaseComponent serverContent() {
        return (IChatBaseComponent) this.unsignedContent().orElse(this.signedContent().decorated());
    }

    public Instant timeStamp() {
        return this.signedBody.timeStamp();
    }

    public long salt() {
        return this.signedBody.salt();
    }

    public boolean hasExpiredServer(Instant instant) {
        return instant.isAfter(this.timeStamp().plus(PlayerChatMessage.MESSAGE_EXPIRES_AFTER_SERVER));
    }

    public boolean hasExpiredClient(Instant instant) {
        return instant.isAfter(this.timeStamp().plus(PlayerChatMessage.MESSAGE_EXPIRES_AFTER_CLIENT));
    }

    public MessageSigner signer() {
        return new MessageSigner(this.signedHeader.sender(), this.timeStamp(), this.salt());
    }

    @Nullable
    public LastSeenMessages.a toLastSeenEntry() {
        MessageSigner messagesigner = this.signer();

        return !this.headerSignature.isEmpty() && !messagesigner.isSystem() ? new LastSeenMessages.a(messagesigner.profileId(), this.headerSignature) : null;
    }

    public boolean hasSignatureFrom(UUID uuid) {
        return !this.headerSignature.isEmpty() && this.signedHeader.sender().equals(uuid);
    }

    public boolean isFullyFiltered() {
        return this.filterMask.isFullyFiltered();
    }
}
