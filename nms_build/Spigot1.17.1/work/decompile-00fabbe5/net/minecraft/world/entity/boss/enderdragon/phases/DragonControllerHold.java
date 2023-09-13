package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerHold extends AbstractDragonController {

    private static final PathfinderTargetCondition NEW_TARGET_TARGETING = PathfinderTargetCondition.a().d();
    private PathEntity currentPath;
    private Vec3D targetLocation;
    private boolean clockwise;

    public DragonControllerHold(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public DragonControllerPhase<DragonControllerHold> getControllerPhase() {
        return DragonControllerPhase.HOLDING_PATTERN;
    }

    @Override
    public void c() {
        double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.c(this.dragon.locX(), this.dragon.locY(), this.dragon.locZ());

        if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.j();
        }

    }

    @Override
    public void d() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.targetLocation;
    }

    private void j() {
        int i;

        if (this.currentPath != null && this.currentPath.c()) {
            BlockPosition blockposition = this.dragon.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPosition(WorldGenEndTrophy.END_PODIUM_LOCATION));

            i = this.dragon.getEnderDragonBattle() == null ? 0 : this.dragon.getEnderDragonBattle().c();
            if (this.dragon.getRandom().nextInt(i + 3) == 0) {
                this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING_APPROACH);
                return;
            }

            double d0 = 64.0D;
            EntityHuman entityhuman = this.dragon.level.a(DragonControllerHold.NEW_TARGET_TARGETING, this.dragon, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

            if (entityhuman != null) {
                d0 = blockposition.a((IPosition) entityhuman.getPositionVector(), true) / 512.0D;
            }

            if (entityhuman != null && (this.dragon.getRandom().nextInt(MathHelper.a((int) d0) + 2) == 0 || this.dragon.getRandom().nextInt(i + 2) == 0)) {
                this.a(entityhuman);
                return;
            }
        }

        if (this.currentPath == null || this.currentPath.c()) {
            int j = this.dragon.p();

            i = j;
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.clockwise = !this.clockwise;
                i = j + 6;
            }

            if (this.clockwise) {
                ++i;
            } else {
                --i;
            }

            if (this.dragon.getEnderDragonBattle() != null && this.dragon.getEnderDragonBattle().c() >= 0) {
                i %= 12;
                if (i < 0) {
                    i += 12;
                }
            } else {
                i -= 12;
                i &= 7;
                i += 12;
            }

            this.currentPath = this.dragon.a(j, i, (PathPoint) null);
            if (this.currentPath != null) {
                this.currentPath.a();
            }
        }

        this.k();
    }

    private void a(EntityHuman entityhuman) {
        this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.STRAFE_PLAYER);
        ((DragonControllerStrafe) this.dragon.getDragonControllerManager().b(DragonControllerPhase.STRAFE_PLAYER)).a(entityhuman);
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
    public void a(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource, @Nullable EntityHuman entityhuman) {
        if (entityhuman != null && this.dragon.c((EntityLiving) entityhuman)) {
            this.a(entityhuman);
        }

    }
}
