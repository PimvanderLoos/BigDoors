package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;

public class PotionRegistry {

    private static final MinecraftKey b = new MinecraftKey("empty");
    public static final RegistryBlocks<MinecraftKey, PotionRegistry> a = new RegistryBlocks(PotionRegistry.b);
    private static int c;
    private final String d;
    private final ImmutableList<MobEffect> e;

    @Nullable
    public static PotionRegistry a(String s) {
        return (PotionRegistry) PotionRegistry.a.get(new MinecraftKey(s));
    }

    public PotionRegistry(MobEffect... amobeffect) {
        this((String) null, amobeffect);
    }

    public PotionRegistry(@Nullable String s, MobEffect... amobeffect) {
        this.d = s;
        this.e = ImmutableList.copyOf(amobeffect);
    }

    public String b(String s) {
        return this.d == null ? s + ((MinecraftKey) PotionRegistry.a.b(this)).getKey() : s + this.d;
    }

    public List<MobEffect> a() {
        return this.e;
    }

    public static void b() {
        a("empty", new PotionRegistry(new MobEffect[0]));
        a("water", new PotionRegistry(new MobEffect[0]));
        a("mundane", new PotionRegistry(new MobEffect[0]));
        a("thick", new PotionRegistry(new MobEffect[0]));
        a("awkward", new PotionRegistry(new MobEffect[0]));
        a("night_vision", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.NIGHT_VISION, 3600)}));
        a("long_night_vision", new PotionRegistry("night_vision", new MobEffect[] { new MobEffect(MobEffects.NIGHT_VISION, 9600)}));
        a("invisibility", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.INVISIBILITY, 3600)}));
        a("long_invisibility", new PotionRegistry("invisibility", new MobEffect[] { new MobEffect(MobEffects.INVISIBILITY, 9600)}));
        a("leaping", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.JUMP, 3600)}));
        a("long_leaping", new PotionRegistry("leaping", new MobEffect[] { new MobEffect(MobEffects.JUMP, 9600)}));
        a("strong_leaping", new PotionRegistry("leaping", new MobEffect[] { new MobEffect(MobEffects.JUMP, 1800, 1)}));
        a("fire_resistance", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.FIRE_RESISTANCE, 3600)}));
        a("long_fire_resistance", new PotionRegistry("fire_resistance", new MobEffect[] { new MobEffect(MobEffects.FIRE_RESISTANCE, 9600)}));
        a("swiftness", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.FASTER_MOVEMENT, 3600)}));
        a("long_swiftness", new PotionRegistry("swiftness", new MobEffect[] { new MobEffect(MobEffects.FASTER_MOVEMENT, 9600)}));
        a("strong_swiftness", new PotionRegistry("swiftness", new MobEffect[] { new MobEffect(MobEffects.FASTER_MOVEMENT, 1800, 1)}));
        a("slowness", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.SLOWER_MOVEMENT, 1800)}));
        a("long_slowness", new PotionRegistry("slowness", new MobEffect[] { new MobEffect(MobEffects.SLOWER_MOVEMENT, 4800)}));
        a("water_breathing", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.WATER_BREATHING, 3600)}));
        a("long_water_breathing", new PotionRegistry("water_breathing", new MobEffect[] { new MobEffect(MobEffects.WATER_BREATHING, 9600)}));
        a("healing", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.HEAL, 1)}));
        a("strong_healing", new PotionRegistry("healing", new MobEffect[] { new MobEffect(MobEffects.HEAL, 1, 1)}));
        a("harming", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.HARM, 1)}));
        a("strong_harming", new PotionRegistry("harming", new MobEffect[] { new MobEffect(MobEffects.HARM, 1, 1)}));
        a("poison", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.POISON, 900)}));
        a("long_poison", new PotionRegistry("poison", new MobEffect[] { new MobEffect(MobEffects.POISON, 1800)}));
        a("strong_poison", new PotionRegistry("poison", new MobEffect[] { new MobEffect(MobEffects.POISON, 432, 1)}));
        a("regeneration", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.REGENERATION, 900)}));
        a("long_regeneration", new PotionRegistry("regeneration", new MobEffect[] { new MobEffect(MobEffects.REGENERATION, 1800)}));
        a("strong_regeneration", new PotionRegistry("regeneration", new MobEffect[] { new MobEffect(MobEffects.REGENERATION, 450, 1)}));
        a("strength", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.INCREASE_DAMAGE, 3600)}));
        a("long_strength", new PotionRegistry("strength", new MobEffect[] { new MobEffect(MobEffects.INCREASE_DAMAGE, 9600)}));
        a("strong_strength", new PotionRegistry("strength", new MobEffect[] { new MobEffect(MobEffects.INCREASE_DAMAGE, 1800, 1)}));
        a("weakness", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.WEAKNESS, 1800)}));
        a("long_weakness", new PotionRegistry("weakness", new MobEffect[] { new MobEffect(MobEffects.WEAKNESS, 4800)}));
        a("luck", new PotionRegistry("luck", new MobEffect[] { new MobEffect(MobEffects.z, 6000)}));
        PotionRegistry.a.a();
    }

    protected static void a(String s, PotionRegistry potionregistry) {
        PotionRegistry.a.a(PotionRegistry.c++, new MinecraftKey(s), potionregistry);
    }

    public boolean c() {
        if (!this.e.isEmpty()) {
            UnmodifiableIterator unmodifiableiterator = this.e.iterator();

            while (unmodifiableiterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) unmodifiableiterator.next();

                if (mobeffect.getMobEffect().isInstant()) {
                    return true;
                }
            }
        }

        return false;
    }
}
