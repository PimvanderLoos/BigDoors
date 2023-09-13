package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.animal.EntityAnimal;

public class BehaviorMakeLoveAnimal extends Behavior<EntityAnimal> {

    private static final int BREED_RANGE = 3;
    private static final int MIN_DURATION = 60;
    private static final int MAX_DURATION = 110;
    private final EntityTypes<? extends EntityAnimal> partnerType;
    private final float speedModifier;
    private long spawnChildAtTime;

    public BehaviorMakeLoveAnimal(EntityTypes<? extends EntityAnimal> entitytypes, float f) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), 110);
        this.partnerType = entitytypes;
        this.speedModifier = f;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityAnimal entityanimal) {
        return entityanimal.isInLove() && this.findValidBreedPartner(entityanimal).isPresent();
    }

    protected void start(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        EntityAnimal entityanimal1 = (EntityAnimal) this.findValidBreedPartner(entityanimal).get();

        entityanimal.getBrain().setMemory(MemoryModuleType.BREED_TARGET, (Object) entityanimal1);
        entityanimal1.getBrain().setMemory(MemoryModuleType.BREED_TARGET, (Object) entityanimal);
        BehaviorUtil.lockGazeAndWalkToEachOther(entityanimal, entityanimal1, this.speedModifier);
        int j = 60 + entityanimal.getRandom().nextInt(50);

        this.spawnChildAtTime = i + (long) j;
    }

    protected boolean canStillUse(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        if (!this.hasBreedTargetOfRightType(entityanimal)) {
            return false;
        } else {
            EntityAnimal entityanimal1 = this.getBreedTarget(entityanimal);

            return entityanimal1.isAlive() && entityanimal.canMate(entityanimal1) && BehaviorUtil.entityIsVisible(entityanimal.getBrain(), entityanimal1) && i <= this.spawnChildAtTime;
        }
    }

    protected void tick(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        EntityAnimal entityanimal1 = this.getBreedTarget(entityanimal);

        BehaviorUtil.lockGazeAndWalkToEachOther(entityanimal, entityanimal1, this.speedModifier);
        if (entityanimal.closerThan(entityanimal1, 3.0D)) {
            if (i >= this.spawnChildAtTime) {
                entityanimal.spawnChildFromBreeding(worldserver, entityanimal1);
                entityanimal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
                entityanimal1.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            }

        }
    }

    protected void stop(WorldServer worldserver, EntityAnimal entityanimal, long i) {
        entityanimal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        entityanimal.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entityanimal.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        this.spawnChildAtTime = 0L;
    }

    private EntityAnimal getBreedTarget(EntityAnimal entityanimal) {
        return (EntityAnimal) entityanimal.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean hasBreedTargetOfRightType(EntityAnimal entityanimal) {
        BehaviorController<?> behaviorcontroller = entityanimal.getBrain();

        return behaviorcontroller.hasMemoryValue(MemoryModuleType.BREED_TARGET) && ((EntityAgeable) behaviorcontroller.getMemory(MemoryModuleType.BREED_TARGET).get()).getType() == this.partnerType;
    }

    private Optional<? extends EntityAnimal> findValidBreedPartner(EntityAnimal entityanimal) {
        Optional optional = ((NearestVisibleLivingEntities) entityanimal.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).findClosest((entityliving) -> {
            boolean flag;

            if (entityliving.getType() == this.partnerType && entityliving instanceof EntityAnimal) {
                EntityAnimal entityanimal1 = (EntityAnimal) entityliving;

                if (entityanimal.canMate(entityanimal1)) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        });

        Objects.requireNonNull(EntityAnimal.class);
        return optional.map(EntityAnimal.class::cast);
    }
}
