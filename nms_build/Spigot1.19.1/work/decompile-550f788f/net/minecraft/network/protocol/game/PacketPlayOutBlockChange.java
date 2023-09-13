package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class PacketPlayOutBlockChange implements Packet<PacketListenerPlayOut> {

    private final BlockPosition pos;
    public final IBlockData blockState;

    public PacketPlayOutBlockChange(BlockPosition blockposition, IBlockData iblockdata) {
        this.pos = blockposition;
        this.blockState = iblockdata;
    }

    public PacketPlayOutBlockChange(IBlockAccess iblockaccess, BlockPosition blockposition) {
        this(blockposition, iblockaccess.getBlockState(blockposition));
    }

    public PacketPlayOutBlockChange(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.readBlockPos();
        this.blockState = (IBlockData) packetdataserializer.readById(Block.BLOCK_STATE_REGISTRY);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeId(Block.BLOCK_STATE_REGISTRY, this.blockState);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleBlockUpdate(this);
    }

    public IBlockData getBlockState() {
        return this.blockState;
    }

    public BlockPosition getPos() {
        return this.pos;
    }
}
