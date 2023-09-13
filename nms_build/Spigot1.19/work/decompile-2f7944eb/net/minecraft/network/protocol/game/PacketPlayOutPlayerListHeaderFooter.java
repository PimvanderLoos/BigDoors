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
        this.header = packetdataserializer.readComponent();
        this.footer = packetdataserializer.readComponent();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.header);
        packetdataserializer.writeComponent(this.footer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleTabListCustomisation(this);
    }

    public IChatBaseComponent getHeader() {
        return this.header;
    }

    public IChatBaseComponent getFooter() {
        return this.footer;
    }
}
