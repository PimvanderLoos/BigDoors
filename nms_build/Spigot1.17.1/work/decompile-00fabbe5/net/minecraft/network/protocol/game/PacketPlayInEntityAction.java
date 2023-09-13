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
        this.id = packetdataserializer.j();
        this.action = (PacketPlayInEntityAction.EnumPlayerAction) packetdataserializer.a(PacketPlayInEntityAction.EnumPlayerAction.class);
        this.data = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.a((Enum) this.action);
        packetdataserializer.d(this.data);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.id;
    }

    public PacketPlayInEntityAction.EnumPlayerAction c() {
        return this.action;
    }

    public int d() {
        return this.data;
    }

    public static enum EnumPlayerAction {

        PRESS_SHIFT_KEY, RELEASE_SHIFT_KEY, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, START_RIDING_JUMP, STOP_RIDING_JUMP, OPEN_INVENTORY, START_FALL_FLYING;

        private EnumPlayerAction() {}
    }
}
