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
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.horse.isTamed() && this.horse.isVehicle()) {
            Vec3D vec3d = DefaultRandomPos.getPos(this.horse, 5, 4);

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
    public void start() {
        this.horse.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.horse.isTamed() && !this.horse.getNavigation().isDone() && this.horse.isVehicle();
    }

    @Override
    public void tick() {
        if (!this.horse.isTamed() && this.horse.getRandom().nextInt(this.adjustedTickDelay(50)) == 0) {
            Entity entity = (Entity) this.horse.getPassengers().get(0);

            if (entity == null) {
                return;
            }

            if (entity instanceof EntityHuman) {
                int i = this.horse.getTemper();
                int j = this.horse.getMaxTemper();

                if (j > 0 && this.horse.getRandom().nextInt(j) < i) {
                    this.horse.tameWithName((EntityHuman) entity);
                    return;
                }

                this.horse.modifyTemper(5);
            }

            this.horse.ejectPassengers();
            this.horse.makeMad();
            this.horse.level.broadcastEntityEvent(this.horse, (byte) 6);
        }

    }
}
