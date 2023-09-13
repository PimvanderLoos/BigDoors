package net.minecraft.world.entity.ai.goal;

import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.animal.EntityFishSchool;

public class PathfinderGoalFishSchool extends PathfinderGoal {

    private static final int INTERVAL_TICKS = 200;
    private final EntityFishSchool mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public PathfinderGoalFishSchool(EntityFishSchool entityfishschool) {
        this.mob = entityfishschool;
        this.nextStartTick = this.a(entityfishschool);
    }

    protected int a(EntityFishSchool entityfishschool) {
        return 200 + entityfishschool.getRandom().nextInt(200) % 20;
    }

    @Override
    public boolean a() {
        if (this.mob.fC()) {
            return false;
        } else if (this.mob.fz()) {
            return true;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.a(this.mob);
            Predicate<EntityFishSchool> predicate = (entityfishschool) -> {
                return entityfishschool.fB() || !entityfishschool.fz();
            };
            List<? extends EntityFishSchool> list = this.mob.level.a(this.mob.getClass(), this.mob.getBoundingBox().grow(8.0D, 8.0D, 8.0D), predicate);
            EntityFishSchool entityfishschool = (EntityFishSchool) DataFixUtils.orElse(list.stream().filter(EntityFishSchool::fB).findAny(), this.mob);

            entityfishschool.a(list.stream().filter((entityfishschool1) -> {
                return !entityfishschool1.fz();
            }));
            return this.mob.fz();
        }
    }

    @Override
    public boolean b() {
        return this.mob.fz() && this.mob.fD();
    }

    @Override
    public void c() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void d() {
        this.mob.fA();
    }

    @Override
    public void e() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            this.mob.fE();
        }
    }
}
