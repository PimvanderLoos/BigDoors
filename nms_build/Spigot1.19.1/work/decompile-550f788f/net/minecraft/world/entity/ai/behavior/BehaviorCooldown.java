package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.EntityVillager;

public class BehaviorCooldown extends Behavior<EntityVillager> {

    private static final int SAFE_DISTANCE_FROM_DANGER = 36;

    public BehaviorCooldown() {
        super(ImmutableMap.of());
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        boolean flag = BehaviorPanic.isHurt(entityvillager) || BehaviorPanic.hasHostile(entityvillager) || isCloseToEntityThatHurtMe(entityvillager);

        if (!flag) {
            entityvillager.getBrain().eraseMemory(MemoryModuleType.HURT_BY);
            entityvillager.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
            entityvillager.getBrain().updateActivityFromSchedule(worldserver.getDayTime(), worldserver.getGameTime());
        }

    }

    private static boolean isCloseToEntityThatHurtMe(EntityVillager entityvillager) {
        return entityvillager.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((entityliving) -> {
            return entityliving.distanceToSqr((Entity) entityvillager) <= 36.0D;
        }).isPresent();
    }
}
