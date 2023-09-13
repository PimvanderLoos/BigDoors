package net.minecraft.tags;

import java.util.List;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityTypes;

public final class TagsEntity {

    protected static final TagUtil<EntityTypes<?>> a = TagStatic.a(new MinecraftKey("entity_type"), ITagRegistry::getEntityTags);
    public static final Tag.e<EntityTypes<?>> SKELETONS = a("skeletons");
    public static final Tag.e<EntityTypes<?>> RADIERS = a("raiders");
    public static final Tag.e<EntityTypes<?>> BEEHIVE_INHABITORS = a("beehive_inhabitors");
    public static final Tag.e<EntityTypes<?>> ARROWS = a("arrows");
    public static final Tag.e<EntityTypes<?>> IMPACT_PROJECTILES = a("impact_projectiles");

    private static Tag.e<EntityTypes<?>> a(String s) {
        return TagsEntity.a.a(s);
    }

    public static Tags<EntityTypes<?>> a() {
        return TagsEntity.a.b();
    }

    public static List<? extends Tag.e<EntityTypes<?>>> b() {
        return TagsEntity.a.c();
    }
}
