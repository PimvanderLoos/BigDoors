package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.pathfinder.PathEntity;

public class BehaviorFindPosition extends Behavior<EntityCreature> {

    private final VillagePlaceType b;
    private final MemoryModuleType<GlobalPos> c;
    private final boolean d;
    private final Optional<Byte> e;
    private long f;
    private final Long2ObjectMap<BehaviorFindPosition.a> g;

    public BehaviorFindPosition(VillagePlaceType villageplacetype, MemoryModuleType<GlobalPos> memorymoduletype, MemoryModuleType<GlobalPos> memorymoduletype1, boolean flag, Optional<Byte> optional) {
        super(a(memorymoduletype, memorymoduletype1));
        this.g = new Long2ObjectOpenHashMap();
        this.b = villageplacetype;
        this.c = memorymoduletype1;
        this.d = flag;
        this.e = optional;
    }

    public BehaviorFindPosition(VillagePlaceType villageplacetype, MemoryModuleType<GlobalPos> memorymoduletype, boolean flag, Optional<Byte> optional) {
        this(villageplacetype, memorymoduletype, memorymoduletype, flag, optional);
    }

    private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> a(MemoryModuleType<GlobalPos> memorymoduletype, MemoryModuleType<GlobalPos> memorymoduletype1) {
        Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();

        builder.put(memorymoduletype, MemoryStatus.VALUE_ABSENT);
        if (memorymoduletype1 != memorymoduletype) {
            builder.put(memorymoduletype1, MemoryStatus.VALUE_ABSENT);
        }

        return builder.build();
    }

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        if (this.d && entitycreature.isBaby()) {
            return false;
        } else if (this.f == 0L) {
            this.f = entitycreature.world.getTime() + (long) worldserver.random.nextInt(20);
            return false;
        } else {
            return worldserver.getTime() >= this.f;
        }
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        this.f = i + 20L + (long) worldserver.getRandom().nextInt(20);
        VillagePlace villageplace = worldserver.y();

        this.g.long2ObjectEntrySet().removeIf((entry) -> {
            return !((BehaviorFindPosition.a) entry.getValue()).b(i);
        });
        Predicate<BlockPosition> predicate = (blockposition) -> {
            BehaviorFindPosition.a behaviorfindposition_a = (BehaviorFindPosition.a) this.g.get(blockposition.asLong());

            if (behaviorfindposition_a == null) {
                return true;
            } else if (!behaviorfindposition_a.c(i)) {
                return false;
            } else {
                behaviorfindposition_a.a(i);
                return true;
            }
        };
        Set<BlockPosition> set = (Set) villageplace.b(this.b.c(), predicate, entitycreature.getChunkCoordinates(), 48, VillagePlace.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        PathEntity pathentity = entitycreature.getNavigation().a(set, this.b.d());

        if (pathentity != null && pathentity.j()) {
            BlockPosition blockposition = pathentity.m();

            villageplace.c(blockposition).ifPresent((villageplacetype) -> {
                villageplace.a(this.b.c(), (blockposition1) -> {
                    return blockposition1.equals(blockposition);
                }, blockposition, 1);
                entitycreature.getBehaviorController().setMemory(this.c, (Object) GlobalPos.create(worldserver.getDimensionKey(), blockposition));
                this.e.ifPresent((obyte) -> {
                    worldserver.broadcastEntityEffect(entitycreature, obyte);
                });
                this.g.clear();
                PacketDebug.c(worldserver, blockposition);
            });
        } else {
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                this.g.computeIfAbsent(blockposition1.asLong(), (j) -> {
                    return new BehaviorFindPosition.a(entitycreature.world.random, i);
                });
            }
        }

    }

    static class a {

        private final Random a;
        private long b;
        private long c;
        private int d;

        a(Random random, long i) {
            this.a = random;
            this.a(i);
        }

        public void a(long i) {
            this.b = i;
            int j = this.d + this.a.nextInt(40) + 40;

            this.d = Math.min(j, 400);
            this.c = i + (long) this.d;
        }

        public boolean b(long i) {
            return i - this.b < 400L;
        }

        public boolean c(long i) {
            return i >= this.c;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.b + ", nextScheduledAttemptAt=" + this.c + ", currentDelay=" + this.d + '}';
        }
    }
}
