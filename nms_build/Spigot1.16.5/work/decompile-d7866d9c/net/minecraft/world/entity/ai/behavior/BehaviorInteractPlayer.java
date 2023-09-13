package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;

public class BehaviorInteractPlayer extends Behavior<EntityVillager> {

    private final float b;

    public BehaviorInteractPlayer(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
        this.b = f;
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        EntityHuman entityhuman = entityvillager.getTrader();

        return entityvillager.isAlive() && entityhuman != null && !entityvillager.isInWater() && !entityvillager.velocityChanged && entityvillager.h((Entity) entityhuman) <= 16.0D && entityhuman.activeContainer != null;
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.a(worldserver, entityvillager);
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.a(entityvillager);
    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();

        behaviorcontroller.removeMemory(MemoryModuleType.WALK_TARGET);
        behaviorcontroller.removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.a(entityvillager);
    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    private void a(EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityvillager.getTrader(), false), this.b, 2)));
        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityvillager.getTrader(), true)));
    }
}
