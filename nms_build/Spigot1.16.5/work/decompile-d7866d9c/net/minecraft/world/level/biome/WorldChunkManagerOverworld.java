package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.newbiome.layer.GenLayer;
import net.minecraft.world.level.newbiome.layer.GenLayers;

public class WorldChunkManagerOverworld extends WorldChunkManager {

    public static final Codec<WorldChunkManagerOverworld> e = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter((worldchunkmanageroverworld) -> {
            return worldchunkmanageroverworld.h;
        }), Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", false, Lifecycle.stable()).forGetter((worldchunkmanageroverworld) -> {
            return worldchunkmanageroverworld.i;
        }), Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter((worldchunkmanageroverworld) -> {
            return worldchunkmanageroverworld.j;
        }), RegistryLookupCodec.a(IRegistry.ay).forGetter((worldchunkmanageroverworld) -> {
            return worldchunkmanageroverworld.k;
        })).apply(instance, instance.stable(WorldChunkManagerOverworld::new));
    });
    private final GenLayer f;
    private static final List<ResourceKey<BiomeBase>> g = ImmutableList.of(Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, new ResourceKey[]{Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU});
    private final long h;
    private final boolean i;
    private final boolean j;
    private final IRegistry<BiomeBase> k;

    public WorldChunkManagerOverworld(long i, boolean flag, boolean flag1, IRegistry<BiomeBase> iregistry) {
        super(WorldChunkManagerOverworld.g.stream().map((resourcekey) -> {
            return () -> {
                return (BiomeBase) iregistry.d(resourcekey);
            };
        }));
        this.h = i;
        this.i = flag;
        this.j = flag1;
        this.k = iregistry;
        this.f = GenLayers.a(i, flag, flag1 ? 6 : 4, 4);
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerOverworld.e;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        return this.f.a(this.k, i, k);
    }
}
