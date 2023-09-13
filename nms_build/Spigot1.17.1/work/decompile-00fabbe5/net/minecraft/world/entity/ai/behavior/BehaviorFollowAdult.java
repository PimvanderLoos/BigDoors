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

    protected boolean a(WorldServer worldserver, E e0) {
        if (!e0.isBaby()) {
            return false;
        } else {
            EntityAgeable entityageable = this.a(e0);

            return e0.a((Entity) entityageable, (double) (this.followRange.b() + 1)) && !e0.a((Entity) entityageable, (double) this.followRange.a());
        }
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.a(e0, (Entity) this.a(e0), (Float) this.speedModifier.apply(e0), this.followRange.a() - 1);
    }

    private EntityAgeable a(E e0) {
        return (EntityAgeable) e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}
