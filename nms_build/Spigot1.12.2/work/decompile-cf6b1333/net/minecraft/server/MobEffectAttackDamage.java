package net.minecraft.server;

public class MobEffectAttackDamage extends MobEffectList {

    protected final double a;

    protected MobEffectAttackDamage(boolean flag, int i, double d0) {
        super(flag, i);
        this.a = d0;
    }

    public double a(int i, AttributeModifier attributemodifier) {
        return this.a * (double) (i + 1);
    }
}
