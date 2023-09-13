package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.level.World;

public class PathfinderGoalBreed extends PathfinderGoal {

    private static final PathfinderTargetCondition PARTNER_TARGETING = PathfinderTargetCondition.b().a(8.0D).d();
    protected final EntityAnimal animal;
    private final Class<? extends EntityAnimal> partnerClass;
    protected final World level;
    protected EntityAnimal partner;
    private int loveTime;
    private final double speedModifier;

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0) {
        this(entityanimal, d0, entityanimal.getClass());
    }

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0, Class<? extends EntityAnimal> oclass) {
        this.animal = entityanimal;
        this.level = entityanimal.level;
        this.partnerClass = oclass;
        this.speedModifier = d0;
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
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
    }

    @Override
    public void d() {
        this.partner = null;
        this.loveTime = 0;
    }

    @Override
    public void e() {
        this.animal.getControllerLook().a(this.partner, 10.0F, (float) this.animal.eZ());
        this.animal.getNavigation().a((Entity) this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= 60 && this.animal.f((Entity) this.partner) < 9.0D) {
            this.g();
        }

    }

    @Nullable
    private EntityAnimal h() {
        List<? extends EntityAnimal> list = this.level.a(this.partnerClass, PathfinderGoalBreed.PARTNER_TARGETING, (EntityLiving) this.animal, this.animal.getBoundingBox().g(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityAnimal entityanimal = null;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityAnimal entityanimal1 = (EntityAnimal) iterator.next();

            if (this.animal.mate(entityanimal1) && this.animal.f((Entity) entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.animal.f((Entity) entityanimal1);
            }
        }

        return entityanimal;
    }

    protected void g() {
        this.animal.a((WorldServer) this.level, this.partner);
    }
}
