package net.minecraft.network.chat;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import net.minecraft.network.PacketDataSerializer;

public record SignedMessageBody(ChatMessageContent content, Instant timeStamp, long salt, LastSeenMessages lastSeen) {

    public static final byte HASH_SEPARATOR_BYTE = 70;

    public SignedMessageBody(PacketDataSerializer packetdataserializer) {
        this(ChatMessageContent.read(packetdataserializer), packetdataserializer.readInstant(), packetdataserializer.readLong(), new LastSeenMessages(packetdataserializer));
    }

    public void write(PacketDataSerializer packetdataserializer) {
        ChatMessageContent.write(packetdataserializer, this.content);
        packetdataserializer.writeInstant(this.timeStamp);
        packetdataserializer.writeLong(this.salt);
        this.lastSeen.write(packetdataserializer);
    }

    public HashCode hash() {
        HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha256(), OutputStream.nullOutputStream());

        try {
            DataOutputStream dataoutputstream = new DataOutputStream(hashingoutputstream);

            dataoutputstream.writeLong(this.salt);
            dataoutputstream.writeLong(this.timeStamp.getEpochSecond());
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(dataoutputstream, StandardCharsets.UTF_8);

            outputstreamwriter.write(this.content.plain());
            outputstreamwriter.flush();
            dataoutputstream.write(70);
            if (this.content.isDecorated()) {
                outputstreamwriter.write(IChatBaseComponent.ChatSerializer.toStableJson(this.content.decorated()));
                outputstreamwriter.flush();
            }

            this.lastSeen.updateHash(dataoutputstream);
        } catch (IOException ioexception) {
            ;
        }

        return hashingoutputstream.hash();
    }

    public SignedMessageBody withContent(ChatMessageContent chatmessagecontent) {
        return new SignedMessageBody(chatmessagecontent, this.timeStamp, this.salt, this.lastSeen);
    }
}
