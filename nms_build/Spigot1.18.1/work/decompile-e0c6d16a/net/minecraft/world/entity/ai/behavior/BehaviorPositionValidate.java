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

    private static final int MAX_DISTANCE = 16;
    private final MemoryModuleType<GlobalPos> memoryType;
    private final Predicate<VillagePlaceType> poiPredicate;

    public BehaviorPositionValidate(VillagePlaceType villageplacetype, MemoryModuleType<GlobalPos> memorymoduletype) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.poiPredicate = villageplacetype.getPredicate();
        this.memoryType = memorymoduletype;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        GlobalPos globalpos = (GlobalPos) entityliving.getBrain().getMemory(this.memoryType).get();

        return worldserver.dimension() == globalpos.dimension() && globalpos.pos().closerThan((IPosition) entityliving.position(), 16.0D);
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        GlobalPos globalpos = (GlobalPos) behaviorcontroller.getMemory(this.memoryType).get();
        BlockPosition blockposition = globalpos.pos();
        WorldServer worldserver1 = worldserver.getServer().getLevel(globalpos.dimension());

        if (worldserver1 != null && !this.poiDoesntExist(worldserver1, blockposition)) {
            if (this.bedIsOccupied(worldserver1, blockposition, entityliving)) {
                behaviorcontroller.eraseMemory(this.memoryType);
                worldserver.getPoiManager().release(blockposition);
                PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
            }
        } else {
            behaviorcontroller.eraseMemory(this.memoryType);
        }

    }

    private boolean bedIsOccupied(WorldServer worldserver, BlockPosition blockposition, EntityLiving entityliving) {
        IBlockData iblockdata = worldserver.getBlockState(blockposition);

        return iblockdata.is((Tag) TagsBlock.BEDS) && (Boolean) iblockdata.getValue(BlockBed.OCCUPIED) && !entityliving.isSleeping();
    }

    private boolean poiDoesntExist(WorldServer worldserver, BlockPosition blockposition) {
        return !worldserver.getPoiManager().exists(blockposition, this.poiPredicate);
    }
}
