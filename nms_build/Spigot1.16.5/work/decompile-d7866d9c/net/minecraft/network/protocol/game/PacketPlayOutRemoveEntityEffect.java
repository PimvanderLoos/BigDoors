package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffectList;

public class PacketPlayOutRemoveEntityEffect implements Packet<PacketListenerPlayOut> {

    private int a;
    private MobEffectList b;

    public PacketPlayOutRemoveEntityEffect() {}

    public PacketPlayOutRemoveEntityEffect(int i, MobEffectList mobeffectlist) {
        this.a = i;
        this.b = mobeffectlist;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.i();
        this.b = MobEffectList.fromId(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.writeByte(MobEffectList.getId(this.b));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
