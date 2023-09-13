package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerLanding extends AbstractDragonController {

    @Nullable
    private Vec3D targetLocation;

    public DragonControllerLanding(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void doClientTick() {
        Vec3D vec3d = this.dragon.getHeadLookVector(1.0F).normalize();

        vec3d.yRot(-0.7853982F);
        double d0 = this.dragon.head.getX();
        double d1 = this.dragon.head.getY(0.5D);
        double d2 = this.dragon.head.getZ();

        for (int i = 0; i < 8; ++i) {
            Random random = this.dragon.getRandom();
            double d3 = d0 + random.nextGaussian() / 2.0D;
            double d4 = d1 + random.nextGaussian() / 2.0D;
            double d5 = d2 + random.nextGaussian() / 2.0D;
            Vec3D vec3d1 = this.dragon.getDeltaMovement();

            this.dragon.level.addParticle(Particles.DRAGON_BREATH, d3, d4, d5, -vec3d.x * 0.07999999821186066D + vec3d1.x, -vec3d.y * 0.30000001192092896D + vec3d1.y, -vec3d.z * 0.07999999821186066D + vec3d1.z);
            vec3d.yRot(0.19634955F);
        }

    }

    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            this.targetLocation = Vec3D.atBottomCenterOf(this.dragon.level.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.END_PODIUM_LOCATION));
        }

        if (this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0D) {
            ((DragonControllerLandedFlame) this.dragon.getPhaseManager().getPhase(DragonControllerPhase.SITTING_FLAMING)).resetFlameCount();
            this.dragon.getPhaseManager().setPhase(DragonControllerPhase.SITTING_SCANNING);
        }

    }

    @Override
    public float getFlySpeed() {
        return 1.5F;
    }

    @Override
    public float getTurnSpeed() {
        float f = (float) this.dragon.getDeltaMovement().horizontalDistance() + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return f1 / f;
    }

    @Override
    public void begin() {
        this.targetLocation = null;
    }

    @Nullable
    @Override
    public Vec3D getFlyTargetLocation() {
        return this.targetLocation;
    }

    @Override
    public DragonControllerPhase<DragonControllerLanding> getPhase() {
        return DragonControllerPhase.LANDING;
    }
}
