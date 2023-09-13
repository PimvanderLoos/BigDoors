package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorStopAdmiringItem<E extends EntityPiglin> extends Behavior<E> {

    private final int maxDistanceToItem;

    public BehaviorStopAdmiringItem(int i) {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.REGISTERED));
        this.maxDistanceToItem = i;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        if (!e0.getOffhandItem().isEmpty()) {
            return false;
        } else {
            Optional<EntityItem> optional = e0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);

            return !optional.isPresent() ? true : !((EntityItem) optional.get()).closerThan(e0, (double) this.maxDistanceToItem);
        }
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        e0.getBrain().eraseMemory(MemoryModuleType.ADMIRING_ITEM);
    }
}
