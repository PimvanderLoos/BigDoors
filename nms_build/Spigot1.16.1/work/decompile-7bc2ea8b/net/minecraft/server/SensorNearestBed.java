package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SensorNearestBed extends Sensor<EntityInsentient> {

    private final Long2LongMap a = new Long2LongOpenHashMap();
    private int b;
    private long c;

    public SensorNearestBed() {
        super(20);
    }

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }

    protected void a(WorldServer worldserver, EntityInsentient entityinsentient) {
        if (entityinsentient.isBaby()) {
            this.b = 0;
            this.c = worldserver.getTime() + (long) worldserver.getRandom().nextInt(20);
            VillagePlace villageplace = worldserver.x();
            Predicate<BlockPosition> predicate = (blockposition) -> {
                long i = blockposition.asLong();

                if (this.a.containsKey(i)) {
                    return false;
                } else if (++this.b >= 5) {
                    return false;
                } else {
                    this.a.put(i, this.c + 40L);
                    return true;
                }
            };
            Stream<BlockPosition> stream = villageplace.a(VillagePlaceType.r.c(), predicate, entityinsentient.getChunkCoordinates(), 48, VillagePlace.Occupancy.ANY);
            PathEntity pathentity = entityinsentient.getNavigation().a(stream, VillagePlaceType.r.d());

            if (pathentity != null && pathentity.i()) {
                BlockPosition blockposition = pathentity.m();
                Optional<VillagePlaceType> optional = villageplace.c(blockposition);

                if (optional.isPresent()) {
                    entityinsentient.getBehaviorController().setMemory(MemoryModuleType.NEAREST_BED, (Object) blockposition);
                }
            } else if (this.b < 5) {
                this.a.long2LongEntrySet().removeIf((entry) -> {
                    return entry.getLongValue() < this.c;
                });
            }

        }
    }
}
