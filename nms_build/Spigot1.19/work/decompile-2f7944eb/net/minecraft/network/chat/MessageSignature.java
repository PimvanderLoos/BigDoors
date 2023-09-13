package net.minecraft.network.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.SystemUtils;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(UUID sender, Instant timeStamp, MinecraftEncryption.b saltSignature) {

    public static MessageSignature unsigned() {
        return new MessageSignature(SystemUtils.NIL_UUID, Instant.now(), MinecraftEncryption.b.EMPTY);
    }

    public boolean verify(SignatureValidator signaturevalidator, IChatBaseComponent ichatbasecomponent) {
        return this.isValid() ? signaturevalidator.validate((signatureupdater_a) -> {
            updateSignature(signatureupdater_a, ichatbasecomponent, this.sender, this.timeStamp, this.saltSignature.salt());
        }, this.saltSignature.signature()) : false;
    }

    public boolean verify(SignatureValidator signaturevalidator, String s) throws SignatureException {
        return this.verify(signaturevalidator, (IChatBaseComponent) IChatBaseComponent.literal(s));
    }

    public static void updateSignature(SignatureUpdater.a signatureupdater_a, IChatBaseComponent ichatbasecomponent, UUID uuid, Instant instant, long i) throws SignatureException {
        byte[] abyte = new byte[32];
        ByteBuffer bytebuffer = ByteBuffer.wrap(abyte).order(ByteOrder.BIG_ENDIAN);

        bytebuffer.putLong(i);
        bytebuffer.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        bytebuffer.putLong(instant.getEpochSecond());
        signatureupdater_a.update(abyte);
        signatureupdater_a.update(encodeContent(ichatbasecomponent));
    }

    private static byte[] encodeContent(IChatBaseComponent ichatbasecomponent) {
        String s = IChatBaseComponent.ChatSerializer.toStableJson(ichatbasecomponent);

        return s.getBytes(StandardCharsets.UTF_8);
    }

    public boolean isValid() {
        return this.sender != SystemUtils.NIL_UUID && this.saltSignature.isValid();
    }

    public boolean isValid(UUID uuid) {
        return this.isValid() && uuid.equals(this.sender);
    }
}
