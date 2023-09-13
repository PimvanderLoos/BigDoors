package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTitlesAnimationPacket implements Packet<PacketListenerPlayOut> {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public ClientboundSetTitlesAnimationPacket(int i, int j, int k) {
        this.fadeIn = i;
        this.stay = j;
        this.fadeOut = k;
    }

    public ClientboundSetTitlesAnimationPacket(PacketDataSerializer packetdataserializer) {
        this.fadeIn = packetdataserializer.readInt();
        this.stay = packetdataserializer.readInt();
        this.fadeOut = packetdataserializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.fadeIn);
        packetdataserializer.writeInt(this.stay);
        packetdataserializer.writeInt(this.fadeOut);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.setTitlesAnimation(this);
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }
}
