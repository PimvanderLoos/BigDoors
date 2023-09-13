package net.minecraft.world.entity.ai.control;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;

public class ControllerLookDolphin extends ControllerLook {

    private final int h;

    public ControllerLookDolphin(EntityInsentient entityinsentient, int i) {
        super(entityinsentient);
        this.h = i;
    }

    @Override
    public void a() {
        if (this.d) {
            this.d = false;
            this.a.aC = this.a(this.a.aC, this.h() + 20.0F, this.b);
            this.a.pitch = this.a(this.a.pitch, this.g() + 10.0F, this.c);
        } else {
            if (this.a.getNavigation().m()) {
                this.a.pitch = this.a(this.a.pitch, 0.0F, 5.0F);
            }

            this.a.aC = this.a(this.a.aC, this.a.aA, this.b);
        }

        float f = MathHelper.g(this.a.aC - this.a.aA);

        if (f < (float) (-this.h)) {
            this.a.aA -= 4.0F;
        } else if (f > (float) this.h) {
            this.a.aA += 4.0F;
        }

    }
}
