package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.entity.TileEntityJigsaw;

public class PacketPlayInSetJigsaw implements Packet<PacketListenerPlayIn> {

    private BlockPosition a;
    private MinecraftKey b;
    private MinecraftKey c;
    private MinecraftKey d;
    private String e;
    private TileEntityJigsaw.JointType f;

    public PacketPlayInSetJigsaw() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.p();
        this.c = packetdataserializer.p();
        this.d = packetdataserializer.p();
        this.e = packetdataserializer.e(32767);
        this.f = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.a(packetdataserializer.e(32767)).orElse(TileEntityJigsaw.JointType.ALIGNED);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.a(this.c);
        packetdataserializer.a(this.d);
        packetdataserializer.a(this.e);
        packetdataserializer.a(this.f.getName());
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition b() {
        return this.a;
    }

    public MinecraftKey c() {
        return this.b;
    }

    public MinecraftKey d() {
        return this.c;
    }

    public MinecraftKey e() {
        return this.d;
    }

    public String f() {
        return this.e;
    }

    public TileEntityJigsaw.JointType g() {
        return this.f;
    }
}
