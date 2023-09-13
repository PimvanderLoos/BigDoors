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
        this.pos = blockposition.immutableCopy();
        this.direction = enumdirection;
    }

    public PacketPlayInBlockDig(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInBlockDig.EnumPlayerDigType) packetdataserializer.a(PacketPlayInBlockDig.EnumPlayerDigType.class);
        this.pos = packetdataserializer.f();
        this.direction = EnumDirection.fromType1(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.action);
        packetdataserializer.a(this.pos);
        packetdataserializer.writeByte(this.direction.b());
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition b() {
        return this.pos;
    }

    public EnumDirection c() {
        return this.direction;
    }

    public PacketPlayInBlockDig.EnumPlayerDigType d() {
        return this.action;
    }

    public static enum EnumPlayerDigType {

        START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM, SWAP_ITEM_WITH_OFFHAND;

        private EnumPlayerDigType() {}
    }
}
