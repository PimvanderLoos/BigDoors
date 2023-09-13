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

    private static final int BATCH_SIZE = 5;
    private static final int RATE = 20;
    public static final int SCAN_RANGE = 48;
    private final VillagePlaceType poiType;
    private final MemoryModuleType<GlobalPos> memoryToAcquire;
    private final boolean onlyIfAdult;
    private final Optional<Byte> onPoiAcquisitionEvent;
    private long nextScheduledStart;
    private final Long2ObjectMap<BehaviorFindPosition.a> batchCache;

    public BehaviorFindPosition(VillagePlaceType villageplacetype, MemoryModuleType<GlobalPos> memorymoduletype, MemoryModuleType<GlobalPos> memorymoduletype1, boolean flag, Optional<Byte> optional) {
        super(a(memorymoduletype, memorymoduletype1));
        this.batchCache = new Long2ObjectOpenHashMap();
        this.poiType = villageplacetype;
        this.memoryToAcquire = memorymoduletype1;
        this.onlyIfAdult = flag;
        this.onPoiAcquisitionEvent = optional;
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
        if (this.onlyIfAdult && entitycreature.isBaby()) {
            return false;
        } else if (this.nextScheduledStart == 0L) {
            this.nextScheduledStart = entitycreature.level.getTime() + (long) worldserver.random.nextInt(20);
            return false;
        } else {
            return worldserver.getTime() >= this.nextScheduledStart;
        }
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        this.nextScheduledStart = i + 20L + (long) worldserver.getRandom().nextInt(20);
        VillagePlace villageplace = worldserver.A();

        this.batchCache.long2ObjectEntrySet().removeIf((entry) -> {
            return !((BehaviorFindPosition.a) entry.getValue()).b(i);
        });
        Predicate<BlockPosition> predicate = (blockposition) -> {
            BehaviorFindPosition.a behaviorfindposition_a = (BehaviorFindPosition.a) this.batchCache.get(blockposition.asLong());

            if (behaviorfindposition_a == null) {
                return true;
            } else if (!behaviorfindposition_a.c(i)) {
                return false;
            } else {
                behaviorfindposition_a.a(i);
                return true;
            }
        };
        Set<BlockPosition> set = (Set) villageplace.b(this.poiType.c(), predicate, entitycreature.getChunkCoordinates(), 48, VillagePlace.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        PathEntity pathentity = entitycreature.getNavigation().a(set, this.poiType.d());

        if (pathentity != null && pathentity.j()) {
            BlockPosition blockposition = pathentity.m();

            villageplace.c(blockposition).ifPresent((villageplacetype) -> {
                villageplace.a(this.poiType.c(), (blockposition1) -> {
                    return blockposition1.equals(blockposition);
                }, blockposition, 1);
                entitycreature.getBehaviorController().setMemory(this.memoryToAcquire, (Object) GlobalPos.create(worldserver.getDimensionKey(), blockposition));
                this.onPoiAcquisitionEvent.ifPresent((obyte) -> {
                    worldserver.broadcastEntityEffect(entitycreature, obyte);
                });
                this.batchCache.clear();
                PacketDebug.c(worldserver, blockposition);
            });
        } else {
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                this.batchCache.computeIfAbsent(blockposition1.asLong(), (j) -> {
                    return new BehaviorFindPosition.a(entitycreature.level.random, i);
                });
            }
        }

    }

    static class a {

        private static final int MIN_INTERVAL_INCREASE = 40;
        private static final int MAX_INTERVAL_INCREASE = 80;
        private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
        private final Random random;
        private long previousAttemptTimestamp;
        private long nextScheduledAttemptTimestamp;
        private int currentDelay;

        a(Random random, long i) {
            this.random = random;
            this.a(i);
        }

        public void a(long i) {
            this.previousAttemptTimestamp = i;
            int j = this.currentDelay + this.random.nextInt(40) + 40;

            this.currentDelay = Math.min(j, 400);
            this.nextScheduledAttemptTimestamp = i + (long) this.currentDelay;
        }

        public boolean b(long i) {
            return i - this.previousAttemptTimestamp < 400L;
        }

        public boolean c(long i) {
            return i >= this.nextScheduledAttemptTimestamp;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + "}";
        }
    }
}
