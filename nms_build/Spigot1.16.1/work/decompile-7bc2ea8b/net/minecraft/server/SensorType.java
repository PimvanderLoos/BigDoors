package net.minecraft.server;

import java.util.function.Supplier;

public class SensorType<U extends Sensor<?>> {

    public static final SensorType<SensorDummy> a = a("dummy", SensorDummy::new);
    public static final SensorType<SensorNearestItems> b = a("nearest_items", SensorNearestItems::new);
    public static final SensorType<SensorNearestLivingEntities> c = a("nearest_living_entities", SensorNearestLivingEntities::new);
    public static final SensorType<SensorNearestPlayers> d = a("nearest_players", SensorNearestPlayers::new);
    public static final SensorType<SensorInteractableDoors> e = a("interactable_doors", SensorInteractableDoors::new);
    public static final SensorType<SensorNearestBed> f = a("nearest_bed", SensorNearestBed::new);
    public static final SensorType<SensorHurtBy> g = a("hurt_by", SensorHurtBy::new);
    public static final SensorType<SensorVillagerHostiles> h = a("villager_hostiles", SensorVillagerHostiles::new);
    public static final SensorType<SensorVillagerBabies> i = a("villager_babies", SensorVillagerBabies::new);
    public static final SensorType<SensorSecondaryPlaces> j = a("secondary_pois", SensorSecondaryPlaces::new);
    public static final SensorType<SensorGolemLastSeen> k = a("golem_last_seen", SensorGolemLastSeen::new);
    public static final SensorType<SensorPiglinSpecific> l = a("piglin_specific_sensor", SensorPiglinSpecific::new);
    public static final SensorType<SensorHoglinSpecific> m = a("hoglin_specific_sensor", SensorHoglinSpecific::new);
    public static final SensorType<SensorAdult> n = a("nearest_adult", SensorAdult::new);
    private final Supplier<U> o;

    private SensorType(Supplier<U> supplier) {
        this.o = supplier;
    }

    public U a() {
        return (Sensor) this.o.get();
    }

    private static <U extends Sensor<?>> SensorType<U> a(String s, Supplier<U> supplier) {
        return (SensorType) IRegistry.a((IRegistry) IRegistry.SENSOR_TYPE, new MinecraftKey(s), (Object) (new SensorType<>(supplier)));
    }
}
