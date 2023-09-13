package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;

public class BehaviorPositionValidate extends Behavior<EntityLiving> {

    private final MemoryModuleType<GlobalPos> b;
    private final Predicate<VillagePlaceType> c;

    public BehaviorPositionValidate(VillagePlaceType villageplacetype, MemoryModuleType<GlobalPos> memorymoduletype) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.c = villageplacetype.c();
        this.b = memorymoduletype;
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        GlobalPos globalpos = (GlobalPos) entityliving.getBehaviorController().getMemory(this.b).get();

        return worldserver.getDimensionKey() == globalpos.getDimensionManager() && globalpos.getBlockPosition().a((IPosition) entityliving.getPositionVector(), 16.0D);
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        GlobalPos globalpos = (GlobalPos) behaviorcontroller.getMemory(this.b).get();
        BlockPosition blockposition = globalpos.getBlockPosition();
        WorldServer worldserver1 = worldserver.getMinecraftServer().getWorldServer(globalpos.getDimensionManager());

        if (worldserver1 != null && !this.a(worldserver1, blockposition)) {
            if (this.a(worldserver1, blockposition, entityliving)) {
                behaviorcontroller.removeMemory(this.b);
                worldserver.x().b(blockposition);
                PacketDebug.c(worldserver, blockposition);
            }
        } else {
            behaviorcontroller.removeMemory(this.b);
        }

    }

    private boolean a(WorldServer worldserver, BlockPosition blockposition, EntityLiving entityliving) {
        IBlockData iblockdata = worldserver.getType(blockposition);

        return iblockdata.getBlock().a((Tag) TagsBlock.BEDS) && (Boolean) iblockdata.get(BlockBed.OCCUPIED) && !entityliving.isSleeping();
    }

    private boolean a(WorldServer worldserver, BlockPosition blockposition) {
        return !worldserver.x().a(blockposition, this.c);
    }
}
