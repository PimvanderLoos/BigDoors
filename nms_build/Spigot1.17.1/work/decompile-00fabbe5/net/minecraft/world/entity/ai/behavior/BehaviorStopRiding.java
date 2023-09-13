package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorStopRiding<E extends EntityLiving, T extends Entity> extends Behavior<E> {

    private final int maxWalkDistToRideTarget;
    private final BiPredicate<E, Entity> dontRideIf;

    public BehaviorStopRiding(int i, BiPredicate<E, Entity> bipredicate) {
        super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryStatus.REGISTERED));
        this.maxWalkDistToRideTarget = i;
        this.dontRideIf = bipredicate;
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        Entity entity = e0.getVehicle();
        Entity entity1 = (Entity) e0.getBehaviorController().getMemory(MemoryModuleType.RIDE_TARGET).orElse((Object) null);

        if (entity == null && entity1 == null) {
            return false;
        } else {
            Entity entity2 = entity == null ? entity1 : entity;

            return !this.a(e0, entity2) || this.dontRideIf.test(e0, entity2);
        }
    }

    private boolean a(E e0, Entity entity) {
        return entity.isAlive() && entity.a((Entity) e0, (double) this.maxWalkDistToRideTarget) && entity.level == e0.level;
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        e0.stopRiding();
        e0.getBehaviorController().removeMemory(MemoryModuleType.RIDE_TARGET);
    }
}
