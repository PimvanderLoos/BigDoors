package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumDifficulty;

public class PacketPlayInDifficultyChange implements Packet<PacketListenerPlayIn> {

    private final EnumDifficulty difficulty;

    public PacketPlayInDifficultyChange(EnumDifficulty enumdifficulty) {
        this.difficulty = enumdifficulty;
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInDifficultyChange(PacketDataSerializer packetdataserializer) {
        this.difficulty = EnumDifficulty.getById(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.difficulty.a());
    }

    public EnumDifficulty b() {
        return this.difficulty;
    }
}
