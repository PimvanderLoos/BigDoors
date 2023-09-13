package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.GameProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathfinderGoalSelector {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final PathfinderGoalWrapped NO_GOAL = new PathfinderGoalWrapped(Integer.MAX_VALUE, new PathfinderGoal() {
        @Override
        public boolean a() {
            return false;
        }
    }) {
        @Override
        public boolean g() {
            return false;
        }
    };
    private final Map<PathfinderGoal.Type, PathfinderGoalWrapped> lockedFlags = new EnumMap(PathfinderGoal.Type.class);
    private final Set<PathfinderGoalWrapped> availableGoals = Sets.newLinkedHashSet();
    private final Supplier<GameProfilerFiller> profiler;
    private final EnumSet<PathfinderGoal.Type> disabledFlags = EnumSet.noneOf(PathfinderGoal.Type.class);
    private int tickCount;
    private int newGoalRate = 3;

    public PathfinderGoalSelector(Supplier<GameProfilerFiller> supplier) {
        this.profiler = supplier;
    }

    public void a(int i, PathfinderGoal pathfindergoal) {
        this.availableGoals.add(new PathfinderGoalWrapped(i, pathfindergoal));
    }

    @VisibleForTesting
    public void a() {
        this.availableGoals.clear();
    }

    public void a(PathfinderGoal pathfindergoal) {
        this.availableGoals.stream().filter((pathfindergoalwrapped) -> {
            return pathfindergoalwrapped.j() == pathfindergoal;
        }).filter(PathfinderGoalWrapped::g).forEach(PathfinderGoalWrapped::d);
        this.availableGoals.removeIf((pathfindergoalwrapped) -> {
            return pathfindergoalwrapped.j() == pathfindergoal;
        });
    }

    public void doTick() {
        GameProfilerFiller gameprofilerfiller = (GameProfilerFiller) this.profiler.get();

        gameprofilerfiller.enter("goalCleanup");
        this.d().filter((pathfindergoalwrapped) -> {
            boolean flag;

            if (pathfindergoalwrapped.g()) {
                Stream stream = pathfindergoalwrapped.i().stream();
                EnumSet enumset = this.disabledFlags;

                Objects.requireNonNull(this.disabledFlags);
                if (!stream.anyMatch(enumset::contains) && pathfindergoalwrapped.b()) {
                    flag = false;
                    return flag;
                }
            }

            flag = true;
            return flag;
        }).forEach(PathfinderGoal::d);
        this.lockedFlags.forEach((pathfindergoal_type, pathfindergoalwrapped) -> {
            if (!pathfindergoalwrapped.g()) {
                this.lockedFlags.remove(pathfindergoal_type);
            }

        });
        gameprofilerfiller.exit();
        gameprofilerfiller.enter("goalUpdate");
        this.availableGoals.stream().filter((pathfindergoalwrapped) -> {
            return !pathfindergoalwrapped.g();
        }).filter((pathfindergoalwrapped) -> {
            Stream stream = pathfindergoalwrapped.i().stream();
            EnumSet enumset = this.disabledFlags;

            Objects.requireNonNull(this.disabledFlags);
            return stream.noneMatch(enumset::contains);
        }).filter((pathfindergoalwrapped) -> {
            return pathfindergoalwrapped.i().stream().allMatch((pathfindergoal_type) -> {
                return ((PathfinderGoalWrapped) this.lockedFlags.getOrDefault(pathfindergoal_type, PathfinderGoalSelector.NO_GOAL)).a(pathfindergoalwrapped);
            });
        }).filter(PathfinderGoalWrapped::a).forEach((pathfindergoalwrapped) -> {
            pathfindergoalwrapped.i().forEach((pathfindergoal_type) -> {
                PathfinderGoalWrapped pathfindergoalwrapped1 = (PathfinderGoalWrapped) this.lockedFlags.getOrDefault(pathfindergoal_type, PathfinderGoalSelector.NO_GOAL);

                pathfindergoalwrapped1.d();
                this.lockedFlags.put(pathfindergoal_type, pathfindergoalwrapped);
            });
            pathfindergoalwrapped.c();
        });
        gameprofilerfiller.exit();
        gameprofilerfiller.enter("goalTick");
        this.d().forEach(PathfinderGoalWrapped::e);
        gameprofilerfiller.exit();
    }

    public Set<PathfinderGoalWrapped> c() {
        return this.availableGoals;
    }

    public Stream<PathfinderGoalWrapped> d() {
        return this.availableGoals.stream().filter(PathfinderGoalWrapped::g);
    }

    public void a(int i) {
        this.newGoalRate = i;
    }

    public void a(PathfinderGoal.Type pathfindergoal_type) {
        this.disabledFlags.add(pathfindergoal_type);
    }

    public void b(PathfinderGoal.Type pathfindergoal_type) {
        this.disabledFlags.remove(pathfindergoal_type);
    }

    public void a(PathfinderGoal.Type pathfindergoal_type, boolean flag) {
        if (flag) {
            this.b(pathfindergoal_type);
        } else {
            this.a(pathfindergoal_type);
        }

    }
}
