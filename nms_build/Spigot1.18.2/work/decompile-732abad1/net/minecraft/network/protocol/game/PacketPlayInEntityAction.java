package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayInEntityAction implements Packet<PacketListenerPlayIn> {

    private final int id;
    private final PacketPlayInEntityAction.EnumPlayerAction action;
    private final int data;

    public PacketPlayInEntityAction(Entity entity, PacketPlayInEntityAction.EnumPlayerAction packetplayinentityaction_enumplayeraction) {
        this(entity, packetplayinentityaction_enumplayeraction, 0);
    }

    public PacketPlayInEntityAction(Entity entity, PacketPlayInEntityAction.EnumPlayerAction packetplayinentityaction_enumplayeraction, int i) {
        this.id = entity.getId();
        this.action = packetplayinentityaction_enumplayeraction;
        this.data = i;
    }

    public PacketPlayInEntityAction(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.action = (PacketPlayInEntityAction.EnumPlayerAction) packetdataserializer.readEnum(PacketPlayInEntityAction.EnumPlayerAction.class);
        this.data = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeEnum(this.action);
        packetdataserializer.writeVarInt(this.data);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handlePlayerCommand(this);
    }

    public int getId() {
        return this.id;
    }

    public PacketPlayInEntityAction.EnumPlayerAction getAction() {
        return this.action;
    }

    public int getData() {
        return this.data;
    }

    public static enum EnumPlayerAction {

        PRESS_SHIFT_KEY, RELEASE_SHIFT_KEY, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, START_RIDING_JUMP, STOP_RIDING_JUMP, OPEN_INVENTORY, START_FALL_FLYING;

        private EnumPlayerAction() {}
    }
}
