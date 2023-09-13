package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.WorldChunkManagerHell;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class ChunkProviderFlat extends ChunkGenerator {

    public static final Codec<ChunkProviderFlat> CODEC = RecordCodecBuilder.create((instance) -> {
        return commonCodec(instance).and(GeneratorSettingsFlat.CODEC.fieldOf("settings").forGetter(ChunkProviderFlat::settings)).apply(instance, instance.stable(ChunkProviderFlat::new));
    });
    private final GeneratorSettingsFlat settings;

    public ChunkProviderFlat(IRegistry<StructureSet> iregistry, GeneratorSettingsFlat generatorsettingsflat) {
        super(iregistry, generatorsettingsflat.structureOverrides(), new WorldChunkManagerHell(generatorsettingsflat.getBiomeFromSettings()), new WorldChunkManagerHell(generatorsettingsflat.getBiome()), 0L);
        this.settings = generatorsettingsflat;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkProviderFlat.CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long i) {
        return this;
    }

    public GeneratorSettingsFlat settings() {
        return this.settings;
    }

    @Override
    public void buildSurface(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager, IChunkAccess ichunkaccess) {}

    @Override
    public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
        return levelheightaccessor.getMinBuildHeight() + Math.min(levelheightaccessor.getHeight(), this.settings.getLayers().size());
    }

    @Override
    protected Holder<BiomeBase> adjustBiome(Holder<BiomeBase> holder) {
        return this.settings.getBiome();
    }

    @Override
    public CompletableFuture<IChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        List<IBlockData> list = this.settings.getLayers();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        HeightMap heightmap = ichunkaccess.getOrCreateHeightmapUnprimed(HeightMap.Type.OCEAN_FLOOR_WG);
        HeightMap heightmap1 = ichunkaccess.getOrCreateHeightmapUnprimed(HeightMap.Type.WORLD_SURFACE_WG);

        for (int i = 0; i < Math.min(ichunkaccess.getHeight(), list.size()); ++i) {
            IBlockData iblockdata = (IBlockData) list.get(i);

            if (iblockdata != null) {
                int j = ichunkaccess.getMinBuildHeight() + i;

                for (int k = 0; k < 16; ++k) {
                    for (int l = 0; l < 16; ++l) {
                        ichunkaccess.setBlockState(blockposition_mutableblockposition.set(k, j, l), iblockdata, false);
                        heightmap.update(k, j, l, iblockdata);
                        heightmap1.update(k, j, l, iblockdata);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(ichunkaccess);
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        List<IBlockData> list = this.settings.getLayers();

        for (int k = Math.min(list.size(), levelheightaccessor.getMaxBuildHeight()) - 1; k >= 0; --k) {
            IBlockData iblockdata = (IBlockData) list.get(k);

            if (iblockdata != null && heightmap_type.isOpaque().test(iblockdata)) {
                return levelheightaccessor.getMinBuildHeight() + k + 1;
            }
        }

        return levelheightaccessor.getMinBuildHeight();
    }

    @Override
    public BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor) {
        return new BlockColumn(levelheightaccessor.getMinBuildHeight(), (IBlockData[]) this.settings.getLayers().stream().limit((long) levelheightaccessor.getHeight()).map((iblockdata) -> {
            return iblockdata == null ? Blocks.AIR.defaultBlockState() : iblockdata;
        }).toArray((k) -> {
            return new IBlockData[k];
        }));
    }

    @Override
    public void addDebugScreenInfo(List<String> list, BlockPosition blockposition) {}

    @Override
    public Climate.Sampler climateSampler() {
        return Climate.empty();
    }

    @Override
    public void applyCarvers(RegionLimitedWorldAccess regionlimitedworldaccess, long i, BiomeManager biomemanager, StructureManager structuremanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {}

    @Override
    public void spawnOriginalMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {}

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }
}
