package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumDifficulty;

public class PacketPlayInDifficultyChange implements Packet<PacketListenerPlayIn> {

    private final EnumDifficulty difficulty;

    public PacketPlayInDifficultyChange(EnumDifficulty enumdifficulty) {
        this.difficulty = enumdifficulty;
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChangeDifficulty(this);
    }

    public PacketPlayInDifficultyChange(PacketDataSerializer packetdataserializer) {
        this.difficulty = EnumDifficulty.byId(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.difficulty.getId());
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }
}
