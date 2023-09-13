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
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.resources.MinecraftKey;

public class CriterionTriggers {

    private static final Map<MinecraftKey, CriterionTrigger<?>> CRITERIA = Maps.newHashMap();
    public static final CriterionTriggerImpossible IMPOSSIBLE = (CriterionTriggerImpossible) a((CriterionTrigger) (new CriterionTriggerImpossible()));
    public static final CriterionTriggerKilled PLAYER_KILLED_ENTITY = (CriterionTriggerKilled) a((CriterionTrigger) (new CriterionTriggerKilled(new MinecraftKey("player_killed_entity"))));
    public static final CriterionTriggerKilled ENTITY_KILLED_PLAYER = (CriterionTriggerKilled) a((CriterionTrigger) (new CriterionTriggerKilled(new MinecraftKey("entity_killed_player"))));
    public static final CriterionTriggerEnterBlock ENTER_BLOCK = (CriterionTriggerEnterBlock) a((CriterionTrigger) (new CriterionTriggerEnterBlock()));
    public static final CriterionTriggerInventoryChanged INVENTORY_CHANGED = (CriterionTriggerInventoryChanged) a((CriterionTrigger) (new CriterionTriggerInventoryChanged()));
    public static final CriterionTriggerRecipeUnlocked RECIPE_UNLOCKED = (CriterionTriggerRecipeUnlocked) a((CriterionTrigger) (new CriterionTriggerRecipeUnlocked()));
    public static final CriterionTriggerPlayerHurtEntity PLAYER_HURT_ENTITY = (CriterionTriggerPlayerHurtEntity) a((CriterionTrigger) (new CriterionTriggerPlayerHurtEntity()));
    public static final CriterionTriggerEntityHurtPlayer ENTITY_HURT_PLAYER = (CriterionTriggerEntityHurtPlayer) a((CriterionTrigger) (new CriterionTriggerEntityHurtPlayer()));
    public static final CriterionTriggerEnchantedItem ENCHANTED_ITEM = (CriterionTriggerEnchantedItem) a((CriterionTrigger) (new CriterionTriggerEnchantedItem()));
    public static final CriterionTriggerFilledBucket FILLED_BUCKET = (CriterionTriggerFilledBucket) a((CriterionTrigger) (new CriterionTriggerFilledBucket()));
    public static final CriterionTriggerBrewedPotion BREWED_POTION = (CriterionTriggerBrewedPotion) a((CriterionTrigger) (new CriterionTriggerBrewedPotion()));
    public static final CriterionTriggerConstructBeacon CONSTRUCT_BEACON = (CriterionTriggerConstructBeacon) a((CriterionTrigger) (new CriterionTriggerConstructBeacon()));
    public static final CriterionTriggerUsedEnderEye USED_ENDER_EYE = (CriterionTriggerUsedEnderEye) a((CriterionTrigger) (new CriterionTriggerUsedEnderEye()));
    public static final CriterionTriggerSummonedEntity SUMMONED_ENTITY = (CriterionTriggerSummonedEntity) a((CriterionTrigger) (new CriterionTriggerSummonedEntity()));
    public static final CriterionTriggerBredAnimals BRED_ANIMALS = (CriterionTriggerBredAnimals) a((CriterionTrigger) (new CriterionTriggerBredAnimals()));
    public static final CriterionTriggerLocation LOCATION = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("location"))));
    public static final CriterionTriggerLocation SLEPT_IN_BED = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("slept_in_bed"))));
    public static final CriterionTriggerCuredZombieVillager CURED_ZOMBIE_VILLAGER = (CriterionTriggerCuredZombieVillager) a((CriterionTrigger) (new CriterionTriggerCuredZombieVillager()));
    public static final CriterionTriggerVillagerTrade TRADE = (CriterionTriggerVillagerTrade) a((CriterionTrigger) (new CriterionTriggerVillagerTrade()));
    public static final CriterionTriggerItemDurabilityChanged ITEM_DURABILITY_CHANGED = (CriterionTriggerItemDurabilityChanged) a((CriterionTrigger) (new CriterionTriggerItemDurabilityChanged()));
    public static final CriterionTriggerLevitation LEVITATION = (CriterionTriggerLevitation) a((CriterionTrigger) (new CriterionTriggerLevitation()));
    public static final CriterionTriggerChangedDimension CHANGED_DIMENSION = (CriterionTriggerChangedDimension) a((CriterionTrigger) (new CriterionTriggerChangedDimension()));
    public static final CriterionTriggerTick TICK = (CriterionTriggerTick) a((CriterionTrigger) (new CriterionTriggerTick()));
    public static final CriterionTriggerTamedAnimal TAME_ANIMAL = (CriterionTriggerTamedAnimal) a((CriterionTrigger) (new CriterionTriggerTamedAnimal()));
    public static final CriterionTriggerPlacedBlock PLACED_BLOCK = (CriterionTriggerPlacedBlock) a((CriterionTrigger) (new CriterionTriggerPlacedBlock()));
    public static final CriterionTriggerConsumeItem CONSUME_ITEM = (CriterionTriggerConsumeItem) a((CriterionTrigger) (new CriterionTriggerConsumeItem()));
    public static final CriterionTriggerEffectsChanged EFFECTS_CHANGED = (CriterionTriggerEffectsChanged) a((CriterionTrigger) (new CriterionTriggerEffectsChanged()));
    public static final CriterionTriggerUsedTotem USED_TOTEM = (CriterionTriggerUsedTotem) a((CriterionTrigger) (new CriterionTriggerUsedTotem()));
    public static final CriterionTriggerNetherTravel NETHER_TRAVEL = (CriterionTriggerNetherTravel) a((CriterionTrigger) (new CriterionTriggerNetherTravel()));
    public static final CriterionTriggerFishingRodHooked FISHING_ROD_HOOKED = (CriterionTriggerFishingRodHooked) a((CriterionTrigger) (new CriterionTriggerFishingRodHooked()));
    public static final CriterionTriggerChanneledLightning CHANNELED_LIGHTNING = (CriterionTriggerChanneledLightning) a((CriterionTrigger) (new CriterionTriggerChanneledLightning()));
    public static final CriterionTriggerShotCrossbow SHOT_CROSSBOW = (CriterionTriggerShotCrossbow) a((CriterionTrigger) (new CriterionTriggerShotCrossbow()));
    public static final CriterionTriggerKilledByCrossbow KILLED_BY_CROSSBOW = (CriterionTriggerKilledByCrossbow) a((CriterionTrigger) (new CriterionTriggerKilledByCrossbow()));
    public static final CriterionTriggerLocation RAID_WIN = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("hero_of_the_village"))));
    public static final CriterionTriggerLocation BAD_OMEN = (CriterionTriggerLocation) a((CriterionTrigger) (new CriterionTriggerLocation(new MinecraftKey("voluntary_exile"))));
    public static final CriterionSlideDownBlock HONEY_BLOCK_SLIDE = (CriterionSlideDownBlock) a((CriterionTrigger) (new CriterionSlideDownBlock()));
    public static final CriterionTriggerBeeNestDestroyed BEE_NEST_DESTROYED = (CriterionTriggerBeeNestDestroyed) a((CriterionTrigger) (new CriterionTriggerBeeNestDestroyed()));
    public static final CriterionTriggerTargetHit TARGET_BLOCK_HIT = (CriterionTriggerTargetHit) a((CriterionTrigger) (new CriterionTriggerTargetHit()));
    public static final CriterionTriggerInteractBlock ITEM_USED_ON_BLOCK = (CriterionTriggerInteractBlock) a((CriterionTrigger) (new CriterionTriggerInteractBlock()));
    public static final CriterionTriggerPlayerGeneratesContainerLoot GENERATE_LOOT = (CriterionTriggerPlayerGeneratesContainerLoot) a((CriterionTrigger) (new CriterionTriggerPlayerGeneratesContainerLoot()));
    public static final CriterionTriggerThrownItemPickedUpByEntity ITEM_PICKED_UP_BY_ENTITY = (CriterionTriggerThrownItemPickedUpByEntity) a((CriterionTrigger) (new CriterionTriggerThrownItemPickedUpByEntity()));
    public static final CriterionTriggerPlayerInteractedWithEntity PLAYER_INTERACTED_WITH_ENTITY = (CriterionTriggerPlayerInteractedWithEntity) a((CriterionTrigger) (new CriterionTriggerPlayerInteractedWithEntity()));
    public static final StartRidingTrigger START_RIDING_TRIGGER = (StartRidingTrigger) a((CriterionTrigger) (new StartRidingTrigger()));
    public static final LightningStrikeTrigger LIGHTNING_STRIKE = (LightningStrikeTrigger) a((CriterionTrigger) (new LightningStrikeTrigger()));
    public static final UsingItemTrigger USING_ITEM = (UsingItemTrigger) a((CriterionTrigger) (new UsingItemTrigger()));

    public CriterionTriggers() {}

    private static <T extends CriterionTrigger<?>> T a(T t0) {
        if (CriterionTriggers.CRITERIA.containsKey(t0.a())) {
            throw new IllegalArgumentException("Duplicate criterion id " + t0.a());
        } else {
            CriterionTriggers.CRITERIA.put(t0.a(), t0);
            return t0;
        }
    }

    @Nullable
    public static <T extends CriterionInstance> CriterionTrigger<T> a(MinecraftKey minecraftkey) {
        return (CriterionTrigger) CriterionTriggers.CRITERIA.get(minecraftkey);
    }

    public static Iterable<? extends CriterionTrigger<?>> a() {
        return CriterionTriggers.CRITERIA.values();
    }
}
