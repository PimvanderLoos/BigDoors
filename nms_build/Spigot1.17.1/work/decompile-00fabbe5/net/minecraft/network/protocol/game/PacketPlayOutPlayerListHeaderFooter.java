package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutPlayerListHeaderFooter implements Packet<PacketListenerPlayOut> {

    public final IChatBaseComponent header;
    public final IChatBaseComponent footer;

    public PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1) {
        this.header = ichatbasecomponent;
        this.footer = ichatbasecomponent1;
    }

    public PacketPlayOutPlayerListHeaderFooter(PacketDataSerializer packetdataserializer) {
        this.header = packetdataserializer.i();
        this.footer = packetdataserializer.i();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.header);
        packetdataserializer.a(this.footer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public IChatBaseComponent b() {
        return this.header;
    }

    public IChatBaseComponent c() {
        return this.footer;
    }
}
