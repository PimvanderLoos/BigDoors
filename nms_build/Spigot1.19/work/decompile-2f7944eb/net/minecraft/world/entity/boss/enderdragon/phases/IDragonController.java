package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public interface IDragonController {

    boolean isSitting();

    void doClientTick();

    void doServerTick();

    void onCrystalDestroyed(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource, @Nullable EntityHuman entityhuman);

    void begin();

    void end();

    float getFlySpeed();

    float getTurnSpeed();

    DragonControllerPhase<? extends IDragonController> getPhase();

    @Nullable
    Vec3D getFlyTargetLocation();

    float onHurt(DamageSource damagesource, float f);
}
