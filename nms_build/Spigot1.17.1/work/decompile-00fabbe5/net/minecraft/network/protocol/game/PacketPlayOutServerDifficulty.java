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
        this.difficulty = EnumDifficulty.getById(packetdataserializer.readUnsignedByte());
        this.locked = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.difficulty.a());
        packetdataserializer.writeBoolean(this.locked);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean b() {
        return this.locked;
    }

    public EnumDifficulty c() {
        return this.difficulty;
    }
}
