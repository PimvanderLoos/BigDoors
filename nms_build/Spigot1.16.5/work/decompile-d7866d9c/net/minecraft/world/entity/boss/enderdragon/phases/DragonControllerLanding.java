package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerLanding extends AbstractDragonController {

    private Vec3D b;

    public DragonControllerLanding(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void b() {
        Vec3D vec3d = this.a.x(1.0F).d();

        vec3d.b(-0.7853982F);
        double d0 = this.a.bo.locX();
        double d1 = this.a.bo.e(0.5D);
        double d2 = this.a.bo.locZ();

        for (int i = 0; i < 8; ++i) {
            Random random = this.a.getRandom();
            double d3 = d0 + random.nextGaussian() / 2.0D;
            double d4 = d1 + random.nextGaussian() / 2.0D;
            double d5 = d2 + random.nextGaussian() / 2.0D;
            Vec3D vec3d1 = this.a.getMot();

            this.a.world.addParticle(Particles.DRAGON_BREATH, d3, d4, d5, -vec3d.x * 0.07999999821186066D + vec3d1.x, -vec3d.y * 0.30000001192092896D + vec3d1.y, -vec3d.z * 0.07999999821186066D + vec3d1.z);
            vec3d.b(0.19634955F);
        }

    }

    @Override
    public void c() {
        if (this.b == null) {
            this.b = Vec3D.c((BaseBlockPosition) this.a.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.a));
        }

        if (this.b.c(this.a.locX(), this.a.locY(), this.a.locZ()) < 1.0D) {
            ((DragonControllerLandedFlame) this.a.getDragonControllerManager().b(DragonControllerPhase.SITTING_FLAMING)).j();
            this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.SITTING_SCANNING);
        }

    }

    @Override
    public float f() {
        return 1.5F;
    }

    @Override
    public float h() {
        float f = MathHelper.sqrt(Entity.c(this.a.getMot())) + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return f1 / f;
    }

    @Override
    public void d() {
        this.b = null;
    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.b;
    }

    @Override
    public DragonControllerPhase<DragonControllerLanding> getControllerPhase() {
        return DragonControllerPhase.LANDING;
    }
}
