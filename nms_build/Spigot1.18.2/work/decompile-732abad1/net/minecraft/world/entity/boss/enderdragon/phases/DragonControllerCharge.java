package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.phys.Vec3D;
import org.slf4j.Logger;

public class DragonControllerCharge extends AbstractDragonController {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CHARGE_RECOVERY_TIME = 10;
    @Nullable
    private Vec3D targetLocation;
    private int timeSinceCharge;

    public DragonControllerCharge(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            DragonControllerCharge.LOGGER.warn("Aborting charge player as no target was set.");
            this.dragon.getPhaseManager().setPhase(DragonControllerPhase.HOLDING_PATTERN);
        } else if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
            this.dragon.getPhaseManager().setPhase(DragonControllerPhase.HOLDING_PATTERN);
        } else {
            double d0 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

            if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
                ++this.timeSinceCharge;
            }

        }
    }

    @Override
    public void begin() {
        this.targetLocation = null;
        this.timeSinceCharge = 0;
    }

    public void setTarget(Vec3D vec3d) {
        this.targetLocation = vec3d;
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
    public DragonControllerPhase<DragonControllerCharge> getPhase() {
        return DragonControllerPhase.CHARGING_PLAYER;
    }
}
