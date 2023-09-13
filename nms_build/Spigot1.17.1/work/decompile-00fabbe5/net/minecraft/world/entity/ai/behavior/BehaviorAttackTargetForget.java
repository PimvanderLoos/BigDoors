package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorAttackTargetForget<E extends EntityInsentient> extends Behavior<E> {

    private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;
    private final Predicate<EntityLiving> stopAttackingWhen;
    private final Consumer<E> onTargetErased;

    public BehaviorAttackTargetForget(Predicate<EntityLiving> predicate, Consumer<E> consumer) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
        this.stopAttackingWhen = predicate;
        this.onTargetErased = consumer;
    }

    public BehaviorAttackTargetForget(Predicate<EntityLiving> predicate) {
        this(predicate, (entityinsentient) -> {
        });
    }

    public BehaviorAttackTargetForget(Consumer<E> consumer) {
        this((entityliving) -> {
            return false;
        }, consumer);
    }

    public BehaviorAttackTargetForget() {
        this((entityliving) -> {
            return false;
        }, (entityinsentient) -> {
        });
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        EntityLiving entityliving = this.c(e0);

        if (!e0.c(entityliving)) {
            this.a(e0);
        } else if (a((EntityLiving) e0)) {
            this.a(e0);
        } else if (this.d(e0)) {
            this.a(e0);
        } else if (this.b(e0)) {
            this.a(e0);
        } else if (this.stopAttackingWhen.test(this.c(e0))) {
            this.a(e0);
        }
    }

    private boolean b(E e0) {
        return this.c(e0).level != e0.level;
    }

    private EntityLiving c(E e0) {
        return (EntityLiving) e0.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends EntityLiving> boolean a(E e0) {
        Optional<Long> optional = e0.getBehaviorController().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

        return optional.isPresent() && e0.level.getTime() - (Long) optional.get() > 200L;
    }

    private boolean d(E e0) {
        Optional<EntityLiving> optional = e0.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET);

        return optional.isPresent() && !((EntityLiving) optional.get()).isAlive();
    }

    protected void a(E e0) {
        this.onTargetErased.accept(e0);
        e0.getBehaviorController().removeMemory(MemoryModuleType.ATTACK_TARGET);
    }
}
