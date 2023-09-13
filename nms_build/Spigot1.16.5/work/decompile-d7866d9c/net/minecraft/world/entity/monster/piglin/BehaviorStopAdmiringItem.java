package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorStopAdmiringItem<E extends EntityPiglin> extends Behavior<E> {

    private final int b;

    public BehaviorStopAdmiringItem(int i) {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.REGISTERED));
        this.b = i;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        if (!e0.getItemInOffHand().isEmpty()) {
            return false;
        } else {
            Optional<EntityItem> optional = e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);

            return !optional.isPresent() ? true : !((EntityItem) optional.get()).a((Entity) e0, (double) this.b);
        }
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        e0.getBehaviorController().removeMemory(MemoryModuleType.ADMIRING_ITEM);
    }
}
