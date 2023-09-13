package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorBell extends Behavior<EntityLiving> {

    private static final float SPEED_MODIFIER = 0.3F;

    public BehaviorBell() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        Optional<GlobalPos> optional = behaviorcontroller.getMemory(MemoryModuleType.MEETING_POINT);

        return worldserver.getRandom().nextInt(100) == 0 && optional.isPresent() && worldserver.dimension() == ((GlobalPos) optional.get()).dimension() && ((GlobalPos) optional.get()).pos().closerThan((IPosition) entityliving.position(), 4.0D) && ((NearestVisibleLivingEntities) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains((entityliving1) -> {
            return EntityTypes.VILLAGER.equals(entityliving1.getType());
        });
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        behaviorcontroller.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((nearestvisiblelivingentities) -> {
            return nearestvisiblelivingentities.findClosest((entityliving1) -> {
                return EntityTypes.VILLAGER.equals(entityliving1.getType()) && entityliving1.distanceToSqr((Entity) entityliving) <= 32.0D;
            });
        }).ifPresent((entityliving1) -> {
            behaviorcontroller.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object) entityliving1);
            behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving1, true)));
            behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityliving1, false), 0.3F, 1)));
        });
    }
}
