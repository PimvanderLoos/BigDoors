package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerHover extends AbstractDragonController {

    private Vec3D targetLocation;

    public DragonControllerHover(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void c() {
        if (this.targetLocation == null) {
            this.targetLocation = this.dragon.getPositionVector();
        }

    }

    @Override
    public boolean a() {
        return true;
    }

    @Override
    public void d() {
        this.targetLocation = null;
    }

    @Override
    public float f() {
        return 1.0F;
    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.targetLocation;
    }

    @Override
    public DragonControllerPhase<DragonControllerHover> getControllerPhase() {
        return DragonControllerPhase.HOVERING;
    }
}
