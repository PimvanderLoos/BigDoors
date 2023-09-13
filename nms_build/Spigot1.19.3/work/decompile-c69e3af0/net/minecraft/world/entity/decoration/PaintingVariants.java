package net.minecraft.world.entity.decoration;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public class PaintingVariants {

    public static final ResourceKey<PaintingVariant> KEBAB = create("kebab");
    public static final ResourceKey<PaintingVariant> AZTEC = create("aztec");
    public static final ResourceKey<PaintingVariant> ALBAN = create("alban");
    public static final ResourceKey<PaintingVariant> AZTEC2 = create("aztec2");
    public static final ResourceKey<PaintingVariant> BOMB = create("bomb");
    public static final ResourceKey<PaintingVariant> PLANT = create("plant");
    public static final ResourceKey<PaintingVariant> WASTELAND = create("wasteland");
    public static final ResourceKey<PaintingVariant> POOL = create("pool");
    public static final ResourceKey<PaintingVariant> COURBET = create("courbet");
    public static final ResourceKey<PaintingVariant> SEA = create("sea");
    public static final ResourceKey<PaintingVariant> SUNSET = create("sunset");
    public static final ResourceKey<PaintingVariant> CREEBET = create("creebet");
    public static final ResourceKey<PaintingVariant> WANDERER = create("wanderer");
    public static final ResourceKey<PaintingVariant> GRAHAM = create("graham");
    public static final ResourceKey<PaintingVariant> MATCH = create("match");
    public static final ResourceKey<PaintingVariant> BUST = create("bust");
    public static final ResourceKey<PaintingVariant> STAGE = create("stage");
    public static final ResourceKey<PaintingVariant> VOID = create("void");
    public static final ResourceKey<PaintingVariant> SKULL_AND_ROSES = create("skull_and_roses");
    public static final ResourceKey<PaintingVariant> WITHER = create("wither");
    public static final ResourceKey<PaintingVariant> FIGHTERS = create("fighters");
    public static final ResourceKey<PaintingVariant> POINTER = create("pointer");
    public static final ResourceKey<PaintingVariant> PIGSCENE = create("pigscene");
    public static final ResourceKey<PaintingVariant> BURNING_SKULL = create("burning_skull");
    public static final ResourceKey<PaintingVariant> SKELETON = create("skeleton");
    public static final ResourceKey<PaintingVariant> DONKEY_KONG = create("donkey_kong");
    public static final ResourceKey<PaintingVariant> EARTH = create("earth");
    public static final ResourceKey<PaintingVariant> WIND = create("wind");
    public static final ResourceKey<PaintingVariant> WATER = create("water");
    public static final ResourceKey<PaintingVariant> FIRE = create("fire");

    public PaintingVariants() {}

    public static PaintingVariant bootstrap(IRegistry<PaintingVariant> iregistry) {
        IRegistry.register(iregistry, PaintingVariants.KEBAB, new PaintingVariant(16, 16));
        IRegistry.register(iregistry, PaintingVariants.AZTEC, new PaintingVariant(16, 16));
        IRegistry.register(iregistry, PaintingVariants.ALBAN, new PaintingVariant(16, 16));
        IRegistry.register(iregistry, PaintingVariants.AZTEC2, new PaintingVariant(16, 16));
        IRegistry.register(iregistry, PaintingVariants.BOMB, new PaintingVariant(16, 16));
        IRegistry.register(iregistry, PaintingVariants.PLANT, new PaintingVariant(16, 16));
        IRegistry.register(iregistry, PaintingVariants.WASTELAND, new PaintingVariant(16, 16));
        IRegistry.register(iregistry, PaintingVariants.POOL, new PaintingVariant(32, 16));
        IRegistry.register(iregistry, PaintingVariants.COURBET, new PaintingVariant(32, 16));
        IRegistry.register(iregistry, PaintingVariants.SEA, new PaintingVariant(32, 16));
        IRegistry.register(iregistry, PaintingVariants.SUNSET, new PaintingVariant(32, 16));
        IRegistry.register(iregistry, PaintingVariants.CREEBET, new PaintingVariant(32, 16));
        IRegistry.register(iregistry, PaintingVariants.WANDERER, new PaintingVariant(16, 32));
        IRegistry.register(iregistry, PaintingVariants.GRAHAM, new PaintingVariant(16, 32));
        IRegistry.register(iregistry, PaintingVariants.MATCH, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.BUST, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.STAGE, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.VOID, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.SKULL_AND_ROSES, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.WITHER, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.FIGHTERS, new PaintingVariant(64, 32));
        IRegistry.register(iregistry, PaintingVariants.POINTER, new PaintingVariant(64, 64));
        IRegistry.register(iregistry, PaintingVariants.PIGSCENE, new PaintingVariant(64, 64));
        IRegistry.register(iregistry, PaintingVariants.BURNING_SKULL, new PaintingVariant(64, 64));
        IRegistry.register(iregistry, PaintingVariants.SKELETON, new PaintingVariant(64, 48));
        IRegistry.register(iregistry, PaintingVariants.EARTH, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.WIND, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.WATER, new PaintingVariant(32, 32));
        IRegistry.register(iregistry, PaintingVariants.FIRE, new PaintingVariant(32, 32));
        return (PaintingVariant) IRegistry.register(iregistry, PaintingVariants.DONKEY_KONG, new PaintingVariant(64, 48));
    }

    private static ResourceKey<PaintingVariant> create(String s) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, new MinecraftKey(s));
    }
}
