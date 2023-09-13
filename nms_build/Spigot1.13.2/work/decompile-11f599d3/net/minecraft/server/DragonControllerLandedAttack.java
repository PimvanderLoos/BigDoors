package net.minecraft.server;

public class DragonControllerLandedAttack extends AbstractDragonControllerLanded {

    private int b;

    public DragonControllerLandedAttack(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    public void b() {
        this.a.world.a(this.a.locX, this.a.locY, this.a.locZ, SoundEffects.ENTITY_ENDER_DRAGON_GROWL, this.a.bV(), 2.5F, 0.8F + this.a.getRandom().nextFloat() * 0.3F, false);
    }

    public void c() {
        if (this.b++ >= 40) {
            this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.SITTING_FLAMING);
        }

    }

    public void d() {
        this.b = 0;
    }

    public DragonControllerPhase<DragonControllerLandedAttack> getControllerPhase() {
        return DragonControllerPhase.SITTING_ATTACKING;
    }
}
