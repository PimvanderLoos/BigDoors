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
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockBell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class BehaviorBellRing extends Behavior<EntityLiving> {

    public BehaviorBellRing() {
        super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        return worldserver.random.nextFloat() > 0.95F;
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        BlockPosition blockposition = ((GlobalPos) behaviorcontroller.getMemory(MemoryModuleType.MEETING_POINT).get()).getBlockPosition();

        if (blockposition.a((BaseBlockPosition) entityliving.getChunkCoordinates(), 3.0D)) {
            IBlockData iblockdata = worldserver.getType(blockposition);

            if (iblockdata.a(Blocks.BELL)) {
                BlockBell blockbell = (BlockBell) iblockdata.getBlock();

                blockbell.a((World) worldserver, blockposition, (EnumDirection) null);
            }
        }

    }
}
