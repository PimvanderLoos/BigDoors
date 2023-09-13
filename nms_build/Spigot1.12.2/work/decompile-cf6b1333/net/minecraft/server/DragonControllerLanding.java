package net.minecraft.server;

import javax.annotation.Nullable;

public class DragonControllerLanding extends AbstractDragonController {

    private Vec3D b;

    public DragonControllerLanding(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    public void b() {
        Vec3D vec3d = this.a.a(1.0F).a();

        vec3d.b(-0.7853982F);
        double d0 = this.a.bw.locX;
        double d1 = this.a.bw.locY + (double) (this.a.bw.length / 2.0F);
        double d2 = this.a.bw.locZ;

        for (int i = 0; i < 8; ++i) {
            double d3 = d0 + this.a.getRandom().nextGaussian() / 2.0D;
            double d4 = d1 + this.a.getRandom().nextGaussian() / 2.0D;
            double d5 = d2 + this.a.getRandom().nextGaussian() / 2.0D;

            this.a.world.addParticle(EnumParticle.DRAGON_BREATH, d3, d4, d5, -vec3d.x * 0.07999999821186066D + this.a.motX, -vec3d.y * 0.30000001192092896D + this.a.motY, -vec3d.z * 0.07999999821186066D + this.a.motZ, new int[0]);
            vec3d.b(0.19634955F);
        }

    }

    public void c() {
        if (this.b == null) {
            this.b = new Vec3D(this.a.world.q(WorldGenEndTrophy.a));
        }

        if (this.b.c(this.a.locX, this.a.locY, this.a.locZ) < 1.0D) {
            ((DragonControllerLandedFlame) this.a.getDragonControllerManager().b(DragonControllerPhase.f)).j();
            this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.g);
        }

    }

    public float f() {
        return 1.5F;
    }

    public float h() {
        float f = MathHelper.sqrt(this.a.motX * this.a.motX + this.a.motZ * this.a.motZ) + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return f1 / f;
    }

    public void d() {
        this.b = null;
    }

    @Nullable
    public Vec3D g() {
        return this.b;
    }

    public DragonControllerPhase<DragonControllerLanding> getControllerPhase() {
        return DragonControllerPhase.d;
    }
}
