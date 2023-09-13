package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;

public class DragonControllerLandedAttack extends AbstractDragonControllerLanded {

    private static final int ROAR_DURATION = 40;
    private int attackingTicks;

    public DragonControllerLandedAttack(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void b() {
        this.dragon.level.a(this.dragon.locX(), this.dragon.locY(), this.dragon.locZ(), SoundEffects.ENDER_DRAGON_GROWL, this.dragon.getSoundCategory(), 2.5F, 0.8F + this.dragon.getRandom().nextFloat() * 0.3F, false);
    }

    @Override
    public void c() {
        if (this.attackingTicks++ >= 40) {
            this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.SITTING_FLAMING);
        }

    }

    @Override
    public void d() {
        this.attackingTicks = 0;
    }

    @Override
    public DragonControllerPhase<DragonControllerLandedAttack> getControllerPhase() {
        return DragonControllerPhase.SITTING_ATTACKING;
    }
}
