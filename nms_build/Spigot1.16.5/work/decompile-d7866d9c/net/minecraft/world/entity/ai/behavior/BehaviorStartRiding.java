package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorStartRiding<E extends EntityLiving> extends Behavior<E> {

    private final float b;

    public BehaviorStartRiding(float f) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RIDE_TARGET, MemoryStatus.VALUE_PRESENT));
        this.b = f;
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        return !e0.isPassenger();
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        if (this.a(e0)) {
            e0.startRiding(this.b(e0));
        } else {
            BehaviorUtil.a(e0, this.b(e0), this.b, 1);
        }

    }

    private boolean a(E e0) {
        return this.b(e0).a((Entity) e0, 1.0D);
    }

    private Entity b(E e0) {
        return (Entity) e0.getBehaviorController().getMemory(MemoryModuleType.RIDE_TARGET).get();
    }
}
