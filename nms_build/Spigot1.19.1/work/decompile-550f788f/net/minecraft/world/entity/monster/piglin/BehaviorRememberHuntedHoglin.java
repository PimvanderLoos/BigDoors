package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorRememberHuntedHoglin<E extends EntityPiglin> extends Behavior<E> {

    public BehaviorRememberHuntedHoglin() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.REGISTERED));
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        if (this.isAttackTargetDeadHoglin(e0)) {
            PiglinAI.dontKillAnyMoreHoglinsForAWhile(e0);
        }

    }

    private boolean isAttackTargetDeadHoglin(E e0) {
        EntityLiving entityliving = (EntityLiving) e0.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        return entityliving.getType() == EntityTypes.HOGLIN && entityliving.isDeadOrDying();
    }
}
