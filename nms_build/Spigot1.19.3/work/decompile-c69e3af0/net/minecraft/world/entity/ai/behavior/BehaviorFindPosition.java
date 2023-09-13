package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.apache.commons.lang3.mutable.MutableLong;

public class BehaviorFindPosition {

    public static final int SCAN_RANGE = 48;

    public BehaviorFindPosition() {}

    public static BehaviorControl<EntityCreature> create(Predicate<Holder<VillagePlaceType>> predicate, MemoryModuleType<GlobalPos> memorymoduletype, boolean flag, Optional<Byte> optional) {
        return create(predicate, memorymoduletype, memorymoduletype, flag, optional);
    }

    public static BehaviorControl<EntityCreature> create(Predicate<Holder<VillagePlaceType>> predicate, MemoryModuleType<GlobalPos> memorymoduletype, MemoryModuleType<GlobalPos> memorymoduletype1, boolean flag, Optional<Byte> optional) {
        boolean flag1 = true;
        boolean flag2 = true;
        MutableLong mutablelong = new MutableLong(0L);
        Long2ObjectMap<BehaviorFindPosition.a> long2objectmap = new Long2ObjectOpenHashMap();
        OneShot<EntityCreature> oneshot = BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(memorymoduletype1)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entitycreature, i) -> {
                    if (flag && entitycreature.isBaby()) {
                        return false;
                    } else if (mutablelong.getValue() == 0L) {
                        mutablelong.setValue(worldserver.getGameTime() + (long) worldserver.random.nextInt(20));
                        return false;
                    } else if (worldserver.getGameTime() < mutablelong.getValue()) {
                        return false;
                    } else {
                        mutablelong.setValue(i + 20L + (long) worldserver.getRandom().nextInt(20));
                        VillagePlace villageplace = worldserver.getPoiManager();

                        long2objectmap.long2ObjectEntrySet().removeIf((entry) -> {
                            return !((BehaviorFindPosition.a) entry.getValue()).isStillValid(i);
                        });
                        Predicate<BlockPosition> predicate1 = (blockposition) -> {
                            BehaviorFindPosition.a behaviorfindposition_a = (BehaviorFindPosition.a) long2objectmap.get(blockposition.asLong());

                            if (behaviorfindposition_a == null) {
                                return true;
                            } else if (!behaviorfindposition_a.shouldRetry(i)) {
                                return false;
                            } else {
                                behaviorfindposition_a.markAttempt(i);
                                return true;
                            }
                        };
                        Set<Pair<Holder<VillagePlaceType>, BlockPosition>> set = (Set) villageplace.findAllClosestFirstWithType(predicate, predicate1, entitycreature.blockPosition(), 48, VillagePlace.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
                        PathEntity pathentity = findPathToPois(entitycreature, set);

                        if (pathentity != null && pathentity.canReach()) {
                            BlockPosition blockposition = pathentity.getTarget();

                            villageplace.getType(blockposition).ifPresent((holder) -> {
                                villageplace.take(predicate, (holder1, blockposition1) -> {
                                    return blockposition1.equals(blockposition);
                                }, blockposition, 1);
                                memoryaccessor.set(GlobalPos.of(worldserver.dimension(), blockposition));
                                optional.ifPresent((obyte) -> {
                                    worldserver.broadcastEntityEvent(entitycreature, obyte);
                                });
                                long2objectmap.clear();
                                PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
                            });
                        } else {
                            Iterator iterator = set.iterator();

                            while (iterator.hasNext()) {
                                Pair<Holder<VillagePlaceType>, BlockPosition> pair = (Pair) iterator.next();

                                long2objectmap.computeIfAbsent(((BlockPosition) pair.getSecond()).asLong(), (j) -> {
                                    return new BehaviorFindPosition.a(worldserver.random, i);
                                });
                            }
                        }

                        return true;
                    }
                };
            });
        });

        return memorymoduletype1 == memorymoduletype ? oneshot : BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(memorymoduletype)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return oneshot;
            });
        });
    }

    @Nullable
    public static PathEntity findPathToPois(EntityInsentient entityinsentient, Set<Pair<Holder<VillagePlaceType>, BlockPosition>> set) {
        if (set.isEmpty()) {
            return null;
        } else {
            Set<BlockPosition> set1 = new HashSet();
            int i = 1;
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                Pair<Holder<VillagePlaceType>, BlockPosition> pair = (Pair) iterator.next();

                i = Math.max(i, ((VillagePlaceType) ((Holder) pair.getFirst()).value()).validRange());
                set1.add((BlockPosition) pair.getSecond());
            }

            return entityinsentient.getNavigation().createPath((Set) set1, i);
        }
    }

    private static class a {

        private static final int MIN_INTERVAL_INCREASE = 40;
        private static final int MAX_INTERVAL_INCREASE = 80;
        private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
        private final RandomSource random;
        private long previousAttemptTimestamp;
        private long nextScheduledAttemptTimestamp;
        private int currentDelay;

        a(RandomSource randomsource, long i) {
            this.random = randomsource;
            this.markAttempt(i);
        }

        public void markAttempt(long i) {
            this.previousAttemptTimestamp = i;
            int j = this.currentDelay + this.random.nextInt(40) + 40;

            this.currentDelay = Math.min(j, 400);
            this.nextScheduledAttemptTimestamp = i + (long) this.currentDelay;
        }

        public boolean isStillValid(long i) {
            return i - this.previousAttemptTimestamp < 400L;
        }

        public boolean shouldRetry(long i) {
            return i >= this.nextScheduledAttemptTimestamp;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + "}";
        }
    }
}
