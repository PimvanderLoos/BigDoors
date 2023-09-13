package net.minecraft.world.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.animal.EntityFishSchool;

public class PathfinderGoalFishSchool extends PathfinderGoal {

    private final EntityFishSchool a;
    private int b;
    private int c;

    public PathfinderGoalFishSchool(EntityFishSchool entityfishschool) {
        this.a = entityfishschool;
        this.c = this.a(entityfishschool);
    }

    protected int a(EntityFishSchool entityfishschool) {
        return 200 + entityfishschool.getRandom().nextInt(200) % 20;
    }

    @Override
    public boolean a() {
        if (this.a.eR()) {
            return false;
        } else if (this.a.eO()) {
            return true;
        } else if (this.c > 0) {
            --this.c;
            return false;
        } else {
            this.c = this.a(this.a);
            Predicate<EntityFishSchool> predicate = (entityfishschool) -> {
                return entityfishschool.eQ() || !entityfishschool.eO();
            };
            List<EntityFishSchool> list = this.a.world.a(this.a.getClass(), this.a.getBoundingBox().grow(8.0D, 8.0D, 8.0D), predicate);
            EntityFishSchool entityfishschool = (EntityFishSchool) list.stream().filter(EntityFishSchool::eQ).findAny().orElse(this.a);

            entityfishschool.a(list.stream().filter((entityfishschool1) -> {
                return !entityfishschool1.eO();
            }));
            return this.a.eO();
        }
    }

    @Override
    public boolean b() {
        return this.a.eO() && this.a.eS();
    }

    @Override
    public void c() {
        this.b = 0;
    }

    @Override
    public void d() {
        this.a.eP();
    }

    @Override
    public void e() {
        if (--this.b <= 0) {
            this.b = 10;
            this.a.eT();
        }
    }
}
