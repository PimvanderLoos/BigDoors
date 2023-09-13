package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.projectile.EntityArrow;

public abstract class AbstractDragonControllerLanded extends AbstractDragonController {

    public AbstractDragonControllerLanded(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public boolean isSitting() {
        return true;
    }

    @Override
    public float onHurt(DamageSource damagesource, float f) {
        if (damagesource.getDirectEntity() instanceof EntityArrow) {
            damagesource.getDirectEntity().setSecondsOnFire(1);
            return 0.0F;
        } else {
            return super.onHurt(damagesource, f);
        }
    }
}
