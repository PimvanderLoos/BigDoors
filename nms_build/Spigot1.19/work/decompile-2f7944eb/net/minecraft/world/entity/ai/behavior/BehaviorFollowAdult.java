package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorFollowAdult<E extends EntityAgeable> extends Behavior<E> {

    private final UniformInt followRange;
    private final Function<EntityLiving, Float> speedModifier;

    public BehaviorFollowAdult(UniformInt uniformint, float f) {
        this(uniformint, (entityliving) -> {
            return f;
        });
    }

    public BehaviorFollowAdult(UniformInt uniformint, Function<EntityLiving, Float> function) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.followRange = uniformint;
        this.speedModifier = function;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        if (!e0.isBaby()) {
            return false;
        } else {
            EntityAgeable entityageable = this.getNearestAdult(e0);

            return e0.closerThan(entityageable, (double) (this.followRange.getMaxValue() + 1)) && !e0.closerThan(entityageable, (double) this.followRange.getMinValue());
        }
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.setWalkAndLookTargetMemories(e0, (Entity) this.getNearestAdult(e0), (Float) this.speedModifier.apply(e0), this.followRange.getMinValue() - 1);
    }

    private EntityAgeable getNearestAdult(E e0) {
        return (EntityAgeable) e0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}
