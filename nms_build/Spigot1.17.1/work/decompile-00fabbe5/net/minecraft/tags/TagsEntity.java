package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.EntityTypes;

public final class TagsEntity {

    protected static final TagUtil<EntityTypes<?>> HELPER = TagStatic.a(IRegistry.ENTITY_TYPE_REGISTRY, "tags/entity_types");
    public static final Tag.e<EntityTypes<?>> SKELETONS = a("skeletons");
    public static final Tag.e<EntityTypes<?>> RAIDERS = a("raiders");
    public static final Tag.e<EntityTypes<?>> BEEHIVE_INHABITORS = a("beehive_inhabitors");
    public static final Tag.e<EntityTypes<?>> ARROWS = a("arrows");
    public static final Tag.e<EntityTypes<?>> IMPACT_PROJECTILES = a("impact_projectiles");
    public static final Tag.e<EntityTypes<?>> POWDER_SNOW_WALKABLE_MOBS = a("powder_snow_walkable_mobs");
    public static final Tag.e<EntityTypes<?>> AXOLOTL_ALWAYS_HOSTILES = a("axolotl_always_hostiles");
    public static final Tag.e<EntityTypes<?>> AXOLOTL_HUNT_TARGETS = a("axolotl_hunt_targets");
    public static final Tag.e<EntityTypes<?>> FREEZE_IMMUNE_ENTITY_TYPES = a("freeze_immune_entity_types");
    public static final Tag.e<EntityTypes<?>> FREEZE_HURTS_EXTRA_TYPES = a("freeze_hurts_extra_types");

    private TagsEntity() {}

    private static Tag.e<EntityTypes<?>> a(String s) {
        return TagsEntity.HELPER.a(s);
    }

    public static Tags<EntityTypes<?>> a() {
        return TagsEntity.HELPER.b();
    }
}
