package net.minecraft.network.chat;

import java.security.SignatureException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.SignatureUpdater;

public record SignedMessageHeader(@Nullable MessageSignature previousSignature, UUID sender) {

    public SignedMessageHeader(PacketDataSerializer packetdataserializer) {
        this((MessageSignature) packetdataserializer.readNullable(MessageSignature::new), packetdataserializer.readUUID());
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeNullable(this.previousSignature, (packetdataserializer1, messagesignature) -> {
            messagesignature.write(packetdataserializer1);
        });
        packetdataserializer.writeUUID(this.sender);
    }

    public void updateSignature(SignatureUpdater.a signatureupdater_a, byte[] abyte) throws SignatureException {
        if (this.previousSignature != null) {
            signatureupdater_a.update(this.previousSignature.bytes());
        }

        signatureupdater_a.update(UUIDUtil.uuidToByteArray(this.sender));
        signatureupdater_a.update(abyte);
    }
}
