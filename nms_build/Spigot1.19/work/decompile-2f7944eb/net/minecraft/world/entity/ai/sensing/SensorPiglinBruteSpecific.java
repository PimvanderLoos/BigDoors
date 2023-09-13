package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.EntitySkeletonWither;
import net.minecraft.world.entity.monster.piglin.EntityPiglinAbstract;

public class SensorPiglinBruteSpecific extends Sensor<EntityLiving> {

    public SensorPiglinBruteSpecific() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
    }

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        List<EntityPiglinAbstract> list = Lists.newArrayList();
        NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
        Optional optional = nearestvisiblelivingentities.findClosest((entityliving1) -> {
            return entityliving1 instanceof EntitySkeletonWither || entityliving1 instanceof EntityWither;
        });

        Objects.requireNonNull(EntityInsentient.class);
        Optional<EntityInsentient> optional1 = optional.map(EntityInsentient.class::cast);
        List<EntityLiving> list1 = (List) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of());
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            EntityLiving entityliving1 = (EntityLiving) iterator.next();

            if (entityliving1 instanceof EntityPiglinAbstract && ((EntityPiglinAbstract) entityliving1).isAdult()) {
                list.add((EntityPiglinAbstract) entityliving1);
            }
        }

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional1);
        behaviorcontroller.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, (Object) list);
    }
}
