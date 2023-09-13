package net.minecraft.world.entity.ai.sensing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogAttackablesSensor extends NearestVisibleLivingEntitySensor {

    public static final float TARGET_DETECTION_DISTANCE = 10.0F;

    public FrogAttackablesSensor() {}

    @Override
    protected boolean isMatchingEntity(EntityLiving entityliving, EntityLiving entityliving1) {
        return !entityliving.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && Sensor.isEntityAttackable(entityliving, entityliving1) && Frog.canEat(entityliving1) && !this.isUnreachableAttackTarget(entityliving, entityliving1) ? entityliving1.closerThan(entityliving, 10.0D) : false;
    }

    private boolean isUnreachableAttackTarget(EntityLiving entityliving, EntityLiving entityliving1) {
        List<UUID> list = (List) entityliving.getBrain().getMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);

        return list.contains(entityliving1.getUUID());
    }

    @Override
    protected MemoryModuleType<EntityLiving> getMemory() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
