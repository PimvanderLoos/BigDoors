package net.minecraft.world.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityCreature extends EntityInsentient {

    protected static final float DEFAULT_WALK_TARGET_VALUE = 0.0F;

    protected EntityCreature(EntityTypes<? extends EntityCreature> entitytypes, World world) {
        super(entitytypes, world);
    }

    public float getWalkTargetValue(BlockPosition blockposition) {
        return this.getWalkTargetValue(blockposition, this.level);
    }

    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.0F;
    }

    @Override
    public boolean checkSpawnRules(GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn) {
        return this.getWalkTargetValue(this.blockPosition(), generatoraccess) >= 0.0F;
    }

    public boolean isPathFinding() {
        return !this.getNavigation().isDone();
    }

    @Override
    protected void tickLeash() {
        super.tickLeash();
        Entity entity = this.getLeashHolder();

        if (entity != null && entity.level == this.level) {
            this.restrictTo(entity.blockPosition(), 5);
            float f = this.distanceTo(entity);

            if (this instanceof EntityTameableAnimal && ((EntityTameableAnimal) this).isInSittingPose()) {
                if (f > 10.0F) {
                    this.dropLeash(true, true);
                }

                return;
            }

            this.onLeashDistance(f);
            if (f > 10.0F) {
                this.dropLeash(true, true);
                this.goalSelector.disableControlFlag(PathfinderGoal.Type.MOVE);
            } else if (f > 6.0F) {
                double d0 = (entity.getX() - this.getX()) / (double) f;
                double d1 = (entity.getY() - this.getY()) / (double) f;
                double d2 = (entity.getZ() - this.getZ()) / (double) f;

                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
            } else {
                this.goalSelector.enableControlFlag(PathfinderGoal.Type.MOVE);
                float f1 = 2.0F;
                Vec3D vec3d = (new Vec3D(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())).normalize().scale((double) Math.max(f - 2.0F, 0.0F));

                this.getNavigation().moveTo(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z, this.followLeashSpeed());
            }
        }

    }

    protected double followLeashSpeed() {
        return 1.0D;
    }

    protected void onLeashDistance(float f) {}
}
