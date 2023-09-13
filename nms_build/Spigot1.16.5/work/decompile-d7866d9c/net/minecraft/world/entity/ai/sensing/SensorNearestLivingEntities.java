package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
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

        entityliving.getClass();
        list.sort(Comparator.comparingDouble(entityliving::h));
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.MOBS, (Object) list);
        behaviorcontroller.setMemory(MemoryModuleType.VISIBLE_MOBS, list.stream().filter((entityliving1) -> {
            return a(entityliving, entityliving1);
        }).collect(Collectors.toList()));
    }

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
    }
}
