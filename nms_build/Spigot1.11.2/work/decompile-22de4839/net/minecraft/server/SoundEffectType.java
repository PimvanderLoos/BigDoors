package net.minecraft.server;

public class SoundEffectType {

    public static final SoundEffectType a = new SoundEffectType(1.0F, 1.0F, SoundEffects.hV, SoundEffects.id, SoundEffects.ia, SoundEffects.hZ, SoundEffects.hY);
    public static final SoundEffectType b = new SoundEffectType(1.0F, 1.0F, SoundEffects.cl, SoundEffects.cp, SoundEffects.co, SoundEffects.cn, SoundEffects.cm);
    public static final SoundEffectType c = new SoundEffectType(1.0F, 1.0F, SoundEffects.cg, SoundEffects.ck, SoundEffects.cj, SoundEffects.ci, SoundEffects.ch);
    public static final SoundEffectType d = new SoundEffectType(1.0F, 1.0F, SoundEffects.gH, SoundEffects.gP, SoundEffects.gM, SoundEffects.gL, SoundEffects.gK);
    public static final SoundEffectType e = new SoundEffectType(1.0F, 1.5F, SoundEffects.dM, SoundEffects.dS, SoundEffects.dP, SoundEffects.dO, SoundEffects.dN);
    public static final SoundEffectType f = new SoundEffectType(1.0F, 1.0F, SoundEffects.cb, SoundEffects.cf, SoundEffects.ce, SoundEffects.cd, SoundEffects.cc);
    public static final SoundEffectType g = new SoundEffectType(1.0F, 1.0F, SoundEffects.ai, SoundEffects.am, SoundEffects.al, SoundEffects.ak, SoundEffects.aj);
    public static final SoundEffectType h = new SoundEffectType(1.0F, 1.0F, SoundEffects.fm, SoundEffects.fq, SoundEffects.fp, SoundEffects.fo, SoundEffects.fn);
    public static final SoundEffectType i = new SoundEffectType(1.0F, 1.0F, SoundEffects.gt, SoundEffects.gx, SoundEffects.gw, SoundEffects.gv, SoundEffects.gu);
    public static final SoundEffectType j = new SoundEffectType(1.0F, 1.0F, SoundEffects.dl, SoundEffects.dp, SoundEffects.do, SoundEffects.dn, SoundEffects.dm);
    public static final SoundEffectType k = new SoundEffectType(0.3F, 1.0F, SoundEffects.b, SoundEffects.h, SoundEffects.g, SoundEffects.e, SoundEffects.d);
    public static final SoundEffectType l = new SoundEffectType(1.0F, 1.0F, SoundEffects.fY, SoundEffects.gg, SoundEffects.ge, SoundEffects.gb, SoundEffects.ga);
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
