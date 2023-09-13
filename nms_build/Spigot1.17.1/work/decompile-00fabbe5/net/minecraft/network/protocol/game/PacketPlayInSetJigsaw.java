package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.entity.TileEntityJigsaw;

public class PacketPlayInSetJigsaw implements Packet<PacketListenerPlayIn> {

    private final BlockPosition pos;
    private final MinecraftKey name;
    private final MinecraftKey target;
    private final MinecraftKey pool;
    private final String finalState;
    private final TileEntityJigsaw.JointType joint;

    public PacketPlayInSetJigsaw(BlockPosition blockposition, MinecraftKey minecraftkey, MinecraftKey minecraftkey1, MinecraftKey minecraftkey2, String s, TileEntityJigsaw.JointType tileentityjigsaw_jointtype) {
        this.pos = blockposition;
        this.name = minecraftkey;
        this.target = minecraftkey1;
        this.pool = minecraftkey2;
        this.finalState = s;
        this.joint = tileentityjigsaw_jointtype;
    }

    public PacketPlayInSetJigsaw(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.f();
        this.name = packetdataserializer.q();
        this.target = packetdataserializer.q();
        this.pool = packetdataserializer.q();
        this.finalState = packetdataserializer.p();
        this.joint = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.a(packetdataserializer.p()).orElse(TileEntityJigsaw.JointType.ALIGNED);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);
        packetdataserializer.a(this.name);
        packetdataserializer.a(this.target);
        packetdataserializer.a(this.pool);
        packetdataserializer.a(this.finalState);
        packetdataserializer.a(this.joint.getName());
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition b() {
        return this.pos;
    }

    public MinecraftKey c() {
        return this.name;
    }

    public MinecraftKey d() {
        return this.target;
    }

    public MinecraftKey e() {
        return this.pool;
    }

    public String f() {
        return this.finalState;
    }

    public TileEntityJigsaw.JointType g() {
        return this.joint;
    }
}
