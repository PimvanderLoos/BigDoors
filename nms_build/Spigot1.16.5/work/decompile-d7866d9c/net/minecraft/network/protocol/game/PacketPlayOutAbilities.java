package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.PlayerAbilities;

public class PacketPlayOutAbilities implements Packet<PacketListenerPlayOut> {

    private boolean a;
    private boolean b;
    private boolean c;
    private boolean d;
    private float e;
    private float f;

    public PacketPlayOutAbilities() {}

    public PacketPlayOutAbilities(PlayerAbilities playerabilities) {
        this.a = playerabilities.isInvulnerable;
        this.b = playerabilities.isFlying;
        this.c = playerabilities.canFly;
        this.d = playerabilities.canInstantlyBuild;
        this.e = playerabilities.a();
        this.f = playerabilities.b();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        byte b0 = packetdataserializer.readByte();

        this.a = (b0 & 1) != 0;
        this.b = (b0 & 2) != 0;
        this.c = (b0 & 4) != 0;
        this.d = (b0 & 8) != 0;
        this.e = packetdataserializer.readFloat();
        this.f = packetdataserializer.readFloat();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        byte b0 = 0;

        if (this.a) {
            b0 = (byte) (b0 | 1);
        }

        if (this.b) {
            b0 = (byte) (b0 | 2);
        }

        if (this.c) {
            b0 = (byte) (b0 | 4);
        }

        if (this.d) {
            b0 = (byte) (b0 | 8);
        }

        packetdataserializer.writeByte(b0);
        packetdataserializer.writeFloat(this.e);
        packetdataserializer.writeFloat(this.f);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
