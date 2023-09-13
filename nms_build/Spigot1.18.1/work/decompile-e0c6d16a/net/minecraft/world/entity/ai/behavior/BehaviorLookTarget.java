package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorLookTarget extends Behavior<EntityLiving> {

    private final Predicate<EntityLiving> predicate;
    private final float maxDistSqr;
    private Optional<EntityLiving> nearestEntityMatchingTest;

    public BehaviorLookTarget(Tag<EntityTypes<?>> tag, float f) {
        this((entityliving) -> {
            return entityliving.getType().is(tag);
        }, f);
    }

    public BehaviorLookTarget(EnumCreatureType enumcreaturetype, float f) {
        this((entityliving) -> {
            return enumcreaturetype.equals(entityliving.getType().getCategory());
        }, f);
    }

    public BehaviorLookTarget(EntityTypes<?> entitytypes, float f) {
        this((entityliving) -> {
            return entitytypes.equals(entityliving.getType());
        }, f);
    }

    public BehaviorLookTarget(float f) {
        this((entityliving) -> {
            return true;
        }, f);
    }

    public BehaviorLookTarget(Predicate<EntityLiving> predicate, float f) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.nearestEntityMatchingTest = Optional.empty();
        this.predicate = predicate;
        this.maxDistSqr = f * f;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) entityliving.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();

        this.nearestEntityMatchingTest = nearestvisiblelivingentities.findClosest(this.predicate.and((entityliving1) -> {
            return entityliving1.distanceToSqr((Entity) entityliving) <= (double) this.maxDistSqr;
        }));
        return this.nearestEntityMatchingTest.isPresent();
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        entityliving.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity((Entity) this.nearestEntityMatchingTest.get(), true)));
        this.nearestEntityMatchingTest = Optional.empty();
    }
}
