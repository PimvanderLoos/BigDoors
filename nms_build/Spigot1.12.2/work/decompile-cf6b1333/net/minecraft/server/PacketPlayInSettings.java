package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInSettings implements Packet<PacketListenerPlayIn> {

    private String a;
    private int b;
    private EntityHuman.EnumChatVisibility c;
    private boolean d;
    private int e;
    private EnumMainHand f;

    public PacketPlayInSettings() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e(16);
        this.b = packetdataserializer.readByte();
        this.c = (EntityHuman.EnumChatVisibility) packetdataserializer.a(EntityHuman.EnumChatVisibility.class);
        this.d = packetdataserializer.readBoolean();
        this.e = packetdataserializer.readUnsignedByte();
        this.f = (EnumMainHand) packetdataserializer.a(EnumMainHand.class);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.writeByte(this.b);
        packetdataserializer.a((Enum) this.c);
        packetdataserializer.writeBoolean(this.d);
        packetdataserializer.writeByte(this.e);
        packetdataserializer.a((Enum) this.f);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public String a() {
        return this.a;
    }

    public EntityHuman.EnumChatVisibility c() {
        return this.c;
    }

    public boolean d() {
        return this.d;
    }

    public int e() {
        return this.e;
    }

    public EnumMainHand getMainHand() {
        return this.f;
    }
}
