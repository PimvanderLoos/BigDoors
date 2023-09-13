package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayOutSpawnEntityLiving implements Packet<PacketListenerPlayOut> {

    private final int id;
    private final UUID uuid;
    private final int type;
    private final double x;
    private final double y;
    private final double z;
    private final int xd;
    private final int yd;
    private final int zd;
    private final byte yRot;
    private final byte xRot;
    private final byte yHeadRot;

    public PacketPlayOutSpawnEntityLiving(EntityLiving entityliving) {
        this.id = entityliving.getId();
        this.uuid = entityliving.getUniqueID();
        this.type = IRegistry.ENTITY_TYPE.getId(entityliving.getEntityType());
        this.x = entityliving.locX();
        this.y = entityliving.locY();
        this.z = entityliving.locZ();
        this.yRot = (byte) ((int) (entityliving.getYRot() * 256.0F / 360.0F));
        this.xRot = (byte) ((int) (entityliving.getXRot() * 256.0F / 360.0F));
        this.yHeadRot = (byte) ((int) (entityliving.yHeadRot * 256.0F / 360.0F));
        double d0 = 3.9D;
        Vec3D vec3d = entityliving.getMot();
        double d1 = MathHelper.a(vec3d.x, -3.9D, 3.9D);
        double d2 = MathHelper.a(vec3d.y, -3.9D, 3.9D);
        double d3 = MathHelper.a(vec3d.z, -3.9D, 3.9D);

        this.xd = (int) (d1 * 8000.0D);
        this.yd = (int) (d2 * 8000.0D);
        this.zd = (int) (d3 * 8000.0D);
    }

    public PacketPlayOutSpawnEntityLiving(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.uuid = packetdataserializer.l();
        this.type = packetdataserializer.j();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readByte();
        this.xRot = packetdataserializer.readByte();
        this.yHeadRot = packetdataserializer.readByte();
        this.xd = packetdataserializer.readShort();
        this.yd = packetdataserializer.readShort();
        this.zd = packetdataserializer.readShort();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.a(this.uuid);
        packetdataserializer.d(this.type);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeByte(this.yRot);
        packetdataserializer.writeByte(this.xRot);
        packetdataserializer.writeByte(this.yHeadRot);
        packetdataserializer.writeShort(this.xd);
        packetdataserializer.writeShort(this.yd);
        packetdataserializer.writeShort(this.zd);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.id;
    }

    public UUID c() {
        return this.uuid;
    }

    public int d() {
        return this.type;
    }

    public double e() {
        return this.x;
    }

    public double f() {
        return this.y;
    }

    public double g() {
        return this.z;
    }

    public int h() {
        return this.xd;
    }

    public int i() {
        return this.yd;
    }

    public int j() {
        return this.zd;
    }

    public byte k() {
        return this.yRot;
    }

    public byte l() {
        return this.xRot;
    }

    public byte m() {
        return this.yHeadRot;
    }
}
