package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInClientCommand implements Packet<PacketListenerPlayIn> {

    private final PacketPlayInClientCommand.EnumClientCommand action;

    public PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand) {
        this.action = packetplayinclientcommand_enumclientcommand;
    }

    public PacketPlayInClientCommand(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInClientCommand.EnumClientCommand) packetdataserializer.a(PacketPlayInClientCommand.EnumClientCommand.class);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.action);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInClientCommand.EnumClientCommand b() {
        return this.action;
    }

    public static enum EnumClientCommand {

        PERFORM_RESPAWN, REQUEST_STATS;

        private EnumClientCommand() {}
    }
}
