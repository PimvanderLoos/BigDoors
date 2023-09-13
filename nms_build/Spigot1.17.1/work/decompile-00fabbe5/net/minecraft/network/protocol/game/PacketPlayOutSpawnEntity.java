package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayOutSpawnEntity implements Packet<PacketListenerPlayOut> {

    public static final double MAGICAL_QUANTIZATION = 8000.0D;
    private final int id;
    private final UUID uuid;
    private final double x;
    private final double y;
    private final double z;
    private final int xa;
    private final int ya;
    private final int za;
    private final int xRot;
    private final int yRot;
    private final EntityTypes<?> type;
    private final int data;
    public static final double LIMIT = 3.9D;

    public PacketPlayOutSpawnEntity(int i, UUID uuid, double d0, double d1, double d2, float f, float f1, EntityTypes<?> entitytypes, int j, Vec3D vec3d) {
        this.id = i;
        this.uuid = uuid;
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.xRot = MathHelper.d(f * 256.0F / 360.0F);
        this.yRot = MathHelper.d(f1 * 256.0F / 360.0F);
        this.type = entitytypes;
        this.data = j;
        this.xa = (int) (MathHelper.a(vec3d.x, -3.9D, 3.9D) * 8000.0D);
        this.ya = (int) (MathHelper.a(vec3d.y, -3.9D, 3.9D) * 8000.0D);
        this.za = (int) (MathHelper.a(vec3d.z, -3.9D, 3.9D) * 8000.0D);
    }

    public PacketPlayOutSpawnEntity(Entity entity) {
        this(entity, 0);
    }

    public PacketPlayOutSpawnEntity(Entity entity, int i) {
        this(entity.getId(), entity.getUniqueID(), entity.locX(), entity.locY(), entity.locZ(), entity.getXRot(), entity.getYRot(), entity.getEntityType(), i, entity.getMot());
    }

    public PacketPlayOutSpawnEntity(Entity entity, EntityTypes<?> entitytypes, int i, BlockPosition blockposition) {
        this(entity.getId(), entity.getUniqueID(), (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), entity.getXRot(), entity.getYRot(), entitytypes, i, entity.getMot());
    }

    public PacketPlayOutSpawnEntity(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.uuid = packetdataserializer.l();
        this.type = (EntityTypes) IRegistry.ENTITY_TYPE.fromId(packetdataserializer.j());
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.xRot = packetdataserializer.readByte();
        this.yRot = packetdataserializer.readByte();
        this.data = packetdataserializer.readInt();
        this.xa = packetdataserializer.readShort();
        this.ya = packetdataserializer.readShort();
        this.za = packetdataserializer.readShort();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.a(this.uuid);
        packetdataserializer.d(IRegistry.ENTITY_TYPE.getId(this.type));
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeByte(this.xRot);
        packetdataserializer.writeByte(this.yRot);
        packetdataserializer.writeInt(this.data);
        packetdataserializer.writeShort(this.xa);
        packetdataserializer.writeShort(this.ya);
        packetdataserializer.writeShort(this.za);
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

    public double d() {
        return this.x;
    }

    public double e() {
        return this.y;
    }

    public double f() {
        return this.z;
    }

    public double g() {
        return (double) this.xa / 8000.0D;
    }

    public double h() {
        return (double) this.ya / 8000.0D;
    }

    public double i() {
        return (double) this.za / 8000.0D;
    }

    public int j() {
        return this.xRot;
    }

    public int k() {
        return this.yRot;
    }

    public EntityTypes<?> l() {
        return this.type;
    }

    public int m() {
        return this.data;
    }
}
