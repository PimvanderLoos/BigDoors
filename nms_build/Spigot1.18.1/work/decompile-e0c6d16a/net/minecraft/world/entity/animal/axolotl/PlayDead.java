package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PlayDead extends Behavior<Axolotl> {

    public PlayDead() {
        super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT), 200);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, Axolotl axolotl) {
        return axolotl.isInWaterOrBubble();
    }

    protected boolean canStillUse(WorldServer worldserver, Axolotl axolotl, long i) {
        return axolotl.isInWaterOrBubble() && axolotl.getBrain().hasMemoryValue(MemoryModuleType.PLAY_DEAD_TICKS);
    }

    protected void start(WorldServer worldserver, Axolotl axolotl, long i) {
        BehaviorController<Axolotl> behaviorcontroller = axolotl.getBrain();

        behaviorcontroller.eraseMemory(MemoryModuleType.WALK_TARGET);
        behaviorcontroller.eraseMemory(MemoryModuleType.LOOK_TARGET);
        axolotl.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0));
    }
}
