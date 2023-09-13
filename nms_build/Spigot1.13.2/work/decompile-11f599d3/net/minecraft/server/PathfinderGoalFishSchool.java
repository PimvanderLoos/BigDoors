package net.minecraft.server;

import java.util.List;
import java.util.function.Predicate;

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

    public boolean a() {
        if (this.a.dE()) {
            return false;
        } else if (this.a.dB()) {
            return true;
        } else if (this.c > 0) {
            --this.c;
            return false;
        } else {
            this.c = this.a(this.a);
            Predicate<EntityFishSchool> predicate = (entityfishschool) -> {
                return entityfishschool.dD() || !entityfishschool.dB();
            };
            List<EntityFishSchool> list = this.a.world.a(this.a.getClass(), this.a.getBoundingBox().grow(8.0D, 8.0D, 8.0D), predicate);
            EntityFishSchool entityfishschool = (EntityFishSchool) list.stream().filter(EntityFishSchool::dD).findAny().orElse(this.a);

            entityfishschool.a(list.stream().filter((entityfishschool1) -> {
                return !entityfishschool1.dB();
            }));
            return this.a.dB();
        }
    }

    public boolean b() {
        return this.a.dB() && this.a.dF();
    }

    public void c() {
        this.b = 0;
    }

    public void d() {
        this.a.dC();
    }

    public void e() {
        if (--this.b <= 0) {
            this.b = 10;
            this.a.dG();
        }
    }
}
