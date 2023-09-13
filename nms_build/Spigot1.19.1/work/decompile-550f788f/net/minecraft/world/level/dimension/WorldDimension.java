package net.minecraft.world.level.dimension;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;

public final class WorldDimension {

    public static final Codec<WorldDimension> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(DimensionManager.CODEC.fieldOf("type").forGetter(WorldDimension::typeHolder), ChunkGenerator.CODEC.fieldOf("generator").forGetter(WorldDimension::generator)).apply(instance, instance.stable(WorldDimension::new));
    });
    public static final ResourceKey<WorldDimension> OVERWORLD = ResourceKey.create(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<WorldDimension> NETHER = ResourceKey.create(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("the_nether"));
    public static final ResourceKey<WorldDimension> END = ResourceKey.create(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("the_end"));
    private static final Set<ResourceKey<WorldDimension>> BUILTIN_ORDER = ImmutableSet.of(WorldDimension.OVERWORLD, WorldDimension.NETHER, WorldDimension.END);
    private final Holder<DimensionManager> type;
    private final ChunkGenerator generator;

    public WorldDimension(Holder<DimensionManager> holder, ChunkGenerator chunkgenerator) {
        this.type = holder;
        this.generator = chunkgenerator;
    }

    public Holder<DimensionManager> typeHolder() {
        return this.type;
    }

    public ChunkGenerator generator() {
        return this.generator;
    }

    public static Stream<ResourceKey<WorldDimension>> keysInOrder(Stream<ResourceKey<WorldDimension>> stream) {
        return Stream.concat(WorldDimension.BUILTIN_ORDER.stream(), stream.filter((resourcekey) -> {
            return !WorldDimension.BUILTIN_ORDER.contains(resourcekey);
        }));
    }

    public static IRegistry<WorldDimension> sortMap(IRegistry<WorldDimension> iregistry) {
        IRegistryWritable<WorldDimension> iregistrywritable = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function) null);

        keysInOrder(iregistry.registryKeySet().stream()).forEach((resourcekey) -> {
            WorldDimension worlddimension = (WorldDimension) iregistry.get(resourcekey);

            if (worlddimension != null) {
                iregistrywritable.register(resourcekey, (Object) worlddimension, iregistry.lifecycle(worlddimension));
            }

        });
        return iregistrywritable;
    }

    public static boolean stable(IRegistry<WorldDimension> iregistry) {
        if (iregistry.size() != WorldDimension.BUILTIN_ORDER.size()) {
            return false;
        } else {
            Optional<WorldDimension> optional = iregistry.getOptional(WorldDimension.OVERWORLD);
            Optional<WorldDimension> optional1 = iregistry.getOptional(WorldDimension.NETHER);
            Optional<WorldDimension> optional2 = iregistry.getOptional(WorldDimension.END);

            if (!optional.isEmpty() && !optional1.isEmpty() && !optional2.isEmpty()) {
                if (!((WorldDimension) optional.get()).typeHolder().is(BuiltinDimensionTypes.OVERWORLD) && !((WorldDimension) optional.get()).typeHolder().is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
                    return false;
                } else if (!((WorldDimension) optional1.get()).typeHolder().is(BuiltinDimensionTypes.NETHER)) {
                    return false;
                } else if (!((WorldDimension) optional2.get()).typeHolder().is(BuiltinDimensionTypes.END)) {
                    return false;
                } else if (((WorldDimension) optional1.get()).generator() instanceof ChunkGeneratorAbstract && ((WorldDimension) optional2.get()).generator() instanceof ChunkGeneratorAbstract) {
                    ChunkGeneratorAbstract chunkgeneratorabstract = (ChunkGeneratorAbstract) ((WorldDimension) optional1.get()).generator();
                    ChunkGeneratorAbstract chunkgeneratorabstract1 = (ChunkGeneratorAbstract) ((WorldDimension) optional2.get()).generator();

                    if (!chunkgeneratorabstract.stable(GeneratorSettingBase.NETHER)) {
                        return false;
                    } else if (!chunkgeneratorabstract1.stable(GeneratorSettingBase.END)) {
                        return false;
                    } else if (!(chunkgeneratorabstract.getBiomeSource() instanceof WorldChunkManagerMultiNoise)) {
                        return false;
                    } else {
                        WorldChunkManagerMultiNoise worldchunkmanagermultinoise = (WorldChunkManagerMultiNoise) chunkgeneratorabstract.getBiomeSource();

                        if (!worldchunkmanagermultinoise.stable(WorldChunkManagerMultiNoise.a.NETHER)) {
                            return false;
                        } else {
                            WorldChunkManager worldchunkmanager = ((WorldDimension) optional.get()).generator().getBiomeSource();

                            return worldchunkmanager instanceof WorldChunkManagerMultiNoise && !((WorldChunkManagerMultiNoise) worldchunkmanager).stable(WorldChunkManagerMultiNoise.a.OVERWORLD) ? false : chunkgeneratorabstract1.getBiomeSource() instanceof WorldChunkManagerTheEnd;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
