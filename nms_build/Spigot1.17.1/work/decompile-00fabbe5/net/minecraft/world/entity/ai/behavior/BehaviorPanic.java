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

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return b(entityvillager) || a(entityvillager);
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (b(entityvillager) || a(entityvillager)) {
            BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();

            if (!behaviorcontroller.c(Activity.PANIC)) {
                behaviorcontroller.removeMemory(MemoryModuleType.PATH);
                behaviorcontroller.removeMemory(MemoryModuleType.WALK_TARGET);
                behaviorcontroller.removeMemory(MemoryModuleType.LOOK_TARGET);
                behaviorcontroller.removeMemory(MemoryModuleType.BREED_TARGET);
                behaviorcontroller.removeMemory(MemoryModuleType.INTERACTION_TARGET);
            }

            behaviorcontroller.a(Activity.PANIC);
        }

    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (i % 100L == 0L) {
            entityvillager.a(worldserver, i, 3);
        }

    }

    public static boolean a(EntityLiving entityliving) {
        return entityliving.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_HOSTILE);
    }

    public static boolean b(EntityLiving entityliving) {
        return entityliving.getBehaviorController().hasMemory(MemoryModuleType.HURT_BY);
    }
}
