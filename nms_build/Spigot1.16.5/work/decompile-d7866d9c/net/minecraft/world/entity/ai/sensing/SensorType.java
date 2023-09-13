package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;

public class SensorType<U extends Sensor<?>> {

    public static final SensorType<SensorDummy> a = a("dummy", SensorDummy::new);
    public static final SensorType<SensorNearestItems> b = a("nearest_items", SensorNearestItems::new);
    public static final SensorType<SensorNearestLivingEntities> c = a("nearest_living_entities", SensorNearestLivingEntities::new);
    public static final SensorType<SensorNearestPlayers> d = a("nearest_players", SensorNearestPlayers::new);
    public static final SensorType<SensorNearestBed> e = a("nearest_bed", SensorNearestBed::new);
    public static final SensorType<SensorHurtBy> f = a("hurt_by", SensorHurtBy::new);
    public static final SensorType<SensorVillagerHostiles> g = a("villager_hostiles", SensorVillagerHostiles::new);
    public static final SensorType<SensorVillagerBabies> h = a("villager_babies", SensorVillagerBabies::new);
    public static final SensorType<SensorSecondaryPlaces> i = a("secondary_pois", SensorSecondaryPlaces::new);
    public static final SensorType<SensorGolemLastSeen> j = a("golem_detected", SensorGolemLastSeen::new);
    public static final SensorType<SensorPiglinSpecific> k = a("piglin_specific_sensor", SensorPiglinSpecific::new);
    public static final SensorType<SensorPiglinBruteSpecific> l = a("piglin_brute_specific_sensor", SensorPiglinBruteSpecific::new);
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
