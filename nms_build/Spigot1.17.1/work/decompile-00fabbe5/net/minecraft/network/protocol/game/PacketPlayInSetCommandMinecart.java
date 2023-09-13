package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.EntityMinecartCommandBlock;
import net.minecraft.world.level.CommandBlockListenerAbstract;
import net.minecraft.world.level.World;

public class PacketPlayInSetCommandMinecart implements Packet<PacketListenerPlayIn> {

    private final int entity;
    private final String command;
    private final boolean trackOutput;

    public PacketPlayInSetCommandMinecart(int i, String s, boolean flag) {
        this.entity = i;
        this.command = s;
        this.trackOutput = flag;
    }

    public PacketPlayInSetCommandMinecart(PacketDataSerializer packetdataserializer) {
        this.entity = packetdataserializer.j();
        this.command = packetdataserializer.p();
        this.trackOutput = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.entity);
        packetdataserializer.a(this.command);
        packetdataserializer.writeBoolean(this.trackOutput);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    @Nullable
    public CommandBlockListenerAbstract a(World world) {
        Entity entity = world.getEntity(this.entity);

        return entity instanceof EntityMinecartCommandBlock ? ((EntityMinecartCommandBlock) entity).getCommandBlock() : null;
    }

    public String b() {
        return this.command;
    }

    public boolean c() {
        return this.trackOutput;
    }
}
