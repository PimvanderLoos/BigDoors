package net.minecraft.world.food;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class Foods {

    public static final FoodInfo APPLE = (new FoodInfo.a()).a(4).a(0.3F).d();
    public static final FoodInfo BAKED_POTATO = (new FoodInfo.a()).a(5).a(0.6F).d();
    public static final FoodInfo BEEF = (new FoodInfo.a()).a(3).a(0.3F).a().d();
    public static final FoodInfo BEETROOT = (new FoodInfo.a()).a(1).a(0.6F).d();
    public static final FoodInfo BEETROOT_SOUP = a(6).d();
    public static final FoodInfo BREAD = (new FoodInfo.a()).a(5).a(0.6F).d();
    public static final FoodInfo CARROT = (new FoodInfo.a()).a(3).a(0.6F).d();
    public static final FoodInfo CHICKEN = (new FoodInfo.a()).a(2).a(0.3F).a(new MobEffect(MobEffects.HUNGER, 600, 0), 0.3F).a().d();
    public static final FoodInfo CHORUS_FRUIT = (new FoodInfo.a()).a(4).a(0.3F).b().d();
    public static final FoodInfo COD = (new FoodInfo.a()).a(2).a(0.1F).d();
    public static final FoodInfo COOKED_BEEF = (new FoodInfo.a()).a(8).a(0.8F).a().d();
    public static final FoodInfo COOKED_CHICKEN = (new FoodInfo.a()).a(6).a(0.6F).a().d();
    public static final FoodInfo COOKED_COD = (new FoodInfo.a()).a(5).a(0.6F).d();
    public static final FoodInfo COOKED_MUTTON = (new FoodInfo.a()).a(6).a(0.8F).a().d();
    public static final FoodInfo COOKED_PORKCHOP = (new FoodInfo.a()).a(8).a(0.8F).a().d();
    public static final FoodInfo COOKED_RABBIT = (new FoodInfo.a()).a(5).a(0.6F).a().d();
    public static final FoodInfo COOKED_SALMON = (new FoodInfo.a()).a(6).a(0.8F).d();
    public static final FoodInfo COOKIE = (new FoodInfo.a()).a(2).a(0.1F).d();
    public static final FoodInfo DRIED_KELP = (new FoodInfo.a()).a(1).a(0.3F).c().d();
    public static final FoodInfo ENCHANTED_GOLDEN_APPLE = (new FoodInfo.a()).a(4).a(1.2F).a(new MobEffect(MobEffects.REGENERATION, 400, 1), 1.0F).a(new MobEffect(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F).a(new MobEffect(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0F).a(new MobEffect(MobEffects.ABSORPTION, 2400, 3), 1.0F).b().d();
    public static final FoodInfo GOLDEN_APPLE = (new FoodInfo.a()).a(4).a(1.2F).a(new MobEffect(MobEffects.REGENERATION, 100, 1), 1.0F).a(new MobEffect(MobEffects.ABSORPTION, 2400, 0), 1.0F).b().d();
    public static final FoodInfo GOLDEN_CARROT = (new FoodInfo.a()).a(6).a(1.2F).d();
    public static final FoodInfo HONEY_BOTTLE = (new FoodInfo.a()).a(6).a(0.1F).d();
    public static final FoodInfo MELON_SLICE = (new FoodInfo.a()).a(2).a(0.3F).d();
    public static final FoodInfo MUSHROOM_STEW = a(6).d();
    public static final FoodInfo MUTTON = (new FoodInfo.a()).a(2).a(0.3F).a().d();
    public static final FoodInfo POISONOUS_POTATO = (new FoodInfo.a()).a(2).a(0.3F).a(new MobEffect(MobEffects.POISON, 100, 0), 0.6F).d();
    public static final FoodInfo PORKCHOP = (new FoodInfo.a()).a(3).a(0.3F).a().d();
    public static final FoodInfo POTATO = (new FoodInfo.a()).a(1).a(0.3F).d();
    public static final FoodInfo PUFFERFISH = (new FoodInfo.a()).a(1).a(0.1F).a(new MobEffect(MobEffects.POISON, 1200, 1), 1.0F).a(new MobEffect(MobEffects.HUNGER, 300, 2), 1.0F).a(new MobEffect(MobEffects.CONFUSION, 300, 0), 1.0F).d();
    public static final FoodInfo PUMPKIN_PIE = (new FoodInfo.a()).a(8).a(0.3F).d();
    public static final FoodInfo RABBIT = (new FoodInfo.a()).a(3).a(0.3F).a().d();
    public static final FoodInfo RABBIT_STEW = a(10).d();
    public static final FoodInfo ROTTEN_FLESH = (new FoodInfo.a()).a(4).a(0.1F).a(new MobEffect(MobEffects.HUNGER, 600, 0), 0.8F).a().d();
    public static final FoodInfo SALMON = (new FoodInfo.a()).a(2).a(0.1F).d();
    public static final FoodInfo SPIDER_EYE = (new FoodInfo.a()).a(2).a(0.8F).a(new MobEffect(MobEffects.POISON, 100, 0), 1.0F).d();
    public static final FoodInfo SUSPICIOUS_STEW = a(6).b().d();
    public static final FoodInfo SWEET_BERRIES = (new FoodInfo.a()).a(2).a(0.1F).d();
    public static final FoodInfo GLOW_BERRIES = (new FoodInfo.a()).a(2).a(0.1F).d();
    public static final FoodInfo TROPICAL_FISH = (new FoodInfo.a()).a(1).a(0.1F).d();

    public Foods() {}

    private static FoodInfo.a a(int i) {
        return (new FoodInfo.a()).a(i).a(0.6F);
    }
}
