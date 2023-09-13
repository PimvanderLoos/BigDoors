package net.minecraft.network.chat;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;

public record LastSeenMessages(List<LastSeenMessages.a> entries) {

    public static LastSeenMessages EMPTY = new LastSeenMessages(List.of());
    public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 5;

    public LastSeenMessages(PacketDataSerializer packetdataserializer) {
        this((List) packetdataserializer.readCollection(PacketDataSerializer.limitValue(ArrayList::new, 5), LastSeenMessages.a::new));
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeCollection(this.entries, (packetdataserializer1, lastseenmessages_a) -> {
            lastseenmessages_a.write(packetdataserializer1);
        });
    }

    public void updateHash(DataOutput dataoutput) throws IOException {
        Iterator iterator = this.entries.iterator();

        while (iterator.hasNext()) {
            LastSeenMessages.a lastseenmessages_a = (LastSeenMessages.a) iterator.next();
            UUID uuid = lastseenmessages_a.profileId();
            MessageSignature messagesignature = lastseenmessages_a.lastSignature();

            dataoutput.writeByte(70);
            dataoutput.writeLong(uuid.getMostSignificantBits());
            dataoutput.writeLong(uuid.getLeastSignificantBits());
            dataoutput.write(messagesignature.bytes());
        }

    }

    public static record a(UUID profileId, MessageSignature lastSignature) {

        public a(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readUUID(), new MessageSignature(packetdataserializer));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeUUID(this.profileId);
            this.lastSignature.write(packetdataserializer);
        }
    }

    public static record b(LastSeenMessages lastSeen, Optional<LastSeenMessages.a> lastReceived) {

        public b(PacketDataSerializer packetdataserializer) {
            this(new LastSeenMessages(packetdataserializer), packetdataserializer.readOptional(LastSeenMessages.a::new));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            this.lastSeen.write(packetdataserializer);
            packetdataserializer.writeOptional(this.lastReceived, (packetdataserializer1, lastseenmessages_a) -> {
                lastseenmessages_a.write(packetdataserializer1);
            });
        }
    }
}
