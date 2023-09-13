package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalTame extends PathfinderGoal {

    private final EntityHorseAbstract horse;
    private final double speedModifier;
    private double posX;
    private double posY;
    private double posZ;

    public PathfinderGoalTame(EntityHorseAbstract entityhorseabstract, double d0) {
        this.horse = entityhorseabstract;
        this.speedModifier = d0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (!this.horse.isTamed() && this.horse.isVehicle()) {
            Vec3D vec3d = DefaultRandomPos.a(this.horse, 5, 4);

            if (vec3d == null) {
                return false;
            } else {
                this.posX = vec3d.x;
                this.posY = vec3d.y;
                this.posZ = vec3d.z;
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void c() {
        this.horse.getNavigation().a(this.posX, this.posY, this.posZ, this.speedModifier);
    }

    @Override
    public boolean b() {
        return !this.horse.isTamed() && !this.horse.getNavigation().m() && this.horse.isVehicle();
    }

    @Override
    public void e() {
        if (!this.horse.isTamed() && this.horse.getRandom().nextInt(50) == 0) {
            Entity entity = (Entity) this.horse.getPassengers().get(0);

            if (entity == null) {
                return;
            }

            if (entity instanceof EntityHuman) {
                int i = this.horse.getTemper();
                int j = this.horse.getMaxDomestication();

                if (j > 0 && this.horse.getRandom().nextInt(j) < i) {
                    this.horse.i((EntityHuman) entity);
                    return;
                }

                this.horse.w(5);
            }

            this.horse.ejectPassengers();
            this.horse.fW();
            this.horse.level.broadcastEntityEffect(this.horse, (byte) 6);
        }

    }
}
