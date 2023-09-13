package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorLookInteract extends Behavior<EntityLiving> {

    private final EntityTypes<?> type;
    private final int interactionRangeSqr;
    private final Predicate<EntityLiving> targetFilter;
    private final Predicate<EntityLiving> selfFilter;

    public BehaviorLookInteract(EntityTypes<?> entitytypes, int i, Predicate<EntityLiving> predicate, Predicate<EntityLiving> predicate1) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.type = entitytypes;
        this.interactionRangeSqr = i * i;
        this.targetFilter = predicate1;
        this.selfFilter = predicate;
    }

    public BehaviorLookInteract(EntityTypes<?> entitytypes, int i) {
        this(entitytypes, i, (entityliving) -> {
            return true;
        }, (entityliving) -> {
            return true;
        });
    }

    @Override
    public boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        return this.selfFilter.test(entityliving) && this.getVisibleEntities(entityliving).contains(this::isMatchingTarget);
    }

    @Override
    public void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        super.start(worldserver, entityliving, i);
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((nearestvisiblelivingentities) -> {
            return nearestvisiblelivingentities.findClosest((entityliving1) -> {
                return entityliving1.distanceToSqr((Entity) entityliving) <= (double) this.interactionRangeSqr && this.isMatchingTarget(entityliving1);
            });
        }).ifPresent((entityliving1) -> {
            behaviorcontroller.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object) entityliving1);
            behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving1, true)));
        });
    }

    private boolean isMatchingTarget(EntityLiving entityliving) {
        return this.type.equals(entityliving.getType()) && this.targetFilter.test(entityliving);
    }

    private NearestVisibleLivingEntities getVisibleEntities(EntityLiving entityliving) {
        return (NearestVisibleLivingEntities) entityliving.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();
    }
}
