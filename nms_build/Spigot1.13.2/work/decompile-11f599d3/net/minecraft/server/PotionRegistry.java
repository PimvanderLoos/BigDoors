package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;

public class PotionRegistry {

    private final String a;
    private final ImmutableList<MobEffect> b;

    public static PotionRegistry a(String s) {
        return (PotionRegistry) IRegistry.POTION.getOrDefault(MinecraftKey.a(s));
    }

    public PotionRegistry(MobEffect... amobeffect) {
        this((String) null, amobeffect);
    }

    public PotionRegistry(@Nullable String s, MobEffect... amobeffect) {
        this.a = s;
        this.b = ImmutableList.copyOf(amobeffect);
    }

    public String b(String s) {
        return s + (this.a == null ? IRegistry.POTION.getKey(this).getKey() : this.a);
    }

    public List<MobEffect> a() {
        return this.b;
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
        a("strong_slowness", new PotionRegistry("slowness", new MobEffect[] { new MobEffect(MobEffects.SLOWER_MOVEMENT, 400, 3)}));
        a("turtle_master", new PotionRegistry("turtle_master", new MobEffect[] { new MobEffect(MobEffects.SLOWER_MOVEMENT, 400, 3), new MobEffect(MobEffects.RESISTANCE, 400, 2)}));
        a("long_turtle_master", new PotionRegistry("turtle_master", new MobEffect[] { new MobEffect(MobEffects.SLOWER_MOVEMENT, 800, 3), new MobEffect(MobEffects.RESISTANCE, 800, 2)}));
        a("strong_turtle_master", new PotionRegistry("turtle_master", new MobEffect[] { new MobEffect(MobEffects.SLOWER_MOVEMENT, 400, 5), new MobEffect(MobEffects.RESISTANCE, 400, 3)}));
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
        a("luck", new PotionRegistry("luck", new MobEffect[] { new MobEffect(MobEffects.LUCK, 6000)}));
        a("slow_falling", new PotionRegistry(new MobEffect[] { new MobEffect(MobEffects.SLOW_FALLING, 1800)}));
        a("long_slow_falling", new PotionRegistry("slow_falling", new MobEffect[] { new MobEffect(MobEffects.SLOW_FALLING, 4800)}));
    }

    protected static void a(String s, PotionRegistry potionregistry) {
        IRegistry.POTION.a(new MinecraftKey(s), (Object) potionregistry);
    }

    public boolean c() {
        if (!this.b.isEmpty()) {
            UnmodifiableIterator unmodifiableiterator = this.b.iterator();

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
