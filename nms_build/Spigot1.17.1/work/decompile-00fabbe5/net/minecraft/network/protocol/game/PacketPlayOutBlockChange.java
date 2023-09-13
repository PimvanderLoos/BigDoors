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
        this(blockposition, iblockaccess.getType(blockposition));
    }

    public PacketPlayOutBlockChange(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.f();
        this.blockState = (IBlockData) Block.BLOCK_STATE_REGISTRY.fromId(packetdataserializer.j());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);
        packetdataserializer.d(Block.getCombinedId(this.blockState));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public IBlockData b() {
        return this.blockState;
    }

    public BlockPosition c() {
        return this.pos;
    }
}
