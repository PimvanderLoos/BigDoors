package net.minecraft.world.effect;

import net.minecraft.core.IRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;

public class MobEffects {

    public static final MobEffectList MOVEMENT_SPEED = register(1, "speed", (new MobEffectList(MobEffectInfo.BENEFICIAL, 8171462)).addAttributeModifier(GenericAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final MobEffectList MOVEMENT_SLOWDOWN = register(2, "slowness", (new MobEffectList(MobEffectInfo.HARMFUL, 5926017)).addAttributeModifier(GenericAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final MobEffectList DIG_SPEED = register(3, "haste", (new MobEffectList(MobEffectInfo.BENEFICIAL, 14270531)).addAttributeModifier(GenericAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final MobEffectList DIG_SLOWDOWN = register(4, "mining_fatigue", (new MobEffectList(MobEffectInfo.HARMFUL, 4866583)).addAttributeModifier(GenericAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final MobEffectList DAMAGE_BOOST = register(5, "strength", (new MobEffectAttackDamage(MobEffectInfo.BENEFICIAL, 9643043, 3.0D)).addAttributeModifier(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, AttributeModifier.Operation.ADDITION));
    public static final MobEffectList HEAL = register(6, "instant_health", new InstantMobEffect(MobEffectInfo.BENEFICIAL, 16262179));
    public static final MobEffectList HARM = register(7, "instant_damage", new InstantMobEffect(MobEffectInfo.HARMFUL, 4393481));
    public static final MobEffectList JUMP = register(8, "jump_boost", new MobEffectList(MobEffectInfo.BENEFICIAL, 2293580));
    public static final MobEffectList CONFUSION = register(9, "nausea", new MobEffectList(MobEffectInfo.HARMFUL, 5578058));
    public static final MobEffectList REGENERATION = register(10, "regeneration", new MobEffectList(MobEffectInfo.BENEFICIAL, 13458603));
    public static final MobEffectList DAMAGE_RESISTANCE = register(11, "resistance", new MobEffectList(MobEffectInfo.BENEFICIAL, 10044730));
    public static final MobEffectList FIRE_RESISTANCE = register(12, "fire_resistance", new MobEffectList(MobEffectInfo.BENEFICIAL, 14981690));
    public static final MobEffectList WATER_BREATHING = register(13, "water_breathing", new MobEffectList(MobEffectInfo.BENEFICIAL, 3035801));
    public static final MobEffectList INVISIBILITY = register(14, "invisibility", new MobEffectList(MobEffectInfo.BENEFICIAL, 8356754));
    public static final MobEffectList BLINDNESS = register(15, "blindness", new MobEffectList(MobEffectInfo.HARMFUL, 2039587));
    public static final MobEffectList NIGHT_VISION = register(16, "night_vision", new MobEffectList(MobEffectInfo.BENEFICIAL, 2039713));
    public static final MobEffectList HUNGER = register(17, "hunger", new MobEffectList(MobEffectInfo.HARMFUL, 5797459));
    public static final MobEffectList WEAKNESS = register(18, "weakness", (new MobEffectAttackDamage(MobEffectInfo.HARMFUL, 4738376, -4.0D)).addAttributeModifier(GenericAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.ADDITION));
    public static final MobEffectList POISON = register(19, "poison", new MobEffectList(MobEffectInfo.HARMFUL, 5149489));
    public static final MobEffectList WITHER = register(20, "wither", new MobEffectList(MobEffectInfo.HARMFUL, 3484199));
    public static final MobEffectList HEALTH_BOOST = register(21, "health_boost", (new MobEffectHealthBoost(MobEffectInfo.BENEFICIAL, 16284963)).addAttributeModifier(GenericAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, AttributeModifier.Operation.ADDITION));
    public static final MobEffectList ABSORPTION = register(22, "absorption", new MobEffectAbsorption(MobEffectInfo.BENEFICIAL, 2445989));
    public static final MobEffectList SATURATION = register(23, "saturation", new InstantMobEffect(MobEffectInfo.BENEFICIAL, 16262179));
    public static final MobEffectList GLOWING = register(24, "glowing", new MobEffectList(MobEffectInfo.NEUTRAL, 9740385));
    public static final MobEffectList LEVITATION = register(25, "levitation", new MobEffectList(MobEffectInfo.HARMFUL, 13565951));
    public static final MobEffectList LUCK = register(26, "luck", (new MobEffectList(MobEffectInfo.BENEFICIAL, 3381504)).addAttributeModifier(GenericAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, AttributeModifier.Operation.ADDITION));
    public static final MobEffectList UNLUCK = register(27, "unluck", (new MobEffectList(MobEffectInfo.HARMFUL, 12624973)).addAttributeModifier(GenericAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, AttributeModifier.Operation.ADDITION));
    public static final MobEffectList SLOW_FALLING = register(28, "slow_falling", new MobEffectList(MobEffectInfo.BENEFICIAL, 16773073));
    public static final MobEffectList CONDUIT_POWER = register(29, "conduit_power", new MobEffectList(MobEffectInfo.BENEFICIAL, 1950417));
    public static final MobEffectList DOLPHINS_GRACE = register(30, "dolphins_grace", new MobEffectList(MobEffectInfo.BENEFICIAL, 8954814));
    public static final MobEffectList BAD_OMEN = register(31, "bad_omen", new MobEffectList(MobEffectInfo.NEUTRAL, 745784) {
        @Override
        public boolean isDurationEffectTick(int i, int j) {
            return true;
        }

        @Override
        public void applyEffectTick(EntityLiving entityliving, int i) {
            if (entityliving instanceof EntityPlayer && !entityliving.isSpectator()) {
                EntityPlayer entityplayer = (EntityPlayer) entityliving;
                WorldServer worldserver = entityplayer.getLevel();

                if (worldserver.getDifficulty() == EnumDifficulty.PEACEFUL) {
                    return;
                }

                if (worldserver.isVillage(entityliving.blockPosition())) {
                    worldserver.getRaids().createOrExtendRaid(entityplayer);
                }
            }

        }
    });
    public static final MobEffectList HERO_OF_THE_VILLAGE = register(32, "hero_of_the_village", new MobEffectList(MobEffectInfo.BENEFICIAL, 4521796));

    public MobEffects() {}

    private static MobEffectList register(int i, String s, MobEffectList mobeffectlist) {
        return (MobEffectList) IRegistry.registerMapping(IRegistry.MOB_EFFECT, i, s, mobeffectlist);
    }
}
