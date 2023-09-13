package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import org.slf4j.Logger;

public class MobEffect implements Comparable<MobEffect> {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int INFINITE_DURATION = -1;
    private final MobEffectList effect;
    private int duration;
    private int amplifier;
    private boolean ambient;
    private boolean visible;
    private boolean showIcon;
    @Nullable
    private MobEffect hiddenEffect;
    private final Optional<MobEffect.a> factorData;

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
        this(mobeffectlist, i, j, flag, flag1, flag1);
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j, boolean flag, boolean flag1, boolean flag2) {
        this(mobeffectlist, i, j, flag, flag1, flag2, (MobEffect) null, mobeffectlist.createFactorData());
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j, boolean flag, boolean flag1, boolean flag2, @Nullable MobEffect mobeffect, Optional<MobEffect.a> optional) {
        this.effect = mobeffectlist;
        this.duration = i;
        this.amplifier = j;
        this.ambient = flag;
        this.visible = flag1;
        this.showIcon = flag2;
        this.hiddenEffect = mobeffect;
        this.factorData = optional;
    }

    public MobEffect(MobEffect mobeffect) {
        this.effect = mobeffect.effect;
        this.factorData = this.effect.createFactorData();
        this.setDetailsFrom(mobeffect);
    }

    public Optional<MobEffect.a> getFactorData() {
        return this.factorData;
    }

    void setDetailsFrom(MobEffect mobeffect) {
        this.duration = mobeffect.duration;
        this.amplifier = mobeffect.amplifier;
        this.ambient = mobeffect.ambient;
        this.visible = mobeffect.visible;
        this.showIcon = mobeffect.showIcon;
    }

    public boolean update(MobEffect mobeffect) {
        if (this.effect != mobeffect.effect) {
            MobEffect.LOGGER.warn("This method should only be called for matching effects!");
        }

        int i = this.duration;
        boolean flag = false;

        if (mobeffect.amplifier > this.amplifier) {
            if (mobeffect.isShorterDurationThan(this)) {
                MobEffect mobeffect1 = this.hiddenEffect;

                this.hiddenEffect = new MobEffect(this);
                this.hiddenEffect.hiddenEffect = mobeffect1;
            }

            this.amplifier = mobeffect.amplifier;
            this.duration = mobeffect.duration;
            flag = true;
        } else if (this.isShorterDurationThan(mobeffect)) {
            if (mobeffect.amplifier == this.amplifier) {
                this.duration = mobeffect.duration;
                flag = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new MobEffect(mobeffect);
            } else {
                this.hiddenEffect.update(mobeffect);
            }
        }

        if (!mobeffect.ambient && this.ambient || flag) {
            this.ambient = mobeffect.ambient;
            flag = true;
        }

        if (mobeffect.visible != this.visible) {
            this.visible = mobeffect.visible;
            flag = true;
        }

        if (mobeffect.showIcon != this.showIcon) {
            this.showIcon = mobeffect.showIcon;
            flag = true;
        }

        return flag;
    }

    private boolean isShorterDurationThan(MobEffect mobeffect) {
        return !this.isInfiniteDuration() && (this.duration < mobeffect.duration || mobeffect.isInfiniteDuration());
    }

    public boolean isInfiniteDuration() {
        return this.duration == -1;
    }

    public boolean endsWithin(int i) {
        return !this.isInfiniteDuration() && this.duration <= i;
    }

    public int mapDuration(Int2IntFunction int2intfunction) {
        return !this.isInfiniteDuration() && this.duration != 0 ? int2intfunction.applyAsInt(this.duration) : this.duration;
    }

    public MobEffectList getEffect() {
        return this.effect;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean showIcon() {
        return this.showIcon;
    }

    public boolean tick(EntityLiving entityliving, Runnable runnable) {
        if (this.hasRemainingDuration()) {
            int i = this.isInfiniteDuration() ? entityliving.tickCount : this.duration;

            if (this.effect.isDurationEffectTick(i, this.amplifier)) {
                this.applyEffect(entityliving);
            }

            this.tickDownDuration();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.setDetailsFrom(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                runnable.run();
            }
        }

        this.factorData.ifPresent((mobeffect_a) -> {
            mobeffect_a.tick(this);
        });
        return this.hasRemainingDuration();
    }

    private boolean hasRemainingDuration() {
        return this.isInfiniteDuration() || this.duration > 0;
    }

    private int tickDownDuration() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.tickDownDuration();
        }

        return this.duration = this.mapDuration((i) -> {
            return i - 1;
        });
    }

    public void applyEffect(EntityLiving entityliving) {
        if (this.hasRemainingDuration()) {
            this.effect.applyEffectTick(entityliving, this.amplifier);
        }

    }

    public String getDescriptionId() {
        return this.effect.getDescriptionId();
    }

    public String toString() {
        String s;
        String s1;

        if (this.amplifier > 0) {
            s = this.getDescriptionId();
            s1 = s + " x " + (this.amplifier + 1) + ", Duration: " + this.describeDuration();
        } else {
            s = this.getDescriptionId();
            s1 = s + ", Duration: " + this.describeDuration();
        }

        if (!this.visible) {
            s1 = s1 + ", Particles: false";
        }

        if (!this.showIcon) {
            s1 = s1 + ", Show Icon: false";
        }

        return s1;
    }

    private String describeDuration() {
        return this.isInfiniteDuration() ? "infinite" : Integer.toString(this.duration);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MobEffect)) {
            return false;
        } else {
            MobEffect mobeffect = (MobEffect) object;

            return this.duration == mobeffect.duration && this.amplifier == mobeffect.amplifier && this.ambient == mobeffect.ambient && this.effect.equals(mobeffect.effect);
        }
    }

    public int hashCode() {
        int i = this.effect.hashCode();

        i = 31 * i + this.duration;
        i = 31 * i + this.amplifier;
        i = 31 * i + (this.ambient ? 1 : 0);
        return i;
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("Id", MobEffectList.getId(this.getEffect()));
        this.writeDetailsTo(nbttagcompound);
        return nbttagcompound;
    }

    private void writeDetailsTo(NBTTagCompound nbttagcompound) {
        nbttagcompound.putByte("Amplifier", (byte) this.getAmplifier());
        nbttagcompound.putInt("Duration", this.getDuration());
        nbttagcompound.putBoolean("Ambient", this.isAmbient());
        nbttagcompound.putBoolean("ShowParticles", this.isVisible());
        nbttagcompound.putBoolean("ShowIcon", this.showIcon());
        if (this.hiddenEffect != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            this.hiddenEffect.save(nbttagcompound1);
            nbttagcompound.put("HiddenEffect", nbttagcompound1);
        }

        this.factorData.ifPresent((mobeffect_a) -> {
            DataResult dataresult = MobEffect.a.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, mobeffect_a);
            Logger logger = MobEffect.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
                nbttagcompound.put("FactorCalculationData", nbtbase);
            });
        });
    }

    @Nullable
    public static MobEffect load(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("Id");
        MobEffectList mobeffectlist = MobEffectList.byId(i);

        return mobeffectlist == null ? null : loadSpecifiedEffect(mobeffectlist, nbttagcompound);
    }

    private static MobEffect loadSpecifiedEffect(MobEffectList mobeffectlist, NBTTagCompound nbttagcompound) {
        byte b0 = nbttagcompound.getByte("Amplifier");
        int i = nbttagcompound.getInt("Duration");
        boolean flag = nbttagcompound.getBoolean("Ambient");
        boolean flag1 = true;

        if (nbttagcompound.contains("ShowParticles", 1)) {
            flag1 = nbttagcompound.getBoolean("ShowParticles");
        }

        boolean flag2 = flag1;

        if (nbttagcompound.contains("ShowIcon", 1)) {
            flag2 = nbttagcompound.getBoolean("ShowIcon");
        }

        MobEffect mobeffect = null;

        if (nbttagcompound.contains("HiddenEffect", 10)) {
            mobeffect = loadSpecifiedEffect(mobeffectlist, nbttagcompound.getCompound("HiddenEffect"));
        }

        Optional optional;

        if (nbttagcompound.contains("FactorCalculationData", 10)) {
            DataResult dataresult = MobEffect.a.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("FactorCalculationData")));
            Logger logger = MobEffect.LOGGER;

            Objects.requireNonNull(logger);
            optional = dataresult.resultOrPartial(logger::error);
        } else {
            optional = Optional.empty();
        }

        return new MobEffect(mobeffectlist, i, Math.max(b0, 0), flag, flag1, flag2, mobeffect, optional);
    }

    public int compareTo(MobEffect mobeffect) {
        boolean flag = true;

        return (this.getDuration() <= 32147 || mobeffect.getDuration() <= 32147) && (!this.isAmbient() || !mobeffect.isAmbient()) ? ComparisonChain.start().compareFalseFirst(this.isAmbient(), mobeffect.isAmbient()).compareFalseFirst(this.isInfiniteDuration(), mobeffect.isInfiniteDuration()).compare(this.getDuration(), mobeffect.getDuration()).compare(this.getEffect().getColor(), mobeffect.getEffect().getColor()).result() : ComparisonChain.start().compare(this.isAmbient(), mobeffect.isAmbient()).compare(this.getEffect().getColor(), mobeffect.getEffect().getColor()).result();
    }

    public static class a {

        public static final Codec<MobEffect.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("padding_duration").forGetter((mobeffect_a) -> {
                return mobeffect_a.paddingDuration;
            }), Codec.FLOAT.fieldOf("factor_start").orElse(0.0F).forGetter((mobeffect_a) -> {
                return mobeffect_a.factorStart;
            }), Codec.FLOAT.fieldOf("factor_target").orElse(1.0F).forGetter((mobeffect_a) -> {
                return mobeffect_a.factorTarget;
            }), Codec.FLOAT.fieldOf("factor_current").orElse(0.0F).forGetter((mobeffect_a) -> {
                return mobeffect_a.factorCurrent;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_active").orElse(0).forGetter((mobeffect_a) -> {
                return mobeffect_a.ticksActive;
            }), Codec.FLOAT.fieldOf("factor_previous_frame").orElse(0.0F).forGetter((mobeffect_a) -> {
                return mobeffect_a.factorPreviousFrame;
            }), Codec.BOOL.fieldOf("had_effect_last_tick").orElse(false).forGetter((mobeffect_a) -> {
                return mobeffect_a.hadEffectLastTick;
            })).apply(instance, MobEffect.a::new);
        });
        private final int paddingDuration;
        private float factorStart;
        private float factorTarget;
        private float factorCurrent;
        private int ticksActive;
        private float factorPreviousFrame;
        private boolean hadEffectLastTick;

        public a(int i, float f, float f1, float f2, int j, float f3, boolean flag) {
            this.paddingDuration = i;
            this.factorStart = f;
            this.factorTarget = f1;
            this.factorCurrent = f2;
            this.ticksActive = j;
            this.factorPreviousFrame = f3;
            this.hadEffectLastTick = flag;
        }

        public a(int i) {
            this(i, 0.0F, 1.0F, 0.0F, 0, 0.0F, false);
        }

        public void tick(MobEffect mobeffect) {
            this.factorPreviousFrame = this.factorCurrent;
            boolean flag = !mobeffect.endsWithin(this.paddingDuration);

            ++this.ticksActive;
            if (this.hadEffectLastTick != flag) {
                this.hadEffectLastTick = flag;
                this.ticksActive = 0;
                this.factorStart = this.factorCurrent;
                this.factorTarget = flag ? 1.0F : 0.0F;
            }

            float f = MathHelper.clamp((float) this.ticksActive / (float) this.paddingDuration, 0.0F, 1.0F);

            this.factorCurrent = MathHelper.lerp(f, this.factorStart, this.factorTarget);
        }

        public float getFactor(EntityLiving entityliving, float f) {
            if (entityliving.isRemoved()) {
                this.factorPreviousFrame = this.factorCurrent;
            }

            return MathHelper.lerp(f, this.factorPreviousFrame, this.factorCurrent);
        }
    }
}
