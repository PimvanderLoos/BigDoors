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

public class SensorNearestLivingEntities<T extends EntityLiving> extends Sensor<T> {

    public SensorNearestLivingEntities() {}

    @Override
    protected void doTick(WorldServer worldserver, T t0) {
        AxisAlignedBB axisalignedbb = t0.getBoundingBox().inflate((double) this.radiusXZ(), (double) this.radiusY(), (double) this.radiusXZ());
        List<EntityLiving> list = worldserver.getEntitiesOfClass(EntityLiving.class, axisalignedbb, (entityliving) -> {
            return entityliving != t0 && entityliving.isAlive();
        });

        Objects.requireNonNull(t0);
        list.sort(Comparator.comparingDouble(t0::distanceToSqr));
        BehaviorController<?> behaviorcontroller = t0.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, (Object) list);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, (Object) (new NearestVisibleLivingEntities(t0, list)));
    }

    protected int radiusXZ() {
        return 16;
    }

    protected int radiusY() {
        return 16;
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}
