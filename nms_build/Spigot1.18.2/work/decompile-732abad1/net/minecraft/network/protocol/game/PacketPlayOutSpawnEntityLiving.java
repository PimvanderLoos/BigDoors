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
        this.uuid = entityliving.getUUID();
        this.type = IRegistry.ENTITY_TYPE.getId(entityliving.getType());
        this.x = entityliving.getX();
        this.y = entityliving.getY();
        this.z = entityliving.getZ();
        this.yRot = (byte) ((int) (entityliving.getYRot() * 256.0F / 360.0F));
        this.xRot = (byte) ((int) (entityliving.getXRot() * 256.0F / 360.0F));
        this.yHeadRot = (byte) ((int) (entityliving.yHeadRot * 256.0F / 360.0F));
        double d0 = 3.9D;
        Vec3D vec3d = entityliving.getDeltaMovement();
        double d1 = MathHelper.clamp(vec3d.x, -3.9D, 3.9D);
        double d2 = MathHelper.clamp(vec3d.y, -3.9D, 3.9D);
        double d3 = MathHelper.clamp(vec3d.z, -3.9D, 3.9D);

        this.xd = (int) (d1 * 8000.0D);
        this.yd = (int) (d2 * 8000.0D);
        this.zd = (int) (d3 * 8000.0D);
    }

    public PacketPlayOutSpawnEntityLiving(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.uuid = packetdataserializer.readUUID();
        this.type = packetdataserializer.readVarInt();
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
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeUUID(this.uuid);
        packetdataserializer.writeVarInt(this.type);
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

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAddMob(this);
    }

    public int getId() {
        return this.id;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public int getType() {
        return this.type;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getXd() {
        return this.xd;
    }

    public int getYd() {
        return this.yd;
    }

    public int getZd() {
        return this.zd;
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }

    public byte getyHeadRot() {
        return this.yHeadRot;
    }
}
