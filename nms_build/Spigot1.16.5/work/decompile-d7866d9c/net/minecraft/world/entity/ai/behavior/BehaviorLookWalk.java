package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorLookWalk extends Behavior<EntityLiving> {

    private final float b;
    private final int c;

    public BehaviorLookWalk(float f, int i) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT));
        this.b = f;
        this.c = i;
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        BehaviorPosition behaviorposition = (BehaviorPosition) behaviorcontroller.getMemory(MemoryModuleType.LOOK_TARGET).get();

        behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(behaviorposition, this.b, this.c)));
    }
}
