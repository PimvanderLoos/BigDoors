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

    protected boolean a(WorldServer worldserver, E e0) {
        return !e0.getItemInOffHand().isEmpty() && e0.getItemInOffHand().getItem() != Items.SHIELD;
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        PiglinAI.a(e0, true);
    }
}
