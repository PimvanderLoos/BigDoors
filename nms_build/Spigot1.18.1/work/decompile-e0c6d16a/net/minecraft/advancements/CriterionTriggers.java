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
import net.minecraft.advancements.critereon.DistanceTrigger;
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.resources.MinecraftKey;

public class CriterionTriggers {

    private static final Map<MinecraftKey, CriterionTrigger<?>> CRITERIA = Maps.newHashMap();
    public static final CriterionTriggerImpossible IMPOSSIBLE = (CriterionTriggerImpossible) register(new CriterionTriggerImpossible());
    public static final CriterionTriggerKilled PLAYER_KILLED_ENTITY = (CriterionTriggerKilled) register(new CriterionTriggerKilled(new MinecraftKey("player_killed_entity")));
    public static final CriterionTriggerKilled ENTITY_KILLED_PLAYER = (CriterionTriggerKilled) register(new CriterionTriggerKilled(new MinecraftKey("entity_killed_player")));
    public static final CriterionTriggerEnterBlock ENTER_BLOCK = (CriterionTriggerEnterBlock) register(new CriterionTriggerEnterBlock());
    public static final CriterionTriggerInventoryChanged INVENTORY_CHANGED = (CriterionTriggerInventoryChanged) register(new CriterionTriggerInventoryChanged());
    public static final CriterionTriggerRecipeUnlocked RECIPE_UNLOCKED = (CriterionTriggerRecipeUnlocked) register(new CriterionTriggerRecipeUnlocked());
    public static final CriterionTriggerPlayerHurtEntity PLAYER_HURT_ENTITY = (CriterionTriggerPlayerHurtEntity) register(new CriterionTriggerPlayerHurtEntity());
    public static final CriterionTriggerEntityHurtPlayer ENTITY_HURT_PLAYER = (CriterionTriggerEntityHurtPlayer) register(new CriterionTriggerEntityHurtPlayer());
    public static final CriterionTriggerEnchantedItem ENCHANTED_ITEM = (CriterionTriggerEnchantedItem) register(new CriterionTriggerEnchantedItem());
    public static final CriterionTriggerFilledBucket FILLED_BUCKET = (CriterionTriggerFilledBucket) register(new CriterionTriggerFilledBucket());
    public static final CriterionTriggerBrewedPotion BREWED_POTION = (CriterionTriggerBrewedPotion) register(new CriterionTriggerBrewedPotion());
    public static final CriterionTriggerConstructBeacon CONSTRUCT_BEACON = (CriterionTriggerConstructBeacon) register(new CriterionTriggerConstructBeacon());
    public static final CriterionTriggerUsedEnderEye USED_ENDER_EYE = (CriterionTriggerUsedEnderEye) register(new CriterionTriggerUsedEnderEye());
    public static final CriterionTriggerSummonedEntity SUMMONED_ENTITY = (CriterionTriggerSummonedEntity) register(new CriterionTriggerSummonedEntity());
    public static final CriterionTriggerBredAnimals BRED_ANIMALS = (CriterionTriggerBredAnimals) register(new CriterionTriggerBredAnimals());
    public static final CriterionTriggerLocation LOCATION = (CriterionTriggerLocation) register(new CriterionTriggerLocation(new MinecraftKey("location")));
    public static final CriterionTriggerLocation SLEPT_IN_BED = (CriterionTriggerLocation) register(new CriterionTriggerLocation(new MinecraftKey("slept_in_bed")));
    public static final CriterionTriggerCuredZombieVillager CURED_ZOMBIE_VILLAGER = (CriterionTriggerCuredZombieVillager) register(new CriterionTriggerCuredZombieVillager());
    public static final CriterionTriggerVillagerTrade TRADE = (CriterionTriggerVillagerTrade) register(new CriterionTriggerVillagerTrade());
    public static final CriterionTriggerItemDurabilityChanged ITEM_DURABILITY_CHANGED = (CriterionTriggerItemDurabilityChanged) register(new CriterionTriggerItemDurabilityChanged());
    public static final CriterionTriggerLevitation LEVITATION = (CriterionTriggerLevitation) register(new CriterionTriggerLevitation());
    public static final CriterionTriggerChangedDimension CHANGED_DIMENSION = (CriterionTriggerChangedDimension) register(new CriterionTriggerChangedDimension());
    public static final CriterionTriggerTick TICK = (CriterionTriggerTick) register(new CriterionTriggerTick());
    public static final CriterionTriggerTamedAnimal TAME_ANIMAL = (CriterionTriggerTamedAnimal) register(new CriterionTriggerTamedAnimal());
    public static final CriterionTriggerPlacedBlock PLACED_BLOCK = (CriterionTriggerPlacedBlock) register(new CriterionTriggerPlacedBlock());
    public static final CriterionTriggerConsumeItem CONSUME_ITEM = (CriterionTriggerConsumeItem) register(new CriterionTriggerConsumeItem());
    public static final CriterionTriggerEffectsChanged EFFECTS_CHANGED = (CriterionTriggerEffectsChanged) register(new CriterionTriggerEffectsChanged());
    public static final CriterionTriggerUsedTotem USED_TOTEM = (CriterionTriggerUsedTotem) register(new CriterionTriggerUsedTotem());
    public static final DistanceTrigger NETHER_TRAVEL = (DistanceTrigger) register(new DistanceTrigger(new MinecraftKey("nether_travel")));
    public static final CriterionTriggerFishingRodHooked FISHING_ROD_HOOKED = (CriterionTriggerFishingRodHooked) register(new CriterionTriggerFishingRodHooked());
    public static final CriterionTriggerChanneledLightning CHANNELED_LIGHTNING = (CriterionTriggerChanneledLightning) register(new CriterionTriggerChanneledLightning());
    public static final CriterionTriggerShotCrossbow SHOT_CROSSBOW = (CriterionTriggerShotCrossbow) register(new CriterionTriggerShotCrossbow());
    public static final CriterionTriggerKilledByCrossbow KILLED_BY_CROSSBOW = (CriterionTriggerKilledByCrossbow) register(new CriterionTriggerKilledByCrossbow());
    public static final CriterionTriggerLocation RAID_WIN = (CriterionTriggerLocation) register(new CriterionTriggerLocation(new MinecraftKey("hero_of_the_village")));
    public static final CriterionTriggerLocation BAD_OMEN = (CriterionTriggerLocation) register(new CriterionTriggerLocation(new MinecraftKey("voluntary_exile")));
    public static final CriterionSlideDownBlock HONEY_BLOCK_SLIDE = (CriterionSlideDownBlock) register(new CriterionSlideDownBlock());
    public static final CriterionTriggerBeeNestDestroyed BEE_NEST_DESTROYED = (CriterionTriggerBeeNestDestroyed) register(new CriterionTriggerBeeNestDestroyed());
    public static final CriterionTriggerTargetHit TARGET_BLOCK_HIT = (CriterionTriggerTargetHit) register(new CriterionTriggerTargetHit());
    public static final CriterionTriggerInteractBlock ITEM_USED_ON_BLOCK = (CriterionTriggerInteractBlock) register(new CriterionTriggerInteractBlock());
    public static final CriterionTriggerPlayerGeneratesContainerLoot GENERATE_LOOT = (CriterionTriggerPlayerGeneratesContainerLoot) register(new CriterionTriggerPlayerGeneratesContainerLoot());
    public static final CriterionTriggerThrownItemPickedUpByEntity ITEM_PICKED_UP_BY_ENTITY = (CriterionTriggerThrownItemPickedUpByEntity) register(new CriterionTriggerThrownItemPickedUpByEntity());
    public static final CriterionTriggerPlayerInteractedWithEntity PLAYER_INTERACTED_WITH_ENTITY = (CriterionTriggerPlayerInteractedWithEntity) register(new CriterionTriggerPlayerInteractedWithEntity());
    public static final StartRidingTrigger START_RIDING_TRIGGER = (StartRidingTrigger) register(new StartRidingTrigger());
    public static final LightningStrikeTrigger LIGHTNING_STRIKE = (LightningStrikeTrigger) register(new LightningStrikeTrigger());
    public static final UsingItemTrigger USING_ITEM = (UsingItemTrigger) register(new UsingItemTrigger());
    public static final DistanceTrigger FALL_FROM_HEIGHT = (DistanceTrigger) register(new DistanceTrigger(new MinecraftKey("fall_from_height")));
    public static final DistanceTrigger RIDE_ENTITY_IN_LAVA_TRIGGER = (DistanceTrigger) register(new DistanceTrigger(new MinecraftKey("ride_entity_in_lava")));

    public CriterionTriggers() {}

    private static <T extends CriterionTrigger<?>> T register(T t0) {
        if (CriterionTriggers.CRITERIA.containsKey(t0.getId())) {
            throw new IllegalArgumentException("Duplicate criterion id " + t0.getId());
        } else {
            CriterionTriggers.CRITERIA.put(t0.getId(), t0);
            return t0;
        }
    }

    @Nullable
    public static <T extends CriterionInstance> CriterionTrigger<T> getCriterion(MinecraftKey minecraftkey) {
        return (CriterionTrigger) CriterionTriggers.CRITERIA.get(minecraftkey);
    }

    public static Iterable<? extends CriterionTrigger<?>> all() {
        return CriterionTriggers.CRITERIA.values();
    }
}
