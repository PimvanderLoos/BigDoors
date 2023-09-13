package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumDifficulty;

public class PacketPlayOutServerDifficulty implements Packet<PacketListenerPlayOut> {

    private final EnumDifficulty difficulty;
    private final boolean locked;

    public PacketPlayOutServerDifficulty(EnumDifficulty enumdifficulty, boolean flag) {
        this.difficulty = enumdifficulty;
        this.locked = flag;
    }

    public PacketPlayOutServerDifficulty(PacketDataSerializer packetdataserializer) {
        this.difficulty = EnumDifficulty.byId(packetdataserializer.readUnsignedByte());
        this.locked = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.difficulty.getId());
        packetdataserializer.writeBoolean(this.locked);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleChangeDifficulty(this);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }
}
