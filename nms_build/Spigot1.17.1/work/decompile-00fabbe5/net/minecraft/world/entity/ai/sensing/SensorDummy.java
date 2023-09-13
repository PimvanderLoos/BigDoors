package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SensorDummy extends Sensor<EntityLiving> {

    public SensorDummy() {}

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {}

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of();
    }
}
