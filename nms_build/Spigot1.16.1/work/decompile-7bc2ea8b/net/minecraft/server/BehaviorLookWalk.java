package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

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
