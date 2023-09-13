package net.minecraft.world.entity.animal;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;

public record FrogVariant(MinecraftKey texture) {

    public static final FrogVariant TEMPERATE = register("temperate", "textures/entity/frog/temperate_frog.png");
    public static final FrogVariant WARM = register("warm", "textures/entity/frog/warm_frog.png");
    public static final FrogVariant COLD = register("cold", "textures/entity/frog/cold_frog.png");

    private static FrogVariant register(String s, String s1) {
        return (FrogVariant) IRegistry.register(BuiltInRegistries.FROG_VARIANT, s, new FrogVariant(new MinecraftKey(s1)));
    }
}
