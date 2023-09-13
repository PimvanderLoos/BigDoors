package net.minecraft.server;

public class SoundEffectType {

    public static final SoundEffectType a = new SoundEffectType(1.0F, 1.0F, SoundEffects.iZ, SoundEffects.jh, SoundEffects.je, SoundEffects.jd, SoundEffects.jc);
    public static final SoundEffectType b = new SoundEffectType(1.0F, 1.0F, SoundEffects.cr, SoundEffects.cv, SoundEffects.cu, SoundEffects.ct, SoundEffects.cs);
    public static final SoundEffectType c = new SoundEffectType(1.0F, 1.0F, SoundEffects.cm, SoundEffects.cq, SoundEffects.cp, SoundEffects.co, SoundEffects.cn);
    public static final SoundEffectType d = new SoundEffectType(1.0F, 1.0F, SoundEffects.hI, SoundEffects.hQ, SoundEffects.hN, SoundEffects.hM, SoundEffects.hL);
    public static final SoundEffectType e = new SoundEffectType(1.0F, 1.5F, SoundEffects.dZ, SoundEffects.ef, SoundEffects.ec, SoundEffects.eb, SoundEffects.ea);
    public static final SoundEffectType f = new SoundEffectType(1.0F, 1.0F, SoundEffects.ch, SoundEffects.cl, SoundEffects.ck, SoundEffects.cj, SoundEffects.ci);
    public static final SoundEffectType g = new SoundEffectType(1.0F, 1.0F, SoundEffects.al, SoundEffects.ap, SoundEffects.ao, SoundEffects.an, SoundEffects.am);
    public static final SoundEffectType h = new SoundEffectType(1.0F, 1.0F, SoundEffects.gn, SoundEffects.gr, SoundEffects.gq, SoundEffects.gp, SoundEffects.go);
    public static final SoundEffectType i = new SoundEffectType(1.0F, 1.0F, SoundEffects.hu, SoundEffects.hy, SoundEffects.hx, SoundEffects.hw, SoundEffects.hv);
    public static final SoundEffectType j = new SoundEffectType(1.0F, 1.0F, SoundEffects.dy, SoundEffects.dC, SoundEffects.dB, SoundEffects.dA, SoundEffects.dz);
    public static final SoundEffectType k = new SoundEffectType(0.3F, 1.0F, SoundEffects.b, SoundEffects.h, SoundEffects.g, SoundEffects.e, SoundEffects.d);
    public static final SoundEffectType l = new SoundEffectType(1.0F, 1.0F, SoundEffects.gZ, SoundEffects.hh, SoundEffects.hf, SoundEffects.hc, SoundEffects.hb);
    public final float m;
    public final float n;
    private final SoundEffect o;
    private final SoundEffect p;
    private final SoundEffect q;
    private final SoundEffect r;
    private final SoundEffect s;

    public SoundEffectType(float f, float f1, SoundEffect soundeffect, SoundEffect soundeffect1, SoundEffect soundeffect2, SoundEffect soundeffect3, SoundEffect soundeffect4) {
        this.m = f;
        this.n = f1;
        this.o = soundeffect;
        this.p = soundeffect1;
        this.q = soundeffect2;
        this.r = soundeffect3;
        this.s = soundeffect4;
    }

    public float a() {
        return this.m;
    }

    public float b() {
        return this.n;
    }

    public SoundEffect d() {
        return this.p;
    }

    public SoundEffect e() {
        return this.q;
    }

    public SoundEffect g() {
        return this.s;
    }
}
