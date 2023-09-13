package net.minecraft.world.entity.ai.sensing;

import net.minecraft.tags.TagsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AxolotlAttackablesSensor extends NearestVisibleLivingEntitySensor {

    public static final float TARGET_DETECTION_DISTANCE = 8.0F;

    public AxolotlAttackablesSensor() {}

    @Override
    protected boolean isMatchingEntity(EntityLiving entityliving, EntityLiving entityliving1) {
        return this.isClose(entityliving, entityliving1) && entityliving1.isInWaterOrBubble() && (this.isHostileTarget(entityliving1) || this.isHuntTarget(entityliving, entityliving1)) && Sensor.isEntityAttackable(entityliving, entityliving1);
    }

    private boolean isHuntTarget(EntityLiving entityliving, EntityLiving entityliving1) {
        return !entityliving.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && TagsEntity.AXOLOTL_HUNT_TARGETS.contains(entityliving1.getType());
    }

    private boolean isHostileTarget(EntityLiving entityliving) {
        return TagsEntity.AXOLOTL_ALWAYS_HOSTILES.contains(entityliving.getType());
    }

    private boolean isClose(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving1.distanceToSqr((Entity) entityliving) <= 64.0D;
    }

    @Override
    protected MemoryModuleType<EntityLiving> getMemory() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
