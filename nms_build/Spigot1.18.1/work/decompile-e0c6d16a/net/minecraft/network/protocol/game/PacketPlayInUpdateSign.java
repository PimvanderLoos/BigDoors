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
        this.pos = packetdataserializer.readBlockPos();
        this.lines = new String[4];

        for (int i = 0; i < 4; ++i) {
            this.lines[i] = packetdataserializer.readUtf(384);
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);

        for (int i = 0; i < 4; ++i) {
            packetdataserializer.writeUtf(this.lines[i]);
        }

    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSignUpdate(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public String[] getLines() {
        return this.lines;
    }
}
