package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayOutSpawnEntityLiving implements Packet<PacketListenerPlayOut> {

    private int a;
    private UUID b;
    private int c;
    private double d;
    private double e;
    private double f;
    private int g;
    private int h;
    private int i;
    private byte j;
    private byte k;
    private byte l;

    public PacketPlayOutSpawnEntityLiving() {}

    public PacketPlayOutSpawnEntityLiving(EntityLiving entityliving) {
        this.a = entityliving.getId();
        this.b = entityliving.getUniqueID();
        this.c = IRegistry.ENTITY_TYPE.a((Object) entityliving.getEntityType());
        this.d = entityliving.locX();
        this.e = entityliving.locY();
        this.f = entityliving.locZ();
        this.j = (byte) ((int) (entityliving.yaw * 256.0F / 360.0F));
        this.k = (byte) ((int) (entityliving.pitch * 256.0F / 360.0F));
        this.l = (byte) ((int) (entityliving.aC * 256.0F / 360.0F));
        double d0 = 3.9D;
        Vec3D vec3d = entityliving.getMot();
        double d1 = MathHelper.a(vec3d.x, -3.9D, 3.9D);
        double d2 = MathHelper.a(vec3d.y, -3.9D, 3.9D);
        double d3 = MathHelper.a(vec3d.z, -3.9D, 3.9D);

        this.g = (int) (d1 * 8000.0D);
        this.h = (int) (d2 * 8000.0D);
        this.i = (int) (d3 * 8000.0D);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.i();
        this.b = packetdataserializer.k();
        this.c = packetdataserializer.i();
        this.d = packetdataserializer.readDouble();
        this.e = packetdataserializer.readDouble();
        this.f = packetdataserializer.readDouble();
        this.j = packetdataserializer.readByte();
        this.k = packetdataserializer.readByte();
        this.l = packetdataserializer.readByte();
        this.g = packetdataserializer.readShort();
        this.h = packetdataserializer.readShort();
        this.i = packetdataserializer.readShort();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.d(this.c);
        packetdataserializer.writeDouble(this.d);
        packetdataserializer.writeDouble(this.e);
        packetdataserializer.writeDouble(this.f);
        packetdataserializer.writeByte(this.j);
        packetdataserializer.writeByte(this.k);
        packetdataserializer.writeByte(this.l);
        packetdataserializer.writeShort(this.g);
        packetdataserializer.writeShort(this.h);
        packetdataserializer.writeShort(this.i);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
