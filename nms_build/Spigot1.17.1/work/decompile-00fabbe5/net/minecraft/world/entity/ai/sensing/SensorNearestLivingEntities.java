package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.AxisAlignedBB;

public class SensorNearestLivingEntities extends Sensor<EntityLiving> {

    public SensorNearestLivingEntities() {}

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {
        AxisAlignedBB axisalignedbb = entityliving.getBoundingBox().grow(16.0D, 16.0D, 16.0D);
        List<EntityLiving> list = worldserver.a(EntityLiving.class, axisalignedbb, (entityliving1) -> {
            return entityliving1 != entityliving && entityliving1.isAlive();
        });

        Objects.requireNonNull(entityliving);
        list.sort(Comparator.comparingDouble(entityliving::f));
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, (Object) list);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, (Object) ((List) list.stream().filter((entityliving1) -> {
            return b(entityliving, entityliving1);
        }).collect(Collectors.toList())));
    }

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}
