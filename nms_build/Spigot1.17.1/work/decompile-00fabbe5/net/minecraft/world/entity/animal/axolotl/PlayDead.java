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

    protected boolean a(WorldServer worldserver, Axolotl axolotl) {
        return axolotl.aO();
    }

    protected boolean b(WorldServer worldserver, Axolotl axolotl, long i) {
        return axolotl.aO() && axolotl.getBehaviorController().hasMemory(MemoryModuleType.PLAY_DEAD_TICKS);
    }

    protected void a(WorldServer worldserver, Axolotl axolotl, long i) {
        BehaviorController<Axolotl> behaviorcontroller = axolotl.getBehaviorController();

        behaviorcontroller.removeMemory(MemoryModuleType.WALK_TARGET);
        behaviorcontroller.removeMemory(MemoryModuleType.LOOK_TARGET);
        axolotl.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0));
    }
}
