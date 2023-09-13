package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;

public class ClientboundAddVibrationSignalPacket implements Packet<PacketListenerPlayOut> {

    private final VibrationPath vibrationPath;

    public ClientboundAddVibrationSignalPacket(VibrationPath vibrationpath) {
        this.vibrationPath = vibrationpath;
    }

    public ClientboundAddVibrationSignalPacket(PacketDataSerializer packetdataserializer) {
        this.vibrationPath = VibrationPath.read(packetdataserializer);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        VibrationPath.write(packetdataserializer, this.vibrationPath);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAddVibrationSignal(this);
    }

    public VibrationPath getVibrationPath() {
        return this.vibrationPath;
    }
}
