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

    public static void a(WorldServer worldserver, BlockPosition blockposition, String s, int i, int j) {
        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        packetdataserializer.a(blockposition);
        packetdataserializer.writeInt(i);
        packetdataserializer.a(s);
        packetdataserializer.writeInt(j);
        a(worldserver, packetdataserializer, PacketPlayOutCustomPayload.DEBUG_GAME_TEST_ADD_MARKER);
    }

    public static void a(WorldServer worldserver) {
        PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

        a(worldserver, packetdataserializer, PacketPlayOutCustomPayload.DEBUG_GAME_TEST_CLEAR);
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

    public static void a(World world, GameEvent gameevent, BlockPosition blockposition) {}

    public static void a(World world, GameEventListener gameeventlistener) {}

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBeehive tileentitybeehive) {}

    private static void a(EntityLiving entityliving, PacketDataSerializer packetdataserializer) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        long i = entityliving.level.getTime();

        if (entityliving instanceof InventoryCarrier) {
            IInventory iinventory = ((InventoryCarrier) entityliving).getInventory();

            packetdataserializer.a(iinventory.isEmpty() ? "" : iinventory.toString());
        } else {
            packetdataserializer.a("");
        }

        if (behaviorcontroller.hasMemory(MemoryModuleType.PATH)) {
            packetdataserializer.writeBoolean(true);
            PathEntity pathentity = (PathEntity) behaviorcontroller.getMemory(MemoryModuleType.PATH).get();

            pathentity.a(packetdataserializer);
        } else {
            packetdataserializer.writeBoolean(false);
        }

        if (entityliving instanceof EntityVillager) {
            EntityVillager entityvillager = (EntityVillager) entityliving;
            boolean flag = entityvillager.a(i);

            packetdataserializer.writeBoolean(flag);
        } else {
            packetdataserializer.writeBoolean(false);
        }

        packetdataserializer.a((Collection) behaviorcontroller.c(), (packetdataserializer1, activity) -> {
            packetdataserializer1.a(activity.a());
        });
        Set<String> set = (Set) behaviorcontroller.d().stream().map(Behavior::toString).collect(Collectors.toSet());

        packetdataserializer.a((Collection) set, PacketDataSerializer::a);
        packetdataserializer.a((Collection) a(entityliving, i), (packetdataserializer1, s) -> {
            String s1 = UtilColor.a(s, 255, true);

            packetdataserializer1.a(s1);
        });
        Stream stream;
        Set set1;

        if (entityliving instanceof EntityVillager) {
            stream = Stream.of(MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT);
            Objects.requireNonNull(behaviorcontroller);
            set1 = (Set) stream.map(behaviorcontroller::getMemory).flatMap(SystemUtils::a).map(GlobalPos::getBlockPosition).collect(Collectors.toSet());
            packetdataserializer.a((Collection) set1, PacketDataSerializer::a);
        } else {
            packetdataserializer.d(0);
        }

        if (entityliving instanceof EntityVillager) {
            stream = Stream.of(MemoryModuleType.POTENTIAL_JOB_SITE);
            Objects.requireNonNull(behaviorcontroller);
            set1 = (Set) stream.map(behaviorcontroller::getMemory).flatMap(SystemUtils::a).map(GlobalPos::getBlockPosition).collect(Collectors.toSet());
            packetdataserializer.a((Collection) set1, PacketDataSerializer::a);
        } else {
            packetdataserializer.d(0);
        }

        if (entityliving instanceof EntityVillager) {
            Map<UUID, Object2IntMap<ReputationType>> map = ((EntityVillager) entityliving).fT().a();
            List<String> list = Lists.newArrayList();

            map.forEach((uuid, object2intmap) -> {
                String s = DebugEntityNameGenerator.a(uuid);

                object2intmap.forEach((reputationtype, integer) -> {
                    list.add(s + ": " + reputationtype + ": " + integer);
                });
            });
            packetdataserializer.a((Collection) list, PacketDataSerializer::a);
        } else {
            packetdataserializer.d(0);
        }

    }

    private static List<String> a(EntityLiving entityliving, long i) {
        Map<MemoryModuleType<?>, Optional<? extends ExpirableMemory<?>>> map = entityliving.getBehaviorController().a();
        List<String> list = Lists.newArrayList();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MemoryModuleType<?>, Optional<? extends ExpirableMemory<?>>> entry = (Entry) iterator.next();
            MemoryModuleType<?> memorymoduletype = (MemoryModuleType) entry.getKey();
            Optional<? extends ExpirableMemory<?>> optional = (Optional) entry.getValue();
            String s;

            if (optional.isPresent()) {
                ExpirableMemory<?> expirablememory = (ExpirableMemory) optional.get();
                Object object = expirablememory.c();

                if (memorymoduletype == MemoryModuleType.HEARD_BELL_TIME) {
                    long j = i - (Long) object;

                    s = j + " ticks ago";
                } else if (expirablememory.e()) {
                    String s1 = a((WorldServer) entityliving.level, object);

                    s = s1 + " (ttl: " + expirablememory.b() + ")";
                } else {
                    s = a((WorldServer) entityliving.level, object);
                }
            } else {
                s = "-";
            }

            String s2 = IRegistry.MEMORY_MODULE_TYPE.getKey(memorymoduletype).getKey();

            list.add(s2 + ": " + s);
        }

        list.sort(String::compareTo);
        return list;
    }

    private static String a(WorldServer worldserver, @Nullable Object object) {
        if (object == null) {
            return "-";
        } else if (object instanceof UUID) {
            return a(worldserver, (Object) worldserver.getEntity((UUID) object));
        } else {
            Entity entity;

            if (object instanceof EntityLiving) {
                entity = (Entity) object;
                return DebugEntityNameGenerator.a(entity);
            } else if (object instanceof INamableTileEntity) {
                return ((INamableTileEntity) object).getDisplayName().getString();
            } else if (object instanceof MemoryTarget) {
                return a(worldserver, (Object) ((MemoryTarget) object).a());
            } else if (object instanceof BehaviorPositionEntity) {
                return a(worldserver, (Object) ((BehaviorPositionEntity) object).c());
            } else if (object instanceof GlobalPos) {
                return a(worldserver, (Object) ((GlobalPos) object).getBlockPosition());
            } else if (object instanceof BehaviorTarget) {
                return a(worldserver, (Object) ((BehaviorTarget) object).b());
            } else if (object instanceof EntityDamageSource) {
                entity = ((EntityDamageSource) object).getEntity();
                return entity == null ? object.toString() : a(worldserver, (Object) entity);
            } else if (!(object instanceof Collection)) {
                return object.toString();
            } else {
                List<String> list = Lists.newArrayList();
                Iterator iterator = ((Iterable) object).iterator();

                while (iterator.hasNext()) {
                    Object object1 = iterator.next();

                    list.add(a(worldserver, object1));
                }

                return list.toString();
            }
        }
    }

    private static void a(WorldServer worldserver, PacketDataSerializer packetdataserializer, MinecraftKey minecraftkey) {
        Packet<?> packet = new PacketPlayOutCustomPayload(minecraftkey, packetdataserializer);
        Iterator iterator = worldserver.getLevel().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            ((EntityPlayer) entityhuman).connection.sendPacket(packet);
        }

    }
}
