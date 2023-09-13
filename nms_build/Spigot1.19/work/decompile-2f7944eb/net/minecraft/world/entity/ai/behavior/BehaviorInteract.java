package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

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

    public static <T extends EntityLiving> BehaviorInteract<EntityLiving, T> of(EntityTypes<? extends T> entitytypes, int i, MemoryModuleType<T> memorymoduletype, float f, int j) {
        return new BehaviorInteract<>(entitytypes, i, (entityliving) -> {
            return true;
        }, (entityliving) -> {
            return true;
        }, memorymoduletype, f, j);
    }

    public static <T extends EntityLiving> BehaviorInteract<EntityLiving, T> of(EntityTypes<? extends T> entitytypes, int i, Predicate<T> predicate, MemoryModuleType<T> memorymoduletype, float f, int j) {
        return new BehaviorInteract<>(entitytypes, i, (entityliving) -> {
            return true;
        }, predicate, memorymoduletype, f, j);
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return this.selfFilter.test(e0) && this.seesAtLeastOneValidTarget(e0);
    }

    private boolean seesAtLeastOneValidTarget(E e0) {
        NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) e0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();

        return nearestvisiblelivingentities.contains(this::isTargetValid);
    }

    private boolean isTargetValid(EntityLiving entityliving) {
        return this.type.equals(entityliving.getType()) && this.targetFilter.test(entityliving);
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        BehaviorController<?> behaviorcontroller = e0.getBrain();
        Optional<NearestVisibleLivingEntities> optional = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

        if (!optional.isEmpty()) {
            NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) optional.get();

            nearestvisiblelivingentities.findClosest((entityliving) -> {
                return this.canInteract(e0, entityliving);
            }).ifPresent((entityliving) -> {
                behaviorcontroller.setMemory(this.memory, (Object) entityliving);
                behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityliving, false), this.speedModifier, this.maxDist)));
            });
        }
    }

    private boolean canInteract(E e0, EntityLiving entityliving) {
        return this.type.equals(entityliving.getType()) && entityliving.distanceToSqr((Entity) e0) <= (double) this.interactionRangeSqr && this.targetFilter.test(entityliving);
    }
}
