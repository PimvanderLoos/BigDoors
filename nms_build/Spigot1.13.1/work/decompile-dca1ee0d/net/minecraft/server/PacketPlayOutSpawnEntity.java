package net.minecraft.server;

import java.io.IOException;
import java.util.UUID;

public class PacketPlayOutSpawnEntity implements Packet<PacketListenerPlayOut> {

    private int a;
    private UUID b;
    private double c;
    private double d;
    private double e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private int l;

    public PacketPlayOutSpawnEntity() {}

    public PacketPlayOutSpawnEntity(Entity entity, int i) {
        this(entity, i, 0);
    }

    public PacketPlayOutSpawnEntity(Entity entity, int i, int j) {
        this.a = entity.getId();
        this.b = entity.getUniqueID();
        this.c = entity.locX;
        this.d = entity.locY;
        this.e = entity.locZ;
        this.i = MathHelper.d(entity.pitch * 256.0F / 360.0F);
        this.j = MathHelper.d(entity.yaw * 256.0F / 360.0F);
        this.k = i;
        this.l = j;
        double d0 = 3.9D;

        this.f = (int) (MathHelper.a(entity.motX, -3.9D, 3.9D) * 8000.0D);
        this.g = (int) (MathHelper.a(entity.motY, -3.9D, 3.9D) * 8000.0D);
        this.h = (int) (MathHelper.a(entity.motZ, -3.9D, 3.9D) * 8000.0D);
    }

    public PacketPlayOutSpawnEntity(Entity entity, int i, int j, BlockPosition blockposition) {
        this(entity, i, j);
        this.c = (double) blockposition.getX();
        this.d = (double) blockposition.getY();
        this.e = (double) blockposition.getZ();
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = packetdataserializer.i();
        this.k = packetdataserializer.readByte();
        this.c = packetdataserializer.readDouble();
        this.d = packetdataserializer.readDouble();
        this.e = packetdataserializer.readDouble();
        this.i = packetdataserializer.readByte();
        this.j = packetdataserializer.readByte();
        this.l = packetdataserializer.readInt();
        this.f = packetdataserializer.readShort();
        this.g = packetdataserializer.readShort();
        this.h = packetdataserializer.readShort();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.writeByte(this.k);
        packetdataserializer.writeDouble(this.c);
        packetdataserializer.writeDouble(this.d);
        packetdataserializer.writeDouble(this.e);
        packetdataserializer.writeByte(this.i);
        packetdataserializer.writeByte(this.j);
        packetdataserializer.writeInt(this.l);
        packetdataserializer.writeShort(this.f);
        packetdataserializer.writeShort(this.g);
        packetdataserializer.writeShort(this.h);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(int i) {
        this.f = i;
    }

    public void b(int i) {
        this.g = i;
    }

    public void c(int i) {
        this.h = i;
    }
}
