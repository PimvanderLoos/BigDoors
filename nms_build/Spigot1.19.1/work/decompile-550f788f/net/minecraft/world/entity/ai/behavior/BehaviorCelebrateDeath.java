package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;

public class BehaviorCelebrateDeath extends Behavior<EntityLiving> {

    private final int celebrateDuration;
    private final BiPredicate<EntityLiving, EntityLiving> dancePredicate;

    public BehaviorCelebrateDeath(int i, BiPredicate<EntityLiving, EntityLiving> bipredicate) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED, MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_ABSENT, MemoryModuleType.DANCING, MemoryStatus.REGISTERED));
        this.celebrateDuration = i;
        this.dancePredicate = bipredicate;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        return this.getAttackTarget(entityliving).isDeadOrDying();
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        EntityLiving entityliving1 = this.getAttackTarget(entityliving);

        if (this.dancePredicate.test(entityliving, entityliving1)) {
            entityliving.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, (long) this.celebrateDuration);
        }

        entityliving.getBrain().setMemoryWithExpiry(MemoryModuleType.CELEBRATE_LOCATION, entityliving1.blockPosition(), (long) this.celebrateDuration);
        if (entityliving1.getType() != EntityTypes.PLAYER || worldserver.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            entityliving.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            entityliving.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        }

    }

    private EntityLiving getAttackTarget(EntityLiving entityliving) {
        return (EntityLiving) entityliving.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
