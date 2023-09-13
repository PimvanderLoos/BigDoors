package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;

public class BannerPatternTags {

    public static final TagKey<EnumBannerPatternType> NO_ITEM_REQUIRED = create("no_item_required");
    public static final TagKey<EnumBannerPatternType> PATTERN_ITEM_FLOWER = create("pattern_item/flower");
    public static final TagKey<EnumBannerPatternType> PATTERN_ITEM_CREEPER = create("pattern_item/creeper");
    public static final TagKey<EnumBannerPatternType> PATTERN_ITEM_SKULL = create("pattern_item/skull");
    public static final TagKey<EnumBannerPatternType> PATTERN_ITEM_MOJANG = create("pattern_item/mojang");
    public static final TagKey<EnumBannerPatternType> PATTERN_ITEM_GLOBE = create("pattern_item/globe");
    public static final TagKey<EnumBannerPatternType> PATTERN_ITEM_PIGLIN = create("pattern_item/piglin");

    private BannerPatternTags() {}

    private static TagKey<EnumBannerPatternType> create(String s) {
        return TagKey.create(Registries.BANNER_PATTERN, new MinecraftKey(s));
    }
}
