package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SensorHurtBy extends Sensor<EntityLiving> {

    public SensorHurtBy() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
    }

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        DamageSource damagesource = entityliving.getLastDamageSource();

        if (damagesource != null) {
            behaviorcontroller.setMemory(MemoryModuleType.HURT_BY, (Object) entityliving.getLastDamageSource());
            Entity entity = damagesource.getEntity();

            if (entity instanceof EntityLiving) {
                behaviorcontroller.setMemory(MemoryModuleType.HURT_BY_ENTITY, (Object) ((EntityLiving) entity));
            }
        } else {
            behaviorcontroller.eraseMemory(MemoryModuleType.HURT_BY);
        }

        behaviorcontroller.getMemory(MemoryModuleType.HURT_BY_ENTITY).ifPresent((entityliving1) -> {
            if (!entityliving1.isAlive() || entityliving1.level != worldserver) {
                behaviorcontroller.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
            }

        });
    }
}
