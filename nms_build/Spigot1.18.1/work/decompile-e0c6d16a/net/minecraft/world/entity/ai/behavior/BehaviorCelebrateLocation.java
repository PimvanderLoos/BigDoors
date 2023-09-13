package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorCelebrateLocation<E extends EntityInsentient> extends Behavior<E> {

    private final int closeEnoughDist;
    private final float speedModifier;

    public BehaviorCelebrateLocation(int i, float f) {
        super(ImmutableMap.of(MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.closeEnoughDist = i;
        this.speedModifier = f;
    }

    protected void start(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        BlockPosition blockposition = getCelebrateLocation(entityinsentient);
        boolean flag = blockposition.closerThan((BaseBlockPosition) entityinsentient.blockPosition(), (double) this.closeEnoughDist);

        if (!flag) {
            BehaviorUtil.setWalkAndLookTargetMemories(entityinsentient, getNearbyPos(entityinsentient, blockposition), this.speedModifier, this.closeEnoughDist);
        }

    }

    private static BlockPosition getNearbyPos(EntityInsentient entityinsentient, BlockPosition blockposition) {
        Random random = entityinsentient.level.random;

        return blockposition.offset(getRandomOffset(random), 0, getRandomOffset(random));
    }

    private static int getRandomOffset(Random random) {
        return random.nextInt(3) - 1;
    }

    private static BlockPosition getCelebrateLocation(EntityInsentient entityinsentient) {
        return (BlockPosition) entityinsentient.getBrain().getMemory(MemoryModuleType.CELEBRATE_LOCATION).get();
    }
}
