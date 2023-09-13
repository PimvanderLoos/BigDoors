package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class SetWardenLookTarget extends Behavior<Warden> {

    public SetWardenLookTarget() {
        super(ImmutableMap.of(MemoryModuleType.DISTURBANCE_LOCATION, MemoryStatus.REGISTERED, MemoryModuleType.ROAR_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, Warden warden) {
        return warden.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION) || warden.getBrain().hasMemoryValue(MemoryModuleType.ROAR_TARGET);
    }

    protected void start(WorldServer worldserver, Warden warden, long i) {
        BlockPosition blockposition = (BlockPosition) warden.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).map(Entity::blockPosition).or(() -> {
            return warden.getBrain().getMemory(MemoryModuleType.DISTURBANCE_LOCATION);
        }).get();

        warden.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorTarget(blockposition)));
    }
}
