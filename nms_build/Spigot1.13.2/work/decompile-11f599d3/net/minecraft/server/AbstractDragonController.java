package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class AbstractDragonController implements IDragonController {

    protected final EntityEnderDragon a;

    public AbstractDragonController(EntityEnderDragon entityenderdragon) {
        this.a = entityenderdragon;
    }

    public boolean a() {
        return false;
    }

    public void b() {}

    public void c() {}

    public void a(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource, @Nullable EntityHuman entityhuman) {}

    public void d() {}

    public void e() {}

    public float f() {
        return 0.6F;
    }

    @Nullable
    public Vec3D g() {
        return null;
    }

    public float a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
        return f;
    }

    public float h() {
        float f = MathHelper.sqrt(this.a.motX * this.a.motX + this.a.motZ * this.a.motZ) + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return 0.7F / f1 / f;
    }
}
