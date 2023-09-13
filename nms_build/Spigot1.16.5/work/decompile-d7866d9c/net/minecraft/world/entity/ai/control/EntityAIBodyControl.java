package net.minecraft.world.entity.ai.control;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;

public class EntityAIBodyControl {

    private final EntityInsentient a;
    private int b;
    private float c;

    public EntityAIBodyControl(EntityInsentient entityinsentient) {
        this.a = entityinsentient;
    }

    public void a() {
        if (this.f()) {
            this.a.aA = this.a.yaw;
            this.c();
            this.c = this.a.aC;
            this.b = 0;
        } else {
            if (this.e()) {
                if (Math.abs(this.a.aC - this.c) > 15.0F) {
                    this.b = 0;
                    this.c = this.a.aC;
                    this.b();
                } else {
                    ++this.b;
                    if (this.b > 10) {
                        this.d();
                    }
                }
            }

        }
    }

    private void b() {
        this.a.aA = MathHelper.b(this.a.aA, this.a.aC, (float) this.a.Q());
    }

    private void c() {
        this.a.aC = MathHelper.b(this.a.aC, this.a.aA, (float) this.a.Q());
    }

    private void d() {
        int i = this.b - 10;
        float f = MathHelper.a((float) i / 10.0F, 0.0F, 1.0F);
        float f1 = (float) this.a.Q() * (1.0F - f);

        this.a.aA = MathHelper.b(this.a.aA, this.a.aC, f1);
    }

    private boolean e() {
        return this.a.getPassengers().isEmpty() || !(this.a.getPassengers().get(0) instanceof EntityInsentient);
    }

    private boolean f() {
        double d0 = this.a.locX() - this.a.lastX;
        double d1 = this.a.locZ() - this.a.lastZ;

        return d0 * d0 + d1 * d1 > 2.500000277905201E-7D;
    }
}
