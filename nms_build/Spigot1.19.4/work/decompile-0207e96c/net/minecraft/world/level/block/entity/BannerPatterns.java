package net.minecraft.world.level.block.entity;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public class BannerPatterns {

    public static final ResourceKey<EnumBannerPatternType> BASE = create("base");
    public static final ResourceKey<EnumBannerPatternType> SQUARE_BOTTOM_LEFT = create("square_bottom_left");
    public static final ResourceKey<EnumBannerPatternType> SQUARE_BOTTOM_RIGHT = create("square_bottom_right");
    public static final ResourceKey<EnumBannerPatternType> SQUARE_TOP_LEFT = create("square_top_left");
    public static final ResourceKey<EnumBannerPatternType> SQUARE_TOP_RIGHT = create("square_top_right");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_BOTTOM = create("stripe_bottom");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_TOP = create("stripe_top");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_LEFT = create("stripe_left");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_RIGHT = create("stripe_right");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_CENTER = create("stripe_center");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_MIDDLE = create("stripe_middle");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_DOWNRIGHT = create("stripe_downright");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_DOWNLEFT = create("stripe_downleft");
    public static final ResourceKey<EnumBannerPatternType> STRIPE_SMALL = create("small_stripes");
    public static final ResourceKey<EnumBannerPatternType> CROSS = create("cross");
    public static final ResourceKey<EnumBannerPatternType> STRAIGHT_CROSS = create("straight_cross");
    public static final ResourceKey<EnumBannerPatternType> TRIANGLE_BOTTOM = create("triangle_bottom");
    public static final ResourceKey<EnumBannerPatternType> TRIANGLE_TOP = create("triangle_top");
    public static final ResourceKey<EnumBannerPatternType> TRIANGLES_BOTTOM = create("triangles_bottom");
    public static final ResourceKey<EnumBannerPatternType> TRIANGLES_TOP = create("triangles_top");
    public static final ResourceKey<EnumBannerPatternType> DIAGONAL_LEFT = create("diagonal_left");
    public static final ResourceKey<EnumBannerPatternType> DIAGONAL_RIGHT = create("diagonal_up_right");
    public static final ResourceKey<EnumBannerPatternType> DIAGONAL_LEFT_MIRROR = create("diagonal_up_left");
    public static final ResourceKey<EnumBannerPatternType> DIAGONAL_RIGHT_MIRROR = create("diagonal_right");
    public static final ResourceKey<EnumBannerPatternType> CIRCLE_MIDDLE = create("circle");
    public static final ResourceKey<EnumBannerPatternType> RHOMBUS_MIDDLE = create("rhombus");
    public static final ResourceKey<EnumBannerPatternType> HALF_VERTICAL = create("half_vertical");
    public static final ResourceKey<EnumBannerPatternType> HALF_HORIZONTAL = create("half_horizontal");
    public static final ResourceKey<EnumBannerPatternType> HALF_VERTICAL_MIRROR = create("half_vertical_right");
    public static final ResourceKey<EnumBannerPatternType> HALF_HORIZONTAL_MIRROR = create("half_horizontal_bottom");
    public static final ResourceKey<EnumBannerPatternType> BORDER = create("border");
    public static final ResourceKey<EnumBannerPatternType> CURLY_BORDER = create("curly_border");
    public static final ResourceKey<EnumBannerPatternType> GRADIENT = create("gradient");
    public static final ResourceKey<EnumBannerPatternType> GRADIENT_UP = create("gradient_up");
    public static final ResourceKey<EnumBannerPatternType> BRICKS = create("bricks");
    public static final ResourceKey<EnumBannerPatternType> GLOBE = create("globe");
    public static final ResourceKey<EnumBannerPatternType> CREEPER = create("creeper");
    public static final ResourceKey<EnumBannerPatternType> SKULL = create("skull");
    public static final ResourceKey<EnumBannerPatternType> FLOWER = create("flower");
    public static final ResourceKey<EnumBannerPatternType> MOJANG = create("mojang");
    public static final ResourceKey<EnumBannerPatternType> PIGLIN = create("piglin");

    public BannerPatterns() {}

    private static ResourceKey<EnumBannerPatternType> create(String s) {
        return ResourceKey.create(Registries.BANNER_PATTERN, new MinecraftKey(s));
    }

    public static EnumBannerPatternType bootstrap(IRegistry<EnumBannerPatternType> iregistry) {
        IRegistry.register(iregistry, BannerPatterns.BASE, new EnumBannerPatternType("b"));
        IRegistry.register(iregistry, BannerPatterns.SQUARE_BOTTOM_LEFT, new EnumBannerPatternType("bl"));
        IRegistry.register(iregistry, BannerPatterns.SQUARE_BOTTOM_RIGHT, new EnumBannerPatternType("br"));
        IRegistry.register(iregistry, BannerPatterns.SQUARE_TOP_LEFT, new EnumBannerPatternType("tl"));
        IRegistry.register(iregistry, BannerPatterns.SQUARE_TOP_RIGHT, new EnumBannerPatternType("tr"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_BOTTOM, new EnumBannerPatternType("bs"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_TOP, new EnumBannerPatternType("ts"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_LEFT, new EnumBannerPatternType("ls"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_RIGHT, new EnumBannerPatternType("rs"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_CENTER, new EnumBannerPatternType("cs"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_MIDDLE, new EnumBannerPatternType("ms"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_DOWNRIGHT, new EnumBannerPatternType("drs"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_DOWNLEFT, new EnumBannerPatternType("dls"));
        IRegistry.register(iregistry, BannerPatterns.STRIPE_SMALL, new EnumBannerPatternType("ss"));
        IRegistry.register(iregistry, BannerPatterns.CROSS, new EnumBannerPatternType("cr"));
        IRegistry.register(iregistry, BannerPatterns.STRAIGHT_CROSS, new EnumBannerPatternType("sc"));
        IRegistry.register(iregistry, BannerPatterns.TRIANGLE_BOTTOM, new EnumBannerPatternType("bt"));
        IRegistry.register(iregistry, BannerPatterns.TRIANGLE_TOP, new EnumBannerPatternType("tt"));
        IRegistry.register(iregistry, BannerPatterns.TRIANGLES_BOTTOM, new EnumBannerPatternType("bts"));
        IRegistry.register(iregistry, BannerPatterns.TRIANGLES_TOP, new EnumBannerPatternType("tts"));
        IRegistry.register(iregistry, BannerPatterns.DIAGONAL_LEFT, new EnumBannerPatternType("ld"));
        IRegistry.register(iregistry, BannerPatterns.DIAGONAL_RIGHT, new EnumBannerPatternType("rd"));
        IRegistry.register(iregistry, BannerPatterns.DIAGONAL_LEFT_MIRROR, new EnumBannerPatternType("lud"));
        IRegistry.register(iregistry, BannerPatterns.DIAGONAL_RIGHT_MIRROR, new EnumBannerPatternType("rud"));
        IRegistry.register(iregistry, BannerPatterns.CIRCLE_MIDDLE, new EnumBannerPatternType("mc"));
        IRegistry.register(iregistry, BannerPatterns.RHOMBUS_MIDDLE, new EnumBannerPatternType("mr"));
        IRegistry.register(iregistry, BannerPatterns.HALF_VERTICAL, new EnumBannerPatternType("vh"));
        IRegistry.register(iregistry, BannerPatterns.HALF_HORIZONTAL, new EnumBannerPatternType("hh"));
        IRegistry.register(iregistry, BannerPatterns.HALF_VERTICAL_MIRROR, new EnumBannerPatternType("vhr"));
        IRegistry.register(iregistry, BannerPatterns.HALF_HORIZONTAL_MIRROR, new EnumBannerPatternType("hhb"));
        IRegistry.register(iregistry, BannerPatterns.BORDER, new EnumBannerPatternType("bo"));
        IRegistry.register(iregistry, BannerPatterns.CURLY_BORDER, new EnumBannerPatternType("cbo"));
        IRegistry.register(iregistry, BannerPatterns.GRADIENT, new EnumBannerPatternType("gra"));
        IRegistry.register(iregistry, BannerPatterns.GRADIENT_UP, new EnumBannerPatternType("gru"));
        IRegistry.register(iregistry, BannerPatterns.BRICKS, new EnumBannerPatternType("bri"));
        IRegistry.register(iregistry, BannerPatterns.GLOBE, new EnumBannerPatternType("glb"));
        IRegistry.register(iregistry, BannerPatterns.CREEPER, new EnumBannerPatternType("cre"));
        IRegistry.register(iregistry, BannerPatterns.SKULL, new EnumBannerPatternType("sku"));
        IRegistry.register(iregistry, BannerPatterns.FLOWER, new EnumBannerPatternType("flo"));
        IRegistry.register(iregistry, BannerPatterns.MOJANG, new EnumBannerPatternType("moj"));
        return (EnumBannerPatternType) IRegistry.register(iregistry, BannerPatterns.PIGLIN, new EnumBannerPatternType("pig"));
    }
}
