package net.minecraft.world.entity.ai.sensing;

import net.minecraft.tags.TagsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AxolotlAttackablesSensor extends NearestVisibleLivingEntitySensor {

    public static final float TARGET_DETECTION_DISTANCE = 8.0F;

    public AxolotlAttackablesSensor() {}

    @Override
    protected boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return Sensor.c(entityliving, entityliving1) && (this.b(entityliving1) || this.e(entityliving, entityliving1)) ? this.f(entityliving, entityliving1) && entityliving1.aO() : false;
    }

    private boolean e(EntityLiving entityliving, EntityLiving entityliving1) {
        return !entityliving.getBehaviorController().hasMemory(MemoryModuleType.HAS_HUNTING_COOLDOWN) && TagsEntity.AXOLOTL_HUNT_TARGETS.isTagged(entityliving1.getEntityType());
    }

    private boolean b(EntityLiving entityliving) {
        return TagsEntity.AXOLOTL_ALWAYS_HOSTILES.isTagged(entityliving.getEntityType());
    }

    private boolean f(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving1.f((Entity) entityliving) <= 64.0D;
    }

    @Override
    protected MemoryModuleType<EntityLiving> b() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
