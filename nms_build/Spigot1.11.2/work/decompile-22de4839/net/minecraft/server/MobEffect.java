package net.minecraft.server;

import com.google.common.collect.ComparisonChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobEffect implements Comparable<MobEffect> {

    private static final Logger a = LogManager.getLogger();
    private final MobEffectList b;
    private int duration;
    private int amplification;
    private boolean splash;
    private boolean ambient;
    private boolean h;

    public MobEffect(MobEffectList mobeffectlist) {
        this(mobeffectlist, 0, 0);
    }

    public MobEffect(MobEffectList mobeffectlist, int i) {
        this(mobeffectlist, i, 0);
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j) {
        this(mobeffectlist, i, j, false, true);
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j, boolean flag, boolean flag1) {
        this.b = mobeffectlist;
        this.duration = i;
        this.amplification = j;
        this.ambient = flag;
        this.h = flag1;
    }

    public MobEffect(MobEffect mobeffect) {
        this.b = mobeffect.b;
        this.duration = mobeffect.duration;
        this.amplification = mobeffect.amplification;
        this.ambient = mobeffect.ambient;
        this.h = mobeffect.h;
    }

    public void a(MobEffect mobeffect) {
        if (this.b != mobeffect.b) {
            MobEffect.a.warn("This method should only be called for matching effects!");
        }

        if (mobeffect.amplification > this.amplification) {
            this.amplification = mobeffect.amplification;
            this.duration = mobeffect.duration;
        } else if (mobeffect.amplification == this.amplification && this.duration < mobeffect.duration) {
            this.duration = mobeffect.duration;
        } else if (!mobeffect.ambient && this.ambient) {
            this.ambient = mobeffect.ambient;
        }

        this.h = mobeffect.h;
    }

    public MobEffectList getMobEffect() {
        return this.b;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplification;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean isShowParticles() {
        return this.h;
    }

    public boolean tick(EntityLiving entityliving) {
        if (this.duration > 0) {
            if (this.b.a(this.duration, this.amplification)) {
                this.b(entityliving);
            }

            this.h();
        }

        return this.duration > 0;
    }

    private int h() {
        return --this.duration;
    }

    public void b(EntityLiving entityliving) {
        if (this.duration > 0) {
            this.b.tick(entityliving, this.amplification);
        }

    }

    public String f() {
        return this.b.a();
    }

    public String toString() {
        String s;

        if (this.amplification > 0) {
            s = this.f() + " x " + (this.amplification + 1) + ", Duration: " + this.duration;
        } else {
            s = this.f() + ", Duration: " + this.duration;
        }

        if (this.splash) {
            s = s + ", Splash: true";
        }

        if (!this.h) {
            s = s + ", Particles: false";
        }

        return s;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MobEffect)) {
            return false;
        } else {
            MobEffect mobeffect = (MobEffect) object;

            return this.duration == mobeffect.duration && this.amplification == mobeffect.amplification && this.splash == mobeffect.splash && this.ambient == mobeffect.ambient && this.b.equals(mobeffect.b);
        }
    }

    public int hashCode() {
        int i = this.b.hashCode();

        i = 31 * i + this.duration;
        i = 31 * i + this.amplification;
        i = 31 * i + (this.splash ? 1 : 0);
        i = 31 * i + (this.ambient ? 1 : 0);
        return i;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Id", (byte) MobEffectList.getId(this.getMobEffect()));
        nbttagcompound.setByte("Amplifier", (byte) this.getAmplifier());
        nbttagcompound.setInt("Duration", this.getDuration());
        nbttagcompound.setBoolean("Ambient", this.isAmbient());
        nbttagcompound.setBoolean("ShowParticles", this.isShowParticles());
        return nbttagcompound;
    }

    public static MobEffect b(NBTTagCompound nbttagcompound) {
        byte b0 = nbttagcompound.getByte("Id");
        MobEffectList mobeffectlist = MobEffectList.fromId(b0);

        if (mobeffectlist == null) {
            return null;
        } else {
            byte b1 = nbttagcompound.getByte("Amplifier");
            int i = nbttagcompound.getInt("Duration");
            boolean flag = nbttagcompound.getBoolean("Ambient");
            boolean flag1 = true;

            if (nbttagcompound.hasKeyOfType("ShowParticles", 1)) {
                flag1 = nbttagcompound.getBoolean("ShowParticles");
            }

            return new MobEffect(mobeffectlist, i, b1 < 0 ? 0 : b1, flag, flag1);
        }
    }

    public int b(MobEffect mobeffect) {
        boolean flag = true;

        return (this.getDuration() <= 32147 || mobeffect.getDuration() <= 32147) && (!this.isAmbient() || !mobeffect.isAmbient()) ? ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(mobeffect.isAmbient())).compare(this.getDuration(), mobeffect.getDuration()).compare(this.getMobEffect().getColor(), mobeffect.getMobEffect().getColor()).result() : ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(mobeffect.isAmbient())).compare(this.getMobEffect().getColor(), mobeffect.getMobEffect().getColor()).result();
    }

    public int compareTo(Object object) {
        return this.b((MobEffect) object);
    }
}
