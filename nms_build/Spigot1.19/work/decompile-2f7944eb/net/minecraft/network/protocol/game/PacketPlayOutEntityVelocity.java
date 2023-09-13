package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayOutEntityVelocity implements Packet<PacketListenerPlayOut> {

    private final int id;
    private final int xa;
    private final int ya;
    private final int za;

    public PacketPlayOutEntityVelocity(Entity entity) {
        this(entity.getId(), entity.getDeltaMovement());
    }

    public PacketPlayOutEntityVelocity(int i, Vec3D vec3d) {
        this.id = i;
        double d0 = 3.9D;
        double d1 = MathHelper.clamp(vec3d.x, -3.9D, 3.9D);
        double d2 = MathHelper.clamp(vec3d.y, -3.9D, 3.9D);
        double d3 = MathHelper.clamp(vec3d.z, -3.9D, 3.9D);

        this.xa = (int) (d1 * 8000.0D);
        this.ya = (int) (d2 * 8000.0D);
        this.za = (int) (d3 * 8000.0D);
    }

    public PacketPlayOutEntityVelocity(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.xa = packetdataserializer.readShort();
        this.ya = packetdataserializer.readShort();
        this.za = packetdataserializer.readShort();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeShort(this.xa);
        packetdataserializer.writeShort(this.ya);
        packetdataserializer.writeShort(this.za);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetEntityMotion(this);
    }

    public int getId() {
        return this.id;
    }

    public int getXa() {
        return this.xa;
    }

    public int getYa() {
        return this.ya;
    }

    public int getZa() {
        return this.za;
    }
}
