package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class IsInWaterSensor extends Sensor<EntityLiving> {

    public IsInWaterSensor() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.IS_IN_WATER);
    }

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        if (entityliving.isInWater()) {
            entityliving.getBrain().setMemory(MemoryModuleType.IS_IN_WATER, (Object) Unit.INSTANCE);
        } else {
            entityliving.getBrain().eraseMemory(MemoryModuleType.IS_IN_WATER);
        }

    }
}
