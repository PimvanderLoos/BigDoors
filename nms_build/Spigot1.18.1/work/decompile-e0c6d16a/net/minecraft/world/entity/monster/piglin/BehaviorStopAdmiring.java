package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Items;

public class BehaviorStopAdmiring<E extends EntityPiglin> extends Behavior<E> {

    public BehaviorStopAdmiring() {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT));
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return !e0.getOffhandItem().isEmpty() && !e0.getOffhandItem().is(Items.SHIELD);
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        PiglinAI.stopHoldingOffHandItem(e0, true);
    }
}
