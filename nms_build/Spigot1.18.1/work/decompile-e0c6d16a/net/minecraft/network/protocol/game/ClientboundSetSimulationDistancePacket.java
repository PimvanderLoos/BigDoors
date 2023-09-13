package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public record ClientboundSetSimulationDistancePacket(int a) implements Packet<PacketListenerPlayOut> {

    private final int simulationDistance;

    public ClientboundSetSimulationDistancePacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readVarInt());
    }

    public ClientboundSetSimulationDistancePacket(int i) {
        this.simulationDistance = i;
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.simulationDistance);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetSimulationDistance(this);
    }

    public int simulationDistance() {
        return this.simulationDistance;
    }
}
