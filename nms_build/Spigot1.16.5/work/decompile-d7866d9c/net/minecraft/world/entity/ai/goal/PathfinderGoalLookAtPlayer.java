package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;

public class PathfinderGoalLookAtPlayer extends PathfinderGoal {

    protected final EntityInsentient a;
    protected Entity b;
    protected final float c;
    private int g;
    protected final float d;
    protected final Class<? extends EntityLiving> e;
    protected final PathfinderTargetCondition f;

    public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends EntityLiving> oclass, float f) {
        this(entityinsentient, oclass, f, 0.02F);
    }

    public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends EntityLiving> oclass, float f, float f1) {
        this.a = entityinsentient;
        this.e = oclass;
        this.c = f;
        this.d = f1;
        this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        if (oclass == EntityHuman.class) {
            this.f = (new PathfinderTargetCondition()).a((double) f).b().a().d().a((entityliving) -> {
                return IEntitySelector.b(entityinsentient).test(entityliving);
            });
        } else {
            this.f = (new PathfinderTargetCondition()).a((double) f).b().a().d();
        }

    }

    @Override
    public boolean a() {
        if (this.a.getRandom().nextFloat() >= this.d) {
            return false;
        } else {
            if (this.a.getGoalTarget() != null) {
                this.b = this.a.getGoalTarget();
            }

            if (this.e == EntityHuman.class) {
                this.b = this.a.world.a(this.f, this.a, this.a.locX(), this.a.getHeadY(), this.a.locZ());
            } else {
                this.b = this.a.world.b(this.e, this.f, this.a, this.a.locX(), this.a.getHeadY(), this.a.locZ(), this.a.getBoundingBox().grow((double) this.c, 3.0D, (double) this.c));
            }

            return this.b != null;
        }
    }

    @Override
    public boolean b() {
        return !this.b.isAlive() ? false : (this.a.h(this.b) > (double) (this.c * this.c) ? false : this.g > 0);
    }

    @Override
    public void c() {
        this.g = 40 + this.a.getRandom().nextInt(40);
    }

    @Override
    public void d() {
        this.b = null;
    }

    @Override
    public void e() {
        this.a.getControllerLook().a(this.b.locX(), this.b.getHeadY(), this.b.locZ());
        --this.g;
    }
}
