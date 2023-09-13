package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record PlayerChatMessage(SignedMessageLink link, @Nullable MessageSignature signature, SignedMessageBody signedBody, @Nullable IChatBaseComponent unsignedContent, FilterMask filterMask) {

    public static final MapCodec<PlayerChatMessage> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(SignedMessageLink.CODEC.fieldOf("link").forGetter(PlayerChatMessage::link), MessageSignature.CODEC.optionalFieldOf("signature").forGetter((playerchatmessage) -> {
            return Optional.ofNullable(playerchatmessage.signature);
        }), SignedMessageBody.MAP_CODEC.forGetter(PlayerChatMessage::signedBody), ExtraCodecs.COMPONENT.optionalFieldOf("unsigned_content").forGetter((playerchatmessage) -> {
            return Optional.ofNullable(playerchatmessage.unsignedContent);
        }), FilterMask.CODEC.optionalFieldOf("filter_mask", FilterMask.PASS_THROUGH).forGetter(PlayerChatMessage::filterMask)).apply(instance, (signedmessagelink, optional, signedmessagebody, optional1, filtermask) -> {
            return new PlayerChatMessage(signedmessagelink, (MessageSignature) optional.orElse((Object) null), signedmessagebody, (IChatBaseComponent) optional1.orElse((Object) null), filtermask);
        });
    });
    private static final UUID SYSTEM_SENDER = SystemUtils.NIL_UUID;
    public static final Duration MESSAGE_EXPIRES_AFTER_SERVER = Duration.ofMinutes(5L);
    public static final Duration MESSAGE_EXPIRES_AFTER_CLIENT = PlayerChatMessage.MESSAGE_EXPIRES_AFTER_SERVER.plus(Duration.ofMinutes(2L));

    public static PlayerChatMessage system(String s) {
        return unsigned(PlayerChatMessage.SYSTEM_SENDER, s);
    }

    public static PlayerChatMessage unsigned(UUID uuid, String s) {
        SignedMessageBody signedmessagebody = SignedMessageBody.unsigned(s);
        SignedMessageLink signedmessagelink = SignedMessageLink.unsigned(uuid);

        return new PlayerChatMessage(signedmessagelink, (MessageSignature) null, signedmessagebody, (IChatBaseComponent) null, FilterMask.PASS_THROUGH);
    }

    public PlayerChatMessage withUnsignedContent(IChatBaseComponent ichatbasecomponent) {
        IChatBaseComponent ichatbasecomponent1 = !ichatbasecomponent.equals(IChatBaseComponent.literal(this.signedContent())) ? ichatbasecomponent : null;

        return new PlayerChatMessage(this.link, this.signature, this.signedBody, ichatbasecomponent1, this.filterMask);
    }

    public PlayerChatMessage removeUnsignedContent() {
        return this.unsignedContent != null ? new PlayerChatMessage(this.link, this.signature, this.signedBody, (IChatBaseComponent) null, this.filterMask) : this;
    }

    public PlayerChatMessage filter(FilterMask filtermask) {
        return this.filterMask.equals(filtermask) ? this : new PlayerChatMessage(this.link, this.signature, this.signedBody, this.unsignedContent, filtermask);
    }

    public PlayerChatMessage filter(boolean flag) {
        return this.filter(flag ? this.filterMask : FilterMask.PASS_THROUGH);
    }

    public static void updateSignature(SignatureUpdater.a signatureupdater_a, SignedMessageLink signedmessagelink, SignedMessageBody signedmessagebody) throws SignatureException {
        signatureupdater_a.update(Ints.toByteArray(1));
        signedmessagelink.updateSignature(signatureupdater_a);
        signedmessagebody.updateSignature(signatureupdater_a);
    }

    public boolean verify(SignatureValidator signaturevalidator) {
        return this.signature != null && this.signature.verify(signaturevalidator, (signatureupdater_a) -> {
            updateSignature(signatureupdater_a, this.link, this.signedBody);
        });
    }

    public String signedContent() {
        return this.signedBody.content();
    }

    public IChatBaseComponent decoratedContent() {
        return (IChatBaseComponent) Objects.requireNonNullElseGet(this.unsignedContent, () -> {
            return IChatBaseComponent.literal(this.signedContent());
        });
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

    public UUID sender() {
        return this.link.sender();
    }

    public boolean isSystem() {
        return this.sender().equals(PlayerChatMessage.SYSTEM_SENDER);
    }

    public boolean hasSignature() {
        return this.signature != null;
    }

    public boolean hasSignatureFrom(UUID uuid) {
        return this.hasSignature() && this.link.sender().equals(uuid);
    }

    public boolean isFullyFiltered() {
        return this.filterMask.isFullyFiltered();
    }
}
