package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.animal.goat.GoatAi;

public class SensorType<U extends Sensor<?>> {

    public static final SensorType<SensorDummy> DUMMY = a("dummy", SensorDummy::new);
    public static final SensorType<SensorNearestItems> NEAREST_ITEMS = a("nearest_items", SensorNearestItems::new);
    public static final SensorType<SensorNearestLivingEntities> NEAREST_LIVING_ENTITIES = a("nearest_living_entities", SensorNearestLivingEntities::new);
    public static final SensorType<SensorNearestPlayers> NEAREST_PLAYERS = a("nearest_players", SensorNearestPlayers::new);
    public static final SensorType<SensorNearestBed> NEAREST_BED = a("nearest_bed", SensorNearestBed::new);
    public static final SensorType<SensorHurtBy> HURT_BY = a("hurt_by", SensorHurtBy::new);
    public static final SensorType<SensorVillagerHostiles> VILLAGER_HOSTILES = a("villager_hostiles", SensorVillagerHostiles::new);
    public static final SensorType<SensorVillagerBabies> VILLAGER_BABIES = a("villager_babies", SensorVillagerBabies::new);
    public static final SensorType<SensorSecondaryPlaces> SECONDARY_POIS = a("secondary_pois", SensorSecondaryPlaces::new);
    public static final SensorType<SensorGolemLastSeen> GOLEM_DETECTED = a("golem_detected", SensorGolemLastSeen::new);
    public static final SensorType<SensorPiglinSpecific> PIGLIN_SPECIFIC_SENSOR = a("piglin_specific_sensor", SensorPiglinSpecific::new);
    public static final SensorType<SensorPiglinBruteSpecific> PIGLIN_BRUTE_SPECIFIC_SENSOR = a("piglin_brute_specific_sensor", SensorPiglinBruteSpecific::new);
    public static final SensorType<SensorHoglinSpecific> HOGLIN_SPECIFIC_SENSOR = a("hoglin_specific_sensor", SensorHoglinSpecific::new);
    public static final SensorType<SensorAdult> NEAREST_ADULT = a("nearest_adult", SensorAdult::new);
    public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = a("axolotl_attackables", AxolotlAttackablesSensor::new);
    public static final SensorType<TemptingSensor> AXOLOTL_TEMPTATIONS = a("axolotl_temptations", () -> {
        return new TemptingSensor(AxolotlAi.a());
    });
    public static final SensorType<TemptingSensor> GOAT_TEMPTATIONS = a("goat_temptations", () -> {
        return new TemptingSensor(GoatAi.a());
    });
    private final Supplier<U> factory;

    private SensorType(Supplier<U> supplier) {
        this.factory = supplier;
    }

    public U a() {
        return (Sensor) this.factory.get();
    }

    private static <U extends Sensor<?>> SensorType<U> a(String s, Supplier<U> supplier) {
        return (SensorType) IRegistry.a((IRegistry) IRegistry.SENSOR_TYPE, new MinecraftKey(s), (Object) (new SensorType<>(supplier)));
    }
}
