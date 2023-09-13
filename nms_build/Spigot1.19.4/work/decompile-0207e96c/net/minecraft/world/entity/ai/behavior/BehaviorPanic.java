package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.schedule.Activity;

public class BehaviorPanic extends Behavior<EntityVillager> {

    public BehaviorPanic() {
        super(ImmutableMap.of());
    }

    protected boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return isHurt(entityvillager) || hasHostile(entityvillager);
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (isHurt(entityvillager) || hasHostile(entityvillager)) {
            BehaviorController<?> behaviorcontroller = entityvillager.getBrain();

            if (!behaviorcontroller.isActive(Activity.PANIC)) {
                behaviorcontroller.eraseMemory(MemoryModuleType.PATH);
                behaviorcontroller.eraseMemory(MemoryModuleType.WALK_TARGET);
                behaviorcontroller.eraseMemory(MemoryModuleType.LOOK_TARGET);
                behaviorcontroller.eraseMemory(MemoryModuleType.BREED_TARGET);
                behaviorcontroller.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            }

            behaviorcontroller.setActiveActivityIfPossible(Activity.PANIC);
        }

    }

    protected void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (i % 100L == 0L) {
            entityvillager.spawnGolemIfNeeded(worldserver, i, 3);
        }

    }

    public static boolean hasHostile(EntityLiving entityliving) {
        return entityliving.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean isHurt(EntityLiving entityliving) {
        return entityliving.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
    }
}
