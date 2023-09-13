package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerHover extends AbstractDragonController {

    @Nullable
    private Vec3D targetLocation;

    public DragonControllerHover(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            this.targetLocation = this.dragon.position();
        }

    }

    @Override
    public boolean isSitting() {
        return true;
    }

    @Override
    public void begin() {
        this.targetLocation = null;
    }

    @Override
    public float getFlySpeed() {
        return 1.0F;
    }

    @Nullable
    @Override
    public Vec3D getFlyTargetLocation() {
        return this.targetLocation;
    }

    @Override
    public DragonControllerPhase<DragonControllerHover> getPhase() {
        return DragonControllerPhase.HOVERING;
    }
}
