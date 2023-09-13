package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInClientCommand implements Packet<PacketListenerPlayIn> {

    private final PacketPlayInClientCommand.EnumClientCommand action;

    public PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand) {
        this.action = packetplayinclientcommand_enumclientcommand;
    }

    public PacketPlayInClientCommand(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInClientCommand.EnumClientCommand) packetdataserializer.readEnum(PacketPlayInClientCommand.EnumClientCommand.class);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.action);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleClientCommand(this);
    }

    public PacketPlayInClientCommand.EnumClientCommand getAction() {
        return this.action;
    }

    public static enum EnumClientCommand {

        PERFORM_RESPAWN, REQUEST_STATS;

        private EnumClientCommand() {}
    }
}
