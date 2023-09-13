package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class SensorHurtBy extends Sensor<EntityLiving> {

    public SensorHurtBy() {}

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        DamageSource damagesource = entityliving.dl();

        if (damagesource != null) {
            behaviorcontroller.setMemory(MemoryModuleType.HURT_BY, (Object) entityliving.dl());
            Entity entity = damagesource.getEntity();

            if (entity instanceof EntityLiving) {
                behaviorcontroller.setMemory(MemoryModuleType.HURT_BY_ENTITY, (Object) ((EntityLiving) entity));
            }
        } else {
            behaviorcontroller.removeMemory(MemoryModuleType.HURT_BY);
        }

        behaviorcontroller.getMemory(MemoryModuleType.HURT_BY_ENTITY).ifPresent((entityliving1) -> {
            if (!entityliving1.isAlive() || entityliving1.world != worldserver) {
                behaviorcontroller.removeMemory(MemoryModuleType.HURT_BY_ENTITY);
            }

        });
    }
}
