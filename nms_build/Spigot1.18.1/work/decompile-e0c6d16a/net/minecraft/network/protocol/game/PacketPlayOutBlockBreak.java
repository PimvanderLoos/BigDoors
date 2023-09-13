package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record PacketPlayOutBlockBreak(BlockPosition a, IBlockData b, PacketPlayInBlockDig.EnumPlayerDigType c, boolean d) implements Packet<PacketListenerPlayOut> {

    private final BlockPosition pos;
    private final IBlockData state;
    private final PacketPlayInBlockDig.EnumPlayerDigType action;
    private final boolean allGood;
    private static final Logger LOGGER = LogManager.getLogger();

    public PacketPlayOutBlockBreak(BlockPosition blockposition, IBlockData iblockdata, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, boolean flag, String s) {
        this(blockposition, iblockdata, packetplayinblockdig_enumplayerdigtype, flag);
    }

    public PacketPlayOutBlockBreak(BlockPosition blockposition, IBlockData iblockdata, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, boolean flag) {
        blockposition = blockposition.immutable();
        this.pos = blockposition;
        this.state = iblockdata;
        this.action = packetplayinblockdig_enumplayerdigtype;
        this.allGood = flag;
    }

    public PacketPlayOutBlockBreak(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readBlockPos(), (IBlockData) Block.BLOCK_STATE_REGISTRY.byId(packetdataserializer.readVarInt()), (PacketPlayInBlockDig.EnumPlayerDigType) packetdataserializer.readEnum(PacketPlayInBlockDig.EnumPlayerDigType.class), packetdataserializer.readBoolean());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeVarInt(Block.getId(this.state));
        packetdataserializer.writeEnum(this.action);
        packetdataserializer.writeBoolean(this.allGood);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleBlockBreakAck(this);
    }

    public BlockPosition pos() {
        return this.pos;
    }

    public IBlockData state() {
        return this.state;
    }

    public PacketPlayInBlockDig.EnumPlayerDigType action() {
        return this.action;
    }

    public boolean allGood() {
        return this.allGood;
    }
}
