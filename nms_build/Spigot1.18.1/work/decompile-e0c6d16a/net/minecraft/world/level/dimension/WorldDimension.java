package net.minecraft.world.level.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;

public final class WorldDimension {

    public static final Codec<WorldDimension> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(DimensionManager.CODEC.fieldOf("type").flatXmap(ExtraCodecs.nonNullSupplierCheck(), ExtraCodecs.nonNullSupplierCheck()).forGetter(WorldDimension::typeSupplier), ChunkGenerator.CODEC.fieldOf("generator").forGetter(WorldDimension::generator)).apply(instance, instance.stable(WorldDimension::new));
    });
    public static final ResourceKey<WorldDimension> OVERWORLD = ResourceKey.create(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<WorldDimension> NETHER = ResourceKey.create(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("the_nether"));
    public static final ResourceKey<WorldDimension> END = ResourceKey.create(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("the_end"));
    private static final Set<ResourceKey<WorldDimension>> BUILTIN_ORDER = Sets.newLinkedHashSet(ImmutableList.of(WorldDimension.OVERWORLD, WorldDimension.NETHER, WorldDimension.END));
    private final Supplier<DimensionManager> type;
    private final ChunkGenerator generator;

    public WorldDimension(Supplier<DimensionManager> supplier, ChunkGenerator chunkgenerator) {
        this.type = supplier;
        this.generator = chunkgenerator;
    }

    public Supplier<DimensionManager> typeSupplier() {
        return this.type;
    }

    public DimensionManager type() {
        return (DimensionManager) this.type.get();
    }

    public ChunkGenerator generator() {
        return this.generator;
    }

    public static RegistryMaterials<WorldDimension> sortMap(RegistryMaterials<WorldDimension> registrymaterials) {
        RegistryMaterials<WorldDimension> registrymaterials1 = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        Iterator iterator = WorldDimension.BUILTIN_ORDER.iterator();

        while (iterator.hasNext()) {
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) iterator.next();
            WorldDimension worlddimension = (WorldDimension) registrymaterials.get(resourcekey);

            if (worlddimension != null) {
                registrymaterials1.register(resourcekey, (Object) worlddimension, registrymaterials.lifecycle(worlddimension));
            }
        }

        iterator = registrymaterials.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey1 = (ResourceKey) entry.getKey();

            if (!WorldDimension.BUILTIN_ORDER.contains(resourcekey1)) {
                registrymaterials1.register(resourcekey1, (Object) ((WorldDimension) entry.getValue()), registrymaterials.lifecycle((WorldDimension) entry.getValue()));
            }
        }

        return registrymaterials1;
    }

    public static boolean stable(long i, RegistryMaterials<WorldDimension> registrymaterials) {
        List<Entry<ResourceKey<WorldDimension>, WorldDimension>> list = Lists.newArrayList(registrymaterials.entrySet());

        if (list.size() != WorldDimension.BUILTIN_ORDER.size()) {
            return false;
        } else {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) list.get(0);
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry1 = (Entry) list.get(1);
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry2 = (Entry) list.get(2);

            if (entry.getKey() == WorldDimension.OVERWORLD && entry1.getKey() == WorldDimension.NETHER && entry2.getKey() == WorldDimension.END) {
                if (!((WorldDimension) entry.getValue()).type().equalTo(DimensionManager.DEFAULT_OVERWORLD) && ((WorldDimension) entry.getValue()).type() != DimensionManager.DEFAULT_OVERWORLD_CAVES) {
                    return false;
                } else if (!((WorldDimension) entry1.getValue()).type().equalTo(DimensionManager.DEFAULT_NETHER)) {
                    return false;
                } else if (!((WorldDimension) entry2.getValue()).type().equalTo(DimensionManager.DEFAULT_END)) {
                    return false;
                } else if (((WorldDimension) entry1.getValue()).generator() instanceof ChunkGeneratorAbstract && ((WorldDimension) entry2.getValue()).generator() instanceof ChunkGeneratorAbstract) {
                    ChunkGeneratorAbstract chunkgeneratorabstract = (ChunkGeneratorAbstract) ((WorldDimension) entry1.getValue()).generator();
                    ChunkGeneratorAbstract chunkgeneratorabstract1 = (ChunkGeneratorAbstract) ((WorldDimension) entry2.getValue()).generator();

                    if (!chunkgeneratorabstract.stable(i, GeneratorSettingBase.NETHER)) {
                        return false;
                    } else if (!chunkgeneratorabstract1.stable(i, GeneratorSettingBase.END)) {
                        return false;
                    } else if (!(chunkgeneratorabstract.getBiomeSource() instanceof WorldChunkManagerMultiNoise)) {
                        return false;
                    } else {
                        WorldChunkManagerMultiNoise worldchunkmanagermultinoise = (WorldChunkManagerMultiNoise) chunkgeneratorabstract.getBiomeSource();

                        if (!worldchunkmanagermultinoise.stable(WorldChunkManagerMultiNoise.a.NETHER)) {
                            return false;
                        } else {
                            WorldChunkManager worldchunkmanager = ((WorldDimension) entry.getValue()).generator().getBiomeSource();

                            if (worldchunkmanager instanceof WorldChunkManagerMultiNoise && !((WorldChunkManagerMultiNoise) worldchunkmanager).stable(WorldChunkManagerMultiNoise.a.OVERWORLD)) {
                                return false;
                            } else if (!(chunkgeneratorabstract1.getBiomeSource() instanceof WorldChunkManagerTheEnd)) {
                                return false;
                            } else {
                                WorldChunkManagerTheEnd worldchunkmanagertheend = (WorldChunkManagerTheEnd) chunkgeneratorabstract1.getBiomeSource();

                                return worldchunkmanagertheend.stable(i);
                            }
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
