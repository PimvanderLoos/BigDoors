package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

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
