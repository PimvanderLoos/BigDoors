package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
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
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;

public class BehaviorInteractDoor extends Behavior<EntityLiving> {

    private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
    private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 2.0D;
    private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0D;
    @Nullable
    private PathPoint lastCheckedNode;
    private int remainingCooldown;

    public BehaviorInteractDoor() {
        super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.DOORS_TO_CLOSE, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        PathEntity pathentity = (PathEntity) entityliving.getBrain().getMemory(MemoryModuleType.PATH).get();

        if (!pathentity.notStarted() && !pathentity.isDone()) {
            if (!Objects.equals(this.lastCheckedNode, pathentity.getNextNode())) {
                this.remainingCooldown = 20;
                return true;
            } else {
                if (this.remainingCooldown > 0) {
                    --this.remainingCooldown;
                }

                return this.remainingCooldown == 0;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        PathEntity pathentity = (PathEntity) entityliving.getBrain().getMemory(MemoryModuleType.PATH).get();

        this.lastCheckedNode = pathentity.getNextNode();
        PathPoint pathpoint = pathentity.getPreviousNode();
        PathPoint pathpoint1 = pathentity.getNextNode();
        BlockPosition blockposition = pathpoint.asBlockPos();
        IBlockData iblockdata = worldserver.getBlockState(blockposition);

        if (iblockdata.is((Tag) TagsBlock.WOODEN_DOORS)) {
            BlockDoor blockdoor = (BlockDoor) iblockdata.getBlock();

            if (!blockdoor.isOpen(iblockdata)) {
                blockdoor.setOpen(entityliving, worldserver, iblockdata, blockposition, true);
            }

            this.rememberDoorToClose(worldserver, entityliving, blockposition);
        }

        BlockPosition blockposition1 = pathpoint1.asBlockPos();
        IBlockData iblockdata1 = worldserver.getBlockState(blockposition1);

        if (iblockdata1.is((Tag) TagsBlock.WOODEN_DOORS)) {
            BlockDoor blockdoor1 = (BlockDoor) iblockdata1.getBlock();

            if (!blockdoor1.isOpen(iblockdata1)) {
                blockdoor1.setOpen(entityliving, worldserver, iblockdata1, blockposition1, true);
                this.rememberDoorToClose(worldserver, entityliving, blockposition1);
            }
        }

        closeDoorsThatIHaveOpenedOrPassedThrough(worldserver, entityliving, pathpoint, pathpoint1);
    }

    public static void closeDoorsThatIHaveOpenedOrPassedThrough(WorldServer worldserver, EntityLiving entityliving, @Nullable PathPoint pathpoint, @Nullable PathPoint pathpoint1) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        if (behaviorcontroller.hasMemoryValue(MemoryModuleType.DOORS_TO_CLOSE)) {
            Iterator iterator = ((Set) behaviorcontroller.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get()).iterator();

            while (iterator.hasNext()) {
                GlobalPos globalpos = (GlobalPos) iterator.next();
                BlockPosition blockposition = globalpos.pos();

                if ((pathpoint == null || !pathpoint.asBlockPos().equals(blockposition)) && (pathpoint1 == null || !pathpoint1.asBlockPos().equals(blockposition))) {
                    if (isDoorTooFarAway(worldserver, entityliving, globalpos)) {
                        iterator.remove();
                    } else {
                        IBlockData iblockdata = worldserver.getBlockState(blockposition);

                        if (!iblockdata.is((Tag) TagsBlock.WOODEN_DOORS)) {
                            iterator.remove();
                        } else {
                            BlockDoor blockdoor = (BlockDoor) iblockdata.getBlock();

                            if (!blockdoor.isOpen(iblockdata)) {
                                iterator.remove();
                            } else if (areOtherMobsComingThroughDoor(worldserver, entityliving, blockposition)) {
                                iterator.remove();
                            } else {
                                blockdoor.setOpen(entityliving, worldserver, iblockdata, blockposition, false);
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }

    }

    private static boolean areOtherMobsComingThroughDoor(WorldServer worldserver, EntityLiving entityliving, BlockPosition blockposition) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        return !behaviorcontroller.hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES) ? false : ((List) behaviorcontroller.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).get()).stream().filter((entityliving1) -> {
            return entityliving1.getType() == entityliving.getType();
        }).filter((entityliving1) -> {
            return blockposition.closerThan((IPosition) entityliving1.position(), 2.0D);
        }).anyMatch((entityliving1) -> {
            return isMobComingThroughDoor(worldserver, entityliving1, blockposition);
        });
    }

    private static boolean isMobComingThroughDoor(WorldServer worldserver, EntityLiving entityliving, BlockPosition blockposition) {
        if (!entityliving.getBrain().hasMemoryValue(MemoryModuleType.PATH)) {
            return false;
        } else {
            PathEntity pathentity = (PathEntity) entityliving.getBrain().getMemory(MemoryModuleType.PATH).get();

            if (pathentity.isDone()) {
                return false;
            } else {
                PathPoint pathpoint = pathentity.getPreviousNode();

                if (pathpoint == null) {
                    return false;
                } else {
                    PathPoint pathpoint1 = pathentity.getNextNode();

                    return blockposition.equals(pathpoint.asBlockPos()) || blockposition.equals(pathpoint1.asBlockPos());
                }
            }
        }
    }

    private static boolean isDoorTooFarAway(WorldServer worldserver, EntityLiving entityliving, GlobalPos globalpos) {
        return globalpos.dimension() != worldserver.dimension() || !globalpos.pos().closerThan((IPosition) entityliving.position(), 2.0D);
    }

    private void rememberDoorToClose(WorldServer worldserver, EntityLiving entityliving, BlockPosition blockposition) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        GlobalPos globalpos = GlobalPos.of(worldserver.dimension(), blockposition);

        if (behaviorcontroller.getMemory(MemoryModuleType.DOORS_TO_CLOSE).isPresent()) {
            ((Set) behaviorcontroller.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get()).add(globalpos);
        } else {
            behaviorcontroller.setMemory(MemoryModuleType.DOORS_TO_CLOSE, (Object) Sets.newHashSet(new GlobalPos[]{globalpos}));
        }

    }
}
