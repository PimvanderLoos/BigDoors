package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
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

    private static final PathfinderTargetCondition NEW_TARGET_TARGETING = PathfinderTargetCondition.forCombat().ignoreLineOfSight();
    @Nullable
    private PathEntity currentPath;
    @Nullable
    private Vec3D targetLocation;
    private boolean clockwise;

    public DragonControllerHold(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public DragonControllerPhase<DragonControllerHold> getPhase() {
        return DragonControllerPhase.HOLDING_PATTERN;
    }

    @Override
    public void doServerTick() {
        double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

        if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.findNewTarget();
        }

    }

    @Override
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Nullable
    @Override
    public Vec3D getFlyTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        int i;

        if (this.currentPath != null && this.currentPath.isDone()) {
            BlockPosition blockposition = this.dragon.level.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPosition(WorldGenEndTrophy.END_PODIUM_LOCATION));

            i = this.dragon.getDragonFight() == null ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
            if (this.dragon.getRandom().nextInt(i + 3) == 0) {
                this.dragon.getPhaseManager().setPhase(DragonControllerPhase.LANDING_APPROACH);
                return;
            }

            double d0 = 64.0D;
            EntityHuman entityhuman = this.dragon.level.getNearestPlayer(DragonControllerHold.NEW_TARGET_TARGETING, this.dragon, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

            if (entityhuman != null) {
                d0 = blockposition.distSqr((IPosition) entityhuman.position(), true) / 512.0D;
            }

            if (entityhuman != null && (this.dragon.getRandom().nextInt(MathHelper.abs((int) d0) + 2) == 0 || this.dragon.getRandom().nextInt(i + 2) == 0)) {
                this.strafePlayer(entityhuman);
                return;
            }
        }

        if (this.currentPath == null || this.currentPath.isDone()) {
            int j = this.dragon.findClosestNode();

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

            if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() >= 0) {
                i %= 12;
                if (i < 0) {
                    i += 12;
                }
            } else {
                i -= 12;
                i &= 7;
                i += 12;
            }

            this.currentPath = this.dragon.findPath(j, i, (PathPoint) null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }

        this.navigateToNextPathNode();
    }

    private void strafePlayer(EntityHuman entityhuman) {
        this.dragon.getPhaseManager().setPhase(DragonControllerPhase.STRAFE_PLAYER);
        ((DragonControllerStrafe) this.dragon.getPhaseManager().getPhase(DragonControllerPhase.STRAFE_PLAYER)).setTarget(entityhuman);
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
    public void onCrystalDestroyed(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource, @Nullable EntityHuman entityhuman) {
        if (entityhuman != null && this.dragon.canAttack(entityhuman)) {
            this.strafePlayer(entityhuman);
        }

    }
}
