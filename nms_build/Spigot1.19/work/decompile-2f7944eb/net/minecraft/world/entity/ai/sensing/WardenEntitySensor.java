package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenEntitySensor extends SensorNearestLivingEntities<Warden> {

    public WardenEntitySensor() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void doTick(WorldServer worldserver, Warden warden) {
        super.doTick(worldserver, warden);
        getClosest(warden, (entityliving) -> {
            return entityliving.getType() == EntityTypes.PLAYER;
        }).or(() -> {
            return getClosest(warden, (entityliving) -> {
                return entityliving.getType() != EntityTypes.PLAYER;
            });
        }).ifPresentOrElse((entityliving) -> {
            warden.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, (Object) entityliving);
        }, () -> {
            warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        });
    }

    private static Optional<EntityLiving> getClosest(Warden warden, Predicate<EntityLiving> predicate) {
        Stream stream = warden.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream);

        Objects.requireNonNull(warden);
        return stream.filter(warden::canTargetEntity).filter(predicate).findFirst();
    }

    @Override
    protected int radiusXZ() {
        return 24;
    }

    @Override
    protected int radiusY() {
        return 24;
    }
}
