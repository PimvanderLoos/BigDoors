package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;

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
                worldserver.y().b(blockposition);
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
        return !worldserver.y().a(blockposition, this.c);
    }
}
