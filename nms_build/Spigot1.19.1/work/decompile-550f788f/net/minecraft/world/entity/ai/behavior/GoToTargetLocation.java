package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GoToTargetLocation<E extends EntityInsentient> extends Behavior<E> {

    private final MemoryModuleType<BlockPosition> locationMemory;
    private final int closeEnoughDist;
    private final float speedModifier;

    public GoToTargetLocation(MemoryModuleType<BlockPosition> memorymoduletype, int i, float f) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.locationMemory = memorymoduletype;
        this.closeEnoughDist = i;
        this.speedModifier = f;
    }

    protected void start(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        BlockPosition blockposition = this.getTargetLocation(entityinsentient);
        boolean flag = blockposition.closerThan(entityinsentient.blockPosition(), (double) this.closeEnoughDist);

        if (!flag) {
            BehaviorUtil.setWalkAndLookTargetMemories(entityinsentient, getNearbyPos(entityinsentient, blockposition), this.speedModifier, this.closeEnoughDist);
        }

    }

    private static BlockPosition getNearbyPos(EntityInsentient entityinsentient, BlockPosition blockposition) {
        RandomSource randomsource = entityinsentient.level.random;

        return blockposition.offset(getRandomOffset(randomsource), 0, getRandomOffset(randomsource));
    }

    private static int getRandomOffset(RandomSource randomsource) {
        return randomsource.nextInt(3) - 1;
    }

    private BlockPosition getTargetLocation(EntityInsentient entityinsentient) {
        return (BlockPosition) entityinsentient.getBrain().getMemory(this.locationMemory).get();
    }
}
