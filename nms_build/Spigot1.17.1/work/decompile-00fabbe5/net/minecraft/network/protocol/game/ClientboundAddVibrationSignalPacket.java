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
        this.vibrationPath = VibrationPath.a(packetdataserializer);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        VibrationPath.a(packetdataserializer, this.vibrationPath);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public VibrationPath b() {
        return this.vibrationPath;
    }
}
