package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundCategory;

public class PacketPlayOutStopSound implements Packet<PacketListenerPlayOut> {

    private static final int HAS_SOURCE = 1;
    private static final int HAS_SOUND = 2;
    @Nullable
    private final MinecraftKey name;
    @Nullable
    private final SoundCategory source;

    public PacketPlayOutStopSound(@Nullable MinecraftKey minecraftkey, @Nullable SoundCategory soundcategory) {
        this.name = minecraftkey;
        this.source = soundcategory;
    }

    public PacketPlayOutStopSound(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();

        if ((b0 & 1) > 0) {
            this.source = (SoundCategory) packetdataserializer.a(SoundCategory.class);
        } else {
            this.source = null;
        }

        if ((b0 & 2) > 0) {
            this.name = packetdataserializer.q();
        } else {
            this.name = null;
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        if (this.source != null) {
            if (this.name != null) {
                packetdataserializer.writeByte(3);
                packetdataserializer.a((Enum) this.source);
                packetdataserializer.a(this.name);
            } else {
                packetdataserializer.writeByte(1);
                packetdataserializer.a((Enum) this.source);
            }
        } else if (this.name != null) {
            packetdataserializer.writeByte(2);
            packetdataserializer.a(this.name);
        } else {
            packetdataserializer.writeByte(0);
        }

    }

    @Nullable
    public MinecraftKey b() {
        return this.name;
    }

    @Nullable
    public SoundCategory c() {
        return this.source;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
