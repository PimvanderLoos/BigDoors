package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StayCloseToTarget<E extends EntityLiving> extends Behavior<E> {

    private final Function<EntityLiving, Optional<BehaviorPosition>> targetPositionGetter;
    private final int closeEnough;
    private final int tooFar;
    private final float speedModifier;

    public StayCloseToTarget(Function<EntityLiving, Optional<BehaviorPosition>> function, int i, int j, float f) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.targetPositionGetter = function;
        this.closeEnough = i;
        this.tooFar = j;
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        Optional<BehaviorPosition> optional = (Optional) this.targetPositionGetter.apply(e0);

        if (optional.isEmpty()) {
            return false;
        } else {
            BehaviorPosition behaviorposition = (BehaviorPosition) optional.get();

            return !e0.position().closerThan(behaviorposition.currentPosition(), (double) this.tooFar);
        }
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.setWalkAndLookTargetMemories(e0, (BehaviorPosition) ((Optional) this.targetPositionGetter.apply(e0)).get(), this.speedModifier, this.closeEnough);
    }
}
