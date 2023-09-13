package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorFollowAdult<E extends EntityAgeable> extends Behavior<E> {

    private final IntRange b;
    private final float c;

    public BehaviorFollowAdult(IntRange intrange, float f) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULY, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.b = intrange;
        this.c = f;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        if (!e0.isBaby()) {
            return false;
        } else {
            EntityAgeable entityageable = this.a(e0);

            return e0.a((Entity) entityageable, (double) (this.b.b() + 1)) && !e0.a((Entity) entityageable, (double) this.b.a());
        }
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.a(e0, (Entity) this.a(e0), this.c, this.b.a() - 1);
    }

    private EntityAgeable a(E e0) {
        return (EntityAgeable) e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULY).get();
    }
}
