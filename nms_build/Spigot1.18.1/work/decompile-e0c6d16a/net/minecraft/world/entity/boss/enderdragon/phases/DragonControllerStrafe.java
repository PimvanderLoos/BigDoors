package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityDragonFireball;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonControllerStrafe extends AbstractDragonController {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int FIREBALL_CHARGE_AMOUNT = 5;
    private int fireballCharge;
    @Nullable
    private PathEntity currentPath;
    @Nullable
    private Vec3D targetLocation;
    @Nullable
    private EntityLiving attackTarget;
    private boolean holdingPatternClockwise;

    public DragonControllerStrafe(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void doServerTick() {
        if (this.attackTarget == null) {
            DragonControllerStrafe.LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(DragonControllerPhase.HOLDING_PATTERN);
        } else {
            double d0;
            double d1;
            double d2;

            if (this.currentPath != null && this.currentPath.isDone()) {
                d0 = this.attackTarget.getX();
                d1 = this.attackTarget.getZ();
                double d3 = d0 - this.dragon.getX();
                double d4 = d1 - this.dragon.getZ();

                d2 = Math.sqrt(d3 * d3 + d4 * d4);
                double d5 = Math.min(0.4000000059604645D + d2 / 80.0D - 1.0D, 10.0D);

                this.targetLocation = new Vec3D(d0, this.attackTarget.getY() + d5, d1);
            }

            d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            if (d0 < 100.0D || d0 > 22500.0D) {
                this.findNewTarget();
            }

            d1 = 64.0D;
            if (this.attackTarget.distanceToSqr((Entity) this.dragon) < 4096.0D) {
                if (this.dragon.hasLineOfSight(this.attackTarget)) {
                    ++this.fireballCharge;
                    Vec3D vec3d = (new Vec3D(this.attackTarget.getX() - this.dragon.getX(), 0.0D, this.attackTarget.getZ() - this.dragon.getZ())).normalize();
                    Vec3D vec3d1 = (new Vec3D((double) MathHelper.sin(this.dragon.getYRot() * 0.017453292F), 0.0D, (double) (-MathHelper.cos(this.dragon.getYRot() * 0.017453292F)))).normalize();
                    float f = (float) vec3d1.dot(vec3d);
                    float f1 = (float) (Math.acos((double) f) * 57.2957763671875D);

                    f1 += 0.5F;
                    if (this.fireballCharge >= 5 && f1 >= 0.0F && f1 < 10.0F) {
                        d2 = 1.0D;
                        Vec3D vec3d2 = this.dragon.getViewVector(1.0F);
                        double d6 = this.dragon.head.getX() - vec3d2.x * 1.0D;
                        double d7 = this.dragon.head.getY(0.5D) + 0.5D;
                        double d8 = this.dragon.head.getZ() - vec3d2.z * 1.0D;
                        double d9 = this.attackTarget.getX() - d6;
                        double d10 = this.attackTarget.getY(0.5D) - d7;
                        double d11 = this.attackTarget.getZ() - d8;

                        if (!this.dragon.isSilent()) {
                            this.dragon.level.levelEvent((EntityHuman) null, 1017, this.dragon.blockPosition(), 0);
                        }

                        EntityDragonFireball entitydragonfireball = new EntityDragonFireball(this.dragon.level, this.dragon, d9, d10, d11);

                        entitydragonfireball.moveTo(d6, d7, d8, 0.0F, 0.0F);
                        this.dragon.level.addFreshEntity(entitydragonfireball);
                        this.fireballCharge = 0;
                        if (this.currentPath != null) {
                            while (!this.currentPath.isDone()) {
                                this.currentPath.advance();
                            }
                        }

                        this.dragon.getPhaseManager().setPhase(DragonControllerPhase.HOLDING_PATTERN);
                    }
                } else if (this.fireballCharge > 0) {
                    --this.fireballCharge;
                }
            } else if (this.fireballCharge > 0) {
                --this.fireballCharge;
            }

        }
    }

    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            int i = this.dragon.findClosestNode();
            int j = i;

            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.holdingPatternClockwise = !this.holdingPatternClockwise;
                j = i + 6;
            }

            if (this.holdingPatternClockwise) {
                ++j;
            } else {
                --j;
            }

            if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() > 0) {
                j %= 12;
                if (j < 0) {
                    j += 12;
                }
            } else {
                j -= 12;
                j &= 7;
                j += 12;
            }

            this.currentPath = this.dragon.findPath(i, j, (PathPoint) null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }

        this.navigateToNextPathNode();
    }

    private void navigateToNextPathNode() {
        if (this.currentPath != null && !this.currentPath.isDone()) {
            BlockPosition blockposition = this.currentPath.getNextNodePos();

            this.currentPath.advance();
            double d0 = (double) blockposition.getX();
            double d1 = (double) blockposition.getZ();

            double d2;

            do {
                d2 = (double) ((float) blockposition.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
            } while (d2 < (double) blockposition.getY());

            this.targetLocation = new Vec3D(d0, d2, d1);
        }

    }

    @Override
    public void begin() {
        this.fireballCharge = 0;
        this.targetLocation = null;
        this.currentPath = null;
        this.attackTarget = null;
    }

    public void setTarget(EntityLiving entityliving) {
        this.attackTarget = entityliving;
        int i = this.dragon.findClosestNode();
        int j = this.dragon.findClosestNode(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
        int k = this.attackTarget.getBlockX();
        int l = this.attackTarget.getBlockZ();
        double d0 = (double) k - this.dragon.getX();
        double d1 = (double) l - this.dragon.getZ();
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        double d3 = Math.min(0.4000000059604645D + d2 / 80.0D - 1.0D, 10.0D);
        int i1 = MathHelper.floor(this.attackTarget.getY() + d3);
        PathPoint pathpoint = new PathPoint(k, i1, l);

        this.currentPath = this.dragon.findPath(i, j, pathpoint);
        if (this.currentPath != null) {
            this.currentPath.advance();
            this.navigateToNextPathNode();
        }

    }

    @Nullable
    @Override
    public Vec3D getFlyTargetLocation() {
        return this.targetLocation;
    }

    @Override
    public DragonControllerPhase<DragonControllerStrafe> getPhase() {
        return DragonControllerPhase.STRAFE_PLAYER;
    }
}
