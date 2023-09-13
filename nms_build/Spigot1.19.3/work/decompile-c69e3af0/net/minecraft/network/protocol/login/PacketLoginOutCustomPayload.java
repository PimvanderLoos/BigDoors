package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketLoginOutCustomPayload implements Packet<PacketLoginOutListener> {

    private static final int MAX_PAYLOAD_SIZE = 1048576;
    private final int transactionId;
    private final MinecraftKey identifier;
    private final PacketDataSerializer data;

    public PacketLoginOutCustomPayload(int i, MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        this.transactionId = i;
        this.identifier = minecraftkey;
        this.data = packetdataserializer;
    }

    public PacketLoginOutCustomPayload(PacketDataSerializer packetdataserializer) {
        this.transactionId = packetdataserializer.readVarInt();
        this.identifier = packetdataserializer.readResourceLocation();
        int i = packetdataserializer.readableBytes();

        if (i >= 0 && i <= 1048576) {
            this.data = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.transactionId);
        packetdataserializer.writeResourceLocation(this.identifier);
        packetdataserializer.writeBytes(this.data.copy());
    }

    public void handle(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.handleCustomQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public MinecraftKey getIdentifier() {
        return this.identifier;
    }

    public PacketDataSerializer getData() {
        return this.data;
    }
}
