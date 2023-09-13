package net.minecraft.server;

public class MobEffectHealthBoost extends MobEffectList {

    public MobEffectHealthBoost(boolean flag, int i) {
        super(flag, i);
    }

    public void a(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        super.a(entityliving, attributemapbase, i);
        if (entityliving.getHealth() > entityliving.getMaxHealth()) {
            entityliving.setHealth(entityliving.getMaxHealth());
        }

    }
}
