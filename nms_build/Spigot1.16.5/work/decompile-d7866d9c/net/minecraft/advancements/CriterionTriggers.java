package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.CriterionSlideDownBlock;
import net.minecraft.advancements.critereon.CriterionTriggerBeeNestDestroyed;
import net.minecraft.advancements.critereon.CriterionTriggerBredAnimals;
import net.minecraft.advancements.critereon.CriterionTriggerBrewedPotion;
import net.minecraft.advancements.critereon.CriterionTriggerChangedDimension;
import net.minecraft.advancements.critereon.CriterionTriggerChanneledLightning;
import net.minecraft.advancements.critereon.CriterionTriggerConstructBeacon;
import net.minecraft.advancements.critereon.CriterionTriggerConsumeItem;
import net.minecraft.advancements.critereon.CriterionTriggerCuredZombieVillager;
import net.minecraft.advancements.critereon.CriterionTriggerEffectsChanged;
import net.minecraft.advancements.critereon.CriterionTriggerEnchantedItem;
import net.minecraft.advancements.critereon.CriterionTriggerEnterBlock;
import net.minecraft.advancements.critereon.CriterionTriggerEntityHurtPlayer;
import net.minecraft.advancements.critereon.CriterionTriggerFilledBucket;
import net.minecraft.advancements.critereon.CriterionTriggerFishingRodHooked;
import net.minecraft.advancements.critereon.CriterionTriggerImpossible;
import net.minecraft.advancements.critereon.CriterionTriggerInteractBlock;
import net.minecraft.advancements.critereon.CriterionTriggerInventoryChanged;
import net.minecraft.advancements.critereon.CriterionTriggerItemDurabilityChanged;
import net.minecraft.advancements.critereon.CriterionTriggerKilled;
import net.minecraft.advancements.critereon.CriterionTriggerKilledByCrossbow;
import net.minecraft.advancements.critereon.CriterionTriggerLevitation;
import net.minecraft.advancements.critereon.CriterionTriggerLocation;
import net.minecraft.advancements.critereon.CriterionTriggerNetherTravel;
import net.minecraft.advancements.critereon.CriterionTriggerPlacedBlock;
import net.minecraft.advancements.critereon.CriterionTriggerPlayerGeneratesContainerLoot;
import net.minecraft.advancements.critereon.CriterionTriggerPlayerHurtEntity;
import net.minecraft.advancements.critereon.CriterionTriggerPlayerInteractedWithEntity;
import net.minecraft.advancements.critereon.CriterionTriggerRecipeUnlocked;
import net.minecraft.advancements.critereon.CriterionTriggerShotCrossbow;
import net.minecraft.advancements.critereon.CriterionTriggerSummonedEntity;
import net.minecraft.advancements.critereon.CriterionTriggerTamedAnimal;
import net.minecraft.advancements.critereon.CriterionTriggerTargetHit;
import net.minecraft.advancements.critereon.CriterionTriggerThrownItemPickedUpByEntity;
import net.minecraft.advancements.critereon.CriterionTriggerTick;
import net.minecraft.advancements.critereon.CriterionTriggerUsedEnderEye;
import net.minecraft.advancements.critereon.CriterionTriggerUsedTotem;
import net.minecraft.advancements.critereon.CriterionTriggerVillagerTrade;
import net.minecraft.resources.MinecraftKey;

public class CriterionTriggers {

