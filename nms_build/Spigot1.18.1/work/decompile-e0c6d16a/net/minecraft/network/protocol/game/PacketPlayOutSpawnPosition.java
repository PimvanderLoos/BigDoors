package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutSpawnPosition implements Packet<PacketListenerPlayOut> {

    public final BlockPosition pos;
    private final float angle;

    public PacketPlayOutSpawnPosition(BlockPosition blockposition, float f) {
        this.pos = blockposition;
        this.angle = f;
    }

    public PacketPlayOutSpawnPosition(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.readBlockPos();
        this.angle = packetdataserializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeFloat(this.angle);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetSpawn(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public float getAngle() {
        return this.angle;
    }
}
