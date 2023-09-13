package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingVariantTags {

    public static final TagKey<PaintingVariant> PLACEABLE = create("placeable");

    private PaintingVariantTags() {}

    private static TagKey<PaintingVariant> create(String s) {
        return TagKey.create(IRegistry.PAINTING_VARIANT_REGISTRY, new MinecraftKey(s));
    }
}
