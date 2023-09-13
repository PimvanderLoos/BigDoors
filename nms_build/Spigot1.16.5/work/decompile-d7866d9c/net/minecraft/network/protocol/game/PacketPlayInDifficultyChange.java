package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumDifficulty;

public class PacketPlayInDifficultyChange implements Packet<PacketListenerPlayIn> {

    private EnumDifficulty a;

    public PacketPlayInDifficultyChange() {}

    public PacketPlayInDifficultyChange(EnumDifficulty enumdifficulty) {
        this.a = enumdifficulty;
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = EnumDifficulty.getById(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a.a());
    }

    public EnumDifficulty b() {
        return this.a;
    }
}
