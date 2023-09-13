package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayOutSpawnEntity implements Packet<PacketListenerPlayOut> {

    private static final double MAGICAL_QUANTIZATION = 8000.0D;
    private static final double LIMIT = 3.9D;
    private final int id;
    private final UUID uuid;
    private final EntityTypes<?> type;
    private final double x;
    private final double y;
    private final double z;
    private final int xa;
    private final int ya;
    private final int za;
    private final byte xRot;
    private final byte yRot;
    private final byte yHeadRot;
    private final int data;

    public PacketPlayOutSpawnEntity(Entity entity) {
        this(entity, 0);
    }

    public PacketPlayOutSpawnEntity(Entity entity, int i) {
        this(entity.getId(), entity.getUUID(), entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot(), entity.getType(), i, entity.getDeltaMovement(), (double) entity.getYHeadRot());
    }

    public PacketPlayOutSpawnEntity(Entity entity, int i, BlockPosition blockposition) {
        this(entity.getId(), entity.getUUID(), (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), entity.getXRot(), entity.getYRot(), entity.getType(), i, entity.getDeltaMovement(), (double) entity.getYHeadRot());
    }

    public PacketPlayOutSpawnEntity(int i, UUID uuid, double d0, double d1, double d2, float f, float f1, EntityTypes<?> entitytypes, int j, Vec3D vec3d, double d3) {
        this.id = i;
        this.uuid = uuid;
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.xRot = (byte) MathHelper.floor(f * 256.0F / 360.0F);
        this.yRot = (byte) MathHelper.floor(f1 * 256.0F / 360.0F);
        this.yHeadRot = (byte) MathHelper.floor(d3 * 256.0D / 360.0D);
        this.type = entitytypes;
        this.data = j;
        this.xa = (int) (MathHelper.clamp(vec3d.x, -3.9D, 3.9D) * 8000.0D);
        this.ya = (int) (MathHelper.clamp(vec3d.y, -3.9D, 3.9D) * 8000.0D);
        this.za = (int) (MathHelper.clamp(vec3d.z, -3.9D, 3.9D) * 8000.0D);
    }

    public PacketPlayOutSpawnEntity(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.uuid = packetdataserializer.readUUID();
        this.type = (EntityTypes) packetdataserializer.readById(BuiltInRegistries.ENTITY_TYPE);
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.xRot = packetdataserializer.readByte();
        this.yRot = packetdataserializer.readByte();
        this.yHeadRot = packetdataserializer.readByte();
        this.data = packetdataserializer.readVarInt();
        this.xa = packetdataserializer.readShort();
        this.ya = packetdataserializer.readShort();
        this.za = packetdataserializer.readShort();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeUUID(this.uuid);
        packetdataserializer.writeId(BuiltInRegistries.ENTITY_TYPE, this.type);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeByte(this.xRot);
        packetdataserializer.writeByte(this.yRot);
        packetdataserializer.writeByte(this.yHeadRot);
        packetdataserializer.writeVarInt(this.data);
        packetdataserializer.writeShort(this.xa);
        packetdataserializer.writeShort(this.ya);
        packetdataserializer.writeShort(this.za);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAddEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public EntityTypes<?> getType() {
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

    public double getXa() {
        return (double) this.xa / 8000.0D;
    }

    public double getYa() {
        return (double) this.ya / 8000.0D;
    }

    public double getZa() {
        return (double) this.za / 8000.0D;
    }

    public float getXRot() {
        return (float) (this.xRot * 360) / 256.0F;
    }

    public float getYRot() {
        return (float) (this.yRot * 360) / 256.0F;
    }

    public float getYHeadRot() {
        return (float) (this.yHeadRot * 360) / 256.0F;
    }

    public int getData() {
        return this.data;
    }
}
