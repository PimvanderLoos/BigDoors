package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

public class CriterionTriggers {

    private static final Map<MinecraftKey, CriterionTrigger<?>> C = Maps.newHashMap();
    public static final CriterionTriggerImpossible a = (CriterionTriggerImpossible) a((CriterionTrigger) (new CriterionTriggerImpossible()));
    public static final CriterionTriggerKilled b = (CriterionTriggerKilled) a((CriterionTrigger) (new CriterionTriggerKilled(new MinecraftKey("player_killed_entity"))));
    public static final CriterionTriggerKilled c = (CriterionTriggerKilled) a((CriterionTrigger) (new CriterionTriggerKilled(new MinecraftKey("entity_killed_player"))));
    public static final CriterionTriggerEnterBlock d = (CriterionTriggerEnterBlock) a((CriterionTrigger) (new CriterionTriggerEnterBlock()));
    public static final CriterionTriggerInventoryChanged e = (CriterionTriggerInventoryChanged) a((CriterionTrigger) (new CriterionTriggerInventoryChanged()));
    public static final CriterionTriggerRecipeUnlocked f = (CriterionTriggerRecipeUnlocked) a((CriterionTrigger) (new CriterionTriggerRecipeUnlocked()));
    public static final CriterionTriggerPlayerHurtEntity g = (CriterionTriggerPlayerHurtEntity) a((CriterionTrigger) (new CriterionTriggerPlayerHurtEntity()));
    public static final CriterionTriggerEntityHurtPlayer h = (CriterionTriggerEntityHurtPlayer) a((CriterionTrigger) (new CriterionTriggerEntityHurtPlayer()));
    public static final CriterionTriggerEnchantedItem i = (CriterionTriggerEnchantedItem) a((CriterionTrigger) (new CriterionTriggerEnchantedItem()));
    public static final CriterionTriggerBrewedPotion j = (CriterionTriggerBrewedPotion) a((CriterionTrigger) (new CriterionTriggerBrewedPotion()));
    public static final CriterionTriggerConstructBeacon k = (CriterionTriggerConstructBeacon) a((CriterionTrigger) (new CriterionTriggerConstructBeacon()));
    public static final CriterionTriggerUsedEnderEye l = (CriterionTriggerUsedEnderEye) a((CriterionTrigger) (new CriterionTriggerUsedEnderEye()));
    public static final CriterionTriggerSummonedEntity m = (CriterionTriggerSummonedEntity) a((CriterionTrigger) (new CriterionTriggerSummonedEntity()));
    public static final CriterionTriggerBredAnimals n = (CriterionTriggerBredAnimals) a((CriterionTrigger) (new CriterionTriggerBredAnimals()));
    public static final CriterionTriggerLocation o = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("location"))));
    public static final CriterionTriggerLocation p = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("slept_in_bed"))));
    public static final CriterionTriggerCuredZombieVillager q = (CriterionTriggerCuredZombieVillager) a((CriterionTrigger) (new CriterionTriggerCuredZombieVillager()));
    public static final CriterionTriggerVillagerTrade r = (CriterionTriggerVillagerTrade) a((CriterionTrigger) (new CriterionTriggerVillagerTrade()));
    public static final CriterionTriggerItemDurabilityChanged s = (CriterionTriggerItemDurabilityChanged) a((CriterionTrigger) (new CriterionTriggerItemDurabilityChanged()));
    public static final CriterionTriggerLevitation t = (CriterionTriggerLevitation) a((CriterionTrigger) (new CriterionTriggerLevitation()));
    public static final CriterionTriggerChangedDimension u = (CriterionTriggerChangedDimension) a((CriterionTrigger) (new CriterionTriggerChangedDimension()));
    public static final CriterionTriggerTick v = (CriterionTriggerTick) a((CriterionTrigger) (new CriterionTriggerTick()));
    public static final CriterionTriggerTamedAnimal w = (CriterionTriggerTamedAnimal) a((CriterionTrigger) (new CriterionTriggerTamedAnimal()));
    public static final CriterionTriggerPlacedBlock x = (CriterionTriggerPlacedBlock) a((CriterionTrigger) (new CriterionTriggerPlacedBlock()));
    public static final CriterionTriggerConsumeItem y = (CriterionTriggerConsumeItem) a((CriterionTrigger) (new CriterionTriggerConsumeItem()));
    public static final CriterionTriggerEffectsChanged z = (CriterionTriggerEffectsChanged) a((CriterionTrigger) (new CriterionTriggerEffectsChanged()));
    public static final CriterionTriggerUsedTotem A = (CriterionTriggerUsedTotem) a((CriterionTrigger) (new CriterionTriggerUsedTotem()));
    public static final CriterionTriggerNetherTravel B = (CriterionTriggerNetherTravel) a((CriterionTrigger) (new CriterionTriggerNetherTravel()));

    private static <T extends CriterionTrigger> T a(T t0) {
        if (CriterionTriggers.C.containsKey(t0.a())) {
            throw new IllegalArgumentException("Duplicate criterion id " + t0.a());
        } else {
            CriterionTriggers.C.put(t0.a(), t0);
            return t0;
        }
    }

    @Nullable
    public static <T extends CriterionInstance> CriterionTrigger<T> a(MinecraftKey minecraftkey) {
        return (CriterionTrigger) CriterionTriggers.C.get(minecraftkey);
    }

    public static Iterable<? extends CriterionTrigger<?>> a() {
        return CriterionTriggers.C.values();
    }
}
