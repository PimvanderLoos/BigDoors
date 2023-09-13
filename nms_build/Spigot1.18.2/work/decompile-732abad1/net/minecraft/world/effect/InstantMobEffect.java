package net.minecraft.world.effect;

public class InstantMobEffect extends MobEffectList {

    public InstantMobEffect(MobEffectInfo mobeffectinfo, int i) {
        super(mobeffectinfo, i);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return i >= 1;
    }
}
