package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInBlockDig implements Packet<PacketListenerPlayIn> {

    private final BlockPosition pos;
    private final EnumDirection direction;
    private final PacketPlayInBlockDig.EnumPlayerDigType action;

    public PacketPlayInBlockDig(PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, BlockPosition blockposition, EnumDirection enumdirection) {
        this.action = packetplayinblockdig_enumplayerdigtype;
        this.pos = blockposition.immutable();
        this.direction = enumdirection;
    }

    public PacketPlayInBlockDig(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInBlockDig.EnumPlayerDigType) packetdataserializer.readEnum(PacketPlayInBlockDig.EnumPlayerDigType.class);
        this.pos = packetdataserializer.readBlockPos();
        this.direction = EnumDirection.from3DDataValue(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.action);
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeByte(this.direction.get3DDataValue());
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

    public static enum EnumPlayerDigType {

        START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM, SWAP_ITEM_WITH_OFFHAND;

        private EnumPlayerDigType() {}
    }
}
