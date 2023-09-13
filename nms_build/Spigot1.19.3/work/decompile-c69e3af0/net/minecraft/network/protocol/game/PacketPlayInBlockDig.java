package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInBlockDig implements Packet<PacketListenerPlayIn> {

    private final BlockPosition pos;
    private final EnumDirection direction;
    private final PacketPlayInBlockDig.EnumPlayerDigType action;
    private final int sequence;

    public PacketPlayInBlockDig(PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, BlockPosition blockposition, EnumDirection enumdirection, int i) {
        this.action = packetplayinblockdig_enumplayerdigtype;
        this.pos = blockposition.immutable();
        this.direction = enumdirection;
        this.sequence = i;
    }

    public PacketPlayInBlockDig(PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, BlockPosition blockposition, EnumDirection enumdirection) {
        this(packetplayinblockdig_enumplayerdigtype, blockposition, enumdirection, 0);
    }

    public PacketPlayInBlockDig(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInBlockDig.EnumPlayerDigType) packetdataserializer.readEnum(PacketPlayInBlockDig.EnumPlayerDigType.class);
        this.pos = packetdataserializer.readBlockPos();
        this.direction = EnumDirection.from3DDataValue(packetdataserializer.readUnsignedByte());
        this.sequence = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.action);
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeByte(this.direction.get3DDataValue());
        packetdataserializer.writeVarInt(this.sequence);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handlePlayerAction(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public EnumDirection getDirection() {
        return this.direction;
    }

    public PacketPlayInBlockDig.EnumPlayerDigType getAction() {
        return this.action;
    }

    public int getSequence() {
        return this.sequence;
    }

    public static enum EnumPlayerDigType {

        START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM, SWAP_ITEM_WITH_OFFHAND;

        private EnumPlayerDigType() {}
    }
}