    private static final Map<MinecraftKey, CriterionTrigger<?>> Q = Maps.newHashMap();
    public static final CriterionTriggerImpossible a = (CriterionTriggerImpossible) a((CriterionTrigger) (new CriterionTriggerImpossible()));
    public static final CriterionTriggerKilled b = (CriterionTriggerKilled) a((CriterionTrigger) (new CriterionTriggerKilled(new MinecraftKey("player_killed_entity"))));
    public static final CriterionTriggerKilled c = (CriterionTriggerKilled) a((CriterionTrigger) (new CriterionTriggerKilled(new MinecraftKey("entity_killed_player"))));
    public static final CriterionTriggerEnterBlock d = (CriterionTriggerEnterBlock) a((CriterionTrigger) (new CriterionTriggerEnterBlock()));
    public static final CriterionTriggerInventoryChanged e = (CriterionTriggerInventoryChanged) a((CriterionTrigger) (new CriterionTriggerInventoryChanged()));
    public static final CriterionTriggerRecipeUnlocked f = (CriterionTriggerRecipeUnlocked) a((CriterionTrigger) (new CriterionTriggerRecipeUnlocked()));
    public static final CriterionTriggerPlayerHurtEntity g = (CriterionTriggerPlayerHurtEntity) a((CriterionTrigger) (new CriterionTriggerPlayerHurtEntity()));
    public static final CriterionTriggerEntityHurtPlayer h = (CriterionTriggerEntityHurtPlayer) a((CriterionTrigger) (new CriterionTriggerEntityHurtPlayer()));
    public static final CriterionTriggerEnchantedItem i = (CriterionTriggerEnchantedItem) a((CriterionTrigger) (new CriterionTriggerEnchantedItem()));
    public static final CriterionTriggerFilledBucket j = (CriterionTriggerFilledBucket) a((CriterionTrigger) (new CriterionTriggerFilledBucket()));
    public static final CriterionTriggerBrewedPotion k = (CriterionTriggerBrewedPotion) a((CriterionTrigger) (new CriterionTriggerBrewedPotion()));
    public static final CriterionTriggerConstructBeacon l = (CriterionTriggerConstructBeacon) a((CriterionTrigger) (new CriterionTriggerConstructBeacon()));
    public static final CriterionTriggerUsedEnderEye m = (CriterionTriggerUsedEnderEye) a((CriterionTrigger) (new CriterionTriggerUsedEnderEye()));
    public static final CriterionTriggerSummonedEntity n = (CriterionTriggerSummonedEntity) a((CriterionTrigger) (new CriterionTriggerSummonedEntity()));
    public static final CriterionTriggerBredAnimals o = (CriterionTriggerBredAnimals) a((CriterionTrigger) (new CriterionTriggerBredAnimals()));
    public static final CriterionTriggerLocation p = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("location"))));
    public static final CriterionTriggerLocation q = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("slept_in_bed"))));
    public static final CriterionTriggerCuredZombieVillager r = (CriterionTriggerCuredZombieVillager) a((CriterionTrigger) (new CriterionTriggerCuredZombieVillager()));
    public static final CriterionTriggerVillagerTrade s = (CriterionTriggerVillagerTrade) a((CriterionTrigger) (new CriterionTriggerVillagerTrade()));
    public static final CriterionTriggerItemDurabilityChanged t = (CriterionTriggerItemDurabilityChanged) a((CriterionTrigger) (new CriterionTriggerItemDurabilityChanged()));
    public static final CriterionTriggerLevitation u = (CriterionTriggerLevitation) a((CriterionTrigger) (new CriterionTriggerLevitation()));
    public static final CriterionTriggerChangedDimension v = (CriterionTriggerChangedDimension) a((CriterionTrigger) (new CriterionTriggerChangedDimension()));
    public static final CriterionTriggerTick w = (CriterionTriggerTick) a((CriterionTrigger) (new CriterionTriggerTick()));
    public static final CriterionTriggerTamedAnimal x = (CriterionTriggerTamedAnimal) a((CriterionTrigger) (new CriterionTriggerTamedAnimal()));
    public static final CriterionTriggerPlacedBlock y = (CriterionTriggerPlacedBlock) a((CriterionTrigger) (new CriterionTriggerPlacedBlock()));
    public static final CriterionTriggerConsumeItem z = (CriterionTriggerConsumeItem) a((CriterionTrigger) (new CriterionTriggerConsumeItem()));
    public static final CriterionTriggerEffectsChanged A = (CriterionTriggerEffectsChanged) a((CriterionTrigger) (new CriterionTriggerEffectsChanged()));
    public static final CriterionTriggerUsedTotem B = (CriterionTriggerUsedTotem) a((CriterionTrigger) (new CriterionTriggerUsedTotem()));
    public static final CriterionTriggerNetherTravel C = (CriterionTriggerNetherTravel) a((CriterionTrigger) (new CriterionTriggerNetherTravel()));
    public static final CriterionTriggerFishingRodHooked D = (CriterionTriggerFishingRodHooked) a((CriterionTrigger) (new CriterionTriggerFishingRodHooked()));
    public static final CriterionTriggerChanneledLightning E = (CriterionTriggerChanneledLightning) a((CriterionTrigger) (new CriterionTriggerChanneledLightning()));
    public static final CriterionTriggerShotCrossbow F = (CriterionTriggerShotCrossbow) a((CriterionTrigger) (new CriterionTriggerShotCrossbow()));
    public static final CriterionTriggerKilledByCrossbow G = (CriterionTriggerKilledByCrossbow) a((CriterionTrigger) (new CriterionTriggerKilledByCrossbow()));
    public static final CriterionTriggerLocation H = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("hero_of_the_village"))));
    public static final CriterionTriggerLocation I = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("voluntary_exile"))));
    public static final CriterionSlideDownBlock J = (CriterionSlideDownBlock) a((CriterionTrigger) (new CriterionSlideDownBlock()));
    public static final CriterionTriggerBeeNestDestroyed K = (CriterionTriggerBeeNestDestroyed) a((CriterionTrigger) (new CriterionTriggerBeeNestDestroyed()));
    public static final CriterionTriggerTargetHit L = (CriterionTriggerTargetHit) a((CriterionTrigger) (new CriterionTriggerTargetHit()));
    public static final CriterionTriggerInteractBlock M = (CriterionTriggerInteractBlock) a((CriterionTrigger) (new CriterionTriggerInteractBlock()));
    public static final CriterionTriggerPlayerGeneratesContainerLoot N = (CriterionTriggerPlayerGeneratesContainerLoot) a((CriterionTrigger) (new CriterionTriggerPlayerGeneratesContainerLoot()));
    public static final CriterionTriggerThrownItemPickedUpByEntity O = (CriterionTriggerThrownItemPickedUpByEntity) a((CriterionTrigger) (new CriterionTriggerThrownItemPickedUpByEntity()));
    public static final CriterionTriggerPlayerInteractedWithEntity P = (CriterionTriggerPlayerInteractedWithEntity) a((CriterionTrigger) (new CriterionTriggerPlayerInteractedWithEntity()));

    private static <T extends CriterionTrigger<?>> T a(T t0) {
        if (CriterionTriggers.Q.containsKey(t0.a())) {
            throw new IllegalArgumentException("Duplicate criterion id " + t0.a());
        } else {
            CriterionTriggers.Q.put(t0.a(), t0);
            return t0;
        }
    }

    @Nullable
    public static <T extends CriterionInstance> CriterionTrigger<T> a(MinecraftKey minecraftkey) {
        return (CriterionTrigger) CriterionTriggers.Q.get(minecraftkey);
    }

    public static Iterable<? extends CriterionTrigger<?>> a() {
        return CriterionTriggers.Q.values();
    }
}
