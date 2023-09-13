package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerDying extends AbstractDragonController {

    @Nullable
    private Vec3D targetLocation;
    private int time;

    public DragonControllerDying(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void doClientTick() {
        if (this.time++ % 10 == 0) {
            float f = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.dragon.getRandom().nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;

            this.dragon.level.addParticle(Particles.EXPLOSION_EMITTER, this.dragon.getX() + (double) f, this.dragon.getY() + 2.0D + (double) f1, this.dragon.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public void doServerTick() {
        ++this.time;
        if (this.targetLocation == null) {
            BlockPosition blockposition = this.dragon.level.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING, WorldGenEndTrophy.END_PODIUM_LOCATION);

            this.targetLocation = Vec3D.atBottomCenterOf(blockposition);
        }

        double d0 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

        if (d0 >= 100.0D && d0 <= 22500.0D && !this.dragon.horizontalCollision && !this.dragon.verticalCollision) {
            this.dragon.setHealth(1.0F);
        } else {
            this.dragon.setHealth(0.0F);
        }

    }

    @Override
    public void begin() {
        this.targetLocation = null;
        this.time = 0;
    }

    @Override
    public float getFlySpeed() {
        return 3.0F;
    }

    @Nullable
    @Override
    public Vec3D getFlyTargetLocation() {
        return this.targetLocation;
    }

    @Override
    public DragonControllerPhase<DragonControllerDying> getPhase() {
        return DragonControllerPhase.DYING;
    }
}
