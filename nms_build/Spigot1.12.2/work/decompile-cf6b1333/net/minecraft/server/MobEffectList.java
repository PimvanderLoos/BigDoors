package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class MobEffectList {

    public static final RegistryMaterials<MinecraftKey, MobEffectList> REGISTRY = new RegistryMaterials();
    private final Map<IAttribute, AttributeModifier> a = Maps.newHashMap();
    private final boolean c;
    private final int d;
    private String e = "";
    private int f = -1;
    public double durationModifier;
    private boolean h;

    @Nullable
    public static MobEffectList fromId(int i) {
        return (MobEffectList) MobEffectList.REGISTRY.getId(i);
    }

    public static int getId(MobEffectList mobeffectlist) {
        return MobEffectList.REGISTRY.a((Object) mobeffectlist);
    }

    @Nullable
    public static MobEffectList getByName(String s) {
        return (MobEffectList) MobEffectList.REGISTRY.get(new MinecraftKey(s));
    }

    protected MobEffectList(boolean flag, int i) {
        this.c = flag;
        if (flag) {
            this.durationModifier = 0.5D;
        } else {
            this.durationModifier = 1.0D;
        }

        this.d = i;
    }

    protected MobEffectList b(int i, int j) {
        this.f = i + j * 8;
        return this;
    }

    public void tick(EntityLiving entityliving, int i) {
        if (this == MobEffects.REGENERATION) {
            if (entityliving.getHealth() < entityliving.getMaxHealth()) {
                entityliving.heal(1.0F);
            }
        } else if (this == MobEffects.POISON) {
            if (entityliving.getHealth() > 1.0F) {
                entityliving.damageEntity(DamageSource.MAGIC, 1.0F);
            }
        } else if (this == MobEffects.WITHER) {
            entityliving.damageEntity(DamageSource.WITHER, 1.0F);
        } else if (this == MobEffects.HUNGER && entityliving instanceof EntityHuman) {
            ((EntityHuman) entityliving).applyExhaustion(0.005F * (float) (i + 1));
        } else if (this == MobEffects.SATURATION && entityliving instanceof EntityHuman) {
            if (!entityliving.world.isClientSide) {
                ((EntityHuman) entityliving).getFoodData().eat(i + 1, 1.0F);
            }
        } else if ((this != MobEffects.HEAL || entityliving.cc()) && (this != MobEffects.HARM || !entityliving.cc())) {
            if (this == MobEffects.HARM && !entityliving.cc() || this == MobEffects.HEAL && entityliving.cc()) {
                entityliving.damageEntity(DamageSource.MAGIC, (float) (6 << i));
            }
        } else {
            entityliving.heal((float) Math.max(4 << i, 0));
        }

    }

    public void applyInstantEffect(@Nullable Entity entity, @Nullable Entity entity1, EntityLiving entityliving, int i, double d0) {
        int j;

        if ((this != MobEffects.HEAL || entityliving.cc()) && (this != MobEffects.HARM || !entityliving.cc())) {
            if (this == MobEffects.HARM && !entityliving.cc() || this == MobEffects.HEAL && entityliving.cc()) {
                j = (int) (d0 * (double) (6 << i) + 0.5D);
                if (entity == null) {
                    entityliving.damageEntity(DamageSource.MAGIC, (float) j);
                } else {
                    entityliving.damageEntity(DamageSource.b(entity, entity1), (float) j);
                }
            }
        } else {
            j = (int) (d0 * (double) (4 << i) + 0.5D);
            entityliving.heal((float) j);
        }

    }

    public boolean a(int i, int j) {
        int k;

        if (this == MobEffects.REGENERATION) {
            k = 50 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.POISON) {
            k = 25 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.WITHER) {
            k = 40 >> j;
            return k > 0 ? i % k == 0 : true;
        } else {
            return this == MobEffects.HUNGER;
        }
    }

    public boolean isInstant() {
        return false;
    }

    public MobEffectList c(String s) {
        this.e = s;
        return this;
    }

    public String a() {
        return this.e;
    }

    protected MobEffectList a(double d0) {
        this.durationModifier = d0;
        return this;
    }

    public int getColor() {
        return this.d;
    }

    public MobEffectList a(IAttribute iattribute, String s, double d0, int i) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(s), this.a(), d0, i);

        this.a.put(iattribute, attributemodifier);
        return this;
    }

    public void a(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            AttributeInstance attributeinstance = attributemapbase.a((IAttribute) entry.getKey());

            if (attributeinstance != null) {
                attributeinstance.c((AttributeModifier) entry.getValue());
            }
        }

    }

    public void b(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            AttributeInstance attributeinstance = attributemapbase.a((IAttribute) entry.getKey());

            if (attributeinstance != null) {
                AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();

                attributeinstance.c(attributemodifier);
                attributeinstance.b(new AttributeModifier(attributemodifier.a(), this.a() + " " + i, this.a(i, attributemodifier), attributemodifier.c()));
            }
        }

    }

    public double a(int i, AttributeModifier attributemodifier) {
        return attributemodifier.d() * (double) (i + 1);
    }

    public MobEffectList j() {
        this.h = true;
        return this;
    }

    public static void k() {
        MobEffectList.REGISTRY.a(1, new MinecraftKey("speed"), (new MobEffectList(false, 8171462)).c("effect.moveSpeed").b(0, 0).a(GenericAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2).j());
        MobEffectList.REGISTRY.a(2, new MinecraftKey("slowness"), (new MobEffectList(true, 5926017)).c("effect.moveSlowdown").b(1, 0).a(GenericAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, 2));
        MobEffectList.REGISTRY.a(3, new MinecraftKey("haste"), (new MobEffectList(false, 14270531)).c("effect.digSpeed").b(2, 0).a(1.5D).j().a(GenericAttributes.g, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.10000000149011612D, 2));
        MobEffectList.REGISTRY.a(4, new MinecraftKey("mining_fatigue"), (new MobEffectList(true, 4866583)).c("effect.digSlowDown").b(3, 0).a(GenericAttributes.g, "55FCED67-E92A-486E-9800-B47F202C4386", -0.10000000149011612D, 2));
        MobEffectList.REGISTRY.a(5, new MinecraftKey("strength"), (new MobEffectAttackDamage(false, 9643043, 3.0D)).c("effect.damageBoost").b(4, 0).a(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, 0).j());
        MobEffectList.REGISTRY.a(6, new MinecraftKey("instant_health"), (new InstantMobEffect(false, 16262179)).c("effect.heal").j());
        MobEffectList.REGISTRY.a(7, new MinecraftKey("instant_damage"), (new InstantMobEffect(true, 4393481)).c("effect.harm").j());
        MobEffectList.REGISTRY.a(8, new MinecraftKey("jump_boost"), (new MobEffectList(false, 2293580)).c("effect.jump").b(2, 1).j());
        MobEffectList.REGISTRY.a(9, new MinecraftKey("nausea"), (new MobEffectList(true, 5578058)).c("effect.confusion").b(3, 1).a(0.25D));
        MobEffectList.REGISTRY.a(10, new MinecraftKey("regeneration"), (new MobEffectList(false, 13458603)).c("effect.regeneration").b(7, 0).a(0.25D).j());
        MobEffectList.REGISTRY.a(11, new MinecraftKey("resistance"), (new MobEffectList(false, 10044730)).c("effect.resistance").b(6, 1).j());
        MobEffectList.REGISTRY.a(12, new MinecraftKey("fire_resistance"), (new MobEffectList(false, 14981690)).c("effect.fireResistance").b(7, 1).j());
        MobEffectList.REGISTRY.a(13, new MinecraftKey("water_breathing"), (new MobEffectList(false, 3035801)).c("effect.waterBreathing").b(0, 2).j());
        MobEffectList.REGISTRY.a(14, new MinecraftKey("invisibility"), (new MobEffectList(false, 8356754)).c("effect.invisibility").b(0, 1).j());
        MobEffectList.REGISTRY.a(15, new MinecraftKey("blindness"), (new MobEffectList(true, 2039587)).c("effect.blindness").b(5, 1).a(0.25D));
        MobEffectList.REGISTRY.a(16, new MinecraftKey("night_vision"), (new MobEffectList(false, 2039713)).c("effect.nightVision").b(4, 1).j());
        MobEffectList.REGISTRY.a(17, new MinecraftKey("hunger"), (new MobEffectList(true, 5797459)).c("effect.hunger").b(1, 1));
        MobEffectList.REGISTRY.a(18, new MinecraftKey("weakness"), (new MobEffectAttackDamage(true, 4738376, -4.0D)).c("effect.weakness").b(5, 0).a(GenericAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, 0));
        MobEffectList.REGISTRY.a(19, new MinecraftKey("poison"), (new MobEffectList(true, 5149489)).c("effect.poison").b(6, 0).a(0.25D));
        MobEffectList.REGISTRY.a(20, new MinecraftKey("wither"), (new MobEffectList(true, 3484199)).c("effect.wither").b(1, 2).a(0.25D));
        MobEffectList.REGISTRY.a(21, new MinecraftKey("health_boost"), (new MobEffectHealthBoost(false, 16284963)).c("effect.healthBoost").b(7, 2).a(GenericAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0).j());
        MobEffectList.REGISTRY.a(22, new MinecraftKey("absorption"), (new MobEffectAbsorption(false, 2445989)).c("effect.absorption").b(2, 2).j());
        MobEffectList.REGISTRY.a(23, new MinecraftKey("saturation"), (new InstantMobEffect(false, 16262179)).c("effect.saturation").j());
        MobEffectList.REGISTRY.a(24, new MinecraftKey("glowing"), (new MobEffectList(false, 9740385)).c("effect.glowing").b(4, 2));
        MobEffectList.REGISTRY.a(25, new MinecraftKey("levitation"), (new MobEffectList(true, 13565951)).c("effect.levitation").b(3, 2));
        MobEffectList.REGISTRY.a(26, new MinecraftKey("luck"), (new MobEffectList(false, 3381504)).c("effect.luck").b(5, 2).j().a(GenericAttributes.j, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, 0));
        MobEffectList.REGISTRY.a(27, new MinecraftKey("unluck"), (new MobEffectList(true, 12624973)).c("effect.unluck").b(6, 2).a(GenericAttributes.j, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, 0));
    }
}
