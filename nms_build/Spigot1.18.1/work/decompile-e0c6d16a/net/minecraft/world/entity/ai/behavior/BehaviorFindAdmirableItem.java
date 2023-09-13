package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorFindAdmirableItem<E extends EntityLiving> extends Behavior<E> {

    private final Predicate<E> predicate;
    private final int maxDistToWalk;
    private final float speedModifier;

    public BehaviorFindAdmirableItem(float f, boolean flag, int i) {
        this((entityliving) -> {
            return true;
        }, f, flag, i);
    }

    public BehaviorFindAdmirableItem(Predicate<E> predicate, float f, boolean flag, int i) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, flag ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT));
        this.predicate = predicate;
        this.maxDistToWalk = i;
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return this.predicate.test(e0) && this.getClosestLovedItem(e0).closerThan(e0, (double) this.maxDistToWalk);
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.setWalkAndLookTargetMemories(e0, (Entity) this.getClosestLovedItem(e0), this.speedModifier, 0);
    }

    private EntityItem getClosestLovedItem(E e0) {
        return (EntityItem) e0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}
