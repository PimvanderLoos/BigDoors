package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerHover extends AbstractDragonController {

    private Vec3D b;

    public DragonControllerHover(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void c() {
        if (this.b == null) {
            this.b = this.a.getPositionVector();
        }

    }

    @Override
    public boolean a() {
        return true;
    }

    @Override
    public void d() {
        this.b = null;
    }

    @Override
    public float f() {
        return 1.0F;
    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.b;
    }

    @Override
    public DragonControllerPhase<DragonControllerHover> getControllerPhase() {
        return DragonControllerPhase.HOVER;
    }
}
