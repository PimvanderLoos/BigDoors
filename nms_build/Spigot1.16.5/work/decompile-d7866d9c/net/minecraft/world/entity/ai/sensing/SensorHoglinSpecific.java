package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;

public class SensorHoglinSpecific extends Sensor<EntityHoglin> {

    public SensorHoglinSpecific() {}

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, new MemoryModuleType[0]);
    }

    protected void a(WorldServer worldserver, EntityHoglin entityhoglin) {
        BehaviorController<?> behaviorcontroller = entityhoglin.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_REPELLENT, this.b(worldserver, entityhoglin));
        Optional<EntityPiglin> optional = Optional.empty();
        int i = 0;
        List<EntityHoglin> list = Lists.newArrayList();
        List<EntityLiving> list1 = (List) behaviorcontroller.getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList());
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            EntityLiving entityliving = (EntityLiving) iterator.next();

            if (entityliving instanceof EntityPiglin && !entityliving.isBaby()) {
                ++i;
                if (!optional.isPresent()) {
                    optional = Optional.of((EntityPiglin) entityliving);
                }
            }

            if (entityliving instanceof EntityHoglin && !entityliving.isBaby()) {
                list.add((EntityHoglin) entityliving);
            }
        }

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, (Object) list);
        behaviorcontroller.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, (Object) i);
        behaviorcontroller.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, (Object) list.size());
    }

    private Optional<BlockPosition> b(WorldServer worldserver, EntityHoglin entityhoglin) {
        return BlockPosition.a(entityhoglin.getChunkCoordinates(), 8, 4, (blockposition) -> {
            return worldserver.getType(blockposition).a((Tag) TagsBlock.HOGLIN_REPELLENTS);
        });
    }
}
