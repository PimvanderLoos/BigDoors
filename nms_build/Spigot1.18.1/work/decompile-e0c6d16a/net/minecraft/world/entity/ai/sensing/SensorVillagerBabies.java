package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SensorVillagerBabies extends Sensor<EntityLiving> {

    public SensorVillagerBabies() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        entityliving.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object) this.getNearestVillagerBabies(entityliving));
    }

    private List<EntityLiving> getNearestVillagerBabies(EntityLiving entityliving) {
        return ImmutableList.copyOf(this.getVisibleEntities(entityliving).findAll(this::isVillagerBaby));
    }

    private boolean isVillagerBaby(EntityLiving entityliving) {
        return entityliving.getType() == EntityTypes.VILLAGER && entityliving.isBaby();
    }

    private NearestVisibleLivingEntities getVisibleEntities(EntityLiving entityliving) {
        return (NearestVisibleLivingEntities) entityliving.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
    }
}
