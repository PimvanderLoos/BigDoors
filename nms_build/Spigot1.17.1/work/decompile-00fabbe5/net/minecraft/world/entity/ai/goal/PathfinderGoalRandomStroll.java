package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRandomStroll extends PathfinderGoal {

    public static final int DEFAULT_INTERVAL = 120;
    protected final EntityCreature mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;

    public PathfinderGoalRandomStroll(EntityCreature entitycreature, double d0) {
        this(entitycreature, d0, 120);
    }

    public PathfinderGoalRandomStroll(EntityCreature entitycreature, double d0, int i) {
        this(entitycreature, d0, i, true);
    }

    public PathfinderGoalRandomStroll(EntityCreature entitycreature, double d0, int i, boolean flag) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.interval = i;
        this.checkNoActionTime = flag;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (this.mob.isVehicle()) {
            return false;
        } else {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.mob.dK() >= 100) {
                    return false;
                }

                if (this.mob.getRandom().nextInt(this.interval) != 0) {
                    return false;
                }
            }

            Vec3D vec3d = this.g();

            if (vec3d == null) {
                return false;
            } else {
                this.wantedX = vec3d.x;
                this.wantedY = vec3d.y;
                this.wantedZ = vec3d.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3D g() {
        return DefaultRandomPos.a(this.mob, 10, 7);
    }

    @Override
    public boolean b() {
        return !this.mob.getNavigation().m() && !this.mob.isVehicle();
    }

    @Override
    public void c() {
        this.mob.getNavigation().a(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Override
    public void d() {
        this.mob.getNavigation().o();
        super.d();
    }

    public void h() {
        this.forceTrigger = true;
    }

    public void setTimeBetweenMovement(int i) {
        this.interval = i;
    }
}
