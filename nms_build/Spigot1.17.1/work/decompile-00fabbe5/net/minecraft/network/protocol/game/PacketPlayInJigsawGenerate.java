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
        this.pos = packetdataserializer.f();
        this.levels = packetdataserializer.j();
        this.keepJigsaws = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);
        packetdataserializer.d(this.levels);
        packetdataserializer.writeBoolean(this.keepJigsaws);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition b() {
        return this.pos;
    }

    public int c() {
        return this.levels;
    }

    public boolean d() {
        return this.keepJigsaws;
    }
}
