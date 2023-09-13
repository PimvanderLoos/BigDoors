package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.WorldServer;
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

                return globalpos.pos().closerToCenterThan(entityliving.position(), 2.0D) && iblockdata.is(TagsBlock.BEDS) && !(Boolean) iblockdata.getValue(BlockBed.OCCUPIED);
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

            return entityliving.getBrain().isActive(Activity.REST) && entityliving.getY() > (double) blockposition.getY() + 0.4D && blockposition.closerToCenterThan(entityliving.position(), 1.14D);
        }
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        if (i > this.nextOkStartTime) {
            BehaviorController<?> behaviorcontroller = entityliving.getBrain();

            if (behaviorcontroller.hasMemoryValue(MemoryModuleType.DOORS_TO_CLOSE)) {
                Set<GlobalPos> set = (Set) behaviorcontroller.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get();
                Optional optional;

                if (behaviorcontroller.hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES)) {
                    optional = behaviorcontroller.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
                } else {
                    optional = Optional.empty();
                }

                BehaviorInteractDoor.closeDoorsThatIHaveOpenedOrPassedThrough(worldserver, entityliving, (PathPoint) null, (PathPoint) null, set, optional);
            }

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
