package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.animal.goat.GoatAi;

public class SensorType<U extends Sensor<?>> {

    public static final SensorType<SensorDummy> DUMMY = register("dummy", SensorDummy::new);
    public static final SensorType<SensorNearestItems> NEAREST_ITEMS = register("nearest_items", SensorNearestItems::new);
    public static final SensorType<SensorNearestLivingEntities> NEAREST_LIVING_ENTITIES = register("nearest_living_entities", SensorNearestLivingEntities::new);
    public static final SensorType<SensorNearestPlayers> NEAREST_PLAYERS = register("nearest_players", SensorNearestPlayers::new);
    public static final SensorType<SensorNearestBed> NEAREST_BED = register("nearest_bed", SensorNearestBed::new);
    public static final SensorType<SensorHurtBy> HURT_BY = register("hurt_by", SensorHurtBy::new);
    public static final SensorType<SensorVillagerHostiles> VILLAGER_HOSTILES = register("villager_hostiles", SensorVillagerHostiles::new);
    public static final SensorType<SensorVillagerBabies> VILLAGER_BABIES = register("villager_babies", SensorVillagerBabies::new);
    public static final SensorType<SensorSecondaryPlaces> SECONDARY_POIS = register("secondary_pois", SensorSecondaryPlaces::new);
    public static final SensorType<SensorGolemLastSeen> GOLEM_DETECTED = register("golem_detected", SensorGolemLastSeen::new);
    public static final SensorType<SensorPiglinSpecific> PIGLIN_SPECIFIC_SENSOR = register("piglin_specific_sensor", SensorPiglinSpecific::new);
    public static final SensorType<SensorPiglinBruteSpecific> PIGLIN_BRUTE_SPECIFIC_SENSOR = register("piglin_brute_specific_sensor", SensorPiglinBruteSpecific::new);
    public static final SensorType<SensorHoglinSpecific> HOGLIN_SPECIFIC_SENSOR = register("hoglin_specific_sensor", SensorHoglinSpecific::new);
    public static final SensorType<SensorAdult> NEAREST_ADULT = register("nearest_adult", SensorAdult::new);
    public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = register("axolotl_attackables", AxolotlAttackablesSensor::new);
    public static final SensorType<TemptingSensor> AXOLOTL_TEMPTATIONS = register("axolotl_temptations", () -> {
        return new TemptingSensor(AxolotlAi.getTemptations());
    });
    public static final SensorType<TemptingSensor> GOAT_TEMPTATIONS = register("goat_temptations", () -> {
        return new TemptingSensor(GoatAi.getTemptations());
    });
    private final Supplier<U> factory;

    private SensorType(Supplier<U> supplier) {
        this.factory = supplier;
    }

    public U create() {
        return (Sensor) this.factory.get();
    }

    private static <U extends Sensor<?>> SensorType<U> register(String s, Supplier<U> supplier) {
        return (SensorType) IRegistry.register(IRegistry.SENSOR_TYPE, new MinecraftKey(s), new SensorType<>(supplier));
    }
}
