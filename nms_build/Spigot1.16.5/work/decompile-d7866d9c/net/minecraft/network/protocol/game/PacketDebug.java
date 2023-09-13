package net.minecraft.network.protocol.game;

import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketDebug {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void a(WorldServer worldserver, BlockPosition blockposition, String s, int i, int j) {
        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        packetdataserializer.a(blockposition);
        packetdataserializer.writeInt(i);
        packetdataserializer.a(s);
        packetdataserializer.writeInt(j);
        a(worldserver, packetdataserializer, PacketPlayOutCustomPayload.o);
    }

    public static void a(WorldServer worldserver) {
        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        a(worldserver, packetdataserializer, PacketPlayOutCustomPayload.p);
    }

    public static void a(WorldServer worldserver, ChunkCoordIntPair chunkcoordintpair) {}

    public static void a(WorldServer worldserver, BlockPosition blockposition) {
        d(worldserver, blockposition);
    }

    public static void b(WorldServer worldserver, BlockPosition blockposition) {
        d(worldserver, blockposition);
    }

    public static void c(WorldServer worldserver, BlockPosition blockposition) {
        d(worldserver, blockposition);
    }

    private static void d(WorldServer worldserver, BlockPosition blockposition) {}

    public static void a(World world, EntityInsentient entityinsentient, @Nullable PathEntity pathentity, float f) {}

    public static void a(World world, BlockPosition blockposition) {}

    public static void a(GeneratorAccessSeed generatoraccessseed, StructureStart<?> structurestart) {}

    public static void a(World world, EntityInsentient entityinsentient, PathfinderGoalSelector pathfindergoalselector) {
        if (world instanceof WorldServer) {
            ;
        }
    }

    public static void a(WorldServer worldserver, Collection<Raid> collection) {}

    public static void a(EntityLiving entityliving) {}

    public static void a(EntityBee entitybee) {}

    public static void a(TileEntityBeehive tileentitybeehive) {}

    private static void a(WorldServer worldserver, PacketDataSerializer packetdataserializer, MinecraftKey minecraftkey) {
        Packet<?> packet = new PacketPlayOutCustomPayload(minecraftkey, packetdataserializer);
        Iterator iterator = worldserver.getMinecraftWorld().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            ((EntityPlayer) entityhuman).playerConnection.sendPacket(packet);
        }

    }
}
