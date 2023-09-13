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
        this.xRot = MathHelper.floor(f * 256.0F / 360.0F);
        this.yRot = MathHelper.floor(f1 * 256.0F / 360.0F);
        this.type = entitytypes;
        this.data = j;
        this.xa = (int) (MathHelper.clamp(vec3d.x, -3.9D, 3.9D) * 8000.0D);
        this.ya = (int) (MathHelper.clamp(vec3d.y, -3.9D, 3.9D) * 8000.0D);
        this.za = (int) (MathHelper.clamp(vec3d.z, -3.9D, 3.9D) * 8000.0D);
    }

    public PacketPlayOutSpawnEntity(Entity entity) {
        this(entity, 0);
    }

    public PacketPlayOutSpawnEntity(Entity entity, int i) {
        this(entity.getId(), entity.getUUID(), entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot(), entity.getType(), i, entity.getDeltaMovement());
    }

    public PacketPlayOutSpawnEntity(Entity entity, EntityTypes<?> entitytypes, int i, BlockPosition blockposition) {
        this(entity.getId(), entity.getUUID(), (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), entity.getXRot(), entity.getYRot(), entitytypes, i, entity.getDeltaMovement());
    }

    public PacketPlayOutSpawnEntity(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.uuid = packetdataserializer.readUUID();
        this.type = (EntityTypes) IRegistry.ENTITY_TYPE.byId(packetdataserializer.readVarInt());
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
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeUUID(this.uuid);
        packetdataserializer.writeVarInt(IRegistry.ENTITY_TYPE.getId(this.type));
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

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAddEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public UUID getUUID() {
        return this.uuid;
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

    public int getxRot() {
        return this.xRot;
    }

    public int getyRot() {
        return this.yRot;
    }

    public EntityTypes<?> getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }
}
