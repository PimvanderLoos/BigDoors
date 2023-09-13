package net.minecraft.server;

import java.io.IOException;
import java.util.UUID;

public class PacketPlayOutSpawnEntityPainting implements Packet<PacketListenerPlayOut> {

    private int a;
    private UUID b;
    private BlockPosition c;
    private EnumDirection d;
    private int e;

    public PacketPlayOutSpawnEntityPainting() {}

    public PacketPlayOutSpawnEntityPainting(EntityPainting entitypainting) {
        this.a = entitypainting.getId();
        this.b = entitypainting.getUniqueID();
        this.c = entitypainting.getBlockPosition();
        this.d = entitypainting.direction;
        this.e = IRegistry.MOTIVE.a((Object) entitypainting.art);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = packetdataserializer.i();
        this.e = packetdataserializer.g();
        this.c = packetdataserializer.e();
        this.d = EnumDirection.fromType2(packetdataserializer.readUnsignedByte());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.d(this.e);
        packetdataserializer.a(this.c);
        packetdataserializer.writeByte(this.d.get2DRotationValue());
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
