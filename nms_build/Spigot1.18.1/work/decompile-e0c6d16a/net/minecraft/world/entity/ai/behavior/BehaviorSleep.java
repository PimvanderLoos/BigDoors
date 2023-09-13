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

    public static final int COOLDOWN_AFTER_BEING_WOKEN = 100;
    private long nextOkStartTime;

    public BehaviorSleep() {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        if (entityliving.isPassenger()) {
            return false;
        } else {
            BehaviorController<?> behaviorcontroller = entityliving.getBrain();
            GlobalPos globalpos = (GlobalPos) behaviorcontroller.getMemory(MemoryModuleType.HOME).get();

            if (worldserver.dimension() != globalpos.dimension()) {
                return false;
            } else {
                Optional<Long> optional = behaviorcontroller.getMemory(MemoryModuleType.LAST_WOKEN);

                if (optional.isPresent()) {
                    long i = worldserver.getGameTime() - (Long) optional.get();

                    if (i > 0L && i < 100L) {
                        return false;
                    }
                }

                IBlockData iblockdata = worldserver.getBlockState(globalpos.pos());

                return globalpos.pos().closerThan((IPosition) entityliving.position(), 2.0D) && iblockdata.is((Tag) TagsBlock.BEDS) && !(Boolean) iblockdata.getValue(BlockBed.OCCUPIED);
            }
        }
    }

    @Override
    protected boolean canStillUse(WorldServer worldserver, EntityLiving entityliving, long i) {
        Optional<GlobalPos> optional = entityliving.getBrain().getMemory(MemoryModuleType.HOME);

        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPosition blockposition = ((GlobalPos) optional.get()).pos();

            return entityliving.getBrain().isActive(Activity.REST) && entityliving.getY() > (double) blockposition.getY() + 0.4D && blockposition.closerThan((IPosition) entityliving.position(), 1.14D);
        }
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        if (i > this.nextOkStartTime) {
            BehaviorInteractDoor.closeDoorsThatIHaveOpenedOrPassedThrough(worldserver, entityliving, (PathPoint) null, (PathPoint) null);
            entityliving.startSleeping(((GlobalPos) entityliving.getBrain().getMemory(MemoryModuleType.HOME).get()).pos());
        }

    }

    @Override
    protected boolean timedOut(long i) {
        return false;
    }

    @Override
    protected void stop(WorldServer worldserver, EntityLiving entityliving, long i) {
        if (entityliving.isSleeping()) {
            entityliving.stopSleeping();
            this.nextOkStartTime = i + 40L;
        }

    }
}
