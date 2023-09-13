package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(byte[] bytes) {

    public static final MessageSignature EMPTY = new MessageSignature(ByteArrays.EMPTY_ARRAY);

    public MessageSignature(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readByteArray());
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByteArray(this.bytes);
    }

    public boolean verify(SignatureValidator signaturevalidator, SignedMessageHeader signedmessageheader, SignedMessageBody signedmessagebody) {
        if (!this.isEmpty()) {
            byte[] abyte = signedmessagebody.hash().asBytes();

            return signaturevalidator.validate((signatureupdater_a) -> {
                signedmessageheader.updateSignature(signatureupdater_a, abyte);
            }, this.bytes);
        } else {
            return false;
        }
    }

    public boolean verify(SignatureValidator signaturevalidator, SignedMessageHeader signedmessageheader, byte[] abyte) {
        return !this.isEmpty() ? signaturevalidator.validate((signatureupdater_a) -> {
            signedmessageheader.updateSignature(signatureupdater_a, abyte);
        }, this.bytes) : false;
    }

    public boolean isEmpty() {
        return this.bytes.length == 0;
    }

    @Nullable
    public ByteBuffer asByteBuffer() {
        return !this.isEmpty() ? ByteBuffer.wrap(this.bytes) : null;
    }

    public boolean equals(Object object) {
        boolean flag;

        if (this != object) {
            label22:
            {
                if (object instanceof MessageSignature) {
                    MessageSignature messagesignature = (MessageSignature) object;

                    if (Arrays.equals(this.bytes, messagesignature.bytes)) {
                        break label22;
                    }
                }

                flag = false;
                return flag;
            }
        }

        flag = true;
        return flag;
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    public String toString() {
        return !this.isEmpty() ? Base64.getEncoder().encodeToString(this.bytes) : "empty";
    }
}
