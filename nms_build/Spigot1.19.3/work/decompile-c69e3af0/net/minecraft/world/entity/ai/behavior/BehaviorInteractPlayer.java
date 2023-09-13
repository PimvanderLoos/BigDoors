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

    private final float speedModifier;

    public BehaviorInteractPlayer(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
        this.speedModifier = f;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        EntityHuman entityhuman = entityvillager.getTradingPlayer();

        return entityvillager.isAlive() && entityhuman != null && !entityvillager.isInWater() && !entityvillager.hurtMarked && entityvillager.distanceToSqr((Entity) entityhuman) <= 16.0D && entityhuman.containerMenu != null;
    }

    protected boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.checkExtraStartConditions(worldserver, entityvillager);
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.followPlayer(entityvillager);
    }

    protected void stop(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBrain();

        behaviorcontroller.eraseMemory(MemoryModuleType.WALK_TARGET);
        behaviorcontroller.eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    protected void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.followPlayer(entityvillager);
    }

    @Override
    protected boolean timedOut(long i) {
        return false;
    }

    private void followPlayer(EntityVillager entityvillager) {
        BehaviorController<?> behaviorcontroller = entityvillager.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityvillager.getTradingPlayer(), false), this.speedModifier, 2)));
        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityvillager.getTradingPlayer(), true)));
    }
}
