package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;

public class BehaviorPositionValidate {

    private static final int MAX_DISTANCE = 16;

    public BehaviorPositionValidate() {}

    public static BehaviorControl<EntityLiving> create(Predicate<Holder<VillagePlaceType>> predicate, MemoryModuleType<GlobalPos> memorymoduletype) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(memorymoduletype)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityliving, i) -> {
                    GlobalPos globalpos = (GlobalPos) behaviorbuilder_b.get(memoryaccessor);
                    BlockPosition blockposition = globalpos.pos();

                    if (worldserver.dimension() == globalpos.dimension() && blockposition.closerToCenterThan(entityliving.position(), 16.0D)) {
                        WorldServer worldserver1 = worldserver.getServer().getLevel(globalpos.dimension());

                        if (worldserver1 != null && worldserver1.getPoiManager().exists(blockposition, predicate)) {
                            if (bedIsOccupied(worldserver1, blockposition, entityliving)) {
                                memoryaccessor.erase();
                                worldserver.getPoiManager().release(blockposition);
                                PacketDebug.sendPoiTicketCountPacket(worldserver, blockposition);
                            }
                        } else {
                            memoryaccessor.erase();
                        }

                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }

    private static boolean bedIsOccupied(WorldServer worldserver, BlockPosition blockposition, EntityLiving entityliving) {
        IBlockData iblockdata = worldserver.getBlockState(blockposition);

        return iblockdata.is(TagsBlock.BEDS) && (Boolean) iblockdata.getValue(BlockBed.OCCUPIED) && !entityliving.isSleeping();
    }
}
