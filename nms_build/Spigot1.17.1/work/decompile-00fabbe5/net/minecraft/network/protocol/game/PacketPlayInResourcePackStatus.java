package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInResourcePackStatus implements Packet<PacketListenerPlayIn> {

    public final PacketPlayInResourcePackStatus.EnumResourcePackStatus action;

    public PacketPlayInResourcePackStatus(PacketPlayInResourcePackStatus.EnumResourcePackStatus packetplayinresourcepackstatus_enumresourcepackstatus) {
        this.action = packetplayinresourcepackstatus_enumresourcepackstatus;
    }

    public PacketPlayInResourcePackStatus(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInResourcePackStatus.EnumResourcePackStatus) packetdataserializer.a(PacketPlayInResourcePackStatus.EnumResourcePackStatus.class);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.action);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInResourcePackStatus.EnumResourcePackStatus b() {
        return this.action;
    }

    public static enum EnumResourcePackStatus {

        SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED;

        private EnumResourcePackStatus() {}
    }
}
