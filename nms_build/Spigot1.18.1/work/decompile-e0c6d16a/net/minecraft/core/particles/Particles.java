package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public class Particles {

    public static final ParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
    public static final ParticleType ANGRY_VILLAGER = register("angry_villager", false);
    public static final Particle<ParticleParamBlock> BLOCK = register("block", ParticleParamBlock.DESERIALIZER, ParticleParamBlock::codec);
    public static final Particle<ParticleParamBlock> BLOCK_MARKER = register("block_marker", ParticleParamBlock.DESERIALIZER, ParticleParamBlock::codec);
    public static final ParticleType BUBBLE = register("bubble", false);
    public static final ParticleType CLOUD = register("cloud", false);
    public static final ParticleType CRIT = register("crit", false);
    public static final ParticleType DAMAGE_INDICATOR = register("damage_indicator", true);
    public static final ParticleType DRAGON_BREATH = register("dragon_breath", false);
    public static final ParticleType DRIPPING_LAVA = register("dripping_lava", false);
    public static final ParticleType FALLING_LAVA = register("falling_lava", false);
    public static final ParticleType LANDING_LAVA = register("landing_lava", false);
    public static final ParticleType DRIPPING_WATER = register("dripping_water", false);
    public static final ParticleType FALLING_WATER = register("falling_water", false);
    public static final Particle<ParticleParamRedstone> DUST = register("dust", ParticleParamRedstone.DESERIALIZER, (particle) -> {
        return ParticleParamRedstone.CODEC;
    });
    public static final Particle<DustColorTransitionOptions> DUST_COLOR_TRANSITION = register("dust_color_transition", DustColorTransitionOptions.DESERIALIZER, (particle) -> {
        return DustColorTransitionOptions.CODEC;
    });
    public static final ParticleType EFFECT = register("effect", false);
    public static final ParticleType ELDER_GUARDIAN = register("elder_guardian", true);
    public static final ParticleType ENCHANTED_HIT = register("enchanted_hit", false);
    public static final ParticleType ENCHANT = register("enchant", false);
    public static final ParticleType END_ROD = register("end_rod", false);
    public static final ParticleType ENTITY_EFFECT = register("entity_effect", false);
    public static final ParticleType EXPLOSION_EMITTER = register("explosion_emitter", true);
    public static final ParticleType EXPLOSION = register("explosion", true);
    public static final Particle<ParticleParamBlock> FALLING_DUST = register("falling_dust", ParticleParamBlock.DESERIALIZER, ParticleParamBlock::codec);
    public static final ParticleType FIREWORK = register("firework", false);
    public static final ParticleType FISHING = register("fishing", false);
    public static final ParticleType FLAME = register("flame", false);
    public static final ParticleType SOUL_FIRE_FLAME = register("soul_fire_flame", false);
    public static final ParticleType SOUL = register("soul", false);
    public static final ParticleType FLASH = register("flash", false);
    public static final ParticleType HAPPY_VILLAGER = register("happy_villager", false);
    public static final ParticleType COMPOSTER = register("composter", false);
    public static final ParticleType HEART = register("heart", false);
    public static final ParticleType INSTANT_EFFECT = register("instant_effect", false);
    public static final Particle<ParticleParamItem> ITEM = register("item", ParticleParamItem.DESERIALIZER, ParticleParamItem::codec);
    public static final Particle<VibrationParticleOption> VIBRATION = register("vibration", VibrationParticleOption.DESERIALIZER, (particle) -> {
        return VibrationParticleOption.CODEC;
    });
    public static final ParticleType ITEM_SLIME = register("item_slime", false);
    public static final ParticleType ITEM_SNOWBALL = register("item_snowball", false);
    public static final ParticleType LARGE_SMOKE = register("large_smoke", false);
    public static final ParticleType LAVA = register("lava", false);
    public static final ParticleType MYCELIUM = register("mycelium", false);
    public static final ParticleType NOTE = register("note", false);
    public static final ParticleType POOF = register("poof", true);
    public static final ParticleType PORTAL = register("portal", false);
    public static final ParticleType RAIN = register("rain", false);
    public static final ParticleType SMOKE = register("smoke", false);
    public static final ParticleType SNEEZE = register("sneeze", false);
    public static final ParticleType SPIT = register("spit", true);
    public static final ParticleType SQUID_INK = register("squid_ink", true);
    public static final ParticleType SWEEP_ATTACK = register("sweep_attack", true);
    public static final ParticleType TOTEM_OF_UNDYING = register("totem_of_undying", false);
    public static final ParticleType UNDERWATER = register("underwater", false);
    public static final ParticleType SPLASH = register("splash", false);
    public static final ParticleType WITCH = register("witch", false);
    public static final ParticleType BUBBLE_POP = register("bubble_pop", false);
    public static final ParticleType CURRENT_DOWN = register("current_down", false);
    public static final ParticleType BUBBLE_COLUMN_UP = register("bubble_column_up", false);
    public static final ParticleType NAUTILUS = register("nautilus", false);
    public static final ParticleType DOLPHIN = register("dolphin", false);
    public static final ParticleType CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke", true);
    public static final ParticleType CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke", true);
    public static final ParticleType DRIPPING_HONEY = register("dripping_honey", false);
    public static final ParticleType FALLING_HONEY = register("falling_honey", false);
    public static final ParticleType LANDING_HONEY = register("landing_honey", false);
    public static final ParticleType FALLING_NECTAR = register("falling_nectar", false);
    public static final ParticleType FALLING_SPORE_BLOSSOM = register("falling_spore_blossom", false);
    public static final ParticleType ASH = register("ash", false);
    public static final ParticleType CRIMSON_SPORE = register("crimson_spore", false);
    public static final ParticleType WARPED_SPORE = register("warped_spore", false);
    public static final ParticleType SPORE_BLOSSOM_AIR = register("spore_blossom_air", false);
    public static final ParticleType DRIPPING_OBSIDIAN_TEAR = register("dripping_obsidian_tear", false);
    public static final ParticleType FALLING_OBSIDIAN_TEAR = register("falling_obsidian_tear", false);
    public static final ParticleType LANDING_OBSIDIAN_TEAR = register("landing_obsidian_tear", false);
    public static final ParticleType REVERSE_PORTAL = register("reverse_portal", false);
    public static final ParticleType WHITE_ASH = register("white_ash", false);
    public static final ParticleType SMALL_FLAME = register("small_flame", false);
    public static final ParticleType SNOWFLAKE = register("snowflake", false);
    public static final ParticleType DRIPPING_DRIPSTONE_LAVA = register("dripping_dripstone_lava", false);
    public static final ParticleType FALLING_DRIPSTONE_LAVA = register("falling_dripstone_lava", false);
    public static final ParticleType DRIPPING_DRIPSTONE_WATER = register("dripping_dripstone_water", false);
    public static final ParticleType FALLING_DRIPSTONE_WATER = register("falling_dripstone_water", false);
    public static final ParticleType GLOW_SQUID_INK = register("glow_squid_ink", true);
    public static final ParticleType GLOW = register("glow", true);
    public static final ParticleType WAX_ON = register("wax_on", true);
    public static final ParticleType WAX_OFF = register("wax_off", true);
    public static final ParticleType ELECTRIC_SPARK = register("electric_spark", true);
    public static final ParticleType SCRAPE = register("scrape", true);
    public static final Codec<ParticleParam> CODEC = IRegistry.PARTICLE_TYPE.byNameCodec().dispatch("type", ParticleParam::getType, Particle::codec);

    public Particles() {}

    private static ParticleType register(String s, boolean flag) {
        return (ParticleType) IRegistry.register(IRegistry.PARTICLE_TYPE, s, new ParticleType(flag));
    }

    private static <T extends ParticleParam> Particle<T> register(String s, ParticleParam.a<T> particleparam_a, final Function<Particle<T>, Codec<T>> function) {
        return (Particle) IRegistry.register(IRegistry.PARTICLE_TYPE, s, new Particle<T>(false, particleparam_a) {
            @Override
            public Codec<T> codec() {
                return (Codec) function.apply(this);
            }
        });
    }
}
