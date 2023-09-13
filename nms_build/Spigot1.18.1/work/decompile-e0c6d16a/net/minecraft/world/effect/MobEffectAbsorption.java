package net.minecraft.world.effect;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;

public class MobEffectAbsorption extends MobEffectList {

    protected MobEffectAbsorption(MobEffectInfo mobeffectinfo, int i) {
        super(mobeffectinfo, i);
    }

    @Override
    public void removeAttributeModifiers(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        entityliving.setAbsorptionAmount(entityliving.getAbsorptionAmount() - (float) (4 * (i + 1)));
        super.removeAttributeModifiers(entityliving, attributemapbase, i);
    }

    @Override
    public void addAttributeModifiers(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        entityliving.setAbsorptionAmount(entityliving.getAbsorptionAmount() + (float) (4 * (i + 1)));
        super.addAttributeModifiers(entityliving, attributemapbase, i);
    }
}
