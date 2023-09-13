package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record WorldDimension(Holder<DimensionManager> type, ChunkGenerator generator) {

    public static final Codec<WorldDimension> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(DimensionManager.CODEC.fieldOf("type").forGetter(WorldDimension::type), ChunkGenerator.CODEC.fieldOf("generator").forGetter(WorldDimension::generator)).apply(instance, instance.stable(WorldDimension::new));
    });
    public static final ResourceKey<WorldDimension> OVERWORLD = ResourceKey.create(Registries.LEVEL_STEM, new MinecraftKey("overworld"));
    public static final ResourceKey<WorldDimension> NETHER = ResourceKey.create(Registries.LEVEL_STEM, new MinecraftKey("the_nether"));
    public static final ResourceKey<WorldDimension> END = ResourceKey.create(Registries.LEVEL_STEM, new MinecraftKey("the_end"));
}
