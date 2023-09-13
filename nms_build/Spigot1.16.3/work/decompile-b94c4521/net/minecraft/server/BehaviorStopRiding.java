package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;

public class BehaviorStopRiding<E extends EntityLiving, T extends Entity> extends Behavior<E> {

    private final int b;
    private final BiPredicate<E, Entity> c;

    public BehaviorStopRiding(int i, BiPredicate<E, Entity> bipredicate) {
        super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryStatus.REGISTERED));
        this.b = i;
        this.c = bipredicate;
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        Entity entity = e0.getVehicle();
        Entity entity1 = (Entity) e0.getBehaviorController().getMemory(MemoryModuleType.RIDE_TARGET).orElse((Object) null);

        if (entity == null && entity1 == null) {
            return false;
        } else {
            Entity entity2 = entity == null ? entity1 : entity;

            return !this.a(e0, entity2) || this.c.test(e0, entity2);
        }
    }

    private boolean a(E e0, Entity entity) {
        return entity.isAlive() && entity.a((Entity) e0, (double) this.b) && entity.world == e0.world;
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        e0.stopRiding();
        e0.getBehaviorController().removeMemory(MemoryModuleType.RIDE_TARGET);
    }
}
