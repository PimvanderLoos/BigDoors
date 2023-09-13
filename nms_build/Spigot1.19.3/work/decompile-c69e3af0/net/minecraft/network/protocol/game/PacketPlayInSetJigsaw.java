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
        this.pos = packetdataserializer.readBlockPos();
        this.name = packetdataserializer.readResourceLocation();
        this.target = packetdataserializer.readResourceLocation();
        this.pool = packetdataserializer.readResourceLocation();
        this.finalState = packetdataserializer.readUtf();
        this.joint = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.byName(packetdataserializer.readUtf()).orElse(TileEntityJigsaw.JointType.ALIGNED);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeResourceLocation(this.name);
        packetdataserializer.writeResourceLocation(this.target);
        packetdataserializer.writeResourceLocation(this.pool);
        packetdataserializer.writeUtf(this.finalState);
        packetdataserializer.writeUtf(this.joint.getSerializedName());
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSetJigsawBlock(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public MinecraftKey getName() {
        return this.name;
    }

    public MinecraftKey getTarget() {
        return this.target;
    }

    public MinecraftKey getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public TileEntityJigsaw.JointType getJoint() {
        return this.joint;
    }
}
