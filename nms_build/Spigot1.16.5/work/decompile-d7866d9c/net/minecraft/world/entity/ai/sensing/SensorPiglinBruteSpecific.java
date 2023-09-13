package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.EntitySkeletonWither;
import net.minecraft.world.entity.monster.piglin.EntityPiglinAbstract;

public class SensorPiglinBruteSpecific extends Sensor<EntityLiving> {

    public SensorPiglinBruteSpecific() {}

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMSIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        Optional<EntityInsentient> optional = Optional.empty();
        List<EntityPiglinAbstract> list = Lists.newArrayList();
        List<EntityLiving> list1 = (List) behaviorcontroller.getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of());
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            EntityLiving entityliving1 = (EntityLiving) iterator.next();

            if (entityliving1 instanceof EntitySkeletonWither || entityliving1 instanceof EntityWither) {
                optional = Optional.of((EntityInsentient) entityliving1);
                break;
            }
        }

        List<EntityLiving> list2 = (List) behaviorcontroller.getMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of());
        Iterator iterator1 = list2.iterator();

        while (iterator1.hasNext()) {
            EntityLiving entityliving2 = (EntityLiving) iterator1.next();

            if (entityliving2 instanceof EntityPiglinAbstract && ((EntityPiglinAbstract) entityliving2).eM()) {
                list.add((EntityPiglinAbstract) entityliving2);
            }
        }

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMSIS, optional);
        behaviorcontroller.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, (Object) list);
    }
}
