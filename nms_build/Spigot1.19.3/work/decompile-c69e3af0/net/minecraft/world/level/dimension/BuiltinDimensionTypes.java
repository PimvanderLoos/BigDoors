package net.minecraft.world.level.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public class BuiltinDimensionTypes {

    public static final ResourceKey<DimensionManager> OVERWORLD = register("overworld");
    public static final ResourceKey<DimensionManager> NETHER = register("the_nether");
    public static final ResourceKey<DimensionManager> END = register("the_end");
    public static final ResourceKey<DimensionManager> OVERWORLD_CAVES = register("overworld_caves");
    public static final MinecraftKey OVERWORLD_EFFECTS = new MinecraftKey("overworld");
    public static final MinecraftKey NETHER_EFFECTS = new MinecraftKey("the_nether");
    public static final MinecraftKey END_EFFECTS = new MinecraftKey("the_end");

    public BuiltinDimensionTypes() {}

    private static ResourceKey<DimensionManager> register(String s) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, new MinecraftKey(s));
    }
}
