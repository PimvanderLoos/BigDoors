package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorInteract<E extends EntityLiving, T extends EntityLiving> extends Behavior<E> {

    private final int maxDist;
    private final float speedModifier;
    private final EntityTypes<? extends T> type;
    private final int interactionRangeSqr;
    private final Predicate<T> targetFilter;
    private final Predicate<E> selfFilter;
    private final MemoryModuleType<T> memory;

    public BehaviorInteract(EntityTypes<? extends T> entitytypes, int i, Predicate<E> predicate, Predicate<T> predicate1, MemoryModuleType<T> memorymoduletype, float f, int j) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.type = entitytypes;
        this.speedModifier = f;
        this.interactionRangeSqr = i * i;
        this.maxDist = j;
        this.targetFilter = predicate1;
        this.selfFilter = predicate;
        this.memory = memorymoduletype;
    }

    public static <T extends EntityLiving> BehaviorInteract<EntityLiving, T> a(EntityTypes<? extends T> entitytypes, int i, MemoryModuleType<T> memorymoduletype, float f, int j) {
        return new BehaviorInteract<>(entitytypes, i, (entityliving) -> {
            return true;
        }, (entityliving) -> {
            return true;
        }, memorymoduletype, f, j);
    }

    public static <T extends EntityLiving> BehaviorInteract<EntityLiving, T> a(EntityTypes<? extends T> entitytypes, int i, Predicate<T> predicate, MemoryModuleType<T> memorymoduletype, float f, int j) {
        return new BehaviorInteract<>(entitytypes, i, (entityliving) -> {
            return true;
        }, predicate, memorymoduletype, f, j);
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        return this.selfFilter.test(e0) && this.a(e0);
    }

    private boolean a(E e0) {
        List<EntityLiving> list = (List) e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();

        return list.stream().anyMatch(this::b);
    }

    private boolean b(EntityLiving entityliving) {
        return this.type.equals(entityliving.getEntityType()) && this.targetFilter.test(entityliving);
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorController<?> behaviorcontroller = e0.getBehaviorController();

        behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent((list) -> {
            list.stream().filter((entityliving) -> {
                return this.type.equals(entityliving.getEntityType());
            }).map((entityliving) -> {
                return entityliving;
            }).filter((entityliving) -> {
                return entityliving.f((Entity) e0) <= (double) this.interactionRangeSqr;
            }).filter(this.targetFilter).findFirst().ifPresent((entityliving) -> {
                behaviorcontroller.setMemory(this.memory, (Object) entityliving);
                behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityliving, false), this.speedModifier, this.maxDist)));
            });
        });
    }
}
