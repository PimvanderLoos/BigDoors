package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class GoToTargetLocation {

    public GoToTargetLocation() {}

    private static BlockPosition getNearbyPos(EntityInsentient entityinsentient, BlockPosition blockposition) {
        RandomSource randomsource = entityinsentient.level.random;

        return blockposition.offset(getRandomOffset(randomsource), 0, getRandomOffset(randomsource));
    }

    private static int getRandomOffset(RandomSource randomsource) {
        return randomsource.nextInt(3) - 1;
    }

    public static <E extends EntityInsentient> OneShot<E> create(MemoryModuleType<BlockPosition> memorymoduletype, int i, float f) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(memorymoduletype), behaviorbuilder_b.absent(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityinsentient, j) -> {
                    BlockPosition blockposition = (BlockPosition) behaviorbuilder_b.get(memoryaccessor);
                    boolean flag = blockposition.closerThan(entityinsentient.blockPosition(), (double) i);

                    if (!flag) {
                        BehaviorUtil.setWalkAndLookTargetMemories(entityinsentient, getNearbyPos(entityinsentient, blockposition), f, i);
                    }

                    return true;
                };
            });
        });
    }
}
