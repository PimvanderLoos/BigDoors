package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.pathfinder.PathEntity;

public class SensorNearestBed extends Sensor<EntityInsentient> {

    private static final int CACHE_TIMEOUT = 40;
    private static final int BATCH_SIZE = 5;
    private static final int RATE = 20;
    private final Long2LongMap batchCache = new Long2LongOpenHashMap();
    private int triedCount;
    private long lastUpdate;

    public SensorNearestBed() {
        super(20);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }

    protected void doTick(WorldServer worldserver, EntityInsentient entityinsentient) {
        if (entityinsentient.isBaby()) {
            this.triedCount = 0;
            this.lastUpdate = worldserver.getGameTime() + (long) worldserver.getRandom().nextInt(20);
            VillagePlace villageplace = worldserver.getPoiManager();
            Predicate<BlockPosition> predicate = (blockposition) -> {
                long i = blockposition.asLong();

                if (this.batchCache.containsKey(i)) {
                    return false;
                } else if (++this.triedCount >= 5) {
                    return false;
                } else {
                    this.batchCache.put(i, this.lastUpdate + 40L);
                    return true;
                }
            };
            Stream<BlockPosition> stream = villageplace.findAll(VillagePlaceType.HOME.getPredicate(), predicate, entityinsentient.blockPosition(), 48, VillagePlace.Occupancy.ANY);
            PathEntity pathentity = entityinsentient.getNavigation().createPath(stream, VillagePlaceType.HOME.getValidRange());

            if (pathentity != null && pathentity.canReach()) {
                BlockPosition blockposition = pathentity.getTarget();
                Optional<VillagePlaceType> optional = villageplace.getType(blockposition);

                if (optional.isPresent()) {
                    entityinsentient.getBrain().setMemory(MemoryModuleType.NEAREST_BED, (Object) blockposition);
                }
            } else if (this.triedCount < 5) {
                this.batchCache.long2LongEntrySet().removeIf((entry) -> {
                    return entry.getLongValue() < this.lastUpdate;
                });
            }

        }
    }
}
