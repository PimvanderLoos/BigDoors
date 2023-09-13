package net.minecraft.server;

import java.util.Set;

public class TagsEntity {

    private static final TagUtil<EntityTypes<?>> f = new TagUtil<>();
    public static final Tag.e<EntityTypes<?>> SKELETONS = a("skeletons");
    public static final Tag.e<EntityTypes<?>> RADIERS = a("raiders");
    public static final Tag.e<EntityTypes<?>> BEEHIVE_INHABITORS = a("beehive_inhabitors");
    public static final Tag.e<EntityTypes<?>> ARROWS = a("arrows");
    public static final Tag.e<EntityTypes<?>> IMPACT_PROJECTILES = a("impact_projectiles");

    private static Tag.e<EntityTypes<?>> a(String s) {
        return TagsEntity.f.a(s);
    }

    public static void a(Tags<EntityTypes<?>> tags) {
        TagsEntity.f.a(tags);
    }

    public static Tags<EntityTypes<?>> b() {
        return TagsEntity.f.b();
    }

    public static Set<MinecraftKey> b(Tags<EntityTypes<?>> tags) {
        return TagsEntity.f.b(tags);
    }
}
