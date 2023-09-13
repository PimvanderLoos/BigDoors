package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInJigsawGenerate implements Packet<PacketListenerPlayIn> {

    private final BlockPosition pos;
    private final int levels;
    private final boolean keepJigsaws;

    public PacketPlayInJigsawGenerate(BlockPosition blockposition, int i, boolean flag) {
        this.pos = blockposition;
        this.levels = i;
        this.keepJigsaws = flag;
    }

    public PacketPlayInJigsawGenerate(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.readBlockPos();
        this.levels = packetdataserializer.readVarInt();
        this.keepJigsaws = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeVarInt(this.levels);
        packetdataserializer.writeBoolean(this.keepJigsaws);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleJigsawGenerate(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public int levels() {
        return this.levels;
    }

    public boolean keepJigsaws() {
        return this.keepJigsaws;
    }
}
