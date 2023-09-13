package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketPlayOutBlockBreak implements Packet<PacketListenerPlayOut> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockPosition pos;
    private final IBlockData state;
    private final PacketPlayInBlockDig.EnumPlayerDigType action;
    private final boolean allGood;

    public PacketPlayOutBlockBreak(BlockPosition blockposition, IBlockData iblockdata, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, boolean flag, String s) {
        this.pos = blockposition.immutableCopy();
        this.state = iblockdata;
        this.action = packetplayinblockdig_enumplayerdigtype;
        this.allGood = flag;
    }

    public PacketPlayOutBlockBreak(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.f();
        this.state = (IBlockData) Block.BLOCK_STATE_REGISTRY.fromId(packetdataserializer.j());
        this.action = (PacketPlayInBlockDig.EnumPlayerDigType) packetdataserializer.a(PacketPlayInBlockDig.EnumPlayerDigType.class);
        this.allGood = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);
        packetdataserializer.d(Block.getCombinedId(this.state));
        packetdataserializer.a((Enum) this.action);
        packetdataserializer.writeBoolean(this.allGood);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public IBlockData b() {
        return this.state;
    }

    public BlockPosition c() {
        return this.pos;
    }

    public boolean d() {
        return this.allGood;
    }

    public PacketPlayInBlockDig.EnumPlayerDigType e() {
        return this.action;
    }
}
