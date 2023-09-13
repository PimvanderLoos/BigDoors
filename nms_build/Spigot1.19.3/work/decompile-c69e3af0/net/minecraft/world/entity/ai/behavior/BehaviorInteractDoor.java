package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.OptionalBox.Mu;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class BehaviorInteractDoor {

    private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
    private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 3.0D;
    private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0D;

    public BehaviorInteractDoor() {}

    public static BehaviorControl<EntityLiving> create() {
        MutableObject<PathPoint> mutableobject = new MutableObject((Object) null);
        MutableInt mutableint = new MutableInt(0);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.PATH), behaviorbuilder_b.registered(MemoryModuleType.DOORS_TO_CLOSE), behaviorbuilder_b.registered(MemoryModuleType.NEAREST_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityliving, i) -> {
                    PathEntity pathentity = (PathEntity) behaviorbuilder_b.get(memoryaccessor);
                    Optional<Set<GlobalPos>> optional = behaviorbuilder_b.tryGet(memoryaccessor1);

                    if (!pathentity.notStarted() && !pathentity.isDone()) {
                        if (Objects.equals(mutableobject.getValue(), pathentity.getNextNode())) {
                            mutableint.setValue(20);
                        } else if (mutableint.decrementAndGet() > 0) {
                            return false;
                        }

                        mutableobject.setValue(pathentity.getNextNode());
                        PathPoint pathpoint = pathentity.getPreviousNode();
                        PathPoint pathpoint1 = pathentity.getNextNode();
                        BlockPosition blockposition = pathpoint.asBlockPos();
                        IBlockData iblockdata = worldserver.getBlockState(blockposition);

                        if (iblockdata.is(TagsBlock.WOODEN_DOORS, (blockbase_blockdata) -> {
                            return blockbase_blockdata.getBlock() instanceof BlockDoor;
                        })) {
                            BlockDoor blockdoor = (BlockDoor) iblockdata.getBlock();

                            if (!blockdoor.isOpen(iblockdata)) {
                                blockdoor.setOpen(entityliving, worldserver, iblockdata, blockposition, true);
                            }

                            optional = rememberDoorToClose(memoryaccessor1, optional, worldserver, blockposition);
                        }

                        BlockPosition blockposition1 = pathpoint1.asBlockPos();
                        IBlockData iblockdata1 = worldserver.getBlockState(blockposition1);

                        if (iblockdata1.is(TagsBlock.WOODEN_DOORS, (blockbase_blockdata) -> {
                            return blockbase_blockdata.getBlock() instanceof BlockDoor;
                        })) {
                            BlockDoor blockdoor1 = (BlockDoor) iblockdata1.getBlock();

                            if (!blockdoor1.isOpen(iblockdata1)) {
                                blockdoor1.setOpen(entityliving, worldserver, iblockdata1, blockposition1, true);
                                optional = rememberDoorToClose(memoryaccessor1, optional, worldserver, blockposition1);
                            }
                        }

                        optional.ifPresent((set) -> {
                            closeDoorsThatIHaveOpenedOrPassedThrough(worldserver, entityliving, pathpoint, pathpoint1, set, behaviorbuilder_b.tryGet(memoryaccessor2));
                        });
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }

    public static void closeDoorsThatIHaveOpenedOrPassedThrough(WorldServer worldserver, EntityLiving entityliving, @Nullable PathPoint pathpoint, @Nullable PathPoint pathpoint1, Set<GlobalPos> set, Optional<List<EntityLiving>> optional) {
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            GlobalPos globalpos = (GlobalPos) iterator.next();
            BlockPosition blockposition = globalpos.pos();

            if ((pathpoint == null || !pathpoint.asBlockPos().equals(blockposition)) && (pathpoint1 == null || !pathpoint1.asBlockPos().equals(blockposition))) {
                if (isDoorTooFarAway(worldserver, entityliving, globalpos)) {
                    iterator.remove();
                } else {
                    IBlockData iblockdata = worldserver.getBlockState(blockposition);

                    if (!iblockdata.is(TagsBlock.WOODEN_DOORS, (blockbase_blockdata) -> {
                        return blockbase_blockdata.getBlock() instanceof BlockDoor;
                    })) {
                        iterator.remove();
                    } else {
                        BlockDoor blockdoor = (BlockDoor) iblockdata.getBlock();

                        if (!blockdoor.isOpen(iblockdata)) {
                            iterator.remove();
                        } else if (areOtherMobsComingThroughDoor(entityliving, blockposition, optional)) {
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

    private static boolean areOtherMobsComingThroughDoor(EntityLiving entityliving, BlockPosition blockposition, Optional<List<EntityLiving>> optional) {
        return optional.isEmpty() ? false : ((List) optional.get()).stream().filter((entityliving1) -> {
            return entityliving1.getType() == entityliving.getType();
        }).filter((entityliving1) -> {
            return blockposition.closerToCenterThan(entityliving1.position(), 2.0D);
        }).anyMatch((entityliving1) -> {
            return isMobComingThroughDoor(entityliving1.getBrain(), blockposition);
        });
    }

    private static boolean isMobComingThroughDoor(BehaviorController<?> behaviorcontroller, BlockPosition blockposition) {
        if (!behaviorcontroller.hasMemoryValue(MemoryModuleType.PATH)) {
            return false;
        } else {
            PathEntity pathentity = (PathEntity) behaviorcontroller.getMemory(MemoryModuleType.PATH).get();

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
        return globalpos.dimension() != worldserver.dimension() || !globalpos.pos().closerToCenterThan(entityliving.position(), 3.0D);
    }

    private static Optional<Set<GlobalPos>> rememberDoorToClose(MemoryAccessor<Mu, Set<GlobalPos>> memoryaccessor, Optional<Set<GlobalPos>> optional, WorldServer worldserver, BlockPosition blockposition) {
        GlobalPos globalpos = GlobalPos.of(worldserver.dimension(), blockposition);

        return Optional.of((Set) optional.map((set) -> {
            set.add(globalpos);
            return set;
        }).orElseGet(() -> {
            Set<GlobalPos> set = Sets.newHashSet(new GlobalPos[]{globalpos});

            memoryaccessor.set(set);
            return set;
        }));
    }
}
