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
    private PathEntity currentPath;
    private Vec3D targetLocation;
    private EntityLiving attackTarget;
    private boolean holdingPatternClockwise;

    public DragonControllerStrafe(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void c() {
        if (this.attackTarget == null) {
            DragonControllerStrafe.LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
        } else {
            double d0;
            double d1;
            double d2;

            if (this.currentPath != null && this.currentPath.c()) {
                d0 = this.attackTarget.locX();
                d1 = this.attackTarget.locZ();
                double d3 = d0 - this.dragon.locX();
                double d4 = d1 - this.dragon.locZ();

                d2 = Math.sqrt(d3 * d3 + d4 * d4);
                double d5 = Math.min(0.4000000059604645D + d2 / 80.0D - 1.0D, 10.0D);

                this.targetLocation = new Vec3D(d0, this.attackTarget.locY() + d5, d1);
            }

            d0 = this.targetLocation == null ? 0.0D : this.targetLocation.c(this.dragon.locX(), this.dragon.locY(), this.dragon.locZ());
            if (d0 < 100.0D || d0 > 22500.0D) {
                this.j();
            }

            d1 = 64.0D;
            if (this.attackTarget.f((Entity) this.dragon) < 4096.0D) {
                if (this.dragon.hasLineOfSight(this.attackTarget)) {
                    ++this.fireballCharge;
                    Vec3D vec3d = (new Vec3D(this.attackTarget.locX() - this.dragon.locX(), 0.0D, this.attackTarget.locZ() - this.dragon.locZ())).d();
                    Vec3D vec3d1 = (new Vec3D((double) MathHelper.sin(this.dragon.getYRot() * 0.017453292F), 0.0D, (double) (-MathHelper.cos(this.dragon.getYRot() * 0.017453292F)))).d();
                    float f = (float) vec3d1.b(vec3d);
                    float f1 = (float) (Math.acos((double) f) * 57.2957763671875D);

                    f1 += 0.5F;
                    if (this.fireballCharge >= 5 && f1 >= 0.0F && f1 < 10.0F) {
                        d2 = 1.0D;
                        Vec3D vec3d2 = this.dragon.e(1.0F);
                        double d6 = this.dragon.head.locX() - vec3d2.x * 1.0D;
                        double d7 = this.dragon.head.e(0.5D) + 0.5D;
                        double d8 = this.dragon.head.locZ() - vec3d2.z * 1.0D;
                        double d9 = this.attackTarget.locX() - d6;
                        double d10 = this.attackTarget.e(0.5D) - d7;
                        double d11 = this.attackTarget.locZ() - d8;

                        if (!this.dragon.isSilent()) {
                            this.dragon.level.a((EntityHuman) null, 1017, this.dragon.getChunkCoordinates(), 0);
                        }

                        EntityDragonFireball entitydragonfireball = new EntityDragonFireball(this.dragon.level, this.dragon, d9, d10, d11);

                        entitydragonfireball.setPositionRotation(d6, d7, d8, 0.0F, 0.0F);
                        this.dragon.level.addEntity(entitydragonfireball);
                        this.fireballCharge = 0;
                        if (this.currentPath != null) {
                            while (!this.currentPath.c()) {
                                this.currentPath.a();
                            }
                        }

                        this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
                    }
                } else if (this.fireballCharge > 0) {
                    --this.fireballCharge;
                }
            } else if (this.fireballCharge > 0) {
                --this.fireballCharge;
            }

        }
    }

    private void j() {
        if (this.currentPath == null || this.currentPath.c()) {
            int i = this.dragon.p();
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

            if (this.dragon.getEnderDragonBattle() != null && this.dragon.getEnderDragonBattle().c() > 0) {
                j %= 12;
                if (j < 0) {
                    j += 12;
                }
            } else {
                j -= 12;
                j &= 7;
                j += 12;
            }

            this.currentPath = this.dragon.a(i, j, (PathPoint) null);
            if (this.currentPath != null) {
                this.currentPath.a();
            }
        }

        this.k();
    }

    private void k() {
        if (this.currentPath != null && !this.currentPath.c()) {
            BlockPosition blockposition = this.currentPath.g();

            this.currentPath.a();
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
    public void d() {
        this.fireballCharge = 0;
        this.targetLocation = null;
        this.currentPath = null;
        this.attackTarget = null;
    }

    public void a(EntityLiving entityliving) {
        this.attackTarget = entityliving;
        int i = this.dragon.p();
        int j = this.dragon.q(this.attackTarget.locX(), this.attackTarget.locY(), this.attackTarget.locZ());
        int k = this.attackTarget.cW();
        int l = this.attackTarget.dc();
        double d0 = (double) k - this.dragon.locX();
        double d1 = (double) l - this.dragon.locZ();
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        double d3 = Math.min(0.4000000059604645D + d2 / 80.0D - 1.0D, 10.0D);
        int i1 = MathHelper.floor(this.attackTarget.locY() + d3);
        PathPoint pathpoint = new PathPoint(k, i1, l);

        this.currentPath = this.dragon.a(i, j, pathpoint);
        if (this.currentPath != null) {
            this.currentPath.a();
            this.k();
        }

    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.targetLocation;
    }

    @Override
    public DragonControllerPhase<DragonControllerStrafe> getControllerPhase() {
        return DragonControllerPhase.STRAFE_PLAYER;
    }
}
