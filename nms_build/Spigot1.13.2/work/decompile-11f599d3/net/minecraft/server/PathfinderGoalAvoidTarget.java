package net.minecraft.server;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class PathfinderGoalAvoidTarget<T extends Entity> extends PathfinderGoal {

    private final Predicate<Entity> c;
    protected EntityCreature a;
    private final double d;
    private final double e;
    protected T b;
    private final float f;
    private PathEntity g;
    private final NavigationAbstract h;
    private final Class<T> i;
    private final Predicate<? super Entity> j;
    private final Predicate<? super Entity> k;

    public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, float f, double d0, double d1) {
        this(entitycreature, oclass, (entity) -> {
            return true;
        }, f, d0, d1, IEntitySelector.e);
    }

    public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, Predicate<? super Entity> predicate, float f, double d0, double d1, Predicate<Entity> predicate1) {
        this.c = new Predicate<Entity>() {
            public boolean test(@Nullable Entity entity) {
                return entity.isAlive() && PathfinderGoalAvoidTarget.this.a.getEntitySenses().a(entity) && !PathfinderGoalAvoidTarget.this.a.r(entity);
            }
        };
        this.a = entitycreature;
        this.i = oclass;
        this.j = predicate;
        this.f = f;
        this.d = d0;
        this.e = d1;
        this.k = predicate1;
        this.h = entitycreature.getNavigation();
        this.a(1);
    }

    public PathfinderGoalAvoidTarget(EntityCreature entitycreature, Class<T> oclass, float f, double d0, double d1, Predicate<Entity> predicate) {
        this(entitycreature, oclass, (entity) -> {
            return true;
        }, f, d0, d1, predicate);
    }

    public boolean a() {
        List<T> list = this.a.world.a(this.i, this.a.getBoundingBox().grow((double) this.f, 3.0D, (double) this.f), (entity) -> {
            return this.k.test(entity) && this.c.test(entity) && this.j.test(entity);
        });

        if (list.isEmpty()) {
            return false;
        } else {
            this.b = (Entity) list.get(0);
            Vec3D vec3d = RandomPositionGenerator.b(this.a, 16, 7, new Vec3D(this.b.locX, this.b.locY, this.b.locZ));

            if (vec3d == null) {
                return false;
            } else if (this.b.d(vec3d.x, vec3d.y, vec3d.z) < this.b.h(this.a)) {
                return false;
            } else {
                this.g = this.h.a(vec3d.x, vec3d.y, vec3d.z);
                return this.g != null;
            }
        }
    }

    public boolean b() {
        return !this.h.p();
    }

    public void c() {
        this.h.a(this.g, this.d);
    }

    public void d() {
        this.b = null;
    }

    public void e() {
        if (this.a.h(this.b) < 49.0D) {
            this.a.getNavigation().a(this.e);
        } else {
            this.a.getNavigation().a(this.d);
        }

    }
}
