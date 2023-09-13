package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class BehaviorWalkHome {

    private static final int CACHE_TIMEOUT = 40;
    private static final int BATCH_SIZE = 5;
    private static final int RATE = 20;
    private static final int OK_DISTANCE_SQR = 4;

    public BehaviorWalkHome() {}

    public static BehaviorControl<EntityCreature> create(float f) {
        Long2LongOpenHashMap long2longopenhashmap = new Long2LongOpenHashMap();
        MutableLong mutablelong = new MutableLong(0L);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.HOME)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entitycreature, i) -> {
                    if (worldserver.getGameTime() - mutablelong.getValue() < 20L) {
                        return false;
                    } else {
                        VillagePlace villageplace = worldserver.getPoiManager();
                        Optional<BlockPosition> optional = villageplace.findClosest((holder) -> {
                            return holder.is(PoiTypes.HOME);
                        }, entitycreature.blockPosition(), 48, VillagePlace.Occupancy.ANY);

                        if (!optional.isEmpty() && ((BlockPosition) optional.get()).distSqr(entitycreature.blockPosition()) > 4.0D) {
                            MutableInt mutableint = new MutableInt(0);

                            mutablelong.setValue(worldserver.getGameTime() + (long) worldserver.getRandom().nextInt(20));
                            Predicate<BlockPosition> predicate = (blockposition) -> {
                                long j = blockposition.asLong();

                                if (long2longopenhashmap.containsKey(j)) {
                                    return false;
                                } else if (mutableint.incrementAndGet() >= 5) {
                                    return false;
                                } else {
                                    long2longopenhashmap.put(j, mutablelong.getValue() + 40L);
                                    return true;
                                }
                            };
                            Set<Pair<Holder<VillagePlaceType>, BlockPosition>> set = (Set) villageplace.findAllWithType((holder) -> {
                                return holder.is(PoiTypes.HOME);
                            }, predicate, entitycreature.blockPosition(), 48, VillagePlace.Occupancy.ANY).collect(Collectors.toSet());
                            PathEntity pathentity = BehaviorFindPosition.findPathToPois(entitycreature, set);

                            if (pathentity != null && pathentity.canReach()) {
                                BlockPosition blockposition = pathentity.getTarget();
                                Optional<Holder<VillagePlaceType>> optional1 = villageplace.getType(blockposition);

                                if (optional1.isPresent()) {
                                    memoryaccessor.set(new MemoryTarget(blockposition, f, 1));
                                    PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
                                }
                            } else if (mutableint.getValue() < 5) {
                                long2longopenhashmap.long2LongEntrySet().removeIf((entry) -> {
                                    return entry.getLongValue() < mutablelong.getValue();
                                });
                            }

                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            });
        });
    }
}
