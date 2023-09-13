package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.level.World;

public class PathfinderGoalBreed extends PathfinderGoal {

    private static final PathfinderTargetCondition d = (new PathfinderTargetCondition()).a(8.0D).a().b().c();
    protected final EntityAnimal animal;
    private final Class<? extends EntityAnimal> e;
    protected final World b;
    protected EntityAnimal partner;
    private int f;
    private final double g;

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0) {
        this(entityanimal, d0, entityanimal.getClass());
    }

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0, Class<? extends EntityAnimal> oclass) {
        this.animal = entityanimal;
        this.b = entityanimal.world;
        this.e = oclass;
        this.g = d0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.h();
            return this.partner != null;
        }
    }

    @Override
    public boolean b() {
        return this.partner.isAlive() && this.partner.isInLove() && this.f < 60;
    }

    @Override
    public void d() {
        this.partner = null;
        this.f = 0;
    }

    @Override
    public void e() {
        this.animal.getControllerLook().a(this.partner, 10.0F, (float) this.animal.O());
        this.animal.getNavigation().a((Entity) this.partner, this.g);
        ++this.f;
        if (this.f >= 60 && this.animal.h((Entity) this.partner) < 9.0D) {
            this.g();
        }

    }

    @Nullable
    private EntityAnimal h() {
        List<EntityAnimal> list = this.b.a(this.e, PathfinderGoalBreed.d, this.animal, this.animal.getBoundingBox().g(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityAnimal entityanimal = null;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityAnimal entityanimal1 = (EntityAnimal) iterator.next();

            if (this.animal.mate(entityanimal1) && this.animal.h((Entity) entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.animal.h((Entity) entityanimal1);
            }
        }

        return entityanimal;
    }

    protected void g() {
        this.animal.a((WorldServer) this.b, this.partner);
    }
}
