package net.minecraft.world.effect;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class MobEffectAttackDamage extends MobEffectList {

    protected final double multiplier;

    protected MobEffectAttackDamage(MobEffectInfo mobeffectinfo, int i, double d0) {
        super(mobeffectinfo, i);
        this.multiplier = d0;
    }

    @Override
    public double getAttributeModifierValue(int i, AttributeModifier attributemodifier) {
        return this.multiplier * (double) (i + 1);
    }
}
