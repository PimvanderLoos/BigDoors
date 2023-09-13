package net.minecraft.network.protocol.game;

import java.util.Set;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.RelativeMovement;

public class PacketPlayOutPosition implements Packet<PacketListenerPlayOut> {

    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;
    private final Set<RelativeMovement> relativeArguments;
    private final int id;

    public PacketPlayOutPosition(double d0, double d1, double d2, float f, float f1, Set<RelativeMovement> set, int i) {
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.yRot = f;
        this.xRot = f1;
        this.relativeArguments = set;
        this.id = i;
    }

    public PacketPlayOutPosition(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readFloat();
        this.xRot = packetdataserializer.readFloat();
        this.relativeArguments = RelativeMovement.unpack(packetdataserializer.readUnsignedByte());
        this.id = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeFloat(this.yRot);
        packetdataserializer.writeFloat(this.xRot);
        packetdataserializer.writeByte(RelativeMovement.pack(this.relativeArguments));
        packetdataserializer.writeVarInt(this.id);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleMovePlayer(this);
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

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }

    public int getId() {
        return this.id;
    }

    public Set<RelativeMovement> getRelativeArguments() {
        return this.relativeArguments;
    }
}
