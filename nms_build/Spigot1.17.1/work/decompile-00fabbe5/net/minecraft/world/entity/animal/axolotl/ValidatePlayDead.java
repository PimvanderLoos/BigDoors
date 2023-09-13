package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class ValidatePlayDead extends Behavior<Axolotl> {

    public ValidatePlayDead() {
        super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT));
    }

    protected void a(WorldServer worldserver, Axolotl axolotl, long i) {
        BehaviorController<Axolotl> behaviorcontroller = axolotl.getBehaviorController();
        int j = (Integer) behaviorcontroller.getMemory(MemoryModuleType.PLAY_DEAD_TICKS).get();

        if (j <= 0) {
            behaviorcontroller.removeMemory(MemoryModuleType.PLAY_DEAD_TICKS);
            behaviorcontroller.removeMemory(MemoryModuleType.HURT_BY_ENTITY);
            behaviorcontroller.e();
        } else {
            behaviorcontroller.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, (Object) (j - 1));
        }

    }
}
