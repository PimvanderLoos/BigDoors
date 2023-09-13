package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumHand;

public class PacketPlayInBlockPlace implements Packet<PacketListenerPlayIn> {

    private EnumHand a;

    public PacketPlayInBlockPlace() {}

    public PacketPlayInBlockPlace(EnumHand enumhand) {
        this.a = enumhand;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (EnumHand) packetdataserializer.a(EnumHand.class);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public EnumHand b() {
        return this.a;
    }
}
