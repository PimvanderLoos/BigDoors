package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.PathfinderGoalUtil;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalMoveThroughVillage extends PathfinderGoal {

    protected final EntityCreature mob;
    private final double speedModifier;
    @Nullable
    private PathEntity path;
    private BlockPosition poiPos;
    private final boolean onlyAtNight;
    private final List<BlockPosition> visited = Lists.newArrayList();
    private final int distanceToPoi;
    private final BooleanSupplier canDealWithDoors;

    public PathfinderGoalMoveThroughVillage(EntityCreature entitycreature, double d0, boolean flag, int i, BooleanSupplier booleansupplier) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.onlyAtNight = flag;
        this.distanceToPoi = i;
        this.canDealWithDoors = booleansupplier;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        if (!PathfinderGoalUtil.hasGroundPathNavigation(entitycreature)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    @Override
    public boolean canUse() {
        if (!PathfinderGoalUtil.hasGroundPathNavigation(this.mob)) {
            return false;
        } else {
            this.updateVisited();
            if (this.onlyAtNight && this.mob.level.isDay()) {
                return false;
            } else {
                WorldServer worldserver = (WorldServer) this.mob.level;
                BlockPosition blockposition = this.mob.blockPosition();

                if (!worldserver.isCloseToVillage(blockposition, 6)) {
                    return false;
                } else {
                    Vec3D vec3d = LandRandomPos.getPos(this.mob, 15, 7, (blockposition1) -> {
                        if (!worldserver.isVillage(blockposition1)) {
                            return Double.NEGATIVE_INFINITY;
                        } else {
                            Optional<BlockPosition> optional = worldserver.getPoiManager().find(VillagePlaceType.ALL, this::hasNotVisited, blockposition1, 10, VillagePlace.Occupancy.IS_OCCUPIED);

                            return !optional.isPresent() ? Double.NEGATIVE_INFINITY : -((BlockPosition) optional.get()).distSqr(blockposition);
                        }
                    });

                    if (vec3d == null) {
                        return false;
                    } else {
                        Optional<BlockPosition> optional = worldserver.getPoiManager().find(VillagePlaceType.ALL, this::hasNotVisited, new BlockPosition(vec3d), 10, VillagePlace.Occupancy.IS_OCCUPIED);

                        if (!optional.isPresent()) {
                            return false;
                        } else {
                            this.poiPos = ((BlockPosition) optional.get()).immutable();
                            Navigation navigation = (Navigation) this.mob.getNavigation();
                            boolean flag = navigation.canOpenDoors();

                            navigation.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                            this.path = navigation.createPath(this.poiPos, 0);
                            navigation.setCanOpenDoors(flag);
                            if (this.path == null) {
                                Vec3D vec3d1 = DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3D.atBottomCenterOf(this.poiPos), 1.5707963705062866D);

                                if (vec3d1 == null) {
                                    return false;
                                }

                                navigation.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                                this.path = this.mob.getNavigation().createPath(vec3d1.x, vec3d1.y, vec3d1.z, 0);
                                navigation.setCanOpenDoors(flag);
                                if (this.path == null) {
                                    return false;
                                }
                            }

                            for (int i = 0; i < this.path.getNodeCount(); ++i) {
                                PathPoint pathpoint = this.path.getNode(i);
                                BlockPosition blockposition1 = new BlockPosition(pathpoint.x, pathpoint.y + 1, pathpoint.z);

                                if (BlockDoor.isWoodenDoor(this.mob.level, blockposition1)) {
                                    this.path = this.mob.getNavigation().createPath((double) pathpoint.x, (double) pathpoint.y, (double) pathpoint.z, 0);
                                    break;
                                }
                            }

                            return this.path != null;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getNavigation().isDone() ? false : !this.poiPos.closerThan((IPosition) this.mob.position(), (double) (this.mob.getBbWidth() + (float) this.distanceToPoi));
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
    }

    @Override
    public void stop() {
        if (this.mob.getNavigation().isDone() || this.poiPos.closerThan((IPosition) this.mob.position(), (double) this.distanceToPoi)) {
            this.visited.add(this.poiPos);
        }

    }

    private boolean hasNotVisited(BlockPosition blockposition) {
        Iterator iterator = this.visited.iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (!Objects.equals(blockposition, blockposition1));

        return false;
    }

    private void updateVisited() {
        if (this.visited.size() > 15) {
            this.visited.remove(0);
        }

    }
}
