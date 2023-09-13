package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.UtilColor;
import net.minecraft.world.IInventory;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorPositionEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.gossip.ReputationType;
import net.minecraft.world.entity.ai.memory.ExpirableMemory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketDebug {

    private static final Logger LOGGER = LogManager.getLogger();

    public PacketDebug() {}

    public static void sendGameTestAddMarker(WorldServer worldserver, BlockPosition blockposition, String s, int i, int j) {
        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        packetdataserializer.writeBlockPos(blockposition);
        packetdataserializer.writeInt(i);
        packetdataserializer.writeUtf(s);
        packetdataserializer.writeInt(j);
        sendPacketToAllPlayers(worldserver, packetdataserializer, PacketPlayOutCustomPayload.DEBUG_GAME_TEST_ADD_MARKER);
    }

    public static void sendGameTestClearPacket(WorldServer worldserver) {
        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        sendPacketToAllPlayers(worldserver, packetdataserializer, PacketPlayOutCustomPayload.DEBUG_GAME_TEST_CLEAR);
    }

    public static void sendPoiPacketsForChunk(WorldServer worldserver, ChunkCoordIntPair chunkcoordintpair) {}

    public static void sendPoiAddedPacket(WorldServer worldserver, BlockPosition blockposition) {
        sendVillageSectionsPacket(worldserver, blockposition);
    }

    public static void sendPoiRemovedPacket(WorldServer worldserver, BlockPosition blockposition) {
        sendVillageSectionsPacket(worldserver, blockposition);
    }

    public static void sendPoiTicketCountPacket(WorldServer worldserver, BlockPosition blockposition) {
        sendVillageSectionsPacket(worldserver, blockposition);
    }

    private static void sendVillageSectionsPacket(WorldServer worldserver, BlockPosition blockposition) {}

    public static void sendPathFindingPacket(World world, EntityInsentient entityinsentient, @Nullable PathEntity pathentity, float f) {}

    public static void sendNeighborsUpdatePacket(World world, BlockPosition blockposition) {}

    public static void sendStructurePacket(GeneratorAccessSeed generatoraccessseed, StructureStart<?> structurestart) {}

    public static void sendGoalSelector(World world, EntityInsentient entityinsentient, PathfinderGoalSelector pathfindergoalselector) {
        if (world instanceof WorldServer) {
            ;
        }
    }

    public static void sendRaids(WorldServer worldserver, Collection<Raid> collection) {}

    public static void sendEntityBrain(EntityLiving entityliving) {}

    public static void sendBeeInfo(EntityBee entitybee) {}

    public static void sendGameEventInfo(World world, GameEvent gameevent, BlockPosition blockposition) {}

    public static void sendGameEventListenerInfo(World world, GameEventListener gameeventlistener) {}

    public static void sendHiveInfo(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBeehive tileentitybeehive) {}

    private static void writeBrain(EntityLiving entityliving, PacketDataSerializer packetdataserializer) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        long i = entityliving.level.getGameTime();

        if (entityliving instanceof InventoryCarrier) {
            IInventory iinventory = ((InventoryCarrier) entityliving).getInventory();

            packetdataserializer.writeUtf(iinventory.isEmpty() ? "" : iinventory.toString());
        } else {
            packetdataserializer.writeUtf("");
        }

        if (behaviorcontroller.hasMemoryValue(MemoryModuleType.PATH)) {
            packetdataserializer.writeBoolean(true);
            PathEntity pathentity = (PathEntity) behaviorcontroller.getMemory(MemoryModuleType.PATH).get();

            pathentity.writeToStream(packetdataserializer);
        } else {
            packetdataserializer.writeBoolean(false);
        }

        if (entityliving instanceof EntityVillager) {
            EntityVillager entityvillager = (EntityVillager) entityliving;
            boolean flag = entityvillager.wantsToSpawnGolem(i);

            packetdataserializer.writeBoolean(flag);
        } else {
            packetdataserializer.writeBoolean(false);
        }

        packetdataserializer.writeCollection(behaviorcontroller.getActiveActivities(), (packetdataserializer1, activity) -> {
            packetdataserializer1.writeUtf(activity.getName());
        });
        Set<String> set = (Set) behaviorcontroller.getRunningBehaviors().stream().map(Behavior::toString).collect(Collectors.toSet());

        packetdataserializer.writeCollection(set, PacketDataSerializer::writeUtf);
        packetdataserializer.writeCollection(getMemoryDescriptions(entityliving, i), (packetdataserializer1, s) -> {
            String s1 = UtilColor.truncateStringIfNecessary(s, 255, true);

            packetdataserializer1.writeUtf(s1);
        });
        Stream stream;
        Set set1;

        if (entityliving instanceof EntityVillager) {
            stream = Stream.of(MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT);
            Objects.requireNonNull(behaviorcontroller);
            set1 = (Set) stream.map(behaviorcontroller::getMemory).flatMap(SystemUtils::toStream).map(GlobalPos::pos).collect(Collectors.toSet());
            packetdataserializer.writeCollection(set1, PacketDataSerializer::writeBlockPos);
        } else {
            packetdataserializer.writeVarInt(0);
        }

        if (entityliving instanceof EntityVillager) {
            stream = Stream.of(MemoryModuleType.POTENTIAL_JOB_SITE);
            Objects.requireNonNull(behaviorcontroller);
            set1 = (Set) stream.map(behaviorcontroller::getMemory).flatMap(SystemUtils::toStream).map(GlobalPos::pos).collect(Collectors.toSet());
            packetdataserializer.writeCollection(set1, PacketDataSerializer::writeBlockPos);
        } else {
            packetdataserializer.writeVarInt(0);
        }

        if (entityliving instanceof EntityVillager) {
            Map<UUID, Object2IntMap<ReputationType>> map = ((EntityVillager) entityliving).getGossips().getGossipEntries();
            List<String> list = Lists.newArrayList();

            map.forEach((uuid, object2intmap) -> {
                String s = DebugEntityNameGenerator.getEntityName(uuid);

                object2intmap.forEach((reputationtype, integer) -> {
                    list.add(s + ": " + reputationtype + ": " + integer);
                });
            });
            packetdataserializer.writeCollection(list, PacketDataSerializer::writeUtf);
        } else {
            packetdataserializer.writeVarInt(0);
        }

    }

    private static List<String> getMemoryDescriptions(EntityLiving entityliving, long i) {
        Map<MemoryModuleType<?>, Optional<? extends ExpirableMemory<?>>> map = entityliving.getBrain().getMemories();
        List<String> list = Lists.newArrayList();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MemoryModuleType<?>, Optional<? extends ExpirableMemory<?>>> entry = (Entry) iterator.next();
            MemoryModuleType<?> memorymoduletype = (MemoryModuleType) entry.getKey();
            Optional<? extends ExpirableMemory<?>> optional = (Optional) entry.getValue();
            String s;

            if (optional.isPresent()) {
                ExpirableMemory<?> expirablememory = (ExpirableMemory) optional.get();
                Object object = expirablememory.getValue();

                if (memorymoduletype == MemoryModuleType.HEARD_BELL_TIME) {
                    long j = i - (Long) object;

                    s = j + " ticks ago";
                } else if (expirablememory.canExpire()) {
                    String s1 = getShortDescription((WorldServer) entityliving.level, object);

                    s = s1 + " (ttl: " + expirablememory.getTimeToLive() + ")";
                } else {
                    s = getShortDescription((WorldServer) entityliving.level, object);
                }
            } else {
                s = "-";
            }

            String s2 = IRegistry.MEMORY_MODULE_TYPE.getKey(memorymoduletype).getPath();

            list.add(s2 + ": " + s);
        }

        list.sort(String::compareTo);
        return list;
    }

    private static String getShortDescription(WorldServer worldserver, @Nullable Object object) {
        if (object == null) {
            return "-";
        } else if (object instanceof UUID) {
            return getShortDescription(worldserver, worldserver.getEntity((UUID) object));
        } else {
            Entity entity;

            if (object instanceof EntityLiving) {
                entity = (Entity) object;
                return DebugEntityNameGenerator.getEntityName(entity);
            } else if (object instanceof INamableTileEntity) {
                return ((INamableTileEntity) object).getName().getString();
            } else if (object instanceof MemoryTarget) {
                return getShortDescription(worldserver, ((MemoryTarget) object).getTarget());
            } else if (object instanceof BehaviorPositionEntity) {
                return getShortDescription(worldserver, ((BehaviorPositionEntity) object).getEntity());
            } else if (object instanceof GlobalPos) {
                return getShortDescription(worldserver, ((GlobalPos) object).pos());
            } else if (object instanceof BehaviorTarget) {
                return getShortDescription(worldserver, ((BehaviorTarget) object).currentBlockPosition());
            } else if (object instanceof EntityDamageSource) {
                entity = ((EntityDamageSource) object).getEntity();
                return entity == null ? object.toString() : getShortDescription(worldserver, entity);
            } else if (!(object instanceof Collection)) {
                return object.toString();
            } else {
                List<String> list = Lists.newArrayList();
                Iterator iterator = ((Iterable) object).iterator();

                while (iterator.hasNext()) {
                    Object object1 = iterator.next();

                    list.add(getShortDescription(worldserver, object1));
                }

                return list.toString();
            }
        }
    }

    private static void sendPacketToAllPlayers(WorldServer worldserver, PacketDataSerializer packetdataserializer, MinecraftKey minecraftkey) {
        Packet<?> packet = new PacketPlayOutCustomPayload(minecraftkey, packetdataserializer);
        Iterator iterator = worldserver.getLevel().players().iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            ((EntityPlayer) entityhuman).connection.send(packet);
        }

    }
}
