package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInResourcePackStatus implements Packet<PacketListenerPlayIn> {

    public final PacketPlayInResourcePackStatus.EnumResourcePackStatus action;

    public PacketPlayInResourcePackStatus(PacketPlayInResourcePackStatus.EnumResourcePackStatus packetplayinresourcepackstatus_enumresourcepackstatus) {
        this.action = packetplayinresourcepackstatus_enumresourcepackstatus;
    }

    public PacketPlayInResourcePackStatus(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInResourcePackStatus.EnumResourcePackStatus) packetdataserializer.readEnum(PacketPlayInResourcePackStatus.EnumResourcePackStatus.class);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.action);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleResourcePackResponse(this);
    }

    public PacketPlayInResourcePackStatus.EnumResourcePackStatus getAction() {
        return this.action;
    }

    public static enum EnumResourcePackStatus {

        SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED;

        private EnumResourcePackStatus() {}
    }
}
