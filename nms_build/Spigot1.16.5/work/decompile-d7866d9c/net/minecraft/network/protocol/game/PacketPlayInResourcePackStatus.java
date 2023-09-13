package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInResourcePackStatus implements Packet<PacketListenerPlayIn> {

    public PacketPlayInResourcePackStatus.EnumResourcePackStatus status;

    public PacketPlayInResourcePackStatus() {}

    public PacketPlayInResourcePackStatus(PacketPlayInResourcePackStatus.EnumResourcePackStatus packetplayinresourcepackstatus_enumresourcepackstatus) {
        this.status = packetplayinresourcepackstatus_enumresourcepackstatus;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.status = (PacketPlayInResourcePackStatus.EnumResourcePackStatus) packetdataserializer.a(PacketPlayInResourcePackStatus.EnumResourcePackStatus.class);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.status);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public static enum EnumResourcePackStatus {

        SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED;

        private EnumResourcePackStatus() {}
    }
}
