package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SensorVillagerBabies extends Sensor<EntityLiving> {

    public SensorVillagerBabies() {}

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {
        entityliving.getBehaviorController().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object) this.a(entityliving));
    }

    private List<EntityLiving> a(EntityLiving entityliving) {
        return (List) this.c(entityliving).stream().filter(this::b).collect(Collectors.toList());
    }

    private boolean b(EntityLiving entityliving) {
        return entityliving.getEntityType() == EntityTypes.VILLAGER && entityliving.isBaby();
    }

    private List<EntityLiving> c(EntityLiving entityliving) {
        return (List) entityliving.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList());
    }
}
