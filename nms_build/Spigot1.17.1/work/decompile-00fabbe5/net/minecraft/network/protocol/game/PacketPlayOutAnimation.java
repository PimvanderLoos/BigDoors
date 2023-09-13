package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayOutAnimation implements Packet<PacketListenerPlayOut> {

    public static final int SWING_MAIN_HAND = 0;
    public static final int HURT = 1;
    public static final int WAKE_UP = 2;
    public static final int SWING_OFF_HAND = 3;
    public static final int CRITICAL_HIT = 4;
    public static final int MAGIC_CRITICAL_HIT = 5;
    private final int id;
    private final int action;

    public PacketPlayOutAnimation(Entity entity, int i) {
        this.id = entity.getId();
        this.action = i;
    }

    public PacketPlayOutAnimation(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.action = packetdataserializer.readUnsignedByte();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.writeByte(this.action);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.id;
    }

    public int c() {
        return this.action;
    }
}
