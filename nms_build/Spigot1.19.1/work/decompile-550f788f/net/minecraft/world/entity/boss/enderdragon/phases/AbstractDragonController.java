package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public abstract class AbstractDragonController implements IDragonController {

    protected final EntityEnderDragon dragon;

    public AbstractDragonController(EntityEnderDragon entityenderdragon) {
        this.dragon = entityenderdragon;
    }

    @Override
    public boolean isSitting() {
        return false;
    }

    @Override
    public void doClientTick() {}

    @Override
    public void doServerTick() {}

    @Override
    public void onCrystalDestroyed(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource, @Nullable EntityHuman entityhuman) {}

    @Override
    public void begin() {}

    @Override
    public void end() {}

    @Override
    public float getFlySpeed() {
        return 0.6F;
    }

    @Nullable
    @Override
    public Vec3D getFlyTargetLocation() {
        return null;
    }

    @Override
    public float onHurt(DamageSource damagesource, float f) {
        return f;
    }

    @Override
    public float getTurnSpeed() {
        float f = (float) this.dragon.getDeltaMovement().horizontalDistance() + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return 0.7F / f1 / f;
    }
}
