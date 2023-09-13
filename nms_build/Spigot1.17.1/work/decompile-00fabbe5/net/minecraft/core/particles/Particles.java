package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public class Particles {

    public static final ParticleType AMBIENT_ENTITY_EFFECT = a("ambient_entity_effect", false);
    public static final ParticleType ANGRY_VILLAGER = a("angry_villager", false);
    public static final ParticleType BARRIER = a("barrier", false);
    public static final ParticleType LIGHT = a("light", false);
    public static final Particle<ParticleParamBlock> BLOCK = a("block", ParticleParamBlock.DESERIALIZER, ParticleParamBlock::a);
    public static final ParticleType BUBBLE = a("bubble", false);
    public static final ParticleType CLOUD = a("cloud", false);
    public static final ParticleType CRIT = a("crit", false);
    public static final ParticleType DAMAGE_INDICATOR = a("damage_indicator", true);
    public static final ParticleType DRAGON_BREATH = a("dragon_breath", false);
    public static final ParticleType DRIPPING_LAVA = a("dripping_lava", false);
    public static final ParticleType FALLING_LAVA = a("falling_lava", false);
    public static final ParticleType LANDING_LAVA = a("landing_lava", false);
    public static final ParticleType DRIPPING_WATER = a("dripping_water", false);
    public static final ParticleType FALLING_WATER = a("falling_water", false);
    public static final Particle<ParticleParamRedstone> DUST = a("dust", ParticleParamRedstone.DESERIALIZER, (particle) -> {
        return ParticleParamRedstone.CODEC;
    });
    public static final Particle<DustColorTransitionOptions> DUST_COLOR_TRANSITION = a("dust_color_transition", DustColorTransitionOptions.DESERIALIZER, (particle) -> {
        return DustColorTransitionOptions.CODEC;
    });
    public static final ParticleType EFFECT = a("effect", false);
    public static final ParticleType ELDER_GUARDIAN = a("elder_guardian", true);
    public static final ParticleType ENCHANTED_HIT = a("enchanted_hit", false);
    public static final ParticleType ENCHANT = a("enchant", false);
    public static final ParticleType END_ROD = a("end_rod", false);
    public static final ParticleType ENTITY_EFFECT = a("entity_effect", false);
    public static final ParticleType EXPLOSION_EMITTER = a("explosion_emitter", true);
    public static final ParticleType EXPLOSION = a("explosion", true);
    public static final Particle<ParticleParamBlock> FALLING_DUST = a("falling_dust", ParticleParamBlock.DESERIALIZER, ParticleParamBlock::a);
    public static final ParticleType FIREWORK = a("firework", false);
    public static final ParticleType FISHING = a("fishing", false);
    public static final ParticleType FLAME = a("flame", false);
    public static final ParticleType SOUL_FIRE_FLAME = a("soul_fire_flame", false);
    public static final ParticleType SOUL = a("soul", false);
    public static final ParticleType FLASH = a("flash", false);
    public static final ParticleType HAPPY_VILLAGER = a("happy_villager", false);
    public static final ParticleType COMPOSTER = a("composter", false);
    public static final ParticleType HEART = a("heart", false);
    public static final ParticleType INSTANT_EFFECT = a("instant_effect", false);
    public static final Particle<ParticleParamItem> ITEM = a("item", ParticleParamItem.DESERIALIZER, ParticleParamItem::a);
    public static final Particle<VibrationParticleOption> VIBRATION = a("vibration", VibrationParticleOption.DESERIALIZER, (particle) -> {
        return VibrationParticleOption.CODEC;
    });
    public static final ParticleType ITEM_SLIME = a("item_slime", false);
    public static final ParticleType ITEM_SNOWBALL = a("item_snowball", false);
    public static final ParticleType LARGE_SMOKE = a("large_smoke", false);
    public static final ParticleType LAVA = a("lava", false);
    public static final ParticleType MYCELIUM = a("mycelium", false);
    public static final ParticleType NOTE = a("note", false);
    public static final ParticleType POOF = a("poof", true);
    public static final ParticleType PORTAL = a("portal", false);
    public static final ParticleType RAIN = a("rain", false);
    public static final ParticleType SMOKE = a("smoke", false);
    public static final ParticleType SNEEZE = a("sneeze", false);
    public static final ParticleType SPIT = a("spit", true);
    public static final ParticleType SQUID_INK = a("squid_ink", true);
    public static final ParticleType SWEEP_ATTACK = a("sweep_attack", true);
    public static final ParticleType TOTEM_OF_UNDYING = a("totem_of_undying", false);
    public static final ParticleType UNDERWATER = a("underwater", false);
    public static final ParticleType SPLASH = a("splash", false);
    public static final ParticleType WITCH = a("witch", false);
    public static final ParticleType BUBBLE_POP = a("bubble_pop", false);
    public static final ParticleType CURRENT_DOWN = a("current_down", false);
    public static final ParticleType BUBBLE_COLUMN_UP = a("bubble_column_up", false);
    public static final ParticleType NAUTILUS = a("nautilus", false);
    public static final ParticleType DOLPHIN = a("dolphin", false);
    public static final ParticleType CAMPFIRE_COSY_SMOKE = a("campfire_cosy_smoke", true);
    public static final ParticleType CAMPFIRE_SIGNAL_SMOKE = a("campfire_signal_smoke", true);
    public static final ParticleType DRIPPING_HONEY = a("dripping_honey", false);
    public static final ParticleType FALLING_HONEY = a("falling_honey", false);
    public static final ParticleType LANDING_HONEY = a("landing_honey", false);
    public static final ParticleType FALLING_NECTAR = a("falling_nectar", false);
    public static final ParticleType FALLING_SPORE_BLOSSOM = a("falling_spore_blossom", false);
    public static final ParticleType ASH = a("ash", false);
    public static final ParticleType CRIMSON_SPORE = a("crimson_spore", false);
    public static final ParticleType WARPED_SPORE = a("warped_spore", false);
    public static final ParticleType SPORE_BLOSSOM_AIR = a("spore_blossom_air", false);
    public static final ParticleType DRIPPING_OBSIDIAN_TEAR = a("dripping_obsidian_tear", false);
    public static final ParticleType FALLING_OBSIDIAN_TEAR = a("falling_obsidian_tear", false);
    public static final ParticleType LANDING_OBSIDIAN_TEAR = a("landing_obsidian_tear", false);
    public static final ParticleType REVERSE_PORTAL = a("reverse_portal", false);
    public static final ParticleType WHITE_ASH = a("white_ash", false);
    public static final ParticleType SMALL_FLAME = a("small_flame", false);
    public static final ParticleType SNOWFLAKE = a("snowflake", false);
    public static final ParticleType DRIPPING_DRIPSTONE_LAVA = a("dripping_dripstone_lava", false);
    public static final ParticleType FALLING_DRIPSTONE_LAVA = a("falling_dripstone_lava", false);
    public static final ParticleType DRIPPING_DRIPSTONE_WATER = a("dripping_dripstone_water", false);
    public static final ParticleType FALLING_DRIPSTONE_WATER = a("falling_dripstone_water", false);
    public static final ParticleType GLOW_SQUID_INK = a("glow_squid_ink", true);
    public static final ParticleType GLOW = a("glow", true);
    public static final ParticleType WAX_ON = a("wax_on", true);
    public static final ParticleType WAX_OFF = a("wax_off", true);
    public static final ParticleType ELECTRIC_SPARK = a("electric_spark", true);
    public static final ParticleType SCRAPE = a("scrape", true);
    public static final Codec<ParticleParam> CODEC = IRegistry.PARTICLE_TYPE.dispatch("type", ParticleParam::getParticle, Particle::e);

    public Particles() {}

    private static ParticleType a(String s, boolean flag) {
        return (ParticleType) IRegistry.a(IRegistry.PARTICLE_TYPE, s, (Object) (new ParticleType(flag)));
    }

    private static <T extends ParticleParam> Particle<T> a(String s, ParticleParam.a<T> particleparam_a, final Function<Particle<T>, Codec<T>> function) {
        return (Particle) IRegistry.a(IRegistry.PARTICLE_TYPE, s, (Object) (new Particle<T>(false, particleparam_a) {
            @Override
            public Codec<T> e() {
                return (Codec) function.apply(this);
            }
        }));
    }
}
