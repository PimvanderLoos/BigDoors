package net.minecraft.server;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonControllerCharge extends AbstractDragonController {

    private static final Logger b = LogManager.getLogger();
    private Vec3D c;
    private int d;

    public DragonControllerCharge(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    public void c() {
        if (this.c == null) {
            DragonControllerCharge.b.warn("Aborting charge player as no target was set.");
            this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.a);
        } else if (this.d > 0 && this.d++ >= 10) {
            this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.a);
        } else {
            double d0 = this.c.c(this.a.locX, this.a.locY, this.a.locZ);

            if (d0 < 100.0D || d0 > 22500.0D || this.a.positionChanged || this.a.B) {
                ++this.d;
            }

        }
    }

    public void d() {
        this.c = null;
        this.d = 0;
    }

    public void a(Vec3D vec3d) {
        this.c = vec3d;
    }

    public float f() {
        return 3.0F;
    }

    @Nullable
    public Vec3D g() {
        return this.c;
    }

    public DragonControllerPhase<DragonControllerCharge> getControllerPhase() {
        return DragonControllerPhase.i;
    }
}
