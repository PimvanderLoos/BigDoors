package net.minecraft.network.protocol.login;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginInCustomPayload implements Packet<PacketLoginInListener> {

    private static final int MAX_PAYLOAD_SIZE = 1048576;
    private final int transactionId;
    @Nullable
    private final PacketDataSerializer data;

    public PacketLoginInCustomPayload(int i, @Nullable PacketDataSerializer packetdataserializer) {
        this.transactionId = i;
        this.data = packetdataserializer;
    }

    public PacketLoginInCustomPayload(PacketDataSerializer packetdataserializer) {
        this.transactionId = packetdataserializer.readVarInt();
        if (packetdataserializer.readBoolean()) {
            int i = packetdataserializer.readableBytes();

            if (i < 0 || i > 1048576) {
                throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
            }

            this.data = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            this.data = null;
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.transactionId);
        if (this.data != null) {
            packetdataserializer.writeBoolean(true);
            packetdataserializer.writeBytes(this.data.copy());
        } else {
            packetdataserializer.writeBoolean(false);
        }

    }

    public void handle(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.handleCustomQueryPacket(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    public PacketDataSerializer getData() {
        return this.data;
    }
}
