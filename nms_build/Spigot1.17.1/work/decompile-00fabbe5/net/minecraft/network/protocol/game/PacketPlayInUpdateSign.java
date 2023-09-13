package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInUpdateSign implements Packet<PacketListenerPlayIn> {

    private static final int MAX_STRING_LENGTH = 384;
    private final BlockPosition pos;
    private final String[] lines;

    public PacketPlayInUpdateSign(BlockPosition blockposition, String s, String s1, String s2, String s3) {
        this.pos = blockposition;
        this.lines = new String[]{s, s1, s2, s3};
    }

    public PacketPlayInUpdateSign(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.f();
        this.lines = new String[4];

        for (int i = 0; i < 4; ++i) {
            this.lines[i] = packetdataserializer.e(384);
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);

        for (int i = 0; i < 4; ++i) {
            packetdataserializer.a(this.lines[i]);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition b() {
        return this.pos;
    }

    public String[] c() {
        return this.lines;
    }
}
