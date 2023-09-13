package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SensorVillagerHostiles extends NearestVisibleLivingEntitySensor {

    private static final ImmutableMap<EntityTypes<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.builder().put(EntityTypes.DROWNED, 8.0F).put(EntityTypes.EVOKER, 12.0F).put(EntityTypes.HUSK, 8.0F).put(EntityTypes.ILLUSIONER, 12.0F).put(EntityTypes.PILLAGER, 15.0F).put(EntityTypes.RAVAGER, 12.0F).put(EntityTypes.VEX, 8.0F).put(EntityTypes.VINDICATOR, 10.0F).put(EntityTypes.ZOGLIN, 10.0F).put(EntityTypes.ZOMBIE, 8.0F).put(EntityTypes.ZOMBIE_VILLAGER, 8.0F).build();

    public SensorVillagerHostiles() {}

    @Override
    protected boolean isMatchingEntity(EntityLiving entityliving, EntityLiving entityliving1) {
        return this.isHostile(entityliving1) && this.isClose(entityliving, entityliving1);
    }

    private boolean isClose(EntityLiving entityliving, EntityLiving entityliving1) {
        float f = (Float) SensorVillagerHostiles.ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(entityliving1.getType());

        return entityliving1.distanceToSqr((Entity) entityliving) <= (double) (f * f);
    }

    @Override
    protected MemoryModuleType<EntityLiving> getMemory() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    private boolean isHostile(EntityLiving entityliving) {
        return SensorVillagerHostiles.ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(entityliving.getType());
    }
}
