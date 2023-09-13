package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
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
        this.pos = packetdataserializer.readBlockPos();
        this.b0 = packetdataserializer.readUnsignedByte();
        this.b1 = packetdataserializer.readUnsignedByte();
        this.block = (Block) packetdataserializer.readById(BuiltInRegistries.BLOCK);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeByte(this.b0);
        packetdataserializer.writeByte(this.b1);
        packetdataserializer.writeId(BuiltInRegistries.BLOCK, this.block);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleBlockEvent(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public int getB0() {
        return this.b0;
    }

    public int getB1() {
        return this.b1;
    }

    public Block getBlock() {
        return this.block;
    }
}
