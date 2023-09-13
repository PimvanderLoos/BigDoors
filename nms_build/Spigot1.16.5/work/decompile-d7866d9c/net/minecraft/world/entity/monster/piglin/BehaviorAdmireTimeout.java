package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorAdmireTimeout<E extends EntityPiglin> extends Behavior<E> {

    private final int b;
    private final int c;

    public BehaviorAdmireTimeout(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryStatus.REGISTERED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.REGISTERED));
        this.b = i;
        this.c = j;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        return e0.getItemInOffHand().isEmpty();
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorController<EntityPiglin> behaviorcontroller = e0.getBehaviorController();
        Optional<Integer> optional = behaviorcontroller.getMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);

        if (!optional.isPresent()) {
            behaviorcontroller.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, (int) 0);
        } else {
            int j = (Integer) optional.get();

            if (j > this.b) {
                behaviorcontroller.removeMemory(MemoryModuleType.ADMIRING_ITEM);
                behaviorcontroller.removeMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
                behaviorcontroller.a(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, (long) this.c);
            } else {
                behaviorcontroller.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, (Object) (j + 1));
            }
        }

    }
}
