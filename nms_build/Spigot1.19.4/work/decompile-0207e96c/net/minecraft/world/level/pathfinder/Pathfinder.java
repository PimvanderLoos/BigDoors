package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;

public class Pathfinder {

    private static final float FUDGING = 1.5F;
    private final PathPoint[] neighbors = new PathPoint[32];
    private final int maxVisitedNodes;
    private final PathfinderAbstract nodeEvaluator;
    private static final boolean DEBUG = false;
    private final Path openSet = new Path();

    public Pathfinder(PathfinderAbstract pathfinderabstract, int i) {
        this.nodeEvaluator = pathfinderabstract;
        this.maxVisitedNodes = i;
    }

    @Nullable
    public PathEntity findPath(ChunkCache chunkcache, EntityInsentient entityinsentient, Set<BlockPosition> set, float f, int i, float f1) {
        this.openSet.clear();
        this.nodeEvaluator.prepare(chunkcache, entityinsentient);
        PathPoint pathpoint = this.nodeEvaluator.getStart();

        if (pathpoint == null) {
            return null;
        } else {
            Map<PathDestination, BlockPosition> map = (Map) set.stream().collect(Collectors.toMap((blockposition) -> {
                return this.nodeEvaluator.getGoal((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
            }, Function.identity()));
            PathEntity pathentity = this.findPath(chunkcache.getProfiler(), pathpoint, map, f, i, f1);

            this.nodeEvaluator.done();
            return pathentity;
        }
    }

    @Nullable
    private PathEntity findPath(GameProfilerFiller gameprofilerfiller, PathPoint pathpoint, Map<PathDestination, BlockPosition> map, float f, int i, float f1) {
        gameprofilerfiller.push("find_path");
        gameprofilerfiller.markForCharting(MetricCategory.PATH_FINDING);
        Set<PathDestination> set = map.keySet();

        pathpoint.g = 0.0F;
        pathpoint.h = this.getBestH(pathpoint, set);
        pathpoint.f = pathpoint.h;
        this.openSet.clear();
        this.openSet.insert(pathpoint);
        Set<PathPoint> set1 = ImmutableSet.of();
        int j = 0;
        Set<PathDestination> set2 = Sets.newHashSetWithExpectedSize(set.size());
        int k = (int) ((float) this.maxVisitedNodes * f1);

        while (!this.openSet.isEmpty()) {
            ++j;
            if (j >= k) {
                break;
            }

            PathPoint pathpoint1 = this.openSet.pop();

            pathpoint1.closed = true;
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                PathDestination pathdestination = (PathDestination) iterator.next();

                if (pathpoint1.distanceManhattan((PathPoint) pathdestination) <= (float) i) {
                    pathdestination.setReached();
                    set2.add(pathdestination);
                }
            }

            if (!set2.isEmpty()) {
                break;
            }

            if (pathpoint1.distanceTo(pathpoint) < f) {
                int l = this.nodeEvaluator.getNeighbors(this.neighbors, pathpoint1);

                for (int i1 = 0; i1 < l; ++i1) {
                    PathPoint pathpoint2 = this.neighbors[i1];
                    float f2 = this.distance(pathpoint1, pathpoint2);

                    pathpoint2.walkedDistance = pathpoint1.walkedDistance + f2;
                    float f3 = pathpoint1.g + f2 + pathpoint2.costMalus;

                    if (pathpoint2.walkedDistance < f && (!pathpoint2.inOpenSet() || f3 < pathpoint2.g)) {
                        pathpoint2.cameFrom = pathpoint1;
                        pathpoint2.g = f3;
                        pathpoint2.h = this.getBestH(pathpoint2, set) * 1.5F;
                        if (pathpoint2.inOpenSet()) {
                            this.openSet.changeCost(pathpoint2, pathpoint2.g + pathpoint2.h);
                        } else {
                            pathpoint2.f = pathpoint2.g + pathpoint2.h;
                            this.openSet.insert(pathpoint2);
                        }
                    }
                }
            }
        }

        Optional<PathEntity> optional = !set2.isEmpty() ? set2.stream().map((pathdestination1) -> {
            return this.reconstructPath(pathdestination1.getBestNode(), (BlockPosition) map.get(pathdestination1), true);
        }).min(Comparator.comparingInt(PathEntity::getNodeCount)) : set.stream().map((pathdestination1) -> {
            return this.reconstructPath(pathdestination1.getBestNode(), (BlockPosition) map.get(pathdestination1), false);
        }).min(Comparator.comparingDouble(PathEntity::getDistToTarget).thenComparingInt(PathEntity::getNodeCount));

        gameprofilerfiller.pop();
        if (!optional.isPresent()) {
            return null;
        } else {
            PathEntity pathentity = (PathEntity) optional.get();

            return pathentity;
        }
    }

    protected float distance(PathPoint pathpoint, PathPoint pathpoint1) {
        return pathpoint.distanceTo(pathpoint1);
    }

    private float getBestH(PathPoint pathpoint, Set<PathDestination> set) {
        float f = Float.MAX_VALUE;

        float f1;

        for (Iterator iterator = set.iterator(); iterator.hasNext(); f = Math.min(f1, f)) {
            PathDestination pathdestination = (PathDestination) iterator.next();

            f1 = pathpoint.distanceTo((PathPoint) pathdestination);
            pathdestination.updateBest(f1, pathpoint);
        }

        return f;
    }

    private PathEntity reconstructPath(PathPoint pathpoint, BlockPosition blockposition, boolean flag) {
        List<PathPoint> list = Lists.newArrayList();
        PathPoint pathpoint1 = pathpoint;

        list.add(0, pathpoint);

        while (pathpoint1.cameFrom != null) {
            pathpoint1 = pathpoint1.cameFrom;
            list.add(0, pathpoint1);
        }

        return new PathEntity(list, blockposition, flag);
    }
}
