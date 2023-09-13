package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public abstract class AbstractDragonController implements IDragonController {

    protected final EntityEnderDragon a;

    public AbstractDragonController(EntityEnderDragon entityenderdragon) {
        this.a = entityenderdragon;
    }

    @Override
    public boolean a() {
        return false;
    }

    @Override
    public void b() {}

    @Override
    public void c() {}

    @Override
    public void a(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource, @Nullable EntityHuman entityhuman) {}

    @Override
    public void d() {}

    @Override
    public void e() {}

    @Override
    public float f() {
        return 0.6F;
    }

    @Nullable
    @Override
    public Vec3D g() {
        return null;
    }

    @Override
    public float a(DamageSource damagesource, float f) {
        return f;
    }

    @Override
    public float h() {
        float f = MathHelper.sqrt(Entity.c(this.a.getMot())) + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return 0.7F / f1 / f;
    }
}
