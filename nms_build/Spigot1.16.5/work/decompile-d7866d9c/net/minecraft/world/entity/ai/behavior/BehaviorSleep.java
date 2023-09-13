package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathPoint;

public class BehaviorSleep extends Behavior<EntityLiving> {

    private long b;

    public BehaviorSleep() {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        if (entityliving.isPassenger()) {
            return false;
        } else {
            BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
            GlobalPos globalpos = (GlobalPos) behaviorcontroller.getMemory(MemoryModuleType.HOME).get();

            if (worldserver.getDimensionKey() != globalpos.getDimensionManager()) {
                return false;
            } else {
                Optional<Long> optional = behaviorcontroller.getMemory(MemoryModuleType.LAST_WOKEN);

                if (optional.isPresent()) {
                    long i = worldserver.getTime() - (Long) optional.get();

                    if (i > 0L && i < 100L) {
                        return false;
                    }
                }

                IBlockData iblockdata = worldserver.getType(globalpos.getBlockPosition());

                return globalpos.getBlockPosition().a((IPosition) entityliving.getPositionVector(), 2.0D) && iblockdata.getBlock().a((Tag) TagsBlock.BEDS) && !(Boolean) iblockdata.get(BlockBed.OCCUPIED);
            }
        }
    }

    @Override
    protected boolean b(WorldServer worldserver, EntityLiving entityliving, long i) {
        Optional<GlobalPos> optional = entityliving.getBehaviorController().getMemory(MemoryModuleType.HOME);

        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPosition blockposition = ((GlobalPos) optional.get()).getBlockPosition();

            return entityliving.getBehaviorController().c(Activity.REST) && entityliving.locY() > (double) blockposition.getY() + 0.4D && blockposition.a((IPosition) entityliving.getPositionVector(), 1.14D);
        }
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        if (i > this.b) {
            BehaviorInteractDoor.a(worldserver, entityliving, (PathPoint) null, (PathPoint) null);
            entityliving.entitySleep(((GlobalPos) entityliving.getBehaviorController().getMemory(MemoryModuleType.HOME).get()).getBlockPosition());
        }

    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    @Override
    protected void c(WorldServer worldserver, EntityLiving entityliving, long i) {
        if (entityliving.isSleeping()) {
            entityliving.entityWakeup();
            this.b = i + 40L;
        }

    }
}
