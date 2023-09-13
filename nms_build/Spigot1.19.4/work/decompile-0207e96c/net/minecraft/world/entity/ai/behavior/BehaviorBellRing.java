package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.BlockBell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class BehaviorBellRing {

    private static final float BELL_RING_CHANCE = 0.95F;
    public static final int RING_BELL_FROM_DISTANCE = 3;

    public BehaviorBellRing() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.MEETING_POINT)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityliving, i) -> {
                    if (worldserver.random.nextFloat() <= 0.95F) {
                        return false;
                    } else {
                        BlockPosition blockposition = ((GlobalPos) behaviorbuilder_b.get(memoryaccessor)).pos();

                        if (blockposition.closerThan(entityliving.blockPosition(), 3.0D)) {
                            IBlockData iblockdata = worldserver.getBlockState(blockposition);

                            if (iblockdata.is(Blocks.BELL)) {
                                BlockBell blockbell = (BlockBell) iblockdata.getBlock();

                                blockbell.attemptToRing(entityliving, worldserver, blockposition, (EnumDirection) null);
                            }
                        }

                        return true;
                    }
                };
            });
        });
    }
}
