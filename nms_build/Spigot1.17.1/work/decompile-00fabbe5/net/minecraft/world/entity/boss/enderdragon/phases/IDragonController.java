package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public interface IDragonController {

    boolean a();

    void b();

    void c();

    void a(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource, @Nullable EntityHuman entityhuman);

    void d();

    void e();

    float f();

    float h();

    DragonControllerPhase<? extends IDragonController> getControllerPhase();

    @Nullable
    Vec3D g();

    float a(DamageSource damagesource, float f);
}
