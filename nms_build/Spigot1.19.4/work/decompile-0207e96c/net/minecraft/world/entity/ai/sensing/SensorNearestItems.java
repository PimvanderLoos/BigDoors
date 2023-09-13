package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.EntityItem;

public class SensorNearestItems extends Sensor<EntityInsentient> {

    private static final long XZ_RANGE = 32L;
    private static final long Y_RANGE = 16L;
    public static final int MAX_DISTANCE_TO_WANTED_ITEM = 32;

    public SensorNearestItems() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
    }

    protected void doTick(WorldServer worldserver, EntityInsentient entityinsentient) {
        BehaviorController<?> behaviorcontroller = entityinsentient.getBrain();
        List<EntityItem> list = worldserver.getEntitiesOfClass(EntityItem.class, entityinsentient.getBoundingBox().inflate(32.0D, 16.0D, 32.0D), (entityitem) -> {
            return true;
        });

        Objects.requireNonNull(entityinsentient);
        list.sort(Comparator.comparingDouble(entityinsentient::distanceToSqr));
        Stream stream = list.stream().filter((entityitem) -> {
            return entityinsentient.wantsToPickUp(entityitem.getItem());
        }).filter((entityitem) -> {
            return entityitem.closerThan(entityinsentient, 32.0D);
        });

        Objects.requireNonNull(entityinsentient);
        Optional<EntityItem> optional = stream.filter(entityinsentient::hasLineOfSight).findFirst();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
    }
}
