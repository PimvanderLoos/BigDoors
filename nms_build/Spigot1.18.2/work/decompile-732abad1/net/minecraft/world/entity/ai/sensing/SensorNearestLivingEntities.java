package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.AxisAlignedBB;

public class SensorNearestLivingEntities extends Sensor<EntityLiving> {

    public SensorNearestLivingEntities() {}

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        AxisAlignedBB axisalignedbb = entityliving.getBoundingBox().inflate(16.0D, 16.0D, 16.0D);
        List<EntityLiving> list = worldserver.getEntitiesOfClass(EntityLiving.class, axisalignedbb, (entityliving1) -> {
            return entityliving1 != entityliving && entityliving1.isAlive();
        });

        Objects.requireNonNull(entityliving);
        list.sort(Comparator.comparingDouble(entityliving::distanceToSqr));
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, (Object) list);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, (Object) (new NearestVisibleLivingEntities(entityliving, list)));
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}
