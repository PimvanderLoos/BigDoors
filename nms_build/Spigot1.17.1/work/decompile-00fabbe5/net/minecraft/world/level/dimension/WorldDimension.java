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
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;

public final class WorldDimension {

    public static final Codec<WorldDimension> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(DimensionManager.CODEC.fieldOf("type").flatXmap(ExtraCodecs.c(), ExtraCodecs.c()).forGetter(WorldDimension::a), ChunkGenerator.CODEC.fieldOf("generator").forGetter(WorldDimension::c)).apply(instance, instance.stable(WorldDimension::new));
    });
    public static final ResourceKey<WorldDimension> OVERWORLD = ResourceKey.a(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<WorldDimension> NETHER = ResourceKey.a(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("the_nether"));
    public static final ResourceKey<WorldDimension> END = ResourceKey.a(IRegistry.LEVEL_STEM_REGISTRY, new MinecraftKey("the_end"));
    private static final Set<ResourceKey<WorldDimension>> BUILTIN_ORDER = Sets.newLinkedHashSet(ImmutableList.of(WorldDimension.OVERWORLD, WorldDimension.NETHER, WorldDimension.END));
    private final Supplier<DimensionManager> type;
    private final ChunkGenerator generator;

    public WorldDimension(Supplier<DimensionManager> supplier, ChunkGenerator chunkgenerator) {
        this.type = supplier;
        this.generator = chunkgenerator;
    }

    public Supplier<DimensionManager> a() {
        return this.type;
    }

    public DimensionManager b() {
        return (DimensionManager) this.type.get();
    }

    public ChunkGenerator c() {
        return this.generator;
    }

    public static RegistryMaterials<WorldDimension> a(RegistryMaterials<WorldDimension> registrymaterials) {
        RegistryMaterials<WorldDimension> registrymaterials1 = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        Iterator iterator = WorldDimension.BUILTIN_ORDER.iterator();

        while (iterator.hasNext()) {
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) iterator.next();
            WorldDimension worlddimension = (WorldDimension) registrymaterials.a(resourcekey);

            if (worlddimension != null) {
                registrymaterials1.a(resourcekey, (Object) worlddimension, registrymaterials.d((Object) worlddimension));
            }
        }

        iterator = registrymaterials.d().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey1 = (ResourceKey) entry.getKey();

            if (!WorldDimension.BUILTIN_ORDER.contains(resourcekey1)) {
                registrymaterials1.a(resourcekey1, (Object) ((WorldDimension) entry.getValue()), registrymaterials.d((Object) ((WorldDimension) entry.getValue())));
            }
        }

        return registrymaterials1;
    }

    public static boolean a(long i, RegistryMaterials<WorldDimension> registrymaterials) {
        List<Entry<ResourceKey<WorldDimension>, WorldDimension>> list = Lists.newArrayList(registrymaterials.d());

        if (list.size() != WorldDimension.BUILTIN_ORDER.size()) {
            return false;
        } else {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) list.get(0);
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry1 = (Entry) list.get(1);
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry2 = (Entry) list.get(2);

            if (entry.getKey() == WorldDimension.OVERWORLD && entry1.getKey() == WorldDimension.NETHER && entry2.getKey() == WorldDimension.END) {
                if (!((WorldDimension) entry.getValue()).b().a(DimensionManager.DEFAULT_OVERWORLD) && ((WorldDimension) entry.getValue()).b() != DimensionManager.DEFAULT_OVERWORLD_CAVES) {
                    return false;
                } else if (!((WorldDimension) entry1.getValue()).b().a(DimensionManager.DEFAULT_NETHER)) {
                    return false;
                } else if (!((WorldDimension) entry2.getValue()).b().a(DimensionManager.DEFAULT_END)) {
                    return false;
                } else if (((WorldDimension) entry1.getValue()).c() instanceof ChunkGeneratorAbstract && ((WorldDimension) entry2.getValue()).c() instanceof ChunkGeneratorAbstract) {
                    ChunkGeneratorAbstract chunkgeneratorabstract = (ChunkGeneratorAbstract) ((WorldDimension) entry1.getValue()).c();
                    ChunkGeneratorAbstract chunkgeneratorabstract1 = (ChunkGeneratorAbstract) ((WorldDimension) entry2.getValue()).c();

                    if (!chunkgeneratorabstract.a(i, GeneratorSettingBase.NETHER)) {
                        return false;
                    } else if (!chunkgeneratorabstract1.a(i, GeneratorSettingBase.END)) {
                        return false;
                    } else if (!(chunkgeneratorabstract.getWorldChunkManager() instanceof WorldChunkManagerMultiNoise)) {
                        return false;
                    } else {
                        WorldChunkManagerMultiNoise worldchunkmanagermultinoise = (WorldChunkManagerMultiNoise) chunkgeneratorabstract.getWorldChunkManager();

                        if (!worldchunkmanagermultinoise.b(i)) {
                            return false;
                        } else if (!(chunkgeneratorabstract1.getWorldChunkManager() instanceof WorldChunkManagerTheEnd)) {
                            return false;
                        } else {
                            WorldChunkManagerTheEnd worldchunkmanagertheend = (WorldChunkManagerTheEnd) chunkgeneratorabstract1.getWorldChunkManager();

                            return worldchunkmanagertheend.b(i);
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
