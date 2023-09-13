package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SensorAdult extends Sensor<EntityAgeable> {

    public SensorAdult() {}

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULY, MemoryModuleType.VISIBLE_MOBS);
    }

    protected void a(WorldServer worldserver, EntityAgeable entityageable) {
        entityageable.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((list) -> {
            this.a(entityageable, list);
        });
    }

    private void a(EntityAgeable entityageable, List<EntityLiving> list) {
        Optional<EntityAgeable> optional = list.stream().filter((entityliving) -> {
            return entityliving.getEntityType() == entityageable.getEntityType();
        }).map((entityliving) -> {
            return (EntityAgeable) entityliving;
        }).filter((entityageable1) -> {
            return !entityageable1.isBaby();
        }).findFirst();

        entityageable.getBehaviorController().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULY, optional);
    }
}
