package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;

public class PoiTypeTags {

    public static final TagKey<VillagePlaceType> ACQUIRABLE_JOB_SITE = create("acquirable_job_site");
    public static final TagKey<VillagePlaceType> VILLAGE = create("village");
    public static final TagKey<VillagePlaceType> BEE_HOME = create("bee_home");

    private PoiTypeTags() {}

    private static TagKey<VillagePlaceType> create(String s) {
        return TagKey.create(IRegistry.POINT_OF_INTEREST_TYPE_REGISTRY, new MinecraftKey(s));
    }
}
