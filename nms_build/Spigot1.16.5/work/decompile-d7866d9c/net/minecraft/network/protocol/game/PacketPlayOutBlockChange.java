package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class PacketPlayOutBlockChange implements Packet<PacketListenerPlayOut> {

    private BlockPosition a;
    public IBlockData block;

    public PacketPlayOutBlockChange() {}

    public PacketPlayOutBlockChange(BlockPosition blockposition, IBlockData iblockdata) {
        this.a = blockposition;
        this.block = iblockdata;
    }

    public PacketPlayOutBlockChange(IBlockAccess iblockaccess, BlockPosition blockposition) {
        this(blockposition, iblockaccess.getType(blockposition));
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.block = (IBlockData) Block.REGISTRY_ID.fromId(packetdataserializer.i());
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.d(Block.getCombinedId(this.block));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
