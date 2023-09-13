package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;

public class PacketPlayOutBlockAction implements Packet<PacketListenerPlayOut> {

    private final BlockPosition pos;
    private final int b0;
    private final int b1;
    private final Block block;

    public PacketPlayOutBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        this.pos = blockposition;
        this.block = block;
        this.b0 = i;
        this.b1 = j;
    }

    public PacketPlayOutBlockAction(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.f();
        this.b0 = packetdataserializer.readUnsignedByte();
        this.b1 = packetdataserializer.readUnsignedByte();
        this.block = (Block) IRegistry.BLOCK.fromId(packetdataserializer.j());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);
        packetdataserializer.writeByte(this.b0);
        packetdataserializer.writeByte(this.b1);
        packetdataserializer.d(IRegistry.BLOCK.getId(this.block));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public BlockPosition b() {
        return this.pos;
    }

    public int c() {
        return this.b0;
    }

    public int d() {
        return this.b1;
    }

    public Block e() {
        return this.block;
    }
}
