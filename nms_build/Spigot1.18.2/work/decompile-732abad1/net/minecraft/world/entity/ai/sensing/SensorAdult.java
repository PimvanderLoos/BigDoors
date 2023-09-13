package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SensorAdult extends Sensor<EntityAgeable> {

    public SensorAdult() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    protected void doTick(WorldServer worldserver, EntityAgeable entityageable) {
        entityageable.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent((nearestvisiblelivingentities) -> {
            this.setNearestVisibleAdult(entityageable, nearestvisiblelivingentities);
        });
    }

    private void setNearestVisibleAdult(EntityAgeable entityageable, NearestVisibleLivingEntities nearestvisiblelivingentities) {
        Optional optional = nearestvisiblelivingentities.findClosest((entityliving) -> {
            return entityliving.getType() == entityageable.getType() && !entityliving.isBaby();
        });

        Objects.requireNonNull(EntityAgeable.class);
        Optional<EntityAgeable> optional1 = optional.map(EntityAgeable.class::cast);

        entityageable.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional1);
    }
}
