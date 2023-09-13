package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.BlockBell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class BehaviorBellRing extends Behavior<EntityLiving> {

    private static final float BELL_RING_CHANCE = 0.95F;
    public static final int RING_BELL_FROM_DISTANCE = 3;

    public BehaviorBellRing() {
        super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        return worldserver.random.nextFloat() > 0.95F;
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        BlockPosition blockposition = ((GlobalPos) behaviorcontroller.getMemory(MemoryModuleType.MEETING_POINT).get()).pos();

        if (blockposition.closerThan((BaseBlockPosition) entityliving.blockPosition(), 3.0D)) {
            IBlockData iblockdata = worldserver.getBlockState(blockposition);

            if (iblockdata.is(Blocks.BELL)) {
                BlockBell blockbell = (BlockBell) iblockdata.getBlock();

                blockbell.attemptToRing(entityliving, worldserver, blockposition, (EnumDirection) null);
            }
        }

    }
}
